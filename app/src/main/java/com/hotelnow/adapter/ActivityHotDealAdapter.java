package com.hotelnow.adapter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class ActivityHotDealAdapter extends RecyclerView.Adapter<ActivityHotDealAdapter.MyViewHolder> {

    private ArrayList<ActivityHotDealItem> data = new ArrayList<>();
    private HomeFragment hf;
    private DbOpenHelper dbHelper;

    public ActivityHotDealAdapter(ArrayList<ActivityHotDealItem> data, HomeFragment hf, DbOpenHelper dbHelper) {
        this.data = data;
        this.hf = hf;
        this.dbHelper = dbHelper;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_activity_hotdeal_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_catagory.setText(data.get(position).getCategory());
        holder.tv_score.setText(data.get(position).getGrade_score());
        holder.tv_hotelname.setText(data.get(position).getName());
        holder.tv_price.setText(Util.numberFormat(Integer.parseInt(data.get(position).getSale_price())));
        Ion.with(holder.iv_image).load(data.get(position).getImg_url());

        if(data.get(position).getIs_hot_deal().equals("Y")){
            holder.soon_discount.setVisibility(View.VISIBLE);
        }
        else{
            holder.soon_discount.setVisibility(View.GONE);
        }
        if(data.get(position).getIs_add_reserve().equals("Y")){
            holder.soon_point.setVisibility(View.VISIBLE);
        }
        else{
            holder.soon_point.setVisibility(View.GONE);
        }

        for(int i = 0; i < dbHelper.selectAllFavoriteActivityItem().size(); i++) {
            if (dbHelper.selectAllFavoriteActivityItem().get(i).getSel_id().equals(data.get(position).getId())) {
                holder.btn_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                holder.islike = true;
                break;
            } else {
                holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                holder.islike = false;
            }
        }

        holder.btn_favorite.setTag(position);
        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", data.get((int)v.getTag()).getId()+"");
                hf.setActivityLike((int)v.getTag(), holder.islike, ActivityHotDealAdapter.this);
            }
        });

        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("vvvvvv", data.get((int)v.getTag()).getId()+"");
                Intent intent = new Intent(hf.getActivity(), DetailActivityActivity.class);
                intent.putExtra("tid", data.get((int)v.getTag()).getId()+"");
                hf.startActivityForResult(intent,80);
                dbHelper.insertRecentItem(data.get((int)v.getTag()).getId(), "A");
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_catagory, tv_score, tv_hotelname, tv_price;
        ImageView soon_discount, soon_point, btn_favorite;
        LinearLayout sel_item;
        RoundedImageView iv_image;
        boolean islike = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_catagory = (TextView) itemView.findViewById(R.id.tv_catagory);
            tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            tv_hotelname = (TextView) itemView.findViewById(R.id.tv_hotelname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            soon_discount = (ImageView) itemView.findViewById(R.id.soon_discount);
            soon_point = (ImageView) itemView.findViewById(R.id.soon_point);
            btn_favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}
