package com.setayeshco.rooyesh.activities.messages;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.adapters.recyclerView.messages.TransferMessageContactsAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.users.SelectContactsPresenter;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;

/**
 * Created by abderrahimelimame on 6/9/16.
 * Email : abderrahim.elimame@gmail.com
 */

public class TransferMessageContactsActivity extends AppCompatActivity {

    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)

    RecyclerViewFastScroller fastScroller;
    private List<ContactsModel> mContactsModelList;
    private TransferMessageContactsAdapter mTransferMessageContactsAdapter;
    private SelectContactsPresenter mContactsPresenter;
    private ArrayList<String> messageCopied = new ArrayList<>();
    private ArrayList<String> filePathList = new ArrayList<>();
    private String filePath;
    private boolean forCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("messageCopied")) {
                messageCopied = getIntent().getExtras().getStringArrayList("messageCopied");
            }
            forCall = getIntent().getBooleanExtra("forCall", false);

            Intent intent = getIntent();
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri != null) {
                    filePath = FilesManager.getPath(this, uri);
                }
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    messageCopied.add(text);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
                ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (uris != null) {
                    for (Uri uri : uris) {
                        filePathList.add(FilesManager.getPath(this, uri));
                    }
                }
            }

        }
        initializeView();
        mContactsPresenter = new SelectContactsPresenter(this);
        mContactsPresenter.onCreate();

    }

    /**
     * method to initialize the view
     */
    private void initializeView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_select_contacts));

        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (filePathList != null && filePathList.size() != 0) {
            mTransferMessageContactsAdapter = new TransferMessageContactsAdapter(this, mContactsModelList, filePathList, true);
        } else if (filePath != null) {
            mTransferMessageContactsAdapter = new TransferMessageContactsAdapter(this, mContactsModelList, filePath);
        } else if (messageCopied != null && messageCopied.size() != 0) {
            mTransferMessageContactsAdapter = new TransferMessageContactsAdapter(this, mContactsModelList, messageCopied);
        } else if (forCall) {
            mTransferMessageContactsAdapter = new TransferMessageContactsAdapter(this, mContactsModelList, true);
        }

        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mTransferMessageContactsAdapter);
        // set recyclerView to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
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
        return super.onCreateOptionsMenu(menu);
    }

    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            Search(s.trim());
            return true;
        }
    };

    /**
     * method to start searching
     *
     * @param string this is parameter for Search method
     */
    public void Search(String string) {
        mTransferMessageContactsAdapter.setString(string);
        List<ContactsModel> filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            mTransferMessageContactsAdapter.setContacts(filteredModelList);
        }
    }

    /**
     * method to filter the list of contacts
     *
     * @param query this is parameter for FilterList method
     * @return this is what method will return
     */
    private List<ContactsModel> FilterList(String query) {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();

        List<ContactsModel> contactsModels = realm.where(ContactsModel.class)
                .equalTo("Linked", true)
                .equalTo("Exist", true)
                .notEqualTo("id", PreferenceManager.getID(this))
                .beginGroup()
                .contains("phone", query, Case.INSENSITIVE)
                .or()
                .contains("username", query, Case.INSENSITIVE)
                .endGroup()
                .findAll();
        realm.close();
        return contactsModels;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            AnimationsUtil.setSlideOutAnimation(this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method to show linked contacts
     *
     * @param contactsModels this is parameter for ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contactsModels) {
        mContactsModelList = contactsModels;
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle("" + mContactsModelList.size() + getString(R.string.of) + PreferenceManager.getContactSize(this));
        mTransferMessageContactsAdapter.setContacts(mContactsModelList);
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
