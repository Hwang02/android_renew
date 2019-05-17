package com.hotelnow.fragment.reservation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hotelnow.R;
import com.hotelnow.databinding.FragmentReservationBinding;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;

public class ReservationFragment extends Fragment {

    private FragmentReservationBinding mReservationBinding;
    private DbOpenHelper dbHelper;
    private SharedPreferences _preferences;
    private FragmentTransaction childFt;
    private Fragment fg;
    private Activity activity = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

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
        dbHelper = new DbOpenHelper(getActivity());

        mReservationBinding.tabLayout.addTab(mReservationBinding.tabLayout.newTab().setText("숙소"));
        mReservationBinding.tabLayout.addTab(mReservationBinding.tabLayout.newTab().setText("액티비티"));
        mReservationBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mReservationBinding.tabLayout.getTabAt(CONFIG.sel_reserv).select();

        if (CONFIG.sel_reserv == 0) {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            TuneWrap.Event("booking_stay");
                            fg = new ReservationHotelFragment();
                            setChildFragment(fg, CONFIG.sel_reserv);

                        }
                    }, 100);
        } else {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            TuneWrap.Event("booking_activity");
                            fg = new ReservationActivityFragment();
                            setChildFragment(fg, CONFIG.sel_reserv);
                        }
                    }, 100);

        }

        mReservationBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TuneWrap.Event("booking_stay");
                    fg = new ReservationHotelFragment();
                }
                else {
                    TuneWrap.Event("booking_activity");
                    fg = new ReservationActivityFragment();
                }
                setChildFragment(fg, tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setChildFragment(Fragment child, int tag) {
        if(activity != null) {
            activity.getFragmentManager().popBackStack();
        }
        childFt = getChildFragmentManager().beginTransaction();

        if(tag == 0){
            if (getChildFragmentManager().findFragmentByTag("1Reservation") != null) {
                childFt.hide(getChildFragmentManager().findFragmentByTag("1Reservation"));
            }
            if (getChildFragmentManager().findFragmentByTag("0Reservation") == null) {
                childFt.add(R.id.view_pager, child, "0Reservation");
                LogUtil.e("view", "hotel");
            }
            else{
                childFt.show(getChildFragmentManager().findFragmentByTag("0Reservation"));
                LogUtil.e("view", "hotel1");
            }
        }
        else{
            if (getChildFragmentManager().findFragmentByTag("0Reservation") != null) {
                childFt.hide(getChildFragmentManager().findFragmentByTag("0Reservation"));
            }
            if (getChildFragmentManager().findFragmentByTag("1Reservation") == null) {
                childFt.add(R.id.view_pager, child, "1Reservation");
                LogUtil.e("view", "activity");
            } else {
                childFt.show(getChildFragmentManager().findFragmentByTag("1Reservation"));
                LogUtil.e("view", "activity1");
            }
        }
//        childFt.addToBackStack(null);
        childFt.commitAllowingStateLoss();
    }

    public void setChildDelete(int tag) {
        childFt = getChildFragmentManager().beginTransaction();

        if(tag == 0){
            LogUtil.e("delete", "activity");
            if (getChildFragmentManager().findFragmentByTag("1Reservation") != null) {
                LogUtil.e("delete", "activity1");
                childFt.remove(getChildFragmentManager().findFragmentByTag("1Reservation"));
            }
            LogUtil.e("delete", "activity2");
        }
        else{
            LogUtil.e("delete", "hotel");
            if (getChildFragmentManager().findFragmentByTag("0Reservation") != null) {
                LogUtil.e("delete", "hotel1");
                childFt.remove(getChildFragmentManager().findFragmentByTag("0Reservation"));
            }
            LogUtil.e("delete", "hotel2");
        }
//        childFt.addToBackStack(null);
        childFt.commitAllowingStateLoss();
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
