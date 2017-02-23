package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 22/12/2016.
 */
public class UserSearchResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<User> listData;

    public UserSearchResponse() {
    }

    public UserSearchResponse(String error, String message, List<User> listData) {
        this.error = error;
        this.message = message;
        this.listData = listData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getListData() {
        return listData;
    }

    public void setListData(List<User> listData) {
        this.listData = listData;
    }
}
