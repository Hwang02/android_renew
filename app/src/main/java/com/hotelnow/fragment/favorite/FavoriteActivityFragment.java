package com.hotelnow.fragment.favorite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.FavoriteActivityAdapter;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.model.FavoriteStayItem;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteActivityFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ArrayList<FavoriteStayItem> mItems = new ArrayList<>();
    private FavoriteActivityAdapter adapter;
    private Button btn_go_login;
    private RelativeLayout main_view;
    private TextView btn_go_list, btnCancel;
    private DbOpenHelper dbHelper;
    public Context mActivity;
    private DialogConfirm dialogConfirm;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_a, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        init();
    }

    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
//            paramObj.put("ui", AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", "")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(getActivity()));
        } catch (Exception e) {
        }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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

                        mlist.setEmptyView(getView().findViewById(R.id.login_view));
                        getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.white);
                        btn_go_login.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        isdelete(false);
                        MainActivity.hideProgress();
                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        btn_go_list.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                ((MainActivity) getActivity()).setTapMove(6, true);
                            }
                        });

                        getFavorite();
                    }
                } catch (Exception e) {
                    MainActivity.hideProgress();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFavorite() {
        MainActivity.showProgress();
        String url = CONFIG.like_list + "?only_id=N&type=activity";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (obj.getJSONArray("activity").length() > 0) {
                        final JSONArray list = obj.getJSONArray("activity");
                        JSONObject entry = null;
                        dbHelper.deleteFavoriteItem(true, "", "A");

                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new FavoriteStayItem(
                                    entry.getString("id"),
                                    entry.getString("name"),
                                    entry.getString("category"),
                                    entry.getString("location"),
                                    entry.has("street2") ? entry.getString("street2") : "",
                                    entry.getString("img_url"),
                                    entry.getString("sale_price"),
                                    entry.getString("sale_rate"),
                                    entry.has("items_quantity") ? entry.getInt("items_quantity") : 0,
                                    entry.getString("benefit_text"),
                                    entry.getString("grade_score"),
                                    entry.getString("real_grade_score"),
                                    entry.has("is_private_deal") ? "Y" : "N",
                                    entry.getString("is_hot_deal"),
                                    entry.getString("is_add_reserve"),
                                    entry.getInt("coupon_count")
                            ));

                            dbHelper.insertFavoriteItem(entry.getString("id"), "A");
                        }
                        isdelete(true);
                    } else {
                        getView().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        isdelete(false);
                    }
                    adapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.hideProgress();
                        }
                    }, 500);
                } catch (Exception e) {
                    MainActivity.hideProgress();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 80) {
            ((FavoriteFragment)getParentFragment()).setChildDelete(1);
            authCheck();
            ((MainActivity) getActivity()).setTitle();
            ((MainActivity) getActivity()).setTapdelete("MYPAGE");
            CONFIG.TabLogin = true;
        } else if (requestCode == 70 && resultCode == 80) {
            mItems.clear();
            adapter.notifyDataSetChanged();
            MainActivity.showProgress();
            getFavorite();
        }
    }

    public void setLike(final int position) {
        MainActivity.showProgress();
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }

        Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        ((MainActivity) getActivity()).showToast("로그인 후 이용해주세요");
                        return;
                    }

                    dbHelper.deleteFavoriteItem(false, sel_id, "A");
                    LogUtil.e("xxxx", "찜하기 취소");
                    ((MainActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                    mItems.clear();
                    getFavorite();
                    MainActivity.hideProgress();
                } catch (JSONException e) {
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public void setDeleteAll() {
        ((MainActivity) mActivity).showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("all_flag", "Y");
            paramObj.put("type", "activity");

        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }

        Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                ((MainActivity) mActivity).hideProgress();
                Toast.makeText(((MainActivity) mActivity), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        ((MainActivity) mActivity).hideProgress();
                        ((MainActivity) mActivity).showToast("로그인 후 이용해주세요");
                        return;
                    }

                    if(dbHelper == null) {
                        dbHelper = new DbOpenHelper(mActivity);
                    }

                    dbHelper.deleteFavoriteItem(true, "", "A");
                    LogUtil.e("xxxx", "찜하기 전체 취소");
                    ((MainActivity) mActivity).showToast("관심 상품 삭제 완료");
                    mItems.clear();
                    getFavorite();
                    ((MainActivity) mActivity).hideProgress();
                } catch (JSONException e) {
                    ((MainActivity) mActivity).hideProgress();
                }
            }
        });
    }

    private void init() {
        // preference

        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        adapter = new FavoriteActivityAdapter(getActivity(), FavoriteActivityFragment.this, 0, mItems);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_list = (TextView) getView().findViewById(R.id.btn_go_list);
        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), DetailActivityActivity.class);
                intent.putExtra("tid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 70);
            }
        });
        CONFIG.sel_reserv = 0;
        btnCancel = (TextView) getView().findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                allDelete();
            }
        });
        authCheck();
    }

    private void allDelete() {
        dialogConfirm = new DialogConfirm("삭제", "전체 관심 저장 상품을 삭제하시겠습니까?", "취소", "확인", getActivity(), new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialogConfirm.dismiss();
            }
        }, new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setDeleteAll();
                dialogConfirm.dismiss();
            }
        });

        dialogConfirm.show();
    }

    private void isdelete(boolean isdelete) {
        boolean isuser = _preferences.getString("userid", null) != null ? true : false;
        if(isuser){
            if (isdelete) {
                getView().findViewById(R.id.view_filter).setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                getView().findViewById(R.id.view_filter).setVisibility(View.GONE);
            }

        } else {
            getView().findViewById(R.id.view_filter).setVisibility(View.GONE);
        }
    }
}
