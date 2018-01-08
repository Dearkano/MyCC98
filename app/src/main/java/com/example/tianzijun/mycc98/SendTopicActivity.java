package com.example.tianzijun.mycc98;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendTopicActivity extends AppCompatActivity {
    public static String title,content;
    public static int boardId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_topic);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boardId = bundle.getInt("boardId");
        Button button = findViewById(R.id.send_topic_send);
        final EditText titleView = findViewById(R.id.send_topic_title);
        final EditText contentView  = findViewById(R.id.send_topic_content);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_ = titleView.getText().toString();
                String content_ = contentView.getText().toString();
                title=title_;
                content=content_;
                new Thread(runnable).start();
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
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int topicId = AJAXFetch.SendTopic(title, content, boardId);
                Intent intent = new Intent(SendTopicActivity.this,TopicActivity.class);
                intent.putExtra("topicId",topicId);
                intent.putExtra("page",1);
                intent.putExtra("totalPage",1);
                intent.putExtra("boardId",boardId);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
