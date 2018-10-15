package com.example.whiskersapp.petwhiskers.Model;

public class DistanceLocationAddress {
    String owner_id;
    String distance;

    public DistanceLocationAddress(){

    }

    public DistanceLocationAddress(String owner_id, String distance) {
        this.owner_id = owner_id;
        this.distance = distance;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
