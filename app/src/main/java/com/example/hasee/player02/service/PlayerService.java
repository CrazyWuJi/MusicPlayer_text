package com.example.hasee.player02.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.hasee.player02.db.MusicList;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{
    private MediaPlayer mper;
    private PlayBinder mBinder=new PlayBinder();
    private Uri uri;
    private int number=0;
    private Boolean Looping=false,isPrepared=false;
    private List<MusicList> musicLists;
    public PlayerService() {
    }

    @Override
    public void onCreate(){
        mper=new MediaPlayer();
        //Toast.makeText(this,"创建完成",Toast.LENGTH_SHORT).show();
        mper.setOnPreparedListener(this);
        mper.setOnErrorListener(this);
        mper.setOnCompletionListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mper.seekTo(0);
        isPrepared=false;
        int counter=DataSupport.count(MusicList.class);
        if(number+1>counter){
            number=0;
        }else{
            number++;
        }
        setDataSource(5);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(PlayerService.this,"发生错误，停止播放"+"/"+i+"/"+i1,Toast.LENGTH_SHORT).show();
        isPrepared=false;
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPrepared=true;
        Toast.makeText(PlayerService.this,"准备完成",Toast.LENGTH_SHORT).show();
    }

    public void setDataSource(int num){
        try {
            isPrepared=false;
            mper.reset();
            //musicLists.clear();
            musicLists= DataSupport.findAll(MusicList.class);
            //Toast.makeText(PlayerService.this,"setDataSource",Toast.LENGTH_SHORT).show();
            MusicList musicSelected=musicLists.get(number);
            uri=Uri.parse(musicSelected.getUri());
            //Toast.makeText(PlayerService.this,"Show Message"+uri.toString(),Toast.LENGTH_SHORT).show();
            mper.setDataSource(this,uri);
            //Toast.makeText(PlayerService.this,"Show Message"+uri.toString(),Toast.LENGTH_SHORT).show();
            mper.setLooping(Looping);
            mper.prepareAsync();
            //Toast.makeText(PlayerService.this,"Show Message"+uri.toString(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,e.toString()+"啦啦啦"+number,Toast.LENGTH_SHORT).show();
        }
    }


    public class PlayBinder extends Binder{
        Context context;
        public void setDataNumber(int Number, Context context){
            number=Number;
            this.context=context;
            //Toast.makeText(context,String.valueOf(number)+"ooo",Toast.LENGTH_SHORT).show();
            PlayerService.this.setDataSource(number);
            //PlayerService.this.show(number);
        }
        public void startPlay(){
            if(!PlayerService.this.isPrepared){
                Toast.makeText(context,"还未准备好！",Toast.LENGTH_SHORT).show();
            }else{
                if(mper.isPlaying()){
                    mper.pause();
                }else{
                    mper.start();
                }
            }
        }
        public boolean isPlaying(){
            return mper.isPlaying();
        }
        public void setLooping(Boolean b,Context context){
            this.context=context;
            Looping=b;
            mper.setLooping(b);
            if(b){
                Toast.makeText(context,"单曲循环",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"列表循环",Toast.LENGTH_SHORT).show();
            }
        }
        public int getProgress(){
            return mper.getCurrentPosition();
        }
        public void setProgress(int progress){
            mper.seekTo(progress);
        }
        public int getDuration(){
            return mper.getDuration();
        }
        public boolean isPrepared(){
            return isPrepared;
        }
    }
}
