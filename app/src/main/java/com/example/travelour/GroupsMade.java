package com.example.travelour;

public class GroupsMade {
    private String groupName;
    private String leaderName;
    private String tripName;
    private String groupId;

    public GroupsMade() {
    }

    public GroupsMade(String groupName, String leaderName, String tripName, String groupId) {
        this.groupName = groupName;
        this.leaderName = leaderName;
        this.tripName = tripName;
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
