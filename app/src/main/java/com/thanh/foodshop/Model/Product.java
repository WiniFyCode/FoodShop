package com.thanh.foodshop.Model;

import java.io.Serializable;

public class Product implements Serializable {

    public int idfood;
    public String tenfood;
    public int dongia;
    public String mota;
    public String hinhminhhoa;
    public String ngaythemvao;
    public String ngayhethan;
    public int soluongban;

    public Product(int soluongban, String ngayhethan, String ngaythemvao, String hinhminhhoa, String mota, int dongia, String tenfood, int idfood) {
        this.soluongban = soluongban;
        this.ngayhethan = ngayhethan;
        this.ngaythemvao = ngaythemvao;
        this.hinhminhhoa = hinhminhhoa;
        this.mota = mota;
        this.dongia = dongia;
        this.tenfood = tenfood;
        this.idfood = idfood;
    }
}
