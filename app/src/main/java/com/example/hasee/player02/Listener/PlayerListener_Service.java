package com.example.hasee.player02.Listener;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by hasee on 2017/12/21 0021.
 */

public interface PlayerListener_Service{
    void setProgress(int progress);
    void setTitle(String title);
    void setMusicPic(Bitmap bitmap);
    void setDuration(int Duration);
    void initBtnPlay();
}
