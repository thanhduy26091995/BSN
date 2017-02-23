package com.hbbmobile.bsnapp.find_user.model;

import com.google.firebase.database.DatabaseReference;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 18/01/2017.
 */
public class FriendProfileSubmitter {
    private DatabaseReference mDatabase;

    public FriendProfileSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void sendRequest(String currentId, String partnerId) {
        /*
        Lưu vào bảng Request của mình
         */
        Map<String, Object> mapRequest = new HashMap<>();
        mapRequest.put(partnerId, true);

        /*
        Lưu vào bảng received của partner
         */
        Map<String, Object> mapReceive = new HashMap<>();
        mapReceive.put(currentId, true);
        //update data 2 chiều
        mDatabase.child(Constants.USERS).child(currentId).child(Constants.FRIEND_REQUESTS).setValue(mapRequest);
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.FRIEND_RECEIVES).setValue(mapReceive);
    }

    public void deleteAccount(String currentId, String partnerId) {
        mDatabase.child(Constants.USERS).child(currentId).child(Constants.FRIENDS).child(partnerId).removeValue();
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.FRIENDS).child(currentId).removeValue();
    }
}
