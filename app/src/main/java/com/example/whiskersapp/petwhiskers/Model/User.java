package com.example.whiskersapp.petwhiskers.Model;

public class User {
    String id;
    String fname;
    String lname;
    String contact;
    String email;
    String password;
    String banStat;
    //String status; //active, deactive, block

    public User(){

    }

    public User(String id, String fname, String lname, String contact, String email, String password,String banStat) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.banStat = banStat;
    }

    public User(String id, String fname, String lname, String contact, String email, String password) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.contact = contact;
        this.email = email;
        this.password = password;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBanStat() { return banStat; }

    public void setBanStat(String banStat) { this.banStat = banStat; }
}
