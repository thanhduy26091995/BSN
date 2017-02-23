package com.hbbmobile.bsnapp.chat_group.group_info.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 10/02/2017.
 */

public class ChatGroupInfoViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMemberName;
    public ImageView imgMemberAvatar;
    public RelativeLayout relaMore;

    public ChatGroupInfoViewHolder(View itemView) {
        super(itemView);
        txtMemberName = (TextView) itemView.findViewById(R.id.txt_member_name);
        imgMemberAvatar = (ImageView) itemView.findViewById(R.id.img_member_avatar);
        relaMore = (RelativeLayout) itemView.findViewById(R.id.rela_more);
    }
}
