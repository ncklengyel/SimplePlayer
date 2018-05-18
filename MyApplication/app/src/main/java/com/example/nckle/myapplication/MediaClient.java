package com.example.nckle.myapplication;

public class MediaClient extends AbstractMediaComponent {

    private String host;
    private int port;

    public MediaClient(String pHost, int pPort){

        host = pHost;
        port = pPort;

    }
    public void play(){

    }

    public void next(){

    }

    public void back(){

    }

    public void shuffle(){

    }

    public void stop(){

    }

    public void repeatOne(){

    }

    public void repeatAll(){

    }
    public int getCurrentPosition(){
        return 0;
    }

    public int getDuration(){
        return 0;
    }

    public void seekTo(int position){

    }

    public void pause(){

    }


    public boolean isPlaying(){
        return false;
    }

}
