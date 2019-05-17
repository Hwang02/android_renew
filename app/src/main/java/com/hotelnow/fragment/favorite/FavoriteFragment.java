package com.hotelnow.fragment.favorite;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.hotelnow.databinding.FragmentFavoriteBinding;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding mFavoriteBinding;
    private DbOpenHelper dbHelper;
    private int m_Selecttab = 0;
    private SharedPreferences _preferences;
    private View inflate;
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

        mFavoriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        inflate = mFavoriteBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        mFavoriteBinding.tabLayout.addTab(mFavoriteBinding.tabLayout.newTab().setText("숙소"));
        mFavoriteBinding.tabLayout.addTab(mFavoriteBinding.tabLayout.newTab().setText("액티비티"));
        mFavoriteBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // 1번 탭 일때
        m_Selecttab = CONFIG.sel_fav;
        mFavoriteBinding.tabLayout.getTabAt(m_Selecttab).select();

        if (m_Selecttab == 0) {
            TuneWrap.Event("favorite_stay");
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            fg = new FavoriteHotelFragment();
                            setChildFragment(fg, m_Selecttab);
                        }
                    }, 100);
        } else {
            TuneWrap.Event("favorite_activity");
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            fg = new FavoriteActivityFragment();
                            setChildFragment(fg, m_Selecttab);
                        }
                    }, 100);
        }

        mFavoriteBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TuneWrap.Event("favorite_stay");
                    fg = new FavoriteHotelFragment();
                }
                else {
                    TuneWrap.Event("favorite_activity");
                    fg = new FavoriteActivityFragment();
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
            if (getChildFragmentManager().findFragmentByTag("1favorite") != null) {
                childFt.hide(getChildFragmentManager().findFragmentByTag("1favorite"));
            }
            if (getChildFragmentManager().findFragmentByTag("0favorite") == null) {
                childFt.add(R.id.view_pager, child, "0favorite");
                LogUtil.e("view", "hotel");
            }
            else{
                childFt.show(getChildFragmentManager().findFragmentByTag("0favorite"));
                LogUtil.e("view", "hotel1");
            }
        }
        else{
            if (getChildFragmentManager().findFragmentByTag("0favorite") != null) {
                childFt.hide(getChildFragmentManager().findFragmentByTag("0favorite"));
            }
            if (getChildFragmentManager().findFragmentByTag("1favorite") == null) {
                childFt.add(R.id.view_pager, child, "1favorite");
                LogUtil.e("view", "activity");
            } else {
                childFt.show(getChildFragmentManager().findFragmentByTag("1favorite"));
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
            if (getChildFragmentManager().findFragmentByTag("1favorite") != null) {
                LogUtil.e("delete", "activity1");
                childFt.remove(getChildFragmentManager().findFragmentByTag("1favorite"));
            }
            LogUtil.e("delete", "activity2");
        }
        else{
            LogUtil.e("delete", "hotel");
            if (getChildFragmentManager().findFragmentByTag("0favorite") != null) {
                LogUtil.e("delete", "hotel1");
                childFt.remove(getChildFragmentManager().findFragmentByTag("0favorite"));
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
