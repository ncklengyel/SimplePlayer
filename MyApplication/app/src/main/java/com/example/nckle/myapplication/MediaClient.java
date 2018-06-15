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
    private boolean isShuffling = false;
    private boolean isRepeatOne = false;
    private boolean isRepeatAll = false;
    private boolean isPlaying = false;

    public void setIsStreaming(boolean pIsStreaming) {
        isStreaming = pIsStreaming;
        toggleModes(false);
    }

    public MediaClient(Context pContext, String pHost){
        String[] hostParams = pHost.split(":");
        String host = hostParams[0];
        int port = Integer.parseInt(hostParams[1].trim());

        isPlaying = false;
        mContext = pContext;
        mHost = host;
        mPort = port;
        doPost("song");
    }
    public void play(){
        if (!isStreaming) {
            isPlaying = true;
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
        isShuffling = !isShuffling;
        doPost("shuffle");
    }

    public void stop(){
        if (!isStreaming) {
            isPlaying = false;
            doPost("stop");
        } else {
             toggleModes(true);
        }
    }

    public void repeatOne(){
        isRepeatAll = false;
        isRepeatOne = !isRepeatOne;
        doPost("repeatOne");
    }

    public void repeatAll(){
        isRepeatOne = false;
        isRepeatAll = !isRepeatAll;
        doPost("loop");
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
        } else {
            doPost("seekTo");
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
        if (isStreaming) {
            return mMediaPlayer.isPlaying();
        } else {
            return isPlaying;
        }
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
                        if (response.get("title") != null) {
                            if(response.get("url") == null) {
                                mSong = new Song(
                                        response.get("title").toString(),
                                        response.get("artist").toString(),
                                        response.get("album").toString(),
                                        response.get("length").toString(),
                                        convertJSONtoBitmap(response.get("albumImage").toString())
                                );
                            } else {
                                mSong = new Song(
                                        response.get("title").toString(),
                                        response.get("artist").toString(),
                                        response.get("album").toString(),
                                        response.get("length").toString(),
                                        convertJSONtoBitmap(response.get("albumImage").toString()),
                                        response.get("url").toString()
                                );
                            }
                            if (isStreaming) {
                                toggleModes(false);
                            }
                        }
                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }
                }
            }
        });
    }

    private void toggleModes(boolean isStopping){
        mMediaPlayer.reset();
        if(isStreaming) {
            try {
                mMediaPlayer.setDataSource(mContext, mSong.getPath());
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                if(!isStopping) {
                    mMediaPlayer.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
