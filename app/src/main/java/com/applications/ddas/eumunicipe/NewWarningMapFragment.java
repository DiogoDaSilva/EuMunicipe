package com.applications.ddas.eumunicipe;

import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by dsilva on 13-05-2015.
 */
public class NewWarningMapFragment extends PlaceholderFragment
        implements OnMapReadyCallback {

    private View newWarningMapView;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private LatLng myLocation;
    private EditText county;
    private EditText email;
    private RelativeLayout thirdStepLayout;
    private ImageButton thirdStepButton;
    private boolean animate = true;
    private SQLiteDatabase database;
    private MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();
        newWarningMapView = inflater.inflate(R.layout.fragment_new_warning_map, container, false);
        county = (EditText) newWarningMapView.findViewById(R.id.new_warning_map_edit_municipality);
        email = (EditText) newWarningMapView.findViewById(R.id.new_warning_map_edit_email);
        thirdStepLayout = (RelativeLayout) newWarningMapView.findViewById(R.id.new_warning_layout_3_step);
        thirdStepButton = (ImageButton) newWarningMapView.findViewById(R.id.new_warning_goto_3_step_button);

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        database = MainActivity.database;

        thirdStepButton.setOnClickListener(this);

        return newWarningMapView;
    }

    private void initializeMap() {
        if (googleMap == null) {
            for(Fragment f : getChildFragmentManager().getFragments()) {
                Log.d("debug", "" + f.getId() + " " + f.toString());
            }
            mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.new_warning_map);
            googleMap = mapFragment.getMap();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("DebugMap", "Entrei");

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        Location location = googleMap.getMyLocation();
        Log.d("DebugMap", googleMap.toString());
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                Geocoder geo = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.isEmpty()) {
                    county.setText("Waiting for Location");
                }
                else {
                    if (addresses != null && addresses.size() > 0) {
                        String city = addresses.get(0).getSubAdminArea();
                        for(Address address : addresses) {
                            Log.d("DebugMap", address.toString());
                        }

                        if (city != null) {
                            String[] mailAndMunicipality = DbManager.getEmailAndMunicipality(city);
                            Log.d("Map", mailAndMunicipality[0] + mailAndMunicipality[1]);
                            String address = city + ", " + mailAndMunicipality[1]
                                    + ", " + addresses.get(0).getCountryName();
                            county.setText(address);
                            email.setText(mailAndMunicipality[0]);
                            mainActivity.municipalityEmail = mailAndMunicipality[0];
                            Log.d("DebugMap", address);
                        }
                    }
                }

                myLocation = new LatLng(location.getLatitude(),location.getLongitude());

                mainActivity.myLocation = myLocation;

                animateMap(myLocation);
                thirdStepLayout.setVisibility(View.VISIBLE);
            }
        });

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void animateMap(LatLng myLocation) {
        if (animate) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
            animate = false;
        }
    }
}
