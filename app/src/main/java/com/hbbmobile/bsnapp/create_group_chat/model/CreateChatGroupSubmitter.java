package com.hbbmobile.bsnapp.create_group_chat.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 06/02/2017.
 */

public class CreateChatGroupSubmitter {
    private DatabaseReference mDatabase;

    public CreateChatGroupSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy toàn bộ bạn trong danh sách bạn bè
    public Query getAllFriends(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIENDS);
    }

    public void updateMember(String groupId, List<String> listMember) {
        //addd member to group
        Map<String, Object> myMap = new HashMap<>();
        for (String i : listMember) {
            myMap.put(i, 0);
            //add group
            addGroupIdToUser(i, groupId);
        }
        //update member's group
        //add database
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).updateChildren(myMap);
    }

    //thêm member vào group
    public void addMemberToGroup(List<String> listMember, String currentId) {
        final StringBuilder groupName = new StringBuilder();
        //tạo key
        final String key = mDatabase.child(Constants.GROUPS).push().getKey();
        Map<String, Object> myMap = new HashMap<>();
        for (String i : listMember) {
            if (i.equals(currentId)) {
                myMap.put(i, 1);
            } else {
                myMap.put(i, 0);
            }
            //add group
            addGroupIdToUser(i, key);
        }
        //add name default
        for (final String data : listMember) {
            mDatabase.child(Constants.USERS).child(data).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                        if (userFirebase != null) {
                            groupName.append(userFirebase.getName() + ", ");
                            updateGroupName(key, groupName.toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //add database
        mDatabase.child(Constants.GROUPS).child(key).child(Constants.MEMBERS).setValue(myMap);
        //update message count
        Map<String, Object> mapCount = new HashMap<>();
        mapCount.put(Constants.FIREBASE_CHILD_COUNT, 0);
        mDatabase.child(Constants.GROUPS).child(key).updateChildren(mapCount);
    }

    private void updateGroupName(String groupId, String groupName) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.GROUP_NAME, groupName);
        myMap.put(Constants.GROUP_AVATAR, "");
        //update database
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).updateChildren(myMap);
    }

    private void addGroupIdToUser(String userId, String groupId) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(groupId, true);
        mDatabase.child(Constants.USERS).child(userId).child(Constants.GROUPS_LOWER).updateChildren(myMap);
    }
}
