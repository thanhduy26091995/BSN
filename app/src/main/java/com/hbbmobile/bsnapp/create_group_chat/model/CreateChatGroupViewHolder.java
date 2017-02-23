package com.hbbmobile.bsnapp.create_group_chat.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 06/02/2017.
 */

public class CreateChatGroupViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName;
    public ImageView imgAvatar;
    public CheckBox chkCheck;


    public CreateChatGroupViewHolder(View itemView) {
        super(itemView);
        //init
        txtName = (TextView) itemView.findViewById(R.id.txt_username_create_group);
        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_create_group);
        chkCheck = (CheckBox) itemView.findViewById(R.id.chk_create_group);
    }
}
