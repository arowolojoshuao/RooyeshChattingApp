package com.setayeshco.rooyesh.models;

/**
 * Created by Abderrahim El imame on 4/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class RegisterIDResponse {

    private boolean success;
    private String message;
    private String registered_id;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegistered_id() {
        return registered_id;
    }

    public void setRegistered_id(String registered_id) {
        this.registered_id = registered_id;
    }
}
