package com.thanh.foodshop.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Activity.BottomNavigationActivity;
import com.thanh.foodshop.Activity.CheckOutActivity;
import com.thanh.foodshop.Activity.ProductDetailActivity;
import com.thanh.foodshop.Adapter.CartAdapter;
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartFragment extends Fragment {
    RecyclerView rcvCart;
    CartAdapter cartAdapter;
    static List<Cart> cartItems;
    public static TextView tvTotal;

    TextView tvSubtotal, tvDeliveryFee, tvTotalTax;
    AppCompatButton btnCheckout;

    int user_id;

    double totalPrice = 0;
    double subtotal = 0;
    double delivery = 0;
    double tax = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvTotal = view.findViewById(R.id.tvTotal);

        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDeliveryFee = view.findViewById(R.id.tvDelivery);
        tvTotalTax = view.findViewById(R.id.tvTotalTax);

        rcvCart = view.findViewById(R.id.rcvCart);
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, getContext(), this);
        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvCart.setAdapter(cartAdapter);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvCart.addItemDecoration(divider);

        if (BottomNavigationActivity.USER != null) {
            user_id = BottomNavigationActivity.USER.id;
        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
            user_id = sharedPreferences.getInt("user_id", -1); // -1 nếu không tìm thấy user_id
        }
        if (user_id == -1) {
            Toast.makeText(getContext(), "Không tìm thấy user_id. Hãy đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadCartItems(user_id);

        btnCheckout = view.findViewById(R.id.btnGotoCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> selectedCartIds = new ArrayList<>();

                // Kiểm tra các sản phẩm được chọn
                for (Cart cart : cartItems) {
                    if (cart.selected) {
                        selectedCartIds.add(cart.id);
                        Log.d("CartFragment", "Selected Cart ID: " + cart.id + ", Price: " + cart.price);
                    }
                }

                // Kiểm tra nếu có sản phẩm được chọn , nếu có thì chuyển sang CheckOutActivity
                if (!selectedCartIds.isEmpty()) {
                    Intent intent = new Intent(getContext(), CheckOutActivity.class);
                    intent.putIntegerArrayListExtra("selected_cart_ids", new ArrayList<>(selectedCartIds));// lay id san pham duoc chon gui qua CheckOutActivity de xoa san pham trong gio hang
                    intent.putExtra("total_price", totalPrice); // Sử dụng totalPrice đã được tính toán
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.d("CartFragment", "Không có sản phẩm được chọn");
                }
            }
        });
    }

    private void loadCartItems(int userId) {
        cartItems.clear();

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cart = jsonArray.getJSONObject(i);
                        Cart cartItem = new Cart(
                                cart.getInt("id"),
                                cart.getInt("product_id"),
                                cart.getInt("user_id"),
                                cart.getString("name"),
                                cart.getDouble("price"),
                                cart.getInt("quantity"),
                                cart.getString("image_url"),
                                cart.getDouble("total_price")
                        );
                        cartItems.add(cartItem);
                        Log.d("CartFragment", "Added item: " + cartItem.name);
                    }

                    cartAdapter.notifyDataSetChanged();
                    updateTotal();

                    if (cartItems.isEmpty()) {
                        Log.d("CartFragment", "Giỏ hàng trống sau khi tải");
                    } else {
                        Log.d("CartFragment", "Giỏ hàng có " + cartItems.size() + " items");
                    }

                    // cap nhat badge cart ( hien thi so luong san pham co trong gio hang )
                    ((BottomNavigationActivity) getActivity()).updateCartBadge(cartItems.size());

                } catch (Exception e) {
                    Log.e("CartFragment", "Error parsing JSON: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CartFragment", "Volley Error: " + error.toString());
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.get_cart_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(CartFragment.this.user_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void updateTotal() {
        double subtotal = 0;

        // Chỉ tính tổng tiền cho các sản phẩm được chọn
        for (Cart cart : cartItems) {
            if (cart.selected) {
                subtotal += cart.price * cart.quantity;
            }
        }

        double deliveryFee = subtotal * 0.01; // 1% phí giao hàng
        double tax = (subtotal + deliveryFee) * 0.04; // 4% VAT
        double total = subtotal + deliveryFee + tax;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvSubtotal.setText(formatter.format(subtotal));
        tvDeliveryFee.setText(formatter.format(deliveryFee));
        tvTotalTax.setText(formatter.format(tax));
        tvTotal.setText(formatter.format(total));

        this.totalPrice = total;

        // Cập nhật trạng thái nút Checkout
        btnCheckout.setEnabled(total > 0);
    }

    // Thêm phương thức này để CartAdapter có thể gọi
    public void updateTotalFromAdapter() {
        updateTotal();
    }
}