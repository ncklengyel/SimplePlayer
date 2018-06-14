package com.example.nckle.myapplication;

import android.graphics.Bitmap;

interface AbstractMediaComponent {

    int MAX_VOLUME = 10;
    int DEFAULT_VOLUME = 5;

    void play();

    void next();

    void previous();

    void shuffle();

    void stop();

    void toggleRepeatMode();

    int getCurrentPosition();

    int getDuration();

    void seekTo(int position);

    void pause();

    boolean isPlaying();

    void release();

    void setVolume(int volume);

    String getTitle();

    String getAuthor();

    String getAlbum();

    Bitmap getAlbumImage();

    Song getSong();

    void setIsStreaming(boolean pIsStreaming);
}
