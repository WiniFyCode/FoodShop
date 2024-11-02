package com.thanh.foodshop.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Authentication.LoginFragment;
import com.thanh.foodshop.Class.FavoriteManager;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    AppCompatButton btnBack, btnMinus, btnPlus, btnMoreDes, btnAddToCart, btnMoreNut, btnMoreReview;
    ImageView imgProduct, imgFavorite;
    TextView tvNameProduct, tvWeight, tvPrice, tvDescription, tvNutritions;
    TextInputEditText edtQuantity;
    AppCompatRatingBar ratingBar;

    int quantity = 1; // đặt số lượng mặc định khi người dùng ấn add to cart
    int productId = -1;
    private Product product;

    public boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();

        // Ánh xạ các component
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        imgFavorite = findViewById(R.id.imgFavorite);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvWeight = findViewById(R.id.tvWeight);
        tvPrice = findViewById(R.id.tvPrice);
        btnMinus = findViewById(R.id.btnMinus);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMoreDes = findViewById(R.id.btnMoreDes);
        tvDescription = findViewById(R.id.tvDescription);
        tvNutritions = findViewById(R.id.tvNutritions);
        btnMoreNut = findViewById(R.id.btnMoreNut);
        ratingBar = findViewById(R.id.ratingBar);
        btnMoreReview = findViewById(R.id.btnMoreReview);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Lấy dữ liệu từ intent
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String price = intent.getStringExtra("price");
        String weight = intent.getStringExtra("weight");
        String image = intent.getStringExtra("image_url");
        productId = intent.getIntExtra("product_id", 0);
        Log.e("ProdID", productId + " dhhsdhds");

        // Xóa background mặc định
        imgProduct.setBackground(null);

        // Thiết lập dữ liệu
        tvNameProduct.setText(name);
        setupPrice(price);
        tvDescription.setText(description);
        if (weight == null || weight.equals("null")) {
            tvWeight.setText("...");
        } else {
            tvWeight.setText(weight);
        }
        loadImage(image);

        product = (Product) intent.getSerializableExtra("product");

        // back lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // giảm số lượng
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityStr = edtQuantity.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity > 1) {
                        quantity--;
                        edtQuantity.setText(String.valueOf(quantity));
                    }
                }
            }
        });

        // tăng số lượng
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityStr = edtQuantity.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    quantity = Integer.parseInt(quantityStr);
                    quantity++;
                    edtQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        // Thêm TextWatcher để theo dõi thay đổi trong edtQuantity
        edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        quantity = Integer.parseInt(s.toString());
                        if (quantity < 0) {
                            quantity = 0;
                            edtQuantity.setText("0");
                        }
                    } catch (NumberFormatException e) {
                        // Nếu không thể chuyển đổi, thiết lập quantity về 0
                        quantity = 0;
                        edtQuantity.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(edtQuantity.getText().toString());
                addToCart();
            }
        });

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavorite = !isFavorite;
                updateFavoriteStatus();
                Toast.makeText(ProductDetailActivity.this, isFavorite ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        Response.Listener<String> thanhCong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Thêm thất bại do lỗi: " + response.toString(), Toast.LENGTH_LONG).show();
                }
            }
        };

        Response.ErrorListener thatBai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ProductDetailActivity.this, "Thêm thất bại do kết nối", Toast.LENGTH_LONG).show();
            }
        };

        // tạo đối tượng request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.add_to_cart_php, thanhCong, thatBai) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("ADD_TO_CART", "User ID: " + LoginFragment.getUserId(ProductDetailActivity.this));
                Log.d("ADD_TO_CART", "Product ID: " + productId);
                Log.d("ADD_TO_CART", "Quantity: " + quantity);
                // gửi dữ liệu lên server
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(LoginFragment.getUserId(ProductDetailActivity.this)));
                params.put("product_id", String.valueOf(productId));
                params.put("quantity", String.valueOf(quantity));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setupPrice(String price) {
        // Kiểm tra nếu giá bằng 0
        if (Integer.parseInt(price.replace(",", "")) == 0) {
            tvPrice.setText("Tạm hết hàng");
            tvPrice.setTextColor(getResources().getColor(R.color.Red));
            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // Sử dụng SpannableString để định dạng màu
            String formattedPrice = price + " đ";
            SpannableString spannable = new SpannableString(formattedPrice);

            // Đặt màu cho phần giá (Primary_green)
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Primary_green)), 0, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Đặt màu đen cho ký hiệu "đ"
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Black)), price.length(), formattedPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Thiết lập giá trị cho TextView
            tvPrice.setText(spannable);
        }
    }

    private void loadImage(String image) {
        // Sử dụng Picasso để load hình ảnh từ url
        if (image != null && !image.isEmpty()) {
            Picasso.get().load(image).into(imgProduct);
        } else {
            imgProduct.setImageResource(R.drawable.eye_icon);
        }
    }

    private Product getProductDetails() {
        return product;
    }

    private void addToFavorite() {
        if (BottomNavigationActivity.USER == null) {
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetailActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.add_to_favorite_php, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    FavoriteManager.addFavorite(product);
                    isFavorite = true;
                    updateFavoriteStatus();
                } else {
                    Toast.makeText(ProductDetailActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(BottomNavigationActivity.USER.id));
                params.put("product_id", String.valueOf(product.getId()));

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void removeFromFavorite() {
        if (BottomNavigationActivity.USER == null) {
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetailActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.delete_from_cart_php, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    FavoriteManager.removeFavorite(product);
                    isFavorite = false;
                    updateFavoriteStatus();
                } else {
                    Toast.makeText(ProductDetailActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(BottomNavigationActivity.USER.id));
                params.put("product_id", String.valueOf(product.getId()));

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateFavoriteStatus() {
        if (isFavorite) {
            // Cập nhật trạng thái yêu thích trên giao diện người dùng
            imgFavorite.setImageResource(R.drawable.favorite_red);

            // Cập nhật trạng thái yêu thích trên server
            if (BottomNavigationActivity.USER != null) {
                addToFavorite();
            }
        } else {
            // Cập nhật trạng thái yêu thích trên giao diện người dùng
            imgFavorite.setImageResource(R.drawable.favorite_white);

            // Xóa trạng thái yêu thích trên server
            if (BottomNavigationActivity.USER != null) {
                removeFromFavorite();
            }
        }
    }
}