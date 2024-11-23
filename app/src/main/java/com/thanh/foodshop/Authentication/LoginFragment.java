package com.thanh.foodshop.Authentication;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.MenuFragment.ShopFragment;
import com.thanh.foodshop.Model.User;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    TextInputEditText ipedtUsername, ipedtPassword;
    AppCompatCheckBox cbRememberMe;
    AppCompatButton btnLogin, btnLoginGoogle, btnLoginFacebook;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Khởi tạo SharedPreferences để lưu thông tin đăng nhập
        sharedPreferences = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);

        // Ánh xạ các thành phần giao diện
        ipedtUsername = view.findViewById(R.id.ipedtUsername);
        ipedtPassword = view.findViewById(R.id.ipedtPassword);
        cbRememberMe = view.findViewById(R.id.cbRememberMe);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLoginGoogle = view.findViewById(R.id.btnLoginGoogle);
        btnLoginFacebook = view.findViewById(R.id.btnLoginFacebook);

        // Kiểm tra xem có thông tin đăng nhập đã lưu không để tự động đăng nhập
        if (sharedPreferences.getBoolean("remember_me", false)) {
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");

            if (!username.isEmpty() && !password.isEmpty()) {
                // Đăng nhập tự động
                Intent intent = new Intent(requireActivity(), BottomNavigationActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            }
        }

        // Xử lý khi người dùng nhấn vào nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return view;
    }

    // Hàm xử lý đăng nhập
    void login() {
        String username = ipedtUsername.getText().toString().trim();
        String password = ipedtPassword.getText().toString().trim();

        // Phản hồi khi yêu cầu đăng nhập thành công
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty() && !response.equals("fail")) {
                    try {
                        // Chuyển đổi chuỗi JSON nhận được thành đối tượng người dùng
                        JSONObject user = new JSONObject(response);

                        // Lưu ID, username, password người dùng vào SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("remember_me", cbRememberMe.isChecked());
                        editor.putInt("user_id", user.getInt("id"));
                        editor.putString("username", user.getString("username"));
                        editor.putString("password", user.getString("password"));
                        editor.putString("email", user.getString("email"));
                        editor.putString("address", user.getString("address"));
                        editor.putString("phone_number", user.getString("phone_number"));
                        editor.putString("role", user.getString("role"));
                        editor.apply();

                        ShopFragment.users = new User(
                                user.getInt("id"),
                                user.getString("username"),
                                user.getString("password"),
                                user.getString("email"),
                                user.getString("address"),
                                user.getString("phone_number"),
                                user.getString("role")
                        );

                        // Chuyển sang giao diện chính sau khi đăng nhập thành công
                        Intent intent = new Intent(requireActivity(), BottomNavigationActivity.class);
                        startActivity(intent);
                        requireActivity().finish();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(requireActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Phản hồi khi yêu cầu đăng nhập thất bại
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("*LOGIN*", error.toString());
                Toast.makeText(requireActivity(), "Không thể kết nối với máy chủ", Toast.LENGTH_SHORT).show();
            }
        };

        // Tạo yêu cầu đăng nhập tới server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.login_php, thanhcong, thatbai) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Gửi dữ liệu đăng nhập (username và password) lên server
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

    public static int getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        return preferences.getInt("user_id", -1); // -1 là giá trị mặc định nếu không tìm thấy
    }
}