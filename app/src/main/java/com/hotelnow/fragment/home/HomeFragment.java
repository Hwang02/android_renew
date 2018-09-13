package com.hotelnow.fragment.home;

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
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

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
        objects.add(getVerticalData().get(0));
        objects.add(getHorizontalData().get(0));
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
