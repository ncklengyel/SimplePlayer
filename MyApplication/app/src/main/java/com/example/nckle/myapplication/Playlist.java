package com.example.nckle.myapplication;

import android.net.Uri;

import java.util.ArrayList;

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

    // TODO maybe check if it's the right thing to return the uri
    public Uri next(){

        currentIndex++;

        if (currentIndex > lastIndex)
            reset();

        return songs.get(currentIndex).getSongUri();

    }

    // TODO maybe check if it's the right thing to return the uri
    public Uri previous(){

        currentIndex--;

        if (currentIndex < firstIndex)
            reset();

        return songs.get(currentIndex).getSongUri();
    }

    public Song getCurrentSong(){
        if (songs.size() != 0) {
            return songs.get(currentIndex);
        }
        return null;
    }

    public void reset(){
        currentIndex = firstIndex;
    }
}
