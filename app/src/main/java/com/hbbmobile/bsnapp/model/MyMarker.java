package com.hbbmobile.bsnapp.model;

/**
 * Created by buivu on 26/12/2016.
 */
public class MyMarker {
    private String phone;
    private String address;
    private String job;
    private Double lat;
    private Double lng;

    public MyMarker(String phone, String address, String job, Double lat, Double lng) {
        this.phone = phone;
        this.address = address;
        this.job = job;
        this.lat = lat;
        this.lng = lng;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
