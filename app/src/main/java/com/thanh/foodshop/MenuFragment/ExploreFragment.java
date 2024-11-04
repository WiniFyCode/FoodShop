package com.thanh.foodshop.MenuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thanh.foodshop.Adapter.CategoryAdapter;
import com.thanh.foodshop.Class.CategoryDetail;
import com.thanh.foodshop.Model.Categories;
import com.thanh.foodshop.R;
import com.thanh.foodshop.SERVER;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    RecyclerView rcvExplore;
    ArrayList<Categories> categoriesData;
    CategoryAdapter categoryAdapter;

    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.explore_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvExplore = view.findViewById(R.id.rcvExplore);
        categoriesData = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoriesData);

        rcvExplore.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rcvExplore.setAdapter(categoryAdapter);

        searchView = view.findViewById(R.id.searchView);
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

        if (categoriesData.isEmpty()) {
            loadCategories();
        }

    }

    private void loadCategories() {

        Response.Listener<String> thanhcong = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject category = jsonArray.getJSONObject(i);
                        categoriesData.add(new Categories(
                                category.getInt("id"),
                                new String(category.getString("name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8),
                                category.getString("image"),
                                new String(category.getString("description").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
                        ));
                    }
                    categoryAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        };
        Response.ErrorListener thatbai = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(SERVER.category_php, thanhcong, thatbai);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void filterCategories(String query) {
        query = query.toLowerCase();
        if (query.isEmpty()) {
            categoryAdapter.setCategories(categoriesData);
        } else {
            ArrayList<Categories> filteredCategories = new ArrayList<>();
            // Bo di cac ky tu dac biet
            String queryNoMark = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            // Normalizer.NFD là một phương thức trong Java để chuyển chuỗi văn bản sang dạng Unicode Normalization Form D (NFD)
            for (Categories category : categoriesData) {
                String categoryName = category.getName().toLowerCase();
                String categoryNameNoMark = Normalizer.normalize(categoryName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                if (categoryNameNoMark.contains(queryNoMark)) {
                    filteredCategories.add(category);
                }
            }
            categoryAdapter.setCategories(filteredCategories);
        }
        categoryAdapter.notifyDataSetChanged();
    }
}