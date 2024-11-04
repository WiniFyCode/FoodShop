package com.thanh.foodshop.MenuFragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
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
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.Activity.ProductDetailActivity;
import com.thanh.foodshop.Adapter.FavoriteAdapter;
import com.thanh.foodshop.Class.FavoriteManager;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteFragment extends Fragment {
    RecyclerView rcvFavorite;
    FavoriteAdapter favoriteAdapter;
    ArrayList<Product> favoriteProducts;

    AppCompatButton btnDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvFavorite = view.findViewById(R.id.rcvFavorite);
        favoriteProducts = new ArrayList<>(FavoriteManager.getFavorites());
        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteProducts);
        rcvFavorite.setAdapter(favoriteAdapter);
        rcvFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvFavorite.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        loadFavoriteProducts();

        btnDetail = view.findViewById(R.id.btnDetail);
        if (btnDetail != null) {
            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = rcvFavorite.getChildLayoutPosition(v);
                    Product product = favoriteProducts.get(position);
                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getId());
                    startActivity(intent);
                }
            });
        }


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.delete);
                ColorDrawable background = new ColorDrawable(Color.rgb(238,5,51));

                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());

                background.draw(c);
                icon.draw(c);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product product = favoriteProducts.get(position);
                favoriteProducts.remove(position);
                favoriteAdapter.notifyItemRemoved(position);
                FavoriteManager.removeFavorite(product);
                Toast.makeText(getContext(), "Da xoa khoi danh sach yeu thich", Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcvFavorite);
    }

    private void loadFavoriteProducts() {

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product favoriteProduct = new Product(
                                jsonObject.getInt("id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("description"),
                                jsonObject.getString("price"),
                                jsonObject.getString("weight"),
                                jsonObject.getString("image")
                        );

                        favoriteProducts.add(favoriteProduct);
                    }
                } catch (Exception e) {
                    Log.d("FavoriteFragment", "Load favorite products: " + e.getMessage());
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FavoriteFragment", "Load favorite products: " + error.getMessage());
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.get_favorite_php, thanhcong, thatbai ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(BottomNavigationActivity.USER.id));
                Log.d("FavoriteFragment", "user_id: " + BottomNavigationActivity.USER.id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}