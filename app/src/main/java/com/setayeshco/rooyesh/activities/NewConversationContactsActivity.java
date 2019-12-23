package com.setayeshco.rooyesh.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.adapters.recyclerView.contacts.SelectContactsAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.users.SelectContactsPresenter;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by abderrahimelimame on 6/9/16.
 * Email : abderrahim.elimame@gmail.com
 */

public class NewConversationContactsActivity extends AppCompatActivity {
    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.empty)
    LinearLayout emptyContacts;
    private RealmList<ContactsModel> mContactsModelList;
    private SelectContactsAdapter mSelectContactsAdapter;
    private SelectContactsPresenter mContactsPresenter;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        initializerView();
        mContactsPresenter = new SelectContactsPresenter(this);
        mContactsPresenter.onCreate();
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_select_contacts));

        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectContactsAdapter = new SelectContactsAdapter(this, mContactsModelList);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mSelectContactsAdapter);

        // set recyclerView to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        AnimationsUtil.setSlideOutAnimation(this);
        super.onOptionsItemSelected(item);
        return true;
    }

    /**
     * method to show contacts list
     *
     * @param contactsModels this is parameter for ShowContacts  method
     */
    public void ShowContacts(List<ContactsModel> contactsModels) {

        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle("" + contactsModels.size() + getResources().getString(R.string.of) + PreferenceManager.getContactSize(this));
        if (contactsModels.size() != 0) {
         //   fastScroller.setVisibility(View.VISIBLE);
            ContactsList.setVisibility(View.VISIBLE);
            emptyContacts.setVisibility(View.GONE);
            RealmList<ContactsModel> usersModelRealmList = new RealmList<>();
            for (ContactsModel usersModel : contactsModels) {
                usersModelRealmList.add(usersModel);
            }
            mContactsModelList = usersModelRealmList;
            mSelectContactsAdapter.setContacts(usersModelRealmList);
        } else {
            fastScroller.setVisibility(View.GONE);
            ContactsList.setVisibility(View.GONE);
            emptyContacts.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Set up SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_contacts).getActionView();
        searchView.setIconified(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(mQueryTextListener);
        searchView.setQueryHint(getString(R.string.search_hint));
        return true;
    }


    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            mSelectContactsAdapter.setString(s);
            Search(s);
            return true;
        }
    };

    /**
     * method to start searching
     *
     * @param string this  is parameter for Search method
     */
    public void Search(String string) {

        List<ContactsModel> filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            mSelectContactsAdapter.animateTo(filteredModelList);
            ContactsList.scrollToPosition(0);
        }
    }

    /**
     * method to filter the list of contacts
     *
     * @param query this parameter for FilterList  method
     * @return this for what method will return
     */
    private List<ContactsModel> FilterList(String query) {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();


        List<ContactsModel> usersModels = realm.where(ContactsModel.class)
                .equalTo("Exist", true)
                .equalTo("Linked", true)
                .notEqualTo("id", PreferenceManager.getID(this))
                .beginGroup()
                .contains("phone", query, Case.INSENSITIVE)
                .or()
                .contains("username", query, Case.INSENSITIVE)
                .endGroup()
                .findAll();

        realm.close();
        return usersModels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactsPresenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
    }
}
