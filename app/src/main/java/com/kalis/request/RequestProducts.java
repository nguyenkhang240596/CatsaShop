package com.kalis.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Window;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kalis.R;
import com.kalis.activity.MainActivity;
import com.kalis.activity.SearchActivity;
import com.kalis.adapter.ProductAdapter;
import com.kalis.dialog.MyToast;
import com.kalis.keys.KeySource;
import com.kalis.log.LogSystem;
import com.kalis.model.Product;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kalis on 12/28/2015.
 */
public class RequestProducts extends AsyncTask<String, Product, ArrayList<Product>> {

    Activity activity;
    ProgressDialog p;
    ArrayList<Product> listProduct;
    SearchActivity searchActivity;
    ProductAdapter productAdapter;


    public RequestProducts(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        p = new ProgressDialog(activity);
        p.setMessage("Loading ... ");
        p.requestWindowFeature(Window.FEATURE_NO_TITLE);
        p.setCanceledOnTouchOutside(false);
        p.show();

        searchActivity = new SearchActivity();
        listProduct = new ArrayList<>();


    }

    @Override
    protected void onPostExecute(ArrayList<Product> products) {
        super.onPostExecute(products);
        p.dismiss();
        if (listProduct.size() > 0)
        {
            Intent intent = new Intent(activity, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(KeySource.BUNDLE_PUT_PRODUCTS, listProduct);
            intent.putExtras(bundle);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else
        {
            MyToast.toastLong(activity,activity.getString(R.string.error_search));
        }
    }

    @Override
    protected ArrayList<Product> doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            Type listType = new TypeToken<List<Product>>() {
            }.getType();
            List<Product> product = new Gson().fromJson(reader, listType);

            if (product.size() == 0 || product == null) return null;
            for (int i = 0; i < product.size(); i++) {

                SystemClock.sleep(100);

                Product p = product.get(i);

                String prices[] = p.getPrice().split("&");
                p.setPrice(prices[0]);
                if (p.getCateId() == 6) {
                    String des[] = p.getDescription().split("&#8211;");
                    if (des.length > 1) {
                        p.setDescription(des[0] + des[1]);
                    } else {
                        p.setDescription(des[0]);
                    }
                }
                listProduct.add(p);
            }
            return listProduct;
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }

        return null;

    }


}
