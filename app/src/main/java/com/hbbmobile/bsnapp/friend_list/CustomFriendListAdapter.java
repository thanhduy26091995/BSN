package com.hbbmobile.bsnapp.friend_list;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;
import com.hbbmobile.bsnapp.friend_list.model.FriendListViewHolder;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.model_firebase.UserFirebase;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 19/01/2017.
 */
public class CustomFriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder> {

    private List<String> listUid;
    private Activity mActivity;
    private DatabaseReference mDatabase;
    private static final String TAG = "FriendListAdapter";

    public CustomFriendListAdapter(List<String> listUid, Activity mActivity) {
        this.listUid = listUid;
        this.mActivity = mActivity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_contacts, null);
        return new FriendListViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final FriendListViewHolder holder, int position) {
        final String strUid = listUid.get(position);
        Log.d(TAG, strUid);
        mDatabase.child(Constants.USERS).child(strUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserFirebase user = dataSnapshot.getValue(UserFirebase.class);
                    if (user != null) {
                        holder.txtName.setText(user.getInfo().get(Constants.NAME).toString());
                        holder.txtPhone.setText(user.getInfo().get(Constants.PHONE).toString());
                        Glide.with(mActivity)
                                .load(user.getInfo().get(Constants.AVATAR).toString())
                                .placeholder(R.drawable.avatar)
                                .centerCrop()
                                .error(R.drawable.avatar)
                                .into(holder.imgAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.showProgessDialog(mActivity);
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<UserResponse> responseCall = apiService.getUser(strUid);
                responseCall.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Constants.hideProgressDialog();
                        String error = response.body().getError();
                        if (error.equals("false")) {
                            User user = response.body().getListUser().get(0);
                            Intent myIntent = new Intent(mActivity, FriendProfileActivity.class);
                            myIntent.putExtra("User", user);
                            mActivity.startActivity(myIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        ShowAlertDialog.showAlert(mActivity.getResources().getString(R.string.cannotLoadUser), mActivity);
                        Constants.hideProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUid.size();
    }

    //using for pull to refresh
    public void clear() {
        listUid.clear();
        notifyDataSetChanged();
    }
}
