package com.kalis.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kalis.R;
import com.kalis.activity.FavoriteProductsAndShoppingCart;
import com.kalis.activity.ShowProductActivity;
import com.kalis.keys.KeySource;
import com.kalis.listener.RecyclerViewListener;
import com.kalis.log.LogSystem;
import com.kalis.model.Product;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kalis on 12/25/2015.
 */
public class ProductAdapterFavoShop extends RecyclerView.Adapter<ProductAdapterFavoShop.ProductViewHolder> implements RecyclerViewListener {


    private Activity activity;
    private ArrayList<Product> products;
    private String imgSrcSelected, txtCode, txtPrice;
    private int resource;
    private String table;
    private SQLiteDatabase sqLiteDatabase;
    private TextView txtToltalMoney;

    public void setCount(Integer[] count) {
        this.count = count;
    }

    private Integer[] count;

    public ProductAdapterFavoShop(Activity activity, ArrayList<Product> products, TextView txtToltalMoney, int resource, String table) {
        this.activity = activity;
        this.products = products;
        this.resource = resource;
        this.table = table;
        this.txtToltalMoney = txtToltalMoney;
    }

    public ProductAdapterFavoShop(Activity activity, ArrayList<Product> products, int resource, String table) {
        this.activity = activity;
        this.products = products;
        this.resource = resource;
        this.table = table;
    }

    public ProductAdapterFavoShop(Activity activity, ArrayList<Product> products, String table) {
        this.products = products;
        this.table = table;
        this.activity = activity;
    }

    public ProductAdapterFavoShop(Activity activity, ArrayList<Product> products) {
        this.products = products;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return pvh;
    }


    @Override

    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        try {
            if (count == null) {
                count = new Integer[products.size() + 1];
                for (int i = 0; i < products.size(); i++) {
                    count[i] = 1;
                }
            }

            String txtDescription = products.get(position).getDescription();
            String txtPrice = products.get(position).getPrice();
            String imgSrcSelected = products.get(position).getSrc();
            holder.productDescription.setText(txtDescription + "");
            holder.productPrice.setText(txtPrice + " VND");
            Ion.with(holder.productImage).load(imgSrcSelected).setCallback(new FutureCallback<ImageView>() {
                @Override
                public void onCompleted(Exception e, ImageView result) {
                    holder.loadingImageProgressBar.setVisibility(View.GONE);
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListener(position, holder.productImage);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    xoaProduct(holder, position);
                    return false;
                }
            });

            if (table == KeySource.TABLES[1]) {

                addEvents(holder, position);


                if (count.length > 0) {
                    if (count[position] != null) {
                        holder.txtNumbers.setText(count[position] + "");
                    }
                }


            }

            setAnimation(holder.cardView, position);
        } catch (Exception e) {
            Log.e("Update Adapter :", e.toString());
        }

    }

    int getPriceOfShoppingCart() {
        return ((FavoriteProductsAndShoppingCart) activity).getPrice();
    }

    public Integer[] getCount() {
        return count;
    }

    private void addEvents(final ProductViewHolder holder, final int position) {
        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.valueOf(holder.txtNumbers.getText() + "");
                if (t < 100) {
                    holder.txtNumbers.setText(String.valueOf(t + 1) + "");
                    count[position] = Integer.parseInt(holder.txtNumbers.getText() + "");
                    int priceChanged = getPriceOfShoppingCart() + Integer.valueOf(products.get(position).getPrice().replace(".", ""));
                    ((FavoriteProductsAndShoppingCart) activity).setPrice(priceChanged);
                }

            }
        });

        holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.valueOf(holder.txtNumbers.getText() + "");
                if (t > 1) {
                    holder.txtNumbers.setText(String.valueOf(t - 1) + "");
                    count[position] = Integer.parseInt(holder.txtNumbers.getText() + "");
                    int priceChanged = getPriceOfShoppingCart() - Integer.valueOf(products.get(position).getPrice().replace(".", ""));
                    ((FavoriteProductsAndShoppingCart) activity).setPrice(priceChanged);
                }

            }
        });


    }

    private void xoaProduct(final ProductViewHolder holder, final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setMessage("Bạn muốn xóa sản phẩm : \n" + holder.productDescription.getText() + "");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (table == KeySource.TABLES[1]) {
                    int price = Integer.valueOf(products.get(position).getPrice().replace(".", "")) * (count[position] != null ? count[position] : 1);
                    int temp = Integer.valueOf(txtToltalMoney.getText().toString().replace(" VND", ""));
                    ((FavoriteProductsAndShoppingCart) activity).setPrice(temp - price);
                    txtToltalMoney.setText(String.valueOf((temp - price) + " VND"));
                }
                deleteFromSqlite(products.get(position).getCode());
                notifyItemRemoved(position);
                products.remove(position);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean deleteFromSqlite(String s) {
        sqLiteDatabase = activity.openOrCreateDatabase(KeySource.DATABASE_NAME, Context.MODE_PRIVATE, null);
        try {
            sqLiteDatabase.delete(table, "code='" + s + "'", null);
            return true;
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }
        return false;
    }

    private void setAnimation(CardView cardView, int position) {
//        if (position > products.size())
        {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_recyclerview);
            cardView.startAnimation(animation);
//            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }


    @Override
    public void onListener(int position, ImageView productImage) {
        Intent i = new Intent(activity, ShowProductActivity.class);
        Bundle bundle = new Bundle();
        imgSrcSelected = products.get(position).getSrc();
        txtCode = products.get(position).getCode();
        txtPrice = products.get(position).getPrice();
        bundle.putSerializable("ITEM", (Serializable) products.get(position));
        i.putExtras(bundle);
        activity.startActivity(i);

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView productDescription;
        TextView productPrice;
        ImageView productImage;
        ImageButton btnIncrease, btnDecrease;
        TextView txtNumbers;
        ProgressBar loadingImageProgressBar;

        ProductViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            productDescription = (TextView) itemView.findViewById(R.id.productDescription);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            btnIncrease = (ImageButton) itemView.findViewById(R.id.btnIncrease);
            btnDecrease = (ImageButton) itemView.findViewById(R.id.btnDecrease);
            txtNumbers = (TextView) itemView.findViewById(R.id.txtNumbers);
            loadingImageProgressBar = (ProgressBar) itemView.findViewById(R.id.loadingImageProgressBar);


        }
    }


}
