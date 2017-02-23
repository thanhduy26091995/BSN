package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by buivu on 22/12/2016.
 */
public class UserSearch implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("job")
    private String job;
    @SerializedName("avatar")
    private String avatar;

    public UserSearch() {
    }

    public UserSearch(String name, String address, String phone, String job, String avatar) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.job = job;
        this.avatar = avatar;
    }
    //getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
