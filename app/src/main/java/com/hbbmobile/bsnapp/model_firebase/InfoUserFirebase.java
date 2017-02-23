package com.hbbmobile.bsnapp.model_firebase;

/**
 * Created by buivu on 20/01/2017.
 */
public class InfoUserFirebase {
    private String avatar;
    private String deviceToken;
    private String name;
    private String phone;
    private long status;

    public InfoUserFirebase() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
