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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.BannerAllActivity;
import com.hotelnow.activity.HotDealActivity;
import com.hotelnow.activity.RecentAllActivity;
import com.hotelnow.activity.ThemeSAllActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.fragment.model.RecentListItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.SubBannerItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.hotelnow.utils.ViewPagerCustom;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> items;
    private HomeFragment mHf;
    private final int FOOTER = 0; // 하단
    private final int BANNER = 1; // viewpager
    private final int KEYWORD = 2; // 키워드 horizontal
    private final int RECENT = 3; // 최근 본 horizontal
    private final int BANNER_MINI = 4; // viewpager
    private final int HOTDEAL_HOTEL = 5; // 호텔 핫딜 horizontal
    private final int HOTDEAL_ACTIVITY = 6; // 엑티비티 핫딜 horizontal
    private final int PROMOTION = 7; // 변경되는 프로모션 horizontal
    private final int SPECIAL = 8; // 변경되는 프로모션 vertical
    private final int PRIVATEDEAL = 9; // 프라이빗딜
    private DbOpenHelper dbHelper;
    private RecentAdapter recentAdapter = null;
    private PrivateDealAdapter privateAdapter = null;
    private BannerPagerAdapter bannerAdapter = null;
    private FooterAdapter footAdapter = null;
    private ThemeSpecialAdapter themeSAdapter = null;
    private ThemeAdapter themeAdapter = null;
    private ActivityHotDealAdapter acitivityAdapter = null;
    private KeyWordAdapter keyAdapter = null;
    private HotelHotDealAdapter hotelAdapter = null;
    private SubBannerPagerAdapter subbAdapter = null;
    private LayoutInflater inflater;
    private static int nowPosition = 0;
    public static int markNowPosition = 0;
    private static int PAGES = 0, PAGES2 = 0;

    public HomeAdapter(Context context, HomeFragment hf, List<Object> items, DbOpenHelper dbHelper) {
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
                view = inflater.inflate(R.layout.layout_banner, parent, false);
                holder = new BannerViewHolder(view, BANNER);
                break;
            case BANNER_MINI:
                view = inflater.inflate(R.layout.layout_subbanner, parent, false);
                holder = new BannerViewHolder(view, BANNER_MINI);
                break;
            case KEYWORD:
                view = inflater.inflate(R.layout.layout_key_horizontal, parent, false);
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
                view = inflater.inflate(R.layout.layout_footer_home_vertical, parent, false);
                holder = new FooterViewHolder(view, FOOTER);
                break;
            case PRIVATEDEAL:
                view = inflater.inflate(R.layout.layout_horizontal, parent, false);
                holder = new HorizontalViewHolder(view, PRIVATEDEAL);
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
                BannerSubView((BannerViewHolder) holder, holder.getItemViewType());
                break;
            case KEYWORD:
                setKeyWordView((HorizontalViewHolder) holder, holder.getItemViewType());
                break;
            case RECENT:
                setRecentView((HorizontalViewHolder) holder, holder.getItemViewType());
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
            case PRIVATEDEAL:
                setPrivateDealView((HorizontalViewHolder) holder, holder.getItemViewType());
                break;
        }
    }

    private void setRecentView(HorizontalViewHolder holder, int type) {
        if (recentAdapter == null) {

            holder.mMoreView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), RecentAllActivity.class);
                    mHf.startActivityForResult(intent, 80);
                }
            });

            recentAdapter = new RecentAdapter(mHf.getRecentListItem(), mHf, dbHelper, holder.mMoreView);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(recentAdapter);
            if (mHf.getRecentListItem().size() > 1) {
                holder.mMoreView.setVisibility(View.VISIBLE);
            } else {
                holder.mMoreView.setVisibility(View.GONE);
            }

            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void setBottomView(FooterViewHolder holder, int type) {
        if (footAdapter == null) {
            footAdapter = new FooterAdapter(mHf.getDefaultItem(), mHf.getRecyclerView(), mHf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(footAdapter);
        }
    }

    private void setThemeSpecialView(VerticalViewHolder holder, int type) { // 테마베
        if (themeSAdapter == null) {
            themeSAdapter = new ThemeSpecialAdapter(mHf.getThemeSpecialData(), mHf);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(themeSAdapter);
            holder.mMoreView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), ThemeSAllActivity.class);
                    mHf.startActivityForResult(intent, 80);
                }
            });
        }
    }

    private void setThemeView(HorizontalThemeViewHolder holder, int type) { // 컬러
        if (themeAdapter == null) {
            themeAdapter = new ThemeAdapter(mHf.getThemeData(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(themeAdapter);
            holder.background_view.setBackgroundColor(Color.parseColor("#" + mHf.getThemeData().get(0).getBack_color()));
            holder.mTitle.setText(mHf.getThemeData().get(0).getMain_title());
            holder.btn_moreproduct.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mHf.getThemeData().get(0).getTheme_flag().equals("H")) {
                        Intent intent = new Intent(mHf.getContext(), ThemeSpecialHotelActivity.class);
                        intent.putExtra("tid", mHf.getThemeData().get(0).getTheme_id());
                        mHf.startActivityForResult(intent, 80);
                    } else {
                        Intent intent = new Intent(mHf.getContext(), ThemeSpecialActivityActivity.class);
                        intent.putExtra("tid", mHf.getThemeData().get(0).getTheme_id());
                        mHf.startActivityForResult(intent, 80);
                    }
                }
            });

//            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
//            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void setHotelHotDealView(HorizontalViewHolder holder, int type) {
        if (hotelAdapter == null) {
            hotelAdapter = new HotelHotDealAdapter(mHf.getHotelData(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(hotelAdapter);

            if (mHf.getHotelData().size() > 1) {
                holder.mMoreView.setVisibility(View.VISIBLE);
            } else {
                holder.mMoreView.setVisibility(View.GONE);
            }

            holder.mMoreView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    TuneWrap.Event("stay_hotdeal_list");
                    Intent intent = new Intent(mHf.getContext(), HotDealActivity.class);
                    intent.putExtra("tab", 0);
                    mHf.startActivityForResult(intent, 80);
                }
            });

            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void setKeyWordView(HorizontalViewHolder holder, int type) {
        if (keyAdapter == null) {
            keyAdapter = new KeyWordAdapter(mHf.getKeywordData(), context);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(keyAdapter);
            holder.recyclerView.setBackgroundResource(R.color.white);
            holder.main_view.setBackgroundResource(R.color.white);
            holder.mMoreView.setVisibility(View.GONE);

            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void setActivityHotDealView(HorizontalViewHolder holder, int type) {
        if (acitivityAdapter == null) {
            acitivityAdapter = new ActivityHotDealAdapter(mHf.getActivityData(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(acitivityAdapter);

            if (mHf.getActivityData().size() > 1) {
                holder.mMoreView.setVisibility(View.VISIBLE);
            } else {
                holder.mMoreView.setVisibility(View.GONE);
            }

            holder.mMoreView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), HotDealActivity.class);
                    intent.putExtra("tab", 1);
                    mHf.startActivityForResult(intent, 80);
                }
            });
            LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
            linearSnapHelper.attachToRecyclerView(holder.recyclerView);
        }
    }

    private void BannerView(final BannerViewHolder holder, int type) {
        if (bannerAdapter == null) {
            if (mHf.getPbannerData().size() == 1) {
                holder.autoViewPager.canSwipe(false);
                holder.autoViewPager.setCurrentItem(mHf.getEbannerData().size());
            } else {
                holder.autoViewPager.canSwipe(true);
                holder.autoViewPager.setCurrentItem(mHf.getEbannerData().size() * 10);
            }

            PAGES = mHf.getPbannerData().size();
            bannerAdapter = new BannerPagerAdapter(context, mHf.getPbannerData(), mHf);
            holder.autoViewPager.setClipToPadding(true);
            holder.autoViewPager.setOffscreenPageLimit(0);
            holder.autoViewPager.setPageMargin(Util.dptopixel(mHf.getActivity(), 8));
            holder.autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착

            if (mHf.getPbannerData().size() == 1) {
                holder.autoViewPager.canSwipe(false);
                holder.autoViewPager.setCurrentItem(mHf.getPbannerData().size());
            } else {
                holder.autoViewPager.canSwipe(true);
                holder.autoViewPager.setCurrentItem(mHf.getPbannerData().size() * 10);
            }
            holder.autoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }

                @Override
                public void onPageSelected(int position) {
                    nowPosition = position;
                    markNowPosition = position % PAGES;
                    holder.page_view.setText(markNowPosition + 1 + " / " + PAGES + " +");
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            holder.page_view.setText("1 / " + mHf.getPbannerData().size() + " +");
            if (mHf.getPbannerData().size() > 1)
                holder.autoViewPager.startAutoScroll();

            holder.page_view.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mHf.getContext(), BannerAllActivity.class);
                    mHf.startActivityForResult(intent, 80);
                }
            });

            holder.autoViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("xxxxx", (holder.autoViewPager.getChildAt(0).getWidth() * 0.54) + "");
                    RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lparam.height = (int) (holder.autoViewPager.getChildAt(0).getWidth() * 0.5); //*0.54
                    lparam.topMargin = Util.dptopixel(mHf.getActivity(), 15);
                    lparam.bottomMargin = Util.dptopixel(mHf.getActivity(), 15);
                    lparam.leftMargin = Util.dptopixel(mHf.getActivity(), 16);
                    lparam.rightMargin = Util.dptopixel(mHf.getActivity(), 16);
                    holder.autoViewPager.setLayoutParams(lparam);

                    RelativeLayout.LayoutParams lparam2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lparam2.topMargin = -(Util.dptopixel(mHf.getActivity(), 20) - lparam.height);
                    lparam2.rightMargin = Util.dptopixel(mHf.getActivity(), 24);
                    lparam2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    holder.page_view.setLayoutParams(lparam2);
                }
            }, 100);
        }
    }

    private void BannerSubView(final BannerViewHolder holder, int type) {
        if (subbAdapter == null) {
            PAGES2 = mHf.getEbannerData().size();
            subbAdapter = new SubBannerPagerAdapter(context, mHf.getEbannerData(), mHf);

            if (mHf.getEbannerData().size() == 1) {
                holder.autoViewPager.canSwipe(false);
                holder.autoViewPager.setCurrentItem(mHf.getEbannerData().size());
            } else {
                holder.autoViewPager.canSwipe(true);
                holder.autoViewPager.setCurrentItem(mHf.getEbannerData().size() * 10);
            }
            holder.autoViewPager.setAdapter(subbAdapter); //Auto Viewpager에 Adapter 장착

            holder.autoViewPager.setPageMargin(20);
            holder.autoViewPager.setOffscreenPageLimit(mHf.getEbannerData().size());
            holder.autoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    holder.autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }

                @Override
                public void onPageSelected(int position) {
                    nowPosition = position;
                    markNowPosition = position % PAGES2;
                    holder.page_view.setText(markNowPosition + 1 + " / " + PAGES2);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            holder.page_view.setText("1 / " + mHf.getEbannerData().size());

            holder.autoViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    LogUtil.e("xxxxx", (holder.autoViewPager.getChildAt(0).getWidth() * 0.7) + "");
                    RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lparam.height = (int) (holder.autoViewPager.getChildAt(0).getWidth() * 0.9); //*0.8
                    lparam.topMargin = Util.dptopixel(mHf.getActivity(), 7);
                    lparam.bottomMargin = Util.dptopixel(mHf.getActivity(), 4);
                    holder.autoViewPager.setLayoutParams(lparam);

                }
            }, 100);
        }
    }

    private void setPrivateDealView(HorizontalViewHolder holder, int type) {
        if (privateAdapter == null) {
            privateAdapter = new PrivateDealAdapter(mHf.getPrivateDealItem(), mHf, dbHelper);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(privateAdapter);
        }
    }

    public void refreshRecent() {
        if (recentAdapter != null) {
            recentAdapter.notifyDataSetChanged();
        }
    }

    public void allRefresh(boolean isRecent) {
        if (recentAdapter != null && isRecent) {
            recentAdapter.notifyDataSetChanged();
        }
        if (privateAdapter != null) {
            privateAdapter.notifyDataSetChanged();
        }
        if (acitivityAdapter != null) {
            acitivityAdapter.notifyDataSetChanged();
        }
        if (hotelAdapter != null) {
            hotelAdapter.notifyDataSetChanged();
        }
        if (themeAdapter != null) {
            themeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (items != null && !items.isEmpty()) {
            count = items.size();
        }

        return count;
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof BannerItem)
            return BANNER;
        if (items.get(position) instanceof ThemeSpecialItem)
            return SPECIAL;
        if (items.get(position) instanceof KeyWordItem)
            return KEYWORD;
        if (items.get(position) instanceof RecentListItem)
            return RECENT;
        if (items.get(position) instanceof StayHotDealItem)
            return HOTDEAL_HOTEL;
        if (items.get(position) instanceof ActivityHotDealItem)
            return HOTDEAL_ACTIVITY;
        if (items.get(position) instanceof DefaultItem)
            return FOOTER;
        if (items.get(position) instanceof ThemeItem)
            return PROMOTION;
        if (items.get(position) instanceof SubBannerItem)
            return BANNER_MINI;
        if (items.get(position) instanceof PrivateDealItem)
            return PRIVATEDEAL;

        return -1;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        TextView mMoreView;
        LinearLayout main_view;
        LinearLayout gap;

        HorizontalViewHolder(View itemView, final int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mMoreView = (TextView) itemView.findViewById(R.id.all_view);
            main_view = (LinearLayout) itemView.findViewById(R.id.main_view);
            gap = (LinearLayout) itemView.findViewById(R.id.gap);

            if (page == KEYWORD || page == RECENT) {
                gap.setVisibility(View.GONE);
            } else {
                gap.setVisibility(View.VISIBLE);
            }

            setTitle(mTitle, page);
        }
    }

    public class HorizontalThemeViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mTitle;
        LinearLayout background_view;
        TextView btn_moreproduct;

        HorizontalThemeViewHolder(View itemView, final int page) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recyclerView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            background_view = (LinearLayout) itemView.findViewById(R.id.background_view);
            btn_moreproduct = (TextView) itemView.findViewById(R.id.btn_moreproduct);

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
        TextView page_view;
        RelativeLayout main_view;

        BannerViewHolder(View itemView, int page) {
            super(itemView);
            autoViewPager = (ViewPagerCustom) itemView.findViewById(R.id.autoViewPager);
            page_view = (TextView) itemView.findViewById(R.id.page);
            if(page == BANNER_MINI){
                main_view = (RelativeLayout) itemView.findViewById(R.id.main_view);
            }
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
        }
    }

    public void setTitle(TextView title, int page) {
        if (title != null) {
            switch (page) {
                case BANNER:
                    title.setText("베너");
                    break;
                case BANNER_MINI:
                    title.setText("작은베너");
                    break;
                case KEYWORD:
                    title.setText("요즘 뜨는 인기 키워드");
                    break;
                case RECENT:
                    title.setText("최근 본 상품");
                    break;
                case HOTDEAL_HOTEL:
                    SpannableStringBuilder builder = new SpannableStringBuilder(context.getResources().getText(R.string.hotdeal_hotel));
                    builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.purple)), 6, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setText(builder);
                    break;
                case HOTDEAL_ACTIVITY:
                    SpannableStringBuilder builder2 = new SpannableStringBuilder(context.getResources().getText(R.string.hotdeal_activity));
                    builder2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.activitytxt)), 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setText(builder2);
                    break;
                case SPECIAL:
                    title.setText("호텔나우 추천 테마");
                    break;
                case PRIVATEDEAL:
                    title.setText("프라이빗딜");
                    break;
            }
        }
    }
}
