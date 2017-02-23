package com.hbbmobile.bsnapp.chat_group;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.IconTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buivu on 06/02/2017.
 */

public class ChatGroupViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMessage, tv_item_chat_message_nameText, tv_myContentText, tvMyMessageStatus, tvFriendMessageStatus, tvName;

    public IconTextView itvArrow, itvRightArrow;
    //        public ImageView ivAvatar;
    public ImageView ivImage, myImage;

    public CircleImageView civAvatar;

    public RelativeLayout rChat;
    public RelativeLayout r_myChat;
    public MessageGroup item;
    public ChatGroupViewHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tv_item_chat_message_nameText);
        tvMessage = (TextView) itemView.findViewById(R.id.tv_item_chat_message_contentText);
//            tv_item_chat_message_nameText = (TextView) itemView.findViewById(R.id.tv_item_chat_message_nameText);
        tv_myContentText = (TextView) itemView.findViewById(R.id.tv_myContentText);
//            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_chat_profileImage);
        ivImage = (ImageView) itemView.findViewById(R.id.layout_chat_ivImage);
        civAvatar = (CircleImageView) itemView.findViewById(R.id.iv_chat_profileImage);
        itvArrow = (IconTextView) itemView.findViewById(R.id.iv_arrow);
        itvRightArrow = (IconTextView) itemView.findViewById(R.id.iv_right_arrow);
        rChat = (RelativeLayout) itemView.findViewById(R.id.rChat);
        r_myChat = (RelativeLayout) itemView.findViewById(R.id.r_myChat);
        myImage = (ImageView) itemView.findViewById(R.id.iv_myImage);
        tvMyMessageStatus = (TextView) itemView.findViewById(R.id.myMessage_status);
        tvFriendMessageStatus = (TextView) itemView.findViewById(R.id.friendMessage_status);
    }

    public void setItem(MessageGroup item) {
        this.item = item;
    }
}
