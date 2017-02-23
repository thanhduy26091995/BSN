package com.hbbmobile.bsnapp.member_list.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Doma Umaru on 1/13/2017.
 */

@IgnoreExtraProperties
@JsonIgnoreProperties("Friend")
public class User {
    public Info info;
    private List<Friend> friends;

    public User() {
    }

    public Info getInfo() {return info;}

    public void setInfo(Info info) {this.info = info;}

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}
