package com.example.whiskersapp.petwhiskers.Model;

public class LikeDislike {

    private String id;
    private String liker;
    private String user_profile_id;
    private String rateSystem;//like or dislike

    public LikeDislike(){

    }

    public LikeDislike(String id, String liker, String user_profile_id, String rateSystem) {
        this.id = id;
        this.liker = liker;
        this.user_profile_id = user_profile_id;
        this.rateSystem = rateSystem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLiker() {
        return liker;
    }

    public void setLiker(String liker) {
        this.liker = liker;
    }

    public String getUser_profile_id() {
        return user_profile_id;
    }

    public void setUser_profile_id(String user_profile_id) {
        this.user_profile_id = user_profile_id;
    }

    public String getRateSystem() {
        return rateSystem;
    }

    public void setRateSystem(String rateSystem) {
        this.rateSystem = rateSystem;
    }
}
