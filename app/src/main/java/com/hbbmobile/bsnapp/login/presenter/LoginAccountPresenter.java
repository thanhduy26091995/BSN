package com.hbbmobile.bsnapp.login.presenter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.login.view.LoginActivity;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.SessionManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 19/12/2016.
 */
public class LoginAccountPresenter {
    private LoginActivity view;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    public LoginAccountPresenter(LoginActivity view, FirebaseAuth mAuth) {
        this.view = view;
        this.mAuth = mAuth;
        sessionManager = new SessionManager(view);
    }

    public void loginWithAccount(String userName, String password) {
//        AsyncForLogin asyncForLogin = new AsyncForLogin(userName, password);
//        asyncForLogin.execute();
    }

    public void signIn(String email, String password) {
        try {
            view.showProgessDialog();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   // Log.d(view.TAG, "signIn:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        AsyncForLogin asyncForLogin = new AsyncForLogin(task.getResult().getUser().getUid());
                        asyncForLogin.execute();

                    } else {
                        ShowAlertDialog.showAlert(view.getResources().getString(R.string.dangNhapKhongThanhCong), view);
                        //view.showToast(view.getResources().getString(R.string.dangNhapKhongThanhCong));
                    }
                    view.hideProgressDialog();
                }
            });
        } catch (Exception e) {
            Log.d(view.TAG, e.getMessage());
        }
    }

    private class AsyncForLogin extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String uid;

        public AsyncForLogin(String uid) {
            this.uid = uid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(view);
//            progressDialog.setMessage("Đang tải...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserResponse> call = apiService.getUser(uid);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    String error = response.body().getError();
                    if (error.equals("false")) {
                        User user = response.body().getListUser().get(0);
                        sessionManager.createLoginSession(uid, user.getFullName(), user.getNickName(), user.getPhone(),
                                user.getDateOfBirth(), user.getEmail(), user.getGender(), user.getCompanyName(),
                                user.getCompanyAddress(), user.getTitle(), user.getWebsite(), user.getAboutYou(),
                                user.getAvatar());
                    } else {
                        String message = response.body().getMessage();
                        view.showAlert(message);
                    }
                    view.moveToMainActivity();
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                   // view.showAlert(t.getMessage());
                    view.moveToMainActivity();
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.cannotLoadUser), view);
                }

            });
            //move main activity

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
        }
    }
}
