package com.example.nckle.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.text.format.Formatter;
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
import android.widget.Toast;

public class MainActivity extends Activity {

    SharedPreferences pref;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton pauseButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private ImageButton repeatAllButton;
    private ImageButton stopButton;
    private SeekBar seekBar;
    private SeekBar seekBarVolume;
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
        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);


        setContentView(R.layout.activity_main);

        if (!(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //File write logic here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED)) {
            //File write logic here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, MainActivity.REQUEST_CODE);
        }

        pref = getApplicationContext().getSharedPreferences("manets", 0);
        playButton = (ImageButton) findViewById(R.id.playButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        previousButton = (ImageButton) findViewById(R.id.backButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        repeatButton = (ImageButton) findViewById(R.id.repeatButton);
        repeatAllButton = (ImageButton) findViewById(R.id.repeatAllButton);
        shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarVolume = (SeekBar) findViewById(R.id.seekBarVolume);
        seekBarVolume.setMax(AbstractMediaComponent.MAX_VOLUME);
        seekBarVolume.setProgress(AbstractMediaComponent.DEFAULT_VOLUME);
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
            }
            public void onSwipeLeft() {
                topMediaPlayer.previous();
            }

        });

        String ipAddress = "192.168.0.103";
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
            ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        } catch (Exception e) {
            Toast.makeText(this, "need wifi permission", Toast.LENGTH_LONG).show();
        }

        String host = pref.getString("host", ipAddress + ":8080");
        clientHostText.setText(host);
        topMediaPlayer = new TopMediaPlayer(new MediaServer(this, host));

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
                seekBarVolume.setProgress(topMediaPlayer.getVolume());

                if (topMediaPlayer.isPlaying()) {
                    switchStopButton();
                } else {
                    switchPlayButton();
                }

                handler.postDelayed(this, 500);
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser) {
                topMediaPlayer.setVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                    switchStopButton();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.stop();
                switchPlayButton();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.next();
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
                topMediaPlayer.repeatOne();
            }
        });

        repeatAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.repeatAll();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topMediaPlayer.previous();
            }
        });

        clientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String host = clientHostText.getText().toString();
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    SharedPreferences.Editor editor = pref.edit();
                    switchToClient(host);
                    editor.putString("host", host);
                    editor.apply();
                } else {
                    switchToServer(host);
                }

            }
        });

        streamSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean pIsStreaming) {
                topMediaPlayer.setIsStreaming(pIsStreaming);
            }
        });

    }

    // Crash on s4
    private void switchPlayButton() {
//        stopButton.setEnabled(false);
//        stopButton.setVisibility(View.GONE);
//        playButton.setEnabled(true);
//        playButton.setVisibility(View.VISIBLE);
    }
    // Crash on s4
    private void switchStopButton() {
//        playButton.setEnabled(false);
//        playButton.setVisibility(View.GONE);
//        stopButton.setEnabled(true);
//        stopButton.setVisibility(View.VISIBLE);
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

    private void switchToServer(String aHost) {
        topMediaPlayer.release();
        topMediaPlayer = new TopMediaPlayer(new MediaServer(this, aHost));
    }

}

