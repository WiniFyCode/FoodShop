package com.thanh.foodshop.Model;

import java.io.Serializable;

public class Product implements Serializable {

    public int id;
    public String name;
    public String description;
    public String price;
    public String weight;
    public String image_url;
    public int stock_quantity;
    public String last_updated;
    public String expiry_date;
    public int category_id;
    public boolean isFavorited = false;

    public Product(int id, String name, String description, String price, String weight, String image_url, int stock_quantity, String last_updated, String expiry_date, int category_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.image_url = image_url;
        this.stock_quantity = stock_quantity;
        this.last_updated = last_updated;
        this.expiry_date = expiry_date;
        this.category_id = category_id;
    }

    public Product (int id, String name, String description, String price, String weight, String image_url,int stock_quantity, boolean isFavorited) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.image_url = image_url;
        this.stock_quantity = stock_quantity;
        this.isFavorited = isFavorited;
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

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}
