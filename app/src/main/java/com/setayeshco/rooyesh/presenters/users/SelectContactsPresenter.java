package com.setayeshco.rooyesh.presenters.users;


import com.setayeshco.rooyesh.activities.BlockedContactsActivity;
import com.setayeshco.rooyesh.activities.NewConversationContactsActivity;
import com.setayeshco.rooyesh.activities.messages.TransferMessageContactsActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.api.apiServices.UsersService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SelectContactsPresenter implements Presenter {
    private NewConversationContactsActivity newConversationContactsActivity;
    private TransferMessageContactsActivity transferMessageContactsActivity;
    private BlockedContactsActivity blockedContactsActivity;
    private Realm realm;
    private boolean selector;

    public SelectContactsPresenter(NewConversationContactsActivity newConversationContactsActivity) {
        this.newConversationContactsActivity = newConversationContactsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        selector = true;
    }

    public SelectContactsPresenter(TransferMessageContactsActivity transferMessageContactsActivity) {
        this.transferMessageContactsActivity = transferMessageContactsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        selector = false;
    }
    public SelectContactsPresenter(BlockedContactsActivity blockedContactsActivity) {
        this.blockedContactsActivity = blockedContactsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        selector = false;
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (selector) {
            APIService mApiService = APIService.with(this.newConversationContactsActivity);
            UsersService mUsersContacts = new UsersService(realm, this.newConversationContactsActivity, mApiService);
            mUsersContacts.getLinkedContacts().subscribe(newConversationContactsActivity::ShowContacts, throwable -> {
                AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
            });

        } else {
            if (transferMessageContactsActivity != null) {
                APIService mApiService = APIService.with(this.transferMessageContactsActivity);
                UsersService mUsersContacts = new UsersService(realm, this.transferMessageContactsActivity, mApiService);
                mUsersContacts.getLinkedContacts().subscribe(transferMessageContactsActivity::ShowContacts, throwable -> {
                    AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
                });
            }else {
                APIService mApiService = APIService.with(this.blockedContactsActivity);
                UsersService mUsersContacts = new UsersService(realm, this.blockedContactsActivity, mApiService);
                mUsersContacts.getBlockedContacts().subscribe(blockedContactsActivity::ShowContacts, throwable -> {
                    AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
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
}