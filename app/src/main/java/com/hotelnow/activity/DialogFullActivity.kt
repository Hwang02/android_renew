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
        _preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val intentLink = intent
        push_type = intentLink.getStringExtra("push_type")
        bid = intentLink.getStringExtra("bid")
        hid = intentLink.getStringExtra("hid")
        isevt = intentLink.getStringExtra("isevt")
        evtidx = intentLink.getIntExtra("evtidx", 0)
        sdate = intentLink.getStringExtra("sdate")
        edate = intentLink.getStringExtra("edate")
        evttag = intentLink.getStringExtra("evttag")
        action = intentLink.action
        data = intentLink.dataString


        val spannable = SpannableString(title_main.text)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        title_main.text = spannable

        ok.setOnClickListener {
            Util.setPreferenceValues(_preferences, "user_first_app", false)
            val intent = Intent(this@DialogFullActivity, MainActivity::class.java)
            intent.putExtra("action", action)
            intent.putExtra("data", data)
            intent.putExtra("push_type", push_type)
            intent.putExtra("bid", bid)
            intent.putExtra("hid", hid)
            intent.putExtra("isevt", isevt)
            intent.putExtra("evtidx", evtidx)
            intent.putExtra("evttag", evttag)
            intent.putExtra("sdate", sdate)
            intent.putExtra("edate", edate)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}