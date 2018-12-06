package com.hotelnow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by susia on 15. 12. 7..
 */
public class PrivateDealActivity extends FragmentActivity {
    private WebView webview;
    private String linkUrl, pid, bid_id, ec_date, ee_date, bid, city="", hotel_name="";
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Util.setStatusColor(this);

        webview = (WebView)findViewById(R.id.webview);

        Intent intent = getIntent();
        if(intent != null){
            linkUrl = intent.getStringExtra("url");
            pid = intent.getStringExtra("pid");
            bid_id = intent.getStringExtra("bid_id");
            bid = intent.getStringExtra("bid");
            ec_date = intent.getStringExtra("ec_date");
            ee_date = intent.getStringExtra("ee_date");
            city = intent.getStringExtra("city");
            hotel_name = intent.getStringExtra("hotel_name");
        }

        Random oRandom = new Random();
        int rand = oRandom.nextInt(999999) + 1;

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(webview.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        webview.setWebViewClient(new webViewClient());
        webview.addJavascriptInterface(new DetailInterface(), "android");
        webview.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webview.loadUrl(linkUrl);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class webViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/404.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("plusfriend")){
                // 옐로우 아이디 처리
                Util.kakaoYelloId(PrivateDealActivity.this);
                finish();
                return true;

            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;

            } else if (url.startsWith("mailto:")) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;

            } else if(url.contains("www.hotelnow.co.kr")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                return true;
            } else {
                view.loadUrl(url);
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class DetailInterface
    {
        @JavascriptInterface
        public void openPurchaseActivity(int accepted_price) {
            Log.e("webview", "accepted_price : "+accepted_price);
            Intent intent = new Intent(PrivateDealActivity.this, ReservationActivity.class);
            intent.putExtra("page", "Private");
            intent.putExtra("pid", pid);
            intent.putExtra("ec_date", ec_date);
            intent.putExtra("ee_date", ee_date);
            intent.putExtra("accepted_price", accepted_price);
            intent.putExtra("bid_id", bid_id);
            startActivityForResult(intent, 100);
        }

        @JavascriptInterface
        public void closeActivity() {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("sdate", ec_date);
            returnIntent.putExtra("edate", ee_date);
            setResult(80, returnIntent);
            finish();
        }

        @JavascriptInterface
        public void proposalActivity(){
//            프라이빗딜 제안하기
//            api 호출
            setPrivateDealProposal();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (responseCode == 80) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("isPrivate", true);
            returnIntent.putExtra("ec_date", ec_date);
            returnIntent.putExtra("ee_date", ee_date);
            setResult(80, returnIntent);
            finish();
        }
        else if (responseCode == 100) {
            setResult(100);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webview != null)
            webview.destroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setPrivateDealProposal(){
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("room_id", bid);
            paramObj.put("ec_date", ec_date);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }
        Api.post(CONFIG.privateDeaProposalUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
