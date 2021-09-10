package com.app.sscpinstagram.Model;

public class User
{
    String id;
    String username;
    String imageurl;
    String email;
    String password;
    String fullname;
    String bio;
    public User(String id, String username, String imageurl, String email, String password, String fullname,String bio) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.bio=bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User()
    {

    }
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setfullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getfullname() {
        return fullname;
    }

}
