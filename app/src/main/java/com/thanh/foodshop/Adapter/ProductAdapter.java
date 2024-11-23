package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Activity.ProductDetailActivity;
import com.thanh.foodshop.MenuFragment.ShopFragment;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    Context context;
    ArrayList<Product> productsData;

    public ProductAdapter(ArrayList<Product> productsData, Context context) {
        this.productsData = productsData;
        this.context = context;
    }

    // Phương thức để cập nhật dữ liệu o SHOP FRAGMENT ( SEARCH )
    public void setData(ArrayList<Product> productsData) {
        this.productsData = productsData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_shop, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productsData.get(position);

        holder.tvNameFruit.setText(product.name);
        holder.tvWeight.setText(product.weight);

        String price = product.price;
        int quantity = product.stock_quantity;

        // Áp dụng animation khi bind item
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade);
        holder.itemView.startAnimation(animation);

        if (price != null && !price.isEmpty()) {
            try {
                if (quantity == 0) {
                    // Nếu quantity bằng 0, hiển thị "Tạm hết hàng"
                    holder.tvPrice.setText("Tạm hết hàng");
                    holder.tvPrice.setTextColor(context.getResources().getColor(R.color.Red));
                    holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // Nếu quantity khác 0, hiển thị giá và thêm ký hiệu "đ" với màu sắc khác nhau
                    String fullPrice = price + " đ";

                    // Sử dụng SpannableString để áp dụng màu cho phần giá và ký hiệu "đ"
                    SpannableString spannablePrice = new SpannableString(fullPrice);

                    // Thiết lập màu xanh lá cho phần giá (Primary_green)
                    spannablePrice.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.Primary_green)),
                            0, price.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Thiết lập màu đen cho ký hiệu "đ"
                    spannablePrice.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)),
                            price.length(), fullPrice.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Set SpannableString vào TextView
                    holder.tvPrice.setText(spannablePrice);
                }

            } catch (NumberFormatException e) {
                // Xử lý ngoại lệ khi parse giá trị không thành công
                holder.tvPrice.setText("Lỗi giá");
                holder.tvPrice.setTextColor(context.getResources().getColor(R.color.Red));
            }
        }

        // Chi tiết sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // khởi tạo intent để gọi màn ProductDetailActivity
                Intent intent = new Intent(context, ProductDetailActivity.class);
                // truyền dữ liệu qua
                intent.putExtra("name", product.name);
                intent.putExtra("price", product.price);
                intent.putExtra("description", product.description);
                intent.putExtra("image_url", SERVER.food_url + product.image_url);
                intent.putExtra("product_id", product.id);
                intent.putExtra("weight", product.weight);
                intent.putExtra("stock_quantity", product.stock_quantity);

                // chuyển trang
                context.startActivity(intent);
            }
        });

        // Load hình ảnh từ server bằng Picasso
        Picasso.get().load(SERVER.food_url + product.image_url).into(holder.imgProduct);
    }


    @Override
    public int getItemCount() {
        return productsData.size();
    }
}


class ProductViewHolder extends RecyclerView.ViewHolder {
    ImageView imgProduct;
    TextView tvNameFruit, tvWeight, tvPrice;

    public ProductViewHolder(View itemView) {
        super(itemView);
        // anh xa
        imgProduct = itemView.findViewById(R.id.imgProduct);
        tvNameFruit = itemView.findViewById(R.id.tvNameFruit);
        tvWeight = itemView.findViewById(R.id.tvWeight);
        tvPrice = itemView.findViewById(R.id.tvPrice);
    }
}