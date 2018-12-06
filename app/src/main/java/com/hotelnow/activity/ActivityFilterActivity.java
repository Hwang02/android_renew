package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.adapter.ActivityFilterAdapter;
import com.hotelnow.fragment.model.ActivityThemeItem;
import com.hotelnow.utils.DbOpenHelper;

import java.util.List;

public class ActivityFilterActivity extends Activity{

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

        mAdapter = new ActivityFilterAdapter( this, 0, facilityarr);
        mlist = (ListView) findViewById(R.id.listview);
        mlist.setAdapter(mAdapter);
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
                TextView tv_city = (TextView) view.findViewById(R.id.tv_city);

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