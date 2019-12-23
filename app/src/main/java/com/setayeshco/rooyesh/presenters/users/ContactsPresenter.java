package com.setayeshco.rooyesh.presenters.users;


import android.Manifest;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.PrivacyActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.ContactsFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.PusherContacts;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsPresenter implements Presenter {
    private ContactsFragment contactsFragmentView;
    private PrivacyActivity privacyActivity;
    private Realm realm;
    private UsersService mUsersContacts;
    private boolean isImageUpdated = false;

    public ContactsPresenter(ContactsFragment contactsFragment) {
        this.contactsFragmentView = contactsFragment;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    public ContactsPresenter(PrivacyActivity privacyActivity) {
        this.privacyActivity = privacyActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (contactsFragmentView != null) {
            if (!EventBus.getDefault().isRegistered(contactsFragmentView))
                EventBus.getDefault().register(contactsFragmentView);
            APIService mApiService = APIService.with(contactsFragmentView.getActivity());
            mUsersContacts = new UsersService(realm, contactsFragmentView.getActivity(), mApiService);
            getContacts(false);
        } else if (privacyActivity != null) {
            APIService mApiService = APIService.with(privacyActivity);
            mUsersContacts = new UsersService(realm, privacyActivity, mApiService);
            getPrivacyTerms();
        }

    }


    public void getContacts(boolean isRefresh) {
        try
        {
            mUsersContacts.getAllContactsnew().subscribe(contactsModels -> {
                contactsFragmentView.ShowContacts(contactsModels, isRefresh);
                contactsFragmentView.onProgressHide();
            }, throwable -> {
                contactsFragmentView.onProgressHide();
                contactsFragmentView.onErrorLoading(throwable);
            }, () -> {
                contactsFragmentView.onHideLoading();
                contactsFragmentView.onProgressHide();
            });
            try {
                PreferenceManager.setContactSize(contactsFragmentView.getActivity(), mUsersContacts.getLinkedContactsSize());
            } catch (Exception e) {
                AppHelper.LogCat(" Exception size contact fragment");
            }
        } catch (Exception e) {
            AppHelper.LogCat("getAllContacts Exception ContactsPresenter ");
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
        if (contactsFragmentView != null)
            EventBus.getDefault().unregister(contactsFragmentView);
    }

    @Override
    public void onLoadMore() {

    }


    @Override
    public void onRefresh() {

        if (PermissionHandler.checkPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS)) {
            AppHelper.LogCat("Read contact data permission already granted.");
            if (!isImageUpdated)
                contactsFragmentView.onShowLoading();

            Observable.create((ObservableOnSubscribe<List<ContactsModel>>) subscriber -> {
                try {
                    List<ContactsModel> contactsModels = UtilsPhone.GetPhoneContacts();
                    subscriber.onNext(contactsModels);
                    subscriber.onComplete();
                } catch (Exception throwable) {
                    subscriber.onError(throwable);
                }
            }).subscribeOn(Schedulers.computation()).subscribe(contacts -> {
                mUsersContacts.updateContacts(contacts).subscribe(contactsModelList -> {
                    contactsFragmentView.updateContacts(contactsModelList);
                 if (!isImageUpdated)
                        AppHelper.CustomToast(contactsFragmentView.getActivity(), contactsFragmentView.getString(R.string.success_response_contacts));
                }, throwable -> {
                    if (!isImageUpdated)
                        contactsFragmentView.onErrorLoading(throwable);
                    AlertDialog.Builder alert = new AlertDialog.Builder(RooyeshApplication.getInstance());
                    if(contactsFragmentView.isAdded()) {
                        alert.setMessage(contactsFragmentView.getString(R.string.error_response_contacts));
                    }
                    alert.setPositiveButton(R.string.ok, (dialog, which) -> {
                    });
                    alert.setCancelable(false);
                    alert.show();

                }, () -> {
                    if (!isImageUpdated) contactsFragmentView.onHideLoading();
                });
            });

            mUsersContacts.getContactInfo(PreferenceManager.getID(contactsFragmentView.getActivity())).subscribe(contactsModel -> AppHelper.LogCat("getContactInfo"), AppHelper::LogCat);

        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            PermissionHandler.requestPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS);
        }

    }

    @Override
    public void onStop() {

    }

    public void onEventMainThread(PusherContacts pusher) {
        switch (pusher.getAction()) {
            case AppConstants.EVENT_BUS_CONTACTS_PERMISSION:
                isImageUpdated = false;
                onRefresh();
                break;
            case AppConstants.EVENT_BUS_IMAGE_PROFILE_UPDATED:
                isImageUpdated = true;
                onRefresh();
                break;
            case EVENT_BUS_CONTACTS_FRAGMENT_SELECTED:
                if (mUsersContacts.getLinkedContactsSize() == 0) {
                    loadDataFromServer();
                }
                break;
        }
    }

    private void loadDataFromServer() {
        contactsFragmentView.onProgressShow();
        Observable.create((ObservableOnSubscribe<List<ContactsModel>>) subscriber -> {
            try {
                List<ContactsModel> contactsModels = UtilsPhone.GetPhoneContacts();
                subscriber.onNext(contactsModels);
                subscriber.onComplete();
            } catch (Exception throwable) {
                subscriber.onError(throwable);
            }
        }).subscribeOn(Schedulers.computation()).subscribe(contacts -> {
            mUsersContacts.updateContacts(contacts).subscribe(contactsModelList -> {
                if (contactsModelList != null)
                    contactsFragmentView.updateContacts(contactsModelList);
                new Handler().postDelayed(() -> {
                    mUsersContacts.getContactInfo(PreferenceManager.getID(contactsFragmentView.getContext())).subscribe(contactsModel -> AppHelper.LogCat("info user ContactsPresenter"), throwable -> AppHelper.LogCat("On error ContactsPresenter"));
                }, 2000);
            }, throwable -> {
                contactsFragmentView.onErrorLoading(throwable);
            }, () -> {

            });
        });

    }


    private void getPrivacyTerms() {
        mUsersContacts.getPrivacyTerms().subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                privacyActivity.showPrivcay(statusResponse.getMessage());
            } else {
                AppHelper.LogCat(" " + statusResponse.getMessage());
            }

        }, throwable -> {
            AppHelper.LogCat(" " + throwable.getMessage());
        });
    }
}