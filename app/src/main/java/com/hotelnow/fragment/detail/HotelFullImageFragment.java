package com.hotelnow.fragment.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.FullImageViewActivity;
import com.koushikdutta.ion.Ion;

/**
 * Created by susia on 16. 1. 6..
 */
public class HotelFullImageFragment extends Fragment {

    public static Fragment newInstance(Context context, int pos, String url) {
        Bundle b = new Bundle();
        b.putString("url", url);
        return Fragment.instantiate(context, HotelFullImageFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout wrap = (LinearLayout) inflater.inflate(R.layout.layout_fullimage_item, container, false);

        return wrap;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String url = this.getArguments().getString("url");
        ImageView niv = (ImageView)getView().findViewById(R.id.image);
        Ion.with(niv).load(url);

    }
}