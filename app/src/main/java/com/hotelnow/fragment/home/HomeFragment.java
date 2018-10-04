package com.hotelnow.fragment.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.adapter.HomeAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mHomeBinding;
    private List<Object> objects = null;
    public ArrayList<StayHotDealItem> mHotelItem = new ArrayList<>();
    public ArrayList<ActivityHotDealItem> mActivityItem = new ArrayList<>();
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    public ArrayList<ThemeItem> mThemeItem = new ArrayList<>();
    public ArrayList<DefaultItem> mDefaultItem = new ArrayList<>();
    private HomeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View inflate = mHomeBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        objects = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), HomeFragment.this, objects);
        mHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeBinding.recyclerView.setAdapter(adapter);

        mHomeBinding.btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        getObject();
    }

    private void getObject() {
        String url = CONFIG.mainHome;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(obj.has("promotion_banners")){

                    }
                    if(obj.has("popular_keywords")){

                    }
                    if(obj.has("event_banners")){

                    }
                    if(obj.has("stay_hot_deals")){
                        JSONArray mStay = new JSONArray(obj.getJSONArray("stay_hot_deals").toString());
                        for(int i = 0; i < mStay.length(); i++){
                            mHotelItem.add(new StayHotDealItem(
                                    mStay.getJSONObject(i).getString("hotel_id"),
                                    mStay.getJSONObject(i).getString("name"),
                                    mStay.getJSONObject(i).getString("category"),
                                    mStay.getJSONObject(i).getString("landscape"),
                                    mStay.getJSONObject(i).getString("street1"),
                                    mStay.getJSONObject(i).getString("latitude"),
                                    mStay.getJSONObject(i).getString("longuitude"),
                                    mStay.getJSONObject(i).getString("privateDealYN"),
                                    mStay.getJSONObject(i).getString("sale_price"),
                                    mStay.getJSONObject(i).getString("sale_rate"),
                                    mStay.getJSONObject(i).getString("items_quantity"),
                                    mStay.getJSONObject(i).getString("special_msg"),
                                    mStay.getJSONObject(i).getString("grade_score")
                            ));
                        }
                        objects.add(mHotelItem.get(0));
                    }
                    if(obj.has("activity_hot_deals")){
                        JSONArray mActivity = new JSONArray(obj.getJSONArray("activity_hot_deals").toString());
                        for(int i = 0; i < mActivity.length(); i++){
                            mActivityItem.add(new ActivityHotDealItem(
                                    mActivity.getJSONObject(i).getString("deal_id"),
                                    mActivity.getJSONObject(i).getString("name"),
                                    mActivity.getJSONObject(i).getString("normal_price"),
                                    mActivity.getJSONObject(i).getString("sale_price"),
                                    mActivity.getJSONObject(i).getString("sale_rate"),
                                    mActivity.getJSONObject(i).getString("latitude"),
                                    mActivity.getJSONObject(i).getString("longitude"),
                                    mActivity.getJSONObject(i).getString("benefit_text"),
                                    mActivity.getJSONObject(i).getString("img_url"),
                                    mActivity.getJSONObject(i).getString("location"),
                                    mActivity.getJSONObject(i).getString("category"),
                                    mActivity.getJSONObject(i).getString("distance_real"),
                                    mActivity.getJSONObject(i).getString("coupon_count")
                            ));
                        }
                        objects.add(mActivityItem.get(0));

                    }
                    if(obj.has("theme_show")){
                        JSONObject mTheme_show = obj.getJSONObject("theme_show");
                        JSONObject mTheme = mTheme_show.getJSONObject("theme");
                        JSONArray mItems = new JSONArray(mTheme_show.getJSONArray("lists").toString());
                        mTheme.getString("id");
                        mTheme.getString("title");
                        mTheme.getString("sub_title");
                        mTheme.getString("filter_start_color");
                        mTheme.getString("subject");
                        mTheme.getString("detail");
                        mTheme.getString("notice");

                        for(int i = 0; i < mItems.length(); i++){
                            mThemeItem.add(new ThemeItem(
                                    mItems.getJSONObject(i).getString("id"),
                                    mItems.getJSONObject(i).getString("name"),
                                    mItems.getJSONObject(i).getString("landscape"),
                                    mItems.getJSONObject(i).getString("product_id"),
                                    mTheme.getString("id"),
                                    mItems.getJSONObject(i).getString("wo"),
                                    mTheme.getString("filter_start_color")
                            ));
                        }
                        objects.add(mThemeItem.get(0));
                    }
                    if(obj.has("theme_lists")){
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        for(int i = 0; i < mThemeS.length(); i++){
                            mThemeSItem.add(new ThemeSpecialItem(
                                    mThemeS.getJSONObject(i).getString("id"),
                                    mThemeS.getJSONObject(i).getString("title"),
                                    mThemeS.getJSONObject(i).getString("sub_title"),
                                    mThemeS.getJSONObject(i).getString("img_main_top"),
                                    mThemeS.getJSONObject(i).getString("img_main_list"),
                                    mThemeS.getJSONObject(i).getString("theme_flag"),
                                    mThemeS.getJSONObject(i).getString("subject"),
                                    mThemeS.getJSONObject(i).getString("detail"),
                                    mThemeS.getJSONObject(i).getString("notice"),
                                    mThemeS.getJSONObject(i).getString("img_background")
                            ));
                        }
                        objects.add(mThemeSItem.get(0));
                    }

                    mDefaultItem.add(new DefaultItem("bottom"));
                    objects.add(mDefaultItem.get(0));

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //test용 데이터
//    public static ArrayList<SingleVertical> getVerticalData() {
//        ArrayList<SingleVertical> singleVerticals = new ArrayList<>();
//        singleVerticals.add(new SingleVertical("Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", R.drawable.charlie));
//        singleVerticals.add(new SingleVertical("Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", R.drawable.mrbean));
//        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
//        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
//        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
//        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
//        return singleVerticals;
//    }

    public ArrayList<StayHotDealItem> getHotelData() {
        return mHotelItem;
    }

    public ArrayList<ActivityHotDealItem> getActivityData() {
        return mActivityItem;
    }

    public ArrayList<ThemeItem> getThemeData() {
        return mThemeItem;
    }

    public ArrayList<ThemeSpecialItem> getThemeSpecialData() {
        return mThemeSItem;
    }

    public ArrayList<DefaultItem> getDefaultItem() {
        return mDefaultItem;
    }

    public static ArrayList<Banner> getBannerData() {
        ArrayList<Banner> data = new ArrayList<>(); //이미지 url를 저장하는 arraylist
        data.add(new Banner("https://upload.wikimedia.org/wikipedia/en/thumb/2/24/SpongeBob_SquarePants_logo.svg/1200px-SpongeBob_SquarePants_logo.svg.png"));
        data.add(new Banner("http://nick.mtvnimages.com/nick/promos-thumbs/videos/spongebob-squarepants/rainbow-meme-video/spongebob-rainbow-meme-video-16x9.jpg?quality=0.60"));
        data.add(new Banner("http://nick.mtvnimages.com/nick/video/images/nick/sb-053-16x9.jpg?maxdimension=&quality=0.60"));
        data.add(new Banner("https://www.gannett-cdn.com/-mm-/60f7e37cc9fdd931c890c156949aafce3b65fd8c/c=243-0-1437-898&r=x408&c=540x405/local/-/media/2017/03/14/USATODAY/USATODAY/636250854246773757-XXX-IMG-WTW-SPONGEBOB01-0105-1-1-NC9J38E8.JPG"));
        return data;
    }

    public RecyclerView getRecyclerView(){
        return mHomeBinding.recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
