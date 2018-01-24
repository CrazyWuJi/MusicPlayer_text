package com.example.hasee.player02.Listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hasee on 2018/1/24 0024.
 */

public class RecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener{
    GestureDetector mGestureDetector;
    private RecyclerView mRecyclerView;
    private View childView;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onLongClick(View view,int position);
    }

    public RecyclerViewItemClickListener(Context context, final OnItemClickListener mLisnter){
        mGestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
           @Override
            public boolean onSingleTapUp(MotionEvent ev){
               if(mLisnter!=null){
                   mLisnter.onItemClick(childView,233);
               }
               if(childView!=null&&mLisnter!=null){
                   //mLisnter.onItemClick(childView,mRecyclerView.getChildPosition(childView));
               }
               return true;
           }
           @Override
            public void onLongPress(MotionEvent ev){
                if(childView!=null&&mLisnter!=null){
                    mLisnter.onLongClick(childView,mRecyclerView.getChildPosition(childView));
                }
           }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        childView=rv.findChildViewUnder(e.getX(),e.getY());
        mRecyclerView=rv;
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
