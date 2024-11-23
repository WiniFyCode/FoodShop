package com.thanh.foodshop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    AppCompatButton btnConfirm, btnBack, btnDeliveryAddress;
    TextView tvTotalPrice;
    TextInputEditText tvDeliveryMethod, tvDeliveryAddress;
    double totalPrice;
    int user_id;

    ArrayList<Integer> selectedCartIds;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnDeliveryAddress = findViewById(R.id.btnDeliveryAddress);
        tvDeliveryMethod = findViewById(R.id.tvDeliveryMethod);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);

        // Nhận tổng tiền từ Intents
        totalPrice = getIntent().getDoubleExtra("total_price", 0);
        user_id = getIntent().getIntExtra("user_id", -1);
        selectedCartIds = getIntent().getIntegerArrayListExtra("selected_cart_ids");

        // Lấy địa chỉ giao hàng trong SharedPreferences
        sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
        String address = sharedPreferences.getString("address", getResources().getString(R.string.enter_your_address));
        tvDeliveryAddress.setText(address);

        // Format và hiển thị tổng tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(formatter.format(totalPrice));

        btnDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAddress();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCart();
            }
        });

        // Thêm xử lý nút back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void clearCart() {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("success")) {
                        // Xóa giỏ hàng thành công
                        Intent intent = new Intent(CheckOutActivity.this, OrderSuccessActivity.class);
                        startActivity(intent);
                        finish(); // Đóng màn hình checkout
                    } else {
                        Toast.makeText(CheckOutActivity.this, "Lỗi khi xử lý đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(CheckOutActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckOutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.clear_cart_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("id", new JSONArray(selectedCartIds).toString()); // Chuyển đổi selectedCartIds thành chuỗi JSON moi
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Toast.makeText(this, "Đơn hàng đã được đặt thành công", Toast.LENGTH_SHORT).show();
    }

    private void updateAddress() {
        String address = tvDeliveryAddress.getText().toString().trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.apply();

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(CheckOutActivity.this, "Cập nhật địa chỉ giao hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CheckOutActivity.this, "Lỗi khi cập nhật địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckOutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.update_user_info_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(user_id));
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
