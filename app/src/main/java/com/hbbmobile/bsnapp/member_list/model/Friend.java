package com.hbbmobile.bsnapp.member_list.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Doma Umaru on 12/29/2016.
 */

@IgnoreExtraProperties
public class Friend {

    private int role;
    private String createAt;
    private String messageId;

    @JsonIgnore
    private Info friendInfo;

    public Friend() {

    }


    public int getStatus() {
        return role;
    }

    public void setStatus(int status) {
        this.role = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Info getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(Info friendInfo) {
        this.friendInfo = friendInfo;
    }
}
