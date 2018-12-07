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
import com.hotelnow.activity.ReviewHotelWriteActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.BookingEntry;
import com.hotelnow.fragment.reservation.ReservationHotelFragment;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ReservationHotelAdapter extends ArrayAdapter<BookingEntry> {

    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    List<BookingEntry> mlist;
    String userId = "";
    ReservationHotelFragment mActivity;

    public ReservationHotelAdapter(Context context, int textViewResourceId, List<BookingEntry> objects, String userId, ReservationHotelFragment mActivity) {
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
            v = vi.inflate(R.layout.layout_reservation_stay_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final BookingEntry entry = getItem(position);

        holder.hotel_name.setText(entry.getHotelName());
        holder.hotel_room_name.setText(entry.getRoomName());
        holder.hotel_reser_date.setText(Util.formatchange6(entry.getCheckinDate().substring(0, 10)) +" ~ "+Util.formatchange6(entry.getCheckoutDate().substring(0, 10)));

        Ion.with(holder.iv_img).load(entry.getRoom_img());
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

        if(entry.getStatus().equals("booked") && entry.getmReviewCnt() == 0 && entry.getIsRwritable().equals("Y")){
            holder.btn_review.setVisibility(View.VISIBLE);
            holder.review_text1.setText(entry.getReview_writable_words_1());
            holder.review_text2.setText(entry.getReview_writable_words_2());
            holder.btn_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ReviewHotelWriteActivity.class);
                    intent.putExtra("booking_id", mlist.get(position).getId());
                    intent.putExtra("hotel_id", mlist.get(position).getmHotelId());
                    intent.putExtra("room_id", mlist.get(position).getmRoomId());
                    intent.putExtra("userid", userId);
                    intent.putExtra("hotel_name", mlist.get(position).getHotelName());
                    intent.putExtra("room_name", mlist.get(position).getRoomName());
                    mActivity.startActivityForResult(intent, 80);
                }
            });
        } else{
            holder.btn_review.setVisibility(View.GONE);
        }

        holder.hid.setText(entry.getId());

        if(mlist.size()-2 == position){
            mActivity.getBookingList();
        }

        return v;
    }

    private class ViewHolder {

        ImageView iv_img;
        TextView hotel_reser_date, hotel_room_name, hotel_name, booking_status, review_text1, review_text2, hid;
        LinearLayout btn_review;

        public ViewHolder(View v) {
            iv_img = (ImageView) v.findViewById(R.id.iv_img);
            hotel_reser_date = (TextView) v.findViewById(R.id.hotel_reser_date);
            hotel_room_name = (TextView) v.findViewById(R.id.hotel_room_name);
            hotel_name = (TextView) v.findViewById(R.id.hotel_name);
            booking_status = (TextView) v.findViewById(R.id.booking_status);
            btn_review = (LinearLayout) v.findViewById(R.id.btn_review);
            review_text1 = (TextView) v.findViewById(R.id.review_text1);
            review_text2 = (TextView) v.findViewById(R.id.review_text2);
            hid = (TextView) v.findViewById(R.id.hid);

            v.setTag(R.id.id_holder);
        }

    }
}
