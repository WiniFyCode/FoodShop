package com.thanh.foodshop.MenuProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thanh.foodshop.Authentication.Authentication_Container;
import com.thanh.foodshop.R;

public class AccountFragment extends Fragment {
    ConstraintLayout layoutLogout;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        layoutLogout = view.findViewById(R.id.layoutLogout);
        sharedPreferences = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLogout.setBackgroundResource(R.drawable.selected_item_bg);
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();

                                // Sau do, finish activity hien tai
                                Intent intent = new Intent(requireActivity(), Authentication_Container.class);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                layoutLogout.setBackgroundResource(R.drawable.profile_bg);
                            }
                        })
                        .show();
            }
        });

        return view;
    }
}