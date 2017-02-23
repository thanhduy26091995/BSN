package com.hbbmobile.bsnapp.event.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by buivu on 16/12/2016.
 */
public class EventResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("data")
    private List<Event> listEvent;

    public EventResponse(String error, List<Event> listEvent) {
        this.error = error;
        this.listEvent = listEvent;
    }

    public List<Event> getListEvent() {
        return listEvent;
    }

    public void setListEvent(List<Event> listEvent) {
        this.listEvent = listEvent;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
