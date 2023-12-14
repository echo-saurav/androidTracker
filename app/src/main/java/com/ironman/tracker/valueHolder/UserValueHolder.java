package com.ironman.tracker.valueHolder;

public class UserValueHolder {
    public String userName,email,description,imageUrl;
    public String uid;
    public UserValueHolder(){}

    public UserValueHolder(String userName, String email, String description, String imageUrl, String uid) {
        this.userName = userName;
        this.email = email;
        this.description = description;
        this.imageUrl = imageUrl;
        this.uid = uid;
    }
}
