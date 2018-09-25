package com.hotelnow.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.hotelnow.R;
import com.hotelnow.fragment.model.SingleHorizontal;

import java.util.ArrayList;


public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    ArrayList<SingleHorizontal> data = new ArrayList<>();

    public HorizontalAdapter(ArrayList<SingleHorizontal> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recent_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.keyword.setText(data.get(position).getTitle());
        holder.image.setImageResource(data.get(position).getImages());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView keyword;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}
