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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.EventActivity;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.MyCardActivity;
import com.hotelnow.activity.MyCouponActivity;
import com.hotelnow.activity.MySaveActivity;
import com.hotelnow.activity.PrivateDaelAllActivity;
import com.hotelnow.activity.SettingActivity;
import com.hotelnow.activity.SignupActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.databinding.FragmentMypageBinding;
import com.hotelnow.dialog.DialogAgreeAll;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
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
    private DialogAgreeAll dialogAgreeAll;
    private DialogAlert dialogAlert;
    private String url_link = "";

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
                startActivityForResult(intent, 91);
            }
        });

        // 비활성화
        mMypageBinding.notJoin.rlNotJoin.setVisibility(View.GONE);
        mMypageBinding.join.rlJoin.setVisibility(View.GONE);

        // 운영시간
        mMypageBinding.center.infoTime.setText("운영시간 " + CONFIG.operation_time);

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
                    userId = _preferences.getString("userid", null) == null ? null : AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", ""));
                } catch (Exception e) {
                    userId = "";
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
                    userId = "";
                }
                userId = "";
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
                        getString(R.string.wanna_call_hotelnow) + "\n운영시간 : " + CONFIG.operation_time,
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
                if (mMypageBinding.footer.tvCompanyinfo.getVisibility() == View.VISIBLE) {
                    mMypageBinding.footer.tvCompanyinfo.setVisibility(View.GONE);
                    mMypageBinding.footer.lvMore.setBackgroundResource(R.drawable.btn_detail_open_grey);
                } else {
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
                intent.putExtra("title", getString(R.string.term_txt1_sub));
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

        // 사업자 정보확인
        mMypageBinding.footer.term4.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONFIG.setting_agree4));
                startActivity(intent);
            }
        });

        // 앱버전
        mMypageBinding.center.txtVer.setText("V" + Util.getAppVersionName(getActivity()));

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
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("ver", Util.getAppVersionName(getActivity()));
                                    params.put("uuid", Util.getAndroidId(getActivity()));
                                    params.put("os", "a");
                                    params.put("push_token", _preferences.getString("gcm_registration_id", ""));
                                }catch (JSONException e){;}

                                Api.post(CONFIG.logoutUrl, params.toString(), new Api.HttpCallback() {
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
                                            prefEditor.putString("cookie_val", "");
                                            prefEditor.commit();

                                            ((MainActivity) getActivity()).setTitle();
                                            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.VISIBLE);
                                            mMypageBinding.join.rlJoin.setVisibility(View.GONE);

                                            dbHelper.deleteFavoriteItem(true, "", "");
                                            ((MainActivity) getActivity()).moveTabRefresh();
                                            ((MainActivity) getActivity()).moveTabRefresh2();
                                            ((MainActivity) getActivity()).moveTabRefresh3();

                                            try {
                                                LoginManager.getInstance().logOut();
                                            } catch (Exception e) {
                                            }

                                            setUserBenefit(true);
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
                startActivityForResult(intent, 7000);
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
    // flag : 베너 정보
    private void setUserBenefit(final boolean flag){
        String url = CONFIG.maketing_agree;
        String uuid = Util.getAndroidId(getActivity());

        if(uuid != null && !TextUtils.isEmpty(uuid)){
            url += "?uuid="+uuid;
        }
        url +="&marketing_use";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
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

                    if(flag &&!obj.has("marketing_use")){
                        AgreementPopup();
                    }
                    else if(!flag) {
                        if(obj.has("event_banners") && obj.getJSONArray("event_banners").length() >0){
                            mMypageBinding.layoutBanner.setVisibility(View.VISIBLE);
                            Ion.with(mMypageBinding.myBanner).load(obj.getJSONArray("event_banners").getJSONObject(0).getString("image"));
                            final String id = obj.getJSONArray("event_banners").getJSONObject(0).getString("event_id");
                            final String evt_type = obj.getJSONArray("event_banners").getJSONObject(0).getString("evt_type");
                            final String link = obj.getJSONArray("event_banners").getJSONObject(0).getString("link");
                            final String title = obj.getJSONArray("event_banners").getJSONObject(0).getString("title");
                            mMypageBinding.layoutBanner.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    setEventCheck(id, evt_type, link, title);
                                }
                            });
                        }
                        else{
                            mMypageBinding.layoutBanner.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setEventCheck(String id, String type, String link, String title){

        String[] arr = link.split("hotelnowevent://");
        String frontMethod = "";
        String frontTitle = "";
        String frontEvtId = "";
        String method = "";

        if (arr.length > 1) {
            frontMethod = arr[1];
            frontMethod = Util.stringToHTMLString(frontMethod);
            frontTitle = title != "" ? title : "무료 숙박 이벤트";
        }
        if (!type.equals("a")) {
            frontEvtId = id;
        } else {
            frontEvtId = Util.getFrontThemeId(link);
        }

        if (type.equals("a") && !type.equals("")) {
            try {
                JSONObject obj = new JSONObject(frontMethod);
                method = obj.getString("method");
                if(obj.has("param")) {
                    url_link = obj.getString("param");
                }

                if (method.equals("move_near")) {
                    int fDayLimit = _preferences.getInt("future_day_limit", 180);
                    String checkurl = CONFIG.checkinDateUrl + "/" + url_link + "/" + fDayLimit;

                    Api.get(checkurl, new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                JSONArray aobj = obj.getJSONArray("data");

                                if (aobj.length() == 0) {
                                    dialogAlert = new DialogAlert(
                                            getString(R.string.alert_notice),
                                            "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                            getActivity(),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogAlert.dismiss();
                                                }
                                            });
                                    dialogAlert.setCancelable(false);
                                    dialogAlert.show();
                                    return;
                                }

                                String checkin = aobj.getString(0);
                                String checkout = Util.getNextDateStr(checkin);

                                Intent intent = new Intent(getActivity(), DetailHotelActivity.class);
                                intent.putExtra("hid", url_link);
                                intent.putExtra("evt", "N");
                                intent.putExtra("sdate", checkin);
                                intent.putExtra("edate", checkout);

                                startActivityForResult(intent, 80);

                            } catch (Exception e) {
                                // Log.e(CONFIG.TAG, e.toString());
                                Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                    });
                } else if (method.equals("move_theme")) {
                    Intent intent = new Intent(getActivity(), ThemeSpecialHotelActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivityForResult(intent, 80);

                } else if (method.equals("move_theme_ticket")) {
                    Intent intent = new Intent(getActivity(), ThemeSpecialActivityActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivityForResult(intent, 80);

                } else if (method.equals("move_ticket_detail")) {
                    Intent intent = new Intent(getActivity(), DetailActivityActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivityForResult(intent, 80);

                } else if (method.equals("outer_link")) {
                    if (url_link.contains("hotelnow")) {
                        frontTitle = title != "" ? title : "무료 숙박 이벤트";
                        Intent intent = new Intent(getActivity(), WebviewActivity.class);
                        intent.putExtra("url", url_link);
                        intent.putExtra("title", frontTitle);
                        startActivityForResult(intent, 80);

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_link));
                        startActivity(intent);
                    }
                } else if (method.equals("move_privatedeal_all")){
                    Intent intent = new Intent(getActivity(), PrivateDaelAllActivity.class);
                    startActivityForResult(intent, 200);
                }
            } catch (Exception e) {
            }
        } else {
            frontTitle = title != "" ? title : "무료 숙박 이벤트";
            Intent intentEvt = new Intent(getActivity(), EventActivity.class);
            intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
            intentEvt.putExtra("title", frontTitle);
            startActivityForResult(intentEvt, 200);
        }
    }

    public void AgreementPopup(){
        dialogAgreeAll = new DialogAgreeAll(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 필수
                if (!((CheckBox) dialogAgreeAll.findViewById(R.id.agree_checkbox1)).isChecked()) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.validator_service_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }
                //필수
                if(!((CheckBox) dialogAgreeAll.findViewById(R.id.agree_checkbox2)).isChecked()) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.validator_userinfo_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }
                //선택
                String user_check ="N";
                if(((CheckBox) dialogAgreeAll.findViewById(R.id.agree_checkbox3)).isChecked()) {
                    user_check = "Y";
                }
                //선택
                String location_check = "N";
                if(((CheckBox) dialogAgreeAll.findViewById(R.id.agree_checkbox4)).isChecked()) {
                    location_check = "Y";
                }

                if(_preferences.getString("userid", null) == null) {
                    Util.setPreferenceValues(_preferences, "no_user_agree_check", true);
                }
                setMaketing(user_check, location_check);
            }
        }, _preferences.getString("userid", null) == null ? false : true);
        dialogAgreeAll.show();
        dialogAgreeAll.setCancelable(false);
    }

    private void setMaketing(String user_check, String location_check) {
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);

        LogUtil.e("xxxxx", regId);
        if (regId != null) {
            setMaketingSend(getActivity(), regId, user_check, location_check);
        }
    }

    // GCM TOKEN
    public void setMaketingSend(final Context context, String regId, String user_check, String location_check) {
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try {
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));
            paramObj.put("marketing_use", user_check);
            paramObj.put("location", location_check);
            paramObj.put("personal_info", "Y");
            paramObj.put("marketing_receive_push", "Y");
            paramObj.put("marketing_receive_sms", "Y");
            paramObj.put("marketing_receive_email", "Y");

        } catch (JSONException e) {; }

        Api.post(CONFIG.maketing_agree_change, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {

            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(dialogAgreeAll != null) {
                        dialogAgreeAll.dismiss();
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 90 && resultCode == 90) { // 로그인
            authCheck();
            ((MainActivity) getActivity()).setTitle();
        }
        if (requestCode == 91 && resultCode == 91) { // 쿠폰, 카드
            checkLogin();
        }
        else if(requestCode == 7000) {
            checkLogin();
        }
        else if(requestCode == 91 && resultCode == 999){ // 회원탈퇴
            SharedPreferences.Editor prefEditor = _preferences.edit();
            prefEditor.putString("email", null);
            prefEditor.putString("username", null);
            prefEditor.putString("phone", null);
            prefEditor.putString("userid", null);
            prefEditor.putString("moreinfo", null);
            prefEditor.putString("utype", null);
            prefEditor.putString("cookie_val", "");
            prefEditor.commit();

            ((MainActivity) getActivity()).setTitle();
            mMypageBinding.notJoin.rlNotJoin.setVisibility(View.VISIBLE);
            mMypageBinding.join.rlJoin.setVisibility(View.GONE);

            dbHelper.deleteFavoriteItem(true, "", "");
            ((MainActivity) getActivity()).moveTabRefresh();
            ((MainActivity) getActivity()).moveTabRefresh2();
            ((MainActivity) getActivity()).moveTabRefresh3();

            ((MainActivity) getActivity()).setTapMove(4, true);
        }
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
                isPush = false;
                if (getActivity() == null) {
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
                    setUserBenefit(false);
                    checkLogin();

                } catch (Exception e) {
                    isPush = false;
                    MainActivity.hideProgress();
                    if (getActivity() != null && isAdded()) {
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
        try {
            params.put("uuid", Util.getAndroidId(getActivity()));
        } catch (JSONException e) {
        }

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
                    if (getActivity() != null && isAdded())
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            if (getActivity() != null && isAdded())
                                Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            MainActivity.hideProgress();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");
                        JSONObject user = obj.getJSONObject("user");

                        mMypageBinding.join.userName.setText(user.getString("username"));
                        mMypageBinding.join.userEmail.setText(user.getString("useremail"));
                        save_money = Util.numberFormat(data.getInt("amount"));
                        mMypageBinding.join.tvUserSaveMoney.setText(save_money +"p");
                        mMypageBinding.join.tvUserCoupon.setText(data.getString("coupon_cnt_new") + "장");
                        mMypageBinding.join.tvUserCard.setText(data.getString("card_cnt") + "장");
                        expire_money = Util.numberFormat(data.getInt("expire_amount"));
                        mMypageBinding.join.disableMoney.setText(expire_money + "p");
                        mMypageBinding.join.disableCoupon.setText(data.getInt("expire_coupon_cnt") + "장");
                        if (expire_money.equals("0")) {
                            mMypageBinding.join.layoutDiscountMoney.setVisibility(View.GONE);
                        } else {
                            mMypageBinding.join.layoutDiscountMoney.setVisibility(View.VISIBLE);
                        }

                        if (data.getInt("expire_coupon_cnt") == 0) {
                            mMypageBinding.join.layoutDisableCoupon.setVisibility(View.GONE);
                        } else {
                            mMypageBinding.join.layoutDisableCoupon.setVisibility(View.VISIBLE);
                        }

                        // 푸시 사용설정
                        if (obj.getString("status").toUpperCase().equals("Y"))
                            isPush = true;
                        else
                            isPush = false;
                        MainActivity.hideProgress();
                    } catch (Exception e) {
                        Log.e(CONFIG.TAG, e.toString());
                        if (getActivity() != null && isAdded()) {
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
