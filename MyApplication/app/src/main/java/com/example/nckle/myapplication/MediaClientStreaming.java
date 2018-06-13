package com.example.nckle.myapplication;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class MediaClientStreaming implements AbstractMediaComponent {

    private MediaPlayer mp;
    private Context mContext;

    public MediaClientStreaming(Context aContext){
        mContext = aContext;
        mp = new MediaPlayer();

        //mp.start();
        try {
            mp.setDataSource("192.168.2.17:8080");
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            mp.start();
            //mp.setDataSource("192.168.2.17:8080");
            //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mp.prepare();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void play(){};

    public void next(){};

    public void previous(){};

    public void shuffle(){};

    public void stop(){};

    public void repeatOne(){};

    public void repeatAll(){};

    public int getCurrentPosition(){return -1;};

    public int getDuration(){return -1;};

    public void seekTo(int position){};

    public void pause(){};

    public boolean isPlaying(){return false;};

    public void release(){};

    public void setVolume(int level){};

    public String getTitle(){return "";};

    public String getAuthor(){return "";};

    public String getAlbum(){return "";};

    //public Bitmap getAlbumImage(){return new Bitmap(null);};

    //public Song getSong(){return new Song()};
}
