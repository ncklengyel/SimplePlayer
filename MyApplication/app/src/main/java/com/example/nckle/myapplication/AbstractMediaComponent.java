package com.example.nckle.myapplication;

public abstract class AbstractMediaComponent {

    abstract public void play();
    abstract public void next();
    abstract public void back();
    abstract public void shuffle();
    abstract public void stop();
    abstract public void repeatOne();
    abstract public void repeatAll();
    abstract public int getCurrentPosition();
    abstract public int getDuration();
    abstract public void seekTo(int position);
    abstract public void pause();
    abstract public boolean isPlaying();
    abstract public void release();

}
