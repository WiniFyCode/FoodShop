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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
