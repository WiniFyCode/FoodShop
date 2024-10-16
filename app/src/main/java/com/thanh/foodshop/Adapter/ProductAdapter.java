package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Activity.ProductDetailActivity;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_shop, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productsData.get(position);

        holder.tvNameFruit.setText(product.name);
        holder.tvQuantity.setText(product.weight);
        holder.tvPrice.setText(product.price);

        // load anh server bang picasso
        Picasso.get().load(SERVER.food_url + product.image_url).into(holder.imgProduct);

        // Chi tiết sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // khởi tạo intent để gọi màn ProductDetailActivity
                Intent intent = new Intent(context, ProductDetailActivity.class);
                // truyền dữ liệu qua
                intent.putExtra("name", product.name);
//                intent.putExtra("stock_quantity", product.stock_quantity);
                intent.putExtra("price", product.price);
//                intent.putExtra("category_id", product.category_id);
                intent.putExtra("description", product.description);
                intent.putExtra("image_uri", product.image_url);
//                intent.putExtra("last_updated", product.last_updated);
//                intent.putExtra("expiry_date", product.expiry_date);
                intent.putExtra("id", product.id);
                intent.putExtra("weight", product.weight);

                //TODO: Mai mốt làm thêm reviews, nutritions

                // chuyển trang
                context.startActivity(intent);
            }
        });
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
        tvQuantity = itemView.findViewById(R.id.tvWeight);
        tvPrice = itemView.findViewById(R.id.tvPrice);
    }
}