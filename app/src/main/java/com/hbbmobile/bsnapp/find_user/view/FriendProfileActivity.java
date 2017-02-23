package com.hbbmobile.bsnapp.find_user.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.chat.ChatActivity;
import com.hbbmobile.bsnapp.find_user.presenter.FriendProfilePresenter;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model_firebase.FriendInfo;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.push_notification.PushMessage;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

/**
 * Created by buivu on 09/01/2017.
 */
public class FriendProfileActivity extends BaseActivity implements View.OnClickListener {

    private User user;
    private TextView txtUsernameHeader, txtUsername, txtNickname, txtGender, txtTitle, txtCompanyName, txtCompanyAddress, txtTelephone, txtEmail, txtDateOfBirth, txtAboutYou;
    private ImageView imgAvatar;
    private Button btnChat, btnDeleteAccount, btnAddFriend;
    private LinearLayout linearBack;
    private DatabaseReference mDatabase;
    private FriendProfilePresenter prensenter;

    private FriendInfo mFriendInfo;
    private boolean isFriend = false;
    private boolean isMessageExist = false;
    private String mMessageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        //init data firebsae
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prensenter = new FriendProfilePresenter(this);
        initViews();
        //get intent
        user = (User) getIntent().getSerializableExtra("User");
        loadData(user);

        //event click
        linearBack.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);
        btnAddFriend.setOnClickListener(this);

        mFriendInfo = new FriendInfo();
    }

    private void checkFriend() {
//        mDatabase
//                .child(Constants.USERS)
//                .child(getUid())
//                .child(Constants.FRIENDS)
//                .child(user.getId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            isFriend = true;
//
//                            //hiển thị
//                            btnDeleteAccount.setVisibility(View.VISIBLE);
//                            //ẩn
//                            btnAddFriend.setVisibility(View.GONE);
//                        } else {
//                            isFriend = false;
//
//                            //hiển thị
//                            btnDeleteAccount.setVisibility(View.GONE);
//                            //ẩn
//                            btnAddFriend.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        mDatabase
                .child(Constants.USERS)
                .child(getUid())
                .child(Constants.FRIENDS)
                .child(user.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            isFriend = true;

                            //hiển thị
                            btnDeleteAccount.setVisibility(View.VISIBLE);
                            //ẩn
                            btnAddFriend.setVisibility(View.GONE);
                        } else {
                            isFriend = false;

                            //hiển thị
                            btnDeleteAccount.setVisibility(View.GONE);
                            //ẩn
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initViews() {
        //Textview
        txtUsernameHeader = (TextView) findViewById(R.id.txt_user_name_header);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtNickname = (TextView) findViewById(R.id.txtNickName);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        txtCompanyAddress = (TextView) findViewById(R.id.txtCompanyAddress);
        txtTelephone = (TextView) findViewById(R.id.txtPhoneNumber);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtDateOfBirth = (TextView) findViewById(R.id.txtDateOfBirth);
        txtAboutYou = (TextView) findViewById(R.id.txtAboutYou);
        //imageview
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        //button
        btnChat = (Button) findViewById(R.id.btnChat);
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);
        //linear layout
        linearBack = (LinearLayout) findViewById(R.id.linear_img_back);
    }

    private void loadData(User user) {
        txtUsernameHeader.setText(user.getFullName());
        txtUsername.setText(user.getFullName());
        txtNickname.setText(user.getNickName());
        txtGender.setText(user.getGender());
        txtTitle.setText(user.getTitle());
        txtCompanyName.setText(user.getCompanyName());
        txtCompanyAddress.setText(user.getCompanyAddress());
        txtTelephone.setText(user.getPhone());
        txtEmail.setText(user.getEmail());
        txtDateOfBirth.setText(user.getDateOfBirth());
        txtAboutYou.setText(user.getAboutYou());

        Glide.with(FriendProfileActivity.this)
                .load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.avatar)
                .centerCrop()
                .into(imgAvatar);
        //check friend
        checkFriend();
    }

    private void unFriend(String partnerId) {
        if (getUid() != null && partnerId != null) {
            prensenter.deleteAccount(getUid(), partnerId);
        }
    }

    private void showToast(String mess) {
        Toast.makeText(FriendProfileActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(final View view) {
        if (view == linearBack) {
            finish();
        } else if (view == btnAddFriend) {
            if (getUid() != null && user.getId() != null) {
                prensenter.sendRequest(getUid(), user.getId());
                //send notification for add friend
                pushNotificationForRequest();
                //update UI
                btnAddFriend.setVisibility(View.GONE);
                btnDeleteAccount.setVisibility(View.GONE);
                //show toast
                showToast(getResources().getString(R.string.sendRequestSuccessfully));
            } else {
                ShowAlertDialog.showAlert(getResources().getString(R.string.throwError), FriendProfileActivity.this);
            }
        } else if (view == btnDeleteAccount) {
            //show alert xác nhận
            final AlertDialog.Builder builder = new AlertDialog.Builder(FriendProfileActivity.this);
            builder.setMessage(R.string.confirmUnfriend)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            unFriend(user.getId());
                            //refresh UI
                            btnAddFriend.setVisibility(View.VISIBLE);
                            btnDeleteAccount.setVisibility(View.GONE);
                        }
                    })
                    .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
        } else if (view == btnChat) {
            clickChat();
        }
    }

    private void pushNotificationForRequest() {
        //get deviceToken của partnerID (người mà mình muốn gửi lời mời)
        mDatabase.child(Constants.USERS).child(user.getId()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    InfoUserFirebase infoUserFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (infoUserFirebase != null) {
                        String deviceToken = infoUserFirebase.getDeviceToken();
                        if (deviceToken.length() != 0) {
                            final String[] regIds = {deviceToken};
                            //nếu người dùng đó có deviceToken != 0, thì truy vấn đề lấy tên của mình
                            //để truyền vào câu truy vấn
                            mDatabase.child(Constants.USERS).child(getUid()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        InfoUserFirebase currentUser = dataSnapshot.getValue(InfoUserFirebase.class);
                                        if (currentUser != null) {
                                            String currentName = currentUser.getName();
                                            JSONArray regArray = null;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                try {
                                                    regArray = new JSONArray(regIds);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            //gửi 1 push notification
                                            PushMessage.sendMessage(regArray, Constants.APP_NAME, String.format("%s %s", currentName, getResources().getString(R.string.hasRequest)), "", "");
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

    //TODO new update 2.7.2017
    private void clickChat() {
        //get message id
        if (isFriend) {
            mDatabase
                    .child(Constants.USERS)
                    .child(getUid())
                    .child(Constants.FRIENDS)
                    .child(user.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FriendInfo friendInfo = dataSnapshot.getValue(FriendInfo.class);
                                if (friendInfo != null) {
                                    openChatActivity(friendInfo.getMessageId());
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            mDatabase
                    .child(Constants.USERS)
                    .child(getUid())
                    .child(Constants.STRANGER)
                    .child(user.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FriendInfo friendInfo = dataSnapshot.getValue(FriendInfo.class);
                                if (friendInfo != null) {
                                    openChatActivity(friendInfo.getMessageId());
                                }
                            } else {
                                checkMessageExist(user.getId());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void openChatActivity(String messageId) {
        Intent intent = new Intent(FriendProfileActivity.this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MESSAGE_ID, messageId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void checkMessageExist(String partnerId) {
        //check messageId có tồn tại trước đó hay không
        final String messageId_1 = String.format("%s&%s", getUid(), partnerId);
        final String messageId_2 = String.format("%s&%s", partnerId, getUid());

        //kiểm tra thứ tự
        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES).child(messageId_2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //nếu tồn tại thì sử dụng messageId_2 đó
                if (dataSnapshot.exists()) {
                    updateStranger(messageId_2);
                    openChatActivity(messageId_2);
                }

                //nếu không tòn tại, kiểm tra messageId_1
                else {
                    mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES).child(messageId_1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            updateStranger(messageId_1);
                            openChatActivity(messageId_1);
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

    private void updateStranger(String messageId) {
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setCreateAt(new Date().getTime());
        friendInfo.setMessageId(messageId);

        mDatabase
                .child(Constants.USERS)
                .child(getUid())
                .child(Constants.STRANGER)
                .child(user.getId())
                .setValue(friendInfo);

        mDatabase
                .child(Constants.USERS)
                .child(user.getId())
                .child(Constants.STRANGER)
                .child(getUid())
                .setValue(friendInfo);
    }
}
