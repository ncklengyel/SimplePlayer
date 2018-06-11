package com.example.nckle.myapplication;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.os.Handler;
import android.widget.Switch;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.widget.CompoundButton;
import android.app.AlertDialog;

public class MainActivity extends Activity {

    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton backButton;
    private ImageButton pauseButton;
    private SeekBar seekBar;
    private TextView timeRight;
    private TextView timeLeft;
    private TopMediaPlayer topMediaPlayer;
    private Switch clientSwitch;
    private EditText clientHostText;

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //File write logic here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE);

        }

        //TODO We start as client for test purposes (We might what to start as server later on)
        //topMediaPlayer = new TopMediaPlayer(new MediaClient("192.168.1.100",8080));

        //topMediaPlayer = new TopMediaPlayer(new MediaServer(this));
        playButton = (ImageButton) findViewById(R.id.playButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        timeLeft = (TextView) findViewById(R.id.timeLeftText);
        timeRight = (TextView) findViewById(R.id.timeRightText);
        clientSwitch = (Switch) findViewById(R.id.clientSwitch);
        clientHostText = (EditText) findViewById(R.id.clientHostText);

        topMediaPlayer = new TopMediaPlayer(new MediaServer(this));

        final Handler handler = new Handler();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    int currentPosition = topMediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    timeLeft.setText(Utils.millisecondToMMSS(currentPosition));

                if (topMediaPlayer.isPlaying()) {
                    switchPauseButton();
                }else{
                    switchPlayButton();
                }

                handler.postDelayed(this, 500);
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
                topMediaPlayer.pause();
                switchPlayButton();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.next();
                switchPauseButton();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.back();
                switchPauseButton();
                seekBar.setMax(topMediaPlayer.getDuration());
            }
        });

        clientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked){
                    switchToClient(clientHostText.getText().toString());
                }else{
                    switchToServer();
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
        topMediaPlayer.release();
    }


    private void switchToClient(String aHost){

        String host[] = aHost.split(":");
        String hostname = host[0];
        int port;

        try{
             port = Integer.parseInt(host[1].trim());
        }catch(NumberFormatException nfe){
            Log.e("MediaClient", "Convertion string to int failed for port");
            clientSwitch.setChecked(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Invalid port number");
            builder.setCancelable(true);
            builder.show();
            return;
        }

        topMediaPlayer.release();
        topMediaPlayer = new TopMediaPlayer(new MediaClient(hostname,port));
    }

    private void switchToServer(){
        topMediaPlayer.release();
        topMediaPlayer = new TopMediaPlayer(new MediaServer(this));
    }

}

