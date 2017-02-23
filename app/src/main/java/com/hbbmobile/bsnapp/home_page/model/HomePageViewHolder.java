package com.hbbmobile.bsnapp.home_page.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 11/11/2016.
 */
public class HomePageViewHolder extends RecyclerView.ViewHolder {
    public TextView txtFramework, txtProjectName, txtDescription, txtPrice;
    public ImageView imgPic;

    public HomePageViewHolder(View itemView) {
        super(itemView);
        //mapping
        txtFramework = (TextView) itemView.findViewById(R.id.txt_framework);
        txtProjectName = (TextView) itemView.findViewById(R.id.txt_projectName);
        txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
        txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        imgPic = (ImageView) itemView.findViewById(R.id.img_project_pic);
    }
}
