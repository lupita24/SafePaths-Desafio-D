package com.path.safe.safepath.util;

/**
 * Created by Citec-PC on 10/12/17.
 */

public class Usuario {
    public String name_;
    public String lastname;
    public String email;
    public String pass;

    public String getName_() {
        return name_;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}

