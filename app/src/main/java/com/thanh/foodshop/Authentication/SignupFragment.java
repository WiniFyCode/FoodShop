package com.thanh.foodshop.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.thanh.foodshop.Adapter.ViewPager2Adapter;
import com.thanh.foodshop.R;

public class SignupFragment extends Fragment {

    TextInputEditText ipedtCrUsername, ipedtEnEmail, ipedtCrPassword;
    AppCompatButton btnSignup;
    SharedPreferences sharedPreferences;
    ViewPager2 viewPager2;
    ViewPager2Adapter viewPager2Adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, null);

        //khai bao
        sharedPreferences = getContext().getSharedPreferences("FileAuth", Context.MODE_PRIVATE);

        // anh xa
        ipedtCrUsername = view.findViewById(R.id.ipedtCrUsername);
        ipedtEnEmail = view.findViewById(R.id.ipedtEnEmail);
        ipedtCrPassword = view.findViewById(R.id.ipedtCrPassword);
        btnSignup = view.findViewById(R.id.btnSignup);

        // anh xa adapter de lay du lieu chuyen qua fragment login
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

    void signup() {
        String username = ipedtCrUsername.getText().toString();
        String email = ipedtEnEmail.getText().toString();
        String password = ipedtCrPassword.getText().toString();

        // kiem tra thong tin
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lông nhap dủ thong tin", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
            Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();

            // chuyen qua tab login bang ViewPager2
            viewPager2.setCurrentItem(0);

            // lay Login Fragment tu ViewPager2Adapter va cap nhat thong tin tu sign up -> login
            LoginFragment loginFragment = (LoginFragment) viewPager2Adapter.getFragment(0);
            loginFragment.ipedtUsername.setText(username);
            loginFragment.ipedtPassword.setText(password);

            // tranh de nguoi dung quen an vao nut remember me nen de "true" luon=)
            loginFragment.cbRememberMe.setChecked(true);
        }
    }
}