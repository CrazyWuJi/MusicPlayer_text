package com.example.hasee.player02.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.player02.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/1/21 0021.
 */

public class musicLrcRecyclerViewAdapter extends RecyclerView.Adapter<musicLrcRecyclerViewAdapter.ViewHolder>{
    private List<String> list=new ArrayList<>();
    private List<lrcItemForRecyclerViewAdapter> lrcItemForRecyclerViewAdapters=new ArrayList<>();
    private Context context;
    public int po=-1;
    public int la=-2;

    public musicLrcRecyclerViewAdapter(List<String> list,Context context){
        this.list=list;
        this.context=context;
        for (int i=0;i<list.size();i++){
            lrcItemForRecyclerViewAdapter adapter=new lrcItemForRecyclerViewAdapter();
            adapter.setLrc(this.list.get(i));
            lrcItemForRecyclerViewAdapters.add(adapter);
        }
    }

    public void setFocusedNumber(int Number){
        if(la==-2){
            lrcItemForRecyclerViewAdapters.get(Number).setIsFocused(true);
            la=Number;
        }else if(Number!=la){
            lrcItemForRecyclerViewAdapters.get(la).setIsFocused(false);
            lrcItemForRecyclerViewAdapters.get(Number).setIsFocused(true);
            la=Number;
        }
    }

    public void setAdapeters(List<String> list){
        this.list=list;
        lrcItemForRecyclerViewAdapters.clear();
        for (int i=0;i<list.size();i++){
            lrcItemForRecyclerViewAdapter adapter=new lrcItemForRecyclerViewAdapter();
            adapter.setLrc(this.list.get(i));
            adapter.setIsFocused(false);
            lrcItemForRecyclerViewAdapters.add(adapter);
        }
    }

    @Override
    public musicLrcRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.lrcrecyclerviewlist,parent,false);
        final musicLrcRecyclerViewAdapter.ViewHolder viewHolder=new musicLrcRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String lrc=list.get(position);
        holder.textView.setText(lrc);
        if(lrcItemForRecyclerViewAdapters.get(position).getIsFocused()){
            holder.textView.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        }else {
            holder.textView.setTextColor(Color.rgb(115,115,115));
        }
        /*if(position!=po){
            holder.textView.setTextColor(Color.rgb(115,115,115));
            lrcItemForRecyclerViewAdapters.get(position).setIsFocused(false);
            po=position;
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            this.textView=(TextView)itemView.findViewById(R.id.lrc_itemView);
        }
    }
}
