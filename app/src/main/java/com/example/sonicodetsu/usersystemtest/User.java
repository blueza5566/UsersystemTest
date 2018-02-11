package com.example.sonicodetsu.usersystemtest;

import java.util.Vector;

/**
 * Created by Sonicodetsu on 9/2/2561.
 */

public class User {
    private String username;
    private String password;
    Vector<FileDetail> fileDetails = new Vector<>();

    public User(String username, String password, Vector<FileDetail> fileDetails) {
        this.username = username;
        this.password = password;
        this.fileDetails = fileDetails;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Vector<FileDetail> getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(Vector<FileDetail> fileDetails) {
        this.fileDetails = fileDetails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
