package com.example.tianzijun.mycc98;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

/**
 * Created by tianzijun on 2018/1/2.
 */

public class AJAXFetch {
    public static String userName;
    public static String password;
    public static String token;
    public static JSONArray fetch(URL url)throws Exception{
        HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            Log.d(TAG, "fetch: "+AJAXFetch.token);
            connection.setRequestProperty("Authorization",AJAXFetch.token);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONArray jsonArray=new JSONArray(result);
            connection.disconnect();
            return jsonArray;
    }

    public static String getJsonString(URL url)throws Exception{
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",AJAXFetch.token);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String line;
        String result = "";
        while ((line = in.readLine()) != null) {
            result += line;
        }
        connection.disconnect();
        return result;
    }

    public static JSONObject getTopicInfo(int topicId)throws Exception{
        String topicUrl = "http://api-v2.cc98.org/topic/"+topicId;
        URL topic_Url = new URL(topicUrl);
        String topicInfo = getJsonString(topic_Url);
        JSONObject jsonObject = new JSONObject(topicInfo);
        return  jsonObject;
    }

    public static JSONObject getBoardInfo(int boardId)throws Exception{
        String boardUrl = "http://api-v2.cc98.org/board/"+boardId;
        URL board_Url = new URL(boardUrl);
        String boardInfo = getJsonString(board_Url);
        JSONObject jsonObject = new JSONObject(boardInfo);
        return  jsonObject;
    }

    public static JSONArray getHotTopics()throws Exception{
        URL url = new URL("http://api-v2.cc98.org/topic/hot");
        JSONArray jsonArray=fetch(url);
        return jsonArray;
    }

    public static JSONArray getPosts(int topicId,int page)throws Exception{
        String str = "http://api-v2.cc98.org/topic/" + topicId + "/post?from="+10*(page-1)+"&size=10";
        URL url = new URL(str);
        JSONArray jsonArray = fetch(url);
        return jsonArray;
    }

    public static JSONArray getUsersInfo(int[] usersId)throws Exception{
        String usersUrl = "http://api-v2.cc98.org/user";
        for(int i=0;i<usersId.length;i++){
            if(i==0){
                usersUrl=usersUrl+"?id="+usersId[i];
            }else{
                usersUrl=usersUrl+"&id="+usersId[i];
            }
        }
        URL users_url=  new URL(usersUrl);
        JSONArray usersInfo = fetch(users_url);
        Log.d(TAG, "getUsersInfo: "+usersUrl);
        Log.d(TAG, "getUsersInfo: "+usersInfo);
        return usersInfo;
    }

    public static JSONArray getAnonymousUsersInfo(String[] usersName)throws Exception{
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<usersName.length;i++){
            JSONObject userInfo = new JSONObject();
            userInfo.put("name","匿名"+usersName[i].toUpperCase());
            userInfo.put("portraitUrl","http://www.cc98.org/static/images/心灵头像.gif");
            jsonArray.put(userInfo);
        }
        return jsonArray;
    }

    public static JSONArray getBoardTopics(int boardId,int page)throws Exception{
        int start = 10*(page-1);
        URL url = new URL("http://api-v2.cc98.org/board/"+boardId+"/topic?from="+start+"&size=20");
        Log.d(TAG, "getBoardTopics: "+url);
        JSONArray jsonArray = fetch(url);
        return jsonArray;
    }

    public static String fetch(URL url,String body)throws Exception{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Authorization",AJAXFetch.token);
        connection.setRequestMethod("POST");
        Log.d(TAG, "fetch: "+body);
        byte[] bypes = body.getBytes();
        connection.getOutputStream().write(bypes);// 输入参数
        InputStream inStream=connection.getInputStream();
        String str = new String(StreamTool.readInputStream(inStream), "gbk");
        connection.disconnect();
        return str;
    }
    public static void LogIn(String userName,String password)throws Exception{
        String str = "https://openid.cc98.org/connect/token";
        URL url = new URL(str);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setRequestMethod("POST");
        String jsonStr ="client_id=9a1fd200-8687-44b1-4c20-08d50a96e5cd&client_secret=8b53f727-08e2-4509-8857-e34bf92b27f2&username="+URLEncoder.encode(userName)+"&password="+password+"&grant_type=password&scope="+URLEncoder.encode("cc98-api openid","UTF-8") ;
        byte[] bypes = jsonStr.getBytes();
        connection.getOutputStream().write(bypes);// 输入参数
        InputStream inStream=connection.getInputStream();
        String tokenStr = new String(StreamTool.readInputStream(inStream), "gbk");
        JSONObject tokenObj = new JSONObject(tokenStr);
        String token = "Bearer "+tokenObj.getString("access_token");
        AJAXFetch.userName=userName;
        AJAXFetch.password=password;
        AJAXFetch.token=token;

        Log.d(TAG, "LogIn: "+token);
        int res = connection.getResponseCode();
        connection.disconnect();
    }
    public static int SendTopic(String title,String content,int boardId)throws Exception{
        JSONObject body = new JSONObject();
        body.put("title",title);
        body.put("content",content);
        body.put("contentType",0);
        body.put("type",0);
        URL url = new URL("http://api-v2.cc98.org/board/"+boardId+"/topic");
        String topicId = fetch(url,body.toString());
        return parseInt(topicId);
    }
    public static JSONArray getNewTopics(int page)throws Exception{
        int start = 10*(page-1);
        URL url= new URL("http://api-v2.cc98.org/topic/new?from="+start+"&size=10");
        JSONArray jsonArray = fetch(url);
        return jsonArray;
    }
}
