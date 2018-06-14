package com.example.nckle.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.media.MediaPlayer.*;
import static com.example.nckle.myapplication.Utils.convertJSONtoBitmap;

public class MediaClient implements AbstractMediaComponent {

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private String mHost;
    private int mPort;
    private Context mContext;
    private Song mSong;
    private boolean isStreaming = false;

    public void setIsStreaming(boolean pIsStreaming) {
        isStreaming = pIsStreaming;
        toggleModes();
    }

    public MediaClient(Context pContext, String pHost){
        String[] hostParams = pHost.split(":");
        String host = hostParams[0];
        int port = Integer.parseInt(hostParams[1].trim());

        mContext = pContext;
        mHost = host;
        mPort = port;
        doPost("song");
    }
    public void play(){
        if (!isStreaming) {
            doPost("play");
        } else {
            mMediaPlayer.start();
        }
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
        if (!isStreaming) {
            doPost("stop");
        } else {
            mMediaPlayer.stop();
        }
    }

    public void repeatOne(){

    }

    public void repeatAll(){

    }

    public int getCurrentPosition(){
        if (isStreaming) {
            return mMediaPlayer.getCurrentPosition();
        }
        // TODO get duration from server I guess
        return 0;
    }

    public int getDuration(){
        if (isStreaming) {
            return mMediaPlayer.getDuration();
        } else if (mSong != null){
            return Integer.parseInt(mSong.getLength());
        }
        return 0;
    }

    public void seekTo(int position){
        if (isStreaming) {
            mMediaPlayer.seekTo(position);
        }
    }

    public void pause(){
        if (!isStreaming) {
            doPost("pause");
        } else {
            mMediaPlayer.pause();
        }
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
                        if (isStreaming) {
                            toggleModes();
                        }
                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }
                }
            }
        });
    }

    private void toggleModes(){
        mMediaPlayer.reset();
        if(isStreaming) {
            try {
                mMediaPlayer.setDataSource("http://" + mHost + ":8082");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
