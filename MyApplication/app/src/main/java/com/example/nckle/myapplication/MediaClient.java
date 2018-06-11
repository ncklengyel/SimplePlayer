package com.example.nckle.myapplication;

import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

public class MediaClient implements AbstractMediaComponent {

    private String host;
    private int port;
    private String url;

    public MediaClient(String pHost, int pPort){

        host = pHost;
        port = pPort;

    }
    public void play(){
        doPost("play");
    }

    public void next(){
        doPost("next");
    }

    public void back(){
        doPost("back");
    }

    public void shuffle(){

    }

    public void stop(){
        doPost("stop");
    }

    public void repeatOne(){

    }

    public void repeatAll(){

    }
    public int getCurrentPosition(){
        return 0;
    }

    public int getDuration(){
        return 0;
    }

    public void seekTo(int position){

    }

    public void pause(){
        doPost("pause");
    }


    public boolean isPlaying(){
        return false;
    }

    private String getBaseUrl(){
        return "http://" + host + ":" + port;
    }

    public void release(){
        
    }

    public void setVolume(int level){

    }

    private void doPost(String command){
        String url = getBaseUrl() + "/" + command;
        AsyncHttpPost post = new AsyncHttpPost(url);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                if (result == null) {
                    Log.d("MediaClient response:", "result is null");

                }else{
                    Log.d("MediaClient response:", result);
                }
            }
        });
    }

}
