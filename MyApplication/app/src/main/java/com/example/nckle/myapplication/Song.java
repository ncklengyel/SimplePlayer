package com.example.nckle.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Song {

    private Uri path;
    private String title;
    private String artist;
    private String album;
    private String length;
    private Bitmap albumImage;

    public String getTitle() {
        if (title != null) {
            return title;
        }
        return "";
    }

    public String getArtist() {
        if (artist != null) {
            return artist;
        }
        return "";
    }

    public Uri getPath() {
        return path;
    }

    public String getAlbum() {
        if (album != null) {
            return album;
        }
        return "";
    }

    public String getLength() {
        if (length != null) {
            return length;
        }
        return "0";
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    private String getAlbumImageJSON() {
        if (albumImage != null) {
            final int COMPRESSION_QUALITY = 100;
            String encodedAlbumImage;
            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            albumImage.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayBitmapStream);
            byte[] b = byteArrayBitmapStream.toByteArray();
            encodedAlbumImage = Base64.encodeToString(b, Base64.DEFAULT);
            return encodedAlbumImage;
        }
        return null;
    }

    public String getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("title", getTitle());
            json.put("artist", getArtist());
            json.put("length", getLength());
            json.put("album", getAlbum());
            json.put("albumImage", getAlbumImageJSON());
        } catch (JSONException jsonE) {
            // TODO handle this exception
        }
        return json.toString();
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

    public Song(
            String pTitle,
            String pArtist,
            String pAlbum,
            String pLength,
            Bitmap pAlbumImage
    ){
        title = pTitle;
        artist = pArtist;
        album = pAlbum;
        length = pLength;
        albumImage = pAlbumImage;
    }

}
