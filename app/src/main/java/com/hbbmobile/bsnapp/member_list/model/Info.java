package com.hbbmobile.bsnapp.member_list.model;

/**
 * Created by Doma Umaru on 1/13/2017.
 */

public class Info {
    private String avatar;
    private String phone;
    private String name;
    private int status;

    public Info() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
