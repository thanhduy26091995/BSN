package com.hbbmobile.bsnapp.event;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.event.model.Event;
import com.hbbmobile.bsnapp.event.model.EventViewHolder;
import com.hbbmobile.bsnapp.event.model.LoadingEventViewHolder;
import com.hbbmobile.bsnapp.event.view.DetailEventActivity;
import com.hbbmobile.bsnapp.utils.Constants;

import java.util.List;

/**
 * Created by buivu on 21/12/2016.
 */
public class CustomEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Event> listEvent;
    private Activity activity;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private static final int REQUEST_CODE_PHONE_CALL = 1001;
    private static final String[] PERMISSIONS_PHONE_CALL = {
            Manifest.permission.CALL_PHONE
    };

    public CustomEventAdapter(Activity activity, List<Event> listEvent, RecyclerView mRecycler) {
        this.activity = activity;
        this.listEvent = listEvent;
        this.mRecycler = mRecycler;
        final LinearLayoutManager mManager = (LinearLayoutManager) mRecycler.getLayoutManager();

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = mManager.getItemCount();
                lastVisibleItem = mManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return listEvent.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.itemevent, parent, false);
            return new EventViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_event_loading, parent, false);
            return new LoadingEventViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            final Event event = listEvent.get(position);
            EventViewHolder eventViewHolder = (EventViewHolder) holder;
            //load data
            Glide.with(activity)
                    .load(event.getLogoEvent())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(eventViewHolder.imgLogo);
            Glide.with(activity)
                    .load(event.getPoster())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(eventViewHolder.imgPoster);
            eventViewHolder.txtTitle.setText(event.getTitle());
            eventViewHolder.txtTime.setText(String.format("%s | %s", event.getDate(), event.getTime()));
            eventViewHolder.txtAddress.setText(event.getAddress());
            eventViewHolder.txtNumberChoice.setText(String.format("%s people joined", event.getNumberChoice()));
            // holder.txtInfo.setText(event.getThongTin());
            //event click item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(activity, DetailEventActivity.class);
                    myIntent.putExtra(DetailEventActivity.EXTRA_UID, event.getId());
                    activity.startActivity(myIntent);
                    //  Toast.makeText(activity, event.getTenDuAn().toString(), Toast.LENGTH_LONG).show();
                }
            });
            //event click join
            eventViewHolder.txtJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show popup
                    showPopup();
                }
            });
        } else if (holder instanceof LoadingEventViewHolder) {
            LoadingEventViewHolder loadingEventViewHolder = (LoadingEventViewHolder) holder;
            loadingEventViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_PHONE_CALL,
                    REQUEST_CODE_PHONE_CALL
            );

            return false;
        }

        return true;
    }


    private void showPopup() {
        final Dialog dialog = new Dialog(activity);
        // khởi tạo dialog
        dialog.setContentView(R.layout.custom_dialog_join);

        TextView txtCall = (TextView) dialog.findViewById(R.id.txt_call);
        TextView txtEmail = (TextView) dialog.findViewById(R.id.txt_email);
        txtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions()) {
                    phoneCall();
                }
                dialog.dismiss();
            }
        });
        // khai báo control trong dialog để bắt sự kiện
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                dialog.dismiss();
            }
        });
        // bắt sự kiện cho nút đăng kí
        dialog.show();
    }

    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Constants.EMAIL_BSN, null));
        activity.startActivity(i);
    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.PHONE_BSN));

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        activity.startActivity(intent);

    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return listEvent.size();
    }
}
