package com.hbbmobile.bsnapp.list_group.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 06/02/2017.
 */

public class ListGroupViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMember, txtLastMess;
    public ImageView imgAvatar;


    public ListGroupViewHolder(View itemView) {
        super(itemView);

        txtMember = (TextView) itemView.findViewById(R.id.txt_member_group);
        txtLastMess = (TextView) itemView.findViewById(R.id.txt_last_message_group);
        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_group);
    }
}
