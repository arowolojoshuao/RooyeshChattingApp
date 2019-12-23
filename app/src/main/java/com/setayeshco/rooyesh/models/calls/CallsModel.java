package com.setayeshco.rooyesh.models.calls;

import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Abderrahim El imame on 12/3/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class CallsModel extends RealmObject {
    @PrimaryKey
    private int id;
    private boolean received;
    private int counter;
    private String type;
    private String phone;
    private String date;
    private String duration;
    private int from;
    private int to;
    private ContactsModel contactsModel;
    private RealmList<CallsInfoModel> callsInfoModels;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactsModel getContactsModel() {
        return contactsModel;
    }

    public void setContactsModel(ContactsModel contactsModel) {
        this.contactsModel = contactsModel;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public RealmList<CallsInfoModel> getCallsInfoModels() {
        return callsInfoModels;
    }

    public void setCallsInfoModels(RealmList<CallsInfoModel> callsInfoModels) {
        this.callsInfoModels = callsInfoModels;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
