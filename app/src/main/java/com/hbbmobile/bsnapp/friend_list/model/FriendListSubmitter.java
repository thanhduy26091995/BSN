package com.hbbmobile.bsnapp.friend_list.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.utils.Constants;

/**
 * Created by buivu on 19/01/2017.
 */
public class FriendListSubmitter {
    private DatabaseReference mDatabase;

    public FriendListSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Query getAllFriends(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIENDS);
    }
}
