package com.thanh.foodshop.Activity;

import android.content.Intent;
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
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.Model.User;
import com.thanh.foodshop.R;

import java.util.ArrayList;

public class BottomNavigationActivity extends AppCompatActivity {

    public static User USER;
    public static ArrayList<Cart> CART = new ArrayList<>();

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
                        LoadFragment(shopFragment);
                        break;

                    case R.id.explore:
                        LoadFragment(exploreFragment);
                        break;

                    case R.id.cart:
                        LoadFragment(cartFragment);
                        break;

                    case R.id.favorite:
                        LoadFragment(favoriteFragment);
                        break;

                    case R.id.profile:
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