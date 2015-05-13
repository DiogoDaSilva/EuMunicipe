package com.applications.ddas.eumunicipe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dsilva on 13-05-2015.
 */
public class NewWarningFinalCheckFragment extends PlaceholderFragment
        implements OnMapReadyCallback {

    private View newWarningFinalCheckView;
    private ImageView newWarningPhoto;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private EditText newWarningFinalCheckEditEmail;
    private EditText newWarningFinalCheckEditDescription;
    private Button sendButton;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();

        newWarningFinalCheckView = inflater.inflate(R.layout.fragment_new_warning_final_check,
                container, false);

        newWarningPhoto = (ImageView) newWarningFinalCheckView.findViewById(
                R.id.final_check_photo);

        newWarningFinalCheckEditEmail = (EditText) newWarningFinalCheckView.findViewById(
                R.id.new_warning_final_check_edit_email);

        newWarningFinalCheckEditDescription = (EditText) newWarningFinalCheckView.findViewById(
                R.id.new_warning_final_check_edit_description);

        sendButton = (Button) newWarningFinalCheckView.findViewById(
                R.id.send_email
        );

        //On view create the image does not have yet its dimensions
        ViewTreeObserver vto = newWarningPhoto.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                newWarningPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                initializePhoto();
                return true;
            }
        });

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        newWarningFinalCheckEditEmail.setText(mainActivity.municipalityEmail);

        newWarningFinalCheckEditDescription.setText(mainActivity.description);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "diogothesilva@gmail.com";
                String subject = getString(R.string.email_subject);
                String message = getString(R.string.email_message);

                if (mainActivity.description == null) {
                    mainActivity.description = "";
                }
                message = message.replace("$0", mainActivity.description);

                message = message.replace("$1", mainActivity.myLocation.toString());

                message = message.replace("$2", mainActivity.myLocation.latitude + "," +
                        mainActivity.myLocation.longitude);

                message += "\nTeste: " + mainActivity.municipalityEmail;


                final Intent emailIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { email });
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        subject);
                Uri file = Uri.parse("file://" + mainActivity.currentPhotoPath);
                emailIntent.putExtra(Intent.EXTRA_STREAM, file);

                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                mainActivity.startActivity(Intent.createChooser(emailIntent,
                        "Sending email..."));

                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new HomeFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, getArguments().getInt(ARG_SECTION_NUMBER));
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        });

        return newWarningFinalCheckView;
    }

    private void initializePhoto() {
        // Get the dimensions of the View
        int targetW = newWarningPhoto.getWidth();
        int targetH = newWarningPhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        BitmapFactory.decodeFile(mainActivity.currentPhotoPath, bmOptions);
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mainActivity.currentPhotoPath, bmOptions);
        newWarningPhoto.setImageBitmap(bitmap);
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
    public void onMapReady(GoogleMap googleMap) {
        Log.d("DebugMap", "Entrei");
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        animateMap(mainActivity.myLocation);
    }

    public void animateMap(LatLng myLocation) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }
}
