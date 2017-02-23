package com.hbbmobile.bsnapp.chat_group;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.base.view.IconTextView;
import com.hbbmobile.bsnapp.chat_group.group_info.view.ChatGroupInfoActivity;
import com.hbbmobile.bsnapp.model_firebase.GroupInfo;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hbbmobile.bsnapp.utils.EncodeImage.encodeImage;

/**
 * Created by buivu on 06/02/2017.
 */

public class ChatGroupActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecycler;
    private Button btnSend;
    private EditText etMessage;
    private ImageView ivSettings;
    private TextView tvName, txtGroupInfo;
    private IconTextView itvBack;
    private DatabaseReference mDatabase;
    private ChatGroupAdapter mAdapter;
    private MyLinearLayoutManager customManager;
    private List<MessageGroup> mMessageHistoryList;
    private List<MessageGroup> mReloadMessage;
    private int mMessageCount;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int NUMBER_IMAGE_LOADING_ON_START = 20;
    private static int NUMBER_IMAGE_LOADING_ON_PROGRESS = 10;
    private boolean isStartLoading = true;
    private String mKeyStart;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mCount = 0;
    private List<String> listMember;
    private static final String TAG = "ChatGroupActivity";
    public static final String GROUP_ID = "groupId";
    private String groupId = "";
    public static Activity mActivity;
    private int mTotalMessage = 0;
    private static int NUMBER_MESSAGE_LOADING_ON_START = 20;
    private static int NUMBER_MESSAGE_LOADING_ON_HISTORY = 20;
    private String mKeyEnd = null;
    private static final int REQUEST_CODE_READ_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private StorageReference mStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        mActivity = this;
        doFormWidget();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        customManager = new MyLinearLayoutManager(this);
        mMessageHistoryList = new ArrayList<>();
        mReloadMessage = new ArrayList<>();
        listMember = new ArrayList<>();
        mAdapter = new ChatGroupAdapter(mMessageHistoryList, listMember, getUid(), this);
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(100);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycler.setLayoutManager(customManager);
        mRecycler.setAdapter(mAdapter);

        //getMemberInfo();
        groupId = getIntent().getStringExtra(GROUP_ID);
        initInfo();
        getMessageHistory(groupId);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mReloadMessage = new ArrayList<>();
                isStartLoading = true;
                refreshItems();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        mRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSend) {
            String message = etMessage.getText().toString().trim();
            if (message.length() != 0) {
                sendMessage(message);
            } else {
                etMessage.setError(getResources().getString(R.string.require));
            }
        } else if (v == itvBack) {
            finish();
        } else if (v == txtGroupInfo) {
            Intent intent = new Intent(ChatGroupActivity.this, ChatGroupInfoActivity.class);
            intent.putExtra(ChatGroupInfoActivity.GROUP_ID, groupId);
            startActivity(intent);
        } else if (v == ivSettings) {
            displayPopUp(v);
        }
    }

    private void initInfo() {
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    GroupInfo groupInfo = dataSnapshot.getValue(GroupInfo.class);
                    if (groupInfo != null) {
                        tvName.setText(groupInfo.getGroupName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMemberInfo() {
        final int[] i = {0};
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    final List<Boolean> result = new ArrayList<>();
                    result.add(true);

                    for (int i = 0; i < listMember.size(); i++) {
                        if (dataSnapshot.getKey().equals(listMember.get(i))) {
                            result.set(0, false);
                        }
                    }
                    if (result.get(0)) {
                        listMember.add(dataSnapshot.getKey());
                        mAdapter.createMapData(listMember);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listMember.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        mAdapter.loadingHistory(mReloadMessage);

        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);

        mReloadMessage.clear();
    }

    private void refreshItems() {
        mCount = 0;
        if (mMessageCount == 0) {
            swipeRefreshLayout.setRefreshing(false);
            // Toast.makeText(this.getBaseContext(), "No message left", Toast.LENGTH_SHORT).show();
        } else if (mMessageCount - NUMBER_MESSAGE_LOADING_ON_HISTORY > 0) {
            mDatabase.child(Constants.GROUPS)
                    .child(groupId)
                    .child(Constants.CONTENTS)
                    .endAt(null, mKeyStart)
                    .limitToLast(NUMBER_MESSAGE_LOADING_ON_HISTORY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                MessageGroup messageGroup = item.getValue(MessageGroup.class);

                                if (mCount < NUMBER_MESSAGE_LOADING_ON_HISTORY - 1) {
                                    mReloadMessage.add(messageGroup);
                                    mCount++;
                                } else {
                                    mCount = 0;
                                    onItemsLoadComplete();
                                    mMessageCount = mMessageCount - NUMBER_MESSAGE_LOADING_ON_HISTORY + 1;
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
            mDatabase.child(Constants.GROUPS)
                    .child(groupId)
                    .child(Constants.CONTENTS)
                    .endAt(null, mKeyStart)
                    .limitToLast(mMessageCount)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                MessageGroup messageGroup = item.getValue(MessageGroup.class);

                                if (mCount < mMessageCount - 1) {
                                    mReloadMessage.add(messageGroup);
                                    mCount++;
                                } else {
                                    onItemsLoadComplete();
                                    mMessageCount = mMessageCount - mMessageCount;
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
                        case R.id.optTakePicture:
                            if (verifyOpenCamera()) {
                                openCamera();
                            }
                            return true;

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

    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.GALLERY_INTENT);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }
    }

    private boolean verifyOpenCamera() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA
            );

            return false;
        }
        return true;
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE, REQUEST_CODE_READ_STORAGE
            );

            return false;
        }
        return true;
    }

    private void sendMessage(String message) {
        try {

            MessageGroup messageGroup = new MessageGroup();
            messageGroup.setContent(message);
            messageGroup.setCreateAt(new Date().getTime());
            messageGroup.setIsImage(false);
            messageGroup.setSendBy(getUid());

            mDatabase.child(Constants.GROUPS)
                    .child(groupId)
                    .child(Constants.CONTENTS)
                    .push()
                    .setValue(messageGroup);

            updateMessageCount();
            //clear edittext
            etMessage.setText("");
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    private void doFormWidget() {
        txtGroupInfo = (TextView) findViewById(R.id.txt_group_info);
        itvBack = (IconTextView) findViewById(R.id.txtBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        mRecycler = (RecyclerView) findViewById(R.id.rvRecylerView);
        tvName = (TextView) findViewById(R.id.txt_group_name);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);
        //event click
        btnSend.setOnClickListener(this);
        itvBack.setOnClickListener(this);
        txtGroupInfo.setOnClickListener(this);
        ivSettings.setOnClickListener(this);

    }


    private void getMessageHistory(final String id) {
        mDatabase.child(Constants.GROUPS)
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
        mDatabase.child(Constants.GROUPS)
                .child(id)
                .child(Constants.CONTENTS)
                .limitToLast(NUMBER_MESSAGE_LOADING_ON_START)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int i = 0;
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            if (i < NUMBER_MESSAGE_LOADING_ON_START) {
                                MessageGroup messageGroup = item.getValue(MessageGroup.class);
                                mAdapter.addNewMess(messageGroup);
                                mRecycler.smoothScrollToPosition(mAdapter.getItemCount());
                                mKeyEnd = item.getKey();
                                if (isStartLoading) {
                                    mKeyStart = item.getKey();
                                    isStartLoading = false;
                                }
                                i++;
                            }
                        }

                        mDatabase.child(Constants.GROUPS)
                                .child(id)
                                .child(Constants.CONTENTS)
                                .startAt(null, mKeyEnd)
                                .limitToLast(1)
                                .addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        if (!dataSnapshot.getKey().equals(mKeyEnd)) {
                                            MessageGroup messageGroup = dataSnapshot.getValue(MessageGroup.class);
                                            mAdapter.addNewMess(messageGroup);
                                            mRecycler.smoothScrollToPosition(mAdapter.getItemCount());
                                            if (!messageGroup.getSendBy().equals(getUid())) {
                                                mTotalMessage++;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void updateMessageCount() {
        mTotalMessage++;

        mDatabase.child(Constants.GROUPS)
                .child(groupId)
                .child(Constants.FIREBASE_CHILD_COUNT)
                .setValue(mTotalMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            try {
                String filePath = getRealPathFromURI(data.getData());
                byte[] bitmapImage = encodeImage(filePath);
                //upload to server then get path
                String timestamp = String.valueOf(new Date().getTime() / 1000);
                String fileName = String.format("%s%s", getUid(), timestamp);
                StorageReference filePathImageChat = mStorage.child(Constants.CHAT_GROUP).child(fileName);
                filePathImageChat.putBytes(bitmapImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        MessageGroup messageGroup = new MessageGroup();
                        messageGroup.setContent(taskSnapshot.getDownloadUrl().toString());
                        messageGroup.setCreateAt(new Date().getTime());
                        messageGroup.setIsImage(true);
                        messageGroup.setSendBy(getUid());
                        //add image
                        mDatabase.child(Constants.GROUPS)
                                .child(groupId)
                                .child(Constants.CONTENTS)
                                .push()
                                .setValue(messageGroup);

                        updateMessageCount();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(getResources().getString(R.string.throwError));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            try {
                String filePath = getRealPathFromURI(data.getData());
                byte[] bitmapImage = encodeImage(filePath);
                //upload to server then get path
                String timestamp = String.valueOf(new Date().getTime() / 1000);
                String fileName = String.format("%s%s", getUid(), timestamp);
                StorageReference filePathImageChat = mStorage.child(Constants.CHAT_GROUP).child(fileName);
                filePathImageChat.putBytes(bitmapImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        MessageGroup messageGroup = new MessageGroup();
                        messageGroup.setContent(taskSnapshot.getDownloadUrl().toString());
                        messageGroup.setCreateAt(new Date().getTime());
                        messageGroup.setIsImage(true);
                        messageGroup.setSendBy(getUid());
                        //add image
                        mDatabase.child(Constants.GROUPS)
                                .child(groupId)
                                .child(Constants.CONTENTS)
                                .push()
                                .setValue(messageGroup);

                        updateMessageCount();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(getResources().getString(R.string.throwError));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String mess) {
        Toast.makeText(ChatGroupActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}

