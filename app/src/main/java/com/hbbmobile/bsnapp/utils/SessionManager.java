package com.hbbmobile.bsnapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.login.view.LoginActivity;

import java.util.HashMap;

/**
 * Created by buivu on 26/12/2016.
 */
public class SessionManager {
    //use share reference
    private SharedPreferences pref;
    //editor for share reference
    private SharedPreferences.Editor editor;
    private Context context;
    //Shared pref mode
    private int PRIVATE_MODE = 0;
    //shared Ref file name
    private static final String PREF_NAME = "BSN";
    //All shared pref keys
    private static final String IS_LOGIN = "isLogined";
    //key
    public static final String KEY_IDFB = "idfb";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_BIRTH = "birth";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_COMPANY_NAME = "companyname";
    public static final String KEY_COMPANY_ADDRESS = "companyaddress";
    public static final String KEY_TITLE = "title";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_ABOUT_YOU = "aboutyou";
    public static final String KEY_AVATAR = "avatar";

    public static final String KEY_LANGUAGE = "language";
    //constructor


    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //create language session
    public void createLanguageSession(String language) {
        editor.putString(KEY_LANGUAGE, language);
        //commit
        editor.commit();
    }

    public void createRegisterSession(String idfb, String fullName, String email, String phone) {
        editor.putBoolean(IS_LOGIN, true);
        //storing data
        editor.putString(KEY_IDFB, idfb);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }

    //create login session
    public void createLoginSession(String idfb, String fullName, String nickName, String phone,
                                   String birth, String email, String gender, String companyName, String companyAddress,
                                   String title, String website, String aboutYou, String avatar) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        //storing data
        editor.putString(KEY_IDFB, idfb);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_NICKNAME, nickName);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_BIRTH, birth);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_COMPANY_NAME, companyName);
        editor.putString(KEY_COMPANY_ADDRESS, companyAddress);
        editor.putString(KEY_TITLE, title);
        editor.putString(KEY_WEBSITE, website);
        editor.putString(KEY_ABOUT_YOU, aboutYou);
        editor.putString(KEY_AVATAR, avatar);
        //commit changes
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    //check login
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            //
            Intent myIntent = new Intent(context, LoginActivity.class);
            //closing all activities
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //add flag for new activity
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }

    //get stored session data
    public HashMap<String, String> getUserData() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_IDFB, pref.getString(KEY_IDFB, ""));
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, ""));
        user.put(KEY_NICKNAME, pref.getString(KEY_NICKNAME, ""));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, ""));
        user.put(KEY_BIRTH, pref.getString(KEY_BIRTH, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, ""));
        user.put(KEY_COMPANY_NAME, pref.getString(KEY_COMPANY_NAME, ""));
        user.put(KEY_COMPANY_ADDRESS, pref.getString(KEY_COMPANY_ADDRESS, ""));
        user.put(KEY_TITLE, pref.getString(KEY_TITLE, ""));
        user.put(KEY_WEBSITE, pref.getString(KEY_WEBSITE, ""));
        user.put(KEY_ABOUT_YOU, pref.getString(KEY_ABOUT_YOU, ""));
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, ""));
        return user;
    }

    //get language session
    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, context.getResources().getString(R.string.defaultLanguage));
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(context, LoginActivity.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        // Staring Login Activity
//        context.startActivity(i);
    }

}
