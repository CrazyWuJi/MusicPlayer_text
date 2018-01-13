package com.example.hasee.player02.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hasee.player02.Fragments.WordView;
import com.example.hasee.player02.Fragments.interface_class;
import com.example.hasee.player02.Listener.PlayerListener_Service;
import com.example.hasee.player02.MainActivity;
import com.example.hasee.player02.R;
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
    private Boolean Looping=false,isPrepared=false,everPlayed=false,isStart=false,isPause=false;
    private List<MusicList> musicLists;
    public PlayerService() {
    }
    private PlayerListener_Service playerListener_service;
    private Handler mHander;
    private List<Integer> mTimeList;
    public Toast tos;
    public Bitmap musicPic;
    public WordView mWordView;
    public int mIndex=0;
    String musicName;

    //服务被创建时调用，获取各种实例。
    @Override
    public void onCreate(){
        tos=Toast.makeText(PlayerService.this,"",Toast.LENGTH_SHORT);
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
                    if(isPause){
                        getNotificationManager().notify(1,getNotification(musicName));
                    }
                }else if(msg.what==200){
                    mWordView.mIndex=mIndex;
                    mWordView.invalidate();
                }
            }
        };
    }


    //用于活动与服务建立连接时返回Bind类实例。
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //服务被启动时调用，新建定时任务更新播放位置。
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(everPlayed) {
                    if (isPrepared) {
                        mHander.sendEmptyMessage(100);
                    }
                    int ii = 0, iii = 0;
                    if (mper.isPlaying()) {
                        ii = mper.getCurrentPosition();
                        iii = mper.getDuration();
                    }
                    if (ii < iii) {
                        for (int i = 0; i < mTimeList.size(); i++) {
                            if (i < mTimeList.size() - 1) {
                                if (ii < mTimeList.get(i) && i == 0) {
                                    mIndex = i;
                                }
                                if ((ii > mTimeList.get(i)) && ii < mTimeList.get(i + 1)) {
                                    mIndex = i;
                                }
                            }
                            if ((i == mTimeList.size() - 1) && ii > mTimeList.get(i)) {
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

    //MediaPlayer类歌曲播放完毕时调用。
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mper.seekTo(0);
        everPlayed=true;
        isPrepared=false;
        int counter=DataSupport.count(MusicList.class);
        if(counter<=0){
            Toast.makeText(PlayerService.this,"列表为空",Toast.LENGTH_SHORT).show();
            mper.reset();
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

    //播放上一曲。
    public void Previous(MediaPlayer mediaPlayer){
        mper.seekTo(0);
        isPrepared=false;
        everPlayed=true;
        int counter=DataSupport.count(MusicList.class);
        if(counter<=0){
            Toast.makeText(PlayerService.this,"列表为空",Toast.LENGTH_SHORT).show();
            mper.reset();
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

    //MediaPlayer遇到错误时调用。
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        isPrepared=false;
        Toast.makeText(PlayerService.this,"发生错误，停止播放"+"/"+i+"/"+i1,Toast.LENGTH_SHORT).show();
        return true;
    }

    //MediaPlayer准备好播放歌曲时调用。
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

    //设置播放的歌曲地址。
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
                musicName=musicSelected.getTitle();
                byte[] embedPic=mmr.getEmbeddedPicture();
                if(embedPic==null){
                    musicPic=null;
                }else{
                    musicPic= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                }
                //Toast.makeText(PlayerService.this,"Show Message"+uri.toString(),Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("PlayerService",e.toString());
            Toast.makeText(this,e.toString()+number,Toast.LENGTH_SHORT).show();
        }finally {
            mmr.release();
        }
    }

    //从系统获取NotificationManager实例。
    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    //获取Notification实例。
    private Notification getNotification(String title){
        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.logo));
        builder.setContentTitle("当前播放："+title);
        if(isPrepared){
            int current=mper.getCurrentPosition()/1000;
            int duration=mper.getDuration()/1000;
            builder.setContentText(String.format("%02d:%02d/%02d:%02d",current/60,current-60*(current/60),duration/60,duration-60*(duration/60)));
        }else{
            builder.setContentText(String.valueOf("00:00")+"/"+String.valueOf("00:00"));
        }
        builder.setProgress(mper.getDuration(),mper.getCurrentPosition(),false);
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        return builder.build();
    }

    //进入前台服务。
    public void startBack(){
        startForeground(1,getNotification("无正在播放歌曲"));
    }

    //退出前台服务。
    public void stopBack(){
        stopForeground(true);
    }

    //用于与服务绑定的活动控制服务。
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
                tos.setText("单曲循环");
                tos.show();
                //Toast.makeText(context,"单曲循环",Toast.LENGTH_SHORT).show();
            }else{
                tos.setText("列表循环");
                tos.show();
                //Toast.makeText(context,"列表循环",Toast.LENGTH_SHORT).show();
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
        public void setIsPaused(Boolean bb){
            PlayerService.this.isPause=bb;
            if(bb){
                startBack();
            }else{
                stopBack();
            }
        }
        public int getPlayingNumber(){
            return number;
        }
    }
}
