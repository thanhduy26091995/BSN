package com.hbbmobile.bsnapp.list_group;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.chat_group.ChatGroupActivity;
import com.hbbmobile.bsnapp.list_group.model.ListGroupViewHolder;
import com.hbbmobile.bsnapp.model_firebase.GroupInfo;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 06/02/2017.
 */

public class CustomListGroupAdapter extends RecyclerView.Adapter<ListGroupViewHolder> {
    private Activity mActivity;
    private List<String> listGroupId;
    private DatabaseReference mDatabase;

    public CustomListGroupAdapter(Activity mActivity, List<String> listGroupId) {
        this.mActivity = mActivity;
        this.listGroupId = listGroupId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_chat_group, null);
        return new ListGroupViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ListGroupViewHolder holder, int position) {
        final String groupId = listGroupId.get(position);
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GroupInfo groupInfo = dataSnapshot.getValue(GroupInfo.class);
                    if (groupInfo != null) {
                        if (groupInfo.getGroupName() != null && groupInfo.getGroupAvatar() != null) {
                            holder.txtMember.setText(groupInfo.getGroupName());
                            com.hbbmobile.bsnapp.base.view.ImageLoader.getInstance().loadImageCaching(mActivity, groupInfo.getGroupAvatar(), holder.imgAvatar);
                        }
                    }

                } else {
                    loadData(groupId, holder);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ChatGroupActivity.class);
                intent.putExtra(ChatGroupActivity.GROUP_ID, groupId);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listGroupId.size();
    }

    private void loadData(String groupId, final ListGroupViewHolder holder) {
        mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    List<String> listMember = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String member = snapshot.getKey();
                        listMember.add(member);
                        mDatabase.child(Constants.USERS).child(member).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                                    if (userFirebase != null) {
                                        holder.txtMember.append(userFirebase.getName() + ", ");
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
