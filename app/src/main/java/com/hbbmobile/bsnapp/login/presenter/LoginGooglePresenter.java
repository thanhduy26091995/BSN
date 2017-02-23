package com.hbbmobile.bsnapp.login.presenter;

import com.hbbmobile.bsnapp.login.model.GoogleAuthController;
import com.hbbmobile.bsnapp.login.view.LoginActivity;

/**
 * Created by buivu on 28/11/2016.
 */
public class LoginGooglePresenter {
    private LoginActivity view;
    private final String TAG = "LoginGooglePresenter";

    public LoginGooglePresenter(LoginActivity view) {
        this.view = view;
    }

    public void login() {
        GoogleAuthController.getInstance().signIn(view);
    }
}
