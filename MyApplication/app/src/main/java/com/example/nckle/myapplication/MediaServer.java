package com.example.nckle.myapplication;

import android.content.Context;
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
import java.util.ArrayList;

import java.io.File;
import java.util.ListIterator;

public class MediaServer extends AbstractMediaComponent {

    private MediaPlayer mMediaPlayer;
    private AsyncHttpServer mHttpSever;
    private Context mContext;
    private ArrayList<Uri> mSongs;
    private ListIterator<Uri> itr;
    private boolean mLoop;

    public MediaServer(Context pContext){

        mLoop = true; //for the moment
        mSongs = getMusicOnDevice();
        itr = mSongs.listIterator();
        itr.next();
        mContext = pContext;
        mMediaPlayer = MediaPlayer.create( pContext, mSongs.get(0));
        mHttpSever = new AsyncHttpServer();

        mHttpSever.post("/play", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //AsyncHttpRequestBody<String> body = request.getBody();
                response.send(buildResponse("play","ok"));
                play();
            }
        });

        mHttpSever.post("/stop", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
               stop();
               reset();
               response.send(buildResponse("stop","ok"));
            }
        });

        mHttpSever.post("/pause", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                pause();
                response.send(buildResponse("pause","ok"));
            }
        });

        mHttpSever.post("/next", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                next();
                response.send(buildResponse("next","ok"));
            }
        });

        mHttpSever.post("/back", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                back();
                response.send(buildResponse("back","ok"));
            }
        });

        Log.i("Starting Server:","Listening on 80");
        mHttpSever.listen(8080);

    }

    public void play(){
        mMediaPlayer.start();
    }

    public void next(){

        mMediaPlayer.stop();

        if(!itr.hasNext() && mLoop) {
            itr = mSongs.listIterator();
            reset();
            itr.next();
        } else {
            Uri nextSong = itr.next();
            mMediaPlayer = MediaPlayer.create(mContext, nextSong);
        }

        mMediaPlayer.start();
    }

    //TODO: Fix bug that back plays the same song 2 times in a row
    public void back(){
        if(itr.hasPrevious()) {
            mMediaPlayer.stop();
            mMediaPlayer = MediaPlayer.create(mContext, itr.previous());
        }else {
            reset();
            itr.next();
        }

        mMediaPlayer.start();

    }

    public void shuffle(){

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
        return mMediaPlayer.getDuration();
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
        mMediaPlayer = MediaPlayer.create(mContext,mSongs.get(0));
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

    private ArrayList<Uri> getMusicOnDevice(){

        ArrayList<Uri> songs = new ArrayList<Uri>();
        File musicFile = new File(Environment.getExternalStorageDirectory().toString() + "/Music");
        File[] files = musicFile.listFiles();
        for (File file : files) {


            if (file.getName().endsWith(".mp3")){
                songs.add(Uri.parse(file.getAbsolutePath()));
            }

        }

        return songs;

    }

}
