package com.hbbmobile.bsnapp.profile_user.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.MainActivity;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.login.model.GoogleAuthController;
import com.hbbmobile.bsnapp.login.view.LoginActivity;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.profile_user.presenter.MyProfilePresenter;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.EncodeImage;
import com.hbbmobile.bsnapp.utils.SessionManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView title, txtAddress, txtJob;
    private Button btnlogout;
    private ImageView imgAvatar, imgBack, imgMenu, imgEdit, imgSave, imgChooseImage;
    private EditText edtUsername, edtNickName, edtGender, edtTitle, edtCompanyName, edtCompanyAddress, edtTelephone, edtEmail, edtDate, edtAboutYou;
    private MyProfilePresenter presenter;
    private com.hbbmobile.bsnapp.model.User user;
    private SessionManager sessionManager;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String encoded_string = "";
    private boolean isChooseImage = false;
    public static final String TAG = "MyProfileActivity";
    private LinearLayout linearBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        FacebookSdk.sdkInitialize(this);
//        toolbar = (Toolbar) findViewById(R.id.toolbarback);
//        title = (TextView) toolbar.findViewById(R.id.titletoolbar);
//        title.setText("Profile");
//        imgBack = (ImageView) toolbar.findViewById(R.id.imgback);
//        imgMenu = (ImageView) toolbar.findViewById(R.id.img_menu);
//        imgMenu.setImageResource(R.drawable.icon_create);

        presenter = new MyProfilePresenter(this);
        sessionManager = new SessionManager(this);

        initViews();

//        user = (com.hbbmobile.bsnapp.model.User) getIntent().getSerializableExtra("User");
//        if (user.getAvatar().length() != 0) {
//            Glide.with(this).load(user.getAvatar())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .error(R.drawable.avatar)
//                    .centerCrop().into(imgavatar);
//        }
        //loadData(user);
        loadData();
        //event click
        btnlogout.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgSave.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
        linearBack.setOnClickListener(this);
//        imgChooseImage.setOnClickListener(this);
    }


    public void showToast(String mess) {
        Toast.makeText(MyProfileActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        //load dialog
        showProgessDialog();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        apiService.getUser(getUid()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                String error = response.body().getError();
                if (error.equals("true")) {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.cannotLoadUser), MyProfileActivity.this);
                } else {
                    User user = response.body().getListUser().get(0);
                    edtUsername.setText(user.getFullName());
                    edtNickName.setText(user.getNickName());
                    edtGender.setText(user.getGender());
                    edtTitle.setText(user.getTitle());
                    edtCompanyName.setText(user.getCompanyName());
                    edtCompanyAddress.setText(user.getCompanyAddress());
                    edtTelephone.setText(user.getPhone());
                    edtEmail.setText(user.getEmail());
                    edtDate.setText(user.getDateOfBirth());
                    edtAboutYou.setText(user.getAboutYou());

                    //load avatar
                    Glide.with(MyProfileActivity.this)
                            .load(user.getAvatar())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.avatar)
                            .placeholder(R.drawable.avatar)
                            .centerCrop()
                            .into(imgAvatar);
                    Log.d(TAG, user.getAvatar());
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                hideProgressDialog();
                ShowAlertDialog.showAlert(getResources().getString(R.string.cannotLoadUser), MyProfileActivity.this);

            }
        });
    }

    private void initViews() {
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtNickName = (EditText) findViewById(R.id.edtNickName);
        edtGender = (EditText) findViewById(R.id.edtGender);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtCompanyName = (EditText) findViewById(R.id.edtCompanyName);
        edtCompanyAddress = (EditText) findViewById(R.id.edtCompanyAddress);
        edtTelephone = (EditText) findViewById(R.id.edtPhoneNumber);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtDate = (EditText) findViewById(R.id.edtDateOfBirth);
        edtAboutYou = (EditText) findViewById(R.id.edtAboutYou);

        //button
        imgEdit = (ImageView) findViewById(R.id.img_edit);
        imgSave = (ImageView) findViewById(R.id.img_save);
        imgBack = (ImageView) findViewById(R.id.imgback);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
//        imgChooseImage = (ImageView) findViewById(R.id.imgChooseImage);
        btnlogout = (Button) findViewById(R.id.btnLogout);

        linearBack = (LinearLayout) findViewById(R.id.linear_img_back);
    }

    private void controlViews(boolean flag) {
        edtDate.setEnabled(flag);
        edtTelephone.setEnabled(flag);
        edtNickName.setEnabled(flag);
        edtUsername.setEnabled(flag);
        edtAboutYou.setEnabled(flag);
        edtCompanyAddress.setEnabled(flag);
        edtCompanyName.setEnabled(flag);
        edtTitle.setEnabled(flag);
        edtGender.setEnabled(flag);
    }


    @Override
    public void onClick(View view) {
        if (view == btnlogout) {
            logOut();
        } else if (view == imgBack) {
            finish();
        } else if (view == imgEdit) {
            imgEdit.setVisibility(View.GONE);
            imgSave.setVisibility(View.VISIBLE);
            //enable edittext
            controlViews(true);
        } else if (view == imgSave) {
            imgEdit.setVisibility(View.VISIBLE);
            imgSave.setVisibility(View.GONE);
            //disable edittext
            controlViews(false);
            //update data
            String userName = edtUsername.getText().toString();
            String nickName = edtNickName.getText().toString();
            String gender = edtGender.getText().toString();
            String title = edtTitle.getText().toString();
            String companyName = edtCompanyName.getText().toString();
            String companyAddress = edtCompanyAddress.getText().toString();
            String phone = edtTelephone.getText().toString();
            String email = edtEmail.getText().toString();
            String dateOfBirth = edtDate.getText().toString();
            String aboutYou = edtAboutYou.getText().toString();

            //update
            if (!isChooseImage) {
                presenter.updateUserWithoutAvatar(userName, nickName, phone, dateOfBirth, gender, companyName, companyAddress,
                        "", title, aboutYou, getUid());
            } else {
                Log.d(TAG, encoded_string);
                presenter.updateUserInfo(userName, nickName, phone, dateOfBirth, gender, companyName, companyAddress,
                        "", title, aboutYou, encoded_string, getUid());
            }
        } else if (view == imgAvatar) {
            if (imgSave.getVisibility() == View.VISIBLE) {
                if (verifyStoragePermissions()) {
                    openGallery();
                }
            }
        } else if (view == linearBack) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            //load ảnh lên
            Glide.with(MyProfileActivity.this)
                    .load(data.getData().toString())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgAvatar);
            String filePath = getRealPathFromURI(data.getData());
            byte[] bitmapImage = EncodeImage.encodeImage(filePath);
            encoded_string = Base64.encodeToString(bitmapImage, Base64.DEFAULT);
            // set cờ
            isChooseImage = true;
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //updateInfo();
                    openGallery();
                }
                break;
        }

    }

    private void openGallery() {
        Intent myIntent = new Intent(Intent.ACTION_PICK);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, Constants.GALLERY_INTENT);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        MainActivity.instanceMainActivity.finish();
        LoginManager.getInstance().logOut();
        if (GoogleAuthController.getInstance().getGoogleApiClient() != null) {
            GoogleAuthController.getInstance().signOut(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    if (GoogleAuthController.getInstance().getGoogleApiClient().isConnected()) {
                        Auth.GoogleSignInApi.signOut(GoogleAuthController.getInstance().getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Intent myIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.d("Error", "GoogleSubmitter API Client Connection Suspended");
                }
            });
        }
        //restart session
        sessionManager.logoutUser();
    }
}
