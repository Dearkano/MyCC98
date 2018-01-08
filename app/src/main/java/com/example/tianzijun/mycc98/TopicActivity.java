package com.example.tianzijun.mycc98;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class TopicActivity extends AppCompatActivity {
    private static final String TAG = "TopicActivity" ;
    int topicId,page,totalPage,boardId;
    String replyContent;
    private LinearLayout posts_ll;
    EditText editText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        topicId = bundle.getInt("topicId");
        page = bundle.getInt("page");
        totalPage=bundle.getInt("totalPage");
        boardId=bundle.getInt("boardId");
        new Thread(runnable).start();
        //给按钮绑定事件
        Button button_lastPage = (Button)findViewById(R.id.lastPage);
        button_lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPage=page-1;
                if(newPage==0){
                    Toast.makeText(TopicActivity.this, "当前已是第一页",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent_lastPage = new Intent(TopicActivity.this,TopicActivity.class);
                    intent_lastPage.putExtra("topicId",topicId);
                    intent_lastPage.putExtra("page",newPage);
                    intent_lastPage.putExtra("totalPage",totalPage);
                    intent_lastPage.putExtra("boardId",boardId);
                    startActivity(intent_lastPage);
                }

            }
        });
        Button button_nextPage = (Button)findViewById(R.id.nextPage);
        button_nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPage=page+1;
                if(newPage==totalPage+1){
                    Toast.makeText(TopicActivity.this, "当前已是最后一页",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent_nextPage = new Intent(TopicActivity.this,TopicActivity.class);
                    intent_nextPage.putExtra("topicId",topicId);
                    intent_nextPage.putExtra("page",newPage);
                    intent_nextPage.putExtra("totalPage",totalPage);
                    intent_nextPage.putExtra("boardId",boardId);
                    startActivity(intent_nextPage);
                }

            }
        });


        Button replyButton = findViewById(R.id.replyButton);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    editText= findViewById(R.id.replyEditor);
                    replyContent=editText.getText().toString();
                    Log.d(TAG, "onCreate: replycontent="+replyContent);
                   new Thread(sendPost).start();
                }catch (Exception e){
                    e.printStackTrace();
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
    }
    Runnable sendPost = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL("http://api-v2.cc98.org/topic/" + topicId + "/post");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", replyContent);
                jsonObject.put("contentType", 0);
                jsonObject.put("title", "");
                AJAXFetch.fetch(url, jsonObject.toString());
                JSONObject topicInfo = AJAXFetch.getTopicInfo(topicId);
                int replyCount = topicInfo.getInt("replyCount");
                totalPage = replyCount % 10 == 0 ? replyCount / 10 : replyCount / 10 + 1;
                Intent intent = new Intent(TopicActivity.this, TopicActivity.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("page", totalPage);
                intent.putExtra("totalPage", totalPage);
                intent.putExtra("boardId", boardId);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run(){
            try {
                JSONObject boardInfo = AJAXFetch.getBoardInfo(boardId);
                JSONObject topicInfo = AJAXFetch.getTopicInfo(topicId);
                JSONArray jsonArray = AJAXFetch.getPosts(topicId,page);
                Boolean isAnonymous = topicInfo.getBoolean("isAnonymous");
                Log.d(TAG, "run: is anonymous="+isAnonymous);
                if(isAnonymous){
                    String[] usersName= new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        usersName[i]=jsonArray.getJSONObject(i).getString("userName");
                    }
                    JSONArray usersInfo = AJAXFetch.getAnonymousUsersInfo(usersName);
                    showTopic(jsonArray.toString(),usersInfo.toString(),topicInfo.toString(),boardInfo.toString());
                }else{
                    int[] usersId= new int[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        usersId[i]=jsonArray.getJSONObject(i).getInt("userId");
                    }
                    JSONArray usersInfo = AJAXFetch.getUsersInfo(usersId);
                    showTopic(jsonArray.toString(),usersInfo.toString(),topicInfo.toString(),boardInfo.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    private void showTopic(final String postInfo,final String usersInfo,final String topicInfo,final String boardInfo ){
        posts_ll=findViewById(R.id.posts_ll);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout postLayout = new LinearLayout(getBaseContext());
                    WebView webView = new WebView(getBaseContext());
                    TextView textView = new TextView(getBaseContext());
                    //设置属性
                    postLayout.setOrientation(LinearLayout.VERTICAL);
                    textView.setTextColor(Color.WHITE);
                    textView.setPadding(10,10,0,0);
                    textView.setTextSize(20);
                    //组合控件
                    postLayout.addView(webView);
                    PostInfo.generateWholeTopic(postInfo,usersInfo,topicInfo,boardInfo,webView);
                    posts_ll.addView(postLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
