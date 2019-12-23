package com.setayeshco.rooyesh.models.auth;

/**
 * Created by Abderrahim El imame on 10/4/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class LoginModel {

    private String std_number;
    private String country;
    private String pass;

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }



    public String getMobile() {
        return std_number;
    }

    public void setMobile(String mobile) {
        this.std_number = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
