package com.hbbmobile.bsnapp.chat_group;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbbmobile.bsnapp.R;
import com.hbbmobile.bsnapp.base.view.ImageLoader;
import com.hbbmobile.bsnapp.model_firebase.InfoUserFirebase;
import com.hbbmobile.bsnapp.utils.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by buivu on 06/02/2017.
 */

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupViewHolder> {
    private List<MessageGroup> data = Collections.EMPTY_LIST;
    private List<String> listMember = new ArrayList<>();
    private String mCurrentUser;
    private Context mContext;
    private HashMap<String, Object> mapMemberInfo;
    private DatabaseReference mDatabase;
    private HashMap<String, InfoUserFirebase> mapInfo;
    private int pos = 0;
    private com.nostra13.universalimageloader.core.ImageLoader mImageLoader;
    private ImageLoaderConfiguration mConfig;
    private DisplayImageOptions mOptions;
    private File cacheDir;

    public ChatGroupAdapter(List<MessageGroup> data, List<String> listMember, String mCurrentUser, Context mContext) {
        this.data = data;
        this.listMember = listMember;
        this.mCurrentUser = mCurrentUser;
        this.mContext = mContext;
        //
        mImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mapMemberInfo = new HashMap<>();
        mapInfo = new HashMap<>();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "BSNFolder");
        else
            cacheDir = mContext.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        createMapData(listMember);
        configImageLoader(mContext);
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
//        myImageLoader.init(mConfig);
    }

    public void createMapData(List<String> listMember) {
        for (final String data : listMember) {
            mDatabase.child(Constants.USERS).child(data).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                        if (userFirebase != null) {
                            mapMemberInfo.put(data, userFirebase);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addNewMess(MessageGroup messageGroup) {
        data.add(messageGroup);
        notifyDataSetChanged();
    }

    public void loadingHistory(List<MessageGroup> model) {
        Collections.reverse(model);
        for (MessageGroup history : model) {
            data.add(0, history);
            notifyDataSetChanged();
        }
        model.clear();
    }


//    public void addNewMessage(MessageGroup model) {
//        if (model.getSendBy().equals(mCurrentUser)) {
//            data.add(model);
//            notifyItemChanged(pos);
//            pos++;
//        } else {
//            model.setDisplayImage(true);
//            data.add(model);
//            notifyItemChanged(pos);
//            saveLastMessage(pos);
//            pos++;
//        }
//    }

    @Override
    public ChatGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.message_row_group, parent, false);
        return new ChatGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatGroupViewHolder holder, int position) {
        if (position < 5) {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        final MessageGroup item = data.get(position);
        holder.setItem(item);
        String currentUser = item.getSendBy();
        if (item.getSendBy().equals(mCurrentUser)) {
            displayMyMessage(holder, item, checkMessage(position, currentUser));
        } else {
            displayFriendMessage(holder, item, position, checkMessage(position, currentUser));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private boolean checkMessage(int position, String currentId) {
        if (position > 0) {
            String previousId = data.get(position - 1).getSendBy();
            if (currentId.equals(previousId)) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void displayFriendMessage(final ChatGroupViewHolder holder, final MessageGroup item, int position, boolean isNext) {
        try {
            holder.r_myChat.setVisibility(View.GONE);
            holder.myImage.setVisibility(View.GONE);
            holder.tv_myContentText.setVisibility(View.GONE);
            holder.itvRightArrow.setVisibility(View.GONE);
            holder.tvMyMessageStatus.setVisibility(View.GONE);
            holder.rChat.setVisibility(View.VISIBLE);

            if (isNext) {
                holder.civAvatar.setVisibility(View.VISIBLE);
                holder.itvArrow.setVisibility(View.VISIBLE);
            } else {
                holder.civAvatar.setVisibility(View.GONE);
                holder.itvArrow.setVisibility(View.GONE);
            }


            if (mapInfo.get(item.getSendBy()) == null) {
                mDatabase.child(Constants.USERS).child(item.getSendBy()).child(Constants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            InfoUserFirebase userFirebase = dataSnapshot.getValue(InfoUserFirebase.class);
                            if (userFirebase != null) {
                                //add map
                                mapInfo.put(item.getSendBy(), userFirebase);
                                holder.tvName.setText(userFirebase.getName());
                                ImageLoader.getInstance().loadImageCaching((Activity) mContext, userFirebase.getAvatar(), holder.civAvatar);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                InfoUserFirebase userFirebase = mapInfo.get(item.getSendBy());
                holder.tvName.setText(userFirebase.getName());
                ImageLoader.getInstance().loadImageCaching((Activity) mContext, userFirebase.getAvatar(), holder.civAvatar);
            }

//            if (displayAvatar) {
//                holder.civFriendAvatar.setVisibility(View.VISIBLE);
//                holder.itvLeftArrow.setVisibility(View.VISIBLE);
//            } else {
//                holder.civFriendAvatar.setVisibility(View.GONE);
//                holder.itvLeftArrow.setVisibility(View.GONE);
//            }

//            if (!item.isDisplayMessageStatus()) {
//                holder.tvFriendMessageStatus.setVisibility(View.GONE);
//            } else {
//                holder.tvFriendMessageStatus.setVisibility(View.VISIBLE);
//                holder.tvFriendMessageStatus.setText(getDate(item.getCreateAt()));
//            }

            if (item.getIsImage()) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvMessage.setVisibility(View.GONE);
                mImageLoader.displayImage(item.getContent(), holder.ivImage);
                // ImageLoader.getInstance().loadImageCaching((Activity) mContext, item.getContent(), holder.ivFriendImage);
            } else {
                holder.ivImage.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.VISIBLE);
                holder.tvMessage.setText(item.getContent());
            }
            if (position > 0) {
                MessageGroup itemPre = data.get(position - 1);
                if (item.getSendBy().equals(itemPre.getSendBy())) {
                    holder.tvName.setVisibility(View.GONE);
                    holder.civAvatar.setVisibility(View.GONE);
                } else {
                    holder.tvName.setVisibility(View.VISIBLE);
                    holder.civAvatar.setVisibility(View.VISIBLE);
                }
            }
        } catch (
                Exception e
                )

        {
            Log.v("Failed", e.getMessage());
        }

    }

    private void displayMyMessage(ChatGroupViewHolder holder, MessageGroup item, boolean isFirstMessage) {
        try {
            holder.tvName.setVisibility(View.GONE);
            holder.rChat.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.GONE);
            holder.civAvatar.setVisibility(View.GONE);
            holder.itvArrow.setVisibility(View.GONE);

//            if (!item.isDisplayMessageStatus()) {
//                holder.tvMyMessageStatus.setVisibility(View.GONE);
//            } else {
//                holder.tvMyMessageStatus.setVisibility(View.VISIBLE);
//                if (!item.getIsRead()) {
//                    String message = "Sent";
//                    holder.tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
//                } else {
//                    String message = "Read";
//                    holder.tvMyMessageStatus.setText(message + ", " + getDate(item.getCreateAt()));
//                }
//            }

            if (isFirstMessage) {
                holder.itvRightArrow.setVisibility(View.VISIBLE);
            } else {
                holder.itvRightArrow.setVisibility(View.GONE);
            }

            holder.r_myChat.setVisibility(View.VISIBLE);
            if (!item.getIsImage()) {
                holder.myImage.setVisibility(View.GONE);
                holder.tv_myContentText.setVisibility(View.VISIBLE);
                holder.tv_myContentText.setText(item.getContent());
            } else {
                holder.myImage.setVisibility(View.VISIBLE);
                holder.tv_myContentText.setVisibility(View.GONE);
                //   ImageLoader.getInstance().loadImageCaching((Activity) mContext, item.getContent(), holder.ivMyImage);
                mImageLoader.displayImage(item.getContent(), holder.myImage);

//                holder.ivMyImage.setImageURI();
            }
        } catch (Exception e) {
            Log.v("Failed", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
