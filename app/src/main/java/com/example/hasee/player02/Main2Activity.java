package com.example.hasee.player02;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.player02.Adapter.musicAdapter;
import com.example.hasee.player02.Adapter.musicList_Item;
import com.example.hasee.player02.db.MusicList;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    ImageButton addMusic;
    Uri uri;
    ListView musicList;
    musicAdapter adapter;
    Toast tos;
    List<MusicList> musicLists=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        addMusic=(ImageButton)findViewById(R.id.addMusic);
        musicList=(ListView)findViewById(R.id.musicList);
        musicList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MusicList musicSelected=musicLists.get(i);
                String uri=musicSelected.getUri();
                DataSupport.deleteAll(MusicList.class,"uri=?",uri);
                showList();
                return true;
            }
        });
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent();
                intent.putExtra("SelectedLine",i);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        tos=Toast.makeText(this,"",Toast.LENGTH_SHORT);
        /*tos.setText("1 准备完成！");
        tos.show();*/
        showList();
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connector.getDatabase();
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,100);
            }
        });
    }

    protected void showList(){
        try {
            /*tos.setText("showList");
            tos.show();*/
            musicLists.clear();
            List<musicList_Item> musicList_items=new ArrayList<>();
            musicLists=DataSupport.findAll(MusicList.class);
            for(MusicList music:musicLists){
                musicList_Item musicList_item=new musicList_Item();
                musicList_item.setArtist(music.getArtist());
                musicList_item.setDuration(music.getDuration());
                musicList_item.setTitle(music.getTitle());
                musicList_item.setUri(music.getUri());
                musicList_items.add(musicList_item);
            }
            adapter=new musicAdapter(this,R.layout.music_item,musicList_items);
            musicList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            for(musicList_Item mu:musicList_items){
                uri=Uri.parse(mu.getUri());
                MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                mmr.setDataSource(this,uri);
                byte[] embedPic=mmr.getEmbeddedPicture();
                Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                mu.setBitmap(bitmap);
            }
            adapter=new musicAdapter(this,R.layout.music_item,musicList_items);
            musicList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            /*musicList_Item mu=musicList_items.get(0);
            uri=Uri.parse(mu.getUri());
            MediaMetadataRetriever mmr=new MediaMetadataRetriever();
            mmr.setDataSource(this,uri);
            byte[] embedPic=mmr.getEmbeddedPicture();
            Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
            musicPic.setImageBitmap(bitmap);*/
        }catch (Exception e){
            Log.d("Main2Activity",e.toString());
            tos.setText(e.toString());
            //tos.show();
        }
    }

    @Override
    protected void onActivityResult(int reqCode,int resCode,Intent intent){
        super.onActivityResult(reqCode,resCode,intent);
        if(resCode==RESULT_OK){
            uri=intent.getData();
            List<MusicList> findMusic=DataSupport.select("uri").where("uri=?",uri.toString()).find(MusicList.class);
            if(!findMusic.isEmpty()){
                tos.setText("歌曲已存在");
                tos.show();
            }else {
                MusicList musicSelcted=new MusicList();
                musicSelcted.setUri(uri.toString());
                MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                mmr.setDataSource(this,uri);
                String titleString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artistString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String durationString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                mmr.release();
                /*TextView txv=(TextView)findViewById(R.id.title_main);
                txv.setText(titleString);
                tos.setText(titleString);
                tos.show();*/
                musicSelcted.setTitle(titleString);
                musicSelcted.setArtist(artistString);
                musicSelcted.setDuration(durationString);
                musicSelcted.save();
                showList();
            }
        }
    }


}
