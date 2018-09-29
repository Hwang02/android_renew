package com.hotelnow.fragment.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.adapter.HomeAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.fragment.model.StayHotDealItem;
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
    public static ArrayList<StayHotDealItem> mStayItem = new ArrayList<>();
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
//        String url = CONFIG.mainHome;
        String url = CONFIG.mainListUrl_v2+"/1/20";
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
//                        hotel_id, name, category, landscape, street1, latitude, longuitude, privateDealYN, sale_price, sale_rate, items_quantity, special_msg, grade_score
                        for(int i = 0; i < mStay.length(); i++){
                            mStayItem.add(new StayHotDealItem(
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
                        objects.add(mStayItem);
                    }
                    if(obj.has("data")){
                        JSONArray mStay = new JSONArray(obj.getJSONArray("data").toString());
//                        hotel_id, name, category, landscape, street1, latitude, longuitude, privateDealYN, sale_price, sale_rate, items_quantity, special_msg, grade_score
                        for(int i = 0; i < mStay.length(); i++){
                            mStayItem.add(new StayHotDealItem(
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
                        objects.add(mStayItem.get(0));
//                        objects.add(getVerticalData());
                    }
                    if(obj.has("activity_hot_deals")){
//                        objects.add(getVerticalData());
                    }
                    if(obj.has("theme_show")){
//                        objects.add(getVerticalData());
                    }
                    if(obj.has("theme_lists")){
//                        objects.add(getVerticalData());
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //test용 데이터
    public static ArrayList<SingleVertical> getVerticalData() {
        ArrayList<SingleVertical> singleVerticals = new ArrayList<>();
        singleVerticals.add(new SingleVertical("Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", R.drawable.charlie));
        singleVerticals.add(new SingleVertical("Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", R.drawable.mrbean));
        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.jim));
        return singleVerticals;
    }

    public static ArrayList<StayHotDealItem> getStayHotelData() {
//        ArrayList<SingleHorizontal> singleHorizontals = new ArrayList<>();
//        singleHorizontals.add(new SingleHorizontal(R.drawable.charlie, "Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", "2010/2/1"));
//        singleHorizontals.add(new SingleHorizontal(R.drawable.mrbean, "Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", "2010/2/1"));
//        singleHorizontals.add(new SingleHorizontal(R.drawable.jim, "Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", "2010/2/1"));
//        singleHorizontals.add(new SingleHorizontal(R.drawable.charlie, "Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", "2010/2/1"));
//        singleHorizontals.add(new SingleHorizontal(R.drawable.mrbean, "Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", "2010/2/1"));
//        singleHorizontals.add(new SingleHorizontal(R.drawable.jim, "Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", "2010/2/1"));

        return mStayItem;
    }

    public static ArrayList<Banner> getBannerData() {
        ArrayList<Banner> data = new ArrayList<>(); //이미지 url를 저장하는 arraylist
        data.add(new Banner("https://upload.wikimedia.org/wikipedia/en/thumb/2/24/SpongeBob_SquarePants_logo.svg/1200px-SpongeBob_SquarePants_logo.svg.png"));
        data.add(new Banner("http://nick.mtvnimages.com/nick/promos-thumbs/videos/spongebob-squarepants/rainbow-meme-video/spongebob-rainbow-meme-video-16x9.jpg?quality=0.60"));
        data.add(new Banner("http://nick.mtvnimages.com/nick/video/images/nick/sb-053-16x9.jpg?maxdimension=&quality=0.60"));
        data.add(new Banner("https://www.gannett-cdn.com/-mm-/60f7e37cc9fdd931c890c156949aafce3b65fd8c/c=243-0-1437-898&r=x408&c=540x405/local/-/media/2017/03/14/USATODAY/USATODAY/636250854246773757-XXX-IMG-WTW-SPONGEBOB01-0105-1-1-NC9J38E8.JPG"));
        return data;
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
