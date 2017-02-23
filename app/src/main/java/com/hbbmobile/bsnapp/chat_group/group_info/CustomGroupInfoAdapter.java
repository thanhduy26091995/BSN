package com.hbbmobile.bsnapp.chat_group.group_info;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.chat_group.group_info.model.ChatGroupInfoViewHolder;
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.profile_user.view.MyProfileActivity;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 10/02/2017.
 */

public class CustomGroupInfoAdapter extends RecyclerView.Adapter<ChatGroupInfoViewHolder> {
    private List<String> listId;
    private Activity activity;
    private DatabaseReference mDatabase;
    private List<String> strAdmin;
    private String groupId;


    public CustomGroupInfoAdapter(Activity activity, List<String> listId, List<String> strAdmin) {
        this.activity = activity;
        this.listId = listId;
        this.strAdmin = strAdmin;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public CustomGroupInfoAdapter(Activity activity, List<String> listId, List<String> strAdmin, String groupId) {
        this.activity = activity;
        this.listId = listId;
        this.strAdmin = strAdmin;
        this.groupId = groupId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ChatGroupInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(activity, R.layout.item_list_member, null);
        return new ChatGroupInfoViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ChatGroupInfoViewHolder holder, int position) {
        final String strUid = listId.get(position);
        Log.d("CHATGROUP", strAdmin + "/" + strUid);
        if (strAdmin.get(0).equals(BaseActivity.getUid())) {
            holder.relaMore.setVisibility(View.VISIBLE);
            if (strUid.equals(BaseActivity.getUid())) {
                holder.relaMore.setVisibility(View.GONE);
            }
        } else {
            holder.relaMore.setVisibility(View.GONE);
        }

//        long value = (long) hashData.get(strUid);
//        Log.d("KEY_VALUE", strUid + "/" + value);
//        if (value == 1) {
//            if (strUid.equals(BaseActivity.getUid())) {
//                holder.relaMore.setVisibility(View.GONE);
//            } else {
//                holder.relaMore.setVisibility(View.VISIBLE);
//            }
//        } else {
//            holder.relaMore.setVisibility(View.GONE);
//        }
        mDatabase.child(Constants.USERS).child(strUid).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    InfoUserFirebase user = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (user != null) {
                        holder.txtMemberName.setText(user.getName());
                        Glide.with(activity)
                                .load(user.getAvatar())
                                .placeholder(R.drawable.avatar)
                                .centerCrop()
                                .error(R.drawable.avatar)
                                .into(holder.imgMemberAvatar);
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
                Constants.showProgessDialog(activity);
                if (strUid.equals(BaseActivity.getUid())) {
                    Intent it = new Intent(activity, MyProfileActivity.class);
                    activity.startActivity(it);
                    Constants.hideProgressDialog();
                } else {
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<UserResponse> responseCall = apiService.getUser(strUid);
                    responseCall.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            Constants.hideProgressDialog();
                            String error = response.body().getError();
                            if (error.equals("false")) {
                                User user = response.body().getListUser().get(0);
                                Intent myIntent = new Intent(activity, FriendProfileActivity.class);
                                myIntent.putExtra("User", user);
                                activity.startActivity(myIntent);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            ShowAlertDialog.showAlert(activity.getResources().getString(R.string.cannotLoadUser), activity);
                            Constants.hideProgressDialog();
                        }
                    });
                }
            }
        });
        //rela click
        holder.relaMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PopupMenu popupMenu = new PopupMenu(activity, v);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.menu_kick, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_kick:
                                    outGroup(groupId, strUid);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                } catch (Exception e) {

                }
            }
        });

    }

    private void outGroup(String groupId, String currentId) {
        //remove user from groups
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).child(currentId).removeValue();
        //remove group into user data
        mDatabase.child(Constants.USERS).child(currentId).child(Constants.GROUPS_LOWER).child(groupId).removeValue();
    }

    @Override
    public int getItemCount() {
        return listId.size();
    }
}
