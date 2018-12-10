package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.hotelnow.fragment.favorite.FavoriteActivityFragment;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class FavoriteActivityAdapter extends ArrayAdapter<FavoriteStayItem> {

    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    List<FavoriteStayItem> mlist;
    FavoriteActivityFragment fa;

    public FavoriteActivityAdapter(Context context, FavoriteActivityFragment fa, int textViewResourceId, List<FavoriteStayItem> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
        this.fa = fa;
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
        String mLocation = "";
        if(!TextUtils.isEmpty(entry.getStreet1())){
            mLocation = entry.getStreet1();
        }
        if(!TextUtils.isEmpty(entry.getStreet2())){
            mLocation += "/"+entry.getStreet2();
        }
        holder.tv_nearlocation.setText(mLocation);
        Ion.with(holder.iv_img).load(entry.getLandscape());

        holder.room_count.setVisibility(View.GONE);
        holder.tv_soldout.setVisibility(View.GONE);

        holder.iv_favorite.setTag(position);
        holder.iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", mlist.get((int)v.getTag()).getId()+"");
                fa.setLike((int)v.getTag());
            }
        });

        holder.tv_rate.setText(entry.getGrade_score());
        holder.category.setText(entry.getCategory());
        holder.tv_discount_rate.setText(entry.getSale_rate()+"%↓");
        holder.sale_price.setText(Util.numberFormat(Integer.parseInt(entry.getSale_price())));

        if(entry.getGrade_score().equals("0.0")){
            holder.tv_rate.setVisibility(View.GONE);
            holder.text_bar.setVisibility(View.GONE);
            holder.img_star.setVisibility(View.GONE);
        }
        else{
            holder.tv_rate.setVisibility(View.VISIBLE);
            holder.text_bar.setVisibility(View.VISIBLE);
            holder.img_star.setVisibility(View.VISIBLE);
        }

        if(entry.getIs_hot_deal().equals("N")){
            holder.ico_hotdeal.setVisibility(View.GONE);
            holder.sale_price.setTextColor(ContextCompat.getColor(mContext, R.color.blacktxt));
            holder.won.setTextColor(ContextCompat.getColor(mContext, R.color.blacktxt));
        }
        else{
            holder.ico_hotdeal.setVisibility(View.VISIBLE);
            holder.sale_price.setTextColor(ContextCompat.getColor(mContext, R.color.redtext));
            holder.won.setTextColor(ContextCompat.getColor(mContext, R.color.redtext));
        }

        if(entry.getCoupon_count() > 0){
            holder.soon_discount.setVisibility(View.VISIBLE);
        }
        else{
            holder.soon_discount.setVisibility(View.GONE);
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
        holder.hid.setText(entry.getId());
        return v;
    }

    private class ViewHolder {

        ImageView iv_img, iv_favorite, ico_hotdeal, soon_discount, soon_point, img_star;
        TextView tv_rate, category, tv_nearlocation, hotel_name, tv_discount_rate, sale_price, room_count, won, tv_soldout, tv_special, hid;
        LinearLayout special_msg;
        View text_bar;

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
            hid = (TextView) v.findViewById(R.id.hid);

            special_msg = (LinearLayout) v.findViewById(R.id.special_msg);
            text_bar = (View) v.findViewById(R.id.v_bar);
            img_star = (ImageView) v.findViewById(R.id.ico_star);

            v.setTag(R.id.id_holder);
        }

    }
}
