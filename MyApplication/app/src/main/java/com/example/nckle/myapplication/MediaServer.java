package com.example.nckle.myapplication;

import android.content.Context;
import android.media.MediaPlayer;

import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaServer extends AbstractMediaComponent {

    private MediaPlayer mMediaPlayer;
    private AsyncHttpServer mHttpSever;
    private int mSong;
    private Context mContext;

    public MediaServer(Context pContext, final int pSong){

        mSong = pSong;
        mContext = pContext;
        mMediaPlayer = MediaPlayer.create( pContext, pSong);
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

        Log.i("Starting Server:","Listening on 80");
        mHttpSever.listen(8080);

    }

    public void play(){
        mMediaPlayer.start();
    }

    public void next(){

    }

    public void back(){

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
        mMediaPlayer = MediaPlayer.create(mContext,mSong);
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

}
