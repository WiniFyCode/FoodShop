package com.thanh.foodshop.MenuFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.ProductAdapter;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopFragment extends Fragment {

    ViewFlipper viewFlipper;
    RecyclerView rcvExclusiveOffer, rcvBestSelling;

    // Product
    ProductAdapter productsAdapter;
    ArrayList<Product> productsData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // khoi tao view va recyclerView
        // banner
        viewFlipper = view.findViewById(R.id.viewFlipper);

        // Exclusive Offer
        rcvExclusiveOffer = view.findViewById(R.id.rcvExclusiveOffer);

        // Best Selling
        rcvBestSelling = view.findViewById(R.id.rcvBestSelling);

        // khoi tao adapter va layout cho recyclerView
        productsData = new ArrayList<>();
        productsAdapter = new ProductAdapter(productsData, getContext());

        // set adapter cho recyclerView
        rcvExclusiveOffer.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcvExclusiveOffer.setAdapter(productsAdapter);
        rcvBestSelling.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcvBestSelling.setAdapter(productsAdapter);

        // goi ham load du lieu
        loadSilder();
        if (productsData.size() == 0) {
            loadExclusive();
            loadBestSelling();
        }
    }

    private void loadSilder() {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Kiểm tra xem response có phải là chuỗi rỗng hay không
                if (response != null && !response.isEmpty()) {
                    // Tách các tên file ảnh từ String response
                    String[] imageNames = response.split("-");

                    // Xử lý từng tên file ảnh
                    for (String imageName : imageNames) {
                        // Kiểm tra xem tên file ảnh có phải là chuỗi rỗng hay không
                        if (imageName != null && !imageName.isEmpty()) {
                            // Tạo ImageView và load ảnh bằng Picasso
                            ImageView imageView = new ImageView(getContext());
                            Picasso.get().load(SERVER.anhslide_url + imageName).into(imageView);

                            // Thêm ImageView vào ViewFlipper
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            viewFlipper.addView(imageView);
                        }
                    }

                    // Thiết lập ViewFlipper
                    viewFlipper.setFlipInterval(3000);
                    viewFlipper.setAutoStart(true);
                } else {
                    // Xử lý trường hợp response rỗng
                    Log.e("View Flipper - S", "Server response is empty");
                    Log.e("View Flipper - E", "Error: " + response);
                    Toast.makeText(getContext(), "Không thể tải slide", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Response.ErrorListener thatbai= new Response.ErrorListener() {
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
    }

    private void loadExclusive() {
        // B3:
        productsData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject food = jsonArray.getJSONObject(i);
                        productsData.add(new Product(
                                food.getInt("soluongban"),
                                food.getString("ngayhethan"),
                                food.getString("ngaythemvao"),
                                food.getString("hinhminhhoa"),
                                food.getString("mota"),
                                food.getInt("dongia"),
                                food.getString("tenfood"),
                                food.getInt("idfood")
                        ));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "LOI"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }

            }};
        Response.ErrorListener thatbai= new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "thất bại" + error, Toast.LENGTH_SHORT).show();
            }
        };

        // B1: Tạo request trong Volley
        //kiêểu Json mảng array dùng nếu dùng để lấy nhiều đối tượng
        StringRequest stringRequest = new StringRequest(SERVER.laytenfood_php, thanhcong, thatbai);
        // B2: Dùng request với Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadBestSelling(){
        // B3:
        productsData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject food = jsonArray.getJSONObject(i);
                        productsData.add(new Product(
                                food.getInt("soluongban"),
                                food.getString("ngayhethan"),
                                food.getString("ngaythemvao"),
                                food.getString("hinhminhhoa"),
                                food.getString("mota"),
                                food.getInt("dongia"),
                                food.getString("tenfood"),
                                food.getInt("idfood")
                        ));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "LOI"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }

            }};
        Response.ErrorListener thatbai= new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "thất bị" + error, Toast.LENGTH_SHORT).show();
            }
        };

        // B1: Tạo request trong Volley
        //kiểu Json mảng array dùng nhiều đối tượng
        StringRequest stringRequest = new StringRequest(SERVER.bestfood_php, thanhcong, thatbai);
        // B2: Dung request với Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}