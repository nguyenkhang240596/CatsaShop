package com.kalis.request;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kalis.dialog.MyToast;
import com.kalis.googlemap.RouteDecode;
import com.kalis.googlemap.model.DirectionResults;
import com.kalis.googlemap.model.DistanceAndDurationResults;
import com.kalis.googlemap.model.Location;
import com.kalis.googlemap.model.Route;
import com.kalis.googlemap.model.Rows;
import com.kalis.googlemap.model.Steps;
import com.kalis.keys.KeySource;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Kalis on 1/1/2016.
 */
public class GoogleMapRequest {
    private RequestInterface service;

    public void loadData(final Activity activity, final GoogleMap mMap, final LatLng fromPosition, final LatLng toPosition, final String mode) {

        BaseRequest br = new BaseRequest(KeySource.BASE_MAP_URL);
        final RequestInterface service = br.getService();

        Call<DirectionResults> call = service.getGoogleMapResponse(fromPosition.latitude + "," + fromPosition.longitude, toPosition.latitude + "," + toPosition.longitude, mode);
        call.enqueue(new Callback<DirectionResults>() {
                         @Override
                         public void onResponse(Response<DirectionResults> response, Retrofit retrofit) {
                             String modeResult = "";
                             ArrayList<LatLng> routelist = new ArrayList<LatLng>();
                             Double distanceDouble = 0.0;
                             if (response.body().getRoutes().size() > 0) {
                                 ArrayList<LatLng> decodelist;
                                 Route routeA = response.body().getRoutes().get(0);
                                 if (routeA.getLegs().size() > 0) {
                                     List<Steps> steps = routeA.getLegs().get(0).getSteps();
                                     Steps step;
                                     Location location;
                                     String polyline;
                                     for (int i = 0; i < steps.size(); i++) {
                                         step = steps.get(i);
                                         location = step.getStart_location();
                                         routelist.add(new LatLng(location.getLat(), location.getLng()));
                                         polyline = step.getPolyline().getPoints();
                                         decodelist = RouteDecode.decodePoly(polyline);
                                         routelist.addAll(decodelist);
                                         location = step.getEnd_location();
                                         modeResult = step.getTravelMode();
                                         routelist.add(new LatLng(location.getLat(), location.getLng()));
                                     }
                                 }
                             } else {
                                 MyToast.toastLong(activity, KeySource.NO_WAY_RESUTLS);
                             }
                             if (routelist.size() > 0) {
                                 PolylineOptions rectLine = new PolylineOptions().width(10).color(
                                         Color.RED);

                                 for (int i = 0; i < routelist.size(); i++) {
                                     rectLine.add(routelist.get(i));
                                 }
                                 // Adding route on the map
                                 mMap.addPolyline(rectLine);
                                 //  mMap.addPolyline(rectLine);
                                 MarkerOptions markerOptions = new MarkerOptions();
                                 markerOptions.position(toPosition);

                                 markerOptions.draggable(true);

                                 mMap.addMarker(markerOptions);
                                 //update the camera
                                 LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                 builder.include(fromPosition);
                                 builder.include(toPosition);
                                 LatLngBounds bounds = builder.build();
                                 CameraUpdate camenUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//                                mMap.moveCamera(camenUpdate);
                                 mMap.animateCamera(camenUpdate);
//                                double a = (fromPosition.latitude+toPosition.latitude)/2;
//                                double b = (fromPosition.longitude+toPosition.longitude)/2;
//                                CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(a,b),mMap.getCameraPosition().zoom-1);
//                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                 final String finalModeResult = modeResult;
                                 Call<DistanceAndDurationResults> callback = service.getDistanceAndDuration(fromPosition.latitude + "," + fromPosition.longitude, toPosition.latitude + ","
                                         + toPosition.longitude, "vi-VI", mode);
                                 callback.enqueue(new Callback<DistanceAndDurationResults>() {
                                     @Override
                                     public void onResponse(Response<DistanceAndDurationResults> response, Retrofit retrofit) {
                                         DistanceAndDurationResults distanceAndDurationResults = response.body();
                                         List<String> originAddress = distanceAndDurationResults.getOrigin_addresses();
                                         List<String> destinationAddresses = distanceAndDurationResults.getDestination_addresses();
                                         List<Rows> rows = distanceAndDurationResults.getRows();
                                         if (rows.get(0).getElements().get(0).getStatus().equalsIgnoreCase("OK")) {
                                             MyToast.toastLong(activity, "From : " + originAddress + "\n To : " + destinationAddresses
                                                     + "\nTime : " + rows.get(0).getElements().get(0).getDuration().getText() + "- Distance : " + rows.get(0).getElements().get(0).getDistance().getText()
                                                     + " by " + mode);

                                         } else {
                                             MyToast.toastLong(activity, "You cant get there by " + mode.toLowerCase());
                                         }
                                     }

                                     @Override
                                     public void onFailure(Throwable t) {

                                     }
                                 });


                             }

                         }



                         @Override
                         public void onFailure(Throwable t) {

                         }
                     }

        );
    }

}
