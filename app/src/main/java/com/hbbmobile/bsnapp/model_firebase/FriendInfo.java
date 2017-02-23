package com.hbbmobile.bsnapp.model_firebase;

/**
 * Created by buivu on 18/01/2017.
 */
public class FriendInfo {
    private long createAt;
    private String messageId;
    private long role;

    public FriendInfo() {
    }

    public FriendInfo(long createAt, String messageId, long role) {
        this.createAt = createAt;
        this.messageId = messageId;
        this.role = role;
    }
    //getter and setter

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }
}
