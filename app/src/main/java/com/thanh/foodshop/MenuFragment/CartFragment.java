package com.thanh.foodshop.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
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
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.Activity.CheckOutActivity;
import com.thanh.foodshop.Adapter.CartAdapter;
import com.thanh.foodshop.Authentication.LoginFragment;
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    static List<Cart> cartItems;
    public static TextView tvTotal;

    TextView tvSubtotal, tvDeliveryFee, tvTotalTax;
    AppCompatButton btnCheckout;

    public static double totalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.rcvCart);
        tvTotal = view.findViewById(R.id.tvTotal);

        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDeliveryFee = view.findViewById(R.id.tvDelivery);
        tvTotalTax = view.findViewById(R.id.tvTotalTax);

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, getContext());
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalPrice = 0;
        tvTotal.setText(totalPrice + "VND");

        loadCartItems();

        btnCheckout = view.findViewById(R.id.btnGotoCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPrice = 0;
                for (Cart cart : cartItems) {
                    if (cart.quantity > 0) {
                        totalPrice = cart.total_price * cart.quantity;
                        Intent intent = new Intent(getContext(), CheckOutActivity.class);
                        intent.putExtra("totalPrice", totalPrice);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getContext(), "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                }
                CartFragment.tvTotal.setText(totalPrice + "VND");
            }
        });

    }

    private void loadCartItems() {
        cartItems.clear();

        if (BottomNavigationActivity.USER == null) {
            return;
        }

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cart = jsonArray.getJSONObject(i);
                        cartItems.add(new Cart(
                                cart.getInt("id"),
                                cart.getInt("product_id"),
                                cart.getInt("user_id"),
                                new String(cart.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8),
                                cart.getDouble("price"),
                                cart.getInt("quantity"),
                                cart.getString("image_url"),
                                cart.getDouble("total_price")
                        ));
                        cartAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Không có dữ liệu giỏ hàng của mã khach hang trong he thong" + BottomNavigationActivity.USER.id, Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.get_cart_php, thanhcong, thatbai) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(BottomNavigationActivity.USER.id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}