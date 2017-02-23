package com.hbbmobile.bsnapp.create_an_account.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.MainActivity;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.create_an_account.presenter.CreateAnAccountPresenter;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.EmailValidate;
import com.hbbmobile.bsnapp.utils.EncodeImage;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

/**
 * Created by buivu on 10/11/2016.
 */
public class CreateAnAccountActivity extends BaseActivity implements View.OnClickListener {
    private Button btnSignUp;
    private TextView txtAcceptTerm;
    private Button btncancel, btnargee;
    private Toolbar toolbar;
    private TextView title;
    private CheckBox checkBox;
    private EditText edtUsername, edtPassword, edtConfirmPass, edtMail, edtPhone;
    private ImageView imgAvatar;
    private CreateAnAccountPresenter presenter;
    public static final String TAG = "CreateAnAccountActivity";
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String encoded_string = "";
    private boolean isChooseImage = false;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_account);
        initViews();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(getResources().getString(R.string.createAnAccount));
        setSupportActionBar(toolbar);

        //Không hiện tiêu đề
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Hiện nút back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        txtAcceptTerm = (TextView) findViewById(R.id.txt_accept_term);
        checkBox = (CheckBox) findViewById(R.id.check_term);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtMail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtConfirmPass = (EditText) findViewById(R.id.edtConfirmPassword);
        imgAvatar = (ImageView) findViewById(R.id.img_create_avatar);
        mAuth = FirebaseAuth.getInstance();
        presenter = new CreateAnAccountPresenter(this, mAuth);
        //event click
        btnSignUp.setOnClickListener(this);
        txtAcceptTerm.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignUp) {
            registerAccount();
        } else if (view == txtAcceptTerm) {
            showAcceptTerm();
        } else if (view == imgAvatar) {
            if (verifyStoragePermissions()) {
                openGallery();
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.comming_in_left, R.anim.comming_out_left);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            //load ảnh lên
            Glide.with(CreateAnAccountActivity.this)
                    .load(data.getData().toString())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgAvatar);
            String filePath = getRealPathFromURI(data.getData());
            byte[] bitmapImage = EncodeImage.encodeImage(filePath);
            encoded_string = Base64.encodeToString(bitmapImage, Base64.DEFAULT);
            //set cờ
            isChooseImage = true;
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void showAcceptTerm() {
        /*final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAnAccountActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(v);
        builder.create().show();
        btnargee= (Button) v.findViewById(R.id.btnok);
        builder.setCancelable(true);
        btnargee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(true);


            }
        });*/

        final Dialog dialog = new Dialog(CreateAnAccountActivity.this);
        // khởi tạo dialog
        dialog.setContentView(R.layout.custom_alert_dialog);
        // xét layout cho dialog
        dialog.setTitle("Đăng kí");
        // xét tiêu đề cho dialog

        Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
        Button dialogcancel = (Button) dialog.findViewById(R.id.btncancel);
        dialogcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // khai báo control trong dialog để bắt sự kiện
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(true);
                dialog.dismiss();
            }
        });
        // bắt sự kiện cho nút đăng kí
        dialog.show();
        /*AlertDialog.Builder adb=new AlertDialog.Builder(CreateAnAccountActivity.this,R.style.MyAlertDialogStyle);
        adb.setTitle("Terms and conditions");
        adb.setMessage("Vào ngày 19/12 tới, 538 đại cử tri của nước Mỹ sẽ được tập hợp từ 50 tiểu bang và thủ đô Washington DC để bầu ra tổng thống và phó tổng thống theo nguyện vọng của người dân. Kết quả (đã biết trước) sẽ được Phó Tổng thống đương nhiệm Joe Biden “thông báo” trước Thượng viện.");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Argee", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkBox.setChecked(true);
            }});
        adb.show();*/

    }

    private void registerAccount() {
        boolean isOk = true;
        String userName = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPass = edtConfirmPass.getText().toString();
        String email = edtMail.getText().toString();
        String phone = edtPhone.getText().toString();
        if (userName.length() == 0 || password.length() == 0 || email.length() == 0 || phone.length() == 0) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillAllData), CreateAnAccountActivity.this);
        } else {
            if (!EmailValidate.IsOk(email)) {
                isOk = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongFormatEmail), CreateAnAccountActivity.this);
                return;
            }
            if (phone.length() < 10 || phone.length() > 11) {
                isOk = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.phoneLength), CreateAnAccountActivity.this);
                return;
            }
            if (password.length() < 6) {
                isOk = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.passwordLength), CreateAnAccountActivity.this);
                return;
            }
            if (!password.equals(confirmPass)) {
                isOk = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.passwordDontMatch), CreateAnAccountActivity.this);
                return;
            }
            if (!checkBox.isChecked()) {
                isOk = false;
                ShowAlertDialog.showAlert(getResources().getString(R.string.acceptTerms), CreateAnAccountActivity.this);
                return;
            }

            if (isOk) {
                //add database
                presenter.createAccountUseFirebase(userName, email, password, phone, encoded_string);
            }
        }
    }

    //hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //chuyển tới MainActivity
    public void moveToTabActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
        finish();
    }
}
