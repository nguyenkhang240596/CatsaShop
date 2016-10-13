package com.kalis.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Kalis on 1/1/2016.
 */
public class BaseRequest {
    private BaseRequest br;
    private RequestInterface ri;

    public BaseRequest(String url) {
        Gson gson = new GsonBuilder().create();

        Retrofit r = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url).build();
        ri = r.create(RequestInterface.class);
    }

    public BaseRequest() {
    }

    public BaseRequest getInstance() {
        if (br == null) {
            br = new BaseRequest();
        }
        return br;
    }

    public RequestInterface getService() {
        return ri;
    }






// + "&sensor=false"+ "&mode=" + mode

}



