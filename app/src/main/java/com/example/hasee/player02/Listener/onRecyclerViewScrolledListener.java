package com.example.hasee.player02.Listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by hasee on 2018/1/24 0024.
 */

public class onRecyclerViewScrolledListener extends RecyclerView.OnScrollListener{
    onScrolledListener mListener;

    public onRecyclerViewScrolledListener(onScrolledListener mListener){
        super();
        this.mListener=mListener;
    }
    public interface onScrolledListener{
        void onScrolled(int state);
        void move(int dx,int dy);
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView,int newState){
        if(mListener!=null){
            mListener.onScrolled(newState);
        }
    }
    @Override
    public void onScrolled(RecyclerView recyclerView,int dx,int dy){
        super.onScrolled(recyclerView,dx,dy);
        mListener.move(dx,dy);
    }
}
