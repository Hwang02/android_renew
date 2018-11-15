package com.hotelnow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.RecentListItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder> {

    ArrayList<RecentListItem> data = new ArrayList<>();

    public RecentAdapter(ArrayList<RecentListItem> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recent_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_keyword.setText(data.get(position).getName());
        Ion.with(holder.iv_image).load(data.get(position).getImg_url());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_keyword;
        RoundedImageView iv_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}
