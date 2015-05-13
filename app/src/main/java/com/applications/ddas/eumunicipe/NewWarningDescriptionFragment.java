package com.applications.ddas.eumunicipe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by dsilva on 13-05-2015.
 */
public class NewWarningDescriptionFragment extends PlaceholderFragment {
    private View newWarningDescriptionView;
    private EditText newWarningDescriptionEdit;
    private ImageButton forthStepButton;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity)getActivity();

        newWarningDescriptionView = inflater.inflate(R.layout.fragment_new_warning_description,
                container, false);

        newWarningDescriptionEdit = (EditText) newWarningDescriptionView.findViewById(
                R.id.new_warning_description_edit
        );

        newWarningDescriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mainActivity.description = newWarningDescriptionEdit.getText().toString();
            }
        });

        forthStepButton = (ImageButton) newWarningDescriptionView.findViewById(
                R.id.new_warning_goto_4_step_button);

        forthStepButton.setOnClickListener(this);

        return newWarningDescriptionView;
    }
}

