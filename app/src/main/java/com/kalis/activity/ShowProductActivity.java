package com.kalis.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kalis.R;
import com.kalis.connection.ConnectionDetector;
import com.kalis.dialog.MyToast;
import com.kalis.keys.KeySource;
import com.kalis.log.LogSystem;
import com.kalis.model.Product;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ShowProductActivity extends AppCompatActivity {

    private ImageView imageViewProduct;
    private TextView txtPrice, txtDesription;
    private Product productSelected;
    private Toolbar toolbarProduct;
    private ArrayList<Product> favoriteProducts;
    private SQLiteDatabase sqLiteDatabase = null;
    private String table;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        addFacebookInit();

        addControls();
        addEvents();
        getDataFromMainActivity();
        connectDatabase();
        addToolbar();

    }

    private void addFacebookInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void connectDatabase() {
        sqLiteDatabase = openOrCreateDatabase(KeySource.DATABASE_NAME, MODE_PRIVATE, null);
    }

    private void addToolbar() {

        toolbarProduct = (Toolbar) findViewById(R.id.toolbarProduct);
        setSupportActionBar(toolbarProduct);
        ActionBar actionBar = getSupportActionBar();
        //  actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    private void getDataFromMainActivity() {
        try {
            Intent i = getIntent();
            Bundle bundle = i.getExtras();
            productSelected = (Product) bundle.getSerializable("ITEM");

            Ion.with(imageViewProduct).load(productSelected.getSrc());

//            Glide.with(this)
//                    .load(productSelected.getSrc())
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .crossFade()
//                    .placeholder(R.drawable.loading)
//                    .into(imageViewProduct);

            txtDesription.setText(productSelected.getDescription());
            txtPrice.setText(productSelected.getPrice() + " VND");
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }

    }

    private void addEvents() {
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.show_product_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.add_product_to_favorite:
                addProductToDataBase(KeySource.ADD_TO_FAVORITE_TABLE);
                break;
            case R.id.action_share:
                shareProduct();
                break;
            case R.id.add_product_to_shoppingcart:
                addProductToDataBase(KeySource.ADD_TO_SHOPPING_CART_TABLE);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addProductToDataBase(int requestCode) {
        table = KeySource.TABLES[0];

        if (requestCode == KeySource.ADD_TO_SHOPPING_CART_TABLE) table = KeySource.TABLES[1];

        if (!checkProductExist()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KeySource.PRODUCT_FIELD[0], productSelected.getCateId());
            contentValues.put(KeySource.PRODUCT_FIELD[1], productSelected.getId());
            contentValues.put(KeySource.PRODUCT_FIELD[2], productSelected.getCode());
            contentValues.put(KeySource.PRODUCT_FIELD[3], productSelected.getPrice());
            contentValues.put(KeySource.PRODUCT_FIELD[4], productSelected.getDiscount());
            contentValues.put(KeySource.PRODUCT_FIELD[5], productSelected.getDescription());
            contentValues.put(KeySource.PRODUCT_FIELD[6], productSelected.getSrc());
            long l = sqLiteDatabase.insert(table, null, contentValues);
            if ( l == -1 ) {
                MyToast.toastShort(this, getString(R.string.notification_error_to_add_product));
                return;
            }
            if (requestCode == KeySource.ADD_TO_FAVORITE_TABLE)
            {
                if (l != -1) {
                    MyToast.toastShort(this, getString(R.string.notification_add_product_to_favorite_table_success));
                }
            }
            else
            {
                if (l != -1) {
                    MyToast.toastShort(this, getString(R.string.notification_add_product_to_shoppingcart_table_success));
                }
            }
        } else {

                if (requestCode == KeySource.ADD_TO_FAVORITE_TABLE) MyToast.toastShort(this, getString(R.string.notification_exist_product_in_favorite_table));
                else if (requestCode == KeySource.ADD_TO_SHOPPING_CART_TABLE) MyToast.toastShort(this, getString(R.string.notification_exist_product_in_shoppingcart_table));

        }
    }

    private boolean checkProductExist() {
        String query = "SELECT * FROM "
                + table + " WHERE " + KeySource.PRODUCT_FIELD[1] + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{productSelected.getId() + ""});
        long l = cursor.getCount();
        if (l != 0) {
            return true;
        }
        return false;
    }

    private void shareProduct() {
        if (ConnectionDetector.isConnected(this)) {


                String description = productSelected.getDescription() + " - " + productSelected.getPrice();

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentTitle(description)
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .setImageUrl(Uri.parse(productSelected.getSrc()))
                        .build();

                shareDialog.show(content);

        } else {
            MyToast.toastShort(this, getString(R.string.error_load));
        }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void addControls() {

        imageViewProduct = (ImageView) findViewById(R.id.imgItem);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtDesription = (TextView) findViewById(R.id.txtDescription);
        favoriteProducts = new ArrayList<>();

    }

}
