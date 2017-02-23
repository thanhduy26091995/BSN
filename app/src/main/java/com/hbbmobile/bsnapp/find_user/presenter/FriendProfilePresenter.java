package com.hbbmobile.bsnapp.find_user.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbbmobile.bsnapp.find_user.model.FriendProfileSubmitter;
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;

/**
 * Created by buivu on 18/01/2017.
 */
public class FriendProfilePresenter {
    private FriendProfileActivity view;
    private DatabaseReference mDatabase;
    private FriendProfileSubmitter submitter;


    public FriendProfilePresenter(FriendProfileActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new FriendProfileSubmitter(mDatabase);
    }

    public void sendRequest(String currentId, String partnerId) {
        submitter.sendRequest(currentId, partnerId);
    }

    public void deleteAccount(String currentId, String partnerId) {
        submitter.deleteAccount(currentId, partnerId);
    }
}
