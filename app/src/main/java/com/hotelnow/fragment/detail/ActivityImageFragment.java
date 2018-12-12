package com.hotelnow.fragment.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.koushikdutta.ion.Ion;

/**
 * Created by susia on 16. 1. 6..
 */
public class ActivityImageFragment extends Fragment {
    private static String hid;
    private static int idx;

    public static Fragment newInstance(DetailActivityActivity context, int pos, String url, String tid, String[] imgs, int total_cnt) {
        Bundle b = new Bundle();
        b.putString("url", url);
        b.putString("tid", tid);
        b.putStringArray("imgs", imgs);
        b.putInt("img_size",total_cnt);

        return Fragment.instantiate(context, ActivityImageFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.layout_hotelimg, container, false);

        String url = this.getArguments().getString("url");
        ImageView niv = (ImageView)wrap.findViewById(R.id.detail_img);
        Ion.with(niv)
                .error(R.drawable.placeholder)
                .load(url);
        String caption = this.getArguments().getString("caption");
        niv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        hid = this.getArguments().getString("hid");
        idx = DetailHotelActivity.markNowPosition;

        return wrap;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        getView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
//                intent.putExtra("hid", hid);
//                intent.putExtra("idx", idx);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
//            }
//        });

    }
}
