package com.kalis.googlemap.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kalis on 1/2/2016.
 */
public class OverviewPolyLine {

    @SerializedName("points")
    public String points;

    public String getPoints() {
        return points;
    }
}