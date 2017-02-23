package com.hbbmobile.bsnapp.friend_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.friend_list.CustomFriendListAdapter;
import com.hbbmobile.bsnapp.friend_list.presenter.FriendListPresenter;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 19/01/2017.
 */
public class FriendListFragment extends Fragment {

    private RecyclerView mRecycler;
    private MyLinearLayoutManager customLinearLayoutManager;
    private List<String> listUid;
    private CustomFriendListAdapter customAdapter;
    private FriendListPresenter presenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_contacts, container, false);
            //init
            presenter = new FriendListPresenter(this);
            listUid = new ArrayList<>();
            customAdapter = new CustomFriendListAdapter(listUid, getActivity());
            customLinearLayoutManager = new MyLinearLayoutManager(getActivity());
            //init components
            mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_contacts);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            //load data
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                loadData();
            } else {
                ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
            }
            mRecycler.setLayoutManager(customLinearLayoutManager);
            mRecycler.setAdapter(customAdapter);


            //event rehresh
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            customAdapter.clear();
                            loadData();
                        }
                    });
                }
            });
        }
        else {
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void loadData() {
        Constants.showProgessDialog(getActivity());
        Query query = presenter.getAllFriends(BaseActivity.getUid());
        //check nếu node không tồn tại thì đóng progress dialog
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final List<Boolean> result = new ArrayList<>();
                result.add(true);

                for (int i = 0; i < listUid.size(); i++) {
                    if (dataSnapshot.getKey().equals(listUid.get(i))) {
                        result.set(0, false);
                    }
                }
                if (result.get(0)) {
                    listUid.add(dataSnapshot.getKey());
                    customAdapter.notifyDataSetChanged();
                }
                Constants.hideProgressDialog();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listUid.remove(dataSnapshot.getKey());
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
