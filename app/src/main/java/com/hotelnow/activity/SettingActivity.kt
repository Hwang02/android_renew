package com.hotelnow.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.hotelnow.R
import com.hotelnow.dialog.DialogAgreeUser
import com.hotelnow.dialog.DialogAlert
import com.hotelnow.utils.*
import com.koushikdutta.ion.Ion
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONException
import org.json.JSONObject

class SettingActivity : Activity() {

    private var _preferences: SharedPreferences? = null
    private var cookie: String? = ""
    private var dialogAgreeUser: DialogAgreeUser? = null
    private var Sel_check: Boolean = false
    private var dialogAlert: DialogAlert? = null
    private var url_link: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        _preferences = PreferenceManager.getDefaultSharedPreferences(this@SettingActivity)
        _preferences?.let {
                    cookie = it.getString("userid", null)
        }
        cookie?.let {
            tv_alarm_setting.text = "할인 혜택 알림 받기(Push)"
            btn_retire.visibility = View.GONE
            retire_line.visibility = View.GONE
        }.let {
            tv_alarm_setting.text = "할인 혜택 알림 받기(SMS,E-mail,Push)"
            btn_retire.visibility = View.VISIBLE
            retire_line.visibility = View.VISIBLE
        }

        btn_push.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                val intent = Intent(this@SettingActivity, SettingAlarmActivity::class.java)
                startActivity(intent)
            }
        })

        // 개인정보 수집
        btn_agree1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                setUserBenefit(true)
            }
        })

        // 위치서비스
        btn_agree2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                val intent = Intent(this@SettingActivity, LocationAgreeActivity::class.java)
                startActivity(intent)
            }
        })

        // 탈퇴하기
        btn_retire.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                val intent = Intent(this@SettingActivity, PwCheckActivity::class.java)
                startActivityForResult(intent, 999)
            }
        })

        title_back.setOnClickListener() {
            setResult(91)
            finish()
        }

        setUserBenefit(false)
    }

    private fun setUserBenefit(flag: Boolean){
        wrapper.visibility = View.VISIBLE
        var url = CONFIG.maketing_agree
        val uuid = Util.getAndroidId(this)

        if (uuid != null && !TextUtils.isEmpty(uuid)) {
            url += "?uuid=$uuid"
        }
        url += "&marketing_use"

        Api.get(url, object : Api.HttpCallback {
            override fun onFailure(response: Response, throwable: Exception) {
                wrapper.visibility = View.GONE
                Toast.makeText(this@SettingActivity, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(headers: Map<String, String>, body: String) {
                try {
                    val obj = JSONObject(body)

                    if (obj.getString("result") != "success") {
                        Toast.makeText(this@SettingActivity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                        wrapper.visibility = View.GONE
                        return
                    }
                    var agreed_yn = "N"
                    if (obj.has("marketing_use")) {
                        agreed_yn = obj.getJSONObject("marketing_use").getString("agreed_yn")
                        Sel_check = if (obj.getJSONObject("marketing_use").getString("agreed_yn") == "Y") true else false
                    }

                    if (flag) {
                        setAgreedPopup(agreed_yn)
                    } else {
                        if (obj.has("event_banners") && obj.getJSONArray("event_banners").length() > 0) {
                            findViewById<View>(R.id.layout_banner).visibility = View.VISIBLE
                            Ion.with(my_banner).load(obj.getJSONArray("event_banners").getJSONObject(0).getString("image"))
                            val id = obj.getJSONArray("event_banners").getJSONObject(0).getString("event_id")
                            val evt_type = obj.getJSONArray("event_banners").getJSONObject(0).getString("evt_type")
                            val link = obj.getJSONArray("event_banners").getJSONObject(0).getString("link")
                            val title = obj.getJSONArray("event_banners").getJSONObject(0).getString("title")
                            layout_banner.setOnClickListener(object : OnSingleClickListener() {
                                override fun onSingleClick(v: View) {
                                    setEventCheck(id, evt_type, link, title)
                                }
                            })
                        } else {
                            layout_banner.visibility = View.GONE
                        }
                    }
                    wrapper.visibility = View.GONE

                } catch (e: Exception) {
                    wrapper.visibility = View.GONE
                }

            }
        })
    }

    private fun setAgreedPopup(value: String){
        dialogAgreeUser = DialogAgreeUser(this@SettingActivity,
                object : OnSingleClickListener() {
                    override fun onSingleClick(v: View) {
                        // api 호출
                        setMaketing(Sel_check)
                        dialogAgreeUser?.dismiss()
                    }
                },
                object : OnSingleClickListener() {
                    override fun onSingleClick(v: View) {
                        dialogAgreeUser?.dismiss()
                    }
                }, value, CompoundButton.OnCheckedChangeListener { _: CompoundButton, isChecked -> Sel_check = isChecked }, if (cookie == null) false else true)

        dialogAgreeUser?.let {
            it.setCancelable(false)
            it.show()
        }
    }

    private fun setMaketing(ischeck: Boolean) {
        // 푸시 수신 상태값 저장
        var regId: String? = null
        _preferences?.let{
            regId = it.getString("gcm_registration_id", null)
        }

        regId?.let {
            LogUtil.e("xxxxx", it)
            setMaketingSend(this, it, ischeck)
        }.let {
            setMaketingSend(this, "", ischeck)
        }
    }

    // GCM TOKEN
    fun setMaketingSend(context: Context, regId: String, flag: Boolean?) {
        val androidId = Util.getAndroidId(context)

        val paramObj = JSONObject()

        try {
            paramObj.apply {
                put("os", "a")
                put("uuid", androidId)
                put("push_token", regId)
                put("ver", Util.getAppVersionName(context))
                put("marketing_use", if (flag == true) "Y" else "N")
            }

        } catch (e: JSONException) {
        }

        Api.post(CONFIG.maketing_agree_change, paramObj.toString(), object : Api.HttpCallback {
            override fun onFailure(response: Response, e: Exception) {

            }

            override fun onSuccess(headers: Map<String, String>, body: String) {
                try {
                    val obj = JSONObject(body)

                    if (obj.getString("result") != "success") {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show()
                        return
                    }

                } catch (e: Exception) {
                }

            }
        })
    }

    private fun setEventCheck(id: String, type: String, link: String, title: String) {

        val arr = link.split("hotelnowevent://".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var frontMethod: String? = ""
        var frontTitle: String?
        var frontEvtId: String?
        var method: String?

        if (arr.size > 1) {
            frontMethod = arr[1]
            frontMethod = Util.stringToHTMLString(frontMethod)
            frontTitle = if (title !== "") title else "무료 숙박 이벤트"
        }
        if (type != "a") {
            frontEvtId = id
        } else {
            frontEvtId = Util.getFrontThemeId(link)
        }

        if (type == "a" && type != "") {
            try {
                val obj = JSONObject(frontMethod)
                method = obj.getString("method")
                if (obj.has("param")) {
                    url_link = obj.getString("param")
                }

                if (method == "move_near") {
                    var fDayLimit = 180
                    _preferences?.let { fDayLimit = it.getInt("future_day_limit", 180) }
                    val checkurl = CONFIG.checkinDateUrl + "/" + url_link + "/" + fDayLimit

                    Api.get(checkurl, object : Api.HttpCallback {
                        override fun onFailure(response: Response, e: Exception) {
                            Toast.makeText(this@SettingActivity, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show()
                            return
                        }

                        override fun onSuccess(headers: Map<String, String>, body: String) {
                            try {
                                val _obj = JSONObject(body)
                                val aobj = _obj.getJSONArray("data")

                                if (aobj.length() == 0) {
                                    dialogAlert = DialogAlert(
                                            getString(R.string.alert_notice),
                                            "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                            this@SettingActivity,
                                            View.OnClickListener { dialogAlert?.dismiss() })
                                    dialogAlert?.let {
                                        it.setCancelable(false)
                                        it.show()
                                    }
                                    return
                                }

                                val checkin = aobj.getString(0)
                                val checkout = Util.getNextDateStr(checkin)

                                val intent = Intent(this@SettingActivity, DetailHotelActivity::class.java)
                                intent.apply {
                                    putExtra("hid", url_link)
                                    putExtra("evt", "N")
                                    putExtra("sdate", checkin)
                                    putExtra("edate", checkout)
                                }
                                startActivity(intent)

                            } catch (e: Exception) {
                                // Log.e(CONFIG.TAG, e.toString());
                                Toast.makeText(this@SettingActivity, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show()
                                return
                            }

                        }
                    })
                } else if (method == "move_theme") {
                    val intent = Intent(this@SettingActivity, ThemeSpecialHotelActivity::class.java)
                    intent.apply {
                        putExtra("tid", url_link)
                    }
                    startActivity(intent)

                } else if (method == "move_theme_ticket") {
                    val intent = Intent(this@SettingActivity, ThemeSpecialActivityActivity::class.java)
                    intent.apply {
                        putExtra("tid", url_link)
                    }
                    startActivity(intent)
                } else if (method == "move_ticket_detail") {
                    val intent = Intent(this@SettingActivity, DetailActivityActivity::class.java)
                    intent.apply {
                        putExtra("tid", url_link)
                    }
                    startActivity(intent)
                } else if (method == "outer_link") {
                    if (url_link.contains("hotelnow")) {
                        frontTitle = if (title !== "") title else "무료 숙박 이벤트"
                        val intent = Intent(this@SettingActivity, WebviewActivity::class.java)
                        intent.apply {
                            putExtra("url", url_link)
                            putExtra("title", frontTitle)
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url_link))
                        startActivity(intent)
                    }
                } else if (method == "move_privatedeal_all") {
                    val intent = Intent(this@SettingActivity, PrivateDaelAllActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
            }

        } else {
            frontTitle = if (title !== "") title else "무료 숙박 이벤트"
            val intentEvt = Intent(this@SettingActivity, EventActivity::class.java)
            intentEvt.apply {
                frontEvtId?.let { intentEvt.putExtra("idx", Integer.valueOf(frontEvtId)) }
                putExtra("title", frontTitle)
            }
            startActivity(intentEvt)
        }
    }

    override fun onBackPressed() {
        setResult(91)
        finish()
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 999) {
            if (resultCode == 888) {
                finish()
            } else if (resultCode == 999) {
                setResult(999)
                finish()
            }
        }
    }
}