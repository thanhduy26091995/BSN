package com.hbbmobile.bsnapp.create_an_account.model;

import com.google.firebase.database.DatabaseReference;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 18/01/2017.
 */
public class CreateAnAccountSubmitter {
    private DatabaseReference mDatabase;

    public CreateAnAccountSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void addUser(String currentID, String name, String phone, String avatar, Long status) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.NAME, name);
        myMap.put(Constants.STATUS, status);
        myMap.put(Constants.PHONE, phone);
        myMap.put(Constants.AVATAR, avatar);
        myMap.put(Constants.DEVICE_TOKEN, "");
        //add into firebase
        mDatabase.child(Constants.USERS).child(currentID).child(Constants.INFO).setValue(myMap);
    }
}
