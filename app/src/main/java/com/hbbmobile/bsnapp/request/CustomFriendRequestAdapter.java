package com.hbbmobile.bsnapp.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
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
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.model_firebase.FriendInfo;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.model_firebase.UserFirebase;
import com.hbbmobile.bsnapp.push_notification.PushMessage;
import com.hbbmobile.bsnapp.request.model.RequestsViewHolder;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 18/01/2017.
 */
public class CustomFriendRequestAdapter extends RecyclerView.Adapter<RequestsViewHolder> {
    private List<String> listUid;
    private Activity mActivity;
    private DatabaseReference mDatabase;
    private static final String TAG = "FriendRequestAdapter";


    public CustomFriendRequestAdapter(List<String> listUid, Activity mActivity) {
        this.listUid = listUid;
        this.mActivity = mActivity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public RequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_request, null);
        return new RequestsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RequestsViewHolder holder, final int position) {
        final String strUserId = listUid.get(position);
        //cập nhật các button
        holder.linearAccept.setVisibility(View.VISIBLE);
        holder.linearDecline.setVisibility(View.VISIBLE);

        mDatabase.child(Constants.USERS).child(strUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserFirebase user = dataSnapshot.getValue(UserFirebase.class);
                    if (user != null) {
                        holder.txtName.setText(user.getInfo().get(Constants.NAME).toString());
                        holder.txtPhone.setText(user.getInfo().get(Constants.PHONE).toString());
                        Glide.with(mActivity)
                                .load(user.getInfo().get(Constants.AVATAR).toString())
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
        //event click ok
        holder.linearAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptFriendRequest(holder, position);
                pushNotificationForAccept(BaseActivity.getUid(), strUserId);
            }
        });

        //event click dec;ne
        holder.linearDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineFriendRequest(holder, position);
            }
        });

        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.showProgessDialog(mActivity);
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<UserResponse> responseCall = apiService.getUser(strUserId);
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

    //TODO new update 2.7.2017
    private void acceptFriendRequest(final RequestsViewHolder holder, int position) {
        // ẩn các button
        holder.linearAccept.setVisibility(View.GONE);
        holder.linearDecline.setVisibility(View.GONE);
        final String partnerId = listUid.get(position);

        mDatabase
                .child(Constants.USERS)
                .child(BaseActivity.getUid())
                .child(Constants.STRANGER)
                .child(partnerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FriendInfo friendInfo = dataSnapshot.getValue(FriendInfo.class);
                            updateFriends(friendInfo.getMessageId(), partnerId);
                            removeStranger(partnerId);
                        } else {
                            addFriendbyCheckingMessageId(partnerId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addFriendbyCheckingMessageId(final String partnerId) {
        //messageId mặc định
        final String[] messageId = {String.format("%s&%s", BaseActivity.getUid(), partnerId)};

        //check messageId có tồn tại trước đó hay không
        final String messageId_1 = String.format("%s&%s", BaseActivity.getUid(), partnerId);
        final String messageId_2 = String.format("%s&%s", partnerId, BaseActivity.getUid());

        //kiểm tra thứ tự
        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES).child(messageId_1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //nếu tồn tại thì sử dụng messageId đó
                if (dataSnapshot.exists()) {
                    messageId[0] = messageId_1;

                    //cập nhật friend
                    updateFriends(messageId[0], partnerId);
                }

                //nếu không tòn tại, kiểm tra messageId2
                else {
                    mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES).child(messageId_2).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //nếu tồn tại thì sử dụng messageId đó
                            if (dataSnapshot.exists()) {
                                messageId[0] = messageId_2;
                            }

                            //nếu không tồn tại, thì sử dụng messageId như ban đầu
                            updateFriends(messageId[0], partnerId);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateFriends(String messageId, String partnerId) {
        long createAt = new Date().getTime();

        //thêm data vào node Friend
        FriendInfo friendInfo = new FriendInfo(createAt, messageId, 0);

        //cập nhật bạn bè 2 chiều
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FRIENDS).child(partnerId).setValue(friendInfo);
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.FRIENDS).child(BaseActivity.getUid()).setValue(friendInfo);

        //delete node này trong friendReceives của mình và friendRequest của mình
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FRIEND_RECEIVES).child(partnerId).removeValue();
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.FRIEND_REQUESTS).child(BaseActivity.getUid()).removeValue();
    }

    private void removeStranger(String partnerId) {
        mDatabase
                .child(Constants.USERS)
                .child(BaseActivity.getUid())
                .child(Constants.STRANGER)
                .child(partnerId).removeValue();

        mDatabase
                .child(Constants.USERS)
                .child(partnerId)
                .child(Constants.STRANGER)
                .child(BaseActivity.getUid()).removeValue();
    }

    private void pushNotificationForAccept(final String currentId, String partnerId) {
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    InfoUserFirebase infoPartner = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (infoPartner != null) {
                        final String deviceToken = infoPartner.getDeviceToken();
                        if (deviceToken.length() != 0) {
                            //bắn 1 thông báo kết bạn thành công qua
                            mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        InfoUserFirebase currentInfo = dataSnapshot.getValue(InfoUserFirebase.class);
                                        if (currentInfo != null) {
                                            String currentName = currentInfo.getName();
                                            String[] regIds = {deviceToken};
                                            JSONArray regArray = null;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                try {
                                                    regArray = new JSONArray(regIds);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            PushMessage.sendMessage(regArray, Constants.APP_NAME, String.format("%s %s", currentName, mActivity.getResources().getString(R.string.hasAcceptFriend)), "", "");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void declineFriendRequest(RequestsViewHolder holder, int position) {
        String partnerId = listUid.get(position);
        //delete node này trong friendReceives của mình và friendRequest của mình
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FRIEND_RECEIVES).child(partnerId).removeValue();
        mDatabase.child(Constants.USERS).child(partnerId).child(Constants.FRIEND_REQUESTS).child(BaseActivity.getUid()).removeValue();
        // ẩn các button
        holder.linearAccept.setVisibility(View.GONE);
        holder.linearDecline.setVisibility(View.GONE);

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
