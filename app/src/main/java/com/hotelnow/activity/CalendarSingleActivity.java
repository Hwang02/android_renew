package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarSingleActivity extends Activity{

    private CalendarPickerView calendar;
    private Button btn_complate;
    private ImageView btn_back;
    private String selected_checkin_date = null;
    private ArrayList<Date> arrayList = new ArrayList<>();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd(EEE)", Locale.KOREAN);
    private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
    private SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
    private TextView checkin_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar_single);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        checkin_date = (TextView) findViewById(R.id.checkin_date);
        btn_complate = (Button) findViewById(R.id.btn_complate);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, CONFIG.maxDate);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, 0);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();

        String strdate = intent.getStringExtra("ec_date");

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(new Date());
        end.add(Calendar.DAY_OF_MONTH, CONFIG.maxDate);

        if(strdate == null){
            strdate = Util.setCheckinout().get(0);
        }
        checkin_date.setText(Util.formatchange2(strdate));

        Date newdate = null;

        try {
            newdate = dateformat.parse(strdate);
            arrayList.add(newdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("YYYY년 MM월", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDates(arrayList);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("list",  date.toString());
                selected_checkin_date = formatter.format(date);
                checkin_date.setText(selected_checkin_date);
                btn_complate.setBackgroundResource(R.color.purple);
                btn_complate.setClickable(true);
            }

            @Override
            public void onDateUnselected(Date date) {
                selected_checkin_date = null;
                checkin_date.setText("날짜 선택하기");
                btn_complate.setBackgroundResource(R.color.board_line);
                btn_complate.setClickable(false);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_complate.setClickable(false);
        btn_complate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ec_date", Util.formatchange3(selected_checkin_date));
                setResult(80, intent);
                finish();
            }
        });
    }
}
