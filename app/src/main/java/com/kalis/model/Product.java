package com.kalis.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kalis on 12/23/2015.
 */

public class Product implements Serializable {

    @SerializedName("cateId")
    private int cateId;
    @SerializedName("id")
    private int id;
    @SerializedName("code")
    private String code;
    @SerializedName("price")
    private String price;
    @SerializedName("src")
    private String src;
    private int discount;
    private Category category;
    @SerializedName("description")
    private String description;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }



    private ArrayList<Product> arrayListProduct;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }



    public ArrayList<Product> getArrayListProduct() {
        return (arrayListProduct==null ? (arrayListProduct = new ArrayList<>()) : arrayListProduct);
    }

    public void setArrayListProduct(ArrayList<Product> arrayListProduct) {
        this.arrayListProduct = arrayListProduct;
    }




    public Product(int id, String src, String price, String description,int cateId,String code) {
        this.cateId = cateId;
        this.id = id;
        this.price = price;
        this.code = code;
        this.description = description;
        this.src = src;
//        this.setCategory(category);
        arrayListProduct = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  code + "-" + price;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product() {

    }
}
