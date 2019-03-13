package com.hotelnow.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hotelnow.fragment.hotdeal.HotDealActivityFragment;
import com.hotelnow.fragment.hotdeal.HotDealHotelFragment;
import com.hotelnow.fragment.search.ActivitySearchFragment;
import com.hotelnow.fragment.search.HotelSearchFragment;

public class HotDealPagerAdapter extends FragmentPagerAdapter {

    public Context mContext;
    public Bundle bundle;

    public HotDealPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position) {
            case 0:
                HotDealHotelFragment hotDealHotelFragment = new HotDealHotelFragment();
                return hotDealHotelFragment;
            case 1:
                HotDealActivityFragment hotDealActivityFragment = new HotDealActivityFragment();
                return hotDealActivityFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }
}