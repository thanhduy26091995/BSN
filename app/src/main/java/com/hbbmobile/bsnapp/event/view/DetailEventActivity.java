package com.hbbmobile.bsnapp.event.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.event.model.Event;
import com.hbbmobile.bsnapp.event.model.EventResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_UID = "id_event";
    private String idEvent = "";
    private TextView txtTitle, txtTime, txtAddress, txtJoined, txtInfo;
    private ImageView imgPoster, imgLogo, imgDetail;
    private LinearLayout linearBack;
    private Button btnJoin;
    private static final int REQUEST_CODE_PHONE_CALL = 1001;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        initViews();
        //get id intent
        idEvent = getIntent().getStringExtra(EXTRA_UID);
        loadData(idEvent);
    }


    private void initViews() {
        txtTitle = (TextView) findViewById(R.id.txt_detail_event_title);
        txtTime = (TextView) findViewById(R.id.txt_detail_event_time);
        txtAddress = (TextView) findViewById(R.id.txt_detail_event_address);
        txtJoined = (TextView) findViewById(R.id.txt_detail_event_number);
        txtInfo = (TextView) findViewById(R.id.txt_detail_event_info);
        //imageview
        imgPoster = (ImageView) findViewById(R.id.img_detail_event_poster);
        imgLogo = (ImageView) findViewById(R.id.img_detail_event_logo);
        imgDetail = (ImageView) findViewById(R.id.img_detail_event_imgdetail);
        //button
        linearBack = (LinearLayout) findViewById(R.id.linear_img_back);
        btnJoin = (Button) findViewById(R.id.btnjoin);
        //veent click
        linearBack.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
    }

    private void loadData(String idEvent) {
        showProgessDialog();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EventResponse> eventResponseCall = apiService.getEventDetail(idEvent);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Event event = response.body().getListEvent().get(0);
                //load data
                txtTitle.setText(event.getTitle());
                txtInfo.setText(event.getInfo());
                txtJoined.setText(String.format("%s people joined", event.getNumberChoice()));
                txtAddress.setText(event.getAddress());
                txtTime.setText(String.format("%s | %s", event.getDate(), event.getTime()));
                //load image
                Glide.with(DetailEventActivity.this)
                        .load(event.getPoster())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .into(imgPoster);
//                Log.d("LOGO", event.getLogoEvent());
                Glide.with(DetailEventActivity.this)
                        .load(event.getLogoEvent())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .into(imgLogo);
                Glide.with(DetailEventActivity.this)
                        .load(event.getImageDetail())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .into(imgDetail);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.cannotLoadDetailEvent), DetailEventActivity.this);
                hideProgressDialog();
            }
        });
    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_PHONE_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    phoneCall();
                }
                break;
        }

    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.PHONE_BSN));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);

    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_PHONE_CALL,
                    REQUEST_CODE_PHONE_CALL
            );

            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == linearBack) {
            finish();
        } else if (view == btnJoin) {
            showPopup();
        }
    }

    private void showPopup() {
        final Dialog dialog = new Dialog(this);
        // khởi tạo dialog
        dialog.setContentView(R.layout.custom_dialog_join);

        TextView txtCall = (TextView) dialog.findViewById(R.id.txt_call);
        TextView txtEmail = (TextView) dialog.findViewById(R.id.txt_email);
        txtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions()) {
                    phoneCall();
                }
                dialog.dismiss();
            }
        });
        // khai báo control trong dialog để bắt sự kiện
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                dialog.dismiss();
            }
        });
        // bắt sự kiện cho nút đăng kí
        dialog.show();
    }

    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Constants.EMAIL_BSN, null));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
