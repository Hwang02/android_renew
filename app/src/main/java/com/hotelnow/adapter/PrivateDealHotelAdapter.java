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
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class PrivateDealHotelAdapter extends RecyclerView.Adapter<PrivateDealHotelAdapter.MyViewHolder> {

    private ArrayList<PrivateDealItem> data = new ArrayList<>();
    private HotelFragment hf;
    private DbOpenHelper dbHelper;

    public PrivateDealHotelAdapter(ArrayList<PrivateDealItem> data, HotelFragment hf, DbOpenHelper dbHelper) {
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_catagory.setText(data.get(position).getCategory());
//        holder.tv_score.setText(data.get(position).get());
        holder.tv_hotelname.setText(data.get(position).getName());
        holder.tv_price.setText(data.get(position).getSale_rate());
        Ion.with(holder.iv_image).load(data.get(position).getLandscape());
        if(data.get(position).isIslike()) {
            holder.btn_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{
            holder.btn_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
        }
        holder.btn_favorite.setTag(position);
        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", data.get((int)v.getTag()).getId()+"");
                hf.setPrivateLike((int)v.getTag(), data.get((int)v.getTag()).isIslike(), PrivateDealHotelAdapter.this);
            }
        });
        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("vvvvvv", data.get((int)v.getTag()).getId()+"");
                dbHelper.insertRecentItem(hf.getPrivateDealItem().get((int)v.getTag()).getId(), "H");
//                if(hf.getRecentListItem().size()>0) {
//                    hf.getRecentData(false);
//                }
//                else{
//                    hf.getRecentData(true);
//                }
                Intent intent = new Intent(hf.getActivity(), DetailHotelActivity.class);
                intent.putExtra("hid", data.get((int)v.getTag()).getId()+"");
                hf.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_catagory, tv_score, tv_hotelname, tv_price;
        ImageView iv_image, btn_favorite;
        LinearLayout sel_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            sel_item = (LinearLayout) itemView.findViewById(R.id.sel_item);
            tv_catagory = (TextView) itemView.findViewById(R.id.tv_catagory);
            tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            tv_hotelname = (TextView) itemView.findViewById(R.id.tv_hotelname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            btn_favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
        }
    }
}
