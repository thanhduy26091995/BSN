package com.hbbmobile.bsnapp.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.IconTextView;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Doma Umaru on 1/3/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.RecyclerViewHolder> {
    private List<MessageHistory> data = Collections.EMPTY_LIST;
    private String mCurrentUser;
    private Context mContext;
    private ImageLoaderConfiguration mConfig;
    private DisplayImageOptions mOptions;
    private ImageLoader mImageLoader, myImageLoader;
    private ImageLoader mAvatarLoader;
    private File cacheDir;
    private InfoUserFirebase mFriendInfo;
    private String avatarUrl;

    public ChatAdapter(List<MessageHistory> data, String currentUser, Context context) {
        this.data = data;
        this.mCurrentUser = currentUser;
//        mCurrentUser = "AB";
        mContext = context;
        mImageLoader = ImageLoader.getInstance();
        mAvatarLoader = ImageLoader.getInstance();

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "BSNFolder");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        configImageLoader(context);
        configAvatarLoader(context);
    }

    private void configImageLoader(Context context) {
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_loading)
                .showImageOnFail(R.drawable.ic_imgloadfailed)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        mConfig = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiskCache(cacheDir))
                .threadPoolSize(5)
                .defaultDisplayImageOptions(mOptions)
                .build();

        mImageLoader.init(mConfig);
    }

    private void configAvatarLoader(Context context) {
        mOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.img_loading)
//                .showImageOnFail(R.drawable.ic_imgloadfailed)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        mConfig = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiskCache(cacheDir))
                .threadPoolSize(5)
                .defaultDisplayImageOptions(mOptions)
                .build();

        mAvatarLoader.init(mConfig);
    }

    public void updateMessageStatus() {
        if (getItemCount() >= 2) {
            data.get(getItemCount() - 2).setDisplayMessageStatus(false);
            data.get(getItemCount() - 1).setDisplayMessageStatus(true);
        } else if (getItemCount() > 0) {
            data.get(getItemCount() - 1).setDisplayMessageStatus(true);
        }
        notifyDataSetChanged();
    }

    public void updateIsRead() {
        data.get(getItemCount() - 1).setIsRead(true);
        notifyDataSetChanged();
    }

    public void addNewMessage(MessageHistory model) {
        model.setDisplayMessageStatus(false);
        data.add(model);
        updateMessageStatus();
    }

    public void loadingHistory(List<MessageHistory> model) {
        Collections.reverse(model);
        for (MessageHistory history : model) {
            history.setDisplayMessageStatus(false);
            data.add(0, history);
        }
        updateMessageStatus();
        model.clear();
    }

    public void setFriendInfo(InfoUserFirebase info) {
        mFriendInfo = info;
        if (mFriendInfo.getAvatar() != null && !mFriendInfo.getAvatar().equals("")) {
            avatarUrl = mFriendInfo.getAvatar();
        } else {
            avatarUrl = "";
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.message_row, parent, false);
        return new RecyclerViewHolder(itemview);
    }

    private boolean checkMessage(int position, String currentId) {
        if (position > 0) {
            String previousId = data.get(position - 1).getFromUid();
            if (currentId.equals(previousId)) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        try {
            MessageHistory item = data.get(position);
            holder.setItem(item);

            String currentUser = item.getFromUid();
            if (item.getFromUid().equals(mCurrentUser)) {
                displayMyMessage(holder, item, checkMessage(position, currentUser));
            } else {
                displayFriendMessage(holder, item, checkMessage(position, currentUser));
            }
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    private void displayFriendMessage(RecyclerViewHolder holder, MessageHistory item, boolean displayAvatar) {
        try {
            if (!avatarUrl.equals("")) {
                mAvatarLoader.displayImage(mFriendInfo.getAvatar(), holder.civFriendAvatar);
            }

            holder.rlMy.setVisibility(View.GONE);
            holder.ivMyImage.setVisibility(View.GONE);
            holder.tvMyMessage.setVisibility(View.GONE);
            holder.itvRightArrow.setVisibility(View.GONE);
            holder.tvMyMessageStatus.setVisibility(View.GONE);

            holder.rlFriend.setVisibility(View.VISIBLE);
            if (displayAvatar) {
                holder.civFriendAvatar.setVisibility(View.VISIBLE);
                holder.itvLeftArrow.setVisibility(View.VISIBLE);
            } else {
                holder.civFriendAvatar.setVisibility(View.GONE);
                holder.itvLeftArrow.setVisibility(View.GONE);
            }

            if (item.getIsImage()) {
                holder.ivFriendImage.setVisibility(View.VISIBLE);
                holder.tvFriendMessage.setVisibility(View.GONE);
                mImageLoader.displayImage(item.getContent(), holder.ivFriendImage);
            } else {
                holder.ivFriendImage.setVisibility(View.GONE);
                holder.tvFriendMessage.setVisibility(View.VISIBLE);
                holder.tvFriendMessage.setText(item.getContent());
            }
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    private void displayMyMessage(RecyclerViewHolder holder, MessageHistory item, boolean isFirstMessage) {
        try {
            holder.rlFriend.setVisibility(View.GONE);
            holder.ivFriendImage.setVisibility(View.GONE);
            holder.tvFriendMessage.setVisibility(View.GONE);
            holder.civFriendAvatar.setVisibility(View.GONE);
            holder.itvLeftArrow.setVisibility(View.GONE);

            if (!item.isDisplayMessageStatus()) {
                holder.tvMyMessageStatus.setVisibility(View.GONE);
            } else {
                holder.tvMyMessageStatus.setVisibility(View.VISIBLE);
                if (!item.getIsRead()) {
                    String message = "Sent";
                    holder.tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
                } else {
                    String message = "Read";
                    holder.tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
                }
            }

            if (isFirstMessage) {
                holder.itvRightArrow.setVisibility(View.VISIBLE);
            } else {
                holder.itvRightArrow.setVisibility(View.GONE);
            }

            holder.rlMy.setVisibility(View.VISIBLE);
            if (!item.getIsImage()) {
                holder.ivMyImage.setVisibility(View.GONE);
                holder.tvMyMessage.setVisibility(View.VISIBLE);
                holder.tvMyMessage.setText(item.getContent());
            } else {
                holder.ivMyImage.setVisibility(View.VISIBLE);
                holder.tvMyMessage.setVisibility(View.GONE);
                mImageLoader.displayImage(item.getContent(), holder.ivMyImage);
            }
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getDate(Long createAt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = dateFormat.format(new Date());
        String userDate = dateFormat.format(new Date(createAt));
        if (currentDate.equals(userDate)) {
            return new SimpleDateFormat("hh:mm").format(new Date(createAt));
        } else {
            return new SimpleDateFormat("hh:mm MM/dd/yyyy").format(new Date(createAt));
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        public TextView tvFriendMessage, tvMyMessage, tvMyMessageStatus, tvFriendMessageStatus;
        public IconTextView itvLeftArrow, itvRightArrow;
        public ImageView ivFriendImage, ivMyImage;
        public CircleImageView civFriendAvatar;
        public RelativeLayout rlFriend;
        public RelativeLayout rlMy;

        private boolean isClick = false;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvFriendMessage = (TextView) itemView.findViewById(R.id.tv_item_chat_message_contentText);
            tvMyMessage = (TextView) itemView.findViewById(R.id.tv_myContentText);
            ivFriendImage = (ImageView) itemView.findViewById(R.id.layout_chat_ivImage);
            civFriendAvatar = (CircleImageView) itemView.findViewById(R.id.iv_chat_profileImage);
            itvLeftArrow = (IconTextView) itemView.findViewById(R.id.iv_arrow);
            itvRightArrow = (IconTextView) itemView.findViewById(R.id.iv_right_arrow);
            rlFriend = (RelativeLayout) itemView.findViewById(R.id.rChat);
            rlMy = (RelativeLayout) itemView.findViewById(R.id.r_myChat);
            ivMyImage = (ImageView) itemView.findViewById(R.id.iv_myImage);
            tvMyMessageStatus = (TextView) itemView.findViewById(R.id.myMessage_status);
            tvFriendMessageStatus = (TextView) itemView.findViewById(R.id.friendMessage_status);

            itemView.setOnClickListener(this);
            tvFriendMessage.setOnClickListener(this);
            tvMyMessage.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private MessageHistory item;

        public void setItem(MessageHistory item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            if (v == tvFriendMessage) {
                isClick = !isClick;
                if (isClick) {
                    tvFriendMessageStatus.setVisibility(View.VISIBLE);
                    tvFriendMessageStatus.setText(getDate(item.getCreateAt()));
                } else {
                    tvFriendMessageStatus.setVisibility(View.GONE);
                }
            } else if (v == tvMyMessage) {
                isClick = !isClick;
                if (isClick) {
                    tvMyMessageStatus.setVisibility(View.VISIBLE);
                    if (!item.getIsRead()) {
                        String message = "Sent";
                        tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
                    } else {
                        String message = "Read";
                        tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
                    }
                } else {
                    tvMyMessageStatus.setVisibility(View.GONE);
                }
            } else {
                hideKeyboard(v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            System.out.println("On Long Click Listener");
            return false;
        }

        private void hideKeyboard(View view) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
