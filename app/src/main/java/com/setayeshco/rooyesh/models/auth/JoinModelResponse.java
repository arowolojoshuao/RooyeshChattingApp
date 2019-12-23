package com.setayeshco.rooyesh.models.auth;

/**
 * Created by Abderrahim El imame on 01/11/2015.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */
public class JoinModelResponse {
    private boolean success;
    private boolean smsVerification;
    private boolean hasBackup;
    private boolean hasProfile;
    private String message;
    private String mobile;
    private String code;
    private String password;
    private String is_prof;
    private String is_confirmed;
    private int userID;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private String backup_hash;

    public String getBackup_hash() {
        return backup_hash;
    }

    public void setBackup_hash(String backup_hash) {
        this.backup_hash = backup_hash;
    }

    public boolean isSmsVerification() {
        return smsVerification;
    }

    public void setSmsVerification(boolean smsVerification) {
        this.smsVerification = smsVerification;
    }

    public boolean isHasProfile() {
        return hasProfile;
    }

    public void setHasProfile(boolean hasProfile) {
        this.hasProfile = hasProfile;
    }

    public boolean isHasBackup() {
        return hasBackup;
    }

    public void setHasBackup(boolean hasBackup) {
        this.hasBackup = hasBackup;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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


    public String getIs_prof() {
        return is_prof;
    }

    public void setIs_prof(String is_prof) {
        this.is_prof = is_prof;
    }


    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getIs_confirmed() {
        return is_confirmed;
    }

    public void setIs_confirmed(String is_confirmed) {
        this.is_confirmed = is_confirmed;
    }


}
