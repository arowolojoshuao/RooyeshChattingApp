package com.setayeshco.rooyesh.presenters.messages;


import android.os.Handler;

import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.GroupsService;
import com.setayeshco.rooyesh.api.apiServices.MessagesService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_READ;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
//public class MessagesPresenter implements Presenter {
public class MessagesPresenter {
   /* private final MessagesActivity view;
    private final Realm realm;
    private int RecipientID, ConversationID, GroupID;
    private Boolean isGroup;
    private MessagesService mMessagesService;
    private UsersService mUsersContacts;
    private NotificationsManager notificationsManager;


    public MessagesPresenter(MessagesActivity messagesActivity) {
        this.view = messagesActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);

        if (view.getIntent().getExtras() != null) {
            if (view.getIntent().hasExtra("conversationID")) {
                ConversationID = view.getIntent().getExtras().getInt("conversationID");
            }
            if (view.getIntent().hasExtra("recipientID")) {
                RecipientID = view.getIntent().getExtras().getInt("recipientID");
            }

            if (view.getIntent().hasExtra("groupID")) {
                GroupID = view.getIntent().getExtras().getInt("groupID");
            }

            if (view.getIntent().hasExtra("isGroup")) {
                isGroup = view.getIntent().getExtras().getBoolean("isGroup");
            }

        }

        notificationsManager = new NotificationsManager();
        APIService mApiService = APIService.with(view.getApplicationContext());
        mMessagesService = new MessagesService(realm);
        mUsersContacts = new UsersService(realm, view.getApplicationContext(), mApiService);
        GroupsService mGroupsService = new GroupsService(realm, view.getApplicationContext(), mApiService);

        mUsersContacts.getContact(PreferenceManager.getID(view)).subscribe(view::updateContact, view::onErrorLoading);
        mUsersContacts.getContactInfo(PreferenceManager.getID(view)).subscribe(view::updateContact, view::onErrorLoading);

        if (isGroup) {

            mGroupsService.getGroup(GroupID).subscribe(view::updateGroupInfo, view::onErrorLoading);
            mGroupsService.getGroupInfo(GroupID).subscribe(view::updateGroupInfo, view::onErrorLoading);

            mGroupsService.getGroupMembers(GroupID).subscribe(view::ShowGroupMembers, view::onErrorLoading);
            loadLocalGroupData();
            new Handler().postDelayed(this::updateGroupConversationStatus, 500);
        } else {

            getRecipientInfo();
            loadLocalData();
            new Handler().postDelayed(this::updateConversationStatus, 500);
        }

    }

    public void getRecipientInfo() {

        mUsersContacts.getContact(RecipientID).subscribe(view::updateContactRecipient, view::onErrorLoading);
        mUsersContacts.getContactInfo(RecipientID).subscribe(contactsModel -> {
            view.updateContactRecipient(contactsModel);
            int ConversationID = getConversationId(contactsModel.getId(), PreferenceManager.getID(view), realm);
            if (ConversationID != 0) {
                realm.executeTransaction(realm1 -> {
                    ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                    conversationsModel.setRecipientImage(contactsModel.getImage());
                    realm1.copyToRealmOrUpdate(conversationsModel);
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, ConversationID));
                });
            }
        }, view::onErrorLoading);
    }

    public void updateConversationStatus() {
        try {
            realm.executeTransaction(realm1 -> {
                ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).equalTo("RecipientID", RecipientID).findFirst();

                if (conversationsModel1 != null) {
                    conversationsModel1.setStatus(AppConstants.IS_SEEN);
                    conversationsModel1.setUnreadMessageCounter("0");
                    realm1.copyToRealmOrUpdate(conversationsModel1);

                    List<MessagesModel> messagesModel = realm1.where(MessagesModel.class).equalTo("conversationID", ConversationID).equalTo("senderID", RecipientID).findAll();
                    for (MessagesModel messagesModel1 : messagesModel) {
                        if (messagesModel1.getStatus() == AppConstants.IS_WAITING) {
                            messagesModel1.setStatus(AppConstants.IS_SEEN);
                            realm1.copyToRealmOrUpdate(messagesModel1);
                        }
                    }
                    EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));

                    EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_READ, ConversationID));
                    notificationsManager.SetupBadger(view);
                }
            });
        } catch (Exception e) {
            AppHelper.LogCat("There is no conversation unRead MessagesPresenter ");
        }
    }

    public void updateGroupConversationStatus() {
        try {
            realm.executeTransaction(realm1 -> {
                ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).equalTo("groupID", GroupID).findFirst();

                if (conversationsModel1 != null) {
                    conversationsModel1.setStatus(AppConstants.IS_SEEN);
                    conversationsModel1.setUnreadMessageCounter("0");
                    realm1.copyToRealmOrUpdate(conversationsModel1);

                    List<MessagesModel> messagesModel = realm1.where(MessagesModel.class).equalTo("conversationID", ConversationID).notEqualTo("senderID", PreferenceManager.getID(view)).findAll();
                    for (MessagesModel messagesModel1 : messagesModel) {
                        if (messagesModel1.getStatus() == AppConstants.IS_WAITING) {
                            messagesModel1.setStatus(AppConstants.IS_SEEN);
                            realm1.copyToRealmOrUpdate(messagesModel1);
                        }
                    }
                    EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
                    EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_READ, ConversationID));
                    notificationsManager.SetupBadger(view);
                }
            });
        } catch (Exception e) {
            AppHelper.LogCat("There is no conversation unRead MessagesPresenter ");
        }
    }

    private void loadLocalGroupData() {
        if (notificationsManager.getManager())
            notificationsManager.cancelNotification(GroupID);
        mMessagesService.getConversation(ConversationID).subscribe(view::ShowMessages, view::onErrorLoading, view::onHideLoading);
    }

    private void loadLocalData() {
        if (notificationsManager.getManager())
            notificationsManager.cancelNotification(RecipientID);
        try {
            mMessagesService.getConversation(ConversationID, RecipientID, PreferenceManager.getID(view)).subscribe(view::ShowMessages, view::onErrorLoading);
        } catch (Exception e) {
            AppHelper.LogCat("" + e.getMessage());
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(view);
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onStop() {

    }

    *//**
     * method to get a conversation id
     *
     * @param recipientId this is the first parameter for getConversationId method
     * @param senderId    this is the second parameter for getConversationId method
     * @return conversation id
     *//*
    private int getConversationId(int recipientId, int senderId, Realm realm) {
        try {
            ConversationsModel conversationsModelNew = realm.where(ConversationsModel.class)
                    .beginGroup()
                    .equalTo("RecipientID", recipientId)
                    .or()
                    .equalTo("RecipientID", senderId)
                    .endGroup().findAll().first();
            return conversationsModelNew.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception ContactFragment" + e.getMessage());
            return 0;
        }
    }*/


}
