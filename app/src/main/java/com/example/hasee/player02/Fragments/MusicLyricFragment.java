package com.example.hasee.player02.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hasee.player02.R;

/**
 * Created by hasee on 2017/12/20 0020.
 */

public class MusicLyricFragment extends Fragment{
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.musiclyric_layout,container,false);
        this.view=view;
        return view;
    }

    public void setlyric(String last,String current,String next){
        TextView lastLyric=(TextView)view.findViewById(R.id.lyric_last);
        TextView currentLyric=(TextView)view.findViewById(R.id.lyric_current);
        TextView nextLyric=(TextView)view.findViewById(R.id.lyric_next);
        lastLyric.setText(last);
        currentLyric.setText(current);
        nextLyric.setText(next);
    }
}
