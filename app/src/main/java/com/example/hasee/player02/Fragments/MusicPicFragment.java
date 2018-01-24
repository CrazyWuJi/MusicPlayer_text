package com.example.hasee.player02.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hasee.player02.R;

/**
 * Created by hasee on 2017/12/20 0020.
 */

public class MusicPicFragment extends Fragment{
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.musicpic_layout,container,false);
        this.view=view;
        return view;
    }

    public void setPicClickListener(Context context, final OnClickListener mListener){
        if(view!=null&&mListener!=null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick();
                }
            });
        }
    }

    public interface OnClickListener{
        void onClick();
    }

    public void changeMuiscpic(Bitmap bitmap){
        ImageView imageView=(ImageView)view.findViewById(R.id.MusicPicView);
        imageView.setImageBitmap(bitmap);
    }

    public void changesMusicPicByDrawable(Drawable drawable){
        ImageView imageView=(ImageView)view.findViewById(R.id.MusicPicView);
        imageView.setImageDrawable(drawable);
    }
}
