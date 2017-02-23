package com.hbbmobile.bsnapp.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.base.view.IconTextView;
import com.hbbmobile.bsnapp.member_list.model.Friend;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.push_notification.PushMessage;
import com.hbbmobile.bsnapp.utils.Constants;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity
        implements View.OnClickListener {
    private IconTextView itvBack;
    private Button btnSend;
    private EditText etMessage;
    private RecyclerView rvRecyclerView;
    private TextView tvName;
    private PopupMenu popupMenu;
    private ImageView ivSettings;

    private Friend mFriend;

    private RelativeLayout rlFriendRequest;

    private LinearLayoutManager layoutManager;

    private ChatAdapter mAdapter;

    private List<MessageHistory> mMessageHistoryList;
    private List<MessageHistory> mReloadMessage;

    private String mMessageId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Gson mGson;

    private String mCurrentId;
    private String mFriendId;

    private boolean isStartLoading = true;

    private String mKeyStart;

    private int mFriendRequestStatus;

    private int mMessageCount = 0;
    private int mTotalMessage = 0;
    String mKeyEnd = null;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int NUMBER_MESSAGE_LOADING_ON_START = 20;
    private static int NUMBER_MESSAGE_LOADING_ON_HISTORY = 20;

    private FirebaseStorage mFireStorage = FirebaseStorage.getInstance();

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private String deviceToken = "";
    private String fromName = "";
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_PHONE_CALL = 1002;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };

    private InfoUserFirebase mInfoUserFb = new InfoUserFirebase();
    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentId = getUid();
        doFormWidget();
        doEvent();
        getDatafromActivity();
        initCurrentInfo();

        phone = getIntent().getStringExtra(Constants.PHONE);
        mStorageRef = mFireStorage.getReferenceFromUrl("gs://bsnapp-93c52.appspot.com");
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mMessageHistoryList = new ArrayList<>();
        mReloadMessage = new ArrayList<>();

        mAdapter = new ChatAdapter(mMessageHistoryList, mCurrentId, this);
        rvRecyclerView.setHasFixedSize(false);
        rvRecyclerView.setItemViewCacheSize(20);
        rvRecyclerView.setDrawingCacheEnabled(true);
        rvRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        rvRecyclerView.setLayoutManager(layoutManager);
        rvRecyclerView.setAdapter(mAdapter);

//        getDeviceToken();
        getFriendInfo();
        getSingleHistory(mMessageId);
//        updateFriendNameChanged();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isStartLoading = true;
                refreshItems();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 6000);
            }
        });
    }

    int mCount;

    private void refreshItems() {
        mCount = 0;
        if (mMessageCount == 0) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this.getBaseContext(), "No message left", Toast.LENGTH_SHORT).show();
        } else if (mMessageCount - NUMBER_MESSAGE_LOADING_ON_HISTORY > 0) {
            mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                    .child(mMessageId)
                    .child(Constants.FIREBASE_CHILD_HISTORY)
                    .endAt(null, mKeyStart)
                    .limitToLast(NUMBER_MESSAGE_LOADING_ON_HISTORY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                MessageHistory messageHistory = item.getValue(MessageHistory.class);

                                if (mCount < NUMBER_MESSAGE_LOADING_ON_HISTORY - 1) {
                                    mReloadMessage.add(messageHistory);
                                    mCount++;
                                } else {
                                    mCount = 0;
                                    onItemsLoadComplete();
                                    mMessageCount = mMessageCount - NUMBER_MESSAGE_LOADING_ON_HISTORY + 1;
                                }

                                if (!messageHistory.getIsRead() && !messageHistory.getFromUid().equals(mCurrentId)) {
                                    messageHistory.setIsRead(true);
                                    mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                            .child(mMessageId)
                                            .child(Constants.FIREBASE_CHILD_HISTORY)
                                            .child(item.getKey())
                                            .child(Constants.IS_READ).setValue(true);
                                }

                                if (isStartLoading) {
                                    mKeyStart = item.getKey();
                                    isStartLoading = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } else if (mMessageCount - NUMBER_MESSAGE_LOADING_ON_HISTORY < 0) {
            mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                    .child(mMessageId)
                    .child(Constants.FIREBASE_CHILD_HISTORY)
                    .endAt(null, mKeyStart)
                    .limitToLast(mMessageCount)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                MessageHistory messageHistory = item.getValue(MessageHistory.class);

                                if (mCount < mMessageCount - 1) {
                                    mReloadMessage.add(messageHistory);
                                    mCount++;
                                } else {
                                    onItemsLoadComplete();
                                    mMessageCount = mMessageCount - mMessageCount;
                                }

                                if (!messageHistory.getIsRead() && !messageHistory.getFromUid().equals(mCurrentId)) {
                                    messageHistory.setIsRead(true);
                                    mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                            .child(mMessageId)
                                            .child(Constants.FIREBASE_CHILD_HISTORY)
                                            .child(item.getKey())
                                            .child(Constants.IS_READ).setValue(true);
                                }

                                if (isStartLoading) {
                                    mKeyStart = item.getKey();
                                    isStartLoading = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        mAdapter.loadingHistory(mReloadMessage);

        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);

        mReloadMessage.clear();
    }

    private void getSingleHistory(final String id) {
        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                .child(id)
                .child(Constants.FIREBASE_CHILD_COUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Long messageTotal = dataSnapshot.getValue(Long.class);
                            mTotalMessage = (int) (long) messageTotal;
                            if (mTotalMessage > NUMBER_MESSAGE_LOADING_ON_START) {
                                mMessageCount = mTotalMessage;
                                if (mMessageCount > 0) {
                                    mMessageCount = mMessageCount - NUMBER_MESSAGE_LOADING_ON_START + 1;
                                }
                            }
                        } catch (Exception e) {
                            Log.v("Failed", e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                .child(id)
                .child(Constants.FIREBASE_CHILD_HISTORY)
                .limitToLast(NUMBER_MESSAGE_LOADING_ON_START)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            MessageHistory messageHistory = item.getValue(MessageHistory.class);
                            mReloadMessage.add(messageHistory);

                            if (!messageHistory.getIsRead() && !messageHistory.getFromUid().equals(mCurrentId)) {
                                messageHistory.setIsRead(true);
                                mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                        .child(id)
                                        .child(Constants.FIREBASE_CHILD_HISTORY)
                                        .child(item.getKey())
                                        .child(Constants.IS_READ).setValue(true);
                            }

                            mKeyEnd = item.getKey();
                            if (isStartLoading) {
                                mKeyStart = item.getKey();
                                isStartLoading = false;
                            }
                        }

                        mAdapter.loadingHistory(mReloadMessage);
                        rvRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());

                        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                .child(id)
                                .child(Constants.FIREBASE_CHILD_HISTORY)
                                .startAt(null, mKeyEnd)
                                .limitToLast(1)
                                .addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        if (!dataSnapshot.getKey().equals(mKeyEnd)) {
                                            MessageHistory messageHistory = dataSnapshot.getValue(MessageHistory.class);
                                            if (!messageHistory.getFromUid().equals(mCurrentId)) {
//                                                mAdapter.addNewMessage(messageHistory);
//                                                mAdapter.updateMessageStatus();
//                                                mAdapter.addFriendMessage(messageHistory);
                                                mAdapter.addNewMessage(messageHistory);
                                                rvRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                                                mTotalMessage++;

                                                if (active) {
                                                    mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                                            .child(id)
                                                            .child(Constants.FIREBASE_CHILD_HISTORY)
                                                            .child(dataSnapshot.getKey())
                                                            .child(Constants.IS_READ).setValue(true);
                                                } else {
                                                    // do something
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                        MessageHistory messageHistory = dataSnapshot.getValue(MessageHistory.class);
//                                        if (mAdapter.getLastMessageId().equals(messageHistory.getFromUid())){
                                        mAdapter.updateIsRead();
//                                        }
                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void doFormWidget() {
        itvBack = (IconTextView) findViewById(R.id.txtBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        rvRecyclerView = (RecyclerView) findViewById(R.id.rvRecylerView);
        tvName = (TextView) findViewById(R.id.tvName);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);
    }

    private void doEvent() {
        itvBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        ivSettings.setOnClickListener(this);
        etMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == itvBack) {
            mTotalMessage = 0;
            mMessageCount = 0;
            finish();
        } else if (v == btnSend) {
            String message = etMessage.getText().toString().trim();
            if (!message.equals("")) {
                sendMessage(message);
            }
        } else if (v == ivSettings) {
            displayPopUp(v);
        } else if (v == etMessage) {
//            if (mAdapter.getItemCount() < 5) {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            }
//            else {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//            }
        }
    }

    private void getDatafromActivity() {
        Intent intent = getIntent();
        String messageId = intent.getStringExtra(Constants.MESSAGE_ID);
        mMessageId = messageId;

        String[] id = messageId.split("&");
        if (id[0].equals(getUid())) {
            mCurrentId = id[0];
            mFriendId = id[1];
        } else {
            mCurrentId = id[1];
            mFriendId = id[0];
        }
        //get deviceToken
        mDatabase.child(Constants.USERS).child(mFriendId).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    InfoUserFirebase infoUserFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (infoUserFirebase != null) {
                        deviceToken = infoUserFirebase.getDeviceToken();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFriendInfo() {
        mDatabase.child(Constants.USERS).child(mFriendId).child(Constants.INFO)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot != null) {
                                InfoUserFirebase infoUserFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                                if (infoUserFirebase != null) {
                                    mInfoUserFb = infoUserFirebase;
                                    mAdapter.setFriendInfo(mInfoUserFb);
                                    tvName.setText(infoUserFirebase.getName());
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getDeviceToken() {
        //get deviceToken
        mDatabase.child(Constants.USERS)
                .child(mFriendId)
                .child(Constants.INFO)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            InfoUserFirebase infoUserFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                            if (infoUserFirebase != null) {
                                deviceToken = infoUserFirebase.getDeviceToken();
                                mAdapter.setFriendInfo(infoUserFirebase);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initCurrentInfo() {
        //get deviceToken
        mDatabase.child(Constants.USERS).child(getUid()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    InfoUserFirebase infoUserFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (infoUserFirebase != null) {
                        fromName = infoUserFirebase.getName();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String message) {
        try {
            MessageHistory messageHistory = new MessageHistory();

            messageHistory.setContent(message);
            messageHistory.setFromUid(mCurrentId);
            messageHistory.setCreateAt(new Date().getTime());
            messageHistory.setIsImage(false);
            messageHistory.setIsRead(false);

            mAdapter.addNewMessage(messageHistory);
            rvRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());

            mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                    .child(mMessageId)
                    .child(Constants.FIREBASE_CHILD_HISTORY)
                    .push()
                    .setValue(messageHistory);

            updateMessageCount();

            //send push notification
            String[] regIds = {deviceToken};

            JSONArray regArray = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                regArray = new JSONArray(regIds);
            }
            PushMessage.sendMessage(regArray, fromName, etMessage.getText().toString(), "", "Chat");

            //clear edittext
            etMessage.setText("");
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    private void displayPopUp(View v) {
        try {
            MenuBuilder menuBuilder = new MenuBuilder(this);
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate(R.menu.chatfunctions, menuBuilder);
            MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, v);
            optionsMenu.setForceShowIcon(true);

            // Set Item Click Listener
            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        // Handle option1 Click
                        case R.id.optAttachPicture:
                            if (verifyStoragePermissions()) {
                                showGallery();
                            }
                            return true;
                        case R.id.optCall:
                            if (verifyPhoneCallPermission()) {
                                phoneCall();
                            }

                        default:
                            return false;
                    }
                }

                @Override
                public void onMenuModeChange(MenuBuilder menu) {
                }
            });

            // Display the menu
            optionsMenu.show();
        } catch (Exception e) {

        }
    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);

    }

    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private boolean verifyPhoneCallPermission() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_PHONE_CALL,
                    REQUEST_CODE_PHONE_CALL
            );

            return false;
        }

        return true;
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
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
                    showGallery();
                }
                break;
            case REQUEST_CODE_PHONE_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    phoneCall();
                }
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO take picture and attach pictures
        super.onActivityResult(requestCode, resultCode, intent);

        // TODO attach pictures
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != intent) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Uri uri = Uri.parse(picturePath);

            Calendar calendar = Calendar.getInstance();

            final String imageId = "image" + calendar.getTimeInMillis() + ".jpg";
            StorageReference mountainsRef = mStorageRef.child(imageId);

            InputStream stream;
            try {
                File file = new File(uri.getPath());
                stream = new FileInputStream(file);

//                MessageHistory messageHistory = new MessageHistory();
//                messageHistory.setContent(uri.getPath());
//                messageHistory.setFromUid(mCurrentId);
//                messageHistory.setCreateAt(new Date().getTime());
//                messageHistory.setIsImage(true);
//                messageHistory.setIsRead(false);
//                mAdapter.addNewMessage(messageHistory);

                UploadTask uploadTask = mountainsRef.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        MessageHistory messageHistory = new MessageHistory();
                        messageHistory.setContent(downloadUrl.toString());
                        messageHistory.setFromUid(mCurrentId);
                        messageHistory.setCreateAt(new Date().getTime());
                        messageHistory.setIsImage(true);
                        messageHistory.setIsRead(false);
                        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                                .child(mMessageId)
                                .child(Constants.FIREBASE_CHILD_HISTORY)
                                .push().setValue(messageHistory);

                        mAdapter.addNewMessage(messageHistory);
                        rvRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());

                        updateMessageCount();

                        Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        System.out.println("Download Url: " + downloadUrl);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "FileNotFoundExc", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        }
    }

    private void updateMessageCount() {
        mTotalMessage++;

        mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                .child(mMessageId)
                .child(Constants.FIREBASE_CHILD_COUNT)
                .setValue(mTotalMessage);
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
