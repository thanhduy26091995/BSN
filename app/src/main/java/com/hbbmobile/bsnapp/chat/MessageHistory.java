package com.hbbmobile.bsnapp.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Doma Umaru on 1/3/2017.
 */

@IgnoreExtraProperties
public class MessageHistory {
    private String content;
    private String fromUid;
    private long createAt;
    private boolean isRead;
    private boolean isImage;

    public MessageHistory() {

    }

    public MessageHistory(String content, String fromUid, long createAt, boolean isRead) {
        this.content = content;
        this.fromUid = fromUid;
        this.createAt = createAt;
        this.isRead = isRead;
    }

    public MessageHistory(String content, String fromUid, long createAt, boolean isRead, boolean isImage) {
        this.content = content;
        this.fromUid = fromUid;
        this.createAt = createAt;
        this.isRead = isRead;
        this.isImage = isImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    @JsonIgnore
    private boolean isDisplayMessageStatus;

    public boolean isDisplayMessageStatus() {
        return isDisplayMessageStatus;
    }

    public void setDisplayMessageStatus(boolean displayMessageStatus) {
        isDisplayMessageStatus = displayMessageStatus;
    }
}
