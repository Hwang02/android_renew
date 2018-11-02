package com.hotelnow.fragment.favorite;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelnow.R;
import com.hotelnow.adapter.FavoriteAdapter;
import com.hotelnow.databinding.FragmentFavoriteBinding;
import com.hotelnow.utils.DbOpenHelper;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding mFavoriteBinding;
    private DbOpenHelper dbHelper;
    private FavoriteAdapter favoriteAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mFavoriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        View inflate = mFavoriteBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DbOpenHelper(getActivity());
        mFavoriteBinding.tabLayout.addTab(mFavoriteBinding.tabLayout.newTab().setText("숙소"));
        mFavoriteBinding.tabLayout.addTab(mFavoriteBinding.tabLayout.newTab().setText("액티비티"));
        mFavoriteBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        if(m_Selecttab == 0) {
//            tabLayout.getTabAt(0).select();
//        }
//        else {
//            tabLayout.getTabAt(1).select();
//        }

        favoriteAdapter = new FavoriteAdapter(getActivity(), getChildFragmentManager());
        mFavoriteBinding.viewPager.setAdapter(favoriteAdapter);

        mFavoriteBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mFavoriteBinding.viewPager.setCurrentItem(tab.getPosition());
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
