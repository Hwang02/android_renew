package com.hotelnow.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hotelnow.fragment.favorite.FavoriteActivityFragment;
import com.hotelnow.fragment.favorite.FavoriteHotelFragment;
import com.hotelnow.fragment.reservation.ReservationActivityFragment;
import com.hotelnow.fragment.reservation.ReservationHotelFragment;

public class ReservationAdapter extends FragmentPagerAdapter {

    public Context mContext;
    public String search_txt, banner_id;

    public ReservationAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position){
            case 0:
                ReservationHotelFragment resesrvationHotelFragment = new ReservationHotelFragment();
//                Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
//                bundle.putString("search_txt", search_txt); // key , value
//                bundle.putString("banner_id", banner_id); // key , value
//                resesrvationHotelFragment.setArguments(bundle);
                return resesrvationHotelFragment;
            case 1:
                ReservationActivityFragment resesrvationActivityFragment = new ReservationActivityFragment();
                return resesrvationActivityFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}