package com.thanh.foodshop.Activity;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.ProductAdapter;
import com.thanh.foodshop.Model.Categories;
import com.thanh.foodshop.Model.Product;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ProductAdapter productAdapter;
    public ArrayList<Product> productList, filteredList;
    public SearchView searchView;

    AppCompatButton btnBack, btnSort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.rcvSearch);
        searchView = findViewById(R.id.searchView);

        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productAdapter = new ProductAdapter(filteredList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);

        loadProduct();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCategories(newText);
                return true;
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSort = findViewById(R.id.btnSort);
        btnSort.setOnClickListener(v -> {
            showSortOptions();
        });
    }

    private void showSortOptions() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);

        AppCompatButton btnSortByPriceAsc = dialogView.findViewById(R.id.btnSortByPriceAsc);
        AppCompatButton btnSortByPriceDesc = dialogView.findViewById(R.id.btnSortByPriceDesc);
        AppCompatButton btnSortByNameAsc = dialogView.findViewById(R.id.btnSortByNameAsc);
        AppCompatButton btnSortByNameDesc = dialogView.findViewById(R.id.btnSortByNameDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    // Sắp xếp danh sách sản phẩm theo giá tăng dần
    private void sortByPriceAscending() {
        // Sắp xếp danh sách sản phẩm theo giá tăng dần
        // Lấy giá của từng sản phẩm, loại bỏ dấu phẩy và chuyển thành số thực
        // So sánh hai giá trị để sắp xếp tăng dần
        filteredList.sort((p1, p2) -> {
            double price1 = Double.parseDouble(p1.getPrice().replace(",", ""));
            double price2 = Double.parseDouble(p2.getPrice().replace(",", ""));
            return Double.compare(price1, price2);
        });
    }

    // Sắp xếp danh sách sản phẩm theo giá giảm dần
    private void sortByPriceDescending() {
        // Sắp xếp danh sách sản phẩm theo giá giảm dần
        // Lấy giá của từng sản phẩm, loại bỏ dấu phẩy và chuyển thành số thực
        // So sánh hai giá trị để sắp xếp giảm dần
        filteredList.sort((p1, p2) -> {
            double price1 = Double.parseDouble(p1.getPrice().replace(",", ""));
            double price2 = Double.parseDouble(p2.getPrice().replace(",", ""));
            return Double.compare(price2, price1);
        });
    }

    // Sắp xếp danh sách sản phẩm theo tên tăng dần (không phân biệt chữ hoa, chữ thường)
    private void sortByNameAscending() {
        // Sắp xếp danh sách sản phẩm theo tên tăng dần (không phân biệt chữ hoa, chữ thường)
        Collator collator = Collator.getInstance(Locale.forLanguageTag("vi_VN"));
        filteredList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                String name1 = p1.getName().replaceAll("\\[.*?\\]", "").trim();
                String name2 = p2.getName().replaceAll("\\[.*?\\]", "").trim();
                return collator.compare(name1, name2);
            }
        });
    }

    // Sắp xếp danh sách sản phẩm theo tên giảm dần (không phân biệt chữ hoa, chữ thường)
    private void sortByNameDescending() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("vi_VN"));
        filteredList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                String name1 = p1.getName().replaceAll("\\[.*?\\]", "").trim();
                String name2 = p2.getName().replaceAll("\\[.*?\\]", "").trim();
                return collator.compare(name2, name1);
            }
        });
    }

    private void filterCategories(String query) {
        query = query.toLowerCase();
        if (query.isEmpty()) {
            productAdapter.setProducts(productList);
        } else {
            ArrayList<Product> filteredProducts = new ArrayList<>();
            // Bo di cac ky tu dac biet
            String chuoiKoDau = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            // Normalizer.NFD là một phương thức trong Java để chuyển chuỗi văn bản sang dạng Unicode Normalization Form D (NFD)
            for (Product product : productList) {
                String productName = product.getName().toLowerCase();
                String productKoDau = Normalizer.normalize(productName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                if (productKoDau.contains(chuoiKoDau)) {
                    filteredProducts.add(product);
                }
            }
            productAdapter.setProducts(filteredProducts);
        }
        productAdapter.notifyDataSetChanged();
    }

    public void loadProduct() {
        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    productList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product product = new Product(
                                jsonObject.getInt("id"),
                                new String(jsonObject.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8),
                                new String(jsonObject.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8),
                                jsonObject.getString("price"),
                                jsonObject.getString("weight"),
                                jsonObject.getString("image_url"),
                                jsonObject.getInt("stock_quantity"),
                                jsonObject.getString("last_updated"),
                                jsonObject.getString("expiry_date"),
                                jsonObject.getInt("category_id")
                        );
                        productList.add(product);
                    }
                    filteredList.addAll(productList);
                    productAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi tải dữ liệu
                } catch (JSONException e) {
                    Toast.makeText(SearchActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        StringRequest stringRequest = new StringRequest(SERVER.get_all_product_php, thanhcong, thatbai);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
