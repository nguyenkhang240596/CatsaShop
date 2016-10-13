package com.kalis.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kalis.R;
import com.kalis.adapter.ProductAdapterFavoShop;
import com.kalis.fragment.OrderMenuFragment;
import com.kalis.keys.KeySource;
import com.kalis.log.LogSystem;
import com.kalis.model.Product;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteProductsAndShoppingCart extends BaseActivity {
    private TextView txtFavoriteProduct, txtToolbarTitle, txtTotalMoney;
    private RecyclerView rvProducts;
    private ArrayList<Product> listFavoriteProducts;
    private ArrayList<Product> listShopCartProducts;
    private ProductAdapterFavoShop adapter;
    private SQLiteDatabase sqLiteDatabdase = null;
    private Product p;
    private String table;
    private Button btnOrder;
    private OrderMenuFragment orderMenuFragment;
    private int price = 0;
    private String preName = "shopcart";
    private Integer[] count;

    @Override
    protected void onResume() {
        super.onResume();
        if (table == KeySource.TABLES[1]) {
            loadingPreferences();
        }
    }

    private void loadingPreferences() {

        SharedPreferences pre = getSharedPreferences(preName,MODE_PRIVATE);

        Map<String,?> keys = pre.getAll();

        if (count == null){
            if (keys.size() < listShopCartProducts.size())
            {
                count = new Integer[listShopCartProducts.size()];
            }
            else
            {
                count = new Integer[keys.size()];
            }
        }
        for (Map.Entry<String, ?> entry: keys.entrySet()) {
            count[Integer.valueOf(entry.getKey())] = (Integer) entry.getValue();
        }
        if (keys.size() > 0)
        {
            adapter.setCount(count);
            adapter.notifyDataSetChanged();
            this.price = 0;

            for (int i=0;i < listShopCartProducts.size();i++)
            {
                if (count[i] == null) count[i] = 1;
                int num =  count[i];
                int price = Integer.valueOf(listShopCartProducts.get(i).getPrice().replace(".", ""));

                this.price += price*num;
            }
            txtTotalMoney.setText(price + " VND");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (table == KeySource.TABLES[1]) savingPreferences();
    }

    private void savingPreferences() {
        count = adapter.getCount();
        SharedPreferences pre = getSharedPreferences(preName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();

        editor.clear();
        for (int i=0;i< listShopCartProducts.size();i++)
        {
            editor.putInt(i+"",count[i]);
        }
        editor.commit();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        table = KeySource.TABLES[0];

        Intent i = getIntent();
        Bundle b = i.getExtras();
        connectDatabase();

        int u = 5;
        if (b != null) {
            if (b.getInt(KeySource.BUNDLE_PUT_DATA) == KeySource.ADD_TO_FAVORITE_TABLE) {
                setContentView(R.layout.activity_favorite);
                addControlsFavorite();
                txtToolbarTitle.setText(getString(R.string.favorite_activity_title));
                addProductFromSqlite(table, listFavoriteProducts,adapter);
            } else {

                table = KeySource.TABLES[1];
                setContentView(R.layout.activity_shopping_cart);
                addControlsShoppingCart();
                txtToolbarTitle.setText(getString(R.string.shopping_cart_activity_title));
                addProductFromSqlite(table, listShopCartProducts,adapter);

            }
        }


        super.addToolbar(this, 0);

    }


    private void addControlsFavorite() {
        addControls();

        rvProducts = (RecyclerView) findViewById(R.id.rvFavoriteProducts);
//        txtFavoriteProduct = (TextView) findViewById(R.id.txtFa);
        listFavoriteProducts = new ArrayList<>();

        adapter = new ProductAdapterFavoShop(this, listFavoriteProducts, R.layout.layout_custom_favorite_product, table);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.anim_recyclerview_favorite);
        rvProducts.startAnimation(a);
        rvProducts.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProducts.setLayoutManager(layoutManager);


    }

    private void addControlsShoppingCart() {
        addControls();
        btnOrder = (Button) findViewById(R.id.btnOrder);
        txtTotalMoney = (TextView) findViewById(R.id.txtTotalMoney);
        rvProducts = (RecyclerView) findViewById(R.id.rvShopCartProducts);
        listShopCartProducts = new ArrayList<>();
        adapter = new ProductAdapterFavoShop(this, listShopCartProducts, txtTotalMoney,
                R.layout.layout_custom_shop_cart_product, table);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.anim_recyclerview_favorite);
        rvProducts.startAnimation(a);
        rvProducts.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProducts.setLayoutManager(layoutManager);

        addEvents();

    }

    private void addEvents() {

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOderMenuFragment();
            }
        });
    }

    private void openOderMenuFragment() {

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_shopping_cart_2);
        frameLayout.setVisibility(View.INVISIBLE);
        orderMenuFragment = new OrderMenuFragment();

        Bundle b = new Bundle();
        b.putInt(KeySource.DATA_SIZE,listShopCartProducts.size());

        for (int i=0;i<listShopCartProducts.size();i++)
        {

            b.putString(i+"",listShopCartProducts.get(i).toString()+"-"+count[i]);

        }

        orderMenuFragment.setArguments(b);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_shopping_cart, orderMenuFragment);


        fragmentTransaction.addToBackStack(null).commit();


    }

    private void addControls() {
        txtToolbarTitle = (TextView) findViewById(R.id.txtToolbarTitle);
    }


    private void connectDatabase() {
        sqLiteDatabdase = openOrCreateDatabase(KeySource.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
        txtTotalMoney.setText(price + " VND");
    }


    private void addProductFromSqlite(String table, ArrayList<Product> arrs,ProductAdapterFavoShop adapter) {
        try {
            Cursor cursor = sqLiteDatabdase.query(table, null, null, null, null, null, null);
            arrs.clear();
            while (cursor.moveToNext()) {
                p = new Product();
                int cateid = cursor.getInt(0);
                int id = cursor.getInt(1);
                String code = cursor.getString(2);
                String price = cursor.getString(3);
                String descripion = cursor.getString(5);
                String src = cursor.getString(6);
                p.setCateId(cateid);
                p.setId(id);
                p.setCode(code);
                p.setPrice(price);
                p.setDescription(descripion);
                p.setSrc(src);
                this.price += Integer.valueOf(price.replace(".",""));
                arrs.add(p);
            }
            if (table == KeySource.TABLES[1])
            {
                txtTotalMoney.setText(price + " VND");
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
