package com.hotelnow.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.model.DefaultItem;

import java.util.ArrayList;


public class FooterHAAdapter extends RecyclerView.Adapter<FooterHAAdapter.MyViewHolder> {
    private ArrayList<DefaultItem> data = new ArrayList<>();
    private RecyclerView rv;

    public FooterHAAdapter(ArrayList<DefaultItem> data, RecyclerView rv) {
        this.data = data;
        this.rv = rv;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ha_footer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.bt_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (rv != null) {
                            rv.smoothScrollToPosition(0);
                        }
                    }
                }, 100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView lv_more, bt_top;
        TextView tv_companyinfo, tv_more, term1, term2, term3;

        public MyViewHolder(View itemView) {
            super(itemView);
            bt_top = (ImageView) itemView.findViewById(R.id.bt_top);
        }
    }
}
