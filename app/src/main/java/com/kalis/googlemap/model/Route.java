package com.kalis.googlemap.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kalis on 1/2/2016.
 */
public class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyLine overviewPolyLine;

    private List<Legs> legs;

    public OverviewPolyLine getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public List<Legs> getLegs() {
        return legs;
    }
}
