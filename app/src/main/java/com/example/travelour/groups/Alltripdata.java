package com.example.travelour.groups;

public class Alltripdata{
    private String date,key,membersCount,tripEnd,tripName,tripStart,userId;

    public Alltripdata() {
    }

    public Alltripdata(String date, String key, String membersCount, String tripEnd, String tripName, String tripStart, String userId) {
        this.date = date;
        this.key = key;
        this.membersCount = membersCount;
        this.tripEnd = tripEnd;
        this.tripName = tripName;
        this.tripStart = tripStart;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(String membersCount) {
        this.membersCount = membersCount;
    }

    public String getTripEnd() {
        return tripEnd;
    }

    public void setTripEnd(String tripEnd) {
        this.tripEnd = tripEnd;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripStart() {
        return tripStart;
    }

    public void setTripStart(String tripStart) {
        this.tripStart = tripStart;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
