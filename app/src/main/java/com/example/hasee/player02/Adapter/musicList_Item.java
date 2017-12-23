package com.example.hasee.player02.Adapter;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2017/12/19 0019.
 */

public class musicList_Item {
    private String artist;
    private String title;
    private String duration;
    private String uri;
    private Bitmap bitmap;
    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setArtist(String artist){
        this.artist=artist;
    }
    public String getArtist(){
        return artist;
    }
    public void setDuration(String duration){
        this.duration=duration;
    }
    public String getDuration(){
        return duration;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setUri(String uri){
        this.uri=uri;
    }
    public String getUri(){
        return uri;
    }
}
