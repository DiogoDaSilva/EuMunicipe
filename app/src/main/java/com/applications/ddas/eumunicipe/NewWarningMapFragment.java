package com.applications.ddas.eumunicipe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
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
    private TextView municipality;
    private TextView email;
    private RelativeLayout thirdStepLayout;
    private ImageButton thirdStepButton;
    private boolean animate = true;
    private SQLiteDatabase database;
    private MainActivity mainActivity;
    private ArrayList<String> municipalities;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();
        newWarningMapView = inflater.inflate(R.layout.fragment_new_warning_map, container, false);
        municipality = (TextView) newWarningMapView.findViewById(R.id.new_warning_map_edit_municipality);
        email = (TextView) newWarningMapView.findViewById(R.id.new_warning_map_edit_email);
        thirdStepLayout = (RelativeLayout) newWarningMapView.findViewById(R.id.new_warning_layout_3_step);
        thirdStepButton = (ImageButton) newWarningMapView.findViewById(R.id.new_warning_goto_3_step_button);

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        database = MainActivity.database;

        municipality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (municipalities == null || municipalities.size() == 0) {
                    municipalities = DbManager.getMunicipalities(database);
                }
                //AlertDialog OptionDialog = new AlertDialog.Builder(this).create();
                final AlertDialog selectMunicipality = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater inflater = getLayoutInflater(savedInstanceState);
                final View convertView = (View)inflater.inflate(R.layout.fragment_municipalityitem_list, null);
                final ListView listView = (ListView) convertView.findViewById(R.id.municipality_list);

                final EditText search = (EditText) convertView.findViewById(R.id.search);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        search.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        int textLength = search.getText().length();
                        ArrayList<String> arraySort = new ArrayList<String>();

                        for (String municipalityName : municipalities) {
                            if (textLength <= municipalityName.length()) {
                                if (municipalityName.toLowerCase()
                                        .contains(search.getText().toString().toLowerCase().trim())) {

                                    arraySort.add(municipalityName);
                                }
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getActivity(), android.R.layout.select_dialog_singlechoice, arraySort);
                        listView.setAdapter(adapter);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.select_dialog_singlechoice, municipalities);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String city = (String) parent.getItemAtPosition(position);

                        Log.d("item", city.toString());

                        setMunicipalityProperties(city, "PT"); //TODO

                        selectMunicipality.dismiss();

                        thirdStepLayout.setVisibility(View.VISIBLE);
                    }
                });

                selectMunicipality.setView(convertView);

                selectMunicipality.show();
            }
        });


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
                    municipality.setText("Waiting for Location");
                }
                else {
                    if (addresses != null && addresses.size() > 0) {
                        for(Address address : addresses) {
                            Log.d("DebugMap", address.toString());
                        }

                        String city = addresses.get(0).getSubAdminArea();
                        String country = addresses.get(0).getCountryName();

                        setMunicipalityProperties(city, country);
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

    public void setMunicipalityProperties(String city, String country) {
            if (city != null) {
                String[] mailAndMunicipality =
                        DbManager.getEmailAndMunicipality(city, database);
                Log.d("Map", mailAndMunicipality[0] + mailAndMunicipality[1]);
                String address = city + ", " + mailAndMunicipality[1]
                        + ", " + country;
                municipality.setText(address);
                email.setText(mailAndMunicipality[0]);
                mainActivity.municipalityEmail = mailAndMunicipality[0];
                Log.d("DebugMap", address);
            }
    }

    public void animateMap(LatLng myLocation) {
        if (animate) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
            animate = false;
        }
    }
}
