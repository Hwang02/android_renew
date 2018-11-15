package com.hotelnow.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class KeyWordAdapter extends RecyclerView.Adapter<KeyWordAdapter.MyViewHolder> {

    private ArrayList<KeyWordItem> data = new ArrayList<>();
    private Context mContext;

    public KeyWordAdapter(ArrayList<KeyWordItem> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_keyword_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_keyword.setText(data.get(position).getLink());
        Ion.with(holder.iv_image).load(data.get(position).getImage());

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
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
        }
    }
}
