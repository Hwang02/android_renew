package com.hotelnow.fragment.favorite;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.CalendarSingleActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.FavoriteAdapter;
import com.hotelnow.databinding.FragmentFavoriteBinding;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding mFavoriteBinding;
    private DbOpenHelper dbHelper;
    private FavoriteAdapter favoriteAdapter;
    private String ec_date, ee_date;
    private int m_Selecttab = 0;
    private DialogConfirm dialogConfirm;

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
                            mFavoriteBinding.btnDate.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    Api.get(CONFIG.server_time, new Api.HttpCallback() {
                                        @Override
                                        public void onFailure(Response response, Exception throwable) {
                                            Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        @Override
                                        public void onSuccess(Map<String, String> headers, String body) {
                                            try {
                                                JSONObject obj = new JSONObject(body);
                                                if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                                    long time = obj.getInt("server_time") * (long) 1000;

                                                    CONFIG.svr_date = new Date(time);
                                                    Intent intent = new Intent(getContext(), CalendarActivity.class);
                                                    intent.putExtra("ec_date", ec_date);
                                                    intent.putExtra("ee_date", ee_date);
                                                    startActivityForResult(intent, 80);
                                                }
                                            }
                                            catch (Exception e){}
                                        }
                                    });
                                }
                            });
                            mFavoriteBinding.btnCancel.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    allDelete(0);
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
                            mFavoriteBinding.btnDate.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    Intent intent = new Intent(getContext(), CalendarSingleActivity.class);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    startActivityForResult(intent, 80);
                                }
                            });

                            mFavoriteBinding.btnCancel.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    allDelete(1);
                                }
                            });
                        }
                    },100);

        }

        favoriteAdapter = new FavoriteAdapter(getActivity(), getChildFragmentManager());
        mFavoriteBinding.viewPager.setAdapter(favoriteAdapter);

        mFavoriteBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                mFavoriteBinding.viewPager.setCurrentItem(tab.getPosition(), true);
//                m_Selecttab = tab.getPosition();
                if (tab.getPosition() == 0) {
                    mFavoriteBinding.tvDateTitle.setVisibility(View.VISIBLE);
                    mFavoriteBinding.tvDate.setVisibility(View.VISIBLE);
                    mFavoriteBinding.btnDate.setVisibility(View.VISIBLE);
                    mFavoriteBinding.tvDateTitle.setText("숙박일 선택");
                    mFavoriteBinding.tvDate.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date));
                    mFavoriteBinding.btnDate.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            Api.get(CONFIG.server_time, new Api.HttpCallback() {
                                @Override
                                public void onFailure(Response response, Exception throwable) {
                                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);
                                        if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                            long time = obj.getInt("server_time") * (long) 1000;
                                            CONFIG.svr_date = new Date(time);
                                            Intent intent = new Intent(getContext(), CalendarActivity.class);
                                            intent.putExtra("ec_date", ec_date);
                                            intent.putExtra("ee_date", ee_date);
                                            startActivityForResult(intent, 80);
                                        }
                                    }
                                    catch (Exception e){}
                                }
                            });
                        }
                    });
                }

                mFavoriteBinding.btnCancel.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        allDelete(tab.getPosition());
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void isdelete(boolean isdelete){
        if(mFavoriteBinding.tabLayout.getSelectedTabPosition() == 0) {
            if (isdelete) {
                mFavoriteBinding.tvDateTitle.setVisibility(View.VISIBLE);
                mFavoriteBinding.btnDate.setVisibility(View.VISIBLE);
                mFavoriteBinding.viewFilter.setVisibility(View.VISIBLE);
                mFavoriteBinding.btnCancel.setVisibility(View.VISIBLE);
            } else {
                mFavoriteBinding.btnCancel.setVisibility(View.GONE);
            }
        }
        else {
            if (isdelete) {
                mFavoriteBinding.tvDateTitle.setVisibility(View.GONE);
                mFavoriteBinding.btnDate.setVisibility(View.GONE);
                mFavoriteBinding.viewFilter.setVisibility(View.VISIBLE);
                mFavoriteBinding.btnCancel.setVisibility(View.VISIBLE);
            } else {
                mFavoriteBinding.btnCancel.setVisibility(View.GONE);
            }
        }
    }

    private void allDelete(final int tab){

        dialogConfirm = new DialogConfirm("삭제", "전체 관심 저장 상품을 삭제하시겠습니까?", "취소", "확인", getActivity(), new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialogConfirm.dismiss();
            }
        }, new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(tab == 0){
                    FavoriteHotelFragment f = (FavoriteHotelFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
                    f.setDeleteAll();
                }
                else{
                    FavoriteActivityFragment f = (FavoriteActivityFragment) mFavoriteBinding.viewPager.getAdapter().instantiateItem(mFavoriteBinding.viewPager, mFavoriteBinding.viewPager.getCurrentItem());
                    f.setDeleteAll();
                }
                dialogConfirm.dismiss();
            }
        });

        dialogConfirm.show();
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
