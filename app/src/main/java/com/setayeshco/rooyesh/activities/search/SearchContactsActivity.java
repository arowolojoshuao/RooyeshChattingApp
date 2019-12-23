package com.setayeshco.rooyesh.activities.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.adapters.others.TextWatcherAdapter;
import com.setayeshco.rooyesh.adapters.recyclerView.contacts.ContactsAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.users.SearchContactsPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 8/12/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class SearchContactsActivity extends AppCompatActivity {


    @BindView(R.id.close_btn_search_view)
    ImageView closeBtn;
    @BindView(R.id.search_input)
    TextInputEditText searchInput;
    @BindView(R.id.clear_btn_search_view)
    ImageView clearBtn;
    @BindView(R.id.searchList)
    RecyclerView searchList;
    @BindView(R.id.empty)
    LinearLayout emptyLayout;


    private ContactsAdapter mContactsAdapter;
    private SearchContactsPresenter mSearchContactsPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        searchInput.setFocusable(true);
        initializerSearchView(searchInput, clearBtn);
        initializerView();
        setTypeFaces();
        mSearchContactsPresenter = new SearchContactsPresenter(this);
        mSearchContactsPresenter.onCreate();
    }


    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            searchInput.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }
    /**
     * method to initialize the  view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchList.setLayoutManager(mLinearLayoutManager);
        mContactsAdapter = new ContactsAdapter();
        searchList.setAdapter(mContactsAdapter);

        //fix slow recyclerview start
        searchList.setHasFixedSize(true);
        searchList.setItemViewCacheSize(10);
        searchList.setDrawingCacheEnabled(true);
        searchList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ///fix slow recyclerview end
        closeBtn.setOnClickListener(v -> closeSearchView());
        clearBtn.setOnClickListener(v -> clearSearchView());
    }

    /**
     * method to show contacts list
     * @param contactsModelList this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contactsModelList) {
        if (contactsModelList.size() != 0) {

            RealmList<ContactsModel> contactsModels = new RealmList<ContactsModel>();
            for (ContactsModel contactsModel : contactsModelList) {
                contactsModels.add(contactsModel);
            }
            mContactsAdapter.setContacts(contactsModels);

            searchList.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            searchList.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to clear/reset the search view
     */
    public void clearSearchView() {
        if (searchInput.getText() != null) {
            searchInput.setText("");
            mSearchContactsPresenter.onCreate();
            searchList.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * method to close the search view
     */
    public void closeSearchView() {
        finish();
        AnimationsUtil.setSlideOutAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchContactsPresenter.onDestroy();
    }


    /**
     * method to initialize the search view
     */
    public void initializerSearchView(TextInputEditText searchInput, ImageView clearSearchBtn) {

        final Context context = this;
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });
        searchInput.addTextChangedListener(new TextWatcherAdapter() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearSearchBtn.setVisibility(View.GONE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContactsAdapter.setString(s.toString());
                Search(s.toString().trim());
                clearSearchBtn.setVisibility(View.VISIBLE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    clearSearchBtn.setVisibility(View.GONE);
                    mSearchContactsPresenter.onCreate();
                }
            }
        });

    }

    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Search contacts " + throwable.getMessage());
    }

    /**
     * method to start searching
     * @param string this  is parameter for Search method
     */
    public  void Search(String string) {

         List<ContactsModel> filteredModelList;
        filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {

            searchList.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            mContactsAdapter.animateTo(filteredModelList);
            searchList.scrollToPosition(0);
        }else {
            searchList.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to filter the list of contacts
     * @param query this parameter for FilterList  method
     * @return this for what method will return
     */
    private synchronized List<ContactsModel> FilterList(String query) {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();

        List<ContactsModel> contactsModels = realm.where(ContactsModel.class)
                .equalTo("Exist", true)
                .beginGroup()
                .contains("phone", query, Case.INSENSITIVE)
                .or()
                .contains("username", query, Case.INSENSITIVE)
                .endGroup()
                .findAll();

        realm.close();
        return contactsModels;
    }
}
