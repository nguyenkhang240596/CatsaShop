package com.kalis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.kalis.R;
import com.kalis.adapter.ProductAdapter;
import com.kalis.keys.KeySource;
import com.kalis.model.Product;

import java.util.ArrayList;

public class SearchActivity extends FragmentActivity {

    RecyclerView recyclerViewSearch;
    ProductAdapter productAdapter;
    ArrayList<Product> listProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerViewSearch = (RecyclerView) findViewById(R.id.rvSearch);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        listProduct = (ArrayList<Product>) b.getSerializable(KeySource.BUNDLE_PUT_PRODUCTS);
        productAdapter = new ProductAdapter(this,listProduct);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewSearch.setLayoutManager(mLayoutManager);
        recyclerViewSearch.setAdapter(productAdapter);
        recyclerViewSearch.setItemAnimator(new DefaultItemAnimator());


    }

    public void onCloseButton(View V)
    {
        onBackPressed();
    }


}
