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
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.MyViewHolder> {
    private ArrayList<DefaultItem> data = new ArrayList<>();
    private RecyclerView rv;

    public FooterAdapter(ArrayList<DefaultItem> data, RecyclerView rv) {
        this.data = data;
        this.rv = rv;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.tv_companyinfo.getVisibility() == View.VISIBLE){
                    holder.tv_companyinfo.setVisibility(View.GONE);
                }
                else {
                    holder.tv_companyinfo.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.term1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.term2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.term3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.bt_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(rv != null){
                               rv.smoothScrollToPosition(0);
                        }
                    }
                },100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView lv_more;
        TextView tv_companyinfo, tv_more, term1, term2, term3;
        Button bt_top;

        public MyViewHolder(View itemView) {
            super(itemView);
//            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
//            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
//            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            bt_top = (Button) itemView.findViewById(R.id.bt_top);
            tv_companyinfo = (TextView) itemView.findViewById(R.id.tv_companyinfo);
            tv_more = (TextView) itemView.findViewById(R.id.tv_more);
            term1 = (TextView) itemView.findViewById(R.id.term1);
            term2 = (TextView) itemView.findViewById(R.id.term2);
            term3 = (TextView) itemView.findViewById(R.id.term3);
        }
    }
}
