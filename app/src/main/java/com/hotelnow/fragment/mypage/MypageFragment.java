package com.hotelnow.fragment.mypage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MyCardActivity;
import com.hotelnow.activity.MyCouponActivity;
import com.hotelnow.activity.MySaveActivity;
import com.hotelnow.activity.SignupActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.adapter.HomeAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.databinding.FragmentMypageBinding;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MypageFragment extends Fragment {

    private FragmentMypageBinding mMypageBinding;
    private ArrayList<Object> objects = new ArrayList<>();
    private SharedPreferences _preferences;
    private DialogConfirm dialogConfirm;
    private String expire_money, save_money;

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

        mMypageBinding.join.rlJoin.setVisibility(View.GONE);

        // 푸시
        mMypageBinding.center.acceptPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMypageBinding.center.acceptPush.setChecked(!mMypageBinding.center.acceptPush.isChecked());
                setPush();
            }
        });

        // 비활성화
        mMypageBinding.notJoin.rlNotJoin.setVisibility(View.GONE);
        mMypageBinding.join.rlJoin.setVisibility(View.GONE);

        // 운영시간
        mMypageBinding.center.infoTime.setText("운영시간 "+CONFIG.operation_time);

        // 공지사항
        mMypageBinding.center.btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_notice);
                intent.putExtra("title", getString(R.string.notice));
                startActivity(intent);
            }
        });

        // faq
        mMypageBinding.center.btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_faq);
                intent.putExtra("title", getString(R.string.cs_faq));
                startActivity(intent);
            }
        });

        // 이메일로 의견
        mMypageBinding.center.btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = _preferences.getString("userid", null);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{CONFIG.setting_email});
                i.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n---------------------------------------\nver:" + Util.getAppVersionName(getActivity()) + ";" + Util.getUserAgent(getActivity()) + ";" + userId);

                try {
                    startActivity(Intent.createChooser(i, getString(R.string.cs_txt2)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getString(R.string.no_email_program), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // cs - 카카오
        mMypageBinding.center.csKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.kakaoYelloId2(getActivity());
            }
        });

        // cs - 전화
        mMypageBinding.center.csPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        mMypageBinding.footer.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMypageBinding.footer.tvCompanyinfo.getVisibility() == View.VISIBLE){
                    mMypageBinding.footer.tvCompanyinfo.setVisibility(View.GONE);
                    mMypageBinding.footer.lvMore.setBackgroundResource(R.drawable.btn_detail_close_grey);
                }
                else {
                    mMypageBinding.footer.tvCompanyinfo.setVisibility(View.VISIBLE);
                    mMypageBinding.footer.lvMore.setBackgroundResource(R.drawable.btn_detail_open_grey);
                }
            }
        });

        // 서비스 이용약관
        mMypageBinding.footer.term1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree1);
                intent.putExtra("title", getString(R.string.term_txt1));
                startActivity(intent);
            }
        });

        // 개인정보 취급방침
        mMypageBinding.footer.term2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree2);
                intent.putExtra("title", getString(R.string.term_txt2));
                startActivity(intent);
            }
        });

        // 위치기반서비스 이용약관
        mMypageBinding.footer.term3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree3);
                intent.putExtra("title", getString(R.string.term_txt3));
                startActivity(intent);
            }
        });

        // 앱버전
        mMypageBinding.center.txtVer.setText("V"+Util.getAppVersionName(getActivity()));

        // 회원가입
        mMypageBinding.notJoin.btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                startActivityForResult(intent, 90);
            }
        });

        // 로그인
        mMypageBinding.notJoin.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 90);
            }
        });

        //로그아웃
        mMypageBinding.join.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                        Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(Map<String, String> headers, String body) {
                                        try {
                                            JSONObject obj = new JSONObject(body);

                                            if (!obj.getString("result").equals("success")) {
                                                Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

                                            try{
                                                LoginManager.getInstance().logOut();
                                            } catch (Exception e) {}

                                            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.VISIBLE);
                                            mMypageBinding.join.rlJoin.setVisibility(View.GONE);
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
        mMypageBinding.join.llMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MySaveActivity.class);
                startActivity(intent);
            }
        });

        // 등록 카드 상세
        mMypageBinding.join.llCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCardActivity.class);
                startActivityForResult(intent, 91);
            }
        });
        // 쿠폰 상세
        mMypageBinding.join.llCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        }
        if (requestCode == 91 && resultCode == 91) { // 쿠폰, 카드
            checkLogin();
        }
    }
    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                mMypageBinding.center.acceptPush.setChecked(false);
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
                    mMypageBinding.center.acceptPush.setChecked(false);
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
                    mMypageBinding.center.acceptPush.setChecked(false);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try{
                            if(obj.getString("status").toUpperCase().equals("Y"))
                                mMypageBinding.center.acceptPush.setChecked(true);
                            else
                                mMypageBinding.center.acceptPush.setChecked(false);
                        } catch (JSONException e) {
                            mMypageBinding.center.acceptPush.setChecked(false);
                        }
                    } catch (Exception e) {
                        mMypageBinding.center.acceptPush.setChecked(false);
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
                        Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            if(getActivity() != null && isAdded())
                                Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

//                        utype.setVisibility(View.GONE);
//                        if(_preferences.getString("utype",null) != null && _preferences.getString("utype",null).equals("kakao")) {
//                            utype.setImageResource(R.drawable.ico_login_kakao);
//                            utype.setVisibility(View.VISIBLE);
//                        }
//
//                        if(_preferences.getString("utype",null) != null && _preferences.getString("utype",null).equals("facebook")) {
//                            utype.setImageResource(R.drawable.ico_login_facebook);
//                            utype.setVisibility(View.VISIBLE);
//                        }

                        // 푸시 사용설정
                        if(obj.getString("status").toUpperCase().equals("Y"))
                            mMypageBinding.center.acceptPush.setChecked(true);
                        else
                            mMypageBinding.center.acceptPush.setChecked(false);

                    } catch (Exception e) {
                        Log.e(CONFIG.TAG, e.toString());
                        if(getActivity() != null && isAdded()){
                            Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
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

    private void setPush(){
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);
        String userId = _preferences.getString("userid", null);

        if(regId != null)
            setGcmToken(getActivity(), regId, userId, mMypageBinding.center.acceptPush.isChecked());
    }

    // GCM TOKEN
    public static void setGcmToken(final Context context, String regId, String userId, final Boolean flag){
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try{
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));

            if(flag != null) {
                paramObj.put("use_yn", ((flag == true)? "Y":"N"));
            }
            if(userId != null) paramObj.put("user_id", userId);
        } catch (JSONException e) {}

        Api.post(CONFIG.notiSettingUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
//                    setPushCheckPopup(context, flag);
                } catch (Exception e) {
                }
            }
        });
    }
}
