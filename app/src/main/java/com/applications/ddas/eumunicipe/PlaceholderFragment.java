package com.applications.ddas.eumunicipe;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dsilva on 05-05-2015.
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {
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
                fragment = new NewWarningFinalCheckFragment();
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
