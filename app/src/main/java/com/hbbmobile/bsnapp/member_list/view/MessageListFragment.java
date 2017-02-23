package com.hbbmobile.bsnapp.member_list.view;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.create_group_chat.view.CreateGroupChatActivity;
import com.hbbmobile.bsnapp.member_list.MessageListAdapter;
import com.hbbmobile.bsnapp.member_list.model.Friend;
import com.hbbmobile.bsnapp.member_list.model.Info;
import com.hbbmobile.bsnapp.member_list.model.User;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageListFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rvRecyclerView;
    private ImageButton btnAddMember;
    //    private String mCurrentUser = "JPri8130a9dzei3h0Q5AsZ5yKtF2";
    private String mCurrentUser;
    private MessageListAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<Friend> mList;
    private DatabaseReference mDatabase;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listGroup;


    private User user = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mList = new ArrayList<>();
        listGroup = new ArrayList<>();
        user.setFriends(mList);

        doFormWidget(view);
        setUpAdapter(view);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            verifyStoragePermissions();
            mCurrentUser = BaseActivity.getUid();
            getFriend(mCurrentUser);
        } else {
            ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddMember) {
            Intent intent = new Intent(getActivity(), CreateGroupChatActivity.class);
            startActivity(intent);
        }
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }

    }

    private void doFormWidget(View v) {
        rvRecyclerView = (RecyclerView) v.findViewById(R.id.activity_message_list_rvRecyclerView);
        btnAddMember = (ImageButton) v.findViewById(R.id.btn_add_member);
        //event click
        btnAddMember.setOnClickListener(this);
    }

    private void setUpAdapter(View v) {
        layoutManager = new LinearLayoutManager(v.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new MessageListAdapter(user.getFriends(), v.getContext());
        rvRecyclerView.setHasFixedSize(true);
        rvRecyclerView.setLayoutManager(layoutManager);
        rvRecyclerView.setAdapter(mAdapter);
    }

    private void getFriend(String id) {
        Constants.showProgessDialog(getActivity());
        mDatabase.child(Constants.USERS).child(id)
                .child(Constants.FRIENDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Constants.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child(Constants.USERS).child(id)
                .child(Constants.FRIENDS).addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                //try {
                final String friendId = dataSnapshot.getKey();
                final Friend friend = new Friend();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                //  friend.setId((String) map.get("id"));
                friend.setCreateAt(String.valueOf(map.get(Constants.CREATE_AT)));
                //   friend.setStatus((int) (long) map.get("status"));
                friend.setMessageId((String) map.get(Constants.MESSAGE_ID));

                mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES).child(friend.getMessageId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mDatabase.child(Constants.USERS)
                                    .child(friendId)
                                    .child(Constants.INFO)
                                    .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                        @Override
                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                            if (dataSnapshot != null) {
                                                Info info = dataSnapshot.getValue(Info.class);
                                                if (info != null) {
                                                    friend.setFriendInfo(info);
                                                    mAdapter.addFriend(friend);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Constants.hideProgressDialog();
                    }
                });
                Constants.hideProgressDialog();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //TODO
    }
}
