package com.hbbmobile.bsnapp.chat_group.add_more_member.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.create_group_chat.CustomCreateGroupAdapter;
import com.hbbmobile.bsnapp.create_group_chat.presenter.CreateChatGroupPresenter;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 10/02/2017.
 */

public class AddMoreMemberActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout linearBack;
    private RecyclerView mRecycler;
    private CustomCreateGroupAdapter customAdapter;
    private MyLinearLayoutManager customManager;
    private List<String> listUserId;
    private List<String> listMember;
    private CreateChatGroupPresenter presenter;
    private TextView txtCreateGroup;
    private List<String> listFriendRemain;
    private String groupId = "";
    public static final String GROUP_ID = "groupId";
    private TextView txtTitleGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //init views
        linearBack = (LinearLayout) findViewById(R.id.linear_img_back);
        mRecycler = (RecyclerView) findViewById(R.id.frame_all_friends);
        txtCreateGroup = (TextView) findViewById(R.id.txt_create_group);
        txtTitleGroup = (TextView) findViewById(R.id.txt_title_group);
        txtTitleGroup.setText(getResources().getString(R.string.addMember));
        //event click
        linearBack.setOnClickListener(this);
        txtCreateGroup.setOnClickListener(this);
        //init
        listUserId = new ArrayList<>();
        listMember = new ArrayList<>();

        presenter = new CreateChatGroupPresenter(this);
        String[] StringList = getIntent().getStringArrayExtra("data");
        groupId = getIntent().getStringExtra(GROUP_ID);
        listFriendRemain = new ArrayList<>();
        for (String data : StringList) {
            listFriendRemain.add(data);
        }

        customAdapter = new CustomCreateGroupAdapter(this, listFriendRemain, listMember);
        customManager = new MyLinearLayoutManager(this);

        //set data for recyclerview
        mRecycler.setLayoutManager(customManager);
        mRecycler.setAdapter(customAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == linearBack) {
            finish();
        } else if (v == txtCreateGroup) {
            presenter.updateMember(groupId, listMember);
            finish();
        }
    }

}
