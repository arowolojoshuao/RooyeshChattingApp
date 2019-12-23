package com.setayeshco.rooyesh.models.messages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Asrdigital on 18/04/2018.
 */

public class ListConversation  {

    @SerializedName("ConversationsModel")
    private List<ConversationsModel> ConversationsModel;
    public List<ConversationsModel> getMusics() {
        return ConversationsModel;
    }

    public void setMusics(List<ConversationsModel> musics) {
        this.ConversationsModel = musics;
    }


}
