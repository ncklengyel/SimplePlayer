package com.example.nckle.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.ArrayList;
import java.io.File;


public class MediaServer implements AbstractMediaComponent {

    private MediaPlayer mMediaPlayer;
    private AsyncHttpServer mHttpSever;
    private Context mContext;
    private Playlist mPlayList;
    //TextView timeRight, timeLeft;

    public MediaServer(Context pContext){

        mPlayList = new Playlist(getMusicOnDevice());
        mContext = pContext;
        if (mPlayList.getCurrentSong() != null) {
            mMediaPlayer = MediaPlayer.create(pContext, mPlayList.getCurrentSong().getPath());
        } else {
            mMediaPlayer = new MediaPlayer();
        }
        mHttpSever = new AsyncHttpServer();
        /*timeLeft = aTimeLeft;
        timeRight = aTimeRight;*/

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

        // TODO should return a boolean
        mHttpSever.post("/shuffle", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                shuffle();
                response.send(buildResponse("shuffle", mPlayList.getCurrentSong().getJSON()));
            }
        });

        Log.i("Starting Server:","Listening on 8080");
        mHttpSever.listen(8080);

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

    //Crash the app
   /* private void updateRightLeftTime(){
        timeLeft.setText("0:00");
        timeRight.setText(Utils.millisecondToMMSS(mMediaPlayer.getDuration()));
    }*/

    private ArrayList<Song> getMusicOnDevice(){

        ArrayList<Song> songs = new ArrayList<Song>();
        File musicFile = new File(Environment.getExternalStorageDirectory().toString() + "/Music");
        File[] files = musicFile.listFiles();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        for (File file : files) {

            if (file.getName().endsWith(".mp3")){
                mmr.setDataSource(file.getAbsolutePath());
                Song newSong = new Song(
                    Uri.parse(file.getAbsolutePath()),
                    mmr
                );
                songs.add(newSong);
            }

        }

        return songs;

    }

}
