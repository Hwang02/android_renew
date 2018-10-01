package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.utils.ViewPagerCustom;
import com.hotelnow.utils.RecyclerItemClickListener;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> items;
    private HomeFragment mHf;
    private LayoutInflater inflater;
    private final int FOOTER = 0; // 하단
    private final int BANNER =1; // viewpager
    private final int KEYWORD = 2; // 키워드 horizontal
    private final int RECENT = 3; // 최근 본 horizontal
    private final int BANNER_MINI = 4; // viewpager
    private final int HOTDEAL_HOTEL = 5; // 호텔 핫딜 horizontal
    private final int HOTDEAL_ACTIVITY = 6; // 엑티비티 핫딜 horizontal
    private final int PROMOTION = 7; // 변경되는 프로모션 horizontal
    private final int SPECIAL = 8; // 변경되는 프로모션 vertical

    public HomeAdapter(Context context, HomeFragment hf, List<Object> items) {
        this.context = context;
        this.items = items;
        this.mHf = hf;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case BANNER:
                view = inflater.inflate(R.layout.layout_banner, parent, false);
                holder = new BannerViewHolder(view, BANNER);
                break;
            case BANNER_MINI:
                view = inflater.inflate(R.layout.layout_banner, parent, false);
                holder = new BannerViewHolder(view, BANNER_MINI);
                break;
            case KEYWORD:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, KEYWORD);
                break;
            case RECENT:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, RECENT);
                break;
            case HOTDEAL_HOTEL:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, HOTDEAL_HOTEL);
                break;
            case HOTDEAL_ACTIVITY:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, HOTDEAL_ACTIVITY);
                break;
            case PROMOTION:
                view = inflater.inflate(R.layout.layout_theme_horizontal, parent, false);
                holder = new HorizontalThemeViewHolder(view, PROMOTION);
                break;
            case SPECIAL:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new VerticalViewHolder(view, SPECIAL);
                break;
            case FOOTER:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new FooterViewHolder(view, FOOTER);
                break;
            default:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, RECENT);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BANNER:
                BannerView((BannerViewHolder) holder, holder.getItemViewType());
                break;
            case BANNER_MINI:
                BannerView((BannerViewHolder) holder, holder.getItemViewType());
                break;
            case KEYWORD:
                break;
            case RECENT:
                break;
            case HOTDEAL_HOTEL:
                setHotelHotDealView((HorizontalViewHolder) holder, holder.getItemViewType());
                break;
            case HOTDEAL_ACTIVITY:
                setActivityHotDealView((HorizontalViewHolder) holder, holder.getItemViewType());
                break;
            case PROMOTION:
                setThemeView((HorizontalThemeViewHolder) holder, holder.getItemViewType());
                break;
            case SPECIAL:
                setThemeSpecialView((VerticalViewHolder) holder, holder.getItemViewType());
                break;
            case FOOTER:
                setBottomView((FooterViewHolder) holder, holder.getItemViewType());
                break;

        }
    }

//    private void verticalView(VerticalViewHolder holder, int type) {
//        VerticalAdapter adapter = new VerticalAdapter(mHf.getVerticalData());
//        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        holder.recyclerView.setAdapter(adapter);
//    }

    private void setBottomView(FooterViewHolder holder, int type){
        FooterAdapter adapter = new FooterAdapter(mHf.getDefaultItem(), mHf.getRecyclerView());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }

    private void setThemeSpecialView(VerticalViewHolder holder, int type) {
        ThemeSpecialAdapter adapter = new ThemeSpecialAdapter(mHf.getThemeSpecialData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }

    private void setThemeView(HorizontalThemeViewHolder holder, int type) {
        ThemeAdapter adapter = new ThemeAdapter(mHf.getThemeData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);

    }

    private void setHotelHotDealView(HorizontalViewHolder holder, int type) {
        HotelHotDealAdapter adapter = new HotelHotDealAdapter(mHf.getHotelData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);

    }

    private void setActivityHotDealView(HorizontalViewHolder holder, int type) {
        ActivityHotDealAdapter adapter = new ActivityHotDealAdapter(mHf.getActivityData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        BannerPagerAdapter adapter = new BannerPagerAdapter(context, mHf.getBannerData());
        holder.autoViewPager.setAdapter(adapter); //Auto Viewpager에 Adapter 장착
        holder.autoViewPager.setCurrentItem(mHf.getBannerData().size() * 10);
        holder.autoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });
    }


    @Override
    public int getItemCount() {
        int count = 0;

        if(items != null && !items.isEmpty()) {
            count = items.size();
        }

       return count;
    }

    @Override
    public int getItemViewType(int position) {
//        if (items.get(position) instanceof Banner)
//            return BANNER;
        if (items.get(position) instanceof ThemeSpecialItem)
            return SPECIAL;
//        if (items.get(position) instanceof SingleHorizontal)
//            return KEYWORD;
//        if (items.get(position) instanceof SingleHorizontal)
//            return RECENT;
        if (items.get(position) instanceof StayHotDealItem)
            return HOTDEAL_HOTEL;
        if (items.get(position) instanceof ActivityHotDealItem)
            return HOTDEAL_ACTIVITY;
        if (items.get(position) instanceof DefaultItem)
            return FOOTER;
        if (items.get(position) instanceof ThemeItem)
            return PROMOTION;
//        if (items.get(position) instanceof Banner)
//            return BANNER_MINI;

        return -1;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;

        HorizontalViewHolder(View itemView, int page) {
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
                    Toast.makeText(context,position+"번 째 아이템 클릭 horizon",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mHf.getActivity(), DetailHotelActivity.class);
                    context.startActivity(intent);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

            }));
        }
    }

    public class HorizontalThemeViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;

        HorizontalThemeViewHolder(View itemView, int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);

            setTitle(mTitle, page);

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

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        ViewPagerCustom autoViewPager;

        BannerViewHolder(View itemView, int page) {
            super(itemView);
            autoViewPager = (ViewPagerCustom)itemView.findViewById(R.id.autoViewPager);
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

    public void setTitle(TextView title, int page){
        if(title != null){
            switch (page) {
                case BANNER:
                    title.setText("베너");
                    break;
                case BANNER_MINI:
                    title.setText("작은베너");
                    break;
                case KEYWORD:
                    title.setText("요즘 뜨는 키워드");
                    break;
                case RECENT:
                    title.setText("최근 본 상품");
                    break;
                case HOTDEAL_HOTEL:
                    SpannableStringBuilder builder = new SpannableStringBuilder(context.getResources().getText(R.string.hotdeal_hotel));
                    builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.purple)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.append(builder);
                    break;
                case HOTDEAL_ACTIVITY:
                    SpannableStringBuilder builder2 = new SpannableStringBuilder(context.getResources().getText(R.string.hotdeal_activity));
                    builder2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.activitytxt)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.append(builder2);
                    break;
                case PROMOTION:
                    title.setText("제목 받아서");
                    break;
                case SPECIAL:
                    title.setText("특별한 여행 제안");
                    break;
            }
        }
    }

}
