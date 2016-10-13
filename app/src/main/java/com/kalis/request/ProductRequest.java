package com.kalis.request;

import com.kalis.adapter.ViewPagerAdapter;
import com.kalis.fragment.ProductFragment;
import com.kalis.keys.KeySource;
import com.kalis.model.Product;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Kalis on 1/1/2016.
 */
public class ProductRequest {
    private ProductFragment f;

    public void loadData(final Integer param, final ViewPagerAdapter viewPagerAdapter) {
        f = (ProductFragment) viewPagerAdapter.getItem(param);
        BaseRequest br = new BaseRequest(KeySource.BASE_JSON_URL);
        RequestInterface ri = br.getService();
        Call<List<Product>> call = ri.loadProducts(param);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Response<List<Product>> response, Retrofit retrofit) {
                for (Product p : response.body()) {

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

                    f.addDataToRecyclerView(p);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }

        });
    }

}
