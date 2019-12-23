package com.setayeshco.rooyesh.presenters.users;


import com.setayeshco.rooyesh.activities.profile.ProfilePreviewActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.GroupsService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.users.Pusher;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016. Email : abderrahim.elimame@gmail.com
 */
public class ProfilePreviewPresenter implements Presenter {
    private ProfilePreviewActivity profilePreviewActivity;
    private Realm realm;


    public ProfilePreviewPresenter(ProfilePreviewActivity profilePreviewActivity) {
        this.profilePreviewActivity = profilePreviewActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();

    }


    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        if (profilePreviewActivity != null) {
            APIService mApiService = APIService.with(profilePreviewActivity);

            if (profilePreviewActivity.getIntent().hasExtra("userID")) {
                int userID = profilePreviewActivity.getIntent().getExtras().getInt("userID");
                UsersService mUsersContacts = new UsersService(realm, profilePreviewActivity, mApiService);
                mUsersContacts.getContact(userID).subscribe(contactsModel -> {
                    profilePreviewActivity.ShowContact(contactsModel);
                }, throwable -> {
                    profilePreviewActivity.onErrorLoading(throwable);
                });
                mUsersContacts.getContactInfo(userID).subscribe(contactsModel -> {
                    profilePreviewActivity.ShowContact(contactsModel);
                    int ConversationID = getConversationId(contactsModel.getId(), PreferenceManager.getID(profilePreviewActivity), realm);
                    if (ConversationID != 0) {
                        realm.executeTransaction(realm1 -> {
                            ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                            conversationsModel.setRecipientImage(contactsModel.getImage());
                            realm1.copyToRealmOrUpdate(conversationsModel);
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, ConversationID));
                        });
                    }
                }, throwable -> {
                    profilePreviewActivity.onErrorLoading(throwable);
                });

            }

            if (profilePreviewActivity.getIntent().hasExtra("groupID")) {
                GroupsService mGroupsService = new GroupsService(realm, profilePreviewActivity, mApiService);
                int groupID = profilePreviewActivity.getIntent().getExtras().getInt("groupID");


                mGroupsService.getGroup(groupID).subscribe(groupsModel -> {
                    profilePreviewActivity.ShowGroup(groupsModel);
                }, throwable -> {
                    profilePreviewActivity.onErrorLoading(throwable);
                });
                mGroupsService.getGroupInfo(groupID).subscribe(groupsModel -> {
                    profilePreviewActivity.ShowGroup(groupsModel);
                    int ConversationID = getConversationGroupId(groupsModel.getId(), realm);
                    if (ConversationID != 0) {
                        realm.executeTransaction(realm1 -> {
                            ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                            assert conversationsModel != null;
                            conversationsModel.setRecipientImage(groupsModel.getGroupImage());
                            conversationsModel.setRecipientUsername(groupsModel.getGroupName());
                            realm1.copyToRealmOrUpdate(conversationsModel);
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, ConversationID));
                        });
                    }
                }, throwable -> {
                    profilePreviewActivity.onErrorLoading(throwable);
                });

            }
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

    /**
     * method to get a conversation id
     *
     * @param recipientId this is the first parameter for getConversationId method
     * @param senderId    this is the second parameter for getConversationId method
     * @return conversation id
     */
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
    }

    private int getConversationGroupId(int GroupID, Realm realm) {
        try {
            ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("groupID", GroupID).findFirst();
            return conversationsModel.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception ContactFragment" + e.getMessage());
            return 0;
        }
    }
}