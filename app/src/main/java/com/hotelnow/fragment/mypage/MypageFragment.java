package com.hotelnow.fragment.mypage;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelnow.R;
import com.hotelnow.adapter.MainAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;

import java.util.ArrayList;

public class MypageFragment extends Fragment {

    private FragmentHomeBinding mHomeBinding;
    private ArrayList<Object> objects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View inflate = mHomeBinding.getRoot();

        MainAdapter adapter = new MainAdapter(getActivity(), getObject());
        mHomeBinding.recyclerView.setAdapter(adapter);
        mHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return inflate;
    }

    private ArrayList<Object> getObject() {
        objects.add(getBannerData().get(0));
        objects.add(getHorizontalData().get(0));
        objects.add(getVerticalData().get(0));
        return objects;
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

    public static ArrayList<SingleHorizontal> getHorizontalData() {
        ArrayList<SingleHorizontal> singleHorizontals = new ArrayList<>();
        singleHorizontals.add(new SingleHorizontal(R.drawable.charlie, "Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", "2010/2/1"));
        singleHorizontals.add(new SingleHorizontal(R.drawable.mrbean, "Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", "2010/2/1"));
        singleHorizontals.add(new SingleHorizontal(R.drawable.jim, "Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", "2010/2/1"));
        return singleHorizontals;
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
