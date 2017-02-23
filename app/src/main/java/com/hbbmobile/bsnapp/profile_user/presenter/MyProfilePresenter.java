package com.hbbmobile.bsnapp.profile_user.presenter;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.profile_user.model.MyProfileSubmitter;
import com.hbbmobile.bsnapp.profile_user.view.MyProfileActivity;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.SessionManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 29/12/2016.
 */
public class MyProfilePresenter {
    private MyProfileActivity view;
    private SessionManager sessionManager;
    private MyProfileSubmitter submitter;
    private DatabaseReference mDatabase;

    public MyProfilePresenter(MyProfileActivity view) {
        this.view = view;
        sessionManager = new SessionManager(view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new MyProfileSubmitter(mDatabase);
    }

    public void updateProfile(String uid, String name, String avatar, String phone) {
        submitter.updateProfile(uid, name, avatar, phone);
    }

    public void updateUserInfo(final String userName, final String nickName, final String phone, final String birth,
                               final String gender, final String companyName, final String companyAddress, final String website,
                               final String title, final String aboutYou, String encoded_string, final String id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> userResponseCall = apiService.updateUserInfo(userName, nickName, phone, birth, gender,
                companyName, companyAddress, website, title, aboutYou, encoded_string, id);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                String error = response.body().getError();
                if (error.equals("false")) {
                    User user = response.body().getListUser().get(0);
                    //view.showToast("Chỉnh sửa thông tin thành công");
                    //update sesssion
                    sessionManager.createLoginSession(id, userName, nickName, phone, birth, user.getEmail(), gender, companyName,
                            companyAddress, title, website, aboutYou, user.getAvatar());
                    //save datas firebase
                    updateProfile(id, userName, user.getAvatar(), phone);
                    Log.d(view.TAG, user.getAvatar());
                } else {
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.editProfileFail), view);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // view.showToast("Lỗi!" + t.getMessage());
                //  ShowAlertDialog.showAlert(t.getMessage(), view);
                // ShowAlertDialog.showAlert("Cannot edit profile! Please try it again", view);
                ShowAlertDialog.showAlert(view.getResources().getString(R.string.editProfileFail), view);
            }
        });
    }

    public void updateUserWithoutAvatar(final String userName, final String nickName, final String phone, final String birth,
                                        final String gender, final String companyName, final String companyAddress, final String website,
                                        final String title, final String aboutYou, final String id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> userResponseCall = apiService.updateUserWithoutAvatar(userName, nickName, phone, birth, gender,
                companyName, companyAddress, website, title, aboutYou, id);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                String error = response.body().getError();
                if (error.equals("false")) {
                    User user = response.body().getListUser().get(0);
                    // view.showToast("Chỉnh sửa thông tin thành công");
                    //update sesssion
                    sessionManager.createLoginSession(id, userName, nickName, phone, birth, user.getEmail(), gender, companyName,
                            companyAddress, title, website, aboutYou, user.getAvatar());
                    //save datas firebase
                    updateProfile(id, userName, user.getAvatar(), phone);
                    Log.d(view.TAG, user.getAvatar());
                } else {
                    // ShowAlertDialog.showAlert("Cannot edit profile! Please try it again", view);
                    //   view.showToast("Chỉnh sửa thông tin thất bại");
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.editProfileFail), view);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                //view.showToast("Lỗi!" + t.getMessage());
                //Log.d("Error", t.getMessage());
                //ShowAlertDialog.showAlert("Cannot edit profile! Please try it again", view);
                ShowAlertDialog.showAlert(view.getResources().getString(R.string.editProfileFail), view);
            }

        });
    }
}
