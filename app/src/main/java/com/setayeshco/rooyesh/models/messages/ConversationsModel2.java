package com.setayeshco.rooyesh.models.messages;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Asrdigital on 19/04/2018.
 */

public class ConversationsModel2 extends RealmObject {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageDate() {
        return MessageDate;
    }

    public void setMessageDate(String messageDate) {
        MessageDate = messageDate;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getRecipientPhone() {
        return RecipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        RecipientPhone = recipientPhone;
    }

    public String getRecipientUsername() {
        return RecipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        RecipientUsername = recipientUsername;
    }

    public String getUnreadMessageCounter() {
        return UnreadMessageCounter;
    }

    public void setUnreadMessageCounter(String unreadMessageCounter) {
        UnreadMessageCounter = unreadMessageCounter;
    }

    public int getRecipientID() {
        return RecipientID;
    }

    public void setRecipientID(int recipientID) {
        RecipientID = recipientID;
    }

//    public List<String> getRealmList() {
//        return RealmList;
//    }
//
//    public void setRealmList(List<String> realmList) {
//        RealmList = realmList;
//    }

    @PrimaryKey
    private int id;
    private String MessageDate;
    private String LastMessage;
    private String RecipientPhone;
    private String RecipientUsername;
    private String UnreadMessageCounter;
    private int RecipientID;
 //   private List<String> RealmList;
}
