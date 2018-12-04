package com.hotelnow.fragment.reservation;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelnow.R;
import com.hotelnow.adapter.ReservationAdapter;
import com.hotelnow.databinding.FragmentReservationBinding;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    private FragmentReservationBinding mReservationBinding;
    private ArrayList<Object> objects = new ArrayList<>();
    private ReservationAdapter reservationAdapter;
    private DbOpenHelper dbHelper;
    private SharedPreferences _preferences;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mReservationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation, container, false);
        View inflate = mReservationBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(_preferences.getString("userid", null) != null) {
            mReservationBinding.info.setVisibility(View.VISIBLE);
            mReservationBinding.line.setVisibility(View.VISIBLE);
        }
        else{
            mReservationBinding.info.setVisibility(View.GONE);
            mReservationBinding.line.setVisibility(View.GONE);
        }

        dbHelper = new DbOpenHelper(getActivity());
        mReservationBinding.tabLayout.addTab(mReservationBinding.tabLayout.newTab().setText("숙소"));
        mReservationBinding.tabLayout.addTab(mReservationBinding.tabLayout.newTab().setText("액티비티"));
        mReservationBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        reservationAdapter = new ReservationAdapter(getActivity(), getChildFragmentManager());
        mReservationBinding.viewPager.setAdapter(reservationAdapter);

        if(CONFIG.sel_reserv == 0) {
            mReservationBinding.viewPager.setCurrentItem(0);
        }
        else {
            mReservationBinding.viewPager.setCurrentItem(1);
        }

        mReservationBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mReservationBinding.viewPager.setCurrentItem(tab.getPosition());
                if(CONFIG.TabLogin) {
                    reservationAdapter.notifyDataSetChanged();
                    CONFIG.TabLogin=false;
                }
//                CONFIG.sel_reserv = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
