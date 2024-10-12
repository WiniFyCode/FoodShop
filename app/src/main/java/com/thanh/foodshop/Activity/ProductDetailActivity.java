package com.thanh.foodshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

public class ProductDetailActivity extends AppCompatActivity {

    AppCompatButton btnBack, btnFavorite, btnMinus, btnPlus, btnMoreDes, btnAddToCart, btnMoreNut, btnMoreReview;
    ImageView imgProduct;
    TextView tvNameProduct, tvWeight, tvPrice, tvDescription, tvQuantity, tvNutritions;
    AppCompatRatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();

        // Ánh Xạ các component
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvWeight = findViewById(R.id.tvWeight);
        tvPrice = findViewById(R.id.tvPrice);
        btnMinus = findViewById(R.id.btnMinus);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMoreDes = findViewById(R.id.btnMoreDes);
        tvDescription = findViewById(R.id.tvDescription);
        tvNutritions = findViewById(R.id.tvNutritions);
        btnMoreNut = findViewById(R.id.btnMoreNut);
        ratingBar = findViewById(R.id.ratingBar);
        btnMoreReview = findViewById(R.id.btnMoreReview);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Lấy dữ liệu từ intent
        String name = intent.getStringExtra("tenfood");
        String price = intent.getStringExtra("dongia");
        String image = intent.getStringExtra("hinhminhhoa");
        String description = intent.getStringExtra("mota");

        // Xóa background mặc định
        imgProduct.setBackground(null);

        // Thiết lập dữ liệu
        tvNameProduct.setText(name);
        tvPrice.setText(price);
        tvDescription.setText(description);

        // Sử dụng Picasso để load hình ảnh từ url
        Picasso.get().load(SERVER.food_url + image).into(imgProduct);


    }
}