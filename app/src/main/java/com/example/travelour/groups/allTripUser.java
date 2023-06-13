package com.example.travelour.groups;

import java.util.List;

public class allTripUser {
    private String name;
    private List<Alltripdata> tripList;

    public allTripUser() {
    }

    public allTripUser(String name, List<Alltripdata> tripList) {
        this.name = name;
        this.tripList = tripList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Alltripdata> getTripList() {
        return tripList;
    }

    public void setTripList(List<Alltripdata> tripList) {
        this.tripList = tripList;
    }
}
