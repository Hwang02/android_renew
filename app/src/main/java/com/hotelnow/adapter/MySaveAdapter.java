package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.MySaveActivity;
import com.hotelnow.fragment.model.MySaveMoneyItem;

import java.text.NumberFormat;
import java.util.List;

public class MySaveAdapter extends ArrayAdapter<MySaveMoneyItem> {
    private Context mContext;
    List<MySaveMoneyItem> mlist;

    public MySaveAdapter(Context context, int textViewResourceId, List<MySaveMoneyItem> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mlist = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_my_savemoney_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        MySaveMoneyItem entry = getItem(position);

        if(entry.getMid() != 0) {
            holder.view_item.setVisibility(View.VISIBLE);
            holder.empty_item.setVisibility(View.GONE);
            String type = entry.getMtype();
            String desc = "";
            String status = "";
            String price = "";
            int mColor = 0;
            NumberFormat nf = NumberFormat.getNumberInstance();
            if (entry.getMincome() > 0) {
                status = mContext.getString(R.string.reservemoney_status_reserve);
                mColor = ContextCompat.getColor(mContext, R.color.hotdealview);
            } else {
                if (type.equals("withdraw") || type.equals("expired"))
                    status = mContext.getString(R.string.reservemoney_status_withdraw);
                else
                    status = mContext.getString(R.string.reservemoney_status_use);

                mColor = ContextCompat.getColor(mContext, R.color.blacktxt);
            }

            price = entry.getChange_dp();

            String m_end = entry.getEnd_date();
            if(TextUtils.isEmpty(m_end)){
                holder.tv_save_date.setText(entry.getMcreatedat().substring(0, 10));
            }else {
                if (!status.equals("적립")) {
                    holder.tv_save_date.setText(entry.getMcreatedat().substring(0, 10) + "(" + entry.getEnd_date().substring(0, 10) + ")");
                }
                else {
                    holder.tv_save_date.setText(entry.getMcreatedat().substring(0, 10) + "(" + entry.getEnd_date().substring(0, 10) + " 만료)");
                }
            }
            holder.tv_save_status.setText(status);
            holder.tv_save_status.setTextColor(mColor);
            holder.tv_save_money.setText(price);
            holder.tv_save_money.setTextColor(mColor);
            holder.tv_save_title.setText(entry.getType_dp());

            if(mlist.size()-1 == position){
                holder.end_bar.setVisibility(View.INVISIBLE);
            }
            else{
                holder.end_bar.setVisibility(View.GONE);
            }
        }
        else {
            holder.view_item.setVisibility(View.GONE);
            holder.empty_item.setVisibility(View.VISIBLE);
            ((MySaveActivity)mContext).getEmptyHeight(holder.empty_item);
        }
        return v;
    }

    private class ViewHolder {
        TextView tv_save_date;
        TextView tv_save_status;
        TextView tv_save_title;
        TextView tv_save_money;
        LinearLayout view_item, empty_item;
        View end_bar;

        public ViewHolder(View v) {
            tv_save_date = (TextView) v.findViewById(R.id.tv_save_date);
            tv_save_status = (TextView) v.findViewById(R.id.tv_save_status);
            tv_save_title = (TextView) v.findViewById(R.id.tv_save_title);
            tv_save_money = (TextView) v.findViewById(R.id.tv_save_money);
            empty_item = (LinearLayout) v.findViewById(R.id.empty_item);
            view_item = (LinearLayout) v.findViewById(R.id.view_item);
            end_bar = (View) v.findViewById(R.id.end_bar);
            v.setTag(this);
        }
    }
}
