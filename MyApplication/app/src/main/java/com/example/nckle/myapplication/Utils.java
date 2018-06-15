package com.example.nckle.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    /**
     * Function that converts a time in milliseconds to a string mm:ss formated
     * @param ms milliseconds to convert to format mm:ss
     * @return returns a String in mm:ss format
     */
    public static String millisecondToMMSS(int ms) {

        int seconds = (ms/1000) % 60;
        int minutes = (ms/1000) / 60;
        String time = "0:00";

        //Condition to have 0:00 format and not 0:0 when seconds are <10
        if (seconds<10){
            time = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
        } else {
            time = Integer.toString(minutes) + ":" + Integer.toString(seconds);
        }

        return time;

    }

    public static int getRandomNumber(int maximum) {
        if (maximum != 0) {
            return ThreadLocalRandom.current().nextInt(0, maximum);
        }
        return 0;
    }

    public static Bitmap convertJSONtoBitmap(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static ArrayList<Song> getMusicOnDevice(){

        ArrayList<Song> songs = new ArrayList<Song>();
        File musicFile = new File(Environment.getExternalStorageDirectory().toString() + "/Music");
        File[] files = musicFile.listFiles();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        for (File file : files) {

            if (file.getName().endsWith(".mp3")){
                mmr.setDataSource(file.getAbsolutePath());
                Song newSong = new Song(
                        Uri.parse(file.getAbsolutePath()),
                        mmr
                );
                songs.add(newSong);
            }

        }

        return songs;

    }

    public static int validateVolumeInt(int volume){
        if (volume < 0)
            return 0;

        if (volume >= AbstractMediaComponent.MAX_VOLUME)
            return AbstractMediaComponent.MAX_VOLUME;

        return volume;
    }

    //Inspiré du code suivant: https://stackoverflow.com/questions/5215459/android-mediaplayer-setvolume-function
    public static float getComputedVolume(int aVolume){

        int volume = validateVolumeInt(aVolume);

        if (volume == AbstractMediaComponent.MAX_VOLUME)
            return 1;

        return 1-((float)(Math.log(AbstractMediaComponent.MAX_VOLUME - volume)/Math.log(AbstractMediaComponent.MAX_VOLUME)));
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
