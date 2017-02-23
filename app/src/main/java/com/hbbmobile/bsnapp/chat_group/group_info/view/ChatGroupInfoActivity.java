package com.hbbmobile.bsnapp.chat_group.group_info.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.base.view.ImageLoader;
import com.hbbmobile.bsnapp.chat_group.ChatGroupActivity;
import com.hbbmobile.bsnapp.chat_group.add_more_member.view.AddMoreMemberActivity;
import com.hbbmobile.bsnapp.chat_group.group_info.CustomGroupInfoAdapter;
import com.hbbmobile.bsnapp.chat_group.group_info.presenter.ChatGroupInfoPresenter;
import com.hbbmobile.bsnapp.model_firebase.GroupInfo;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by buivu on 09/02/2017.
 */

public class ChatGroupInfoActivity extends BaseActivity implements View.OnClickListener {

    private ChatGroupInfoPresenter presenter;
    private String groupName;
    public static final String GROUP_ID = "groupId";
    private String groupId = "";
    private DatabaseReference mDatabase;
    private TextView txtGroupName, txtTitleGroup;
    private ImageView imgGroupAvatar;
    private RecyclerView mRecycler;
    private CustomGroupInfoAdapter customAdapter;
    private MyLinearLayoutManager customManager;
    private List<String> listMember;
    public static Activity mActivity;
    private RelativeLayout relaAddMember, relaGroupName;
    private List<String> listFriend;
    private HashMap<String, Long> hashData;
    private List<String> strAdmin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_info);
        hashData = new HashMap<>();

        //save instance
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        //init
        presenter = new ChatGroupInfoPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        groupId = getIntent().getStringExtra(GROUP_ID);
        listMember = new ArrayList<>();
        strAdmin = new ArrayList<>();
        customAdapter = new CustomGroupInfoAdapter(this, listMember, strAdmin, groupId);
        customManager = new MyLinearLayoutManager(this);
        mRecycler.setLayoutManager(customManager);
        mRecycler.setAdapter(customAdapter);
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(20);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        initInfo();
        loadData();

        listFriend = new ArrayList<>();
        getAll();


    }

    @Override
    public void onClick(View v) {
        if (v == relaAddMember) {
            listFriend.removeAll(listMember);
            Intent intent = new Intent(ChatGroupInfoActivity.this, AddMoreMemberActivity.class);
            String[] strArray = (String[]) listFriend.toArray(new String[0]);
            intent.putExtra("data", strArray);
            intent.putExtra(AddMoreMemberActivity.GROUP_ID, groupId);
            startActivity(intent);
        } else if (v == relaGroupName) {
            changeGroupName();
        }
    }

    private void getAll() {
        mDatabase.child(Constants.USERS).child(getUid()).child(Constants.FRIENDS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    listFriend.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // listFriend.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData() {
        showProgessDialog();
        presenter.getAllMember(groupId).addChildEventListener(new ChildEventListener() {
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
                        // Log.d("KEY_VALUE", dataSnapshot.getKey() + "/" + dataSnapshot.getValue());
                        hashData.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());
                        if ((Long) dataSnapshot.getValue() == 1) {
                            strAdmin.add(0, dataSnapshot.getKey());
                            //Log.d("CHATGROUP", dataSnapshot.getKey() + "/" + dataSnapshot.getValue() + "/" + strAdmin + "/" + getUid());
                        }
                        customAdapter.notifyDataSetChanged();
                    }
                    hideProgressDialog();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listMember.remove(dataSnapshot.getKey());
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

    private void initViews() {
        relaGroupName = (RelativeLayout) findViewById(R.id.rela_group_name);
        relaAddMember = (RelativeLayout) findViewById(R.id.rela_add_member);
        txtGroupName = (TextView) findViewById(R.id.txt_group_name);
        imgGroupAvatar = (ImageView) findViewById(R.id.img_group_avatar);
        mRecycler = (RecyclerView) findViewById(R.id.frane_all_member);
        //event click
        relaAddMember.setOnClickListener(this);
        relaGroupName.setOnClickListener(this);
    }

    private void initInfo() {
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    GroupInfo groupInfo = dataSnapshot.getValue(GroupInfo.class);
                    if (groupInfo != null) {
                        groupName = groupInfo.getGroupName();
                        txtGroupName.setText(groupName);
                        ImageLoader.getInstance().loadImageCaching(ChatGroupInfoActivity.this, groupInfo.getGroupAvatar(), imgGroupAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setttings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //event click toolbae
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_change_name) {
            changeGroupName();
        } else if (item.getItemId() == R.id.action_change_avatar) {
            selectImage();
        } else if (item.getItemId() == R.id.action_leave_group) {
            showAlertConfirm();
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectImage() {
        final CharSequence[] options = {getResources().getString(R.string.openCamera), getResources().getString(R.string.openGallery)};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ChatGroupInfoActivity.this);
        builder.setTitle(getResources().getString(R.string.change_group_picture));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getResources().getString(R.string.openCamera))) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals(getResources().getString(R.string.openGallery))) {
                    Intent myIntent = new Intent(Intent.ACTION_PICK);
                    myIntent.setType("image/*");
                    startActivityForResult(myIntent, Constants.GALLERY_INTENT);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void showAlertConfirm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ChatGroupInfoActivity.this);
        builder.setMessage(R.string.confirmOutGroup)
                .setTitle(R.string.outGroup)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.outGroup(groupId, getUid());
                        //close 2 activity
                        finish();
                        if (ChatGroupActivity.mActivity != null) {
                            ChatGroupActivity.mActivity.finish();
                        }
                    }
                })
                .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    private void changeGroupName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_alertdialog_group_name, null);
        builder.setView(v);
        //get instance of view in custom-alert-dialog
        final EditText edtGroupName = (EditText) v.findViewById(R.id.edt_group_name);
        //hiển thị data
        edtGroupName.setText(groupName);
        //create button Ok
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtGroupName.setText(edtGroupName.getText());
                presenter.updateGroupName(groupId, edtGroupName.getText().toString());
            }
        });
        //create button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Glide.with(ChatGroupInfoActivity.this)
                        .load(data.getData().toString())
                        .centerCrop()
                        .into(imgGroupAvatar);
                //update
                Constants.AVATAR_GROUP_CHAT = getRealPathFromURI(data.getData());
                presenter.updateGroupAvatar(groupId);
            }
        } else if (requestCode == Constants.GALLERY_INTENT) {
            if (resultCode == RESULT_OK) {
                Glide.with(ChatGroupInfoActivity.this)
                        .load(data.getData().toString())
                        .centerCrop()
                        .into(imgGroupAvatar);
                //update
                Constants.AVATAR_GROUP_CHAT = getRealPathFromURI(data.getData());
                presenter.updateGroupAvatar(groupId);
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}
