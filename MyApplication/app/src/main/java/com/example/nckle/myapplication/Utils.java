package com.example.nckle.myapplication;

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
}
