package com.setayeshco.rooyesh.api.apiServices;

import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class MessagesService {

    private Realm realm;

    public MessagesService(Realm realm) {
        this.realm = realm;

    }

    /**
     * method to get all conversation messages
     *
     * @param conversationID this is the first parameter for getConversation method
     * @param recipientID    this is the second parameter for getConversation method
     * @param senderID       this is the thirded parameter for getConversation method
     * @return return value
     */
    public Observable<List<MessagesModel>> getConversation(int conversationID, int recipientID, int senderID) {

        List<MessagesModel> messages = new ArrayList<>();
        if (conversationID == 0) {
            try {
                ConversationsModel conversationsModel = realm.where(ConversationsModel.class)
                        .beginGroup()
                        .equalTo("RecipientID", recipientID)
                        .or()
                        .equalTo("RecipientID", senderID)
                        .endGroup().findAll().first();
                messages = realm.where(MessagesModel.class)
                        .equalTo("conversationID", conversationsModel.getId())
                        .equalTo("isGroup", false)
                        .findAllSortedAsync("id", Sort.ASCENDING);

            } catch (Exception e) {
                AppHelper.LogCat(e.getMessage());
            }
        } else {
            messages = realm.where(MessagesModel.class)
                    .equalTo("conversationID", conversationID)
                    .equalTo("isGroup", false)
                    .findAllSortedAsync("id", Sort.ASCENDING);
        }
        return Observable.just(messages);
    }

    /**
     * method to get messages list from local
     *
     * @param conversationID this is parameter for getConversation method
     * @return return value
     */
    public Observable<List<MessagesModel>> getConversation(int conversationID) {
        List<MessagesModel> messages = realm.where(MessagesModel.class).equalTo("conversationID", conversationID).equalTo("isGroup", true).findAllSortedAsync("id", Sort.ASCENDING);
        return Observable.just(messages);
    }

    /**
     * method to get user media for profile
     *
     * @param recipientID this is the first parameter for getUserMedia method
     * @param senderID    this is the second parameter for getUserMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getUserMedia(int recipientID, int senderID) {
        List<MessagesModel> messages;
        ConversationsModel conversationsModel = realm.where(ConversationsModel.class)
                .beginGroup()
                .equalTo("RecipientID", recipientID)
                .or()
                .equalTo("RecipientID", senderID)
                .endGroup()
                .findAll()
                .first();

        messages = realm.where(MessagesModel.class)
                .beginGroup()
                .notEqualTo("imageFile", "null")
                .or()
                .notEqualTo("videoFile", "null")
                .or()
                .notEqualTo("audioFile", "null")
                // .or()
                //.notEqualTo("documentFile", "null")
                .endGroup()
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("conversationID", conversationsModel.getId())
                .equalTo("isGroup", false)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }


    /**
     * method to get group media for profile
     *
     * @param groupID this is the first parameter for getGroupMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getGroupMedia(int groupID) {
        List<MessagesModel> messages;

        messages = realm.where(MessagesModel.class)
                .beginGroup()
                .notEqualTo("imageFile", "null")
                .or()
                .notEqualTo("videoFile", "null")
                .or()
                .notEqualTo("audioFile", "null")
                .or()
                .notEqualTo("documentFile", "null")
                .endGroup()
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("groupID", groupID)
                .equalTo("isGroup", true)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }


    /**
     * method to get user media for profile
     *
     * @param recipientID this is the first parameter for getUserMedia method
     * @param senderID    this is the second parameter for getUserMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getUserDocuments(int recipientID, int senderID) {
        List<MessagesModel> messages;
        ConversationsModel conversationsModel = realm.where(ConversationsModel.class)
                .beginGroup()
                .equalTo("RecipientID", recipientID)
                .or()
                .equalTo("RecipientID", senderID)
                .endGroup()
                .findAll()
                .first();

        messages = realm.where(MessagesModel.class)
                .notEqualTo("documentFile", "null")
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("conversationID", conversationsModel.getId())
                .equalTo("isGroup", false)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }

    /**
     * method to get group media for profile
     *
     * @param groupID this is the first parameter for getGroupMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getGroupDocuments(int groupID) {
        List<MessagesModel> messages;

        messages = realm.where(MessagesModel.class)
                .notEqualTo("documentFile", "null")
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("groupID", groupID)
                .equalTo("isGroup", true)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }

    /**
     * method to get user media for profile
     *
     * @param recipientID this is the first parameter for getUserMedia method
     * @param senderID    this is the second parameter for getUserMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getUserLinks(int recipientID, int senderID) {
        List<MessagesModel> messages;
        ConversationsModel conversationsModel = realm.where(ConversationsModel.class)
                .beginGroup()
                .equalTo("RecipientID", recipientID)
                .or()
                .equalTo("RecipientID", senderID)
                .endGroup()
                .findAll()
                .first();

        messages = realm.where(MessagesModel.class)
                .beginGroup()
                .contains("message", "https")
                .or()
                .contains("message", "http")
                .or()
                .contains("message", "www.")
                .endGroup()
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("conversationID", conversationsModel.getId())
                .equalTo("isGroup", false)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }

    /**
     * method to get group media for profile
     *
     * @param groupID this is the first parameter for getGroupMedia method
     * @return return value
     */
    public Observable<List<MessagesModel>> getGroupLinks(int groupID) {
        List<MessagesModel> messages;

        messages = realm.where(MessagesModel.class)
                .contains("message", "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")
                .equalTo("isFileUpload", true)
                .equalTo("isFileDownLoad", true)
                .equalTo("groupID", groupID)
                .equalTo("isGroup", true)
                .findAllSorted("id", Sort.ASCENDING);
        return Observable.just(messages);
    }


}
