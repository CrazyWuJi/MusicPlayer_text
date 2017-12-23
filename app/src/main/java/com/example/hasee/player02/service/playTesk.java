package com.example.hasee.player02.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.hasee.player02.Listener.PlayerListener;

/**
 * Created by hasee on 2017/12/21 0021.
 */

public class playTesk extends AsyncTask<String,Integer,Integer>{
    public static final int PLAY_COMPLETE=0;
    public static final int PLAY_FAILED=1;
    public static final int PLAY_PAUSED=2;
    public static final int PLAY_STOPED=3;
    private boolean isStoped=false;
    private boolean isPaused=false;
    private boolean isSeekBarChanged=false;
    private int Progress;
    private PlayerListener listener;
    private MediaPlayer mper;
    private Context context;
    public playTesk(PlayerListener listener,int progress,Context context){
        this.context=context;
        this.Progress=progress;
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(String... parems){
        try{
            Uri fileUri=Uri.parse(parems[0]);
            mper=new MediaPlayer();
            mper.reset();
            mper.setDataSource(context,fileUri);

        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return PLAY_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values){

    }

    @Override
    protected void onPostExecute(Integer status){

    }

    public int pausePlayer(){
        isPaused=true;
        return Progress;
    }

    public void stopPlayer(){
        isStoped=true;
    }
}
