package com.example.nckle.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.File;

import fi.iki.elonen.NanoHTTPD;


public class MediaServer extends NanoHTTPD implements AbstractMediaComponent {

    private MediaPlayer mMediaPlayer;
    private AsyncHttpServer mHttpSever;
    private Context mContext;
    private Playlist mPlayList;
    private boolean isStreaming = false;

    public void setIsStreaming(boolean pIsStreaming) {
        isStreaming = pIsStreaming;
        toggleModes();
    }

    public MediaServer(Context pContext){
        super(8082);
        mPlayList = new Playlist(Utils.getMusicOnDevice());
        mContext = pContext;
        if (mPlayList.getCurrentSong() != null) {
            mMediaPlayer = MediaPlayer.create(pContext, mPlayList.getCurrentSong().getPath());
        } else {
            mMediaPlayer = new MediaPlayer();
        }
        mHttpSever = new AsyncHttpServer();

        mHttpSever.post("/play", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //AsyncHttpRequestBody<String> body = request.getBody();
                response.send(buildResponse("play", mPlayList.getCurrentSong().getJSON()));
                play();
            }
        });

        mHttpSever.post("/stop", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
               stop();
               reset();
               response.send(buildResponse("stop", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/song", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send(buildResponse("song", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/pause", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                pause();
                response.send(buildResponse("pause", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/next", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                next();
                response.send(buildResponse("next", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/previous", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                previous();
                response.send(buildResponse("previous", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/shuffle", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                shuffle();
                response.send(buildResponse("shuffle", mPlayList.getCurrentSong().getJSON()));
            }
        });

        mHttpSever.post("/stream", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                shuffle();
                response.send(buildResponse("shuffle", mPlayList.getCurrentSong().getJSON()));
            }
        });

        Log.i("Starting Server:","Listening on 8080");
        mHttpSever.listen(8080);

    }

    @Override
    public Response serve(IHTTPSession session) {
        InputStream myInput = null;
        try {
            myInput = new FileInputStream(mPlayList.getCurrentSong().getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createResponse(Response.Status.OK, "audio/mpeg", myInput);
    }

    //Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType,
                                    InputStream message) {
        return newChunkedResponse(status, mimeType, message);
    }

    public void play(){
        mMediaPlayer.start();
    }

    public void next(){
        mMediaPlayer.stop();
        mPlayList.next();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        mMediaPlayer.start();
    }

    public void previous(){
        mMediaPlayer.stop();
        mPlayList.previous();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        mMediaPlayer.start();

    }

    public void shuffle(){
        mMediaPlayer.stop();
        mPlayList.shuffle();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        mMediaPlayer.start();
    }

    public void stop(){
        mMediaPlayer.stop();
    }

    public void repeatOne(){

    }

    public void repeatAll(){

    }

    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        if (!isStreaming)
            return mMediaPlayer.getDuration();

        return 0;
    }

    public void seekTo(int position){
        mMediaPlayer.seekTo(position);
    }

    public void pause(){
        if (isPlaying())
            mMediaPlayer.pause();
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    private void reset(){
        mMediaPlayer.stop();
        mPlayList.reset();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
    }

    public void release(){
        mMediaPlayer.release();
        mHttpSever.stop();
    }

    public String getTitle() {
        if (mPlayList.getCurrentSong() != null) {
            return mPlayList.getCurrentSong().getTitle();
        }
        return "NO SONG";
    }

    public String getAuthor() {
        if (mPlayList.getCurrentSong() != null) {
            return mPlayList.getCurrentSong().getArtist();
        }
        return "NO SONG";
    }

    public String getAlbum() {
        if (mPlayList.getCurrentSong() != null) {
            return mPlayList.getCurrentSong().getAlbum();
        }
        return "NO SONG";
    }

    public Song getSong() {
        return mPlayList.getCurrentSong();
    }

    public Bitmap getAlbumImage() {
        if (mPlayList.getCurrentSong() != null) {
            return mPlayList.getCurrentSong().getAlbumImage();
        }
        return null;
    }

    private JSONObject buildResponse(String command, String value){
        JSONObject json = new JSONObject();
        try {
            json.put(command, value);

        }catch (JSONException e){
            Log.e("MediaServer:",e.toString());
        }

        return json;
    }

    public void setVolume(int level){

    }

    private void toggleModes(){
        if(isStreaming) {
            try {
                super.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        } else {
            super.stop();
        }
    }

}
