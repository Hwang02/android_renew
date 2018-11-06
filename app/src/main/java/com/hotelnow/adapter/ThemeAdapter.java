package com.hotelnow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    ArrayList<ThemeItem> data = new ArrayList<>();

    public ThemeAdapter(ArrayList<ThemeItem> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_theme_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

//        holder.tv_catagory.setText(data.get(position).getCategory());
//        holder.tv_score.setText(data.get(position).getGrade_score());
        holder.tv_hotelname.setText(data.get(position).getName());
        holder.tv_price.setText(Util.numberFormat(Integer.parseInt(data.get(position).getSale_price())));
        Ion.with(holder.iv_image).load(data.get(position).getLandscape());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_catagory, tv_score, tv_hotelname, tv_price;
        ImageView iv_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_hotelname = (TextView) itemView.findViewById(R.id.tv_hotelname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}
