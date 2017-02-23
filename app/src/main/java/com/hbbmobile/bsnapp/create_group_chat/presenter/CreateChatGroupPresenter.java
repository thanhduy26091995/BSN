package com.hbbmobile.bsnapp.create_group_chat.presenter;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hbbmobile.bsnapp.create_group_chat.model.CreateChatGroupSubmitter;

import java.util.List;

/**
 * Created by buivu on 06/02/2017.
 */

public class CreateChatGroupPresenter {
    private CreateChatGroupSubmitter submitter;
    private Activity view;
    private DatabaseReference mDatabase;

    public CreateChatGroupPresenter(Activity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new CreateChatGroupSubmitter(mDatabase);
    }

    //lấy toàn bộ bạn trong danh sách bạn bè
    public Query getAllFriends(String uid) {
        return submitter.getAllFriends(uid);
    }

    public void addMemberToGroup(List<String> listMember, String currentId) {
        submitter.addMemberToGroup(listMember, currentId);
    }

    public void updateMember(String groupId, List<String> listMember) {
        submitter.updateMember(groupId, listMember);
    }
}
