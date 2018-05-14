package com.example.nckle.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
//import android.os.Handler;
import android.os.Handler;

public class MainActivity extends Activity {

    private  MediaPlayer mp;
    private Button playButton;
    private Button stopButton;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.song);

        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        seekBar.setMax(mp.getDuration());

        final Handler handler = new Handler();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mp != null){
                    seekBar.setProgress(mp.getCurrentPosition());
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
                    mp.start();
                    switchPausePlayButton();
                }else {
                    mp.pause();
                    resetPlayButton();
                }
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.stop();
                mp = MediaPlayer.create(MainActivity.this, R.raw.song);
                resetPlayButton();

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
}

