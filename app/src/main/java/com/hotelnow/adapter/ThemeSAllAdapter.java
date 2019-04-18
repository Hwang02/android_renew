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
import com.hotelnow.activity.ThemeSAllActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.model.ThemeSpecialItem;
import com.hotelnow.utils.DbOpenHelper;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ThemeSAllAdapter extends ArrayAdapter<ThemeSpecialItem> {

    List<ThemeSpecialItem> mlist;
    DbOpenHelper dbHelper;
    Context mContext;

    public ThemeSAllAdapter(Context context, int textViewResourceId, List<ThemeSpecialItem> objects, DbOpenHelper dbHelper) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_theme_special_item2, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        ThemeSpecialItem entry = getItem(position);

        Ion.with(holder.iv_image).load(entry.getImg_background());
        holder.tv_title.setText(entry.getTitle());
        holder.tv_message.setText(entry.getSub_title());
        holder.sel_item.setTag(position);
        holder.sel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlist.get((int) v.getTag()).getTheme_flag().equals("H")) {
                    Intent intent = new Intent(mContext, ThemeSpecialHotelActivity.class);
                    intent.putExtra("tid", mlist.get((int) v.getTag()).getId());
                    ((ThemeSAllActivity) mContext).startActivityForResult(intent, 80);
                } else {
                    Intent intent = new Intent(mContext, ThemeSpecialActivityActivity.class);
                    intent.putExtra("tid", mlist.get((int) v.getTag()).getId());
                    ((ThemeSAllActivity) mContext).startActivityForResult(intent, 80);
                }
            }
        });

        if (mlist.size() - 1 == position) {
            holder.gap.setVisibility(View.VISIBLE);
        } else {
            holder.gap.setVisibility(View.GONE);
        }

        return v;
    }

    private class ViewHolder {

        ImageView iv_image;
        TextView tv_title, tv_message;
        LinearLayout sel_item;
        View gap;

        public ViewHolder(View v) {
            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_message = (TextView) v.findViewById(R.id.tv_message);
            sel_item = (LinearLayout) v.findViewById(R.id.sel_item);
            gap = (View) v.findViewById(R.id.gap);

            v.setTag(R.id.id_holder);
        }

    }

}
