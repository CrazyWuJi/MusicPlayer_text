package com.example.hasee.player02;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.player02.Fragments.LrcHandle;
import com.example.hasee.player02.Fragments.MusicLyricFragment;
import com.example.hasee.player02.Fragments.MusicPicFragment;
import com.example.hasee.player02.Fragments.WordView;
import com.example.hasee.player02.Fragments.interface_class;
import com.example.hasee.player02.Listener.PlayerListener_Service;
import com.example.hasee.player02.db.MusicList;
import com.example.hasee.player02.service.PlayerService;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    int duration;
    ImageButton showList;
    TextView Title_Main,musicProgress_text;
    Boolean isMusicPicFragment=true,isSeekBarChanging=false,isplayed=false,isBind=false;
    FrameLayout frameLayout;
    ImageView musicPic;
    SeekBar musicProgress_seekbar;
    MusicPicFragment musicPicFragment;
    MusicLyricFragment musicLyricFragment;
    PlayerService.PlayBinder playerBinder;
    Integer SelectedNumber;
    ImageButton btnStart,btnNext,btnPre;
    CheckBox Looping;
    LrcHandle lrcHandle;
    WordView mWordView;

    //首次启动活动时调用，用于获取各个组件实例与开启后台服务。
    @SuppressLint("SdCardPath")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        musicPicFragment=new MusicPicFragment();
        musicLyricFragment=new MusicLyricFragment();
        Title_Main=(TextView)findViewById(R.id.title_main);
        musicProgress_seekbar=(SeekBar)findViewById(R.id.musicProgress_seekbar);
        musicProgress_seekbar.setOnSeekBarChangeListener(this);
        musicProgress_text=(TextView)findViewById(R.id.musicProgress_text);
        showList=(ImageButton)findViewById(R.id.showList);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivityForResult(intent,100);
            }
        });
        //musicPic=(ImageView)findViewById(R.id.musicPic);
        frameLayout=(FrameLayout)findViewById(R.id.Main_Fragment);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMusicPicFragment==true){
                    switchFragment(musicPicFragment,musicLyricFragment);
                    isMusicPicFragment=false;
                }else if(isMusicPicFragment==false){
                    switchFragment(musicLyricFragment,musicPicFragment);
                    //musicPicFragment.changeMuiscpic(bitmap);
                    //Toast.makeText(MainActivity.this,String.valueOf(bitmap.getByteCount()),Toast.LENGTH_SHORT).show();
                    isMusicPicFragment=true;
                }
            }
        });
        btnStart=(ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isplayed){
                    playerBinder.startPlay();
                    if(playerBinder.isPlaying()){
                        btnStart.setImageResource(android.R.drawable.ic_media_pause);
                    }else{
                        btnStart.setImageResource(android.R.drawable.ic_media_play);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"请选择歌曲",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext=(ImageButton)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isplayed){
                    playerBinder.nextOne();
                }else{
                    Toast.makeText(MainActivity.this,"请选择歌曲",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPre=(ImageButton)findViewById(R.id.btnPre);
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isplayed){
                    playerBinder.previousOne();
                }else{
                    Toast.makeText(MainActivity.this,"请选择歌曲",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Looping=(CheckBox)findViewById(R.id.Looping);
        Looping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                playerBinder.setLooping(b,MainActivity.this);
            }
        });
        initLrcHandle("NULL");
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.Main_Fragment,musicLyricFragment);
        //transaction.commit();
        transaction.add(R.id.Main_Fragment,musicPicFragment);
        transaction.commit();
        isMusicPicFragment=true;
        Intent startService=new Intent(this,PlayerService.class);
        startService(startService);
        bindService(startService,connection,BIND_AUTO_CREATE);
    }

    //回调函数，用于获取歌曲列表返回的被选中的歌曲并播放，寻找歌词。
    @Override
    protected void onActivityResult(int reqCode,int resCode,Intent intent){
        switch (reqCode){
            case 100:
                if(resCode==RESULT_OK){
                    btnStart.setImageResource(android.R.drawable.ic_media_play);
                    SelectedNumber=intent.getIntExtra("SelectedLine",0);
                    //Toast.makeText(MainActivity.this,String.valueOf(SelectedNumber),Toast.LENGTH_SHORT).show();
                    List<MusicList> musicLists= DataSupport.findAll(MusicList.class);
                    MusicList selectedMusic=musicLists.get(SelectedNumber);
                    try{
                        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                        mmr.setDataSource(MainActivity.this, Uri.parse(selectedMusic.getUri()));
                        byte[] embedPic=mmr.getEmbeddedPicture();
                        if(embedPic!=null){
                            Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                            musicPicFragment.changeMuiscpic(bitmap);
                        }else{
                            musicPicFragment.changesMusicPicByDrawable(this.getResources().getDrawable(R.mipmap.logo));
                        }
                        String titleString=selectedMusic.getTitle();
                        Title_Main.setText(titleString);
                        playerBinder.setDataNumber(SelectedNumber,MainActivity.this);
                        //initLrcHanle_forall(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        mmr.release();
                    }catch (Exception e){
                        Log.d("MainActivity",e.toString());
                        //Toast.makeText(MainActivity.this,e.toString()+"哦哦哦",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    //绑定与解绑服务时调用，获取服务的Bind实例。
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder=(PlayerService.PlayBinder)iBinder;
            playerBinder.setListener_service(playerListener_service);
            mWordView=musicLyricFragment.getview();
            mWordView.upDataLrc(lrcHandle.getWords());
            SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
            int number=sharedPreferences.getInt("number",-1);
            int progress=sharedPreferences.getInt("progress",-1);
            //Toast.makeText(MainActivity.this,String.valueOf(progress)+"  "+String.valueOf(number),Toast.LENGTH_SHORT).show();
            if(number!=-1){
                playerBinder.setDataNumber(number,MainActivity.this);
                if(progress!=-1){
                    playerBinder.setNewPregress(progress);
                }
            }
            isBind=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBind=false;
        }
    };


    //切换歌曲专辑图碎片与歌词显示碎片。
    private void switchFragment(android.support.v4.app.Fragment from, android.support.v4.app.Fragment to){
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        if(!to.isAdded()){
            transaction.hide(from).add(R.id.Main_Fragment,to).commit();
        }else{
            transaction.hide(from).show(to).commit();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    //当SeekBar被拖动时调用，此时停止后台服务对SeekBar的调用。
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekBarChanging=true;
    }

    //SeekBar被释放时调用，获取释放后的位置并修改播放时间。
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekBarChanging=false;
        playerBinder.setProgress(seekBar.getProgress());
    }

    //由onCreate方法调用，初始化歌词界面使用。
    public void initLrcHandle(String title){
        lrcHandle=new LrcHandle();
        lrcHandle.readLRC(Environment.getExternalStorageDirectory()+"/Download/"+title+".lrc");
        //Toast.makeText(MainActivity.this,"Success!"+String.valueOf(lrcHandle.getWords().size()),Toast.LENGTH_SHORT).show();
    }

    //获取歌词方法。
    public void initLrcHanle_forall(String title){
        lrcHandle.readLRC(Environment.getExternalStorageDirectory()+"/Download/"+title+".lrc");
        mWordView.upDataLrc(lrcHandle.getWords());
        playerBinder.setmTimeList(lrcHandle.getTime());
        //Toast.makeText(this,String.valueOf(lrcHandle.getWords().size())+"+"+String.valueOf(lrcHandle.getTime().size()),Toast.LENGTH_SHORT).show();
    }

    //用于后台服务改变主界面。
    public PlayerListener_Service playerListener_service=new PlayerListener_Service() {
        @Override
        public void setProgress(int progress) {
            if(!isSeekBarChanging){
                musicProgress_seekbar.setProgress(progress);
            }
            int temppro=progress/1000;
            int tempdur=duration/1000;
            musicProgress_text.setText(String.format("%02d:%02d/%02d:%02d",temppro/60,temppro-60*(temppro/60),tempdur/60,tempdur-60*(tempdur/60)));
        }
        @Override
        public void setTitle(String title) {
            isplayed=true;
            Title_Main.setText(String.valueOf(title));
            initLrcHanle_forall(title);
        }

        @Override
        public void setMusicPic(Bitmap bitmap) {
            if(bitmap==null){
                musicPicFragment.changesMusicPicByDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.logo));
            }else{
                musicPicFragment.changeMuiscpic(bitmap);
            }
        }

        @Override
        public void setDuration(int Duration) {
            duration=Duration;
            //musicProgress_text.setText(String.valueOf(Duration));
            musicProgress_seekbar.setMax(Duration);
        }
        @Override
        public void initBtnPlay(){
            if(playerBinder.isPlaying()){
                btnStart.setImageResource(android.R.drawable.ic_media_pause);
            }else{
                btnStart.setImageResource(android.R.drawable.ic_media_play);
            }
        }

        @Override
        public List<Integer> getTimeList() {
            return lrcHandle.getTime();
        }

        @Override
        public WordView getWordView() {
            return musicLyricFragment.getview();
        }
    };

    //退出时保存当前播放歌曲与位置。
    @Override
    protected void onDestroy(){
        if(playerBinder.isEverPlayed()){
            SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putInt("progress",playerBinder.getPregress());
            editor.putInt("number",playerBinder.getPlayingNumber());
            editor.apply();
            playerBinder.stop();
        }
        unbindService(connection);
        super.onDestroy();
    }

    //转后台时进入前台服务。
    @Override
    protected void onPause(){
        super.onPause();
        if(isBind){
            playerBinder.setIsPaused(true);
        }
    }

    //转前台时取消前台服务。
    @Override
    protected void onResume(){
        super.onResume();
        if(isBind){
            playerBinder.setIsPaused(false);
        }
    }

    //用于Android6.0以上系统获取运行时权限。
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(MainActivity.this,"程序运行需要此权限",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
