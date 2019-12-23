package com.setayeshco.rooyesh.presenters.users;


import com.setayeshco.rooyesh.activities.search.SearchContactsActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.api.apiServices.UsersService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SearchContactsPresenter implements Presenter {
    private SearchContactsActivity mSearchContactsActivity;
    private Realm realm;
    private UsersService mUsersContacts;


    public SearchContactsPresenter(SearchContactsActivity mSearchContactsActivity) {
        this.mSearchContactsActivity = mSearchContactsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        APIService mApiService = APIService.with(this.mSearchContactsActivity);
        mUsersContacts = new UsersService(realm, this.mSearchContactsActivity, mApiService);
        loadLocalData();
    }

    private void loadLocalData() {
        mUsersContacts.getAllContacts().subscribe(mSearchContactsActivity::ShowContacts, mSearchContactsActivity::onErrorLoading);
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