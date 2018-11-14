package com.hotelnow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.fragment.hotel.HotelFragment;
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
    private PrivateDealHotelAdapter privateAdapter = null;
    private BannerPagerHotelAdapter bannerAdapter = null;
    private FooterHAAdapter footAdapter = null;
    private HeaderAdapter headerAdapter = null;
    private ThemeSpecialAdapter themeSAdapter = null;
    private ThemeAdapter themeAdapter = null;

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

        return -1;
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
            themeSAdapter = new ThemeSpecialAdapter(mHf.getThemeSpecialData());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(themeSAdapter);
        }
    }

    private void setThemeView(HorizontalThemeViewHolder holder, int type) {
        if(themeAdapter == null) {
            themeAdapter = new ThemeAdapter(mHf.getThemeData());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(themeAdapter);
            holder.background_view.setBackgroundColor(Color.parseColor("#"+mHf.getThemeData().get(0).getBack_color()));
            holder.mTitle.setText(mHf.getThemeData().get(0).getMain_title());
        }
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        if(bannerAdapter == null) {
            bannerAdapter = new BannerPagerHotelAdapter(context, mHf.getPbannerData());
            holder.autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착
            holder.autoViewPager.setCurrentItem(mHf.getPbannerData().size() * 10);
            holder.autoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }
            });
            holder.autoViewPager.startAutoScroll();
        }
    }

    private void setPrivateDealView(HorizontalViewHolder holder, int type) {
        if(privateAdapter == null) {
            privateAdapter = new PrivateDealHotelAdapter(mHf.getPrivateDealItem(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(privateAdapter);
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

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Toast.makeText(context,position+"번 째 아이템 클릭 horizon",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

            }));
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

        BannerViewHolder(View itemView, int page) {
            super(itemView);
            autoViewPager = (ViewPagerCustom) itemView.findViewById(R.id.autoViewPager);
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

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(context,position+"번 째 아이템 클릭 vertical",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

    public void setHeaderRefresh(){
        if(headerAdapter != null){
            headerAdapter.notifyDataSetChanged();
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
                    title.setText("오늘의 프라이빗딜");
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