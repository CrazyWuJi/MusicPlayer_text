package com.example.hasee.player02.Adapter;

/**
 * Created by hasee on 2018/1/21 0021.
 */

public class lrcItemForRecyclerViewAdapter {
    private String lrc;
    private Boolean isFocused=false;
    public void setLrc(String lrc){
        this.lrc=lrc;
    }
    public String getLrc(){
        return lrc;
    }
    public void setIsFocused(Boolean isFocused){
        this.isFocused=isFocused;
    }
    public Boolean getIsFocused(){
        return isFocused;
    }
}
