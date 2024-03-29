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
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.model.RecentListItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder> {

    ArrayList<RecentListItem> data = new ArrayList<>();
    HomeFragment hf;
    DbOpenHelper dbHelper;
    TextView more;

    public RecentAdapter(ArrayList<RecentListItem> data, HomeFragment hf, DbOpenHelper dbHelper, TextView more) {
        this.data = data;
        this.hf = hf;
        this.dbHelper = dbHelper;
        this.more = more;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recent_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_keyword.setText(data.get(position).getName());

        if (data.size() > 1) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.GONE);
        }

        if (data.get(position).getFlag().equals("1")) { // 호텔
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
            holder.iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
            holder.iv_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        Ion.with(holder.iv_image).animateIn(R.anim.fadein).load(data.get(position).getImg_url());

        holder.btn_favorite.setTag(position);
        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", data.get((int) v.getTag()).getId() + "");
                hf.setRecentLike((int) v.getTag(), holder.islike, RecentAdapter.this);
            }
        });
        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LogUtil.e("ggggg", data.get((int) v.getTag()).getId() + "");
                if (data.get((int) v.getTag()).getFlag().equals("1")) { // 호텔
                    Intent intent = new Intent(hf.getActivity(), DetailHotelActivity.class);
                    intent.putExtra("hid", data.get((int) v.getTag()).getId());
                    intent.putExtra("save", true);
                    hf.startActivityForResult(intent, 80);
                } else { // 액티비티
                    Intent intent = new Intent(hf.getActivity(), DetailActivityActivity.class);
                    intent.putExtra("tid", data.get((int) v.getTag()).getId());
                    intent.putExtra("save", true);
                    hf.startActivityForResult(intent, 80);
                }
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
        ImageView btn_favorite;
        LinearLayout sel_item;
        boolean islike = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            btn_favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}
