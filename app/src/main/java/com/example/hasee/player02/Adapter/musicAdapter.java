package com.example.hasee.player02.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hasee.player02.Main2Activity;
import com.example.hasee.player02.R;
import com.example.hasee.player02.db.MusicList;

import java.util.List;

/**
 * Created by hasee on 2017/12/19 0019.
 */

public class musicAdapter extends ArrayAdapter<musicList_Item>{

    private int resourceId;
    //private Uri uri;
    //private MediaMetadataRetriever mmr;

    public musicAdapter(Context context, int textViewResourceId, List<musicList_Item> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        musicList_Item musiclist=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.musicTitle=(TextView)view.findViewById(R.id.musicTitle);
            viewHolder.musicArtist=(TextView)view.findViewById(R.id.singerAndduration);
            viewHolder.musicPic=(ImageView)view.findViewById(R.id.musicPic);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.musicTitle.setText(musiclist.getTitle());
        viewHolder.musicArtist.setText(musiclist.getArtist());
        viewHolder.musicPic.setImageBitmap(musiclist.getBitmap());
        return view;
    }

    class ViewHolder{
        TextView musicTitle,musicArtist;
        ImageView musicPic;
    }
}
