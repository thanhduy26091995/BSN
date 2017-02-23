package com.hbbmobile.bsnapp.member_list.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Doma Umaru on 12/29/2016.
 */

@IgnoreExtraProperties
public class LastMessage {
    private String id;
    private String content;
    private Date createAt;

    public LastMessage() {

    }

    public LastMessage(String id, String content, Date createAt) {
        this.id = id;
        this.content = content;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
