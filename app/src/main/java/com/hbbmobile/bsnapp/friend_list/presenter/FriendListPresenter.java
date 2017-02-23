package com.hbbmobile.bsnapp.friend_list.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.friend_list.model.FriendListSubmitter;
import com.hbbmobile.bsnapp.friend_list.view.FriendListFragment;

/**
 * Created by buivu on 19/01/2017.
 */
public class FriendListPresenter {
    private FriendListSubmitter submitter;
    private FriendListFragment view;
    private DatabaseReference mDatabase;

    public FriendListPresenter(FriendListFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new FriendListSubmitter(mDatabase);
    }

    public Query getAllFriends(String uid) {
        return submitter.getAllFriends(uid);
    }
}
