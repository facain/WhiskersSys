package com.example.whiskersapp.petwhiskers.Model;

public class LocationAddress {
    String id;
    String owner_id;
    String longitude;
    String latitude;

    public LocationAddress(){

    }

    public LocationAddress(String id, String owner_id, String longitude, String latitude) {
        this.id = id;
        this.owner_id = owner_id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
