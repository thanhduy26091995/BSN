package com.hbbmobile.bsnapp.base.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.utils.SessionManager;

import java.util.Locale;

/**
 * Created by buivu on 16/09/2016.
 */
public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;
    private Locale mCurrentLocale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mCurrentLocale = getResources().getConfiguration().locale;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Locale locale = getLocale(this);
//
//        if (!locale.equals(mCurrentLocale)) {
//
//            mCurrentLocale = locale;
//            recreate();
//        }
    }



    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getResources().getString(R.string.loading));
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
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static Locale getLocale(Context context) {
        SessionManager sessionManager = new SessionManager(context);

        String lang = sessionManager.getLanguage();
        switch (lang) {
            case "Tiếng Việt":
                lang = "vi";
                break;
            case "English":
                lang = "en";
                break;
        }
        return new Locale(lang);
    }

}