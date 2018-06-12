package com.example.nckle.myapplication;

import android.net.Uri;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Playlist {

    private ArrayList<Song> songs;
    private int currentIndex;
    private int firstIndex;
    private int lastIndex;

    public Playlist(ArrayList<Song> pSongs){

        songs = pSongs;
        currentIndex = 0;
        firstIndex = 0;
        lastIndex = songs.size() - 1;
    }

    public void next(){
        currentIndex++;

        if (currentIndex > lastIndex)
            reset();
    }

    // TODO maybe check if it's the right thing to return the uri
    public void previous(){
        currentIndex--;

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
        int numberOfSongs = getNumberOfSongs();
        if (numberOfSongs != 0) {
            currentIndex = ThreadLocalRandom.current().nextInt(0, getNumberOfSongs());
        }
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
