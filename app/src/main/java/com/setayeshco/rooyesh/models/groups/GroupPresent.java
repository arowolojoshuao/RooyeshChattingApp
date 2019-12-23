package com.setayeshco.rooyesh.models.groups;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vahid on 24/02/2018.
 */

public class GroupPresent extends RealmObject {
    @PrimaryKey

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("groupID")
    @Expose
    private String groupID;
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("pid")
    @Expose
    private String pID;
    @SerializedName("present")
    @Expose
    private String present;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("submitDate")
    @Expose
    private String submitDate;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_date")
    @Expose
    private String statusDate;
    @SerializedName("is_activated")
    @Expose
    private String isActivated;
    @SerializedName("has_backup")
    @Expose
    private String hasBackup;
    @SerializedName("backup_hash")
    @Expose
    private String backupHash;
    @SerializedName("registered_id")
    @Expose
    private String registeredId;
    @SerializedName("Linked")
    @Expose
    private Boolean linked;
    @SerializedName("Deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("isLeft")
    @Expose
    private Boolean isLeft;
    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;


    //''''''''''''''''''''''''''''''''''''''''''''



    @SerializedName("std_number")
    @Expose
    private String stdNumber;





    //''''''''''''''''''''''''''''''''''''''''''''



    public GroupPresent(){

    }

    public String getStdNumber() {
        return stdNumber;
    }

    public void setStdNumber(String stdNumber) {
        this.stdNumber = stdNumber;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(String isActivated) {
        this.isActivated = isActivated;
    }

    public String getHasBackup() {
        return hasBackup;
    }

    public void setHasBackup(String hasBackup) {
        this.hasBackup = hasBackup;
    }

    public String getBackupHash() {
        return backupHash;
    }

    public void setBackupHash(String backupHash) {
        this.backupHash = backupHash;
    }

    public String getRegisteredId() {
        return registeredId;
    }

    public void setRegisteredId(String registeredId) {
        this.registeredId = registeredId;
    }

    public Boolean getLinked() {
        return linked;
    }

    public void setLinked(Boolean linked) {
        this.linked = linked;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getIsLeft() {
        return isLeft;
    }

    public void setIsLeft(Boolean isLeft) {
        this.isLeft = isLeft;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

}
