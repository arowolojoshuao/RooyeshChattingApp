package com.setayeshco.rooyesh.presenters.users;


import com.setayeshco.rooyesh.activities.settings.SettingsActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.Presenter;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016. Email : abderrahim.elimame@gmail.com
 */
public class SettingsPresenter implements Presenter {
    private final SettingsActivity view;
    private final Realm realm;
    private UsersService mUsersContacts;


    public SettingsPresenter(SettingsActivity settingsActivity) {
        this.view = settingsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        APIService mApiService = APIService.with(view);
        mUsersContacts = new UsersService(realm, view, mApiService);
        loadData();
    }

    public void loadData() {
        mUsersContacts.getContact(PreferenceManager.getID(view)).subscribe(view::ShowContact, throwable -> {
            AppHelper.LogCat(throwable.getMessage());
        });
        mUsersContacts.getContactInfo(PreferenceManager.getID(view)).subscribe(view::ShowContact, throwable -> {
            AppHelper.LogCat(throwable.getMessage());
        });
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