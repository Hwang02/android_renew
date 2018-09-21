package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.fragment.favorite.FavoriteFragment;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.utils.RecyclerItemClickListener;
import com.hotelnow.utils.ViewPagerCustom;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hotelnow.fragment.home.HomeFragment.getBannerData;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;
    private FavoriteFragment mHf;
    private final int BANNER =1; // viewpager
    private final int KEYWORD = 2; // 키워드 horizontal
    private final int RECENT = 3; // 최근 본 horizontal
    private final int BANNER_MINI = 4; // viewpager
    private final int HOTDEAL_HOTEL = 5; // 호텔 핫딜 horizontal
    private final int HOTDEAL_ACTIVITY = 6; // 엑티비티 핫딜 horizontal
    private final int PROMOTION = 7; // 변경되는 프로모션 horizontal
    private final int SPECIAL = 8; // 변경되는 프로모션 vertical



    public FavoriteAdapter(Context context, FavoriteFragment hf, ArrayList<Object> items) {
        this.context = context;
        this.items = items;
        this.mHf = hf;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case SPECIAL:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new VerticalViewHolder(view);
                break;
            case KEYWORD:
            case RECENT:
            case HOTDEAL_HOTEL:
            case HOTDEAL_ACTIVITY:
            case PROMOTION:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
            case BANNER:
            case BANNER_MINI:
                view = inflater.inflate(R.layout.layout_banner, parent, false);
                holder = new BannerViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == BANNER || holder.getItemViewType() == BANNER_MINI) {
            BannerView((BannerViewHolder) holder, holder.getItemViewType());
        }
        else if (holder.getItemViewType() == KEYWORD || holder.getItemViewType() == RECENT || holder.getItemViewType() == HOTDEAL_HOTEL
                || holder.getItemViewType() == HOTDEAL_ACTIVITY || holder.getItemViewType() == PROMOTION) {
            horizontalView((HorizontalViewHolder) holder, holder.getItemViewType());
        }
        else if (holder.getItemViewType() == SPECIAL) {
            verticalView((VerticalViewHolder) holder, holder.getItemViewType());
        }
    }

    private void verticalView(VerticalViewHolder holder, int type) {
        VerticalAdapter adapter = new VerticalAdapter(mHf.getVerticalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }


    private void horizontalView(HorizontalViewHolder holder, int type) {
        HorizontalAdapter adapter = new HorizontalAdapter(mHf.getHorizontalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        BannerPagerAdapter adapter = new BannerPagerAdapter(context, mHf.getBannerData());
        holder.autoViewPager.setAdapter(adapter); //Auto Viewpager에 Adapter 장착
        holder.autoViewPager.setCurrentItem(getBannerData().size() * 10);
        holder.autoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof SingleVertical)
            return SPECIAL;
        if (items.get(position) instanceof SingleHorizontal)
            return KEYWORD;
        if (items.get(position) instanceof SingleHorizontal)
            return RECENT;
        if (items.get(position) instanceof SingleHorizontal)
            return HOTDEAL_HOTEL;
        if (items.get(position) instanceof SingleHorizontal)
            return HOTDEAL_ACTIVITY;
        if (items.get(position) instanceof SingleHorizontal)
            return PROMOTION;
        if (items.get(position) instanceof Banner)
            return BANNER;
        if (items.get(position) instanceof Banner)
            return BANNER_MINI;

        return -1;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            recyclerView.requestDisallowInterceptTouchEvent(true);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getApplicationContext(),position+"번 째 아이템 클릭 horizon",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

            }));
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        ViewPagerCustom autoViewPager;

        BannerViewHolder(View itemView) {
            super(itemView);
            autoViewPager = (ViewPagerCustom)itemView.findViewById(R.id.autoViewPager);
        }
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        VerticalViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getApplicationContext(),position+"번 째 아이템 클릭 vertical",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

}
