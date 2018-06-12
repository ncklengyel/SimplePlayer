package com.example.nckle.myapplication;

import android.net.Uri;

import java.util.ArrayList;

public class Song {

    private Uri songUri;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    private String author;
    public Uri getSongUri() {
        return songUri;
    }

    public Song(
            Uri pSongUri,
            String pTitle,
            String pAuthor
    ){
        songUri = pSongUri;
        title = pTitle;
        author = pAuthor;
    }

}
