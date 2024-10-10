package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    Context context;
    ArrayList<Product> productsData;

    public ProductAdapter(ArrayList<Product> productsData, Context context) {
        this.productsData = productsData;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productsData.get(position);
        holder.tvNameFruit.setText(product.tenfood);
        holder.tvQuantity.setText(product.soluongban);
        holder.tvPrice.setText(product.dongia);
        // load anh server bang picasso
        Picasso.get().load(SERVER.food_url + product.hinhminhhoa).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productsData.size();
    }
}

class ProductViewHolder extends RecyclerView.ViewHolder {
    ImageView imgProduct;
    TextView tvNameFruit, tvQuantity, tvPrice;

    public ProductViewHolder(View itemView) {
        super(itemView);
        // anh xa
        imgProduct = itemView.findViewById(R.id.imgProduct);
        tvNameFruit = itemView.findViewById(R.id.tvNameFruit);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvPrice = itemView.findViewById(R.id.tvPrice);
    }
}