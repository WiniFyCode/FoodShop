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
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productsData.get(position);

        holder.tvNameFruit.setText(product.tenfood);
        holder.tvQuantity.setText(String.valueOf(product.soluongban)); // Chuyển đổi soluongban thành chuỗi
        holder.tvPrice.setText(String.valueOf(product.dongia)); // Chuyển đổi dongia thành chuỗi
        // load anh server bang picasso
        Picasso.get().load(SERVER.food_url + product.hinhminhhoa).into(holder.imgProduct);

        // Chi tiết sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // khởi tạo intent để gọi màn ProductDetailActivity
                Intent intent = new Intent(context, ProductDetailActivity.class);
                // truyền dữ liệu qua
                intent.putExtra("tenfood", product.tenfood);
                intent.putExtra("soluongban", product.soluongban);
                intent.putExtra("dongia", product.dongia);
                intent.putExtra("mota", product.mota);
                intent.putExtra("hinhminhhoa", product.hinhminhhoa);
                intent.putExtra("ngaythemvao", product.ngaythemvao);
                intent.putExtra("ngayhethan", product.ngayhethan);
                intent.putExtra("idfood", product.idfood);
                //TODO: Mai mốt làm thêm description, reviews, nutritions

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