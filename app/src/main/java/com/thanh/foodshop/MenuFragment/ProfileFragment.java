package com.thanh.foodshop.MenuFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.thanh.foodshop.Adapter.ViewPager2Adapter;
import com.thanh.foodshop.MenuProfile.AccountFragment;
import com.thanh.foodshop.MenuProfile.HistoryFragment;
import com.thanh.foodshop.MenuProfile.PaymentMethodFragment;
import com.thanh.foodshop.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    ArrayList<Fragment> fragmentlist = new ArrayList<>();
    FragmentManager fragmentManager;
    ViewPager2Adapter viewPager2Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // anh xa
        tabLayout = view.findViewById(R.id.proTabLayout);
        viewPager2 = view.findViewById(R.id.proViewPager2);

        fragmentlist.add(new AccountFragment());
        fragmentlist.add(new PaymentMethodFragment());
        fragmentlist.add(new HistoryFragment());

        fragmentManager = getChildFragmentManager();
        viewPager2Adapter = new ViewPager2Adapter(getChildFragmentManager(), getLifecycle(), fragmentlist);
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.setSaveEnabled(false);

        tabLayout.addTab(tabLayout.newTab().setText("ACCOUNT"));
        tabLayout.addTab(tabLayout.newTab().setText("PAYMENT METHOD"));
        tabLayout.addTab(tabLayout.newTab().setText("HISTORY"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return view;
    }
}