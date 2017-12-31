package com.example.hasee.player02.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.hasee.player02.Fragments.WordView;
import com.example.hasee.player02.Fragments.interface_class;
import com.example.hasee.player02.Listener.PlayerListener_Service;
import com.example.hasee.player02.db.MusicList;

import org.litepal.crud.DataSupport;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{
    private MediaPlayer mper;
    private PlayBinder mBinder=new PlayBinder();
    private Uri uri;
    private int number=0;
    private Timer timer;
    private Boolean Looping=false,isPrepared=false,everPlayed=false,isStart=false;
    private List<MusicList> musicLists;
    public PlayerService() {
    }
    private PlayerListener_Service playerListener_service;
    private Handler mHander;
    private List<Integer> mTimeList;
    public Bitmap musicPic;
    public WordView mWordView;
    public int mIndex=0;
    String musicName;

    @Override
    public void onCreate(){
        mper=new MediaPlayer();
        timer=new Timer();
        //Toast.makeText(this,"创建完成",Toast.LENGTH_SHORT).show();
        mper.setOnPreparedListener(this);
        mper.setOnErrorListener(this);
        mper.setOnCompletionListener(this);
        mHander=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==100){
                    playerListener_service.setProgress(mper.getCurrentPosition());
                }else if(msg.what==200){
                    mWordView.mIndex=mIndex;
                    mWordView.invalidate();
                }
            }
        };
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(everPlayed){
                    if(isPrepared){
                        mHander.sendEmptyMessage(100);
                    }
                    int ii=0,iii=0;
                    if(mper.isPlaying()){
                        ii=mper.getCurrentPosition();
                        iii=mper.getDuration();
                    }
                    if(ii<iii){
                        for (int i = 0; i < mTimeList.size(); i++) {
                            if (i < mTimeList.size() - 1) {
                                if (ii < mTimeList.get(i) && i == 0) {
                                    mIndex = i;
                                }
                                if ((ii > mTimeList.get(i))&& ii < mTimeList.get(i+1)) {
                                    mIndex = i;
                                }
                            }
                            if ((i == mTimeList.size() - 1)&& ii > mTimeList.get(i)) {
                                mIndex = i;
                            }
                        }
                        mHander.sendEmptyMessage(200);
                    }
                }

            }
        },0,50);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mper.seekTo(0);
        everPlayed=true;
        isPrepared=false;
        int counter=DataSupport.count(MusicList.class);
        if(counter<=0){
            Toast.makeText(PlayerService.this,"列表为空",Toast.LENGTH_SHORT).show();
            number=0;
        }else{
            if(number+1>=counter){
                number=0;
            }else{
                number++;
            }
            playerListener_service.initBtnPlay();
            isStart=true;
            setDataSource(5);
        }

    }

    public void Previous(MediaPlayer mediaPlayer){
        mper.seekTo(0);
        isPrepared=false;
        everPlayed=true;
        int counter=DataSupport.count(MusicList.class);
        if(counter<=0){
            Toast.makeText(PlayerService.this,"列表为空",Toast.LENGTH_SHORT).show();
            number=0;
        }else{
            if(number-1<0){
                number=counter-1;
            }else{
                number--;
            }
            playerListener_service.initBtnPlay();
            isStart=true;
            setDataSource(5);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        isPrepared=false;
        Toast.makeText(PlayerService.this,"发生错误，停止播放"+"/"+i+"/"+i1,Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPrepared=true;
        everPlayed=true;
        mIndex=0;
        playerListener_service.setTitle(musicName);
        playerListener_service.setDuration(mper.getDuration());
        playerListener_service.setMusicPic(musicPic);
        if(isStart){
            mper.start();
            playerListener_service.initBtnPlay();
        }
       //Toast.makeText(PlayerService.this,"准备完成",Toast.LENGTH_SHORT).show();
    }

    public void setDataSource(int num){
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        try {
            isPrepared=false;
            if(DataSupport.count(MusicList.class)<=0){
                Toast.makeText(PlayerService.this,"列表为空",Toast.LENGTH_SHORT).show();
                number=0;
            }else{
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

                mmr.setDataSource(PlayerService.this,uri);
                musicName=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                if(musicName==null){
                    musicName=uri.getLastPathSegment();
                }
                byte[] embedPic=mmr.getEmbeddedPicture();
                musicPic= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);

                //Toast.makeText(PlayerService.this,"Show Message"+uri.toString(),Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("PlayerService",e.toString());
            Toast.makeText(this,e.toString()+number,Toast.LENGTH_SHORT).show();
        }finally {
            mmr.release();
        }
    }


    public class PlayBinder extends Binder{
        Context context;
        int pp;
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
        public int getPregress(){
            return PlayerService.this.mper.getCurrentPosition();
        }
        public void setNewPregress(int pro){
            this.pp=pro;
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    PlayerService.this.mper.seekTo(pp);
                }
            };
            Timer tt=new Timer();
            tt.schedule(task,500);
        }
        public void setProgress(int progress){
            PlayerService.this.mper.seekTo(progress);
        }
        public void setListener_service(PlayerListener_Service pp){
            PlayerService.this.playerListener_service=pp;
            PlayerService.this.mTimeList=pp.getTimeList();
            //Toast.makeText(PlayerService.this,String.valueOf(pp.getTimeList().size()),Toast.LENGTH_SHORT).show();
            PlayerService.this.mWordView=pp.getWordView();
        }
        public void setmTimeList(List<Integer> ii){
            PlayerService.this.mTimeList=ii;
        }
        public void nextOne(){
            PlayerService.this.onCompletion(PlayerService.this.mper);
        }
        public void previousOne(){
            PlayerService.this.Previous(PlayerService.this.mper);
        }
        public void stop(){
            PlayerService.this.mper.pause();
            PlayerService.this.mper.release();
        }
        public Boolean isEverPlayed(){
            return PlayerService.this.everPlayed;
        }
        public int getPlayingNumber(){
            return number;
        }
    }
}
