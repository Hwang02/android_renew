package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.utils.ViewPagerCustom;
import com.hotelnow.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hotelnow.fragment.home.HomeFragment.getBannerData;
import static com.hotelnow.fragment.home.HomeFragment.getHorizontalData;
import static com.hotelnow.fragment.home.HomeFragment.getVerticalData;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;
    private final int VERTICAL = 3;
    private final int HORIZONTAL = 2;
    private final int BANNER =1;

    public MainAdapter(Context context, ArrayList<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VERTICAL:
                view = inflater.inflate(R.layout.vertical, parent, false);
                holder = new VerticalViewHolder(view);
                break;
            case HORIZONTAL:
                view = inflater.inflate(R.layout.horizontal, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
            case BANNER:
                view = inflater.inflate(R.layout.layout_banner, parent, false);
                holder = new BannerViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.horizontal, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == BANNER) {
            BannerView((BannerViewHolder) holder);
        }
        else if (holder.getItemViewType() == HORIZONTAL) {
            horizontalView((HorizontalViewHolder) holder);
        }
        else if (holder.getItemViewType() == VERTICAL) {
            verticalView((VerticalViewHolder) holder);
        }
    }

    private void verticalView(VerticalViewHolder holder) {

        VerticalAdapter adapter = new VerticalAdapter(getVerticalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }


    private void horizontalView(HorizontalViewHolder holder) {
        HorizontalAdapter adapter = new HorizontalAdapter(getHorizontalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    private void BannerView(final BannerViewHolder holder) {
//        HorizontalAdapter adapter = new HorizontalAdapter(getHorizontalData());

        BannerPagerAdapter adapter = new BannerPagerAdapter(context, getBannerData());
        holder.autoViewPager.setAdapter(adapter); //Auto Viewpager에 Adapter 장착
//        holder.autoViewPager.setInterval(2000); // 페이지 넘어갈 시간 간격 설정
//        holder.autoViewPager.startAutoScroll(); //Auto Scroll 시작
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
            return VERTICAL;
        if (items.get(position) instanceof SingleHorizontal)
            return HORIZONTAL;
        if (items.get(position) instanceof Banner)
            return BANNER;
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
