package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hotelnow.R;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by idhwang on 2018. 7. 3..
 */

public class ReservationPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> img;

    public ReservationPagerAdapter(Context context, ArrayList<String> img) {
        this.context = context;
        this.img = img;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return img.size() * 10;
//        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.layout_reservation_banner_item, container, false);
        int realPos = position % img.size();
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Ion.with(imageView).load(img.get(realPos));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
