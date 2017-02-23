package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buivu on 26/12/2016.
 */
public class RegisterResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
    //getter and setter

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
}
