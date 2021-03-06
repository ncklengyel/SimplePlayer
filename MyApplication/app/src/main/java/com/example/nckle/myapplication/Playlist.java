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
            currentIndex = currentIndex;
        } else {
            currentIndex = getRandomNumber(this.getNumberOfSongs());
        }

        if (currentIndex > lastIndex)
            reset(true, false);
    }

    public void previous(){
        // If not shuffling go next song
        if (!isShuffling) {
            currentIndex--;
        } else if(isRepeatOne) {
            currentIndex = currentIndex;
        } else {
            currentIndex = getRandomNumber(this.getNumberOfSongs());
        }

        if (currentIndex < firstIndex)
            reset(false, false
            );
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
        }
    }

    public void setRepeatOne(boolean value){
        isRepeatOne = value;
    }

    public void setRepeatAll(boolean value){
        isRepeatAll = value;
    }

    public boolean getIsShuffling(){
        return isShuffling;
    }

    public boolean getIsRepeatingOne(){
        return isRepeatOne;
    }

    public boolean getIsRepeatingAll(){
        return isRepeatAll;
    }

    public int getNumberOfSongs(){
        if (songs != null) {
            return songs.size();
        }
        return 0;
    }

    public void reset(boolean isNext, boolean isReset){
        if (!isRepeatAll || isReset) {
            currentIndex = firstIndex;
        } else {
            if (isNext) {
                currentIndex = firstIndex;
            } else {
                currentIndex = getNumberOfSongs() - 1;
            }
        }
    }
}
