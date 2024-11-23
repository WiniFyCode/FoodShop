package com.thanh.foodshop.Model;

public class    Cart {
    public int id;
    public int product_id;
    public int user_id;
    public String name;
    public double price;
    public int quantity;
    public String image;
    public double total_price;
    public boolean selected;


    public Cart(int id, int product_id, int user_id, String name, double price, int quantity, String image, double total_price) {
        this.id = id;
        this.product_id = product_id;
        this.user_id = user_id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.total_price = total_price;
        this.selected = false; // Mặc định là false
    }

    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", product_id=" + product_id +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", total_price=" + total_price +
                '}';
    }
}