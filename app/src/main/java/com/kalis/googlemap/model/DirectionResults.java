package com.kalis.googlemap.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kalis on 1/2/2016.
*/
public class DirectionResults {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }}