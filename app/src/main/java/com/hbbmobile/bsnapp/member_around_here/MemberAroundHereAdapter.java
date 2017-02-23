package com.hbbmobile.bsnapp.member_around_here;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.find_user.view.FriendProfileActivity;
import com.hbbmobile.bsnapp.member_around_here.model.MemberAroundHereViewHolder;
import com.hbbmobile.bsnapp.model.GPS;
import com.hbbmobile.bsnapp.model.User;
import com.hbbmobile.bsnapp.model.UserResponse;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 11/01/2017.
 */
public class MemberAroundHereAdapter extends RecyclerView.Adapter<MemberAroundHereViewHolder> {
    private List<GPS> listGps;
    private Activity mActivity;
    public ProgressDialog mProgressDialog;

    public MemberAroundHereAdapter(List<GPS> listGps, Activity mActivity) {
        this.listGps = listGps;
        this.mActivity = mActivity;
    }

    @Override
    public MemberAroundHereViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_member_around, null);
        return new MemberAroundHereViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MemberAroundHereViewHolder holder, int position) {
        final GPS gps = listGps.get(position);
        //load data
        holder.txtName.setText(gps.getFullName());
        holder.txtDistance.setText(String.format("%s km", gps.getDistance()));
        Glide.with(mActivity)
                .load(gps.getAvatar())
                .error(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .centerCrop()
                .into(holder.imgAvatar);
        //event click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgessDialog();
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<UserResponse> responseCall = apiService.getUser(gps.getIdfb());
                responseCall.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        hideProgressDialog();
                        String error = response.body().getError();
                        if (error.equals("false")) {
                            User user = response.body().getListUser().get(0);
                            Intent myIntent = new Intent(mActivity, FriendProfileActivity.class);
                            myIntent.putExtra("User", user);
                            mActivity.startActivity(myIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        ShowAlertDialog.showAlert("Cannot load his/here profile! Please try it again", mActivity);
                        hideProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listGps.size();
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
