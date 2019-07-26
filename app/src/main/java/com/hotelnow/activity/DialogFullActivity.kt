package com.hotelnow.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import com.hotelnow.R
import com.hotelnow.utils.Util
import kotlinx.android.synthetic.main.dialog_full.*

class DialogFullActivity : Activity(){
    private var _preferences: SharedPreferences? = null
    private var push_type: String? = ""
    private var bid: String? = null
    private var hid: String? = null
    private var isevt: String? = null
    private var evtidx: Int = 0
    private var evttag: String? = ""
    private var sdate: String? = null
    private var edate: String? = null
    private var action: String? = ""
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_full)
        _preferences = PreferenceManager.getDefaultSharedPreferences(this@DialogFullActivity)

        val intentLink = intent
        intentLink?.let {
            push_type = it.getStringExtra("push_type")
            bid = it.getStringExtra("bid")
            hid = it.getStringExtra("hid")
            isevt = it.getStringExtra("isevt")
            evtidx = it.getIntExtra("evtidx", 0)
            sdate = it.getStringExtra("sdate")
            edate = it.getStringExtra("edate")
            evttag = it.getStringExtra("evttag")
            action = it.action
            data = it.dataString
        }

        val spannable = SpannableString(title_main.text)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        title_main.text = spannable

        ok.setOnClickListener {
            Util.setPreferenceValues(_preferences, "user_first_app", false)
            val intent = Intent(this@DialogFullActivity, MainActivity::class.java)
            intent?.let {
                it.putExtra("action", action)
                it.putExtra("data", data)
                it.putExtra("push_type", push_type)
                it.putExtra("bid", bid)
                it.putExtra("hid", hid)
                it.putExtra("isevt", isevt)
                it.putExtra("evtidx", evtidx)
                it.putExtra("evttag", evttag)
                it.putExtra("sdate", sdate)
                it.putExtra("edate", edate)

                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)
                finish()
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}