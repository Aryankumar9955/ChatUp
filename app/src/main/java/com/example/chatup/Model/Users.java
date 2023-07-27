package com.example.chatup.Model;

public class Users {

    private String id;
    private String mobile;
    private String username;
    private String imageURL;
    private String about;

    //Constructors


    public Users() {
    }

    public Users(String id, String mobile, String username, String imageURL, String about) {
        this.id = id;
        this.mobile = mobile;
        this.username = username;
        this.imageURL = imageURL;
        this.about = about;
    }

    //Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
