package com.example.hasee.player02.Listener;

/**
 * Created by hasee on 2017/12/21 0021.
 */

public interface PlayerListener {
    void onProgress(int progress);
    void onPause();
    void onComplete();
    void onFailed();
}
