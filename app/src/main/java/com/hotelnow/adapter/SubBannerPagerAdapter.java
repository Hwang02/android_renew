package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.SubBannerItem;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class SubBannerPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<SubBannerItem> data;

    public SubBannerPagerAdapter(Context context, ArrayList<SubBannerItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {

        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        int realPos = position % data.size();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subbanner_item, parent, false);
        ImageView image_container = (ImageView) v.findViewById(R.id.image_container);
        Ion.with(image_container).load(data.get(realPos).getImage());
        parent.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);

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
