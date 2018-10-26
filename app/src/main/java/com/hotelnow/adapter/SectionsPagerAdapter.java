package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.hotelnow.fragment.search.ActivitySearchFragment;
import com.hotelnow.fragment.search.HotelSearchFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {

        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position){
            case 0:
                HotelSearchFragment hotelSearchFragment = new HotelSearchFragment();
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

//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        switch (position) {
//
//            case 0:
//                return "Tab 1";
//            case 1:
//                return "Tab 2";
//        }
//        return null;
//    }
}