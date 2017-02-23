package com.hbbmobile.bsnapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by buivu on 28/11/2016.
 */
public class Constants {
    public static final String APP_NAME = "BSN";
    public static final String EMAIL_BSN = "solutionshbb@gmail.com";
    public static final String PHONE_BSN = "0123456";

    public static final int PICK_IMAGE = 1;
    public static String AVATAR_GROUP_CHAT = null;
    public static final String GROUP_PHOTO = "groupPhoto";

    public static final int RC_SIGN_IN = 0;
    public static final String[] FACEBOOK_PERMISSION = new String[]{"public_profile", "user_friends", "email"};
    public static final int GALLERY_INTENT = 2;
    public static final int CAMERA_INTENT = 3;
    public static final String CURRENT_ID = "currentID";
    public static final String BSTYLE_VN = "http://bstyle.vn/";
    public static final String PCDN_ONLINE = "";
    public static final String USERS = "Users";
    public static final String FRIENDS = "friends";
    public static final String FRIEND_REQUESTS = "friendRequests";
    public static final String FRIEND_RECEIVES = "friendReceives";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String INFO = "info";
    public static final String PHONE = "phone";
    public static final String AVATAR = "avatar";
    //Firebase child
    public static String FIREBASE_CHILD_MESSAGES = "Messages";
    public static String FIREBASE_CHILD_COUNT = "count";
    public static String FIREBASE_CHILD_HISTORY = "history";
    public static String MESSAGE_ID = "messageId";
    public static final String STRANGER = "stranger";
    //MESSAGE
    public static final String CONTENT = "content";
    public static final String CREATE_AT = "createAt";
    public static final String FROM_UID = "fromUid";
    public static final String IS_IMAGE = "isImage";
    public static final String IS_READ = "isRead";

    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "AAAA7QQQ5WY:APA91bHjCTsQxAMOvVB-tH49lPRwDUDmoD3j0WgVDLDju7QDGZADcjTCt3P11q5lWMyyrq0VWIFkcpOoxm8ZxUGUbEJa9eUE3QPU75yaHDQhQlv1kQ_ezEki9K0Rchzn7cn8IGa6pFZe";
    public static final String PUSH_NOTIFICATION = "pushNotification";


    public static final String GROUPS = "Groups";
    public static final String GROUPS_LOWER = "groups";
    public static final String MEMBERS = "members";
    public static final String CONTENTS = "contents";
    public static final String GROUP_NAME = "groupName";
    public static final String GROUP_AVATAR = "groupAvatar";
    public static final String CHAT_GROUP = "chatGroup";

    public static ProgressDialog mProgressDialog;

    public static void showProgessDialog(Activity activity) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }

}
