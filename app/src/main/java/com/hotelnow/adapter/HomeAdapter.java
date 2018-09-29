package com.hotelnow.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.utils.ViewPagerCustom;
import com.hotelnow.utils.RecyclerItemClickListener;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> items;
    private HomeFragment mHf;
    private LayoutInflater inflater;
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
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, PROMOTION);
                break;
            case SPECIAL:
                view = inflater.inflate(R.layout.layout_vertical, parent, false);
                holder = new VerticalViewHolder(view, SPECIAL);
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
        HotelHotDealAdapter adapter = new HotelHotDealAdapter(mHf.getStayHotelData());
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
        if (items.get(position) instanceof SingleVertical)
            return SPECIAL;
//        if (items.get(position) instanceof SingleHorizontal)
//            return KEYWORD;
//        if (items.get(position) instanceof SingleHorizontal)
//            return RECENT;
        if (items.get(position) instanceof StayHotDealItem)
            return HOTDEAL_HOTEL;
//        if (items.get(position) instanceof SingleHorizontal)
//            return HOTDEAL_ACTIVITY;
//        if (items.get(position) instanceof SingleHorizontal)
//            return PROMOTION;
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
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

            }));
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

        VerticalViewHolder(View itemView, int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
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
                    title.setText("숙소 단독 핫딜");
                    break;
                case HOTDEAL_ACTIVITY:
                    title.setText("액티비티 단독 핫딜");
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
