package com.hotelnow.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.hotelnow.BuildConfig;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by susia on 15. 9. 17..
 */
public class Api {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String COOKIE_PRE_FIX_REMEMBER = "remember_";
    private static final String COOKIE_PRE_FIX_SESS = "h_sess";
    private static SharedPreferences _preferences;
    private String finalBody="";
    private static String finalBody2="";

    private Api() {
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public static void get(String url, HttpCallback cb) {
        call("GET", url, null, cb);
    }

    public static void post(String url, String json, HttpCallback cb) {
        Api api = new Api();
        api.call2("POST", url, json, cb);
        api = null;
    }


    private void call2(String method, String url, String data, final HttpCallback cb) {
        RequestBody body = null;
        if (BuildConfig.DEBUG) {
            Log.e(CONFIG.TAG, "------------------------ API CALL ------------------------");
            Log.e(CONFIG.TAG, " API " + method + " url : " + url);
            if (data != null) Log.e(CONFIG.TAG, "data : " + data);
            Log.e(CONFIG.TAG, "----------------------------------------------------------");
            Log.e(CONFIG.TAG, "-");
            Log.e(CONFIG.TAG, "-");
            Log.e(CONFIG.TAG, "-");
        }

        if (data != null) {
            body = RequestBody.create(JSON, data);
            data = "";
        }

        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);

        // User Agent Setting
        String userAgent = " HOTELNOW_APP_ANDROID / ";
        try {
            Random oRandom = new Random();
            int rand = oRandom.nextInt(999999) + 1;
            userAgent += Util.getUserAgent(HotelnowApplication.getAppContext()) + " " + Util.getAppVersionName(HotelnowApplication.getAppContext()) + " " + String.valueOf(rand);
        } catch (Exception e) {
        }

        _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        // 삭제할 코드 나중에
        String hsessVal = "";

        try {
            hsessVal = Util.decode(_preferences.getString("cookie_val", "")).replace("+","-").replace("/","_");
            List<String> lists = Arrays.asList(hsessVal.split("\n"));
            for (int i = 0; i < lists.size(); i++) {
                String list = lists.get(i);

                if (list.contains("h_sess=")) {
                    List<String> cookies = Arrays.asList(list.split(" "));

                    for (int j = 0; j < cookies.size(); j++) {
                        String cookie = cookies.get(j);

                        if (cookie.contains("h_sess=")) {
                            hsessVal = cookie;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsessVal="";
        }
        // 삭제할 코드 .header("Cookie", hsessVal) 까지 나중에

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Cookie", hsessVal)
                .method(method, method.equals("GET") ? null : body).build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(HotelnowApplication.getAppContext().getMainLooper());


            @Override
            public void onFailure(Request request, final IOException e) {
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
//                        Log.e(CONFIG.TAG, "API EXCEPTON : ", e);
                        cb.onFailure(null, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {

                String body = null;
                if (response.isSuccessful()) {
                    body = response.body().string();
                }

                finalBody = body;
                mainHandler.post(new Runnable() {

                    @SuppressLint("LongLogTag")
                    @Override
                    public void run() {

                        if (!response.isSuccessful()) {
                            cb.onFailure(response, null);
                            return;
                        }
// 세션 이유로 추가 삭제 할 코드 나중에
                        List<String> lists = Arrays.asList(response.headers().toString().split("\n"));

                        for (int i = 0; i < lists.size(); i++) {
                            String list = lists.get(i);

                            if (list.contains("h_sess=")) {
                                List<String> cookies = Arrays.asList(list.split(" "));

                                String h_sess = "";
                                if (cookies.size() > 1) {
                                    h_sess = cookies.get(1);
                                }

                                _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
                                SharedPreferences.Editor prefEditor = _preferences.edit();
                                h_sess = h_sess.replace("-","+").replace("_","/");
                                prefEditor.putString("cookie_val", Base64.encodeToString(h_sess.getBytes(), Base64.URL_SAFE));
                                prefEditor.commit();
                                LogUtil.e(CONFIG.TAG, "HEADER response.hecader h_sess: " + h_sess);
                            }
                        }
// 세션 이유로 추가 삭제 할 코드 나중에
                        HashMap<String, String> headers = new HashMap<>();

                        for (String key : response.headers().names()) {
                            headers.put(key, response.header(key));
                        }

                        if (BuildConfig.DEBUG) {
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, "------------------------ API RETURN ----------------------");
                            Log.e(CONFIG.TAG + " API - result : ", finalBody);
                            Log.e(CONFIG.TAG, "----------------------------------------------------------");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                        }

                        cb.onSuccess(headers, finalBody);
                        finalBody = "";
                    }
                });
                body = "";
            }
        });
        body = null;
        request = null;
    }

    private static void call(String method, String url, String data, final HttpCallback cb) {
        RequestBody body = null;
        if (BuildConfig.DEBUG) {
            Log.e(CONFIG.TAG, "------------------------ API CALL ------------------------");
            Log.e(CONFIG.TAG, " API " + method + " url : " + url);
            if (data != null) Log.e(CONFIG.TAG, "data : " + data);
            Log.e(CONFIG.TAG, "----------------------------------------------------------");
            Log.e(CONFIG.TAG, "-");
            Log.e(CONFIG.TAG, "-");
            Log.e(CONFIG.TAG, "-");
        }

        if (data != null) {
            body = RequestBody.create(JSON, data);
            data = "";
        }

        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);

        // User Agent Setting
        String userAgent = " HOTELNOW_APP_ANDROID / ";
        try {
            Random oRandom = new Random();
            int rand = oRandom.nextInt(999999) + 1;
            userAgent += Util.getUserAgent(HotelnowApplication.getAppContext()) + " " + Util.getAppVersionName(HotelnowApplication.getAppContext()) + " " + String.valueOf(rand);
        } catch (Exception e) {
        }

        _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        // 삭제할 코드 나중에
        String hsessVal = "";

        try {
            hsessVal = Util.decode(_preferences.getString("cookie_val", "")).replace("+","-").replace("/","_");
            List<String> lists = Arrays.asList(hsessVal.split("\n"));
            for (int i = 0; i < lists.size(); i++) {
                String list = lists.get(i);

                if (list.contains("h_sess=")) {
                    List<String> cookies = Arrays.asList(list.split(" "));

                    for (int j = 0; j < cookies.size(); j++) {
                        String cookie = cookies.get(j);

                        if (cookie.contains("h_sess=")) {
                            hsessVal = cookie;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsessVal = "";
        }
        // 삭제할 코드 .header("Cookie", hsessVal) 까지 나중에

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Cookie", hsessVal)
                .method(method, method.equals("GET") ? null : body).build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(HotelnowApplication.getAppContext().getMainLooper());


            @Override
            public void onFailure(Request request, final IOException e) {
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
//                        Log.e(CONFIG.TAG, "API EXCEPTON : ", e);
                        cb.onFailure(null, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {

                String body = null;
                if (response.isSuccessful()) {
                    body = response.body().string();
                }

                finalBody2 = body;

                mainHandler.post(new Runnable() {

                    @SuppressLint("LongLogTag")
                    @Override
                    public void run() {

                        if (!response.isSuccessful()) {
                            cb.onFailure(response, null);
                            return;
                        }
// 세션 이유로 추가 삭제 할 코드 나중에
                        List<String> lists = Arrays.asList(response.headers().toString().split("\n"));

                        for (int i = 0; i < lists.size(); i++) {
                            String list = lists.get(i);

                            if (list.contains("h_sess=")) {
                                List<String> cookies = Arrays.asList(list.split(" "));

                                String h_sess = "";
                                if (cookies.size() > 1) {
                                    h_sess = cookies.get(1);
                                }

                                _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
                                SharedPreferences.Editor prefEditor = _preferences.edit();
                                h_sess = h_sess.replace("-","+").replace("_","/");
                                prefEditor.putString("cookie_val", Base64.encodeToString(h_sess.getBytes(), Base64.URL_SAFE));
                                prefEditor.commit();
                                LogUtil.e(CONFIG.TAG, "HEADER response.hecader h_sess: " + h_sess);
                            }
                        }
// 세션 이유로 추가 삭제 할 코드 나중에
                        HashMap<String, String> headers = new HashMap<>();

                        for (String key : response.headers().names()) {
                            headers.put(key, response.header(key));
                        }

                        if (BuildConfig.DEBUG) {
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, "------------------------ API RETURN ----------------------");
                            Log.e(CONFIG.TAG + " API - result : ", finalBody2);
                            Log.e(CONFIG.TAG, "----------------------------------------------------------");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                            Log.e(CONFIG.TAG, " ");
                        }

                        cb.onSuccess(headers, finalBody2);
                        finalBody2 = "";
                    }
                });
                body = "";
            }
        });
        body = null;
        request = null;
    }

    public interface HttpCallback {

        void onFailure(Response response, Exception throwable);

        void onSuccess(Map<String, String> headers, String body);

    }

}