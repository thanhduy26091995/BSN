package com.hbbmobile.bsnapp.member_around_here.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 11/01/2017.
 */
public class MemberAroundHereViewHolder extends RecyclerView.ViewHolder {
    public TextView txtName, txtDistance;
    public ImageView imgAvatar;

    public MemberAroundHereViewHolder(View itemView) {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.txt_name_around_here);
        txtDistance = (TextView) itemView.findViewById(R.id.txt_distance_member_around);
        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_around_here);
    }
}
