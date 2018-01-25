package com.example.hasee.player02.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.player02.R;

/**
 * Created by hasee on 2017/12/20 0020.
 */

public class MusicLyricFragment extends Fragment{
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        /*View view=inflater.inflate(R.layout.musiclyric_layout,container,false);
        this.view=view;
        return view;*/
        View view=inflater.inflate(R.layout.recyclerviewforlrc,container,false);
        this.view=view;
        return view;
    }

    public SeekBar getVolumSeekBar(){
        return (SeekBar)view.findViewById(R.id.VolumSeekBar);
    }

    public RecyclerView getRecyclerView(){
        return (RecyclerView)view.findViewById(R.id.lrc_RecyclerView);
    }

    //返回歌词组件实例用于更新歌词List<String>。
    /*public WordView getview(){
        return (WordView)view.findViewById(R.id.text);
    }*/

    //测试BUG时用。
    /*public void show(Context context){
        Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
    }*/

}
