package com.thanh.foodshop.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    public int id;
    public String name;
    public String description;
    public String price;
    public String weight;
    public int category_id;
    public String image_url;
    public String stock_quantity;
    public String last_updated;
    public String expiry_date;

    public Product(int id, String name, String description, String price, String weight, int category_id, String image_url, String stock_quantity, String last_updated, String expiry_date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.category_id = category_id;
        this.image_url = image_url;
        this.stock_quantity = stock_quantity;
        this.last_updated = last_updated;
        this.expiry_date = expiry_date;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        weight = in.readString();
        category_id = in.readInt();
        image_url = in.readString();
        stock_quantity = in.readString();
        last_updated = in.readString();
        expiry_date = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(weight);
        dest.writeInt(category_id);
        dest.writeString(image_url);
        dest.writeString(stock_quantity);
        dest.writeString(last_updated);
        dest.writeString(expiry_date);
    }
}
