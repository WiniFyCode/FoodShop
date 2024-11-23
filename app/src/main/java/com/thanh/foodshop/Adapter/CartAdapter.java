package com.thanh.foodshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.MenuFragment.CartFragment;
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

//    li do khai bao CartFragment o day
// Cho phép Adapter có thể gọi các phương thức của Fragment
// Cập nhật UI trong Fragment khi có thay đổi trong Adapter
// Đồng bộ hóa dữ liệu giữa Adapter và Fragment
    private CartFragment cartFragment;

    private List<Cart> cartData;
    private Context context;

    public CartAdapter(List<Cart> cartData, Context context, CartFragment cartFragment) {
        this.cartData = cartData;
        this.context = context;
        this.cartFragment = cartFragment;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        Cart item = cartData.get(position);

        // load anh
        Picasso.get()
                .load(SERVER.food_url + item.image)
                .resize(110, 100)
                .into(holder.imgProduct);

        //ten san pham
        holder.tvProductName.setText(item.name);
        // so luong
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        // gia
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(item.price));

        // tong tien
        double totalPrice = item.price * item.quantity;
        holder.tvTotalPrice.setText(formatter.format(totalPrice));

        // dat trang thai checkbox ma kho trigger listener
        holder.selectedCart.setOnCheckedChangeListener(null);
        holder.selectedCart.setChecked(item.selected);
        holder.selectedCart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.selected = isChecked;
            // Cập nhật tổng giá trong đoạn giỏ hàng
            cartFragment.updateTotalFromAdapter();
        });

        holder.btnIncrease.setOnClickListener(v -> {
            // Kiểm tra xem số lượng của mặt hàng có nhỏ hơn 99 không để hạn chế số lượng tối đa
            if (item.quantity < 99) {
                int oldQuantity = item.quantity; // Lưu trữ số lượng hiện tại
                // Cập nhật số lượng của mặt hàng cục bộ bằng cách tăng thêm 1
                updateQuantityLocally(item, oldQuantity + 1);
                holder.tvQuantity.setText(String.valueOf(item.quantity));
                holder.tvTotalPrice.setText(formatter.format(item.price * item.quantity));
                // Gửi yêu cầu cập nhật số lượng trên máy chủ
                updateQuantity(item.id, item.user_id, item.product_id, oldQuantity, item.quantity);
                // Cập nhật tổng giá trong đoạn giỏ hàng
                cartFragment.updateTotalFromAdapter();
            }
            else {
                Toast.makeText(context, "Không thể thêm thêm nữa", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            // Giảm số lượng sản phẩm nếu lớn hơn 1
            if (item.quantity > 1) {
                int oldQuantity = item.quantity; // Lưu trữ số lượng cũ
                updateQuantityLocally(item, oldQuantity - 1); // Cập nhật số lượng mới cục bộ
                holder.tvQuantity.setText(String.valueOf(item.quantity)); // Hiển thị số lượng mới
                holder.tvTotalPrice.setText(formatter.format(item.price * item.quantity)); // Hiển thị tổng giá mới
                updateQuantity(item.id, item.user_id, item.product_id, oldQuantity, item.quantity); // Cập nhật số lượng mới lên server
                cartFragment.updateTotalFromAdapter(); // Cập nhật tổng giá trị toàn bộ giỏ hàng
            } else {
                // Hiện dialog xác nhận xóa khi số lượng là 1
                new AlertDialog.Builder(context)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            deleteItemCart(item, position);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmDialog(item, position);
        });

    }

    @Override
    public int getItemCount() {
        return cartData.size();
    }

    public void updateQuantityLocally(Cart item, int newQuantity) {
        item.quantity = newQuantity;
        item.total_price = item.price * newQuantity;
    }

    private void showDeleteConfirmDialog(Cart item, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteItemCart(item, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteItemCart(Cart item, int position) {
        // Chỉ gửi request xóa đến server, KHÔNG xóa khỏi cartData ở đây
        deleteFromCart(item, position);
    }

    private void updateQuantity(int id, int userId, int productId, int quantity, int quantityNew) {
        Response.Listener<String> thanhCong = response -> {
            if (!response.equals("success")) {
                Toast.makeText(context, "Lỗi khi cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener thatBai = error -> Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.update_cart_php, thanhCong, thatBai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("user_id", String.valueOf(userId));
                params.put("product_id", String.valueOf(productId));
                params.put("quantity", String.valueOf(quantity));
                params.put("quantityNew", String.valueOf(quantityNew));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void deleteFromCart(Cart item, int position) {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("success")) {
                        cartData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartData.size());
                        cartFragment.updateTotalFromAdapter();

                        // cap nhat badge
                        ((BottomNavigationActivity) cartFragment.getActivity()).updateCartBadge(cartData.size());
                    } else {
                        Toast.makeText(context, "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
                Toast.makeText(context, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        };
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.delete_from_cart_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(item.id));
                params.put("user_id", String.valueOf(item.user_id));
                params.put("product_id", String.valueOf(item.product_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}


class CartViewHolder extends RecyclerView.ViewHolder {
    ImageView imgProduct;
    TextView tvProductName, tvProductPrice, tvQuantity, tvTotalPrice;
    AppCompatButton btnIncrease, btnDecrease, btnDelete;
    AppCompatCheckBox selectedCart;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        imgProduct = itemView.findViewById(R.id.imgCartProduct);
        tvProductName = itemView.findViewById(R.id.tvNameCart);
        tvProductPrice = itemView.findViewById(R.id.tvPriceCart);
        tvQuantity = itemView.findViewById(R.id.tvCount);
        btnIncrease = itemView.findViewById(R.id.btnIncreaseCart);
        btnDecrease = itemView.findViewById(R.id.btnDecreaseCart);
        btnDelete = itemView.findViewById(R.id.btnRemoveCart);
        selectedCart = itemView.findViewById(R.id.selectedCart);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }
}