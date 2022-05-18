package com.example.application.models;

import java.io.Serializable;
import com.example.application.models.Actions;
import com.example.application.models.User;

public class Response implements Serializable {
    public final Actions action;
    public User user;
    public User newUser;
    public String message;
    public Response(Actions action) {
        this.action = action;
    }
    public Response(Actions action, User user) {
        this.action = action;
        this.user = user;
    }
    public Response(Actions action, User user, User newuser) {
        this.action = action;
        this.user = user;
        this.newUser = newuser;
    }
    public Response(Actions action, String message) {
        this.action = action;
        this.message = message;
    }
    public Response(Actions action, User user, String message) {
        this.action = action;
        this.user = user;
        this.message = message;
    }
}
