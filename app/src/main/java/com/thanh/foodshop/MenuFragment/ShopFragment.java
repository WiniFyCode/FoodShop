package com.thanh.foodshop.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thanh.foodshop.Activity.ProductDetailActivity;
import com.thanh.foodshop.Activity.SearchActivity;
import com.thanh.foodshop.Adapter.ProductAdapter;
import com.thanh.foodshop.Authentication.LoginFragment;
import com.thanh.foodshop.Class.SeeAll;

import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.Model.User;
import com.thanh.foodshop.R;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ShopFragment extends Fragment {

    public static User users;

    ViewFlipper viewFlipper;
    RecyclerView rcvExclusiveOffer, rcvBestSelling;

    // Product exclusive
    ProductAdapter exclusiveAdapter;
    ArrayList<Product> exclusiveData;
    // Product best selling
    ProductAdapter bestSellingAdapter;
    ArrayList<Product> bestSellingData;

    // Ten user
    TextView tvNameUser;
    TextView tvAddress, tvSeeAllExclusive, tvSeeAllBestSelling;

    // search view
    ImageView searchView;

    // Notifications
    AppCompatButton btnNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // khởi tạo List product rỗng
        exclusiveData = new ArrayList<>();
        bestSellingData = new ArrayList<>();

        // Khởi tạo productsData ở đây
        exclusiveAdapter = new ProductAdapter(exclusiveData, getContext());
        bestSellingAdapter = new ProductAdapter(bestSellingData, getContext());

        // Khởi tạo các view
        viewFlipper = view.findViewById(R.id.viewFlipper);

        // Exclusive Offer
        rcvExclusiveOffer = view.findViewById(R.id.rcvExclusiveOffer);

        // Best Selling
        rcvBestSelling = view.findViewById(R.id.rcvBestSelling);

        // Thiết lập RecyclerView
        rcvExclusiveOffer.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        rcvExclusiveOffer.setAdapter(exclusiveAdapter);

        rcvBestSelling.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcvBestSelling.setAdapter(bestSellingAdapter);

        // Notifications
        btnNotifications = view.findViewById(R.id.btnNotifications);

        // Lay ten user
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String address = sharedPreferences.getString("address", "");
        tvNameUser = view.findViewById(R.id.tvUsername);
        tvNameUser.setText(getResources().getString(R.string.hello) + ", " + username + " !");
        tvAddress = view.findViewById(R.id.tvAddress);
        if (address.length() > 20) {
            tvAddress.setText(address.substring(0, 20) + "...");
        } else {
            tvAddress.setText(address);
        }


        // Click xem thêm
        tvSeeAllExclusive = view.findViewById(R.id.tvSellAllExclusive);
        tvSeeAllBestSelling = view.findViewById(R.id.tvSellAllBestSelling);

        tvSeeAllExclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SeeAll.class);
                intent.putExtra("category", "Exclusive Offer");
                startActivity(intent);
            }
        });

        tvSeeAllBestSelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SeeAll.class);
                intent.putExtra("category", "Best Selling");
                startActivity(intent);
            }
        });

        // search view
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        
        // Gọi loadData() ở đây
        loadSilder();

        // nếu sài shimmer thì comment chỗ này
        if (exclusiveData.isEmpty()) {
            loadExclusive();
        }
        if (bestSellingData.isEmpty()) {
            loadBestSelling();
        }
    }


    private void loadSilder() {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] mangFile = response.split("-"); // Phân tách các file từ chuỗi response

                for (String filename : mangFile) {
                    ImageView imageView = new ImageView(getContext());
                    Picasso.get().load(SERVER.anhslide_url + filename).into(imageView);
                    // Căn chỉnh chiều rộng và chiều cao của slide
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewFlipper.addView(imageView);
                }
            }
        };
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "thất bại" + error, Toast.LENGTH_SHORT).show();
            }
        };


        // Tạo request
        StringRequest stringRequest = new StringRequest(SERVER.layanhslide_php, thanhcong, thatbai);

        // Tạo requestQueue và thêm request vào hàng đợi
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

        viewFlipper.setFlipInterval(2000);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bounce));
        viewFlipper.setAutoStart(true);
    }

    private void loadExclusive() {

        // B3:
        exclusiveData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject food = jsonArray.getJSONObject(i);

                        String name = new String(food.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String description = new String(food.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String price = new String(food.getString("price").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                        exclusiveData.add(new Product(
                                food.getInt("id"),
                                name,
                                description,
                                price,
                                food.getString("weight"),
                                food.getString("image_url"),
                                food.getInt("stock_quantity"),
                                food.getString("last_updated"),
                                food.getString("expiry_date"),
                                food.getInt("category_id")
                        ));
                    }
                    exclusiveAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "LOI" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "thất bại" + error, Toast.LENGTH_SHORT).show();
            }
        };

        // B1: Tạo request trong Volley
        StringRequest stringRequest = new StringRequest(SERVER.exclusive_offer_php, thanhcong, thatbai);
        // B2: Dùng request với Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadBestSelling() {
        // B3:
        bestSellingData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject food = jsonArray.getJSONObject(i);
                        String name = new String(food.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String description = new String(food.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String price = new String(food.getString("price").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);


                        bestSellingData.add(new Product(
                                food.getInt("id"),
                                name,
                                description,
                                price,
                                food.getString("weight"),
                                food.getString("image_url"),
                                food.getInt("stock_quantity"),
                                food.getString("last_updated"),
                                food.getString("expiry_date"),
                                food.getInt("category_id")
                        ));
                    }
                    bestSellingAdapter.notifyDataSetChanged(); // dat o day de dung
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "LOI" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "thất bại" + error, Toast.LENGTH_SHORT).show();
            }
        };

        // B1: Tạo request trong Volley
        StringRequest stringRequest = new StringRequest(SERVER.bestfood_php, thanhcong, thatbai);
        // B2: Dung request với Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}