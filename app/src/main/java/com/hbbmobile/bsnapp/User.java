package com.hbbmobile.bsnapp;

import java.io.Serializable;

/**
 * Created by Me on 11/29/2016.
 */

public class User implements Serializable {

    private String ten, email, url;

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User(String ten, String email, String url) {
        this.ten = ten;
        this.email = email;
        this.url = url;
    }
}
