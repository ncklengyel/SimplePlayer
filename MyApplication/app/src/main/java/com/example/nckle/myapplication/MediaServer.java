package com.example.nckle.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

public class MediaServer extends AbstractMediaComponent {

    private MediaPlayer mediaPlayer;

    public MediaServer(Context pContext, int pSong){

        mediaPlayer = MediaPlayer.create( pContext, pSong);

    }

    public void play(){
        mediaPlayer.start();
    }

    public void next(){

    }

    public void back(){

    }

    public void shuffle(){

    }

    public void stop(){
        mediaPlayer.stop();
    }

    public void repeatOne(){

    }

    public void repeatAll(){

    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

}
