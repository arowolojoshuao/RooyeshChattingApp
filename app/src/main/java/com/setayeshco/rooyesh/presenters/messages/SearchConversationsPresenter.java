package com.setayeshco.rooyesh.presenters.messages;


import com.setayeshco.rooyesh.activities.search.SearchConversationsActivity;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.api.apiServices.ConversationsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SearchConversationsPresenter implements Presenter {
    private SearchConversationsActivity mSearchConversationsActivity;
    private Realm realm;


    public SearchConversationsPresenter(SearchConversationsActivity mSearchConversationsActivity) {
        this.mSearchConversationsActivity = mSearchConversationsActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        ConversationsService mConversationsService = new ConversationsService(realm);
        mConversationsService.getConversations().subscribe(mSearchConversationsActivity::ShowConversation, mSearchConversationsActivity::onErrorLoading);
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