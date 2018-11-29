package com.hotelnow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class BannerAllAdapter extends ArrayAdapter<BannerItem> {

    List<BannerItem> mlist;
    DbOpenHelper dbHelper;
    Context mContext;
    String page;

    public BannerAllAdapter(Context context, int textViewResourceId, List<BannerItem> objects, DbOpenHelper dbHelper, String page) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;
        this.dbHelper = dbHelper;
        this.page = page;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_banner_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        BannerItem entry = getItem(position);
        RelativeLayout.LayoutParams param;
        if(page == "Home") {
            param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dptopixel(mContext, 180));
        }
        else {
            param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dptopixel(mContext, 130));
        }

        if(position == 0){
            param.topMargin = Util.dptopixel(mContext, 14);
            param.leftMargin = Util.dptopixel(mContext, 16);
            param.rightMargin = Util.dptopixel(mContext, 16);
            holder.iv_img.setLayoutParams(param);
        }else{
            param.topMargin = Util.dptopixel(mContext, 10);
            param.leftMargin = Util.dptopixel(mContext, 16);
            param.rightMargin = Util.dptopixel(mContext, 16);
            if(mlist.size()-1 == position)
                param.bottomMargin = Util.dptopixel(mContext, 10);
            holder.iv_img.setLayoutParams(param);
        }
        Ion.with(holder.iv_img).load(entry.getImage());

        return v;
    }

    private class ViewHolder {

        RoundedImageView iv_img;

        public ViewHolder(View v) {
            iv_img = (RoundedImageView) v.findViewById(R.id.image_container);

            v.setTag(R.id.id_holder);
        }

    }

}
