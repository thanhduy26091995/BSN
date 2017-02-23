package com.hbbmobile.bsnapp.member_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.chat.ChatActivity;
import com.hbbmobile.bsnapp.chat.MessageHistory;
import com.hbbmobile.bsnapp.member_list.model.Friend;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.Collections;
import java.util.List;

/**
 * Created by Doma Umaru on 12/29/2016.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.RecyclerViewHolder> {
    private List<Friend> data = Collections.EMPTY_LIST;
    private DatabaseReference mDatabase;
    private Context context;

    //TODO
    // private List<String> listGroupId;

    public MessageListAdapter(List<Friend> data, Context context) {
        this.data = data;
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

//    //TODO
//    public MessageListAdapter(List<Friend> data, Context context, List<String> listGroupId) {
//        this.data = data;
//        this.context = context;
//        this.listGroupId = listGroupId;
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//    }

    public void addFriend(Friend friend) {
        data.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_list_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        try {
            final Friend friend = data.get(position);
            holder.getFriend(friend);

            holder.tvUserName.setText(friend.getFriendInfo().getName());
            mDatabase.child(Constants.FIREBASE_CHILD_MESSAGES)
                    .child(friend.getMessageId())
                    .child(Constants.FIREBASE_CHILD_HISTORY)
                    .limitToLast(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (com.google.firebase.database.DataSnapshot item : dataSnapshot.getChildren()) {
                                    MessageHistory messageHistory = item.getValue(MessageHistory.class);
                                    if (messageHistory != null) {
                                        if (messageHistory.getIsImage()) {
                                            if (messageHistory.getFromUid().equals(BaseActivity.getUid())) {
                                                holder.tvLastMessage.setText(String.format("%s", context.getResources().getString(R.string.sentImage)));
                                            } else {
                                                holder.tvLastMessage.setText(String.format("%s", context.getResources().getString(R.string.receiveImage)));
                                            }
                                        } else {
                                            holder.tvLastMessage.setText(messageHistory.getContent());
                                        }

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            //TODO
//            String groupId = listGroupId.get(position);
//            Log.d("GROUP", groupId);
//            mDatabase.child(Constants.GROUPS).child(groupId).child(Constants.MEMBERS).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot != null) {
//                        List<String> listMember = new ArrayList<String>();
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            String member = snapshot.getKey();
//                            listMember.add(member);
//                            mDatabase.child(Constants.USERS).child(member).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot != null) {
//                                        InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
//                                        if (userFirebase != null) {
//                                            holder.tvUserName.append(userFirebase.getName() + ", ");
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUserName, tvLastMessage;
        private final Context mContext;

        public RecyclerViewHolder(View view) {
            super(view);
            mContext = view.getContext();
            tvUserName = (TextView) view.findViewById(R.id.message_list_row_tvName);
            tvLastMessage = (TextView) view.findViewById(R.id.message_list_row_tvLastMessage);
            view.setOnClickListener(this);
        }

        Friend mFriend;

        public void getFriend(Friend friend) {
            mFriend = friend;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ChatActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString(Constants.MESSAGE_ID, mFriend.getMessageId());
            bundle.putString(Constants.PHONE, mFriend.getFriendInfo().getPhone());
            Log.d("PHONEMESSAGE", mFriend.getFriendInfo().getPhone() + "/" + mFriend.getMessageId());

            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }
}
