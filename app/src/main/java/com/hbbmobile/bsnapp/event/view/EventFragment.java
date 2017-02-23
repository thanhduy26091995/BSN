package com.hbbmobile.bsnapp.event.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.event.presenter.EventPresenter;

public class EventFragment extends Fragment {

    public static final String TAG = "EventFragment";
    private RecyclerView mRecycler;
    private EventPresenter presenter;

    private static final int REQUEST_CODE_PHONE_CALL = 1001;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_event, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_event);
        presenter = new EventPresenter(this, mRecycler);
        verifyStoragePermissions();
        //get data
        presenter.fetchData();
        return rootView;
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_PHONE_CALL,
                    REQUEST_CODE_PHONE_CALL
            );

            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_PHONE_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }

    }
}
