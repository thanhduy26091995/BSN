package com.hbbmobile.bsnapp.create_an_account.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.create_an_account.model.CreateAnAccountSubmitter;
import com.hbbmobile.bsnapp.create_an_account.view.CreateAnAccountActivity;
import com.hbbmobile.bsnapp.login.view.LoginActivity;
import com.hbbmobile.bsnapp.model.RegisterResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.SessionManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 21/12/2016.
 */
public class CreateAnAccountPresenter {
    private CreateAnAccountActivity view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private CreateAnAccountSubmitter submitter;
    private SessionManager sessionManager;

    public CreateAnAccountPresenter(CreateAnAccountActivity view, FirebaseAuth mAuth) {
        this.view = view;
        this.mAuth = mAuth;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new CreateAnAccountSubmitter(mDatabase);
        sessionManager = new SessionManager(view);
    }

    public void createAccountUseFirebase(final String userName, final String email, String password, final String phoneNumber, final String encoded_string) {
        view.showProgessDialog();
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                          @Override
                                          public void onComplete(@NonNull final Task<AuthResult> task) {
                                              // Log.d(view.TAG, "createUser:onComplete:" + task.isSuccessful());
                                              if (!task.isSuccessful()) {
                                                  //view.showToast(task.getException().getMessage());
                                                  ShowAlertDialog.showAlert(task.getException().getMessage(), view);
                                              } else {

                                                  //saving data session
                                                  sessionManager.createRegisterSession(task.getResult().getUser().getUid(), userName, email, phoneNumber);
                                                  //saving data in firebase
                                                  submitter.addUser(task.getResult().getUser().getUid(), userName, phoneNumber, "", (long) 0);

                                                  onAuthSuccess(task.getResult().getUser(), userName, email, phoneNumber, encoded_string);

                                              }

                                              view.hideProgressDialog();
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               ShowAlertDialog.showAlert(e.getMessage(), view);
                                               // Log.d(view.TAG, e.getMessage());
                                               //view.showToast("Email đã trùng hoặc bị sai! Vui lòng thử lại");
                                           }
                                       }

        );
    }

    //đăng nhập thành công thì lưu user và chuyển tới MainActivity
    private void onAuthSuccess(final FirebaseUser user, final String userName, String email,
                               String phoneNumber, String encoded_string) {
        try {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<RegisterResponse> call = apiService.register(user.getUid().toString(), userName, phoneNumber, email, encoded_string);
            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    RegisterResponse registerResponse = response.body();
                    String error = registerResponse.getError();
                    String message = registerResponse.getMessage();
                    if (error.equals("false")) {

                        //ShowAlertDialog.showAlert(view.getResources().getString(R.string.registerSuccess), view);
                        // Toast.makeText(view, "Success", Toast.LENGTH_SHORT).show();
                        view.finish();
                    } else {
                        if (message.equals("email existing")) {
                            ShowAlertDialog.showAlert(view.getResources().getString(R.string.emailExists), view);
                            // Toast.makeText(view, "Email đã tồn tại! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        } else if (message.equals("missing parameter")) {
                            //Toast.makeText(view, "Hãy điền đẩy đủ thông tin!", Toast.LENGTH_SHORT).show();
                            ShowAlertDialog.showAlert(view.getResources().getString(R.string.fillAllData), view);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    // Toast.makeText(view, "Đăng Ký Thất Bại!\n" + t.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                    ShowAlertDialog.showAlert(view.getResources().getString(R.string.registerFail), view);
                }
            });

            //view.finish();
            view.moveToTabActivity();
            LoginActivity.activity.finish();
        } catch (Exception e) {
            Log.d(view.TAG, e.getMessage());
        }
    }

}

