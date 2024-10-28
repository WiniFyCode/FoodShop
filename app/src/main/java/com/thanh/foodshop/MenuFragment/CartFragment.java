package com.thanh.foodshop.MenuFragment;

import static com.thanh.foodshop.Authentication.LoginFragment.getUserId;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.CartAdapter;
import com.thanh.foodshop.DatabaseHelper;
import com.thanh.foodshop.Model.Cart;
import com.thanh.foodshop.Model.Order;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView rcvCart;
    private TextView tvSubtotal, tvDelivery, tvTotalTax, tvTotal;
    private AppCompatButton btnGotoCheckout, btnBack;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems;
    private DatabaseHelper dbHelper;
    private double subtotal = 0.0;
    private final double DELIVERY_FEE = 5.0;
    private final double TAX_RATE = 0.1; // 10% tax

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        initializeViews(view);
        setupRecyclerView();
        loadCartItems();
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        rcvCart = view.findViewById(R.id.rcvCart);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDelivery = view.findViewById(R.id.tvDelivery);
        tvTotalTax = view.findViewById(R.id.tvTotalTax);
        tvTotal = view.findViewById(R.id.textView5);
        btnGotoCheckout = view.findViewById(R.id.btnGotoCheckout);
        btnBack = view.findViewById(R.id.btnBack);
        dbHelper = new DatabaseHelper(getContext());
        cartItems = new ArrayList<>();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter( cartItems, new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int quantity) {
                updateCartItemQuantity(position, quantity);
            }

            @Override
            public void onItemRemoved(int position) {
                removeCartItem(position);
            }
        });
        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvCart.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        int userId = getUserId();
        cartItems.clear();

        String query = "SELECT c.id AS cart_id, c.quantity, p.id AS product_id, p.name, p.price, p.image_url " +
                "FROM cart c " +
                "JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int cartId = cursor.getInt(cursor.getColumnIndexOrThrow("cart_id"));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

            Cart item = new Cart(
                    cartId,
                    productId,
                    userId,
                    name,
                    price,
                    quantity,
                    imageUrl,
                    price * quantity
            );
            cartItems.add(item);
        }
        cursor.close();

        cartAdapter.notifyDataSetChanged();
        calculateTotals();
    }

    private void updateCartItemQuantity(int position, int quantity) {
        Cart item = cartItems.get(position);
        if (quantity <= 0) {
            removeCartItem(position);
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        db.update("cart", values, "id = ?",
                new String[]{String.valueOf(item.getId())});

        item.setQuantity(quantity);
        cartAdapter.notifyItemChanged(position);
        calculateTotals();
    }

    private void removeCartItem(int position) {
        Cart item = cartItems.get(position);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("cart", "id = ?",
                new String[]{String.valueOf(item.getId())});

        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);
        calculateTotals();
    }

    private void calculateTotals() {
        subtotal = 0.0;
        for (Cart item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double tax = subtotal * TAX_RATE;
        double total = subtotal + DELIVERY_FEE + tax;

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvDelivery.setText(String.format("$%.2f", DELIVERY_FEE));
        tvTotalTax.setText(String.format("$%.2f", tax));
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnGotoCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            proceedToCheckout();
        });
    }

    private void proceedToCheckout() {
        long orderId = createOrder();
        if (orderId != -1) {
            clearCart();
            navigateToOrderConfirmation(orderId);
        }
    }

    private long createOrder() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put("user_id", getUserId());
            orderValues.put("total_amount", subtotal + DELIVERY_FEE + (subtotal * TAX_RATE));
            orderValues.put("order_status", "processing");

            long orderId = db.insert("orders", null, orderValues);

            for (Cart item : cartItems) {
                ContentValues detailValues = new ContentValues();
                detailValues.put("order_id", orderId);
                detailValues.put("product_id", item.getProductId());
                detailValues.put("quantity", item.getQuantity());
                detailValues.put("price", item.getPrice());

                db.insert("orderdetails", null, detailValues);
            }

            db.setTransactionSuccessful();
            return orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    private void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(getUserId())});
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        calculateTotals();
    }

    private int getUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void navigateToOrderConfirmation(long orderId) {
        Bundle args = new Bundle();
        args.putLong("order_id", orderId);
        Navigation.findNavController(requireView())
                .navigate(R.id.cartFragment, args);
    }
}
