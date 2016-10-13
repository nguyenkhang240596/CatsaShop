package com.kalis.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kalis.R;
import com.kalis.adapter.ProductAdapter;
import com.kalis.dialog.MyToast;
import com.kalis.log.LogSystem;
import com.kalis.model.Category;
import com.kalis.model.Product;

import java.util.ArrayList;

public class ProductFragment extends Fragment {

    private ArrayList<Product> listProduct;
    private ProductAdapter productAdapter;
    private ProgressDialog pDialog;
    protected RecyclerView.LayoutManager mLayoutManager;
    private int categoryId;
    private RecyclerView recycleviewProduct;
    private Category category;

    public ProductFragment() {
        listProduct = new ArrayList<>();
    }

    public ProductFragment(Category category) {
        this.categoryId = category.getCateId();
        this.category = category;
        listProduct = new ArrayList<>();
    }

    public void addDataToRecyclerView(Product p) {
        if (productAdapter == null)
            productAdapter = new ProductAdapter(getActivity(), listProduct, category);
        if (listProduct == null) listProduct = new ArrayList<>();
        listProduct.add(p);
        productAdapter.notifyDataSetChanged();
    }


    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView(view);

    }

    private void setUpRecyclerView(View view) {
        try {

            recycleviewProduct = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            mLayoutManager = new GridLayoutManager(getActivity(), 2);

            recycleviewProduct.setLayoutManager(mLayoutManager);
            productAdapter = new ProductAdapter(getActivity(), listProduct, category);
            recycleviewProduct.setAdapter(productAdapter);
        } catch (Exception e) {
            MyToast.toastShort(getActivity(), getString(R.string.error_load));
            LogSystem.E(e.toString());
        }
    }

}
