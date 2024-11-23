package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Activity.ProductDetailActivity;
import com.thanh.foodshop.Authentication.LoginFragment;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
    Context context;
    private List<Product> favoriteProducts;

    public FavoriteAdapter(Context context, List<Product> favoriteProducts) {
        this.context = context;
        this.favoriteProducts = favoriteProducts;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        Product product = favoriteProducts.get(position);

        Picasso.get().load(SERVER.food_url + product.image_url).into(holder.imageView);

        holder.tvWeight.setText(product.weight);
        holder.tvFoodName.setText(product.name);
        holder.tvPrice.setText(String.valueOf(product.price));

        holder.btnDetail.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("name", product.name);
            intent.putExtra("price", product.price);
            intent.putExtra("description", product.description);
            intent.putExtra("image_url", SERVER.food_url + product.image_url);
            intent.putExtra("product_id", product.id);
            intent.putExtra("weight", product.weight);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteProducts.size();
    }

    public void removeItem(int position) {
        Product product = favoriteProducts.get(position);
        deleteFromFavorite(product.id, position);
        favoriteProducts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoriteProducts.size());
    }

    public void deleteFromFavorite(int productId, int position) {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(context, "Xóa khỏi danh sách yêu thích thành công", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, favoriteProducts.size());
                } else if (response.equals("not found")) {
                    Toast.makeText(context, "Sản phẩm không có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại: " + response, Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FavoriteAdapter", "VolleyError: " + error.getMessage());
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.delete_from_favorite_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("FavoriteAdapter", "UserID: " + LoginFragment.getUserId(context) + ", ProductID: " + productId);
                params.put("user_id", String.valueOf(LoginFragment.getUserId(context)));
                params.put("product_id", String.valueOf(productId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

class FavoriteViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView tvWeight, tvFoodName, tvPrice;
    AppCompatButton btnDetail;

    public FavoriteViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        tvWeight = itemView.findViewById(R.id.tvWeight);
        tvFoodName = itemView.findViewById(R.id.tvFoodName);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        btnDetail = itemView.findViewById(R.id.btnDetail);
    }
}