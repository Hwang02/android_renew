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
            intent.apply {
                putExtra("action", action)
                putExtra("data", data)
                putExtra("push_type", push_type)
                putExtra("bid", bid)
                putExtra("hid", hid)
                putExtra("isevt", isevt)
                putExtra("evtidx", evtidx)
                putExtra("evttag", evttag)
                putExtra("sdate", sdate)
                putExtra("edate", edate)

                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}