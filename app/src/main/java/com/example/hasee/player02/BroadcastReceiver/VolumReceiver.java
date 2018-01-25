package com.example.hasee.player02.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

/**
 * Created by hasee on 2018/1/25 0025.
 */

public class VolumReceiver extends BroadcastReceiver {
    Receiver mListener;

    public VolumReceiver(Receiver mListener){
        this.mListener=mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener!=null){
            mListener.onReceive(context,intent);
        }
    }

    public interface Receiver{
        void onReceive(Context context,Intent intent);
    }
}
