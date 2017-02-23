package com.hbbmobile.bsnapp.request.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 18/01/2017.
 */
public class RequestsViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgAvatar;
    public TextView txtName, txtPhone;
    public LinearLayout linearAccept, linearDecline;


    public RequestsViewHolder(View itemView) {
        super(itemView);

        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_request);
        txtName = (TextView) itemView.findViewById(R.id.txt_name_request);
        txtPhone = (TextView) itemView.findViewById(R.id.txt_phone_request);
        linearAccept = (LinearLayout) itemView.findViewById(R.id.linear_accept);
        linearDecline = (LinearLayout) itemView.findViewById(R.id.linear_decline);
    }
}
