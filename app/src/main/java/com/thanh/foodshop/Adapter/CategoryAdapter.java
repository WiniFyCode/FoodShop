package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Class.CategoryDetail;
import com.thanh.foodshop.Model.Categories;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    Context context;
    ArrayList<Categories> categoriesData;

    public CategoryAdapter(Context context, ArrayList<Categories> categoriesData) {
        this.context = context;
        this.categoriesData = categoriesData;
    }

    // Phương thức để cập nhật dữ liệu cho adapter o EXPLORE FRAGMENT
    public void setCategories(ArrayList<Categories> categories) {
        this.categoriesData = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_explore, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull CategoryViewHolder holder, int position) {

        Categories categories = categoriesData.get(position);
        holder.tvNameProduct.setText(categories.name);
        Picasso.get().load(SERVER.food_url + categories.image)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Bundle để truyền CategoryID và CategoryName sang Fragment chi tiết
                Bundle bundle = new Bundle();
                bundle.putString("CategoryID", String.valueOf(categories.id));  // Truyền CategoryID
                bundle.putString("CategoryName", categories.name);  // Truyền CategoryName

                // Tạo đối tượng Fragment CategoryDetail và thiết lập arguments
                CategoryDetail categoryDetailFragment = new CategoryDetail();
                categoryDetailFragment.setArguments(bundle);

                // Thực hiện chuyển đổi sang Fragment CategoryDetail
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, categoryDetailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesData.size();
    }
}

class CategoryViewHolder extends RecyclerView.ViewHolder {
    ImageView imgProduct;
    TextView tvNameProduct;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        imgProduct = itemView.findViewById(R.id.imgProduct);
        tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
    }

}
