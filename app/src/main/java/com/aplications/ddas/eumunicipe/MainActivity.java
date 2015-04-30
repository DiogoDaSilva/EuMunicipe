package com.aplications.ddas.eumunicipe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    FragmentManager fragmentManager;

    private DbManager dbManager;
    public static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        dbManager = new DbManager(this);
        database = dbManager.getWritableDatabase();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section_home);
                break;
            case 1:
                mTitle = getString(R.string.title_section_new_warning);
                break;
            case 2:
                mTitle = getString(R.string.title_section_history);
                break;
            case 3:
                mTitle = getString(R.string.title_section_about);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        protected static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment;

            switch(sectionNumber) {
                case 0:
                    fragment = new HomeFragment();
                    break;

                case 1:
                    fragment = new NewWarningPhotoFragment();
                    break;
                default:
                    fragment = new PlaceholderFragment();
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment;
            int id = v.getId();
            switch (id) {
                case R.id.new_warning_goto_2_step_button:
                    fragment = new NewWarningMapFragment();
                    break;

                case R.id.new_warning_goto_3_step_button:
                    fragment = new NewWarningDescriptionFragment();
                    break;

                case R.id.new_warning_goto_4_step_button:
                    fragment = new NewWarningDescriptionFragment();
                    break;
                default:
                    fragment = new HomeFragment();
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, getArguments().getInt(ARG_SECTION_NUMBER));
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }





    public static class HomeFragment extends PlaceholderFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }
    }






    public static class NewWarningPhotoFragment extends PlaceholderFragment {
        private LayoutInflater inflater;
        private ViewGroup container;

        private static final int CAMERA_REQUEST = 1888;
        private View newWarningPhotoView;
        private ImageView photoView;
        private Button takePhotoButton;
        private RelativeLayout secondStepLayout;
        private ImageButton secondStepButton;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            newWarningPhotoView = inflater.inflate(R.layout.fragment_photo_new_warning, container, false);

            photoView = (ImageView) newWarningPhotoView.findViewById(R.id.new_warning_photo);
            takePhotoButton = (Button) newWarningPhotoView.findViewById(R.id.new_warning_photo_button);
            secondStepLayout = (RelativeLayout) newWarningPhotoView.findViewById(R.id.new_warning_layout_2_step);
            secondStepButton = (ImageButton) newWarningPhotoView.findViewById(R.id.new_warning_goto_2_step_button);

            takePhotoButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });

            secondStepButton.setOnClickListener(this);

            return newWarningPhotoView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photoView.setImageBitmap(photo);
                takePhotoButton.setText(R.string.new_warning_take_another_photo);
                secondStepLayout.setVisibility(View.VISIBLE);
            }
        }
    }





    /**
     * Created by ddas on 25-03-2015.
     */
    public static class NewWarningMapFragment extends MainActivity.PlaceholderFragment
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


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            newWarningMapView = inflater.inflate(R.layout.fragment_map_new_warning, container, false);
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
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.new_warning_map);
                mapFragment = supportMapFragment;
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
                    if (addresses.isEmpty()) {
                        county.setText("Waiting for Location");
                    }
                    else {
                        if (addresses.size() > 0) {
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

                                Log.d("DebugMap", address);
                            }
                        }
                    }

                    myLocation = new LatLng(location.getLatitude(),location.getLongitude());
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

    public static class NewWarningDescriptionFragment extends MainActivity.PlaceholderFragment {
        private View newWarningDescriptionView;
        private ImageButton forthStepButton;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            newWarningDescriptionView = inflater.inflate(R.layout.fragment_description_new_warning,
                    container, false);

            forthStepButton = (ImageButton) newWarningDescriptionView.findViewById(
                    R.id.new_warning_goto_4_step_button);

            forthStepButton.setOnClickListener(this);

            return newWarningDescriptionView;
        }
    }
}
