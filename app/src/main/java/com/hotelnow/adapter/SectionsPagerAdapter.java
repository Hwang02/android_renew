package com.hotelnow.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.hotelnow.fragment.search.ActivitySearchFragment;
import com.hotelnow.fragment.search.HotelSearchFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public Context mContext;
    public String search_txt, banner_id;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String search_txt, String banner_id) {
        super(fm);
        mContext = context;
        this.search_txt = search_txt;
        this.banner_id = banner_id;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position){
            case 0:
                HotelSearchFragment hotelSearchFragment = new HotelSearchFragment();
                Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                bundle.putString("search_txt", search_txt); // key , value
                bundle.putString("banner_id", banner_id); // key , value
                hotelSearchFragment.setArguments(bundle);
                return hotelSearchFragment;
            case 1:
                ActivitySearchFragment activitySearchFragment = new ActivitySearchFragment();
                return activitySearchFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}