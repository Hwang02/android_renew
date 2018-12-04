package com.hotelnow.fragment.favorite;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelnow.R;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.CalendarSingleActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.FavoriteAdapter;
import com.hotelnow.databinding.FragmentFavoriteBinding;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.Util;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding mFavoriteBinding;
    private DbOpenHelper dbHelper;
    private FavoriteAdapter favoriteAdapter;
    private String ec_date, ee_date;
    private int m_Selecttab = 0;

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
        ec_date = Util.setCheckinout().get(0);
        ee_date = Util.setCheckinout().get(1);

        // 1번 탭 일때
        if(m_Selecttab == 0) {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteBinding.tabLayout.getTabAt(0).select();
                            mFavoriteBinding.viewPager.setCurrentItem(0);
                            mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date) +" - "+Util.formatchange5(ee_date));
                            mFavoriteBinding.btnDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), CalendarActivity.class);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    startActivityForResult(intent, 80);
                                }
                            });
                            mFavoriteBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    allDelete();
                                }
                            });
                        }
                    }, 100);
        }
        else {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteBinding.tabLayout.getTabAt(1).select();
                            mFavoriteBinding.viewPager.setCurrentItem(1);
                            mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date));
                            mFavoriteBinding.btnDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), CalendarSingleActivity.class);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    startActivityForResult(intent, 80);
                                }
                            });

                            mFavoriteBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    allDelete();
                                }
                            });
                        }
                    },100);

        }

        favoriteAdapter = new FavoriteAdapter(getActivity(), getChildFragmentManager());
        mFavoriteBinding.viewPager.setAdapter(favoriteAdapter);

        mFavoriteBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mFavoriteBinding.viewPager.setCurrentItem(tab.getPosition());
//                m_Selecttab = tab.getPosition();
                if(tab.getPosition() == 0){
                    mFavoriteBinding.tvDateTitle.setText("숙박일 선택");
                    mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date) +" - "+Util.formatchange5(ee_date));
                    mFavoriteBinding.btnDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), CalendarActivity.class);
                            intent.putExtra("ec_date", ec_date);
                            intent.putExtra("ee_date", ee_date);
                            startActivityForResult(intent, 80);
                        }
                    });
                }
                else if(tab.getPosition() == 1){
                    mFavoriteBinding.tvDateTitle.setText("방문일 선택");
                    mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date));
                    mFavoriteBinding.btnDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), CalendarSingleActivity.class);
                            intent.putExtra("ec_date", ec_date);
                            intent.putExtra("ee_date", ee_date);
                            startActivityForResult(intent, 80);
                        }
                    });
                }
                if(CONFIG.TabLogin) {
                    favoriteAdapter.notifyDataSetChanged();
                    CONFIG.TabLogin=false;
                }

                mFavoriteBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allDelete();
                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setCancelView(boolean isEmpty){
        if(isEmpty) {
            mFavoriteBinding.btnCancel.setVisibility(View.GONE);
        }
        else{
            mFavoriteBinding.btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void allDelete(){
        if(m_Selecttab == 0){
            FavoriteHotelFragment f = (FavoriteHotelFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
            f.setDeleteAll();
        }
        else{
            FavoriteActivityFragment f = (FavoriteActivityFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
            f.setDeleteAll();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 80){
            if(m_Selecttab == 0) {
                ec_date = data.getStringExtra("ec_date");
                ee_date = data.getStringExtra("ee_date");
                mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date));
                FavoriteHotelFragment f = (FavoriteHotelFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
                f.setDateRefresh(ec_date, ee_date);
            }
            else{
                ec_date = data.getStringExtra("ec_date");
                mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date));
                FavoriteActivityFragment f2 = (FavoriteActivityFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
                f2.setDateRefresh("","");
            }
        }
    }
}
