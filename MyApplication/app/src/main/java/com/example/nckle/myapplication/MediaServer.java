package com.example.nckle.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class MediaServer implements AbstractMediaComponent {

    private MediaPlayer mMediaPlayer;
    private AsyncHttpServer mHttpSever;
    private Context mContext;
    private Playlist mPlayList;
    private boolean isStreaming = false;

    public void setIsStreaming(boolean pIsStreaming) {
        isStreaming = pIsStreaming;
    }

    public MediaServer(Context pContext){
        mPlayList = new Playlist(Utils.getMusicOnDevice());
        mContext = pContext;
        if (mPlayList.getCurrentSong() != null) {
            mMediaPlayer = MediaPlayer.create(pContext, mPlayList.getCurrentSong().getPath());
        } else {
            mMediaPlayer = new MediaPlayer();
        }

        //set the volume of the mediaplayer
        //if you want to change the volume of the player you need to compute the volume with
        // Utils.getComputedVolume and then pass it to mMediaPlayer.setVolume function
        float computedVolume = Utils.getComputedVolume(AbstractMediaComponent.DEFAULT_VOLUME);
        mMediaPlayer.setVolume(computedVolume,computedVolume);

        mHttpSever = new AsyncHttpServer();

        mHttpSever.post("/play", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
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

        mHttpSever.post("/setvolume", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                try {
                    JSONObject body = new JSONObject(request.getBody().get().toString());
                    setVolume(body.getInt("volume"));
                    response.send(buildResponse("setvolume","ok"));
                }catch (JSONException e) {
                    response.send(buildResponse("setvolume","Error"));
                    e.printStackTrace();
                }
            }
        });

        mHttpSever.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                File song = new File(mPlayList.getCurrentSong().getPath().toString());
                response.sendFile(song);
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
        if(!isStreaming) {
            mMediaPlayer.start();
        }
    }

    public void previous(){
        mMediaPlayer.stop();
        mPlayList.previous();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        if(!isStreaming) {
            mMediaPlayer.start();
        }

    }

    public void shuffle(){
        mMediaPlayer.stop();
        mPlayList.shuffle();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        if(!isStreaming) {
            mMediaPlayer.start();
        }
    }

    public void stop(){
        mMediaPlayer.stop();
    }

    public void toggleRepeatMode(){
        mPlayList.repeat();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
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

    public void setVolume(int volume){
        float computedVolume = Utils.getComputedVolume(volume);
        mMediaPlayer.setVolume(computedVolume,computedVolume);
    }

}
