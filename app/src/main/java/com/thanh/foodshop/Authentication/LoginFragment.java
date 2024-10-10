package com.thanh.foodshop.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.R;

public class LoginFragment extends Fragment {

    TextInputEditText ipedtUsername, ipedtPassword;
    AppCompatCheckBox cbRememberMe;
    AppCompatButton btnLogin, btnLoginGoogle, btnLoginFacebook;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // khai bao
        sharedPreferences = requireActivity().getSharedPreferences("FileAuth", Context.MODE_PRIVATE);

        // anh xa
        ipedtUsername = view.findViewById(R.id.ipedtUsername);
        ipedtPassword = view.findViewById(R.id.ipedtPassword);
        cbRememberMe = view.findViewById(R.id.cbRememberMe);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLoginGoogle = view.findViewById(R.id.btnLoginGoogle);
        btnLoginFacebook = view.findViewById(R.id.btnLoginFacebook);

        // Nap thong tin lan dau dang nhap
        if (sharedPreferences.getBoolean("remember", false)) {
            ipedtUsername.setText(sharedPreferences.getString("username", ""));
            ipedtPassword.setText(sharedPreferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }

        // onClick xu li login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        return view;
    }

    // ham xu li login
    void login() {
        String username = ipedtUsername.getText().toString();
        String password = ipedtPassword.getText().toString();

        String usernameSaved = sharedPreferences.getString("username", "");
        String passwordSaved = sharedPreferences.getString("password", "");

        // xu li logic
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else if (!username.equals(usernameSaved)) {
            Toast.makeText(getContext(), "Sai tên đăng nhập", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordSaved)) {
            Toast.makeText(getContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRememberMe.isChecked()) {
                editor.putString("username", ipedtUsername.getText().toString());
                editor.putString("password", ipedtPassword.getText().toString());
                editor.putBoolean("remember", true);
                editor.apply();
            } else {
                editor.clear();
                editor.apply();
            }

            // cho nay chuyen qua man hinh chinh - hien chu co
//            TODO: chuyen qua man hinh chinh
            Intent intent = new Intent(getContext(), BottomNavigationActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Login thành công", Toast.LENGTH_SHORT).show();
        }
    }
}