package com.hbbmobile.bsnapp;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hbbmobile.bsnapp.model.MyMarker;

import java.util.HashMap;

/**
 * Created by buivu on 26/12/2016.
 */
public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    private HashMap<Marker, MyMarker> myMarkerHashMap;

    public MarkerInfoWindowAdapter(Activity context, HashMap<Marker, MyMarker> myMarkerHashMap) {
        this.context = context;
        this.myMarkerHashMap = myMarkerHashMap;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = context.getLayoutInflater().inflate(R.layout.custom_marker_layout_v2, null);
        TextView txtPhone = (TextView) v.findViewById(R.id.txtPhone);
        TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
        TextView txtJob = (TextView) v.findViewById(R.id.txtJob);

        MyMarker myMarker = myMarkerHashMap.get(marker);
        txtPhone.setText(myMarker.getPhone());
        txtJob.setText(myMarker.getJob());
        txtAddress.setText(myMarker.getAddress());

        return v;
    }
}
