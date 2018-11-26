package com.hotelnow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.RecyclerItemClickListener;
import com.hotelnow.utils.ViewPagerCustom;

import java.util.List;

public class LeisureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> items;
    private LeisureFragment mLf;
    private DbOpenHelper dbHelper;
    private LayoutInflater inflater;
    private final int HEADER = 0; // 하단
    private final int BANNER =1; // viewpager
    private final int HOTDEAL_ACTIVITY = 2; // 엑티비티 핫딜
    private final int PROMOTION = 3; // 변경되는 프로모션 horizontal
    private final int SPECIAL = 4; // 변경되는 프로모션 vertical
    private final int FOOTER = 5; // 변경되는 프로모션 vertical
    private BannerPagerHotelAdapter bannerAdapter = null;
    private FooterHAAdapter footAdapter = null;
    private HeaderLAdapter headerAdapter = null;
    private ThemeSpecialLeisureAdapter themeSAdapter = null;
    private ThemeLeisureAdapter themeAdapter = null;
    private ActivityHotDealLeisureAdapter acitivityAdapter = null;
    private static int nowPosition = 0;
    public static int markNowPosition = 0;
    private static int PAGES = 0;

    public LeisureAdapter(Context context, LeisureFragment Lf, List<Object> items, DbOpenHelper dbHelper) {
        this.context = context;
        this.items = items;
        this.mLf = Lf;
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
                holder = new LeisureAdapter.BannerViewHolder(view, BANNER);
                break;
            case PROMOTION:
                view = inflater.inflate(R.layout.layout_theme_horizontal, parent, false);
                holder = new LeisureAdapter.HorizontalThemeViewHolder(view, PROMOTION);
                break;
            case SPECIAL:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new LeisureAdapter.VerticalViewHolder(view, SPECIAL);
                break;
            case FOOTER:
                view = inflater.inflate(R.layout.layout_footer_vertical, parent, false);
                holder = new LeisureAdapter.FooterViewHolder(view, FOOTER);
                break;
            case HOTDEAL_ACTIVITY:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new LeisureAdapter.HorizontalViewHolder(view, HOTDEAL_ACTIVITY);
                break;
            case HEADER:
                view = inflater.inflate(R.layout.layout_header_vertical, parent, false);
                holder = new LeisureAdapter.HeaderViewHolder(view, HEADER);
                break;
            default:
                view = inflater.inflate(R.layout.layout_header_vertical, parent, false);
                holder = new LeisureAdapter.HeaderViewHolder(view, HEADER);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BANNER:
                BannerView((LeisureAdapter.BannerViewHolder) holder, holder.getItemViewType());
                break;
            case PROMOTION:
                setThemeView((LeisureAdapter.HorizontalThemeViewHolder) holder, holder.getItemViewType());
                break;
            case SPECIAL:
                setThemeSpecialView((LeisureAdapter.VerticalViewHolder) holder, holder.getItemViewType());
                break;
            case FOOTER:
                setBottomView((LeisureAdapter.FooterViewHolder) holder, holder.getItemViewType());
                break;
            case HEADER:
                setTopView((LeisureAdapter.HeaderViewHolder) holder, holder.getItemViewType());
                break;
            case HOTDEAL_ACTIVITY:
                setActivityHotDealView((LeisureAdapter.HorizontalViewHolder) holder, holder.getItemViewType());
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
        if (items.get(position) instanceof ActivityHotDealItem)
            return HOTDEAL_ACTIVITY;

        return -1;
    }

    private void setTopView(HeaderViewHolder holder, int type){
        if(headerAdapter == null) {
            headerAdapter = new HeaderLAdapter(mLf.getTopItem(), mLf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(headerAdapter);
        }
    }

    private void setBottomView(FooterViewHolder holder, int type){
        if(footAdapter == null) {
            footAdapter = new FooterHAAdapter(mLf.getDefaultItem(), mLf.getRecyclerView());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(footAdapter);
        }
    }

    private void setThemeSpecialView(VerticalViewHolder holder, int type) {
        if(themeSAdapter == null) {
            themeSAdapter = new ThemeSpecialLeisureAdapter(mLf.getThemeSpecialData(), mLf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(themeSAdapter);
        }
    }

    private void setThemeView(HorizontalThemeViewHolder holder, int type) {
        if(themeAdapter == null) {
            themeAdapter = new ThemeLeisureAdapter(mLf.getThemeData(), mLf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(themeAdapter);
            holder.background_view.setBackgroundColor(Color.parseColor("#"+mLf.getThemeData().get(0).getBack_color()));
            holder.mTitle.setText(mLf.getThemeData().get(0).getMain_title());
        }
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        if(bannerAdapter == null) {
            PAGES = mLf.getPbannerData().size();
            bannerAdapter = new BannerPagerHotelAdapter(context, mLf.getPbannerData());
            holder.autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착
            holder.autoViewPager.setCurrentItem(mLf.getPbannerData().size() * 10);
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

            holder.page_view.setText("1 / "+ mLf.getPbannerData().size()+" +");

            holder.autoViewPager.startAutoScroll();
        }
    }

    private void setActivityHotDealView(HorizontalViewHolder holder, int type) {
        if(acitivityAdapter == null) {
            acitivityAdapter = new ActivityHotDealLeisureAdapter(mLf.getActivityData(), mLf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(acitivityAdapter);
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

        HorizontalThemeViewHolder(View itemView, final int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            background_view = (LinearLayout) itemView.findViewById(R.id.background_view);
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
        if(themeAdapter != null) {
            themeAdapter.notifyDataSetChanged();
        }
        if(acitivityAdapter != null){
            acitivityAdapter.notifyDataSetChanged();
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
                case HOTDEAL_ACTIVITY:
                    SpannableStringBuilder builder2 = new SpannableStringBuilder("단독핫딜 호텔나우 만의 최저가 상품");
                    builder2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.graytxt)), 5, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder2.setSpan(new RelativeSizeSpan(0.8f), 5, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
