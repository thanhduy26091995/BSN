package com.hbbmobile.bsnapp.chat_group;

/**
 * Created by buivu on 06/02/2017.
 */

public class MessageGroup {
    private String content;
    private long createAt;
    private String sendBy;
    private boolean isImage;

    public MessageGroup() {
    }

    public MessageGroup(String content, long createAt, String sendBy, boolean isImage) {
        this.content = content;
        this.createAt = createAt;
        this.sendBy = sendBy;
        this.isImage = isImage;
    }

    public boolean getIsImage() {
        return isImage;
    }

    public void setIsImage(boolean image) {
        isImage = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}
