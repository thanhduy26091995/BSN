package com.hbbmobile.bsnapp.profile_user.model;

import com.google.firebase.database.DatabaseReference;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 20/01/2017.
 */
public class MyProfileSubmitter {
    private DatabaseReference mDatabase;

    public MyProfileSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void updateProfile(String uid, String name, String avatar, String phone) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.AVATAR, avatar);
        myMap.put(Constants.NAME, name);
        myMap.put(Constants.PHONE, phone);
        myMap.put(Constants.STATUS, 0);
        //update database
        mDatabase.child(Constants.USERS).child(uid).child(Constants.INFO).updateChildren(myMap);
    }
}
