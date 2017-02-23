package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 19/12/2016.
 */
public class UserResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<User> listUser;

    public UserResponse() {
    }

    public UserResponse(String error, String message, List<User> listUser) {
        this.error = error;
        this.message = message;
        this.listUser = listUser;
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

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }
}
