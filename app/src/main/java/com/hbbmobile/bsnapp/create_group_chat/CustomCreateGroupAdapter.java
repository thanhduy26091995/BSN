package com.hbbmobile.bsnapp.create_group_chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.create_group_chat.model.CreateChatGroupViewHolder;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buivu on 06/02/2017.
 */

public class CustomCreateGroupAdapter extends RecyclerView.Adapter<CreateChatGroupViewHolder> {
    private Activity mActivity;
    private List<String> listUserId;
    private List<String> listMember;
    private DatabaseReference mDatabase;
    private static Boolean isTouched = false;
    private LinearLayout photoContainer;
    public ProgressDialog mProgressDialog;

    public CustomCreateGroupAdapter(Activity mActivity, List<String> listUserId, List<String> listMember) {
        this.mActivity = mActivity;
        this.listUserId = listUserId;
        this.listMember = listMember;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        photoContainer = (LinearLayout) mActivity.findViewById(R.id.photoContainer);
    }

    @Override
    public CreateChatGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_create_group, null);
        return new CreateChatGroupViewHolder(rootView);
    }


    @Override
    public void onBindViewHolder(final CreateChatGroupViewHolder holder, int position) {
        showProgessDialog();
        final String strId = listUserId.get(position);
        mDatabase.child(Constants.USERS).child(strId).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    hideProgressDialog();
                    final InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                    if (userFirebase != null) {
                        holder.txtName.setText(userFirebase.getName());
                        com.hbbmobile.bsnapp.base.view.ImageLoader.getInstance().loadImage(mActivity, userFirebase.getAvatar(), holder.imgAvatar);
                        int previewImageSize = 250;
                        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
                        params.setMargins(5, 5, 5, 5);
                        //event click checkbox
                        holder.chkCheck.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                isTouched = true;
                                return false;
                            }
                        });
                        holder.chkCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (isTouched) {
                                    isTouched = false;
                                    if (!isChecked) {
                                        // photoContainer.removeAllViews();
                                        int index = findIndex(strId);
                                        listMember.remove(strId);
                                        photoContainer.removeViewAt(index);
                                    } else {
                                        CircleImageView photo = new CircleImageView(mActivity);
                                        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        photo.setLayoutParams(params);
                                        //using Glide to load image
                                        Glide.with(mActivity)
                                                .load(userFirebase.getAvatar())
                                                .centerCrop()
                                                .error(R.drawable.avatar)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .centerCrop()
                                                .into(photo);
                                        photoContainer.addView(photo);
                                        listMember.add(strId);

                                    }
                                }

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUserId.size();
    }

    private int findIndex(String id) {
        int index = 0;
        for (int i = 0; i < listMember.size(); i++) {
            if (listMember.get(i).equals(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage(mActivity.getResources().getString(R.string.loading));
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
