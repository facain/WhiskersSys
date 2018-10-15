package com.example.whiskersapp.petwhiskers.Model;

public class Bookmark {
    String id;
    String pet_name;
    String breed;
    String status; //available, adopted, deleted post
    String gender;
    String imgUrl;
    String owner_id;
    String bookmark_user_id;
    String pet_id;

    public String getPet_id() {
        return pet_id;
    }

    public void setPet_id(String pet_id) {
        this.pet_id = pet_id;
    }

    public Bookmark(){}

    public Bookmark(String id, String pet_name, String breed, String status, String gender, String imgUrl, String owner_id, String bookmark_user_id) {
        this.id = id;
        this.pet_name = pet_name;
        this.breed = breed;
        this.status = status;
        this.gender = gender;
        this.imgUrl = imgUrl;
        this.owner_id = owner_id;
        this.bookmark_user_id = bookmark_user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getBookmark_user_id() {
        return bookmark_user_id;
    }

    public void setBookmark_user_id(String bookmark_user_id) {
        this.bookmark_user_id = bookmark_user_id;
    }

}
