package com.example.musicplayer.api.model;

public class PbUser {
    public String username;
    public String email;
    public String password;
    public String passwordConfirm;
    public String name;

    public PbUser(String username, String email, String password, String passwordConfirm) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
