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
import com.hotelnow.activity.PrivateDaelAllActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.thebrownarrow.model.SearchResultItem;

import java.util.List;

/**
 * Created by susia on 15. 12. 10..
 */

public class PrivateDealAllAdapter extends ArrayAdapter<SearchResultItem> {
    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    List<SearchResultItem> mlist;
    DbOpenHelper dbHelper;

    public PrivateDealAllAdapter(Context context, int textViewResourceId, List<SearchResultItem> objects, DbOpenHelper dbHelper) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_stay_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final SearchResultItem entry = getItem(position);

        holder.hotel_name.setText(entry.getName());
        holder.tv_nearlocation.setText(entry.getStreet1()+"/"+entry.getStreet2());
        Ion.with(holder.iv_img).load(entry.getLandscape());

        if(entry.getItems_quantity() < 5){
            if(entry.getItems_quantity() == 0) {
                holder.room_count.setVisibility(View.GONE);
                holder.tv_discount_rate.setVisibility(View.INVISIBLE);
                holder.sale_price.setVisibility(View.INVISIBLE);
                holder.won.setVisibility(View.INVISIBLE);
                holder.tv_soldout.setVisibility(View.VISIBLE);
            }
            else{
                holder.room_count.setVisibility(View.VISIBLE);
                holder.room_count.setText("남은객실 "+ entry.getItems_quantity()+"개");
                holder.tv_soldout.setVisibility(View.GONE);
            }
        }
        else{
            holder.room_count.setVisibility(View.GONE);
            holder.tv_soldout.setVisibility(View.GONE);
        }

        if(dbHelper.selectAllFavoriteStayItem().size() > 0) {
            for (int i = 0; i < dbHelper.selectAllFavoriteStayItem().size(); i++) {
                if (dbHelper.selectAllFavoriteStayItem().get(i).getSel_id().equals(entry.getId())) {
                    holder.iv_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                    holder.islike = true;
                    break;
                } else {
                    holder.iv_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                    holder.islike = false;
                }
            }
        }
        else{
            holder.iv_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
            holder.islike = false;
        }

        holder.iv_favorite.setTag(position);
        final ViewHolder finalHolder = holder;
        finalHolder.iv_favorite.setTag(position);
        finalHolder.iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("ggggg", mlist.get((int)v.getTag()).getId()+"");
                ((PrivateDaelAllActivity)mContext).setLike((int)v.getTag(), finalHolder.islike);
            }
        });

        holder.tv_rate.setText(entry.getGrade_score());

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

        holder.category.setText(entry.getCategory());
        holder.tv_discount_rate.setText(entry.getSale_rate()+"%↓");
        holder.sale_price.setText(Util.numberFormat(Integer.parseInt(entry.getSale_price())));

        if(entry.getIs_private_deal().equals("N")){
            holder.ico_private.setVisibility(View.GONE);
        }
        else{
            holder.ico_private.setVisibility(View.VISIBLE);
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

        ImageView iv_img, iv_favorite, ico_private, ico_hotdeal, soon_discount, soon_point, img_star;
        TextView tv_rate, category, tv_nearlocation, hotel_name, tv_discount_rate, sale_price, room_count, won, tv_soldout, tv_special, hid;
        LinearLayout special_msg;
        boolean islike = false;
        View text_bar;

        public ViewHolder(View v) {

            iv_img = (ImageView) v.findViewById(R.id.iv_img);
            iv_favorite = (ImageView) v.findViewById(R.id.iv_favorite);
            ico_private = (ImageView) v.findViewById(R.id.ico_private);
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
            hid = (TextView)v.findViewById(R.id.hid);

            text_bar = (View) v.findViewById(R.id.v_bar);
            img_star = (ImageView) v.findViewById(R.id.ico_star);

            v.setTag(R.id.id_holder);
        }

    }
}
