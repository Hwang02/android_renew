package com.hotelnow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class ThemeSpecialAdapter extends RecyclerView.Adapter<ThemeSpecialAdapter.MyViewHolder> {
    private ArrayList<ThemeSpecialItem> data = new ArrayList<>();

    public ThemeSpecialAdapter(ArrayList<ThemeSpecialItem> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_theme_special_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ion.with(holder.iv_image).load(data.get(position).getImg_background());
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_message.setText(data.get(position).getSub_title());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title, tv_message;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
