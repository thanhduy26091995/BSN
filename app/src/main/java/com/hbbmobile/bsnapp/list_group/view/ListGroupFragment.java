package com.hbbmobile.bsnapp.list_group.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.list_group.CustomListGroupAdapter;
import com.hbbmobile.bsnapp.list_group.presenter.ListGroupPresenter;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 06/02/2017.
 */

public class ListGroupFragment extends android.support.v4.app.Fragment {

    private List<String> listGroupId;
    private RecyclerView mRecycler;
    private MyLinearLayoutManager customManager;
    private CustomListGroupAdapter customAdapter;
    private DatabaseReference mDatabase;
    private ListGroupPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_groups, container, false);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new ListGroupPresenter(this);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_all_groups);
        listGroupId = new ArrayList<>();
        customAdapter = new CustomListGroupAdapter(getActivity(), listGroupId);
        customManager = new MyLinearLayoutManager(getActivity());
        getGroup(BaseActivity.getUid());
        mRecycler.setLayoutManager(customManager);
        mRecycler.setAdapter(customAdapter);
        return rootView;
    }

    private void getGroup(String id) {
        presenter.getAllGroups(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    listGroupId.add(dataSnapshot.getKey());
                    customAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listGroupId.remove(dataSnapshot.getKey());
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
