package com.example.hasee.player02.Listener;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.example.hasee.player02.Fragments.WordView;
import com.example.hasee.player02.Fragments.interface_class;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hasee on 2017/12/21 0021.
 */

public interface PlayerListener_Service{
    void setProgress(int progress);
    void setTitle(String title);
    void setMusicPic(Bitmap bitmap);
    void setDuration(int Duration);
    void initBtnPlay();
    void setFocusedNumber(int Number);
    List<Integer> getTimeList();
}
