package com.hbbmobile.bsnapp.login.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.MainActivity;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.create_an_account.view.CreateAnAccountActivity;
import com.hbbmobile.bsnapp.forget_password.view.ForgotPasswordActivity;
import com.hbbmobile.bsnapp.login.model.GoogleAuthController;
import com.hbbmobile.bsnapp.login.presenter.LoginAccountPresenter;
import com.hbbmobile.bsnapp.login.presenter.LoginFacebookPresenter;
import com.hbbmobile.bsnapp.login.presenter.LoginGooglePresenter;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.EmailValidate;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private TextView tvForgotPassword, tvCreateAccount;
    private Button btnLogin;
    private ImageView imgfb, imggm;
    private EditText edtpass, edtUsername;

    private LoginGooglePresenter googlePresenter;
    private LoginFacebookPresenter facebookPresenter;
    private LoginAccountPresenter accountPresenter;
    public static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private ProgressDialog mProgressDialog;
    public static Activity activity;
    private FirebaseAuth mAuth;
    private TextView txtBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        //init presenter
        callbackManager = CallbackManager.Factory.create();
        googlePresenter = new LoginGooglePresenter(this);
        facebookPresenter = new LoginFacebookPresenter(this);
        accountPresenter = new LoginAccountPresenter(this, mAuth);
        doFormWidget();


    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            loginWithAccount();
        } else if (view == tvForgotPassword) {
            forgotPassword();
        } else if (view == tvCreateAccount) {
            createAccount();
        } else if (view == imgfb) {
            facebookPresenter.setUpFacebook(LoginActivity.this, callbackManager);
        } else if (view == imggm) {
            googlePresenter.login();
        } else if (view == txtBack) {
            finish();
        }
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

    private void createAccount() {
        Intent intent2 = new Intent(LoginActivity.this, CreateAnAccountActivity.class);
        startActivity(intent2);
        overridePendingTransition(R.anim.comming_in_right, R.anim.comming_out_right);
    }

    private void forgotPassword() {
        Intent intent1 = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.comming_in_right, R.anim.comming_out_right);
    }

    public void showAlert(String mess) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        dialog.setContentView(R.layout.customdialogpass);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.tv);
        text.setText(mess);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loginWithAccount() {
        String username = edtUsername.getText().toString().trim();
        String password = edtpass.getText().toString().trim();

        if (username.length() == 0 || password.length() == 0) {
            ShowAlertDialog.showAlert(getResources().getString(R.string.fillUsernamePassword), LoginActivity.this);

        } else {
            if (EmailValidate.IsOk(username)) {
                accountPresenter.signIn(username, password);
            } else {
                ShowAlertDialog.showAlert(getResources().getString(R.string.wrongFormatEmail), LoginActivity.this);
            }
        }
    }

    //chuyển tới MainActivity
    public void moveToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        if (MainActivity.instanceMainActivity != null) {
            MainActivity.instanceMainActivity.finish();
        }
    }

    //show thông báo cho người dùng
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
//                GoogleSignInAccount account = result.getSignInAccount();
//                Log.d(TAG, account.getDisplayName());
//                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
//                //User user = new User(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
//                /*myIntent.putExtra(TempActivity.DISPLAY_NAME, account.getDisplayName());
//                myIntent.putExtra(TempActivity.EMAIL, account.getEmail());
//                myIntent.putExtra(TempActivity.PHOTO_URL, account.getPhotoUrl().toString());*/
//                com.hbbmobile.bsnapp.model.User user = new com.hbbmobile.bsnapp.model.User("", account.getDisplayName(),
//                        "", "", "", "", account.getPhotoUrl().toString());
//                myIntent.putExtra("User", user);
//                startActivity(myIntent);
//                finish();
            } else {
                Log.d(TAG, "fail");
            }
        }
        if (requestCode != Constants.RC_SIGN_IN && resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doFormWidget() {
        tvCreateAccount = (TextView) findViewById(R.id.act_main_tvCreateAccount);
        tvForgotPassword = (TextView) findViewById(R.id.act_main_tvForgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        imgfb = (ImageView) findViewById(R.id.btnfb);
        imggm = (ImageView) findViewById(R.id.btngm);
        edtpass = (EditText) findViewById(R.id.edtpass);
        edtUsername = (EditText) findViewById(R.id.edt_user_name);
        txtBack = (TextView) findViewById(R.id.txtBack);
        //event click
        btnLogin.setOnClickListener(this);
        tvCreateAccount.setOnClickListener(this);
        txtBack.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        imggm.setOnClickListener(this);
        imgfb.setOnClickListener(this);
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Đang tải...");
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
}
