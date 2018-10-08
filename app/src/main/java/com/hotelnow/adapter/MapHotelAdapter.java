package com.hotelnow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.utils.LogUtil;
import com.thebrownarrow.model.MyLocation;

import java.util.ArrayList;

public class MapHotelAdapter  extends PagerAdapter {

    ArrayList<MyLocation> arr_LocationList;
    Context context;

    public MapHotelAdapter(Context context, ArrayList<MyLocation> arr_ExploreList) {
        this.context = context;
        this.arr_LocationList = arr_ExploreList;
    }

    @Override
    public int getCount() {
        return arr_LocationList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.layout_map_item, null);

        LinearLayout main_board = (LinearLayout) itemView.findViewById(R.id.main_board);
        TextView tv_name = (TextView) itemView.findViewById(R.id.hotel_name);

        tv_name.setText("Location:" + (position + 1) +"\n" + arr_LocationList.get(position).getLatitude()+"\n" + arr_LocationList.get(position).getLongitude());

        if(arr_LocationList.get(position).isIsfocus()){
            main_board.setBackgroundResource(R.drawable.style_round_map_item);
        }
        else{
            main_board.setBackgroundResource(R.drawable.style_round_map_item_default);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("xxxxx",position+"");

            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.7f;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}