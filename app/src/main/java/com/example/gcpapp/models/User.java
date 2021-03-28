package com.example.gcpapp.models;

/**
 * Created by Rohit Neel on 30-08-2020
 */

public class User {

    private String name;
    private String mobile;
    private String password;

    public User(String name, String mobile, String password) {
        this.name = name;
        this.mobile = mobile;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobile;
    }

    public String getPassword(){
        return password;
    }

}
