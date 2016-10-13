package com.kalis.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kalis on 12/28/2015.
 */
public class Category implements Serializable {

    private ArrayList<Product> listProducts;
    private int cateId;
    private String cateName;
    private String cateCount;
    private String cateSrc;

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCateCount() {
        return cateCount;
    }

    public void setCateCount(String cateCount) {
        this.cateCount = cateCount;
    }

    public String getCateSrc() {
        return cateSrc;
    }

    public void setCateSrc(String cateSrc) {
        this.cateSrc = cateSrc;
    }

    public Category(String cateName) {
        this.cateName = cateName;
    }

    public Category() {

    }

    public Category(int cateId, String cateName, String cateCount, String cateSrc) {

        this.cateId = cateId;
        this.cateName = cateName;
        this.cateCount = cateCount;
        this.cateSrc = cateSrc;
    }
}
