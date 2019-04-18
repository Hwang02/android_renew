package com.hotelnow.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.model.ThemeItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class ThemeLeisureAdapter extends RecyclerView.Adapter<ThemeLeisureAdapter.MyViewHolder> {

    ArrayList<ThemeItem> data = new ArrayList<>();
    DbOpenHelper dbHelper;
    LeisureFragment hf;

    public ThemeLeisureAdapter(ArrayList<ThemeItem> data, LeisureFragment hf, DbOpenHelper dbHelper) {
        this.data = data;
        this.hf = hf;
        this.dbHelper = dbHelper;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_theme_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_hotelname.setText(data.get(position).getName());
        holder.tv_price.setText(Util.numberFormat(Integer.parseInt(data.get(position).getSale_price())));
        Ion.with(holder.iv_image).animateIn(R.anim.fadein).load(data.get(position).getLandscape());

        if (data.get(position).getTheme_flag().equals("H")) {
            if (dbHelper.selectAllFavoriteStayItem().size() > 0) {
                for (int i = 0; i < dbHelper.selectAllFavoriteStayItem().size(); i++) {
                    if (dbHelper.selectAllFavoriteStayItem().get(i).getSel_id().equals(data.get(position).getId())) {
                        holder.btn_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                        holder.islike = true;
                        break;
                    } else {
                        holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                        holder.islike = false;
                    }
                }
            } else {
                holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                holder.islike = false;
            }

            holder.btn_favorite.setTag(position);
            holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("ggggg", data.get((int) v.getTag()).getId() + "");
                    hf.setThemeLike((int) v.getTag(), holder.islike, ThemeLeisureAdapter.this);
                }
            });

            holder.sel_item.setTag(position);
            holder.sel_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TuneWrap.Event("stay_colortheme", data.get((int) v.getTag()).getTheme_id(), data.get((int) v.getTag()).getId());
                    LogUtil.e("vvvvvv", data.get((int) v.getTag()).getId() + "");
                    Intent intent = new Intent(hf.getActivity(), DetailHotelActivity.class);
                    intent.putExtra("hid", data.get((int) v.getTag()).getId() + "");
                    intent.putExtra("save", true);
                    hf.startActivityForResult(intent, 70);
                }
            });
        } else {
            if (dbHelper.selectAllFavoriteActivityItem().size() > 0) {
                for (int i = 0; i < dbHelper.selectAllFavoriteActivityItem().size(); i++) {
                    if (dbHelper.selectAllFavoriteActivityItem().get(i).getSel_id().equals(data.get(position).getId())) {
                        holder.btn_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                        holder.islike = true;
                        break;
                    } else {
                        holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                        holder.islike = false;
                    }
                }
            } else {
                holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                holder.islike = false;
            }

            holder.btn_favorite.setTag(position);
            holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("ggggg", data.get((int) v.getTag()).getId() + "");
                    hf.setThemeLike((int) v.getTag(), holder.islike, ThemeLeisureAdapter.this);
                }
            });

            holder.sel_item.setTag(position);
            holder.sel_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TuneWrap.Event("activity_colortheme", data.get((int) v.getTag()).getTheme_id(), data.get((int) v.getTag()).getId());
                    LogUtil.e("vvvvvv", data.get((int) v.getTag()).getId() + "");
                    Intent intent = new Intent(hf.getActivity(), DetailActivityActivity.class);
                    intent.putExtra("tid", data.get((int) v.getTag()).getId() + "");
                    intent.putExtra("save", true);
                    hf.startActivityForResult(intent, 70);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_hotelname, tv_price;
        RoundedImageView iv_image;
        ImageView btn_favorite;
        LinearLayout sel_item;
        boolean islike = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_hotelname = (TextView) itemView.findViewById(R.id.tv_hotelname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            btn_favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}
