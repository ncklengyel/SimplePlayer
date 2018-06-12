package com.example.nckle.myapplication;

interface AbstractMediaComponent {

    void play();

    void next();

    void back();

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

}
