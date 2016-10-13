package com.kalis.googlemap.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kalis on 1/3/2016.
 */
public class DistanceAndDurationResults {
    @SerializedName("destination_addresses")
    List<String> destination_addresses;
    @SerializedName("origin_addresses")
    List<String> origin_addresses;
    @SerializedName("rows")
    List<Rows> rows;

    public  List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public List<Rows> getRows() {
        return rows;
    }
}
