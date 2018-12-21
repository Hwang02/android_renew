package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.BannerStayAllActivity;
import com.hotelnow.activity.HotDealActivity;
import com.hotelnow.activity.PrivateDaelAllActivity;
import com.hotelnow.activity.ThemeSAllActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.hotelnow.utils.ViewPagerCustom;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> items;
    private HotelFragment mHf;
    private DbOpenHelper dbHelper;
    private LayoutInflater inflater;
    private final int HEADER = 0; // 하단
    private final int BANNER =1; // viewpager
    private final int PRIVATEDEAL = 2; // 프라이빗딜
    private final int PROMOTION = 3; // 변경되는 프로모션 horizontal
    private final int SPECIAL = 4; // 변경되는 프로모션 vertical
    private final int FOOTER = 5; // 변경되는 프로모션 vertical
    private final int HOTDEAL_HOTEL = 6; // 호텔 핫딜 horizontal
    private PrivateDealHotelAdapter privateAdapter = null;
    private BannerPagerHotelAdapter bannerAdapter = null;
    private FooterHAAdapter footAdapter = null;
    private HeaderAdapter headerAdapter = null;
    private ThemeSpecialStayAdapter themeSAdapter = null;
    private ThemeStayAdapter themeAdapter = null;
    private HotelHotDealStayAdapter hotelAdapter = null;
    private static int nowPosition = 0;
    public static int markNowPosition = 0;
    private static int PAGES = 0;


    public HotelAdapter(Context context, HotelFragment hf, List<Object> items, DbOpenHelper dbHelper) {
        this.context = context;
        this.items = items;
        this.mHf = hf;
        this.dbHelper = dbHelper;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case BANNER:
                view = inflater.inflate(R.layout.layout_hotel_banner, parent, false);
                holder = new HotelAdapter.BannerViewHolder(view, BANNER);
                break;
            case PROMOTION:
                view = inflater.inflate(R.layout.layout_theme_horizontal, parent, false);
                holder = new HotelAdapter.HorizontalThemeViewHolder(view, PROMOTION);
                break;
            case SPECIAL:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new HotelAdapter.VerticalViewHolder(view, SPECIAL);
                break;
            case FOOTER:
                view = inflater.inflate(R.layout.layout_footer_vertical, parent, false);
                holder = new HotelAdapter.FooterViewHolder(view, FOOTER);
                break;
            case PRIVATEDEAL:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HotelAdapter.HorizontalViewHolder(view, PRIVATEDEAL);
                break;
            case HEADER:
                view = inflater.inflate(R.layout.layout_header_vertical, parent, false);
                holder = new HotelAdapter.HeaderViewHolder(view, HEADER);
                break;
            case HOTDEAL_HOTEL:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HotelAdapter.HorizontalViewHolder(view, HOTDEAL_HOTEL);
                break;
            default:
                view = inflater.inflate(R.layout.layout_header_vertical, parent, false);
                holder = new HotelAdapter.HeaderViewHolder(view, HEADER);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BANNER:
                BannerView((HotelAdapter.BannerViewHolder) holder, holder.getItemViewType());
                break;
            case PROMOTION:
                setThemeView((HotelAdapter.HorizontalThemeViewHolder) holder, holder.getItemViewType());
                break;
            case SPECIAL:
                setThemeSpecialView((HotelAdapter.VerticalViewHolder) holder, holder.getItemViewType());
                break;
            case FOOTER:
                setBottomView((HotelAdapter.FooterViewHolder) holder, holder.getItemViewType());
                break;
            case HEADER:
                setTopView((HotelAdapter.HeaderViewHolder) holder, holder.getItemViewType());
                break;
            case PRIVATEDEAL:
                setPrivateDealView((HotelAdapter.HorizontalViewHolder) holder, holder.getItemViewType());
                break;
            case HOTDEAL_HOTEL:
                setHotelHotDealView((HotelAdapter.HorizontalViewHolder) holder, holder.getItemViewType());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof BannerItem)
            return BANNER;
        if (items.get(position) instanceof ThemeSpecialItem)
            return SPECIAL;
        if (items.get(position) instanceof DefaultItem)
            return FOOTER;
        if (items.get(position) instanceof ThemeItem)
            return PROMOTION;
        if (items.get(position) instanceof TopItem)
            return HEADER;
        if (items.get(position) instanceof PrivateDealItem)
            return PRIVATEDEAL;
        if (items.get(position) instanceof StayHotDealItem)
            return HOTDEAL_HOTEL;

        return -1;
    }

    private void setHotelHotDealView(HorizontalViewHolder holder, int type) {
        if(hotelAdapter == null) {
            hotelAdapter = new HotelHotDealStayAdapter(mHf.getHotelData(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(hotelAdapter);
            holder.mMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), HotDealActivity.class);
                    intent.putExtra("tab",0);
                    mHf.startActivityForResult(intent, 70);
                }
            });
            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void setTopView(HeaderViewHolder holder, int type){
        if(headerAdapter == null) {
            headerAdapter = new HeaderAdapter(mHf.getTopItem(), mHf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(headerAdapter);
        }
    }

    private void setBottomView(FooterViewHolder holder, int type){
        if(footAdapter == null) {
            footAdapter = new FooterHAAdapter(mHf.getDefaultItem(), mHf.getRecyclerView());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(footAdapter);
        }
    }

    private void setThemeSpecialView(VerticalViewHolder holder, int type) {
        if(themeSAdapter == null) {
            themeSAdapter = new ThemeSpecialStayAdapter(mHf.getThemeSpecialData(), mHf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(themeSAdapter);
            holder.mMoreView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), ThemeSAllActivity.class);
                    intent.putExtra("page", "H");
                    mHf.startActivityForResult(intent, 70);
                }
            });
        }
    }

    private void setThemeView(HorizontalThemeViewHolder holder, int type) {
        if(themeAdapter == null) {
            themeAdapter = new ThemeStayAdapter(mHf.getThemeData(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(themeAdapter);
            holder.background_view.setBackgroundColor(Color.parseColor("#"+mHf.getThemeData().get(0).getBack_color()));
            holder.mTitle.setText(mHf.getThemeData().get(0).getMain_title());
            holder.btn_moreproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHf.getThemeData().get(0).getTheme_flag().equals("H")){
                        Intent intent = new Intent(mHf.getContext(), ThemeSpecialHotelActivity.class);
                        intent.putExtra("tid", mHf.getThemeData().get(0).getTheme_id());
                        mHf.startActivityForResult(intent,70);
                    }
                    else{
                        Intent intent = new Intent(mHf.getContext(), ThemeSpecialActivityActivity.class);
                        intent.putExtra("tid", mHf.getThemeData().get(0).getTheme_id());
                        mHf.startActivityForResult(intent,70);
                    }
                }
            });

            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        if(bannerAdapter == null) {
            PAGES = mHf.getPbannerData().size();
            bannerAdapter = new BannerPagerHotelAdapter(context, mHf.getPbannerData());
            holder.autoViewPager.setClipToPadding(false);
            holder.autoViewPager.setOffscreenPageLimit(0);
            holder.autoViewPager.setPageMargin(20);
            holder.autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착
            holder.autoViewPager.setCurrentItem(mHf.getPbannerData().size() * 10);
            holder.autoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }
                @Override
                public void onPageSelected(int position) {
                    nowPosition = position;
                    markNowPosition = position % PAGES;
                    holder.page_view.setText(markNowPosition+1 +" / "+ PAGES +" +");
                }
            });

            holder.page_view.setText("1 / "+ mHf.getPbannerData().size()+" +");
            holder.page_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), BannerStayAllActivity.class);
                    mHf.startActivityForResult(intent, 70);
                }
            });

            holder.autoViewPager.startAutoScroll();

            holder.autoViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("xxxxx", (holder.autoViewPager.getChildAt(0).getWidth()*0.39)+"");
                    RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lparam.height = (int)(holder.autoViewPager.getChildAt(0).getWidth()*0.33); //39
                    lparam.topMargin = Util.dptopixel(mHf.getActivity(), 15);
                    lparam.leftMargin = Util.dptopixel(mHf.getActivity(), 16);
                    lparam.rightMargin = Util.dptopixel(mHf.getActivity(), 16);

                    holder.autoViewPager.setLayoutParams(lparam);

                    RelativeLayout.LayoutParams lparam2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lparam2.topMargin = -(Util.dptopixel(mHf.getActivity(), 17) - lparam.height);
                    lparam2.rightMargin = Util.dptopixel(mHf.getActivity(), 24);
                    lparam2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    holder.page_view.setLayoutParams(lparam2);
                }
            }, 100);

        }
    }

    private void setPrivateDealView(HorizontalViewHolder holder, int type) {
        if(privateAdapter == null) {
            privateAdapter = new PrivateDealHotelAdapter(mHf.getPrivateDealItem(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(privateAdapter);
            holder.mMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), PrivateDaelAllActivity.class);
                    mHf.startActivityForResult(intent, 70);
                }
            });

            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;
        LinearLayout main_view;

        HorizontalViewHolder(View itemView, final int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mMoreView = (TextView) itemView.findViewById(R.id.all_view);
            main_view = (LinearLayout) itemView.findViewById(R.id.main_view);

            setTitle(mTitle, page);

            mMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public class HorizontalThemeViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        LinearLayout background_view;
        LinearLayout btn_moreproduct;

        HorizontalThemeViewHolder(View itemView, final int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            background_view = (LinearLayout) itemView.findViewById(R.id.background_view);
            btn_moreproduct = (LinearLayout) itemView.findViewById(R.id.btn_moreproduct);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;

        FooterViewHolder(View itemView, int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mMoreView = (TextView) itemView.findViewById(R.id.all_view);

            mTitle.setVisibility(View.GONE);
            mMoreView.setVisibility(View.GONE);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;

        HeaderViewHolder(View itemView, int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mMoreView = (TextView) itemView.findViewById(R.id.all_view);
            mTitle.setVisibility(View.GONE);
            mMoreView.setVisibility(View.GONE);
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        ViewPagerCustom autoViewPager;
        TextView page_view;

        BannerViewHolder(View itemView, int page) {
            super(itemView);
            autoViewPager = (ViewPagerCustom) itemView.findViewById(R.id.autoViewPager);
            page_view = (TextView) itemView.findViewById(R.id.page);
        }
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;

        VerticalViewHolder(View itemView, int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mMoreView = (TextView) itemView.findViewById(R.id.all_view);

            setTitle(mTitle, page);

            mMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void setHeaderRefresh(){
        if(headerAdapter != null){
            headerAdapter.notifyDataSetChanged();
        }
    }

    public void setAllRefresh(){
        if(hotelAdapter != null) {
            hotelAdapter.notifyDataSetChanged();
        }
        if(themeAdapter != null) {
            themeAdapter.notifyDataSetChanged();
        }
        if(privateAdapter != null) {
            privateAdapter.notifyDataSetChanged();
        }
    }

    public void setTitle(TextView title, int page){
        if(title != null){
            switch (page) {
                case BANNER:
                    title.setText("베너");
                    break;
                case PROMOTION:
                    title.setText("제목 받아서");
                    break;
                case SPECIAL:
                    title.setText("특별한 여행 제안");
                    break;
                case PRIVATEDEAL:
                    SpannableStringBuilder builder = new SpannableStringBuilder("오늘의 프라이빗딜 원하는 가격을 직접 제안!");
                    builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.privateview)), 4, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.graytxt)), 10, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new RelativeSizeSpan(0.8f), 10, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setText(builder);
                    break;
                case HOTDEAL_HOTEL:
                    SpannableStringBuilder builder2 = new SpannableStringBuilder("단독핫딜 호텔나우만의 최저가 상품");
                    builder2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.graytxt)), 5, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder2.setSpan(new RelativeSizeSpan(0.8f), 5, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setText(builder2);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if(items != null && !items.isEmpty()) {
            count = items.size();
        }

        return count;
    }
}
