package com.example.hasee.player02.db;

import android.net.Uri;

import org.litepal.crud.DataSupport;

/**
 * Created by hasee on 2017/12/18 0018.
 */

public class MusicList extends DataSupport{
    private int id;
    private String artist;
    private String title;
    private String duration;
    private String uri;
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
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
