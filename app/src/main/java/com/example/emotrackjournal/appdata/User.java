package com.example.emotrackjournal.appdata;

public class User {
    public String uid, username, email, dateJoined;

    public User() { }

    public User(String uid, String username, String email, String dateJoined) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.dateJoined = dateJoined;
    }
}
