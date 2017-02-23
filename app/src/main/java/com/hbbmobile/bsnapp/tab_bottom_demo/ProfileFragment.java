package com.hbbmobile.bsnapp.tab_bottom_demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 14/11/2016.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        return rootView;
    }
}
