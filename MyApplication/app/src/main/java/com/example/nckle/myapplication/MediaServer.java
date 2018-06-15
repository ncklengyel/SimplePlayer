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
        mHttpSever = new AsyncHttpServer();

        mHttpSever.post("/play", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
                play();
            }
        });

        mHttpSever.get("^(/play|/stop|/pause|/previous|/next)", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/stop", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
               stop();
               reset();
               response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/song", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send( mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/pause", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                pause();
                response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/next", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                next();
                response.send( mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/previous", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                previous();
                response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.post("/shuffle", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                shuffle();
                response.send(mPlayList.getCurrentSong().getJSON(isPlaying()));
            }
        });

        mHttpSever.get("/shuffle", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                shuffle();
                response.send(buildResponse("repeatOne", mPlayList.getIsShuffling()));
            }
        });

        mHttpSever.post("/repeatOne", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                repeatOne();
                response.send(buildResponse("repeatOne", mPlayList.getIsRepeatingOne()));
            }
        });

        mHttpSever.post("/repeatOne", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                repeatOne();
                response.send(buildResponse("repeatOne", mPlayList.getIsRepeatingOne()));
            }
        });

        mHttpSever.post("/loop", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                repeatAll();
                response.send(buildResponse("loop", mPlayList.getIsRepeatingAll()));
            }
        });

        mHttpSever.get("/loop", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                repeatAll();
                response.send(buildResponse("loop", mPlayList.getIsRepeatingAll()));
            }
        });

        mHttpSever.post("/seek", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                // TODO this is missing
//                seekTo();
//                response.send(buildResponse("seek", mPlayList.getIsRepeatingAll()));
            }
        });

        mHttpSever.get("/seek", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                repeatAll();
                response.send(buildResponse("seek", mPlayList.getIsRepeatingAll()));
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
        if (!mPlayList.getIsRepeatingOne()) {
            mPlayList.next();
        }
        mMediaPlayer.stop();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        if (!isStreaming) {
            mMediaPlayer.start();
        }
    }

    public void previous(){
        if (!mPlayList.getIsRepeatingOne()) {
            mPlayList.previous();
        }
        mMediaPlayer.stop();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
        if(!isStreaming) {
            mMediaPlayer.start();
        }
    }

    public void shuffle(){
        mPlayList.shuffle();
    }

    public void stop(){
        mMediaPlayer.stop();
        mMediaPlayer = MediaPlayer.create(mContext, mPlayList.getCurrentSong().getPath());
    }

    public void repeatOne(){
        mPlayList.setRepeatAll(false);
        mPlayList.setRepeatOne(!mPlayList.getIsRepeatingOne());
    }

    public void repeatAll(){
        mPlayList.setRepeatOne(false);
        mPlayList.setRepeatAll(!mPlayList.getIsRepeatingAll());
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

    private JSONObject buildResponse(String command, Object value){
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

}
