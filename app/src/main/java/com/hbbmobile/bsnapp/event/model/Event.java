package com.hbbmobile.bsnapp.event.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buivu on 16/12/2016.
 */
public class Event {

   @SerializedName("id")
   private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    @SerializedName("address")
    private String address;
    @SerializedName("info")
    private String info;
    @SerializedName("cost")
    private String cost;
    @SerializedName("numberchoice")
    private String numberChoice;
    @SerializedName("poster")
    private String poster;
    @SerializedName("logoevent")
    private String logoEvent;
    @SerializedName("imagedetail")
    private String imageDetail;

    public Event() {
    }

    public Event(String id, String title, String date, String time, String address, String info, String cost, String numberChoice, String poster, String logoEvent, String imageDetail) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.address = address;
        this.info = info;
        this.cost = cost;
        this.numberChoice = numberChoice;
        this.poster = poster;
        this.logoEvent = logoEvent;
        this.imageDetail = imageDetail;
    }
    //getter and setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNumberChoice() {
        return numberChoice;
    }

    public void setNumberChoice(String numberChoice) {
        this.numberChoice = numberChoice;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getLogoEvent() {
        return logoEvent;
    }

    public void setLogoEvent(String logoEvent) {
        this.logoEvent = logoEvent;
    }

    public String getImageDetail() {
        return imageDetail;
    }

    public void setImageDetail(String imageDetail) {
        this.imageDetail = imageDetail;
    }
}
