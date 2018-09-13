package com.hotelnow.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hotelnow.R;
import com.hotelnow.databinding.ActivityMainBinding;
import com.hotelnow.fragment.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 하단 탭 버튼 동작 제거
        mbinding.navigation.enableAnimation(false);
        mbinding.navigation.enableShiftingMode(false);
        mbinding.navigation.enableItemShiftingMode(false);

        // 홈 상단 탭
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("Tab 1"));
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("Tab 2"));
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("Tab 3"));

        //상단 toolbar
        mbinding.layoutSearch.txtSearch.setText("dkdkdkdkdkdkdkdkk");

        getFragmentManager().beginTransaction()
                .replace(R.id.screen_container, new HomeFragment(), "home")
                .commitAllowingStateLoss();
    }
}
