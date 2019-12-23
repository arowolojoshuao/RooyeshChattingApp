package com.setayeshco.rooyesh.fragments.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.adapters.recyclerView.contacts.ContactsAdapter;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.interfaces.LoadingData;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.PusherContacts;
import com.setayeshco.rooyesh.presenters.users.ContactsPresenter;
import com.setayeshco.rooyesh.ui.CustomProgressView;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 02/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsFragment extends Fragment implements LoadingData {

    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.empty)
    LinearLayout emptyContacts;
    private static final int REQUEST_PERMISSIONS = 10;


    @BindView(R.id.progress_bar_load)
    CustomProgressView progressBarLoad;

    private RealmList<ContactsModel> mContactsModelList = new RealmList<>();
    private ContactsAdapter mContactsAdapter;
    private ContactsPresenter mContactsPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, mView);
        mContactsPresenter = new ContactsPresenter(this);
        mContactsPresenter.onCreate();
        initializerView();


        return mView;
    }

//    public Observable<ContactsModel> getContactInfo(int userID) {
//        return initializeApiContact().contact(userID)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(contactsModel -> {
//                    Observable.create(subscriber -> {
//                        try {
//                            copyOrUpdateContactInfo(contactsModel);
//                            subscriber.onComplete();
//                        } catch (Exception throwable) {
//                            subscriber.onError(throwable);
//                        }
//                    }).subscribeOn(Schedulers.computation()).subscribe();
//                    return contactsModel;
//                });
//    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
       /* LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactsAdapter = new ContactsAdapter(mContactsModelList);*/
        setHasOptionsMenu(true);
       /* ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mContactsAdapter);
        ContactsList.setItemAnimator(new DefaultItemAnimator());
        ContactsList.getItemAnimator().setChangeDuration(0);

        //fix slow recyclerview start
        ContactsList.setHasFixedSize(true);
        ContactsList.setItemViewCacheSize(10);
        ContactsList.setDrawingCacheEnabled(true);
        ContactsList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ///fix slow recyclerview end
        // set recycler view to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                mContactsPresenter.onRefresh();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(1,1,1, "تازه کردن لیست دانشجوها" );

    }

    public void onProgressShow() {
        progressBarLoad.setVisibility(View.VISIBLE);
        progressBarLoad.setColor(AppHelper.getColor(getActivity(), R.color.colorPrimaryDark));
    }

    public void onProgressHide() {
        progressBarLoad.setVisibility(View.GONE);
    }

    /**
     * method to show contacts list
     *
     * @param contactsModelList this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contactsModelList, boolean isRefresh) {
        RealmList<ContactsModel> contactsModels = new RealmList<ContactsModel>();

        ContactsModel fg=new ContactsModel();
        fg.setActivate(true);
        fg.setContactID(34);
        fg.setLinked(true);
        fg.setPhone("dffffgg");
        ContactsModel fg2=new ContactsModel();
        fg.setActivate(true);
        fg.setContactID(35);
        fg.setLinked(true);
        fg.setPhone("dffffggddd");
      //  contactsModels.add(fg);
      //  contactsModels.add(fg2);

        for (ContactsModel contactsModel : contactsModelList) {

            //..............................................

            //  contactsModels.add(contactsModel);

            if ( contactsModel.isLinked() &&  contactsModel.isActivate())
            {
                contactsModels.add(contactsModel);
            }
            //..............................................
        }
        if (!isRefresh) {

            mContactsModelList = contactsModels;
        } else {
            mContactsAdapter.setContacts(contactsModels);
        }
        if (contactsModels.size() != 0) {
         //   fastScroller.setVisibility(View.VISIBLE);
            ContactsList.setVisibility(View.VISIBLE);
            emptyContacts.setVisibility(View.GONE);

        } else {
            fastScroller.setVisibility(View.GONE);
            ContactsList.setVisibility(View.GONE);
            emptyContacts.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to update contacts
     *
     * @param contactsModelList this is parameter for  updateContacts method
     */
    public void updateContacts(List<ContactsModel> contactsModelList) {
        RealmList<ContactsModel> contactsModels = new RealmList<ContactsModel>();
        for (ContactsModel contactsModel : contactsModelList) {
            contactsModels.add(contactsModel);
        }
        this.mContactsModelList = contactsModels;
        mContactsPresenter.getContacts(true);
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PusherContacts pusher) {
        mContactsPresenter.onEventMainThread(pusher);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactsPresenter.onDestroy();
    }


    @Override
    public void onShowLoading() {
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_START_REFRESH));
    }

    @Override
    public void onHideLoading() {
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_STOP_REFRESH));
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Contacts Fragment " + throwable.getMessage());
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_STOP_REFRESH));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactsAdapter = new ContactsAdapter(mContactsModelList);
       // mContactsPresenter.onRefresh();
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mContactsAdapter);
        ContactsList.setItemAnimator(new DefaultItemAnimator());
        ContactsList.getItemAnimator().setChangeDuration(0);

        //fix slow recyclerview start
        ContactsList.setHasFixedSize(true);
        ContactsList.setItemViewCacheSize(10);
        ContactsList.setDrawingCacheEnabled(true);
        ContactsList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ///fix slow recyclerview end
        // set recycler view to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
        EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
        super.onActivityCreated(savedInstanceState);
    }


    private void Permissions() {
        if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_CONTACTS)) {
            AppHelper.LogCat("Read contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            AppHelper.showPermissionDialog(getActivity());
            PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        }
        if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("Read storage data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read storage data permission.");
            PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AppHelper.hidePermissionsDialog();
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CONTACTS_PERMISSION));
            }
        }

        if (requestCode == REQUEST_PERMISSIONS) {
            if ((grantResults.length > 0) && (grantResults[0] +
                    grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "ط´ظ…ط§ ط§ط¬ط§ط²ظ‡ ط¯ط³طھط±ط³غŒ ظ†ط¯ط§ط¯ظ‡ ط§غŒط¯",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }




  /*  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AppHelper.hidePermissionsDialog();
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CONTACTS_PERMISSION));
            }
        }

        if (requestCode == REQUEST_PERMISSIONS) {

            if ((grantResults.length > 0) && (grantResults[0] +
                    grantResults[1]) == PackageManager.PERMISSION_GRANTED) {

            } else {

                Snackbar.make(getActivity().findViewById(android.R.id.content), "ط´ظ…ط§ ط§ط¬ط§ط²ظ‡ ط¯ط³طھط±ط³غŒ ظ†ط¯ط§ط¯ظ‡ ط§غŒط¯",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }*/
}