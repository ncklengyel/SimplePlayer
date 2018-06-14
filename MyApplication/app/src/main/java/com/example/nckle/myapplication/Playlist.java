package com.example.nckle.myapplication;

import android.net.Uri;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.nckle.myapplication.Utils.getRandomNumber;

public class Playlist {

    private ArrayList<Song> songs;
    private int currentIndex;
    private int firstIndex;
    private int lastIndex;
    private boolean isShuffling = false;
    private boolean isRepeatAll = false;
    private boolean isRepeatOne = false;

    public Playlist(ArrayList<Song> pSongs){

        songs = pSongs;
        currentIndex = 0;
        firstIndex = 0;
        lastIndex = songs.size() - 1;
    }

    public void next(){
        // If not shuffling go next song
        if (!isShuffling) {
            currentIndex++;
        } else if(isRepeatOne) {

        } else {
            currentIndex = getRandomNumber(this.getNumberOfSongs());
        }

        if (currentIndex > lastIndex)
            reset();
    }

    public void previous(){
        // If not shuffling go next song
        if (!isShuffling) {
            currentIndex--;
        } else if(isRepeatOne) {

        } else {
            currentIndex = getRandomNumber(this.getNumberOfSongs());
        }

        if (currentIndex < firstIndex)
            reset();
    }

    public Song getCurrentSong(){
        if (getNumberOfSongs() != 0) {
            return songs.get(currentIndex);
        }
        return null;
    }
    public void shuffle(){
        if (!isRepeatOne) {
            isShuffling = !isShuffling;
            next();
        }
    }
    public void repeat(){
        isRepeatOne = !isRepeatOne;
    }

    public int getNumberOfSongs(){
        if (songs != null) {
            return songs.size();
        }
        return 0;
    }

    public void reset(){
        currentIndex = firstIndex;
    }
}
