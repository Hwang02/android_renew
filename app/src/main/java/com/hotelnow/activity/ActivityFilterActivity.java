package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.adapter.ActivityFilterAdapter;
import com.hotelnow.model.ActivityThemeItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;

import java.util.List;

public class ActivityFilterActivity extends Activity {

    private List<ActivityThemeItem> facilityarr;
    private DbOpenHelper dbHelper;
    private ActivityFilterAdapter mAdapter;
    private ListView mlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter_activity);

        dbHelper = new DbOpenHelper(this);
        facilityarr = dbHelper.selectAllActivityTheme();

        LogUtil.e("xxxxx", getIntent().getStringExtra("tv_category").replace("테마전체", "전체"));
        mAdapter = new ActivityFilterAdapter(this, 0, facilityarr, getIntent().getStringExtra("tv_category").replace("테마전체", "전체"));
        mlist = (ListView) findViewById(R.id.listview);
        mlist.setAdapter(mAdapter);
        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
                TextView tv_city = (TextView) view.findViewById(R.id.tv_city);

                TuneWrap.Event("category_activity", tv_city.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("id", tv_id.getText().toString());
                intent.putExtra("name", tv_city.getText().toString());
                setResult(80, intent);
                finish();
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
