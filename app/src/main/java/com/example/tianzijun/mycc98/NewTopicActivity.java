package com.example.tianzijun.mycc98;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class NewTopicActivity extends AppCompatActivity {
    public static int page,totalPage=5;
    public static final String TAG ="NewTopicActivity" ;
    public static LinearLayout topics_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        page = bundle.getInt("page");
        Button button_lastPage = (Button)findViewById(R.id.board_lastPage);
        button_lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPage=page-1;
                if(newPage==0){
                    Toast.makeText(NewTopicActivity.this, "当前已是第一页",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent_lastPage = new Intent(NewTopicActivity.this,NewTopicActivity.class);
                    intent_lastPage.putExtra("page",newPage);
                    startActivity(intent_lastPage);
                }

            }
        });
        Button button_nextPage = (Button)findViewById(R.id.board_nextPage);
        button_nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPage=page+1;
                if(newPage==totalPage+1){
                    Toast.makeText(NewTopicActivity.this, "当前已是最后一页",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent_nextPage = new Intent(NewTopicActivity.this,NewTopicActivity.class);
                    intent_nextPage.putExtra("page",newPage);
                    startActivity(intent_nextPage);
                }

            }
        });
        Button mainBtn = findViewById(R.id.button_toolbar_main);
        Button boardBtn = findViewById(R.id.button_toolbar_board);
        Button newBtn = findViewById(R.id.button_toolbar_new);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        boardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),BoardListActivity.class);

                startActivity(intent);
            }
        });
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewTopicActivity.class);
                intent.putExtra("page",1);
                startActivity(intent);
            }
        });
        new Thread(runnable).start();
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                JSONArray jsonArray = AJAXFetch.getNewTopics(page);
                showBoardTopics(jsonArray);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    public void showBoardTopics(final JSONArray jsonArray){
        topics_ll=findViewById(R.id.new_topics_ll);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i =0;i<jsonArray.length();i++){
                        String title = jsonArray.getJSONObject(i).getString("title");
                        final int topicId = jsonArray.getJSONObject(i).getInt("id");
                        final int boardId = jsonArray.getJSONObject(i).getInt("boardId");
                        final String userName = jsonArray.getJSONObject(i).getString("userName")!=null?jsonArray.getJSONObject(i).getString("userName"):"匿名";
                        final LinearLayout ll= (LinearLayout) LayoutInflater.from(NewTopicActivity.this).inflate(R.layout.board_topic,null);
                        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        final TextView titleView= ll.findViewById(R.id.board_topic_title);
                        titleView.setTextSize(20);
                        final TextView userNameView=ll.findViewById(R.id.board_topic_userName);
                        final TextView replyCountView = ll.findViewById(R.id.board_topic_replyCount);
                        int replyCount = jsonArray.getJSONObject(i).getInt("replyCount");
                        replyCount++;
                        final int totalPage = replyCount%10==0?replyCount/10:replyCount/10+1;
                        userNameView.setText(userName);
                        replyCountView.setText("回复:"+(replyCount-1));
                        titleView.setText(title);
                        //给title添加点击事件
                        titleView.setClickable(true);
                        titleView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(NewTopicActivity.this,TopicActivity.class);
                                intent.putExtra("topicId",topicId);
                                intent.putExtra("page",1);
                                intent.putExtra("totalPage",totalPage);
                                intent.putExtra("boardId",boardId);
                                startActivity(intent);
                            }
                        });
                        topics_ll.addView(ll);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
