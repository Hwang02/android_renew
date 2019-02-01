package com.hotelnow.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.home.PagerMainFragment;
import com.hotelnow.utils.Util;
import com.hotelnow.utils.ViewPagerCustom;

import org.json.JSONArray;
import org.json.JSONException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by susia on 16. 1. 7..
 */
public class DialogMainFragment extends DialogFragment {
    public Button mButton;
//    public Button mButton;
    public onSubmitListener mListener;
    public JSONArray popup_data = new JSONArray();
    public HomeFragment pf;
    private PagerAdapter mPagerAdapter;
    private String[] id;
    private String[] title;
    private String[] thumb_img;
    private String[] thumb_link_action;
    private String[] popup_img;
    private String[] response_wh;
    private String[] popup_map;
    private String[] evt_type;
    private String[] front_img;
    private TextView page;

    public interface onSubmitListener {
        void setOnSubmitListener(int idx);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pager_main, container);

        if(popup_data != null && popup_data.length() == 0){
            Toast.makeText(getActivity(),"알림이 없습니다. 리스트 새로고침 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }

        id = new String[popup_data.length()];
        title = new String[popup_data.length()];
        thumb_img = new String[popup_data.length()];
        thumb_link_action = new String[popup_data.length()];
        popup_img = new String[popup_data.length()];
        response_wh = new String[popup_data.length()];
        popup_map = new String[popup_data.length()];
        evt_type = new String[popup_data.length()];
        front_img = new String[popup_data.length()];

        try{
            // 팝업 정의
            for(int i=0; i < popup_data.length(); i++){
                id[i] = popup_data.getJSONObject(i).getString("id");
                title[i] = popup_data.getJSONObject(i).getString("title");
                thumb_img[i] = popup_data.getJSONObject(i).getString("thumb_img");
                thumb_link_action[i] = popup_data.getJSONObject(i).getString("thumb_link_action");
                popup_img[i] = popup_data.getJSONObject(i).getString("popup_img");
                response_wh[i] = popup_data.getJSONObject(i).getString("response_wh");
                popup_map[i] = popup_data.getJSONObject(i).getString("popup_map");
                evt_type[i] = popup_data.getJSONObject(i).getString("evt_type");
                front_img[i] = popup_data.getJSONObject(i).getString("front_img");
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "객실 정보가 올바르지 않습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewPagerCustom mViewPager = (ViewPagerCustom) getView().findViewById(R.id.popup_pager);

        final CheckBox left = (CheckBox) getView().findViewById(R.id.left);
        TextView right = (TextView) getView().findViewById(R.id.right);
        page = (TextView) getView().findViewById(R.id.page);

        mViewPager.setClipToPadding(true);
//        mViewPager.setPadding(5, 0, 5, 0);

        mPagerAdapter = new PagerAdapter(getChildFragmentManager(), getActivity(), popup_data, mViewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0, true);
        mViewPager.addOnPageChangeListener(mPagerAdapter);
        page.setText("1 / "+popup_data.length());
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                if(left.isChecked()){
                    // 오늘 하루 닫기
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 7);

                    if(pf != null && pf.frgpopup != null)
                        pf.frgpopup.dismiss();
                }
                else {
                    // 닫기
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    if (pf != null && pf.frgpopup != null) {
                        pf.frgpopup.dismiss();
                    }
                }

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String checkdate = mSimpleDateFormat.format(calendar.getTime());
                if(pf != null && pf._preferences != null) {
                    Util.setPreferenceValues(pf._preferences, "front_popup_date", checkdate);
                }
            }
        });


    }

    private class PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private JSONArray pdata;
        private ViewPagerCustom autoViewPager;

        public PagerAdapter(FragmentManager fm, Context context, JSONArray data, ViewPagerCustom autoViewPager) {
            super(fm);
            pdata = data;
            this.autoViewPager = autoViewPager;
        }

        @Override
        public Fragment getItem(int position) {
            return PagerMainFragment.create(pf, id[position], title[position], thumb_img[position], thumb_link_action[position], popup_img[position], response_wh[position], popup_map[position], evt_type[position], front_img[position]);
        }

        @Override
        public int getCount() {
            return pdata.length();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
//            if(autoViewPager != null)
//                resizePager(autoViewPager, position);
            page.setText(position+" / "+popup_data.length());
        }

        @Override
        public void onPageScrollStateChanged(int state) {}

        public void resizePager(ViewPagerCustom pager, int position) {
            View view = pager.findViewWithTag(position);
            if (view == null)
                return;
            view.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight(); //The layout params must match the parent of the ViewPager
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, Util.dptopixel(getContext(), 435));
            pager.setLayoutParams(params);
        }
    }

}