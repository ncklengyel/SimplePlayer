package com.example.nckle.myapplication;

import android.graphics.Bitmap;

import org.json.JSONObject;

public class TopMediaPlayer implements AbstractMediaComponent {

    public AbstractMediaComponent component;

    public TopMediaPlayer(AbstractMediaComponent pComponent) {
        component = pComponent;
    }

    public void setVolume(int level) {
        component.setVolume(level);
    }

    public void play() {
        component.play();
    }

    public void next() {
        component.next();
    }

    public void previous() {
        component.previous();
    }

    public void shuffle() {
        component.shuffle();
    }

    public void stop() {
        component.stop();
    }

    public void repeatOne() {
        component.repeatOne();
    }

    public void repeatAll() {
        component.repeatAll();
    }

    public int getDuration() {
        return component.getDuration();
    }

    public int getCurrentPosition() {
        return component.getCurrentPosition();
    }

    public void seekTo(int position) {
        component.seekTo(position);
    }

    public void pause() {
        component.pause();
    }

    public boolean isPlaying() {
        return component.isPlaying();
    }

    public void release() {
        component.release();
    }

    public String getTitle() {
        return component.getTitle();
    }

    public String getAuthor() {
        return component.getAuthor();
    }

    public String getAlbum() {
        return component.getAlbum();
    }

    public Song getSong() {
        return component.getSong();
    }

    public Bitmap getAlbumImage() {
        return component.getAlbumImage();
    }

    public void setIsStreaming(boolean pIsStreaming) {
        component.setIsStreaming(pIsStreaming);
    }

    public int getVolume(){
        return component.getVolume();
    }
}
