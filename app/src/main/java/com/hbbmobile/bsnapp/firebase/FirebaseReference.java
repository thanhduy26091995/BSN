package com.hbbmobile.bsnapp.firebase;

import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by Doma Umaru on 12/29/2016.
 */

public class FirebaseReference {
    private Firebase mFirebaseRef;
    private String mFirebaseUrl = "https://bsnapp-93c52.firebaseio.com/";

    public FirebaseReference(Context context) {
        Firebase.setAndroidContext(context);
        mFirebaseRef = new Firebase(mFirebaseUrl);
    }

    public FirebaseReference(Context context, String url) {
        Firebase.setAndroidContext(context);
        mFirebaseUrl = url;
        mFirebaseRef = new Firebase(mFirebaseUrl);
    }

    public Firebase getFirebaseRef() {
        return mFirebaseRef;
    }

    public String getFirebaseUrl() {
        return mFirebaseUrl;
    }
}
