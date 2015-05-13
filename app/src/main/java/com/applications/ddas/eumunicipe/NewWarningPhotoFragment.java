package com.applications.ddas.eumunicipe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dsilva on 13-05-2015.
 */
public class NewWarningPhotoFragment extends PlaceholderFragment {
    static final int REQUEST_TAKE_PHOTO = 1;

    private LayoutInflater inflater;
    private ViewGroup container;
    private View newWarningPhotoView;
    private ImageView photoView;
    private Button takePhotoButton;
    private RelativeLayout secondStepLayout;
    private ImageButton secondStepButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        newWarningPhotoView = inflater.inflate(R.layout.fragment_new_warning_photo, container, false);

        photoView = (ImageView) newWarningPhotoView.findViewById(R.id.new_warning_photo);
        takePhotoButton = (Button) newWarningPhotoView.findViewById(R.id.new_warning_photo_button);
        secondStepLayout = (RelativeLayout) newWarningPhotoView.findViewById(R.id.new_warning_layout_2_step);
        secondStepButton = (ImageButton) newWarningPhotoView.findViewById(R.id.new_warning_goto_2_step_button);

        takePhotoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                Log.d("PHOTO", "Antes do IF");
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        Log.d("PHOTO2", photoFile.toString());
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d("PHOTO_EX", ex.getLocalizedMessage());
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Log.d("PHOTO3", photoFile.toString());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });

        secondStepButton.setOnClickListener(this);

        return newWarningPhotoView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            // Get the dimensions of the View
            int targetW = photoView.getWidth();
            int targetH = photoView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            MainActivity mainActivity = (MainActivity)getActivity();
            BitmapFactory.decodeFile(mainActivity.currentPhotoPath, bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mainActivity.currentPhotoPath, bmOptions);
            photoView.setImageBitmap(bitmap);
            takePhotoButton.setText(R.string.new_warning_take_another_photo);
            secondStepLayout.setVisibility(View.VISIBLE);
        }
    }
}
