package com.hbbmobile.bsnapp.model_firebase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 18/01/2017.
 */
public class UserFirebase {
    private List<FriendInfo> listFriend;
    private Map<String, Object> info;
    private Map<String, Boolean> friendRequests = new HashMap<>();
    private Map<String, Boolean> friendReceives = new HashMap<>();
    private Map<String, Boolean> blocks = new HashMap<>();
    private Map<String, Boolean> groups = new HashMap<>();

    public UserFirebase() {
    }

    public UserFirebase(List<FriendInfo> listFriend, Map<String, Object> info, Map<String, Boolean> friendRequests,
                        Map<String, Boolean> friendReceives, Map<String, Boolean> blocks, Map<String, Boolean> groups) {
        this.listFriend = listFriend;
        this.info = info;
        this.friendRequests = friendRequests;
        this.friendReceives = friendReceives;
        this.blocks = blocks;
        this.groups = groups;
    }
    //getter and setter

    public List<FriendInfo> getListFriend() {
        return listFriend;
    }

    public void setListFriend(List<FriendInfo> listFriend) {
        this.listFriend = listFriend;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public Map<String, Boolean> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Map<String, Boolean> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public Map<String, Boolean> getFriendReceives() {
        return friendReceives;
    }

    public void setFriendReceives(Map<String, Boolean> friendReceives) {
        this.friendReceives = friendReceives;
    }

    public Map<String, Boolean> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<String, Boolean> blocks) {
        this.blocks = blocks;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }
}
