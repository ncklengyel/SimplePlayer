package com.example.nckle.myapplication;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private SeekBar seekBar;
    private TextView timeRight;
    private TextView timeLeft;
    private TopMediaPlayer topMediaPlayer;
    private Switch clientSwitch;
    private Switch streamSwitch;
    private EditText clientHostText;
    private TextView lblTitle;
    private TextView lblAuthor;
    private TextView lblAlbum;
    private ImageView imgAlbumArt;


    private boolean isClient = false;
    public static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //File write logic here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE);
        }

        playButton = (ImageButton) findViewById(R.id.playButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        repeatButton = (ImageButton) findViewById(R.id.repeatButton);
        shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        timeLeft = (TextView) findViewById(R.id.timeLeftText);
        timeRight = (TextView) findViewById(R.id.timeRightText);
        clientSwitch = (Switch) findViewById(R.id.clientSwitch);
        streamSwitch = (Switch) findViewById(R.id.streamSwitch);
        clientHostText = (EditText) findViewById(R.id.clientHostText);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAuthor = (TextView) findViewById(R.id.lblAuthor);
        lblAlbum = (TextView) findViewById(R.id.lblAlbum);
        imgAlbumArt = (ImageView) findViewById(R.id.imgAlbumArt);

        // TODO if time refactor this
        imgAlbumArt.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){

            public void onSwipeRight() {
                topMediaPlayer.next();
                switchPauseButton();
            }
            public void onSwipeLeft() {
                topMediaPlayer.previous();
                switchPauseButton();
            }

        });

        topMediaPlayer = new TopMediaPlayer(new MediaServer(this));

        final Handler handler = new Handler();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int songDuration = topMediaPlayer.getDuration(); //Duration of the song in mms
                int currentPosition = topMediaPlayer.getCurrentPosition(); //current position of the song currently playing
                seekBar.setMax(songDuration);
                seekBar.setProgress(currentPosition);
                timeLeft.setText(Utils.millisecondToMMSS(currentPosition));
                timeRight.setText(Utils.millisecondToMMSS(songDuration));
                lblTitle.setText(topMediaPlayer.getTitle());
                lblAuthor.setText(topMediaPlayer.getAuthor());
                lblAlbum.setText(topMediaPlayer.getAlbum());
                imgAlbumArt.setImageBitmap(topMediaPlayer.getAlbumImage());

                if (topMediaPlayer.isPlaying()) {
                    switchPauseButton();
                } else {
                    switchPlayButton();
                }

                handler.postDelayed(this, 500);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
                if (!topMediaPlayer.isPlaying()) {
                    topMediaPlayer.play();
                    switchPauseButton();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO bring back this if
//                if (topMediaPlayer.isPlaying())
//                {
                topMediaPlayer.pause();
                switchPlayButton();
//                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.next();
                switchPauseButton();
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.shuffle();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.toggleRepeatMode();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.previous();
                switchPauseButton();
            }
        });

        clientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    switchToClient(clientHostText.getText().toString());
                } else {
                    switchToServer();
                }

            }
        });

        streamSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean pIsStreaming) {
                topMediaPlayer.setIsStreaming(pIsStreaming);
            }
        });

    }

    private void switchPlayButton() {
//        pauseButton.setEnabled(false);
//        pauseButton.setVisibility(View.GONE);
//        playButton.setEnabled(true);
//        playButton.setVisibility(View.VISIBLE);
    }

    private void switchPauseButton() {
//        playButton.setEnabled(false);
//        playButton.setVisibility(View.GONE);
//        pauseButton.setEnabled(true);
//        pauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        topMediaPlayer.release();
    }

    private void switchToClient(String aHost) {
        topMediaPlayer.release();
        topMediaPlayer = new TopMediaPlayer(new MediaClient(this, aHost));
    }

    private void switchToServer() {
        topMediaPlayer.release();
        topMediaPlayer = new TopMediaPlayer(new MediaServer(this));
    }

}

