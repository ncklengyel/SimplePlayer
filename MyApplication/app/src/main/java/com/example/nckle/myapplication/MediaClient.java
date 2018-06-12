package com.example.nckle.myapplication;

import android.graphics.Bitmap;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.parser.JSONObjectParser;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.nckle.myapplication.Utils.convertJSONtoBitmap;

public class MediaClient implements AbstractMediaComponent {

    private String mHost;
    private int mPort;
    private Song mSong;

    public MediaClient(String pHost){
        String[] hostParams = pHost.split(":");
        String host = hostParams[0];
        int port = Integer.parseInt(hostParams[1].trim());

        mHost = host;
        mPort = port;
        doPost("song");
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

    public String getTitle() {
        if (mSong != null) {
            return mSong.getTitle();
        }
        return "NO SONG";
    }

    public String getAuthor() {
        if (mSong != null) {
            return mSong.getArtist();
        }
        return "NO SONG";
    }

    public String getAlbum() {
        if (mSong != null) {
            return mSong.getAlbum();
        }
        return "NO SONG";
    }

    public Bitmap getAlbumImage() {
        if (mSong != null) {
            return mSong.getAlbumImage();
        }
        return null;
    }

    public Song getSong() {
        return mSong;
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

    private void doPost(final String command){
        String url = getBaseUrl() + "/" + command;
        AsyncHttpPost post = new AsyncHttpPost(url);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                if (result == null) {
                    Log.d("MediaClient response:", "result is null");

                }else{
                    Log.d("MediaClient response:", result);
                    try {
                        JSONObject response = new JSONObject(result);
                        JSONObject song = new JSONObject((String)response.get(command));
                        mSong = new Song(
                                song.get("title").toString(),
                                song.get("artist").toString(),
                                song.get("album").toString(),
                                song.get("length").toString(),
                                convertJSONtoBitmap(song.get("albumImage").toString())
                        );
                    } catch (JSONException jsonE) {
                        // TODO do something
                    }
//                    mSong = result;
                }
            }
        });
    }

}
