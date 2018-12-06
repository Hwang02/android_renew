package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.ReviewActivityWriteActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.BookingQEntry;
import com.hotelnow.fragment.reservation.ReservationActivityFragment;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ReservationActivityAdapter extends ArrayAdapter<BookingQEntry> {

    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    List<BookingQEntry> mlist;
    String userId = "";
    ReservationActivityFragment mActivity;

    public ReservationActivityAdapter(Context context, int textViewResourceId, List<BookingQEntry> objects, String userId, ReservationActivityFragment mActivity) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
        this.userId = userId;
        this.mActivity = mActivity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_reservation_activity_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final BookingQEntry entry = getItem(position);

        holder.hotel_name.setText(entry.getDeal_name());
        holder.hotel_reser_date.setText("예약일 : "+entry.getCreated_at_format());

        Ion.with(holder.iv_img).load(entry.getImg_url());

        if(entry.getStatus_detail().equals("inprogress")){
            // 대기
            holder.booking_status.setBackgroundResource(R.drawable.bg_round_status_inpro);
        }
        else if(entry.getStatus_detail().equals("used") || entry.getStatus_detail().equals("cancel") || entry.getStatus_detail().equals("expiration")){
            // 사용완료
            holder.booking_status.setBackgroundResource(R.drawable.bg_round_status_can_comple);
        }
        else {
            //결제만료
            holder.booking_status.setBackgroundResource(R.drawable.bg_round_status_book);
        }
        holder.booking_status.setText(entry.getStatus_display());

        if(entry.getStatus().equals("booked") && entry.getIs_review_writable().equals("Y")){
            holder.btn_review.setVisibility(View.VISIBLE);
            holder.review_text1.setText(entry.getReview_writable_words_1());
            holder.review_text2.setText(entry.getReview_writable_words_2());
            holder.btn_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ReviewActivityWriteActivity.class);
                    intent.putExtra("booking_id", mlist.get(position).getId());
                    intent.putExtra("deal_id", mlist.get(position).getId());
                    intent.putExtra("userid", userId);
                    intent.putExtra("name", mlist.get(position).getDeal_name());
                    intent.putExtra("cnt", mlist.get(position).getTotal_ticket_count());
                    mActivity.startActivityForResult(intent, 90);
                }
            });
        } else{
            holder.btn_review.setVisibility(View.GONE);
        }

        holder.not_used_count.setText(entry.getNot_used_ticket_count()+"장");
        holder.used_count.setText(entry.getUsed_ticket_count()+"장");
        holder.cancel_count.setText(entry.getCancel_ticket_count()+"장");
        holder.aid.setText(entry.getId());
        return v;
    }

    private class ViewHolder {

        ImageView iv_img;
        TextView hotel_reser_date, hotel_name, booking_status, not_used_count, used_count, cancel_count, review_text1, review_text2, aid;
        LinearLayout btn_review;

        public ViewHolder(View v) {
            iv_img = (ImageView) v.findViewById(R.id.iv_img);
            hotel_reser_date = (TextView) v.findViewById(R.id.hotel_reser_date);
            hotel_name = (TextView) v.findViewById(R.id.hotel_name);
            booking_status = (TextView) v.findViewById(R.id.booking_status);
            btn_review = (LinearLayout) v.findViewById(R.id.btn_review);
            not_used_count = (TextView) v.findViewById(R.id.not_used_count);
            used_count = (TextView) v.findViewById(R.id.used_count);
            cancel_count = (TextView) v.findViewById(R.id.cancel_count);
            review_text1 = (TextView) v.findViewById(R.id.review_text1);
            review_text2 = (TextView) v.findViewById(R.id.review_text2);
            aid = (TextView) v.findViewById(R.id.aid);

            v.setTag(R.id.id_holder);
        }

    }
}
