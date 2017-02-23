package com.hbbmobile.bsnapp.create_group_chat.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.create_group_chat.CustomCreateGroupAdapter;
import com.hbbmobile.bsnapp.create_group_chat.presenter.CreateChatGroupPresenter;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 06/02/2017.
 */

public class CreateGroupChatActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout linearBack;
    private RecyclerView mRecycler;
    private CustomCreateGroupAdapter customAdapter;
    private MyLinearLayoutManager customManager;
    private List<String> listUserId;
    private List<String> listMember;
    private CreateChatGroupPresenter presenter;
    private TextView txtCreateGroup, txtTitleGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //init views
        linearBack = (LinearLayout) findViewById(R.id.linear_img_back);
        mRecycler = (RecyclerView) findViewById(R.id.frame_all_friends);
        txtCreateGroup = (TextView) findViewById(R.id.txt_create_group);
        txtTitleGroup = (TextView) findViewById(R.id.txt_title_group);
        txtTitleGroup.setText(getResources().getString(R.string.createGroup));
        //event click
        linearBack.setOnClickListener(this);
        txtCreateGroup.setOnClickListener(this);
        //init
        listUserId = new ArrayList<>();
        listMember = new ArrayList<>();

        presenter = new CreateChatGroupPresenter(this);
        customAdapter = new CustomCreateGroupAdapter(this, listUserId, listMember);
        customManager = new MyLinearLayoutManager(this);
        loadData();
        //set data for recyclerview
        mRecycler.setLayoutManager(customManager);
        mRecycler.setAdapter(customAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == linearBack) {
            finish();
        } else if (v == txtCreateGroup) {
            listMember.add(getUid());
            presenter.addMemberToGroup(listMember, getUid());
            finish();
        }
    }

    private void loadData() {
        final Query myQuery = presenter.getAllFriends(BaseActivity.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        listUserId.add(data.getKey());
                        customAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
