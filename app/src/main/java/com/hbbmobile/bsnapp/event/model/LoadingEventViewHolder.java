package com.hbbmobile.bsnapp.event.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 22/12/2016.
 */
public class LoadingEventViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;
    public LoadingEventViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_item_event);
    }
}
