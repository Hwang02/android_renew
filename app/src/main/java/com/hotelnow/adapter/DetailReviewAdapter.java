package com.hotelnow.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.ReviewHotelActivity;
import com.hotelnow.fragment.model.ReviewItem;
import com.hotelnow.utils.Util;

import java.util.List;

public class DetailReviewAdapter extends ArrayAdapter<ReviewItem> {
    private Context mContext;
    List<ReviewItem> mlist;
    boolean is_q =false;

    public DetailReviewAdapter(Context context, int textViewResourceId, List<ReviewItem> objects, boolean is_q) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mlist = objects;
        this.is_q = is_q;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_detail_review_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        ReviewItem entry = getItem(position);

        if(!is_q) {
            holder.review_rate.setText(entry.getTotal_rating().substring(0, 3));
            holder.review_info.setText(entry.getRoom_name() + ", " + entry.getStay_cnt() + "박");
            holder.review_message.setText(entry.getComment());
            holder.review_user.setText(entry.getMasked_name() + " | " + Util.formatchange4(entry.getCreated_at()));
            holder.hotel_answer.setVisibility(View.GONE);

            if (!entry.getOwner_comment().equals("null")) {
                holder.hotel_answer.setVisibility(View.VISIBLE);
                holder.tv_hotel_name.setText(entry.getHotel_name());
                holder.tv_hotel_date.setText(" |    " + Util.formatchange4(entry.getUpdated_at()));
                holder.hotel_message.setText(Html.fromHtml(entry.getOwner_comment()));
            }
        }
        else{
            holder.review_rate.setText(entry.getTotal_rating().substring(0, 3));
            holder.review_info.setVisibility(View.GONE);
            holder.review_message.setText(entry.getComment());
            holder.review_user.setText(entry.getMasked_name() + " | " + Util.formatchange4(entry.getCreated_at()));
            holder.hotel_answer.setVisibility(View.GONE);
        }

        if(mlist.size() == position+2){
            ((ReviewHotelActivity)mContext).getReviewList();
        }

        return v;
    }

    private class ViewHolder {
        TextView review_rate, review_info, review_message, review_user, tv_hotel_name, tv_hotel_date, hotel_message;
        LinearLayout hotel_answer;

        public ViewHolder(View v) {
            review_rate = (TextView) v.findViewById(R.id.review_rate);
            review_info = (TextView) v.findViewById(R.id.review_info);
            review_message = (TextView) v.findViewById(R.id.review_message);
            review_user = (TextView) v.findViewById(R.id.review_user);
            tv_hotel_name = (TextView) v.findViewById(R.id.tv_hotel_name);
            tv_hotel_date = (TextView) v.findViewById(R.id.tv_hotel_date);
            hotel_message = (TextView) v.findViewById(R.id.hotel_message);
            hotel_answer = (LinearLayout) v.findViewById(R.id.hotel_answer);
            v.setTag(this);
        }
    }
}
