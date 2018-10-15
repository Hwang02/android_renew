package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.Util;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends Activity{

    private CalendarPickerView calendar;
    private Button btn_complate;
    private ArrayList<Date> selList = new ArrayList<>();
    private ArrayList<Date> arrayList = new ArrayList<>();
    private ArrayList<Date> unList = new ArrayList<>();
    private ArrayList<Date> not_dates = new ArrayList<Date>();
    private ImageView btn_back;
    private String selected_checkin_date = null, selected_checkout_date = null;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd(EEE)", Locale.KOREAN);
    private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
    private TextView checkin_date, checkout_date, check_inout_count;
    private Date fir_cancelday = null;
    private int select_cnt = 1;

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

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, 180);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, 0);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
//        calendar.
//        button = (Button) findViewById(R.id.get_selected_dates);
        ArrayList<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//        list.add(6);

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String strdate = "2018-10-20";
        String strdate2 = "2018-10-21";
        Date newdate = null;
        Date newdate2 = null;
        try {
            newdate = dateformat.parse(strdate);
            newdate2 = dateformat.parse(strdate2);
            arrayList.add(newdate);
            arrayList.add(newdate2);
            unList.add(newdate2);
            not_dates.add(newdate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("YYYY년 MM월", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(arrayList)
                .withHighlightedDate(newdate2);
//                .withDeactivateDates(list);
//              .withHighlightedDates(arrayList);


        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("list",  date.toString());
                if (calendar.getSelectedDates().size() == 1) {
                    selected_checkout_date = null;
                    selected_checkin_date = formatter.format(date);
                    fir_cancelday = date;
                    checkout_date.setText("날짜 선택하기");
                    checkin_date.setText(selected_checkin_date);
                    check_inout_count.setText("0박");
                }
                else if(calendar.getSelectedDates().size() > 1) {
                    int sel_count = calendar.getSelectedDates().size() - 1;
                    int sel_unday = 0;
                    String sel_start_day = formatter2.format(calendar.getSelectedDates().get(0));
                    String sel_end_day = formatter2.format(calendar.getSelectedDates().get(sel_count));
                    Date nextday = null;
                    for (int i = 0; i < Util.diffOfDate(sel_start_day.replace(".", ""), sel_end_day.replace(".", "")); i++) {
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

//                    if (!lodge_type.equals("Y") && calendar.getSelectedDates().size() > 2) {
//                        calendar.clearSelectedDates();
//                        Toast.makeText(getApplication(), "연박을 할 수 없는 상품입니다.", Toast.LENGTH_SHORT).show();
//                        sel_unday = 1;
//                    }

                    if (sel_unday == 0) {
                        select_cnt = calendar.getSelectedDates().size() - 1;
                        date = calendar.getSelectedDates().get(select_cnt);
                        selected_checkout_date = formatter.format(date);
                        checkout_date.setText(selected_checkout_date);
                        check_inout_count.setText(select_cnt + "박");
                    } else {
                        checkin_date.setText("날짜 선택하기");
                        checkout_date.setText("날짜 선택하기");
                        check_inout_count.setText("0박");
                    }
                }
                else {
                    Toast.makeText(getApplication(), "다른 날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    calendar.clearSelectedDates();
                    checkin_date.setText("날짜 선택하기");
                    checkout_date.setText("날짜 선택하기");
                    check_inout_count.setText("0박");
                }
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_complate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if()
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
