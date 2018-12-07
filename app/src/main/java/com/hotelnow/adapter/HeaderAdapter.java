package com.hotelnow.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hotelnow.R;
import com.hotelnow.activity.AreaHotelActivity;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.HotelSearchActivity;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import java.util.ArrayList;


public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.MyViewHolder> {
    private ArrayList<TopItem> data = new ArrayList<>();
    private HotelFragment fm;

    public HeaderAdapter(ArrayList<TopItem> data, HotelFragment fm) {
        this.data = data;
        this.fm = fm;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hotel_header, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_date.setText(Util.formatchange5(data.get(0).getEc_date())+" - "+Util.formatchange5(data.get(0).getEe_date())+", "+Util.diffOfDate2(data.get(0).getEc_date(), data.get(0).getEe_date())+"박");

        holder.tv_location.setText(data.get(0).getLocation());

        holder.btn_date.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(fm.getActivity(), CalendarActivity.class);
                intent.putExtra("ec_date", data.get(0).getEc_date());
                intent.putExtra("ee_date", data.get(0).getEe_date());
                intent.putExtra("city", data.get(0).getLocation());
                intent.putExtra("city_code", data.get(0).getLocation_id());
                intent.putExtra("subcity_code", data.get(0).getLocation_subid());
                intent.putExtra("lodge_type", "Y");
                fm.startActivityForResult(intent, 80);
            }
        });

        holder.btn_location.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(fm.getActivity(), AreaHotelActivity.class);
                intent.putExtra("ec_date", data.get(0).getEc_date());
                intent.putExtra("ee_date", data.get(0).getEe_date());
                intent.putExtra("city", data.get(0).getLocation());
                intent.putExtra("city_code", data.get(0).getLocation_id());
                intent.putExtra("subcity_code", data.get(0).getLocation_subid());
                intent.putExtra("lodge_type", "Y");
                fm.startActivityForResult(intent, 80);
            }
        });

        holder.btn_search.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(fm.getActivity(), HotelSearchActivity.class);
                intent.putExtra("ec_date", data.get(0).getEc_date());
                intent.putExtra("ee_date", data.get(0).getEe_date());
                intent.putExtra("city", data.get(0).getLocation());
                intent.putExtra("city_code", data.get(0).getLocation_id());
                intent.putExtra("subcity_code", data.get(0).getLocation_subid());
                fm.startActivityForResult(intent, 80);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout btn_location, btn_date;
        TextView tv_date, tv_location;
        Button btn_search;

        public MyViewHolder(View itemView) {
            super(itemView);
            btn_location = (LinearLayout) itemView.findViewById(R.id.btn_location);
            btn_date = (LinearLayout) itemView.findViewById(R.id.btn_date);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            btn_search = (Button) itemView.findViewById(R.id.btn_search);
        }
    }
}
