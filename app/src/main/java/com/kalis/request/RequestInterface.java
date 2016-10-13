package com.kalis.request;

import com.kalis.googlemap.model.DirectionResults;
import com.kalis.googlemap.model.DistanceAndDurationResults;
import com.kalis.model.Category;
import com.kalis.model.Product;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Kalis on 1/8/2016.
 */
public interface RequestInterface {

    @GET("/connect.php?")
    Call<List<Product>> loadProducts(@Query("load_product_with_cateid") Integer cateId);

    @GET("/connect.php?")
    Call<List<Category>> getCategories(@Query("load_category") Integer cateId);

    //        @GET("/maps/api/directions/json")
//        public void getJsonDirectionResults(@Query("origin") String origin,
//                                            @Query("destination") String destination,
//                                            @Query("sensor=false&mode") String travelMode,
//                                            Callback<DirectionResults> callback);
//
//        @GET("/maps/api/distancematrix/json")
//        public void getDistanceAndDuration(@Query("origins") String origin, @Query("destinations") String destination,
//                                           @Query("sensor=false&language") String language,
//                                           @Query("mode") String travelmode,
//                                           Callback<DistanceAndDurationResults> callback);
    @GET("/maps/api/directions/json")
    Call<DirectionResults> getGoogleMapResponse(@Query("origin") String origin,
                                                @Query("destination") String destination,
                                                @Query("sensor=false&mode") String travelMode);

    @GET("/maps/api/distancematrix/json")
    Call<DistanceAndDurationResults> getDistanceAndDuration(@Query("origins") String origin, @Query("destinations") String destination,
                                                            @Query("sensor=false&language") String language,
                                                            @Query("mode") String travelmode);
}
