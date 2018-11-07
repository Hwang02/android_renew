package com.hotelnow.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class FavoriteActivityAdapter extends ArrayAdapter<FavoriteStayItem> {

    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    List<FavoriteStayItem> mlist;

    public FavoriteActivityAdapter(Context context, int textViewResourceId, List<FavoriteStayItem> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_activity_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final FavoriteStayItem entry = getItem(position);

        holder.hotel_name.setText(entry.getName());
        holder.tv_nearlocation.setText(entry.getStreet1());
        Ion.with(holder.iv_img).load(entry.getLandscape());

        holder.room_count.setVisibility(View.GONE);
        holder.tv_soldout.setVisibility(View.GONE);

        holder.tv_rate.setText(entry.getGrade_score());
        holder.category.setText(entry.getCategory());
        holder.tv_discount_rate.setText(entry.getSale_rate()+"%↓");
        holder.sale_price.setText(entry.getSale_price());

        if(entry.getIs_hot_deal().equals("N")){
            holder.ico_hotdeal.setVisibility(View.GONE);
        }
        else{
            holder.ico_hotdeal.setVisibility(View.VISIBLE);
        }

        if(entry.getIs_add_reserve().equals("N")){
            holder.soon_point.setVisibility(View.GONE);
        }
        else{
            holder.soon_point.setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(entry.getSpecial_msg()) || entry.getSpecial_msg().equals("null")){
            holder.special_msg.setVisibility(View.GONE);
        }
        else{
            holder.special_msg.setVisibility(View.VISIBLE);
            holder.tv_special.setText(entry.getSpecial_msg());
        }

        return v;
    }

    private class ViewHolder {

        ImageView iv_img, iv_favorite, ico_hotdeal, soon_discount, soon_point;
        TextView tv_rate, category, tv_nearlocation, hotel_name, tv_discount_rate, sale_price, room_count, won, tv_soldout, tv_special;
        LinearLayout special_msg;

        public ViewHolder(View v) {

            iv_img = (ImageView) v.findViewById(R.id.iv_img);
            iv_favorite = (ImageView) v.findViewById(R.id.iv_favorite);
            ico_hotdeal = (ImageView) v.findViewById(R.id.ico_hotdeal);
            soon_discount = (ImageView) v.findViewById(R.id.soon_discount);
            soon_point = (ImageView) v.findViewById(R.id.soon_point);

            tv_rate = (TextView) v.findViewById(R.id.tv_rate);
            category = (TextView) v.findViewById(R.id.category);
            tv_nearlocation = (TextView) v.findViewById(R.id.tv_nearlocation);
            hotel_name = (TextView) v.findViewById(R.id.hotel_name);
            tv_discount_rate = (TextView) v.findViewById(R.id.tv_discount_rate);
            sale_price = (TextView) v.findViewById(R.id.sale_price);
            room_count = (TextView) v.findViewById(R.id.room_count);
            won = (TextView) v.findViewById(R.id.won);
            tv_soldout = (TextView) v.findViewById(R.id.tv_soldout);
            tv_special = (TextView) v.findViewById(R.id.tv_special);

            special_msg = (LinearLayout) v.findViewById(R.id.special_msg);

            v.setTag(R.id.id_holder);
        }

    }
}
