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
import com.hotelnow.utils.OnSingleClickListener;
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

public class CalendarActivity extends Activity{

    private CalendarPickerView calendar;
    private Button btn_complate;
    private ArrayList<Date> arrayList = new ArrayList<>();
    private String[] selectList;
    private ArrayList<Date> not_dates = new ArrayList<Date>();
    private ImageView btn_back;
    private String selected_checkin_date = null, selected_checkout_date = null;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd(EEE)", Locale.KOREAN);
    private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
    private SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
    private TextView checkin_date, checkout_date, check_inout_count;
    private Date fir_cancelday = null;
    private int select_cnt = 1;
    private String lodge_type;
    private String city, city_code, subcity_code;
    private Date today;
    private String strdate, strdate2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        checkin_date = (TextView) findViewById(R.id.checkin_date);
        checkout_date = (TextView) findViewById(R.id.checkout_date);
        check_inout_count = (TextView) findViewById(R.id.check_inout_count);
        btn_complate = (Button) findViewById(R.id.btn_complate);
//        btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
//        btn_complate.setClickable(false);
//        btn_complate.setEnabled(false);

        Date dateObj = new Date();

        // 서버타임있는지 확인하고 없으면 설정
        if (CONFIG.svr_date == null) {
            long time = System.currentTimeMillis();
            CONFIG.svr_date = new Date(time);

            try {
                dateObj = CONFIG.svr_date;
            } catch (Exception e) {
            }
        }

        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(dateObj);
        today = todayCal.getTime();

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, CONFIG.maxDate);

        final Calendar lastYear = Calendar.getInstance();
        if (CurHourFormat.format(today).equals("00") || CurHourFormat.format(today).equals("01")) {
            lastYear.add(Calendar.DATE, -1);
        }
        else {
            lastYear.add(Calendar.DATE, 0);
        }
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();

        strdate = intent.getStringExtra("ec_date");
        strdate2 = intent.getStringExtra("ee_date");
        selectList = intent.getStringArrayExtra("selectList");
        lodge_type = intent.getStringExtra("lodge_type");

        //홈 - 호텔 page에서 진입시
        city = intent.getStringExtra("city");
        city_code = intent.getStringExtra("city_code");
        subcity_code = intent.getStringExtra("subcity_code");

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(new Date());
        end.add(Calendar.DAY_OF_MONTH, CONFIG.maxDate);

//        선택안되는 날
        if(selectList != null) {
            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                if (Arrays.asList(selectList).contains(formatter3.format(date)) == false) {
                    not_dates.add(date);
                }
            }
        }

        if(strdate == null || strdate2 == null){
            strdate = Util.setCheckinout().get(0);
            strdate2 = Util.setCheckinout().get(1);
        }
        checkin_date.setText(Util.formatchange2(strdate));
        checkout_date.setText(Util.formatchange2(strdate2));
        check_inout_count.setText(Util.diffOfDate(strdate.replace("-", ""), strdate2.replace("-", ""))+"박");

        Date newdate = null;
        Date newdate2 = null;
        try {
            newdate = dateformat.parse(strdate);
            newdate2 = dateformat.parse(strdate2);
            arrayList.add(newdate);
            arrayList.add(newdate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("yyyy년 MM월", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(arrayList)
                .withHighlightedDates(not_dates);


        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("list",  date.toString());
                if (calendar.getSelectedDates().size() == 1) {
                    if(selected_checkin_date == null) {
                        selected_checkout_date = null;
                        selected_checkin_date = formatter.format(date);
                        fir_cancelday = date;
                        checkout_date.setText("날짜 선택하기");
                        checkin_date.setText(selected_checkin_date);
                        check_inout_count.setText("0박");
                        btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
                        btn_complate.setClickable(false);
                        btn_complate.setEnabled(false);
                    }
                    else{
                        select_cnt = calendar.getSelectedDates().size();
                        selected_checkout_date = formatter.format(date);
                        if(selected_checkin_date.equals(selected_checkout_date)){
                            calendar.clearSelectedDates();
                            checkin_date.setText("날짜 선택하기");
                            checkout_date.setText("날짜 선택하기");
                            check_inout_count.setText("0박");
                            btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
                            btn_complate.setClickable(false);
                            btn_complate.setEnabled(false);
                            return;
                        }
                        checkout_date.setText(selected_checkout_date);
                        check_inout_count.setText(select_cnt + "박");
                        btn_complate.setBackgroundResource(R.drawable.cal_active_round);
                        btn_complate.setClickable(true);
                        btn_complate.setEnabled(true);
                    }
                }
                else if(calendar.getSelectedDates().size() > 1) {
//                    int sel_count = calendar.getSelectedDates().size()-1;
                    int sel_unday = 0;
                    String sel_start_day = formatter2.format(calendar.getSelectedDates().get(0));
                    String sel_end_day = formatter2.format(date);
                    Date nextday = null;
                    long diffofday = Util.diffOfDate(sel_start_day.replace(".", ""), sel_end_day.replace(".", ""));
                    for (int i = 0; i < diffofday; i++) {
                        try {
                            nextday = getAfterDate(sel_start_day, i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (int j = 0; j < not_dates.size(); j++) {
                            if (sel_unday == 0 && formatter.format(nextday.getTime()).equals(formatter.format(not_dates.get(j)))) {
                                calendar.clearSelectedDates();
                                Toast.makeText(getApplication(), "연박을 진행 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                                sel_unday = 1;
                            }
                        }
                        if (sel_unday == 1)
                            break;
                    }

                    if (lodge_type != null && !lodge_type.equals("Y") && calendar.getSelectedDates().size() > 2) {
                        calendar.clearSelectedDates();
                        Toast.makeText(getApplication(), "연박을 할 수 없는 상품입니다.", Toast.LENGTH_SHORT).show();
                        sel_unday = 1;
                    }

                    if(diffofday >15){
                        calendar.clearSelectedDates();
                        Toast.makeText(getApplication(), "최대 15박까지 가능합니다.", Toast.LENGTH_SHORT).show();
                        sel_unday = 1;
                    }

                    if (sel_unday == 0) {
                        select_cnt = calendar.getSelectedDates().size() - 1;
                        selected_checkout_date = formatter.format(date);
                        checkout_date.setText(selected_checkout_date);
                        check_inout_count.setText(diffofday + "박");
                        btn_complate.setBackgroundResource(R.drawable.cal_active_round);
                        btn_complate.setClickable(true);
                        btn_complate.setEnabled(true);
                    } else {
                        checkin_date.setText("날짜 선택하기");
                        checkout_date.setText("날짜 선택하기");
                        check_inout_count.setText("0박");
                        btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
                        btn_complate.setClickable(false);
                        btn_complate.setEnabled(false);
                    }
                }
                else {
                    Toast.makeText(getApplication(), "다른 날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    calendar.clearSelectedDates();
                    checkin_date.setText("날짜 선택하기");
                    checkout_date.setText("날짜 선택하기");
                    check_inout_count.setText("0박");
                    btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
                    btn_complate.setClickable(false);
                    btn_complate.setEnabled(false);
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                selected_checkin_date = null;
                selected_checkout_date = null;
                btn_complate.setBackgroundResource(R.drawable.cal_unactive_round);
                btn_complate.setClickable(false);
                btn_complate.setFocusable(false);
            }
        });

        btn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        btn_complate.setClickable(false);
        btn_complate.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent intent = new Intent();
                if(TextUtils.isEmpty(selected_checkin_date) || TextUtils.isEmpty(selected_checkout_date)){
                    intent.putExtra("ec_date", strdate);
                    intent.putExtra("ee_date", strdate2);
                }
                else {
                    intent.putExtra("ec_date", Util.formatchange3(selected_checkin_date));
                    intent.putExtra("ee_date", Util.formatchange3(selected_checkout_date));
                }
                intent.putExtra("city", TextUtils.isEmpty(city) ? "" : city);
                intent.putExtra("city_code", TextUtils.isEmpty(city_code) ? "" : city_code);
                intent.putExtra("subcity_code", TextUtils.isEmpty(subcity_code) ? "" : subcity_code);
                setResult(80, intent);
                finish();
            }
        });
    }

    /**
     * 입력받은 날짜에 해달 일수를 증가한다.
     * @param str yyyy.MM.dd 형식
     * @param i 증가시킬 날
     * @return yyyy.MM.dd 증가된 날짜
     * @throws Exception
     */
    public static Date getAfterDate(String str , int i) throws Exception {
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Date sDate = formatter.parse(str);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.add(Calendar.DATE, i );
        return cal.getTime();
    }
}
