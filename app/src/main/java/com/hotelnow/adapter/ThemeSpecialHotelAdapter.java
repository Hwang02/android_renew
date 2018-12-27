package com.hotelnow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.fragment.model.ThemeSItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by susia on 16. 5. 8..
 */
public class ThemeSpecialHotelAdapter extends ArrayAdapter<ThemeSItem> {
    Context mContext;
    List<ThemeSItem> data;
    DbOpenHelper dbHelper;

    public ThemeSpecialHotelAdapter(Context context, int textViewResourceId, List<ThemeSItem> objects, DbOpenHelper dbHelper) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.data = objects;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_theme_speical_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        ThemeSItem entry = getItem(position);

        if(!entry.getCategory().equals("-1")) {
            holder.layout_top.setVisibility(View.GONE);
            holder.layout_item.setVisibility(View.VISIBLE);
            holder.pid.setText(entry.getId());
            holder.hid.setText(entry.getId());
            holder.hotel_name.setText(entry.getName());
            holder.tv_nearlocation.setText(entry.getStreet1()+"/"+entry.getStreet2());
            Ion.with(holder.iv_img).load(entry.getLandscape());
            holder.show_text.setText(entry.getStreet2());

            if(entry.getItems_quantity() < 4){
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

            if(entry.getIs_add_reserve().equals("N")){
                holder.soon_point.setVisibility(View.GONE);
            }
            else{
                holder.soon_point.setVisibility(View.VISIBLE);
            }

            if(entry.getCoupon_count() > 0){
                holder.soon_discount.setVisibility(View.VISIBLE);
            }
            else{
                holder.soon_discount.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(entry.getSpecial_msg()) || entry.getSpecial_msg().equals("null")){
                holder.special_msg.setVisibility(View.GONE);
            }
            else{
                holder.special_msg.setVisibility(View.VISIBLE);
                holder.tv_special.setText(entry.getSpecial_msg());
            }
            holder.sdate.setText(entry.getCheckin());
            holder.edate.setText(entry.getCheckout());

            final ViewHolder finalHolder = holder;
            finalHolder.iv_favorite.setTag(position);
            if(dbHelper.selectAllFavoriteStayItem().size()>0) {
                for (int i = 0; i < dbHelper.selectAllFavoriteStayItem().size(); i++) {
                    if (dbHelper.selectAllFavoriteStayItem().get(i).getSel_id().equals(data.get(position).getId())) {
                        holder.iv_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                        finalHolder.islike = true;
                        break;
                    } else {
                        holder.iv_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                        finalHolder.islike = false;
                    }
                }
            }
            else{
                holder.iv_favorite.setBackgroundResource(R.drawable.ico_favorite_enabled);
                finalHolder.islike = false;
            }

            finalHolder.iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("ggggg", data.get((int)v.getTag()).getId()+"");
                    ((ThemeSpecialHotelActivity)mContext).setLike((int)v.getTag(), finalHolder.islike);
                }
            });

        } else{
            holder.layout_top.setVisibility(View.VISIBLE);
            holder.layout_item.setVisibility(View.GONE);
            holder.pid.setText(entry.getCategory());
            //이미지
            Ion.with(holder.iv_top).load(entry.getTop_img());
            //제목
            holder.tv_subject.setMaxLines(1);
            holder.tv_subject.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_subject.setText(entry.getName());
            //상세
            holder.tv_detail.setMaxLines(4);
            holder.tv_detail.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_detail.setText(entry.getStreet1());
        }

        return v;
    }

    @Override
    public ThemeSItem getItem(int position) {
        return super.getItem(position);
    }

    private class ViewHolder {
        ImageView iv_img, iv_favorite, ico_private, ico_hotdeal, soon_discount, soon_point, iv_top, img_star;
        TextView tv_rate, category, tv_nearlocation, hotel_name, tv_discount_rate, sale_price, room_count, won, tv_soldout, tv_special, tv_subject, tv_detail, pid, hid, show_text;
        LinearLayout special_msg, layout_item;
        RelativeLayout layout_top;
        TextView sdate, edate;
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
            layout_item = (LinearLayout) v.findViewById(R.id.ll_item);
            layout_top = (RelativeLayout) v.findViewById(R.id.layout_top);
            iv_top = (ImageView) v.findViewById(R.id.iv_top);
            tv_subject = (TextView) v.findViewById(R.id.tv_subject);
            tv_detail = (TextView) v.findViewById(R.id.tv_detail);
            pid = (TextView) v.findViewById(R.id.pid);
            hid = (TextView) v.findViewById(R.id.hid);
            sdate = (TextView)v.findViewById(R.id.sdate);
            edate = (TextView)v.findViewById(R.id.edate);

            text_bar = (View) v.findViewById(R.id.v_bar);
            img_star = (ImageView) v.findViewById(R.id.ico_star);
            show_text = (TextView) v.findViewById(R.id.show_text);

            v.setTag(R.id.id_holder);
        }
    }
}
