package com.setayeshco.rooyesh.models.messages;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Asrdigital on 18/04/2018.
 */

public class Conversatio extends RealmObject
{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStd_number() {
        return std_number;
    }

    public void setStd_number(String std_number) {
        this.std_number = std_number;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_date() {
        return status_date;
    }

    public void setStatus_date(String status_date) {
        this.status_date = status_date;
    }

    public int getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(int is_activated) {
        this.is_activated = is_activated;
    }

    public int getIs_prof() {
        return is_prof;
    }

    public void setIs_prof(int is_prof) {
        this.is_prof = is_prof;
    }

    public int getIs_confirmed() {
        return is_confirmed;
    }

    public void setIs_confirmed(int is_confirmed) {
        this.is_confirmed = is_confirmed;
    }

    public int getHas_backup() {
        return has_backup;
    }

    public void setHas_backup(int has_backup) {
        this.has_backup = has_backup;
    }

    public String getBackup_hash() {
        return backup_hash;
    }

    public void setBackup_hash(String backup_hash) {
        this.backup_hash = backup_hash;
    }

    public String getRegistered_id() {
        return registered_id;
    }

    public void setRegistered_id(String registered_id) {
        this.registered_id = registered_id;
    }

    public boolean isLinked() {
        return Linked;
    }

    public void setLinked(boolean linked) {
        Linked = linked;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @PrimaryKey
    int id;


    int sender;


                     int recipient;


                     String Date;


                     String std_number;

                     String username;


                     String password;


                     String phone;


                     String country;



                     String status;


                     String status_date;

                     int is_activated;

                     int is_prof;


                     int is_confirmed;


                     int has_backup;


                     String backup_hash;

                     String registered_id;

                     boolean Linked;

              boolean Deleted;


                     boolean isLeft;


    boolean isAdmin;




}
