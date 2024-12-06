package com.thanh.foodshop.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText ipedtNewPassword, ipedtPasswordConfirm, ipedtEnPhoneNumber;
    AppCompatButton btnResetPassword, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ipedtEnPhoneNumber = findViewById(R.id.ipedtEnPhoneNumber);
        ipedtNewPassword = findViewById(R.id.ipedtNewPassword);
        ipedtPasswordConfirm = findViewById(R.id.ipedtPasswordConfirm);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPass();
            }
        });
    }

    private void resetPass() {
        String phoneNumber = ipedtEnPhoneNumber.getText().toString().trim();
        String newPassword = ipedtNewPassword.getText().toString().trim();
        String passwordConfirm = ipedtPasswordConfirm.getText().toString().trim();

        if (newPassword.equals("") || passwordConfirm.equals("")) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(passwordConfirm)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("success")) {
                        Toast.makeText(ForgotPasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                        // Lưu thông tin đăng nhập mới vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", phoneNumber);
                        editor.putString("password", newPassword);
                        editor.putBoolean("remember_me", true);
                        editor.apply();

                        // Chuyển sang giao diện đăng nhập
                        Intent intent = new Intent(ForgotPasswordActivity.this, Authentication_Container.class);
                        intent.putExtra("username", phoneNumber);
                        intent.putExtra("password", newPassword);
                        intent.putExtra("remember_me", true);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("SDT khong ton tai")) {
                        Toast.makeText(ForgotPasswordActivity.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lỗi kết nối
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi khi thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.change_password_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("phone_number", phoneNumber);
                params.put("password", newPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}