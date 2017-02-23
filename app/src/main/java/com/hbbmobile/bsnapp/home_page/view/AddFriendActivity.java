package com.hbbmobile.bsnapp.home_page.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 11/11/2016.
 */
public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtBack;
    private EditText edtPhone;
    private Button btnSearch;
    private static final String TAG = "AddFriendActivity";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initViews();
    }

    private void initViews() {
        txtBack = (TextView) findViewById(R.id.txtBack);
        edtPhone = (EditText) findViewById(R.id.edt_email_or_phone);
        btnSearch = (Button) findViewById(R.id.btn_search);
        //event click
        txtBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == txtBack) {
            finish();
            overridePendingTransition(R.anim.comming_in_left, R.anim.comming_out_left);
        } else if (view == btnSearch) {
            //load progress dialog
            showProgessDialog();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserResponse> call = apiService.searchUser(edtPhone.getText().toString());
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    hideProgressDialog();
                    String error = response.body().getError();
                    if (error.equals("false")) {
                        User user = response.body().getListUser().get(0);
                        Intent myIntent = new Intent(AddFriendActivity.this, FriendProfileActivity.class);
                        myIntent.putExtra("User", user);
                        startActivity(myIntent);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    hideProgressDialog();
                    Log.d(TAG, t.getMessage());
                    // Toast.makeText(AddFriendActivity.this, "Không thể tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    ShowAlertDialog.showAlert(getResources().getString(R.string.cannotFindUser), AddFriendActivity.this);
                }
            });
        }
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
}
