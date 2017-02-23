package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 26/12/2016.
 */
public class GPSResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<GPS> listGPS;

    public GPSResponse() {
    }

    public GPSResponse(String error, String message, List<GPS> listGPS) {
        this.error = error;
        this.message = message;
        this.listGPS = listGPS;
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

    public List<GPS> getListGPS() {
        return listGPS;
    }

    public void setListGPS(List<GPS> listGPS) {
        this.listGPS = listGPS;
    }
}
