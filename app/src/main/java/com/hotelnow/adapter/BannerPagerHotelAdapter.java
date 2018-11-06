package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelnow.R;
import com.hotelnow.fragment.model.BannerItem;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class BannerPagerHotelAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<BannerItem> data;

    public BannerPagerHotelAdapter(Context context, ArrayList<BannerItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {

        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        int realPos = position % data.size();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hotel_banner_item, parent, false);
        RoundedImageView image_container = (RoundedImageView) v.findViewById(R.id.image_container);
        Ion.with(image_container).load(data.get(realPos).getImage());
        parent.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);

    }

    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
