package com.kalis.request;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kalis.log.LogSystem;
import com.kalis.model.Category;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kalis on 12/28/2015.
 */
public class RequestCategories extends AsyncTask<String,Object,ArrayList<Category>> {
    @Override
    protected ArrayList<Category> doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);

            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            Type listType = new TypeToken<List<Category>>(){}.getType();
            List<Category> category = new Gson().fromJson(reader,listType);
            if (category == null) {
                category = new ArrayList<>();
                category.add(new Category(("NO CONNECTION")));
            }
            ArrayList<Category> categories = new ArrayList<>();
            SystemClock.sleep(100);
            categories.addAll(category);
            return categories;
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }

        return null;

    }


}
