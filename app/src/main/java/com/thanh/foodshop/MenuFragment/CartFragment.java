package com.thanh.foodshop.MenuFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CartFragment extends Fragment {
    RecyclerView rcvCart;
    CartAdapter cartAdapter;
    List<CartItem> cartItems; // CartItem is a class representing a cart item

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        rcvCart = view.findViewById(R.id.rcvCart);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems); // Assuming you have a CartAdapter
        rcvCart.setAdapter(cartAdapter);
        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchCartItems();
    }

    private void fetchCartItems() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", android.databinding.tool.Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.GET, SERVER.get_cart_php + "?user_id=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            cartItems.clear(); // Clear existing items

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // Create CartItem objects from JSON data and add to cartItems list
                                CartItem cartItem = new CartItem(
                                        jsonObject.getInt("id"),
                                        jsonObject.getInt("product_id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("price"),
                                        jsonObject.getString("image_url"),
                                        jsonObject.getInt("quantity")
                                );
                                cartItems.add(cartItem);
                            }

                            cartAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Lỗi phân tích JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Lỗi mạng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}