package com.hbbmobile.bsnapp.request.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.request.model.RequestsSubmitter;
import com.hbbmobile.bsnapp.request.view.RequestsFragment;
import com.hbbmobile.bsnapp.utils.Constants;

/**
 * Created by buivu on 18/01/2017.
 */
public class RequestsPresenter {
    private DatabaseReference mDatabase;
    private RequestsFragment view;
    private RequestsSubmitter submitter;

    public RequestsPresenter(RequestsFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new RequestsSubmitter(mDatabase);
    }

    //get all friend add
    public Query getAllRequest(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIEND_RECEIVES);
    }


}
