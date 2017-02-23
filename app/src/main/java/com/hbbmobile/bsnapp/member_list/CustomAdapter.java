package com.hbbmobile.bsnapp.member_list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by buivu on 06/02/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SINGLE_CHAT  = 0;
    private static final int GROUP_CHAT = 1;


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
