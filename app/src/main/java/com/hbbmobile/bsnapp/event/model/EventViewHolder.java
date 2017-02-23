package com.hbbmobile.bsnapp.event.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 21/12/2016.
 */
public class EventViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTime, txtName, txtTitle, txtAddress, txtNumberChoice, txtJoin;
    public ImageView imgLogo, imgPoster;

    public EventViewHolder(View itemView) {
        super(itemView);
        txtJoin = (TextView) itemView.findViewById(R.id.txtJoin);
        txtTime = (TextView) itemView.findViewById(R.id.txt_event_time);
        txtTitle = (TextView) itemView.findViewById(R.id.txt_event_title);
        txtAddress = (TextView) itemView.findViewById(R.id.txt_event_address);
        txtNumberChoice = (TextView) itemView.findViewById(R.id.txt_event_numberOfChoice);
        // txtInfo = (TextView) itemView.findViewById(R.id.txt_thongTinDuAn);
        imgLogo = (ImageView) itemView.findViewById(R.id.img_event_logo);
        imgPoster = (ImageView) itemView.findViewById(R.id.img_event_poster);
    }
}
