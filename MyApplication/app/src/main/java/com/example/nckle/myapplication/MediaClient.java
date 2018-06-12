package com.example.nckle.myapplication;

import android.graphics.Bitmap;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

public class MediaClient implements AbstractMediaComponent {

    private String mHost;
    private int mPort;

    public MediaClient(String pHost){
        String[] hostParams = pHost.split(":");
        String host = hostParams[0];
        int port = Integer.parseInt(hostParams[1].trim());

        mHost = host;
        mPort = port;
    }
    public void play(){
        doPost("play");
    }

    public void next(){
        doPost("next");
    }

    public void previous(){
        doPost("previous");
    }

    public void shuffle(){
        doPost("shuffle");
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

    // TODO implement client side getTitle
    public String getTitle() {
        return "NEEDS TO BE DONE";
    }

    // TODO implement client side getAuthor
    public String getAuthor() {
        return "NEEDS TO BE DONE";
    }

    // TODO implement client side getAlbum
    public String getAlbum() {
        return "NEEDS TO BE DONE";
    }

    // TODO implement client side getAlbumImage
    public Bitmap getAlbumImage() {
        return null;
    }

    // TODO implement client side getSong
    public Song getSong() {
        return null;
    }

    public boolean isPlaying(){
        return false;
    }

    private String getBaseUrl(){
        return "http://" + mHost + ":" + mPort;
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
