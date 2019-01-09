package com.hotelnow.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.OnSingleClickListener;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

/**
 * Created by susia on 16. 1. 11..
 */
public class DialogTicketShare extends Dialog {
    Context mContext;
    String t_id;
    String main_photo;
    String t_name;
    Button closeButton;
    View.OnClickListener mClickListener;
//    Tracker t;
    SharedPreferences _preferences;
    String linkUrl;
    Double value;
    ResponseCallback<KakaoLinkResponse> callback;

    public DialogTicketShare(Context context, String t_id, String main_photo, String t_name, Double value, View.OnClickListener close) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

        mContext = context;
        this.t_id = t_id;
        this.main_photo = main_photo;
        this.t_name = t_name;
        this.value = value;
        this.mClickListener = close;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = (Activity)mContext;
//        t = ((HotelnowApplication)activity.getApplication()).getTracker(HotelnowApplication.TrackerName.APP_TRACKER);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_share);

        // 문구....
        LinearLayout kakao_btn = (LinearLayout)findViewById(R.id.kakao_btn);
        LinearLayout sms_btn = (LinearLayout)findViewById(R.id.sms_btn);
        LinearLayout share_btn = (LinearLayout)findViewById(R.id.share_btn);

        _preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        linkUrl = CONFIG.domain+"/?ticket_id="+t_id;

        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(mContext, errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
//                 Toast.makeText(mContext, "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            }
        };

        kakao_btn.setOnClickListener(new OnSingleClickListener(){
            @Override
            public void onSingleClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //At least O
                    String msg = "";
                    if(value >0) {
                        msg = "투숙객 평점 " + value + "점 강력추천\n언제 어디서나 호텔나우와 함께 즐거운 여행을 계획하세요!";
                    }
                    else{
                        msg = "언제어디서나 호텔나우만 믿고 떠나세요!";
                    }
                    FeedTemplate params = FeedTemplate
                            .newBuilder(ContentObject.newBuilder("["+t_name+"]",
                                    main_photo,
                                    LinkObject.newBuilder()
                                    .setMobileWebUrl(linkUrl).build())
                                    .setDescrption(msg)
                                    .build())
                            .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                    .setMobileWebUrl(linkUrl)
                                    .setAndroidExecutionParams(linkUrl)
                                    .setIosExecutionParams("hotelnow://hnevent={\"method\":\"move_detail_ticket\", \"param\":{\"ticket_id\":\""+t_id+"\"}}")
                                    .build()))
                            .build();

                    KakaoLinkService.getInstance().sendDefault(mContext, params, callback);
//                }
//                else {
//                    try {
//                        KakaoLink kakaoLink = KakaoLink.getKakaoLink(mContext);
//                        KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//                        kakaoTalkLinkMessageBuilder.addText("친구가 추천하는 액티비티!\n"
//                                + "[" + t_name + "]\n\n"
//                                + "지금 호텔나우 앱에서 바로 확인해보세요!\n");
//
//                        kakaoTalkLinkMessageBuilder.addWebButton("액티비티 자세히 보기", linkUrl);
//                        kakaoTalkLinkMessageBuilder.addImage(main_photo, 360, 200);
//                        kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, mContext);
//
//                        t.send(new HitBuilders.EventBuilder().setCategory("SHARE").setAction("KAKAO").build());
//                        TuneWrap.Event("SHARE", "KAKAO");

                        dismiss();
//                    } catch (KakaoParameterException e) {
//                        Log.e(CONFIG.TAG, "???????", e);
//                    }
//                }
            }
        });

        sms_btn.setOnClickListener(new OnSingleClickListener(){
            @Override
            public void onSingleClick(View v) {

                String smsBody = "[호텔나우]\n"
                        +"친구가 추천하는 액티비티!\n"
                        + "[" + t_name + "]\n\n"
                        + "자세히보기 >> "+linkUrl;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mContext); //Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);

                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, smsBody);

                    if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                    {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    mContext.startActivity(sendIntent);

                }
                else //For early versions, do what worked for you before.
                {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                    sendIntent.putExtra("sms_body", smsBody);
                    sendIntent.putExtra("address", ""); // 받는사람 번호
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    mContext.startActivity(sendIntent);
                }
//                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                String smsBody = "[호텔나우]\n"
//                        +"친구가 추천하는 호텔!\n"
//                        + "[" + hotel_name + "]\n"
//                        + "- 체크인 : " + Util.weekdayFormat(checkin) + "\n"
//                        + "- 체크아웃 : " + Util.weekdayFormat(checkout) + "\n\n"
//                        + "자세히보기 >> "+linkUrl;
//                sendIntent.putExtra("sms_body", smsBody);
//                sendIntent.putExtra("address", ""); // 받는사람 번호
//                sendIntent.setType("vnd.android-dir/mms-sms");
//                mContext.startActivity(sendIntent);

//                t.send(new HitBuilders.EventBuilder().setCategory("SHARE").setAction("SMS").build());
//                TuneWrap.Event("SHARE", "SMS");

                dismiss();
            }
        });

        share_btn.setOnClickListener(new OnSingleClickListener(){
            @Override
            public void onSingleClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", linkUrl);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "링크 메시지가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();

//                t.send(new HitBuilders.EventBuilder().setCategory("SHARE").setAction("LINK_COPY").build());
//                TuneWrap.Event("SHARE", "LINK_COPY");

                dismiss();
            }
        });

        closeButton = (Button) findViewById(R.id.btn_ok);
        closeButton.setOnClickListener(mClickListener);
    }
}
