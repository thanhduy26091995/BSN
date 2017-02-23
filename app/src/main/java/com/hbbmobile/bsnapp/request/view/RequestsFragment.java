package com.hbbmobile.bsnapp.request.view;

import android.app.ProgressDialog;
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
import com.hbbmobile.bsnapp.request.CustomFriendRequestAdapter;
import com.hbbmobile.bsnapp.request.presenter.RequestsPresenter;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 18/01/2017.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRecycler;
    private RequestsPresenter presenter;
    private MyLinearLayoutManager myLinearLayoutManager;
    private List<String> listUserId;
    private CustomFriendRequestAdapter customAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog mProgressDialog;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {


            rootView = inflater.inflate(R.layout.activity_request, container, false);
            presenter = new RequestsPresenter(this);
            myLinearLayoutManager = new MyLinearLayoutManager(getActivity());
            listUserId = new ArrayList<>();
            customAdapter = new CustomFriendRequestAdapter(listUserId, getActivity());
            //init components
            mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_friend_request);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                loadData();
            } else {
                ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
            }
            mRecycler.setLayoutManager(myLinearLayoutManager);
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
        } else {
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void loadData() {
        showProgessDialog();
        Query query = presenter.getAllRequest(BaseActivity.getUid());
        //check nếu node không tồn tại thì đóng progress dialog
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    hideProgressDialog();
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

                for (int i = 0; i < listUserId.size(); i++) {
                    if (dataSnapshot.getKey().equals(listUserId.get(i))) {
                        result.set(0, false);
                    }
                }
                if (result.get(0)) {
                    listUserId.add(dataSnapshot.getKey());
                    customAdapter.notifyDataSetChanged();
                }
                hideProgressDialog();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listUserId.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
