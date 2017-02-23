package com.hbbmobile.bsnapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by buivu on 19/12/2016.
 */
public class User implements Serializable {
    @SerializedName("idfb")
    private String id;
    @SerializedName("fullname")
    private String fullName;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("birth")
    private String dateOfBirth;
    @SerializedName("email")
    private String email;
    @SerializedName("gender")
    private String gender;
    @SerializedName("companyname")
    private String companyName;
    @SerializedName("companyaddress")
    private String companyAddress;
    @SerializedName("title")
    private String title;
    @SerializedName("website")
    private String website;
    @SerializedName("aboutyou")
    private String aboutYou;
    @SerializedName("avatar")
    private String avatar;

    public User() {
    }

    public User(String id, String fullName, String nickName, String phone, String dateOfBirth, String email,
                String gender, String companyName, String companyAddress, String title,
                String website, String aboutYou, String avatar) {
        this.id = id;
        this.fullName = fullName;
        this.nickName = nickName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.gender = gender;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.title = title;
        this.website = website;
        this.aboutYou = aboutYou;
        this.avatar = avatar;
    }
    //getter and setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
