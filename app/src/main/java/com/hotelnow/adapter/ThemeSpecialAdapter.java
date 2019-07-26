package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.model.ThemeSpecialItem;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class ThemeSpecialAdapter extends RecyclerView.Adapter<ThemeSpecialAdapter.MyViewHolder> {
    private ArrayList<ThemeSpecialItem> data = new ArrayList<>();
    private Context mContext;
    private HomeFragment mHf;

    public ThemeSpecialAdapter(ArrayList<ThemeSpecialItem> data, HomeFragment mHf) {
        this.data = data;
        this.mHf = mHf;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_theme_special_item, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ion.with(holder.iv_image).animateIn(R.anim.fadein).load(data.get(position).getImg_main_list());
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_message.setText(data.get(position).getSub_title());
        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get((int) v.getTag()).getTheme_flag().equals("H")) {
                    Intent intent = new Intent(mContext, ThemeSpecialHotelActivity.class);
                    intent.putExtra("tid", data.get((int) v.getTag()).getId());
                    mHf.startActivityForResult(intent, 80);
                } else if(data.get((int) v.getTag()).getTheme_flag().equals("Q")){
                    Intent intent = new Intent(mContext, ThemeSpecialActivityActivity.class);
                    intent.putExtra("tid", data.get((int) v.getTag()).getId());
                    mHf.startActivityForResult(intent, 80);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title, tv_message;
        LinearLayout sel_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}
