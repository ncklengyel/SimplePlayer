package com.example.nckle.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Button;
import android.os.Handler;
import android.widget.TextView;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {

    //private  MediaPlayer mp;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton backButton;
    private ImageButton pauseButton;
    private SeekBar seekBar;
    private TextView timeRight;
    private TextView timeLeft;
    private int durationSong;

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //File write logic here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE);

        }

        final TopMediaPlayer topMediaPlayer = new TopMediaPlayer(new MediaServer(this));
        /*final TopMediaPlayer topMediaPlayer = new TopMediaPlayer(new MediaClient("192.168.2.17",8080));
        topMediaPlayer.play();
        topMediaPlayer.next();
        topMediaPlayer.stop();*/

        durationSong = topMediaPlayer.getDuration();

        playButton = (ImageButton) findViewById(R.id.playButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        switchPlayButton();
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        timeLeft = (TextView) findViewById(R.id.timeLeftText);
        timeRight = (TextView) findViewById(R.id.timeRightText);
        timeRight.setText(millisecondToMMSS(durationSong));

        seekBar.setMax(durationSong);

        final Handler handler = new Handler();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                    int currentPosition = topMediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    timeLeft.setText(millisecondToMMSS(currentPosition));

                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    topMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!topMediaPlayer.isPlaying())
                {
                   topMediaPlayer.play();
                   switchPauseButton();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topMediaPlayer.isPlaying()){
                    topMediaPlayer.pause();
                    switchPlayButton();
                }
            }
        });

    }

    private void switchPlayButton(){
        pauseButton.setEnabled(false);
        pauseButton.setVisibility(View.GONE);
        playButton.setEnabled(true);
        playButton.setVisibility(View.VISIBLE);
    }

    private void switchPauseButton() {
        playButton.setEnabled(false);
        playButton.setVisibility(View.GONE);
        pauseButton.setEnabled(true);
        pauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //release mediaplayer
    }

    /**
     * Function that converts a time in milliseconds to a string mm:ss formated
     * @param ms milliseconds to convert to format mm:ss
     * @return returns a String in mm:ss format
     */
    private String millisecondToMMSS(int ms){

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

}

