package com.thanh.foodshop.Class;

import static java.security.AccessController.getContext;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Locale;

public class SeeAll extends AppCompatActivity {

    RecyclerView rcvSeeAll;
    ProductAdapter productAdapter;
    ArrayList<Product> productData;
    TextView tvSeeAll;
    AppCompatButton btnBack, btnSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_all);

        rcvSeeAll = findViewById(R.id.rcvSeeAll);
        tvSeeAll = findViewById(R.id.tvSeeAll);

        productData = new ArrayList<>();
        productAdapter = new ProductAdapter(productData, this);

        rcvSeeAll.setLayoutManager(new GridLayoutManager(this, 2));
        rcvSeeAll.setAdapter(productAdapter);

        // Lay category từ intent
        String category = getIntent().getStringExtra("category");
        tvSeeAll.setText(category);

        // Load products dựa trên category
        if (category.equals("Exclusive Offer")) {
            loadExclusiveOfferProducts();
        } else if (category.equals("Best Selling")) {
            loadBestSellingProducts();
        } else {
            loadAllProducts();
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSortBy = findViewById(R.id.tvSortBy);
        btnSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortOptions();
            }
        });
    }

    private void showSortOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_custom_dialog, null);
        builder.setView(dialogView);

        AppCompatButton btnSortByPriceAsc = dialogView.findViewById(R.id.btnSortByPriceAsc);
        AppCompatButton btnSortByPriceDesc = dialogView.findViewById(R.id.btnSortByPriceDesc);
        AppCompatButton btnSortByNameAsc = dialogView.findViewById(R.id.btnSortByNameAsc);
        AppCompatButton btnSortByNameDesc = dialogView.findViewById(R.id.btnSortByNameDesc);

        btnSortByPriceAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPriceAscending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByPriceDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByPriceDescending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByNameAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNameAscending();
                productAdapter.notifyDataSetChanged();
            }
        });

        btnSortByNameDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNameDescending();
                productAdapter.notifyDataSetChanged();
            }
        });

        builder.show();
    }

    // Sort product list by ascending price
    private void sortByPriceAscending() {
        productData.sort(new Comparator<Product>() {
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
        productData.sort(new Comparator<Product>() {
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
        productData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return collator.compare(p1.getName(), p2.getName());
            }
        });
    }

    // Sort product list by descending name
    private void sortByNameDescending() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("vi_VN"));
        productData.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return collator.compare(p2.getName(), p1.getName());
            }
        });
    }

    private void loadExclusiveOfferProducts() {
        loadProducts(SERVER.exclusive_offer_php);
    }

    private void loadBestSellingProducts() {
        loadProducts(SERVER.bestfood_php);
    }

    private void loadAllProducts() {
        loadProducts(SERVER.food_url);
    }

    private void loadProducts(String apiUrl) {
        productData.clear();
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject food = jsonArray.getJSONObject(i);
                        String name = new String(food.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String description = new String(food.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        String price = new String(food.getString("price").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                        productData.add(new Product(
                                food.getInt("id"),
                                name,
                                description,
                                price,
                                food.getString("weight"),
                                food.getString("image_url"),
                                food.getInt("stock_quantity"),
                                food.getString("last_updated"),
                                food.getString("expiry_date"),
                                food.getInt("category_id")
                        ));
                    }
                    productAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(SeeAll.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SeeAll.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(apiUrl, thanhcong, thatbai);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}