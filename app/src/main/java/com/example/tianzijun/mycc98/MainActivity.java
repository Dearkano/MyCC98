package com.example.tianzijun.mycc98;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private LinearLayout hot_topics_ll;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Button logoutBtn = findViewById(R.id.button_toolbar_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("userName","defaultusername");
                editor.putString("password","defaultpassword");
                editor.commit();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        new Thread(getHotTopic).start();
    }
/**
 * 获取每层楼的ubb代码
 * 用字符串拼接成
 * <html>
 *     <script>
 *         ubb->html
 *     </script>
 *          显示这段html
 * </html>
 *
 * webview.loaddata(html)
 * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.boardList:
                Intent intent = new Intent(MainActivity.this,BoardListActivity.class);
                startActivity(intent);
                break;
            case R.id.logOut:
                SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("userName","defaultusername");
                editor.putString("password","defaultpassword");
                editor.commit();
                Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent1);
                break;
        }
        return true;
    }
    //开启另一个线程请求
    Runnable getHotTopic = new Runnable() {
        @Override
        public void run() {
            //获取版面信息
            try {
                JSONArray jsonArray=AJAXFetch.getHotTopics();
                showHotTopic(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void showHotTopic(final JSONArray jsonArray ){
        hot_topics_ll=findViewById(R.id.hot_topics_ll);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i =0;i<jsonArray.length();i++){
                        String title = jsonArray.getJSONObject(i).getString("title");
                        final int topicId = jsonArray.getJSONObject(i).getInt("id");
                        final int boardId = jsonArray.getJSONObject(i).getInt("boardId");
                        final String userName = jsonArray.getJSONObject(i).getString("authorName")!=null?jsonArray.getJSONObject(i).getString("authorName"):"匿名";
                        final String boardName = jsonArray.getJSONObject(i).getString("boardName");
                        final LinearLayout ll= (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.board_topic,null);

                        final TextView titleView=(TextView) ll.findViewById(R.id.board_topic_title);
                        titleView.setTextSize(20);
                        final TextView boardNameView = ll.findViewById(R.id.board_topic_boardName);
                        boardNameView.setText(boardName);
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
                                Intent intent = new Intent(MainActivity.this,TopicActivity.class);
                                intent.putExtra("topicId",topicId);
                                intent.putExtra("page",1);
                                intent.putExtra("totalPage",totalPage);
                                intent.putExtra("boardId",boardId);
                                startActivity(intent);
                            }
                        });
                        hot_topics_ll.addView(ll);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
