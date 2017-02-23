package com.hbbmobile.bsnapp.request.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.utils.Constants;

/**
 * Created by buivu on 18/01/2017.
 */
public class RequestsSubmitter {
    private DatabaseReference mDatabase;

    public RequestsSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //get all friend add
    public Query getAllRequest(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIEND_RECEIVES);
    }
}
