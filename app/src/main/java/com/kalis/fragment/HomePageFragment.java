package com.kalis.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kalis.R;
import com.kalis.keys.KeySource;
import com.kalis.request.GoogleMapRequest;


public class HomePageFragment extends Fragment {

    private int categoryIdPosition;
    private Button btnSettings;
    private Location mLocation;
    private Spinner spinner;
    private ProgressDialog dialog;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayAdapter<String> adapter;

    private final String[] TRAVEL_MOVES = {
            "driving",
            "walking",
            "bicycling",
            "bus",
            "subway",
            "train",
            "tram",
            "rail"};

    private String travelMode = TRAVEL_MOVES[0];

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }


    @SuppressLint("ValidFragment")
    public HomePageFragment(int categoryIdPosition) {
        this.categoryIdPosition = categoryIdPosition;
    }

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);


        return rootView;
    }

    private void addControls(View v) {
        dialog = new ProgressDialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setMessage("Loading Map ...");
//        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        mLocation = location;


                    }
                });
                try {
                    mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                                    .title("You are here !")
//                                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_dialog_map))
                    );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        });

        spinner = (Spinner) v.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.map_types));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        if (mLocation != null)
        {
            initCamera(mLocation);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSettings = (Button) view.findViewById(R.id.btnSettings);
        addControls(view);
        addEvent(view);
    }

    private void addEvent(View v) {

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsMap();

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap!= null)
                {
                    xuLyDoiMapTypes(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void goToShop() {
        LatLng shopLatLng = new LatLng(KeySource.SHOP_LATITUDE,KeySource.SHOP_LONGITUDE);
        LatLng mLatLng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
        new GoogleMapRequest().loadData( getActivity(), mMap, mLatLng, shopLatLng, travelMode);
    }

    private void initCamera(Location location) {

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(15f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void xuLyDoiMapTypes(int position) {

        switch (position) {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

    }

    private void showSettingsMap() {

        final PopupMenu popup = new PopupMenu(getActivity(), btnSettings);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.map_menu, popup.getMenu());
        MenuItem itemTravelMode = popup.getMenu().findItem(R.id.travel_mode);
        itemTravelMode.setTitle("Travel Mode" + " ( " + travelMode + " ) ");


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.travel_mode_driving:
                        travelMode = TRAVEL_MOVES[0];
                        break;
                    case R.id.travel_mode_walking:
                        travelMode = TRAVEL_MOVES[1];
                        break;
                    case R.id.travel_mode_bicycling:
                        travelMode = TRAVEL_MOVES[2];
                        break;
                    case R.id.transit_mode_bus:
                        travelMode = TRAVEL_MOVES[3];
                        break;
                    case R.id.transit_mode_subway:
                        travelMode = TRAVEL_MOVES[4];
                        break;
                    case R.id.transit_mode_train:
                        travelMode = TRAVEL_MOVES[5];
                        break;
                    case R.id.transit_mode_tram:
                        travelMode = TRAVEL_MOVES[6];
                        break;
                    case R.id.transit_mode_rail:
                        travelMode = TRAVEL_MOVES[7];
                        break;
                    case R.id.action_go_to_shop: {
                        goToShop();
                        return true;
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
