package com.thanh.foodshop.MenuFragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Adapter.ViewPager2Adapter;
import com.thanh.foodshop.MenuProfile.AccountFragment;
import com.thanh.foodshop.MenuProfile.HistoryFragment;
import com.thanh.foodshop.MenuProfile.PaymentMethodFragment;
import com.thanh.foodshop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TextView tvNameUser, tvEmailUser;
    ShapeableImageView imgProfile;

    public static final int PICK_IMAGE_REQUEST = 1;

    ArrayList<Fragment> fragmentlist = new ArrayList<>();
    FragmentManager fragmentManager;
    ViewPager2Adapter viewPager2Adapter;

    String selectedImagePath = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Ánh xạ các thành phần giao diện
        tabLayout = view.findViewById(R.id.proTabLayout);
        viewPager2 = view.findViewById(R.id.proViewPager2);
        tvNameUser = view.findViewById(R.id.tvNameUser);
        tvEmailUser = view.findViewById(R.id.tvEmailUser);
        imgProfile = view.findViewById(R.id.profile_image);

        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String imagePath = sharedPreferences.getString("image_path", "");

        tvNameUser.setText(name);
        tvEmailUser.setText(email);

        // Kiểm tra và tải ảnh từ đường dẫn lưu trong SharedPreferences
        File file = new File(imagePath);
        if (!imagePath.isEmpty() && file.exists()) {
            Picasso.get().load(file).placeholder(R.drawable.profile_icon).error(R.drawable.profile_icon).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.profile_icon);
        }


        // Khi người dùng nhấp vào ảnh hồ sơ
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Thiết lập ViewPager2 và TabLayout
        fragmentlist.add(new AccountFragment());
        fragmentlist.add(new PaymentMethodFragment());
        fragmentlist.add(new HistoryFragment());

        fragmentManager = getChildFragmentManager();
        viewPager2Adapter = new ViewPager2Adapter(getChildFragmentManager(), getLifecycle(), fragmentlist);
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.setSaveEnabled(false);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.ACCOUNT)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.PAYMENT_METHOD)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.HISTORY)));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        tabLayout.setBackgroundResource(R.drawable.profile_top_bg);

        return view;
    }


    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Nếu người dùng chọn ảnh thành công
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Xóa file ảnh cũ
            File oldImageFile = new File(requireContext().getFilesDir(), "profile_image.png");
            if (oldImageFile.exists()) {// neu co file da ton tai
                oldImageFile.delete(); // xoa file do di
            }

            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(data.getData());
                 // Mở luồng ghi và ghi dữ liệu vào file
                 FileOutputStream outputStream = new FileOutputStream(oldImageFile)) {

                // Đọc dữ liệu từ InputStream và ghi vào FileOutputStream
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                outputStream.write(buffer);

                // Lưu đường dẫn của file vào biến
                selectedImagePath = oldImageFile.getAbsolutePath();

                 // Cập nhật đường dẫn vào SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("image_path", selectedImagePath).apply();

                // Cập nhật ảnh trong ImageView
                // Xóa cache trước để đảm bảo load ảnh mới
                Picasso.get().invalidate(oldImageFile);
                // Load ảnh mới và hiển thị trong ImageView
                Picasso.get().load(oldImageFile).placeholder(R.drawable.profile_icon).into(imgProfile);

            } catch (Exception e) {
                Log.e("ProfileFragment", "onActivityResult - Loading image: ", e);
            }
        }
    }
}