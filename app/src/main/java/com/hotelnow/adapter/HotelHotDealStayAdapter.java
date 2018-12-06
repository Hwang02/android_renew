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
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class HotelHotDealStayAdapter extends RecyclerView.Adapter<HotelHotDealStayAdapter.MyViewHolder> {

    ArrayList<StayHotDealItem> data = new ArrayList<>();
    HotelFragment hf;
    DbOpenHelper dbHelper;

    public HotelHotDealStayAdapter(ArrayList<StayHotDealItem> data, HotelFragment hf, DbOpenHelper dbHelper) {
        this.data = data;
        this.hf = hf;
        this.dbHelper = dbHelper;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hotel_hotdeal_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_catagory.setText(data.get(position).getCategory());
        holder.tv_score.setText(data.get(position).getGrade_score());
        holder.tv_hotelname.setText(data.get(position).getName());
        holder.tv_price.setText(Util.numberFormat(Integer.parseInt(data.get(position).getSale_price())));
        Ion.with(holder.iv_image).load(data.get(position).getLandscape());

        if(data.get(position).getCoupon_count()>0){
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
        if(dbHelper.selectAllFavoriteStayItem().size()>0) {
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
        }
        else {
            holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
            holder.islike = false;
        }
        holder.tv_per.setText(data.get(position).getSale_rate()+"%↓");

        holder.btn_favorite.setTag(position);
        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", data.get((int)v.getTag()).getId()+"");
                hf.setStayLike((int)v.getTag(), holder.islike, HotelHotDealStayAdapter.this);
            }
        });
        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("vvvvvv", data.get((int)v.getTag()).getId()+"");
                Intent intent = new Intent(hf.getActivity(), DetailHotelActivity.class);
                intent.putExtra("hid", data.get((int)v.getTag()).getId());
                intent.putExtra("save", true);
                hf.startActivityForResult(intent, 70);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_catagory, tv_score, tv_hotelname, tv_price, tv_per;
        RoundedImageView iv_image;
        ImageView btn_favorite, soon_discount, soon_point;
        LinearLayout sel_item;
        boolean islike = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_catagory = (TextView) itemView.findViewById(R.id.tv_catagory);
            tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            tv_hotelname = (TextView) itemView.findViewById(R.id.tv_hotelname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_image = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            tv_per = (TextView) itemView.findViewById(R.id.tv_per);
            btn_favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
            soon_discount = (ImageView) itemView.findViewById(R.id.soon_discount);
            soon_point = (ImageView) itemView.findViewById(R.id.soon_point);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
        }
    }
}