package com.hotelnow.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.AreaHotelActivity;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.HotelSearchActivity;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


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
                Api.get(CONFIG.server_time, new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        Toast.makeText(fm.getActivity(), fm.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                long time = obj.getInt("server_time") * (long) 1000;

                                CONFIG.svr_date = new Date(time);

                                Intent intent = new Intent(fm.getActivity(), CalendarActivity.class);
                                intent.putExtra("ec_date", data.get(0).getEc_date());
                                intent.putExtra("ee_date", data.get(0).getEe_date());
//                                intent.putExtra("ec_date", "2019-01-07");
//                                intent.putExtra("ee_date", "2019-01-08");
                                intent.putExtra("city", data.get(0).getLocation());
                                intent.putExtra("city_code", data.get(0).getLocation_id());
                                intent.putExtra("subcity_code", data.get(0).getLocation_subid());
                                intent.putExtra("lodge_type", "Y");
                                fm.startActivityForResult(intent, 80);
                            }
                        }
                        catch (Exception e){}
                    }
                });
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

                TuneWrap.Event("stay_search");

                Intent intent = new Intent(fm.getActivity(), HotelSearchActivity.class);
                intent.putExtra("ec_date", data.get(0).getEc_date());
                intent.putExtra("ee_date", data.get(0).getEe_date());
//                intent.putExtra("ec_date", "2019-01-07");
//                intent.putExtra("ee_date", "2019-01-08");
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
