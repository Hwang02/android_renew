package com.hotelnow.fragment.reservation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.ReservationHotelDetailActivity;
import com.hotelnow.adapter.ReservationHotelAdapter;
import com.hotelnow.fragment.favorite.FavoriteFragment;
import com.hotelnow.fragment.model.BookingEntry;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ReservationHotelFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ReservationHotelAdapter adapter;
    private Button btn_go_login, u_send;
    private ImageView back;
    private RelativeLayout main_view;
    private TextView btn_go_reservation;
    private ArrayList<BookingEntry> mEntries = new ArrayList<BookingEntry>();
    private EditText u_name, u_tel, u_num;
    private int total_count = 0;
    private int currentPage = 1;
    private boolean isAdd = true;
    private boolean _hasLoadedOnce = false; // your boolean field
    boolean firstDragFlag = true;
    boolean dragFlag = false;   //현재 터치가 드래그 인지 확인
    float startYPosition = 0, endYPosition = 0;       //터치이벤트의 시작점의 Y(세로)위치
    private NestedScrollView scroll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation_h, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", "")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(getActivity()));
        } catch (Exception e) {
        }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                if (isAdded()) {
                    MainActivity.hideProgress();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();
                        if (!CONFIG.Mypage_Search) {
                            mlist.setEmptyView(getView().findViewById(R.id.login_view));
                            getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                        } else {
                            mlist.setEmptyView(getView().findViewById(R.id.reserv_view));
                            getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                            CONFIG.Mypage_Search = false;
                        }
                        main_view.setBackgroundResource(R.color.white);
                        btn_go_login.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        btn_go_reservation.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.VISIBLE);

                            }
                        });
                        u_send.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                if (!TextUtils.isEmpty(u_name.getText().toString()) && !TextUtils.isEmpty(u_tel.getText().toString()) && !TextUtils.isEmpty(u_num.getText().toString())) {
                                    Intent intent = new Intent(getActivity(), ReservationHotelDetailActivity.class);
                                    intent.putExtra("user_name", u_name.getText().toString());
                                    intent.putExtra("user_phone", u_tel.getText().toString());
                                    intent.putExtra("bid", u_num.getText().toString());
                                    intent.putExtra("title", "비회원 예약조회");
                                    startActivityForResult(intent, 80);
                                } else {
                                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        back.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                mlist.setEmptyView(getView().findViewById(R.id.login_view));
                                getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                            }
                        });
                        MainActivity.hideProgress();
                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        mlist.getEmptyView().findViewById(R.id.tv_info1).setVisibility(View.GONE);
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        getBookingList();
                    }
                } catch (Exception e) {
                    if (isAdded()) {
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        MainActivity.hideProgress();
                    }
                }
            }
        });
    }

    public void getBookingList() {

        String url = CONFIG.bookingListUrl + "?page=" + currentPage + "&per_page=10000";
        if (isAdd) {
            Api.get(url, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            MainActivity.hideProgress();
                            return;
                        }

                        total_count = obj.getInt("total_count");

                        JSONArray feed = obj.getJSONArray("lists");
                        JSONObject entry;
                        if (feed.length() > 0) {
                            isAdd = true;
                            currentPage++;
                            for (int i = 0; i < feed.length(); i++) {
                                entry = feed.getJSONObject(i);
                                mEntries.add(new BookingEntry(
                                        entry.getString("id"),
                                        entry.getString("status"),
                                        entry.getString("hotel_name"),
                                        entry.getString("room_name"),
                                        entry.getString("room_img"),
                                        entry.getString("checkin_date"),
                                        entry.getString("checkout_date"),
                                        entry.getString("room_id"),
                                        entry.getString("hotel_id"),
                                        entry.getInt("myreview_cnt"),
                                        entry.getString("is_review_writable"),
                                        entry.getString("status_display"),
                                        entry.getString("review_writable_words_1"),
                                        entry.getString("review_writable_words_2"),
                                        entry.getString("status_detail"))
                                );
                            }
                            if (feed.length() > 1) {
                                scroll.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent ev) {

                                        switch (ev.getAction()) {
                                            case MotionEvent.ACTION_MOVE:       //터치를 한 후 움직이고 있으면
                                                dragFlag = true;
                                                if (firstDragFlag) {     //터치후 계속 드래그 하고 있다면 ACTION_MOVE가 계속 일어날 것임으로 무브를 시작한 첫번째 터치만 값을 저장함
                                                    startYPosition = ev.getY(); //첫번째 터치의 Y(높이)를 저장
                                                    firstDragFlag = false;   //두번째 MOVE가 실행되지 못하도록 플래그 변경
                                                }

                                                break;

                                            case MotionEvent.ACTION_UP:
                                                endYPosition = ev.getY();
                                                firstDragFlag = true;

                                                if (dragFlag) {  //드래그를 하다가 터치를 실행

                                                    // 시작Y가 끝 Y보다 크다면 터치가 아래서 위로 이루어졌다는 것이고, 스크롤은 아래로내려갔다는 뜻이다.
                                                    // (startYPosition - endYPosition) > 10 은 터치로 이동한 거리가 10픽셀 이상은 이동해야 스크롤 이동으로 감지하겠다는 뜻임으로 필요하지 않으면 제거해도 된다.
                                                    if ((startYPosition > endYPosition) && (startYPosition - endYPosition) > 10) {
                                                        //TODO 스크롤 다운 시 작업
                                                        LogUtil.e("xxxxxxx", "down");
                                                        ((ReservationFragment) getParentFragment()).toolbarAnimateHide();
                                                    }
                                                    //시작 Y가 끝 보다 작다면 터치가 위에서 아래로 이러우졌다는 것이고, 스크롤이 올라갔다는 뜻이다.
                                                    else if ((startYPosition < endYPosition) && (endYPosition - startYPosition) > 10) {
                                                        //TODO 스크롤 업 시 작업
                                                        LogUtil.e("xxxxxxx", "up");
                                                        ((ReservationFragment) getParentFragment()).toolbarAnimateShow(0);
                                                    }
                                                }

                                                startYPosition = 0.0f;
                                                endYPosition = 0.0f;
                                                break;
                                        }
                                        return false;
                                    }

                                });
                            }
                            adapter.notifyDataSetChanged();
                            ((ReservationFragment) getParentFragment()).setInfobar();
                        } else {
                            mlist.getEmptyView().findViewById(R.id.tv_info1).setVisibility(View.VISIBLE);
                            isAdd = false;
                        }

                        if (total_count == mEntries.size()) {
                            isAdd = false;
                        }

                        MainActivity.hideProgress();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        MainActivity.hideProgress();
                    }
                }
            });
        } else {
            MainActivity.hideProgress();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 80) {
            authCheck();
            ((MainActivity) getActivity()).setTitle();
            ((MainActivity) getActivity()).setTapdelete("MYPAGE");
            CONFIG.TabLogin = true;
            ((ReservationFragment) getParentFragment()).setInfobar();
        } else if (requestCode == 90 && resultCode == 0) {
            mEntries.clear();
            adapter.notifyDataSetChanged();
            currentPage = 1;
            isAdd = true;
            MainActivity.showProgress();
            getBookingList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
        // we check that the fragment is becoming visible
        if (isFragmentVisible_ && !_hasLoadedOnce) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 500);
            _hasLoadedOnce = true;
        } else if (isFragmentVisible_ && CONFIG.TabLogin && _hasLoadedOnce) {
            CONFIG.TabLogin = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 500);
        } else if (isFragmentVisible_ && !CONFIG.TabLogin && _hasLoadedOnce) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (_preferences.getString("userid", null) != null) {
                        mEntries.clear();
                        adapter.notifyDataSetChanged();
                        currentPage = 1;
                        isAdd = true;
                        MainActivity.showProgress();
                        getBookingList();
                    }
                }
            }, 500);
        }
    }

    private void init() {
        // preference

        TuneWrap.Event("booking_stay");

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        scroll = (NestedScrollView) getView().findViewById(R.id.scroll);
        adapter = new ReservationHotelAdapter(getActivity(), 0, mEntries, _preferences.getString("userid", ""), ReservationHotelFragment.this);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_reservation = (TextView) getView().findViewById(R.id.btn_go_reservation);
        u_send = (Button) getView().findViewById(R.id.u_send);
        back = (ImageView) getView().findViewById(R.id.back);
        u_name = (EditText) getView().findViewById(R.id.u_name);
        u_tel = (EditText) getView().findViewById(R.id.u_tel);
        u_num = (EditText) getView().findViewById(R.id.u_num);

        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.hid);

                TuneWrap.Event("booking_stay_detail", tv.getText().toString());
                Intent intent = new Intent(getActivity(), ReservationHotelDetailActivity.class);
                intent.putExtra("bid", tv.getText().toString());
                startActivityForResult(intent, 90);
            }
        });
        if (CONFIG.sel_reserv == 0)
            authCheck();
    }
}
