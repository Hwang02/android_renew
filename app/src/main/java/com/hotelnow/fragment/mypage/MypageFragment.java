package com.hotelnow.fragment.mypage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.MyCardActivity;
import com.hotelnow.activity.MyCouponActivity;
import com.hotelnow.activity.MySaveActivity;
import com.hotelnow.activity.SettingActivity;
import com.hotelnow.activity.SignupActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.databinding.FragmentMypageBinding;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

public class MypageFragment extends Fragment {

    private FragmentMypageBinding mMypageBinding;
    private ArrayList<Object> objects = new ArrayList<>();
    private SharedPreferences _preferences;
    private DialogConfirm dialogConfirm;
    private String expire_money, save_money;
    private DbOpenHelper dbHelper;
    private boolean isPush = false;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mMypageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage, container, false);
        View inflate = mMypageBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());
        mMypageBinding.join.rlJoin.setVisibility(View.GONE);

        // 설정하기
        mMypageBinding.center.btnSetting.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("isPush", isPush);
                intent.putExtra("isEmail", _preferences.getString("marketing_email_yn", "N"));
                intent.putExtra("isSms", _preferences.getString("marketing_sms_yn", "N"));
                startActivityForResult(intent, 91);
            }
        });

        // 비활성화
        mMypageBinding.notJoin.rlNotJoin.setVisibility(View.GONE);
        mMypageBinding.join.rlJoin.setVisibility(View.GONE);

        // 운영시간
        mMypageBinding.center.infoTime.setText("운영시간 "+CONFIG.operation_time);

        // 공지사항
        mMypageBinding.center.btnNotice.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_notice);
                intent.putExtra("title", getString(R.string.notice));
                startActivity(intent);
            }
        });

        // faq
        mMypageBinding.center.btnFaq.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_faq);
                intent.putExtra("title", getString(R.string.cs_faq));
                startActivity(intent);
            }
        });

        // 이메일로 의견
        mMypageBinding.center.btnEmail.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String userId = "";
                try {
                    userId = Util.decode(_preferences.getString("userid", null).replace("HN|",""));
                } catch (UnsupportedEncodingException e) {
                    userId ="";
                    e.printStackTrace();
                }

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{CONFIG.setting_email});
                i.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n---------------------------------------\nver:" + Util.getAppVersionName(getActivity()) + ";" + Util.getUserAgent(getActivity()) + ";" + userId);

                try {
                    startActivity(Intent.createChooser(i, getString(R.string.cs_txt2)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.no_email_program), Toast.LENGTH_SHORT).show();
                    userId="";
                }
                userId="";
            }
        });

        // cs - 카카오
        mMypageBinding.center.csKakao.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.kakaoYelloId2(getActivity());
            }
        });

        // cs - 전화
        mMypageBinding.center.csPhone.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialogConfirm = new DialogConfirm(
                        getString(R.string.alert_notice),
                        getString(R.string.wanna_call_hotelnow)+"\n운영시간 : "+CONFIG.operation_time,
                        getString(R.string.alert_no),
                        getString(R.string.alert_connect),
                        getActivity(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + CONFIG.csPhoneNum)));

                                dialogConfirm.dismiss();
                            }
                        });
                dialogConfirm.setCancelable(false);
                dialogConfirm.show();
            }
        });

        //하단 펼치기
        mMypageBinding.footer.tvMore.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(mMypageBinding.footer.tvCompanyinfo.getVisibility() == View.VISIBLE){
                    mMypageBinding.footer.tvCompanyinfo.setVisibility(View.GONE);
                    mMypageBinding.footer.lvMore.setBackgroundResource(R.drawable.btn_detail_open_grey);
                }
                else {
                    mMypageBinding.footer.tvCompanyinfo.setVisibility(View.VISIBLE);
                    mMypageBinding.footer.lvMore.setBackgroundResource(R.drawable.btn_detail_close_grey);
                }
            }
        });

        // 서비스 이용약관
        mMypageBinding.footer.term1.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree1);
                intent.putExtra("title", getString(R.string.term_txt1));
                startActivity(intent);
            }
        });

        // 개인정보 취급방침
        mMypageBinding.footer.term2.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree2);
                intent.putExtra("title", getString(R.string.term_txt2));
                startActivity(intent);
            }
        });

        // 위치기반서비스 이용약관
        mMypageBinding.footer.term3.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree3);
                intent.putExtra("title", getString(R.string.term_txt3));
                startActivity(intent);
            }
        });

        // 앱버전
        mMypageBinding.center.txtVer.setText("V"+Util.getAppVersionName(getActivity()));

        // 회원가입
        mMypageBinding.notJoin.btJoin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                startActivityForResult(intent, 90);
            }
        });

        // 로그인
        mMypageBinding.notJoin.btLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 90);
            }
        });

        //로그아웃
        mMypageBinding.join.tvLogout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialogConfirm = new DialogConfirm(
                        getString(R.string.alert_notice),
                        getString(R.string.wanna_logout),
                        getString(R.string.alert_no),
                        getString(R.string.alert_yes),
                        getActivity(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Api.get(CONFIG.logoutUrl, new Api.HttpCallback() {
                                    @Override
                                    public void onFailure(Response response, Exception e) {
                                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(Map<String, String> headers, String body) {
                                        try {
                                            JSONObject obj = new JSONObject(body);

                                            if (!obj.getString("result").equals("success")) {
                                                Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            String moreinfo = (obj.has("_umi") == true) ? obj.getString("_umi") : "";

                                            SharedPreferences.Editor prefEditor = _preferences.edit();
                                            prefEditor.putString("email", null);
                                            prefEditor.putString("username", null);
                                            prefEditor.putString("phone", null);
                                            prefEditor.putString("userid", null);
                                            prefEditor.putString("moreinfo", moreinfo);
                                            prefEditor.putString("utype", null);
                                            prefEditor.commit();

                                            ((MainActivity)getActivity()).setTitle();
                                            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.VISIBLE);
                                            mMypageBinding.join.rlJoin.setVisibility(View.GONE);

                                            dbHelper.deleteFavoriteItem(true,"","");
                                            ((MainActivity)getActivity()).moveTabRefresh();
                                            ((MainActivity)getActivity()).moveTabRefresh2();
                                            ((MainActivity)getActivity()).moveTabRefresh3();

                                            try{
                                                LoginManager.getInstance().logOut();
                                            } catch (Exception e) {}

                                        } catch (Exception e) {
                                        }
                                    }
                                });
                                dialogConfirm.dismiss();
                            }
                        });
                dialogConfirm.setCancelable(false);
                dialogConfirm.show();
            }
        });

        // 적립금 상세
        mMypageBinding.join.llMoney.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), MySaveActivity.class);
                startActivity(intent);
            }
        });

        // 등록 카드 상세
        mMypageBinding.join.llCard.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), MyCardActivity.class);
                startActivityForResult(intent, 91);
            }
        });
        // 쿠폰 상세
        mMypageBinding.join.llCoupon.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                startActivityForResult(intent, 91);
            }
        });


        authCheck();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 90 && resultCode == 90) { // 로그인
            authCheck();
            ((MainActivity)getActivity()).setTitle();
        }
        if (requestCode == 91 && resultCode == 91) { // 쿠폰, 카드
            checkLogin();
        }
    }
    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", Util.decode(_preferences.getString("userid", null).replace("HN|","")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                isPush = false;
                if(getActivity() == null) {
                    Util.doRestart(HotelnowApplication.getAppContext());
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

                    }

                    checkLogin();

                } catch (Exception e) {
                    isPush = false;
                    MainActivity.hideProgress();
                    if(getActivity() != null && isAdded()){
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkLogin() {
        // 로그인 확인 후 레이아웃 노출 여부 처리
        String userid = _preferences.getString("userid", null);

        JSONObject params = new JSONObject();
        try{
            params.put("uuid", Util.getAndroidId(getActivity()));
        } catch (JSONException e) {}

        if (userid == null) {
            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.VISIBLE);
            mMypageBinding.join.rlJoin.setVisibility(View.GONE);

            // 푸시 사용설정
            Api.post(CONFIG.notiStatusUrl, params.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    isPush = false;
                    MainActivity.hideProgress();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        isPush = obj.getString("status").toUpperCase().equals("Y") ? true : false;
                        MainActivity.hideProgress();
                    } catch (Exception e) {
                        isPush = false;
                        MainActivity.hideProgress();
                    }
                }
            });
        } else {
            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.GONE);
            mMypageBinding.join.rlJoin.setVisibility(View.VISIBLE);

            Api.post(CONFIG.recommendInfoUrl, params.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    if(getActivity() != null && isAdded())
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            if(getActivity() != null && isAdded())
                                Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            MainActivity.hideProgress();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");

                        mMypageBinding.join.userName.setText(_preferences.getString("username", null));
                        mMypageBinding.join.userEmail.setText(_preferences.getString("email", null));
                        save_money = Util.numberFormat(data.getInt("amount"));
                        mMypageBinding.join.tvUserSaveMoney.setText(save_money + "원");
                        mMypageBinding.join.tvUserCoupon.setText(data.getString("coupon_cnt_new") + "장");
                        mMypageBinding.join.tvUserCard.setText(data.getString("card_cnt") + "장");
                        expire_money = Util.numberFormat(data.getInt("expire_amount"));
                        mMypageBinding.join.disableMoney.setText(expire_money+"원");
                        mMypageBinding.join.disableCoupon.setText(data.getInt("expire_coupon_cnt")+"장");
                        if(expire_money.equals("0")) {
                            mMypageBinding.join.layoutDiscountMoney.setVisibility(View.GONE);
                        }
                        else {
                            mMypageBinding.join.layoutDiscountMoney.setVisibility(View.VISIBLE);
                        }

                        if(data.getInt("expire_coupon_cnt") == 0){
                            mMypageBinding.join.layoutDisableCoupon.setVisibility(View.GONE);
                        }
                        else{
                            mMypageBinding.join.layoutDisableCoupon.setVisibility(View.VISIBLE);
                        }


                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("marketing_email_yn", obj.getString("marketing_email_yn"));
                        prefEditor.putString("marketing_sms_yn", obj.getString("marketing_sms_yn"));
                        prefEditor.commit();

                        // 푸시 사용설정
                        if(obj.getString("status").toUpperCase().equals("Y"))
                            isPush = true;
                        else
                            isPush = false;
                        MainActivity.hideProgress();
                    } catch (Exception e) {
                        Log.e(CONFIG.TAG, e.toString());
                        if(getActivity() != null && isAdded()){
                            Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.hideProgress();
                    }
                }
            });
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
}
