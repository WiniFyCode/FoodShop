    package com.thanh.foodshop.Authentication;

    import android.os.Bundle;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.viewpager2.widget.ViewPager2;

    import com.google.android.material.tabs.TabLayout;
    import com.thanh.foodshop.Adapter.ViewPager2Adapter;
    import com.thanh.foodshop.R;

    import java.util.ArrayList;

public class Authentication_Container extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    ArrayList<Fragment> mangfragments = new ArrayList<>();
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.authentication_container);

        // anh xa
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        mangfragments.add(new LoginFragment());
        mangfragments.add(new SignupFragment());

        fragmentManager = getSupportFragmentManager();
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(fragmentManager, getLifecycle(), mangfragments);
        viewPager2.setAdapter(viewPager2Adapter);

        tabLayout.addTab(tabLayout.newTab().setText("LOGIN"));
        tabLayout.addTab(tabLayout.newTab().setText("REGISTER"));

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
    }
}