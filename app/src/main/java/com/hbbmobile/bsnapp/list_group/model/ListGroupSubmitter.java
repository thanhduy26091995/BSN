package com.hbbmobile.bsnapp.list_group.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.utils.Constants;

/**
 * Created by buivu on 06/02/2017.
 */

public class ListGroupSubmitter {
    private DatabaseReference mDatabase;

    public ListGroupSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Query getAllGroups(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.GROUPS_LOWER);
    }
}
