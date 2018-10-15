package com.example.whiskersapp.petwhiskers.Model;

public class UserChat {
    String id;
    String user_one_id;
    String user_two_id;
    String isReadUserOne; //0 - read, 1 - unread
    String isReadUserTwo; //0 - read, 1 - unread
    String last_msg;
    String last_time;

    public UserChat(){}

    public UserChat(String id, String user_one_id, String user_two_id, String isReadUserOne, String isReadUserTwo, String last_msg, String last_time) {
        this.id = id;
        this.user_one_id = user_one_id;
        this.user_two_id = user_two_id;
        this.isReadUserOne = isReadUserOne;
        this.isReadUserTwo = isReadUserTwo;
        this.last_msg = last_msg;
        this.last_time = last_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_one_id() {
        return user_one_id;
    }

    public void setUser_one_id(String user_one_id) {
        this.user_one_id = user_one_id;
    }

    public String getUser_two_id() {
        return user_two_id;
    }

    public void setUser_two_id(String user_two_id) {
        this.user_two_id = user_two_id;
    }

    public String getIsReadUserOne() {
        return isReadUserOne;
    }

    public void setIsReadUserOne(String isReadUserOne) {
        this.isReadUserOne = isReadUserOne;
    }

    public String getIsReadUserTwo() {
        return isReadUserTwo;
    }

    public void setIsReadUserTwo(String isReadUserTwo) {
        this.isReadUserTwo = isReadUserTwo;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

}