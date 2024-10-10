package com.thanh.foodshop.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPager2Adapter extends FragmentStateAdapter {

    ArrayList<Fragment> mangfragments;

    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Fragment> mangfragments) {

        super(fragmentManager, lifecycle);
        this.mangfragments = mangfragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mangfragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mangfragments.size();
    }

    public Fragment getFragment(int position) {
        return mangfragments.get(position);
    }
}