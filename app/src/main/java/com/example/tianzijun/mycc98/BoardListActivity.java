package com.example.tianzijun.mycc98;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class BoardListActivity extends AppCompatActivity {
    private static String TAG = "BoardListActivity";
    LinearLayout linearLayout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);
        new Thread(getBoardList).start();
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

    //开启另一个线程请求
    Runnable getBoardList = new Runnable() {
        @Override
        public void run() {
            //获取版面信息
            try {
                URL url = new URL("http://api-v2.cc98.org/board/all");
                JSONArray jsonArray = AJAXFetch.fetch(url);
                for(int i =0;i<jsonArray.length();i++){
                   // URL url1 = new URL("http://api-v2.cc98.org/board/"+jsonArray.getJSONObject(i).getString("id")+"/sub");
                    final JSONArray boards = jsonArray.getJSONObject(i).getJSONArray("boards");

                    showBoardBlock(jsonArray.getJSONObject(i),boards);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void showBoardBlock(final JSONObject block,final JSONArray boards){
        linearLayout= (LinearLayout)findViewById(R.id.boardList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    String name =block.getString("name");
                    if(!name.equals("天下一家")&&!name.equals("社团风采")&&!name.equals("教师答疑")&&!name.equals("院系交流")&&!name.equals("CC98协会")){
                        TextView textView = new TextView(getBaseContext());
                        textView.setText(name);
                        textView.setTextSize(24);
                        textView.setTextColor(getResources().getColor(R.color.board_block));
                        textView.setGravity(Gravity.CENTER);
                        linearLayout.addView(textView);
                        showBoardList(boards);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private  void showBoardList(final JSONArray boards ){
        linearLayout= (LinearLayout)findViewById(R.id.boardList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout boardsLayout = new LinearLayout(getBaseContext());
                    boardsLayout.setOrientation(LinearLayout.HORIZONTAL);
                    boardsLayout.setGravity(Gravity.CENTER);
                    //生成版面
                    for( int j = 0;j<boards.length();j++){
                        if(j%4==0){
                            boardsLayout = new LinearLayout(getBaseContext());
                            boardsLayout.setOrientation(LinearLayout.HORIZONTAL);
                            boardsLayout.setGravity(Gravity.CENTER);
                        }
                        TextView boardView = new TextView(getBaseContext());
                        boardView.setText(boards.getJSONObject(j).getString("name"));

                        boardView.setTextSize(20);
                        boardView.setTextColor(getResources().getColor(R.color.black));
                        int left, top, right, bottom;
                        left = 8;right=8;top=0;bottom=0;
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(left, top, right, bottom);
                        boardView.setLayoutParams(params);
                        final int boardId = boards.getJSONObject(j).getInt("id");
                        final int topicCount = boards.getJSONObject(j).getInt("topicCount");
                        final int totalPage = topicCount%20==0?topicCount/20:topicCount/20+1;
                        final String boardName = boards.getJSONObject(j).getString("name");
                        boardsLayout.addView(boardView);
                        //绑定跳转
                        boardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BoardListActivity.this,BoardActivity.class);

                                intent.putExtra("boardId",boardId);
                                intent.putExtra("totalPage",totalPage);
                                intent.putExtra("page",1);
                                intent.putExtra("boardName",boardName);
                                startActivity(intent);


                            }
                        });
                        if(j%4==0)linearLayout.addView(boardsLayout);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
