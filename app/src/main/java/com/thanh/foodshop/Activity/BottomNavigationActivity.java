package com.thanh.foodshop.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.thanh.foodshop.MenuFragment.CartFragment;
import com.thanh.foodshop.MenuFragment.ExploreFragment;
import com.thanh.foodshop.MenuFragment.FavoriteFragment;
import com.thanh.foodshop.MenuFragment.ProfileFragment;
import com.thanh.foodshop.MenuFragment.ShopFragment;
import com.thanh.foodshop.R;

public class BottomNavigationActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ShopFragment shopFragment;
    CartFragment cartFragment;
    ExploreFragment exploreFragment;
    FavoriteFragment favoriteFragment;
    ProfileFragment profileFragment;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setSelectedItemId(R.id.shop);
        shopFragment = new ShopFragment();
        cartFragment = new CartFragment();
        exploreFragment = new ExploreFragment();
        favoriteFragment = new FavoriteFragment();
        profileFragment = new ProfileFragment();

        LoadFragment(shopFragment);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.shop:
                        Toast.makeText(BottomNavigationActivity.this, "Shop", Toast.LENGTH_SHORT).show();
                        LoadFragment(shopFragment);
                        break;

                    case R.id.explore:
                        Toast.makeText(BottomNavigationActivity.this, "Explore", Toast.LENGTH_SHORT).show();
                        LoadFragment(exploreFragment);
                        break;

                    case R.id.cart:
                        Toast.makeText(BottomNavigationActivity.this, "Cart", Toast.LENGTH_SHORT).show();
                        LoadFragment(cartFragment);
                        break;

                    case R.id.favorite:
                        Toast.makeText(BottomNavigationActivity.this, "Favorite", Toast.LENGTH_SHORT).show();
                        LoadFragment(favoriteFragment);
                        break;

                    case R.id.profile:
                        Toast.makeText(BottomNavigationActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        LoadFragment(profileFragment);
                        break;
                }

                return true;
            }
        });
    }

    public void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }
}