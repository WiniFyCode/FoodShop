package com.thanh.foodshop.Class;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.ProductAdapter;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CategoryDetail extends Fragment {
    RecyclerView rcvDetailCategory;
    String Explore = "";
    ArrayList<Product> productsData = new ArrayList<>();
    ProductAdapter productAdapter;

    TextView tvTitleCategory;
    AppCompatButton btnBack, btnSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_detail, container, false);

        rcvDetailCategory = view.findViewById(R.id.rcvDetailCategory);
        tvTitleCategory = view.findViewById(R.id.tvTitleCategory);

        productAdapter = new ProductAdapter(productsData, getContext());
        rcvDetailCategory.setAdapter(productAdapter);
        rcvDetailCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnSort = view.findViewById(R.id.btnSort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortOptions();
            }
        });

        // Nhận CategoryID và CategoryName từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            Explore = bundle.getString("CategoryID");
            String categoryName = bundle.getString("CategoryName");

            // Gán tên Category vào TextView
            tvTitleCategory.setText(categoryName);
        }

        if (Explore != null) {
            loadProducts();
        }
        return view;
    }

    private void showSortOptions() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);

        AppCompatButton btnSortByPriceAsc = dialogView.findViewById(R.id.btnSortByPriceAsc);
        AppCompatButton btnSortByPriceDesc = dialogView.findViewById(R.id.btnSortByPriceDesc);
        AppCompatButton btnSortByNameAsc = dialogView.findViewById(R.id.btnSortByNameAsc);
        AppCompatButton btnSortByNameDesc = dialogView.findViewById(R.id.btnSortByNameDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        btnSortByPriceAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPriceAscending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByPriceDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPriceDescending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByNameAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByNameAscending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByNameDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByNameDescending();
                productAdapter.notifyDataSetChanged();
            }
        });

        builder.show();
    }

    // Sort product list by ascending price
    private void sortByPriceAscending() {
        productsData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                double price1 = Double.parseDouble(p1.getPrice().replace(",", ""));
                double price2 = Double.parseDouble(p2.getPrice().replace(",", ""));
                return Double.compare(price1, price2);
            }
        });
    }

    // Sort product list by descending price
    private void sortByPriceDescending() {
        productsData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                double price1 = Double.parseDouble(p1.getPrice().replace(",", ""));
                double price2 = Double.parseDouble(p2.getPrice().replace(",", ""));
                return Double.compare(price2, price1);
            }
        });
    }

    // Sort product list by ascending name
    private void sortByNameAscending() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("vi_VN"));
        productsData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return collator.compare(p1.getName(), p2.getName());
            }
        });
    }

    // Sort product list by descending name
    private void sortByNameDescending() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("vi_VN"));
        productsData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return collator.compare(p2.getName(), p1.getName());
            }
        });
    }

    private void loadProducts() {

        productsData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject food = jsonArray.getJSONObject(i);
                        String name = new String(food.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String description = new String(food.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        productsData.add(new Product(
                                food.getInt("id"),
                                name,
                                description,
                                food.getString("price"),
                                food.getString("weight"),
                                food.getString("image_url"),
                                food.getInt("stock_quantity"),
                                food.getString("last_updated"),
                                food.getString("expiry_date"),
                                food.getInt("category_id")
                        ));
                    }
                    //Cập nhật lại giao diện
                    productAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Có lỗi xảy ra. Vui lòng thử lại." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };


        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Có lỗi xảy ra. Vui lòng thử lại." + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER.product_categories_php, thanhcong, thatbai) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("CategoryID", Explore);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}