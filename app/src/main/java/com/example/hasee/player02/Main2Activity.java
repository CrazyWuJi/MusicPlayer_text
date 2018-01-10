package com.example.hasee.player02;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.player02.Adapter.musicList_Item;
import com.example.hasee.player02.db.MusicList;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity {

    Toast tos;
    public Uri uri;
    public List<MusicList> musicLists=new ArrayList<>();
    public List<musicList_Item> musicList_items=new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    musicRecycleAdapter adapter;
    private Handler mHandler;

    //获取组件实例。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("音乐列表");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView=(RecyclerView)findViewById(R.id.musicList);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter=new musicRecycleAdapter(musicList_items);
        showList();
        tos=Toast.makeText(this,"",Toast.LENGTH_SHORT);
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        updataListView();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add:
                Connector.getDatabase();
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,100);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void DeletMusicList(int ii){
        final int po=ii;
        AlertDialog.Builder dialog=new AlertDialog.Builder(Main2Activity.this);
        dialog.setTitle("警告");
        dialog.setMessage("确定要删除 "+musicLists.get(po).getTitle()+" 吗？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MusicList musicSelected=musicLists.get(po);
                String uri=musicSelected.getUri();
                DataSupport.deleteAll(MusicList.class,"uri=?",uri);
                //Toast.makeText(Main2Activity.this,String.valueOf(DataSupport.count(MusicList.class))+"  "+String.valueOf(po),Toast.LENGTH_SHORT).show();
                musicLists.remove(po);
                musicList_items.remove(po);
                adapter.notifyItemRemoved(po);
                //showList();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public void addMusiclist(MusicList e,musicList_Item o,int po){
        musicLists.add(e);
        musicList_items.add(o);
        adapter.notifyItemInserted(po);
    }

    //显示歌曲列表。
    protected void showList(){
        try {
            musicLists.clear();
            musicList_items.clear();
            musicLists=DataSupport.findAll(MusicList.class);
            for(MusicList music:musicLists){
                musicList_Item musicList_item=new musicList_Item();
                musicList_item.setArtist(music.getArtist());
                musicList_item.setDuration(music.getDuration());
                musicList_item.setTitle(music.getTitle());
                musicList_item.setUri(music.getUri());
                musicList_items.add(musicList_item);
            }
            updataListView();

            /*for(musicList_Item mu:musicList_items){
                uri=Uri.parse(mu.getUri());
                MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                mmr.setDataSource(this,uri);
                byte[] embedPic=mmr.getEmbeddedPicture();
                Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                mu.setBitmap(bitmap);
                mmr.release();
            }
            updataListView();*/

            new Thread(new Runnable() {
                @Override
                public void run() {
                    updataMusicPic();
                    mHandler.sendEmptyMessage(0);
                }
            }).start();
        }catch (Exception e){
            Log.d("Main2Activity",e.toString());
            tos.setText(e.toString());
            tos.show();
        }
    }

    //更新歌曲列表。
    public void updataListView(){
        recyclerView.setAdapter(adapter);
    }

    //更新歌曲专辑图。
    public void updataMusicPic(){
        try {
            for(musicList_Item mu:musicList_items){
                uri=Uri.parse(mu.getUri());
                if(mu.getTitle()!=null){
                    MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                    mmr.setDataSource(Main2Activity.this,uri);
                    byte[] embedPic=mmr.getEmbeddedPicture();
                    Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);
                    mu.setBitmap(bitmap);
                    mmr.release();
                }else{
                    mu.setTitle(String.valueOf(uri.getLastPathSegment()));
                }
            }
        }catch (Exception e){
            Log.d("Main2Activity",e.toString());
            Toast.makeText(Main2Activity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    //回调方法获取要添加的歌曲路径。
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
                try {
                    MusicList musicSelcted=new MusicList();
                    musicList_Item musicList_item=new musicList_Item();
                    musicSelcted.setUri(uri.toString());
                    MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                    mmr.setDataSource(this,uri);
                    String titleString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artistString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String durationString=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    byte[] embedPic=mmr.getEmbeddedPicture();
                    Bitmap bitmap= BitmapFactory.decodeByteArray(embedPic,0,embedPic.length);


                    musicSelcted.setTitle(titleString);
                    musicSelcted.setArtist(artistString);
                    musicSelcted.setDuration(durationString);
                    musicList_item.setTitle(titleString);
                    musicList_item.setArtist(artistString);
                    musicList_item.setDuration(durationString);
                    musicList_item.setUri(uri.toString());
                    musicList_item.setBitmap(bitmap);
                    addMusiclist(musicSelcted,musicList_item,musicList_items.size());
                    musicSelcted.save();
                    //showList();
                }catch (Exception e){
                    Log.d("Main2Activity",e.toString());
                    Toast.makeText(Main2Activity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void backTomain(int ii){
        Intent intent=new Intent();
        intent.putExtra("SelectedLine",ii);
        setResult(RESULT_OK,intent);
        finish();
    }


    class musicRecycleAdapter extends RecyclerView.Adapter<musicRecycleAdapter.ViewHolder>{

        private List<musicList_Item> musicList;

        class ViewHolder extends RecyclerView.ViewHolder{
            View musicListview;
            CircleImageView circleMusicPic;
            TextView musicTitle,musicArtist;

            public ViewHolder(View itemView) {
                super(itemView);
                musicListview=itemView;
                circleMusicPic=(CircleImageView)itemView.findViewById(R.id.circleMusicpic);
                musicTitle=(TextView)itemView.findViewById(R.id.musicTitle);
                musicArtist=(TextView)itemView.findViewById(R.id.singerAndduration);
            }
        }

        public musicRecycleAdapter(List<musicList_Item> list){
            musicList=list;
        }

        @Override
        public musicRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
            final musicRecycleAdapter.ViewHolder holder=new musicRecycleAdapter.ViewHolder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Main2Activity.this.DeletMusicList(holder.getAdapterPosition());
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Main2Activity.this.backTomain(holder.getAdapterPosition());
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(musicRecycleAdapter.ViewHolder holder, int position) {
            musicList_Item item=musicList.get(position);
            holder.circleMusicPic.setImageBitmap(item.getBitmap());
            holder.musicTitle.setText(item.getTitle());
            int dura=Integer.parseInt(item.getDuration())/1000;
            holder.musicArtist.setText(item.getArtist()+"  ·  "+String.format("%02d:%02d",dura/60,dura-60*(dura/60)));
        }

        @Override
        public int getItemCount() {
            return musicList.size();
        }

    }
}
