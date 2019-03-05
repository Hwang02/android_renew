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
import com.hotelnow.activity.ActivityFilterActivity;
import com.hotelnow.activity.ActivitySearchActivity;
import com.hotelnow.activity.AreaActivityActivity;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.TuneWrap;

import java.util.ArrayList;


public class HeaderLAdapter extends RecyclerView.Adapter<HeaderLAdapter.MyViewHolder> {
    private ArrayList<TopItem> data = new ArrayList<>();
    private LeisureFragment fm;

    public HeaderLAdapter(ArrayList<TopItem> data, LeisureFragment fm) {
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
        holder.title_category.setText("테마선택");

        holder.tv_date.setText(data.get(0).getEc_date());

        holder.tv_location.setText(data.get(0).getLocation());

        holder.btn_date.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                fm.sel_location = data.get(0).getLocation();
                fm.sel_location_id = data.get(0).getLocation_id();
                Intent intent = new Intent(fm.getActivity(), ActivityFilterActivity.class);
                intent.putExtra("tv_category", data.get(0).getEc_date());
                fm.startActivityForResult(intent, 60);
            }
        });

        holder.btn_location.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                fm.sel_theme = data.get(0).getEc_date();
                fm.sel_theme_id = data.get(0).getEe_date();
                Intent intent = new Intent(fm.getActivity(), AreaActivityActivity.class);
                fm.startActivityForResult(intent, 80);
            }
        });

        holder.btn_search.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                TuneWrap.Event("activity_search");

                Intent intent = new Intent(fm.getActivity(), ActivitySearchActivity.class);
                intent.putExtra("location", data.get(0).getLocation());
                intent.putExtra("location_id", data.get(0).getLocation_id());
                intent.putExtra("theme", data.get(0).getEc_date());
                intent.putExtra("theme_id", data.get(0).getEe_date());
                fm.startActivityForResult(intent, 40);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout btn_location, btn_date;
        TextView tv_date, tv_location, title_category;
        Button btn_search;

        public MyViewHolder(View itemView) {
            super(itemView);
            btn_search = (Button) itemView.findViewById(R.id.btn_search);
            btn_location = (LinearLayout) itemView.findViewById(R.id.btn_location);
            btn_date = (LinearLayout) itemView.findViewById(R.id.btn_date);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            title_category = (TextView) itemView.findViewById(R.id.title_category);
        }
    }
}
