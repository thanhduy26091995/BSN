package com.hbbmobile.bsnapp.friend_list.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 19/01/2017.
 */
public class FriendListViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgAvatar;
    public TextView txtPhone, txtName;


    public FriendListViewHolder(View itemView) {
        super(itemView);

        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_contacts);
        txtPhone = (TextView) itemView.findViewById(R.id.txt_phone_contacts);
        txtName = (TextView) itemView.findViewById(R.id.txt_name_contacts);
    }
}
