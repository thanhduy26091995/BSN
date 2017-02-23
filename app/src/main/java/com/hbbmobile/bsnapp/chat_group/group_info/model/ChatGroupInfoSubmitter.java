package com.hbbmobile.bsnapp.chat_group.group_info.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.EncodeImage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 09/02/2017.
 */

public class ChatGroupInfoSubmitter {
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public ChatGroupInfoSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public void updateGroupName(String groupId, String groupName) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.GROUP_NAME, groupName);
        //update database
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).updateChildren(myMap);
    }

    public Query getAllMember(String groupInfo) {
        return mDatabase.child(Constants.GROUPS).child(groupInfo).child(Constants.MEMBERS);
    }

    public void addImage(String groupId, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        byte[] bitmapGroupAvatar = null;
        if (Constants.AVATAR_GROUP_CHAT != null) {
            bitmapGroupAvatar = EncodeImage.encodeImage(Constants.AVATAR_GROUP_CHAT);
        }
        if (bitmapGroupAvatar != null) {
            StorageReference filePathAvatar = mStorage.child(Constants.GROUP_PHOTO).child(groupId);
            UploadTask uploadTask = filePathAvatar.putBytes(bitmapGroupAvatar);
            uploadTask.addOnSuccessListener(listener);

            //restart bitmap
            Constants.AVATAR_GROUP_CHAT = null;
        }
    }
    public void updateGroupAvatar(String groupId, String linkGroup) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.GROUP_AVATAR, linkGroup);
        //update database
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).updateChildren(myMap);
    }

    public void outGroup(String groupId, String currentId){
        //remove user from groups
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).child(currentId).removeValue();
        //remove group into user data
        mDatabase.child(Constants.USERS).child(currentId).child(Constants.GROUPS_LOWER).child(groupId).removeValue();
    }
}
