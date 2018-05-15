package com.example.nckle.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends Activity {

    private  MediaPlayer mp;
    private Button playButton;
    private Button stopButton;
    private SeekBar seekBar;
    private TextView timeRight;
    private TextView timeLeft;
    private int durationSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.song);

        durationSong = mp.getDuration();

        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        timeLeft = (TextView) findViewById(R.id.timeLeftText);
        timeRight = (TextView) findViewById(R.id.timeRightText);
        timeRight.setText(millisecondToMMSS(durationSong));


        seekBar.setMax(durationSong);

        final Handler handler = new Handler();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mp != null){

                    int currentPosition = mp.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    timeLeft.setText(millisecondToMMSS(currentPosition));

                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp != null && fromUser){
                    mp.seekTo(progress);
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
                if (!mp.isPlaying())
                {
                    play();
                }else {
                    pause();
                }
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stop();

            }
        });
    }

    private void resetPlayButton() {
        playButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        playButton.setText(getResources().getString(R.string.playButtonText));
    }

    private void switchPausePlayButton() {
        playButton.setText("Pause");
        playButton.setBackgroundColor(Color.BLUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
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

    private void pause(){
        mp.pause();
        resetPlayButton();
    }

    private void play(){
        mp.start();
        switchPausePlayButton();
    }

    private void stop(){
        mp.stop();
        mp = MediaPlayer.create(MainActivity.this, R.raw.song);
        resetPlayButton();
    }
}

