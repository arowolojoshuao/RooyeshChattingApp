package com.setayeshco.rooyesh.models.messages;

import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;

import java.util.List;

/**
 * Created by Asrdigital on 18/04/2018.
 */



public class SynCConversation {


    private List<ConversationsModel> contactsModelList;


    public List<ConversationsModel> getContactsModelList() {
        return contactsModelList;
    }

    public void setContactsModelList(List<ConversationsModel> contactsModelList) {
        this.contactsModelList = contactsModelList;
    }
}