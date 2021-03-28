package com.example.gcpapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit Neel on 24/08/2020.
 */

public class Result {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("user")
    private User user;

    private String msg;

    public Result(Boolean error,String msg, User user) {
        this.error = error;
        this.msg = msg;
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public Boolean getError() {
        return error;
    }

    public User getUser() {
        return user;
    }
}
