package com.setayeshco.rooyesh.presenters.calls;


import com.setayeshco.rooyesh.activities.search.SearchCallsActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.interfaces.Presenter;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SearchCallsPresenter implements Presenter {
    private SearchCallsActivity mSearchCallsActivity;
    private Realm realm;
    private UsersService mUsersContacts;


    public SearchCallsPresenter(SearchCallsActivity mSearchCallsActivity) {
        this.mSearchCallsActivity = mSearchCallsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        APIService mApiService = APIService.with(this.mSearchCallsActivity);
        mUsersContacts = new UsersService(realm, this.mSearchCallsActivity, mApiService);
        loadLocalData();
    }

    private void loadLocalData() {
        mUsersContacts.getAllCalls().subscribe(mSearchCallsActivity::ShowCalls, mSearchCallsActivity::onErrorLoading);
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