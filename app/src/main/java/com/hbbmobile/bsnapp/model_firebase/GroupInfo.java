package com.hbbmobile.bsnapp.model_firebase;

/**
 * Created by buivu on 09/02/2017.
 */

public class GroupInfo {
    private String groupAvatar;
    private String groupName;
    private int numberOfMember;

    public GroupInfo() {
    }

    public GroupInfo(String groupAvatar, String groupName, int numberOfMember) {
        this.groupAvatar = groupAvatar;
        this.groupName = groupName;
        this.numberOfMember = numberOfMember;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumberOfMember() {
        return numberOfMember;
    }

    public void setNumberOfMember(int numberOfMember) {
        this.numberOfMember = numberOfMember;
    }
}
