package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.MapHotelActivity;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.thebrownarrow.model.SearchResultItem;

import java.util.ArrayList;

public class MapHotelAdapter  extends PagerAdapter {

    ArrayList<SearchResultItem> arr_LocationList;
    Context context;

    public MapHotelAdapter(Context context, ArrayList<SearchResultItem> arr_ExploreList) {
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
        TextView tv_score = (TextView) itemView.findViewById(R.id.tv_score);
        TextView tv_category = (TextView) itemView.findViewById(R.id.tv_category);
        TextView tv_discount = (TextView) itemView.findViewById(R.id.tv_discount);
        TextView tv_sale = (TextView) itemView.findViewById(R.id.tv_sale);
        ImageView img_hotel = (ImageView) itemView.findViewById(R.id.img_hotel);
        View text_bar = (View) itemView.findViewById(R.id.text_bar);
        ImageView img_star = (ImageView) itemView.findViewById(R.id.img_star);

        tv_name.setText(arr_LocationList.get(position).getName());
        tv_score.setText(arr_LocationList.get(position).getGrade_score());

        if(arr_LocationList.get(position).getGrade_score().equals("0.0")){
            tv_score.setVisibility(View.GONE);
            text_bar.setVisibility(View.GONE);
            img_star.setVisibility(View.GONE);
        }
        else {
            tv_score.setVisibility(View.VISIBLE);
            text_bar.setVisibility(View.VISIBLE);
            img_star.setVisibility(View.VISIBLE);
        }

        tv_category.setText(arr_LocationList.get(position).getCategory());
        tv_discount.setText(arr_LocationList.get(position).getSale_rate()+"%↓");
        Ion.with(img_hotel).load(arr_LocationList.get(position).getLandscape());
        if(!TextUtils.isEmpty(arr_LocationList.get(position).getSale_price()))
            tv_sale.setText(Util.numberFormat(Integer.parseInt(arr_LocationList.get(position).getSale_price())));

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

        main_board.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(context, DetailHotelActivity.class);
                intent.putExtra("hid", arr_LocationList.get(position).getHotel_id());
                intent.putExtra("save", true);
                context.startActivity(intent);
            }
        });

        if(position == arr_LocationList.size()-2){
            ((MapHotelActivity)context).getSearch();
        }

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