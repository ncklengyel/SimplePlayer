package com.example.nckle.myapplication;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class MediaServerStreaming extends NanoHTTPD implements AbstractMediaComponent {

    private Context mContext;


    public MediaServerStreaming(Context aContext){
        super(8080);
        mContext = aContext;
        try {
            super.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        InputStream myInput = null;
        try {
            myInput = new FileInputStream(Utils.getMusicOnDevice().get(0).getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createResponse(Response.Status.OK, "audio/mpeg", myInput);
    }

    //Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType,
                                    InputStream message) {
        return newChunkedResponse(status, mimeType, message);
    }


    public void play(){};

    public void next(){};

    public void previous(){};

    public void shuffle(){};

    public void stop(){};

    public void repeatOne(){};

    public void repeatAll(){};

    public int getCurrentPosition(){return -1;};

    public int getDuration(){return -1;};

    public void seekTo(int position){};

    public void pause(){};

    public boolean isPlaying(){return false;};

    public void release(){};

    public void setVolume(int level){};

    public String getTitle(){return "";};

    public String getAuthor(){return "";};

    public String getAlbum(){return "";};

    //public Bitmap getAlbumImage(){return new Bitmap(null);};

    //public Song getSong(){return new Song()};
}
