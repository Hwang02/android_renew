package com.hotelnow.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.hotelnow.fragment.favorite.FavoriteActivityFragment;
import com.hotelnow.fragment.favorite.FavoriteHotelFragment;
import com.hotelnow.fragment.search.ActivitySearchFragment;
import com.hotelnow.fragment.search.HotelSearchFragment;

public class FavoriteAdapter extends FragmentPagerAdapter {

    public Context mContext;
    public String search_txt, banner_id;

    public FavoriteAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position) {
            case 0:
                FavoriteHotelFragment favoriteHotelFragment = new FavoriteHotelFragment();
                return favoriteHotelFragment;
            case 1:
                FavoriteActivityFragment favoriteActivityFragment = new FavoriteActivityFragment();
                return favoriteActivityFragment;
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