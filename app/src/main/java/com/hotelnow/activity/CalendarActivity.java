package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hotelnow.R;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends Activity{

    CalendarPickerView calendar;
    Button button;
    ArrayList<Date> selList = new ArrayList<>();
    ArrayList<Date> arrayList = new ArrayList<>();
    ArrayList<Date> unList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, 180);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, 0);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
//        calendar.
        button = (Button) findViewById(R.id.get_selected_dates);
        ArrayList<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//        list.add(6);

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String strdate = "2018-09-30";
        String strdate2 = "2018-10-01";
        Date newdate = null;
        Date newdate2 = null;
        try {
            newdate = dateformat.parse(strdate);
            newdate2 = dateformat.parse(strdate2);
            arrayList.add(newdate);
            arrayList.add(newdate2);
            unList.add(newdate2);
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
//                if (calendar.getSelectedDates().size() == 1) {
//
//                }
//                else {
//                    calendar.clearSelectedDates();
//                }
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("list",  calendar.getSelectedDates().toString());

            }
        });
    }
}
