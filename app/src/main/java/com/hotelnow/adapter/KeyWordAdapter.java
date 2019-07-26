package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.SearchResultActivity;
import com.hotelnow.model.KeyWordItem;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_keyword.setText(data.get(position).getKeyword());
        Ion.with(holder.iv_image).animateIn(R.anim.fadein).load(data.get(position).getImage());
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchResultActivity.class);
                intent.putExtra("banner_id", data.get(position).getId());
                intent.putExtra("banner_name", data.get(position).getLink());
                intent.putExtra("page", "key");
                ((MainActivity) mContext).startActivityForResult(intent, 80);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_keyword;
        RoundedImageView iv_image;
        LinearLayout sel_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}
