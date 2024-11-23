package com.thanh.foodshop.MenuFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.CartAdapter;
import com.thanh.foodshop.Adapter.FavoriteAdapter;
import com.thanh.foodshop.Authentication.LoginFragment;
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteFragment extends Fragment {
    RecyclerView rcvFavorite;
    FavoriteAdapter favoriteAdapter;
    ArrayList<Product> favoriteProducts;
    AppCompatButton btnAddAllToCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvFavorite = view.findViewById(R.id.rcvFavorite);
        btnAddAllToCart = view.findViewById(R.id.btnAddAllToCart);

        favoriteProducts = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteProducts);
        rcvFavorite.setAdapter(favoriteAdapter);
        rcvFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvFavorite.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        loadFavorites();

        btnAddAllToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: thêm tất cả sản phẩm vào giỏ hàng
            }
        });

        // Tạo ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0 && position < favoriteProducts.size()) {
                    favoriteAdapter.removeItem(position);
                    favoriteAdapter.deleteFromFavorite(favoriteProducts.get(position).id, position);
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(rcvFavorite);
    }

    private void loadFavorites() {
        favoriteProducts.clear();

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("FavoriteFragment", "Server Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product product = new Product(
                                jsonObject.getInt("id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("description"),
                                jsonObject.getString("price"),
                                jsonObject.getString("weight"),
                                jsonObject.getString("image_url"),
                                true
                        );
                        favoriteProducts.add(product);
                    }
                    favoriteAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("FavoriteFragment", "Lỗi: " + e.getMessage());
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FavoriteFragment", "Lỗi: " + error.getMessage());
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.get_favorite_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(LoginFragment.getUserId(getContext())));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}