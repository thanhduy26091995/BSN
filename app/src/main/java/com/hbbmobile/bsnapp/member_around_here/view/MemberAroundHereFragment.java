package com.hbbmobile.bsnapp.member_around_here.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.member_around_here.MemberAroundHereAdapter;
import com.hbbmobile.bsnapp.model.GPS;
import com.hbbmobile.bsnapp.model.GPSResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class MemberAroundHereFragment extends Fragment implements LocationListener, View.OnClickListener {
    // private LatLng location;
    private List<GPS> listGPS;
    private ListView lvMember;
    private List<String> listData;
    private ArrayAdapter adapter;
    public static final int REQUEST_ID_ACCESS_COARSE_FINE_LOCATION = 101;
    private LatLng latLng;
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private MemberAroundHereAdapter customAdapter;
    private List<GPS> listGps;
    private RecyclerView mRecycler;
    private LinearLayout linearReload;
    private Button btnReload;
    private LocationManager locationManager;
    private ProgressDialog mProgressDialog;
    private boolean isReload = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_member__around__here, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_member_around);
        linearReload = (LinearLayout) rootView.findViewById(R.id.linear_reload_member);
        btnReload = (Button) rootView.findViewById(R.id.btn_reload_member_around);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkLocationPermission()) {
                    getLocation();
                }
            }

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingLocationAlert();
            } else {
                mRecycler.setVisibility(View.VISIBLE);
                linearReload.setVisibility(View.GONE);
            }
        } else {
            ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
            linearReload.setVisibility(View.GONE);
        }


        //event click
        btnReload.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == btnReload) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingLocationAlert();
            } else {

                showProgessDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        hideProgressDialog();
                        getLocation();
                        if (location != null) {
                            mRecycler.setVisibility(View.VISIBLE);
                            linearReload.setVisibility(View.GONE);

                        }
                    }
                }, 5000);
            }
        }
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }

    public void getAllUserFromGPS(LatLng latLng) {
        showProgessDialog();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GPSResponse> call = apiInterface.getMemberAround(BaseActivity.getUid(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        call.enqueue(new Callback<GPSResponse>() {
            @Override
            public void onResponse(Call<GPSResponse> call, Response<GPSResponse> response) {
                String error = response.body().getError();
                if (error.equals("false")) {
                    Log.d("SIZE", "" + response.body().getListGPS().size());
                    customAdapter = new MemberAroundHereAdapter(response.body().getListGPS(), getActivity());
                    customAdapter.notifyDataSetChanged();
                    mRecycler.setAdapter(customAdapter);

                } else {
                    String message = response.body().getMessage();
                    Log.d("ERROR", message);
                }

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<GPSResponse> call, Throwable t) {
                ShowAlertDialog.showAlert("Cannot load member around here! Please try it again", getActivity());
                //   Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });
    }

    private void showSettingLocationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle(getResources().getString(R.string.GPSTitle));
        //set message
        builder.setMessage(getResources().getString(R.string.GPSContent));
        //on press
        builder.setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingIntent);
            }
        });
        //on cancel
        builder.setNegativeButton(getResources().getString(R.string.huy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_ACCESS_COARSE_FINE_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_ACCESS_COARSE_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    public void getLocation() {
        try {

            LocationManager locationManager;
            locationManager = (LocationManager) getActivity()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return TODO;
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null) {

            Log.d("LATLNG", "" + location.getLatitude() + "/" + location.getLongitude());
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            getAllUserFromGPS(latLng);
            // Toast.makeText(this, String.valueOf(latLng.latitude), Toast.LENGTH_LONG).show();
        } else {

            //   Toast.makeText(getActivity(), "Location not found!", Toast.LENGTH_LONG).show();

        }
        // return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_ID_ACCESS_COARSE_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;
        }

    }

}
