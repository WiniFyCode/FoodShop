package com.thanh.foodshop.Model;

import java.io.Serializable;

public class Product implements Serializable {

    public int id;
    public String name;
    public String description;
    public String price;
    public String weight;
//    public int category_id;
    public String image_url;
//    public String stock_quantity;
//    public String last_updated;
//    public String expiry_date;

    public Product(int id, String name, String description, String price, String weight, String image_url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
//        this.category_id = category_id;
        this.image_url = image_url;
//        this.stock_quantity = stock_quantity;
//        this.last_updated = last_updated;
//        this.expiry_date = expiry_date;
    }
}
