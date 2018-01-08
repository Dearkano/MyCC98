package com.example.tianzijun.mycc98;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by tianzijun on 2018/1/4.
 */
//生成一层楼的全部信息
public class PostInfo {
    public  static void generateWholeTopic(final String postInfo, final String usersInfo, final String topicInfo, final String boardInfo,WebView webView){
        webView.setVerticalScrollBarEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getJson() {
                return postInfo;
            }
        }, "postsInfo");
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getJson() {
                return usersInfo;
            }
        }, "usersInfo");
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getJson() {
                return topicInfo;
            }
        }, "topicInfo");
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getJson() {
                return boardInfo;
            }
        }, "boardInfo");
        webView.loadUrl("file:///android_asset/post.html");
    }
}
