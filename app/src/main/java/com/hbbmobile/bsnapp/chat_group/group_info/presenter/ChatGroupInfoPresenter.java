package com.hbbmobile.bsnapp.chat_group.group_info.presenter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.UploadTask;
import com.hbbmobile.bsnapp.chat_group.group_info.model.ChatGroupInfoSubmitter;
import com.hbbmobile.bsnapp.chat_group.group_info.view.ChatGroupInfoActivity;

/**
 * Created by buivu on 09/02/2017.
 */

public class ChatGroupInfoPresenter {
    private ChatGroupInfoSubmitter submitter;
    private ChatGroupInfoActivity view;
    private DatabaseReference mDatabase;

    public ChatGroupInfoPresenter(ChatGroupInfoActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ChatGroupInfoSubmitter(mDatabase);
    }

    public void updateGroupName(String groupId, String groupName) {
        submitter.updateGroupName(groupId, groupName);
    }

    public Query getAllMember(String groupInfo) {
        return submitter.getAllMember(groupInfo);
    }

    public void updateGroupAvatar(final String groupId) {
        submitter.addImage(groupId, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                submitter.updateGroupAvatar(groupId, taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    public void outGroup(String groupId, String currentId) {
        submitter.outGroup(groupId, currentId);
    }
}
