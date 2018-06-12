package com.example.nckle.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class Song {

    private Uri path;
    private String title;
    private String artist;
    private String album;
    private String length;
    private Bitmap albumImage;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public String getLength() {
        return length;
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public Song(
            Uri pPath,
            MediaMetadataRetriever mmr
    ){
        try {
            byte[] imageBytes = mmr.getEmbeddedPicture();
            albumImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            albumImage = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        }
        path = pPath;
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        length = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

}
