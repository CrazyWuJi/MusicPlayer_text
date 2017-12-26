package com.example.hasee.player02;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
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

import com.example.hasee.player02.Fragments.MusicLyricFragment;
import com.example.hasee.player02.Fragments.MusicPicFragment;
import com.example.hasee.player02.Listener.PlayerListener_Service;
import com.example.hasee.player02.db.MusicList;
import com.example.hasee.player02.service.PlayerService;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    int duration;
    ImageButton showList;
    TextView Title_Main,musicProgress_text;
    Boolean isMusicPicFragment=true,isSeekBarChanging=false;
    FrameLayout frameLayout;
    ImageView musicPic;
    SeekBar musicProgress_seekbar;
    MusicPicFragment musicPicFragment;
    MusicLyricFragment musicLyricFragment;
    PlayerService.PlayBinder playerBinder;
    Integer SelectedNumber;
    ImageButton btnStart,btnNext,btnPre;
    CheckBox Looping;
    public PlayerListener_Service playerListener_service=new PlayerListener_Service() {
        @Override
        public void setProgress(int progress) {
            if(!isSeekBarChanging){
                musicProgress_seekbar.setProgress(progress);
            }
            musicProgress_text.setText(String.valueOf(progress/1000)+"/"+String.valueOf(duration/1000));
        }
        @Override
        public void setTitle(String title) {
            Title_Main.setText(String.valueOf(title));
        }

        @Override
        public void setMusicPic(Bitmap bitmap) {

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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        musicPic=(ImageView)findViewById(R.id.musicPic);
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
                playerBinder.startPlay();
                if(playerBinder.isPlaying()){
                    btnStart.setImageResource(android.R.drawable.ic_media_pause);
                }else{
                    btnStart.setImageResource(android.R.drawable.ic_media_play);
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
        Intent startService=new Intent(this,PlayerService.class);
        startService(startService);
        bindService(startService,connection,BIND_AUTO_CREATE);
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.Main_Fragment,musicPicFragment);
        transaction.commit();
        isMusicPicFragment=true;
    }

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
                        Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                        musicPicFragment.changeMuiscpic(bitmap);
                        String titleString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        Title_Main.setText(titleString);
                        playerBinder.setDataNumber(SelectedNumber,MainActivity.this);
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

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder=(PlayerService.PlayBinder)iBinder;
            playerBinder.setListener_service(playerListener_service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekBarChanging=true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekBarChanging=false;
        playerBinder.setProgress(seekBar.getProgress());
    }
}
