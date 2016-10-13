package com.kalis.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.kalis.R;
import com.kalis.activity.MainActivity;
import com.kalis.activity.SearchActivity;
import com.kalis.connection.ConnectionDetector;
import com.kalis.dialog.AlertDialog;
import com.kalis.dialog.MyToast;
import com.kalis.keys.KeySource;
import com.kalis.log.LogSystem;
import com.kalis.model.Product;
import com.kalis.request.RequestProducts;
import com.kalis.thread.CloseActivityThread;

import java.util.ArrayList;

/**
 * Created by Kalis on 12/29/2015.
 */
    public class SearchViewListener implements android.support.v7.widget.SearchView.OnQueryTextListener {

    private Activity activity;
    private MenuItem searchItem;
    private String errString;
    ArrayList<Product> listProduct;

    public SearchViewListener(Activity activity, MenuItem searchItem, String errString) {
        this.activity = activity;
        this.searchItem = searchItem;
        this.errString = errString;
        listProduct = new ArrayList<>();



    }

    @Override
    public boolean onQueryTextSubmit(String key) {

        if (ConnectionDetector.isConnected(activity))
        {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );

            searchItem.collapseActionView();

            try {

                new RequestProducts(activity).execute(KeySource.SEARCH_WITH_KEY + key);

//                if (listProduct == null) {
//                    searchErrorMessage();
//                } else {
//                    sendDataToSearchActivity();
//                }

            } catch (Exception e) {
                LogSystem.E(e.toString());
            }

        }
        else
        {
            new AlertDialog(activity).dialogOnlyRead("Warning", activity.getString(R.string.no_internet_access));
            new CloseActivityThread().waitSecondToCloseActivity(2, activity);
        }
        return true;
    }

    private void sendDataToSearchActivity() {

        if (listProduct.size() > 0)
        {
            Intent intent = new Intent(activity, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(KeySource.BUNDLE_PUT_PRODUCTS, listProduct);
            intent.putExtras(bundle);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            ((MainActivity)activity).offProgressBar();
        }
        else
        {
            MyToast.toastLong(activity,"không tìm thấy sản phẩm");
        }
    }

    private void searchErrorMessage() {
        MyToast.toastLong(activity, errString);

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}
