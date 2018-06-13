package com.example.nckle.myapplication;

import android.graphics.Bitmap;

interface AbstractMediaComponent {

    void play();

    void next();

    void previous();

    void shuffle();

    void stop();

    void repeatOne();

    void repeatAll();

    int getCurrentPosition();

    int getDuration();

    void seekTo(int position);

    void pause();

    boolean isPlaying();

    void release();

    void setVolume(int level);

    String getTitle();

    String getAuthor();

    String getAlbum();

    //Bitmap getAlbumImage();

    //Song getSong();

}
