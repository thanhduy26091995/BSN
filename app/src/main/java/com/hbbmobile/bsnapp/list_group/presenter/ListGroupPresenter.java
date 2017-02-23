package com.hbbmobile.bsnapp.list_group.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.list_group.model.ListGroupSubmitter;
import com.hbbmobile.bsnapp.list_group.view.ListGroupFragment;

/**
 * Created by buivu on 06/02/2017.
 */

public class ListGroupPresenter {
    private ListGroupSubmitter submitter;
    private DatabaseReference mDatabase;
    private ListGroupFragment view;

    public ListGroupPresenter(ListGroupFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ListGroupSubmitter(mDatabase);
    }

    public Query getAllGroups(String uid) {
        return submitter.getAllGroups(uid);
    }
}
