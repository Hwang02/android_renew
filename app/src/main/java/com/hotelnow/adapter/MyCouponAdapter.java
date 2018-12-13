package com.hotelnow.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.MyCouponActivity;
import com.hotelnow.dialog.DialogScrollAlert;
import com.hotelnow.fragment.model.CouponEntry;

import java.util.List;

/**
 * Created by susia on 15. 12. 10..
 */

public class MyCouponAdapter extends ArrayAdapter<CouponEntry> {
    Context mContext;
    String hotels = "";
    DialogScrollAlert dialogScrollAlert;
    List<CouponEntry> mlist;

    public MyCouponAdapter(Context context, int textViewResourceId, List<CouponEntry> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_my_coupon_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final CouponEntry entry = getItem(position);

        if(!entry.getmProduct().equals("empty")) {
            holder.view_item.setVisibility(View.VISIBLE);
            holder.empty_item.setVisibility(View.GONE);

            //title
            if (entry.getmProduct().equals("activity")) {
                holder.coupon_kind.setBackgroundResource(R.drawable.ico_coupon_activity);
            }
            else{
                holder.coupon_kind.setBackgroundResource(R.drawable.ico_coupon_stay);
            }

            //name
            if (entry.getmName() != null && !TextUtils.isEmpty(entry.getmName()))
                holder.coupon_name.setText(entry.getmName());

            //할인율
            if (entry.getmDiscount_value() != null && !TextUtils.isEmpty(entry.getmDiscount_value()))
                holder.coupon_money.setText(entry.getmDiscount_value());

            //기간
            if (entry.getmExpiration_date() != null && !TextUtils.isEmpty(entry.getmExpiration_date())) {
                holder.coupon_date.setVisibility(View.VISIBLE);
                holder.coupon_date.setText("\n" + entry.getmExpiration_date() + "\n");
            } else {
                holder.coupon_date.setVisibility(View.GONE);
            }

            // 쿠폰 옵션
            String options = "";
            if (entry.getmOption() != null && entry.getmOption().length > 0) {
                for (int i = 0; i < entry.getmOption().length; i++) {
                    options += "ㆍ" + entry.getmOption()[i] + "\n";
                }
                holder.coupon_info.setVisibility(View.VISIBLE);
                if (!entry.getmMin_price().equals("") && !entry.getmMin_price().equals("null")) {
                    holder.coupon_info.setText(options+"ㆍ"+entry.getmMin_price());
                }
                else{
                    holder.coupon_info.setText(options);
                }
            } else {
                if (!entry.getmMin_price().equals("") && !entry.getmMin_price().equals("null")) {
                    holder.coupon_info.setText("ㆍ"+entry.getmMin_price());
                }
                else {
                    holder.coupon_info.setVisibility(View.GONE);
                }
            }

            if (entry.getmTarget_text() != null && !TextUtils.isEmpty(entry.getmTarget_text()))
                holder.hids.setText(entry.getmTarget_text());

            if (entry.getmTarget_lists() != null && entry.getmTarget_lists().length > 0) {
                holder.coupon_hotel_count.setVisibility(View.VISIBLE);
                if (entry.getmProduct().equals("activity")) {
                    holder.coupon_hotel_count.setText(Html.fromHtml("<u>/  " + entry.getmTarget_lists().length + "개의 액티비티</u>"));
                }
                else {
                    holder.coupon_hotel_count.setText(Html.fromHtml("<u>/  " + entry.getmTarget_lists().length + "개의 숙소</u>"));
                }
                holder.coupon_hotel_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        hotels = "";
                        for (int i = 0; i < entry.getmTarget_lists().length; i++) {
                            hotels += "ㆍ"+entry.getmTarget_lists()[i] + "\n";
                        }
                        String mTitle = "";
                        if (entry.getmProduct().equals("activity")) {
                            mTitle = "사용 가능 액티비티 목록";
                        } else {
                            mTitle = "사용 가능 숙소 목록";
                        }
                        dialogScrollAlert = new DialogScrollAlert(
                                mTitle,
                                hotels,
                                mContext,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogScrollAlert.dismiss();
                                    }
                                });
                        dialogScrollAlert.setCancelable(true);
                        dialogScrollAlert.show();

                    }
                });
            } else {
                holder.coupon_hotel_count.setVisibility(View.VISIBLE);
                if (entry.getmProduct().equals("activity")) {
                    holder.coupon_hotel_count.setText("/  모든 액티비티");
                }
                else{
                    holder.coupon_hotel_count.setText("/  모든 숙소");
                }
            }

            holder.tv_limit.setText(entry.getmExpire_text());
            if(mlist.size()-1 == position){
                holder.end_bar.setVisibility(View.INVISIBLE);
            }
            else{
                holder.end_bar.setVisibility(View.GONE);
            }
        }
        else{
            holder.view_item.setVisibility(View.GONE);
            holder.empty_item.setVisibility(View.VISIBLE);
            ((MyCouponActivity)mContext).getEmptyHeight(holder.empty_item);
        }

        return v;
    }

    private class ViewHolder {
        ImageView coupon_kind;
        TextView coupon_money;
        TextView coupon_name;
        TextView coupon_hotel_count;
        TextView coupon_date;
        TextView coupon_info;
        TextView hids;
        TextView tv_limit;
        View end_bar;
        LinearLayout view_item, empty_item;

        public ViewHolder(View v) {
            coupon_kind = (ImageView) v.findViewById(R.id.coupon_kind);
            coupon_money = (TextView) v.findViewById(R.id.coupon_money);
            coupon_name = (TextView) v.findViewById(R.id.coupon_name);
            coupon_hotel_count = (TextView) v.findViewById(R.id.coupon_hotel_count);
            coupon_date = (TextView) v.findViewById(R.id.coupon_date);
            coupon_info = (TextView) v.findViewById(R.id.coupon_info);
            hids = (TextView) v.findViewById(R.id.hids);
            tv_limit = (TextView) v.findViewById(R.id.tv_limit);
            empty_item = (LinearLayout) v.findViewById(R.id.empty_item);
            view_item = (LinearLayout) v.findViewById(R.id.view_item);
            end_bar = (View) v.findViewById(R.id.end_bar);
            v.setTag(R.id.id_holder);
        }

    }
}
