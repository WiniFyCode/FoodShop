package com.thanh.foodshop.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.thanh.foodshop.Adapter.ViewPager2Adapter;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    TextInputEditText ipedtCrUsername, ipedtEnEmail, ipedtCrPassword;
    AppCompatButton btnSignup;
    SharedPreferences sharedPreferences;
    ViewPager2 viewPager2;
    ViewPager2Adapter viewPager2Adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Khai báo SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("FileAuth", Context.MODE_PRIVATE);

        // Ánh xạ các thành phần giao diện
        ipedtCrUsername = view.findViewById(R.id.ipedtCrUsername);
        ipedtEnEmail = view.findViewById(R.id.ipedtEnEmail);
        ipedtCrPassword = view.findViewById(R.id.ipedtCrPassword);
        btnSignup = view.findViewById(R.id.btnSignup);

        // Ánh xạ ViewPager2 để chuyển qua lại giữa các fragment
        viewPager2 = getActivity().findViewById(R.id.viewPager2);
        viewPager2Adapter = (ViewPager2Adapter) viewPager2.getAdapter();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        return view;
    }

    // Hàm xử lý đăng ký
    void signup() {
        String username = ipedtCrUsername.getText().toString().trim();
        String email = ipedtEnEmail.getText().toString().trim();
        String password = ipedtCrPassword.getText().toString().trim();

        // Kiểm tra thông tin
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {

            Response.Listener<String> thanhcong = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                        // Chuyển qua tab đăng nhập
                        viewPager2.setCurrentItem(0);

                        // Cập nhật thông tin đăng nhập cho LoginFragment
                        LoginFragment loginFragment = (LoginFragment) viewPager2Adapter.getFragment(0);
                        loginFragment.ipedtUsername.setText(username);
                        loginFragment.ipedtPassword.setText(password);
                        loginFragment.cbRememberMe.setChecked(true);

                    } else if (response.equals("exists")) {
                        Toast.makeText(getContext(), "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Response.ErrorListener thatbai = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SignupFragment", "Lỗi kết nối: " + error.getMessage());
                    Toast.makeText(getContext(), "Không thể kết nối với máy chủ", Toast.LENGTH_SHORT).show();
                }
            };


            // Gửi yêu cầu đăng ký lên máy chủ
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.register_php, thanhcong,thatbai)
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Gửi dữ liệu đăng ký (username, email, password)
                    HashMap<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            // Thêm yêu cầu vào hàng đợi
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
    }
}

