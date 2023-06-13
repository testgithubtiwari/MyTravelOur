package com.example.travelour.profile;

public class TripData {
    String tripName,tripStart,tripEnd,date,memberscount,key;

    public TripData() {
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

    public String getTripEnd() {
        return tripEnd;
    }

    public void setTripEnd(String tripEnd) {
        this.tripEnd = tripEnd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemberscount() {
        return memberscount;
    }

    public void setMemberscount(String memberscount) {
        this.memberscount = memberscount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public TripData(String tripName, String tripStart, String tripEnd, String date, String memberscount, String key) {
        this.tripName = tripName;
        this.tripStart = tripStart;
        this.tripEnd = tripEnd;
        this.date = date;
        this.memberscount = memberscount;
        this.key = key;
    }
}
