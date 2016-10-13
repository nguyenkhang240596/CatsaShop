package com.kalis.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kalis.R;
import com.kalis.activity.ShowProductActivity;
import com.kalis.listener.RecyclerViewListener;
import com.kalis.log.LogSystem;
import com.kalis.model.Category;
import com.kalis.model.Product;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kalis on 12/25/2015.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements RecyclerViewListener {


    private Activity activity;
    private ArrayList<Product> products;
    private String imgSrcSelected, txtCode, txtPrice;
    private Category category;
    private int resource = 0;

    public ProductAdapter(Activity activity, ArrayList<Product> products, Category category) {
        this.products = products;
        this.activity = activity;
        this.category = category;
    }


    public ProductAdapter(Activity activity, ArrayList<Product> products) {
        this.products = products;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (resource == 0) resource = R.layout.layout_custom_product;
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ProductViewHolder pvh = new ProductViewHolder(v);

        return pvh;
    }

    @Override

    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        try {
            String txtDescription = products.get(position).getDescription();
            String txtPrice = products.get(position).getPrice();
            String imgSrcSelected = products.get(position).getSrc();


            products.get(position).setCategory(category);
            holder.productDescription.setText(txtDescription);
            holder.productPrice.setText(txtPrice + "VND");


            Ion.with(holder.productImage).load(imgSrcSelected).setCallback(new FutureCallback<ImageView>() {
                @Override
                public void onCompleted(Exception e, ImageView result) {
                    holder.loadingImageProgressBar.setVisibility(View.GONE);
                }
            });

//            Glide.with(activity)
//                    .load(imgSrcSelected)
//                    .centerCrop()
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .placeholder(R.drawable.loading)
//                    .into(holder.productImage);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListener(position, holder.productImage);
                }
            });

            setAnimation(holder.cardView, position);
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }

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

//        productImage.buildDrawingCache();
//        bm =  productImage.getDrawingCache();

        bundle.putSerializable("ITEM", (Serializable) products.get(position));
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//        byte[] byteArray = bStream.toByteArray();
//        i.putExtra(KeySource.BUNDLE_PUT_IMAGEVIEW, byteArray);
        i.putExtras(bundle);
        activity.startActivity(i);

    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView productDescription;
        TextView productPrice;
        ImageView productImage;
        ProgressBar loadingImageProgressBar;

        ProductViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            productDescription = (TextView) itemView.findViewById(R.id.productDescription);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            loadingImageProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_image_progress);

        }
    }


}
