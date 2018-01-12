package com.example.hasee.player02.PopupWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.hasee.player02.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/1/11 0011.
 */

public class moreMusicinfo extends PopupWindow{
    private Context context;
    private View view;
    private ListView listView;
    private List<String> list;

    public ListView getListView(){
        return listView;
    }

    public moreMusicinfo(Context context){
        this(context,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public moreMusicinfo(Context context,int width,int height){
        this.context=context;
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        view= LayoutInflater.from(context).inflate(R.layout.moreinfo_popwindow,null);
        setContentView(view);
        initData();
    }

    private void initData(){
        listView=(ListView)view.findViewById(R.id.moreInfo_list);
        list=new ArrayList<String>();
        list.add("详细信息");
        list.add("删除");
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int i) {
                return list.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView=null;
                if(view==null){
                    textView=new TextView(context);
                    //textView.setTextColor(Color.rgb(255,255,255));
                    textView.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
                    textView.setBackground(context.getResources().getDrawable(R.drawable.item_selector));
                    textView.setTextSize(14);
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(0,13,0,13);
                    textView.setSingleLine(true);
                }else{
                    textView=(TextView)view;
                }
                textView.setText(list.get(i));
                return textView;
            }
        });
    }
}
