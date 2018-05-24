package com.example.nckle.myapplication;

import android.net.Uri;

import java.util.ArrayList;

public class Playlist {

    private ArrayList<Uri> songs;
    private int currentIndex;
    private int firstIndex;
    private int lastIndex;

    public Playlist(ArrayList<Uri> pSongs){

        songs = pSongs;
        currentIndex = 0;
        firstIndex = 0;
        lastIndex = songs.size() - 1;
    }

    public Uri next(){

        currentIndex++;

        if (currentIndex > lastIndex)
            reset();

        return songs.get(currentIndex);

    }

    public Uri previous(){

        currentIndex--;

        if (currentIndex < firstIndex)
            reset();

        return songs.get(currentIndex);
    }

    public Uri getCurrentSong(){
        return songs.get(currentIndex);
    }

    public void reset(){
        currentIndex = firstIndex;
    }
}
