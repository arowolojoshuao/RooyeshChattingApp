package com.setayeshco.rooyesh.presenters.users;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.popups.StatusDelete;
import com.setayeshco.rooyesh.activities.status.EditStatusActivity;
import com.setayeshco.rooyesh.activities.status.StatusActivity;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.status.StatusModel;

import org.greenrobot.eventbus.EventBus;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusPresenter implements Presenter {

    private StatusActivity view;
    private EditStatusActivity editStatusActivity;
    private StatusDelete viewDelete;
    private Realm realm;
    private UsersService mUsersContacts;
    private APIService mApiService;

    public StatusPresenter(StatusActivity statusActivity) {
        this.view = statusActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();

    }

    public StatusPresenter(StatusDelete statusDelete) {
        this.viewDelete = statusDelete;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();

    }

    public StatusPresenter(EditStatusActivity editStatusActivity) {
        this.editStatusActivity = editStatusActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        this.mApiService = APIService.with(this.editStatusActivity);
        this.mUsersContacts = new UsersService(this.realm, this.editStatusActivity, this.mApiService);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);
        this.mApiService = APIService.with(view);
        mUsersContacts = new UsersService(realm, view, this.mApiService);
        getStatusFromLocal();
        getStatusFromServer();
        getCurrentStatus();

    }

    private void getStatusFromLocal() {
        mUsersContacts.getAllStatus().subscribe(view::ShowStatus, view::onErrorLoading);

    }

    private void getStatusFromServer() {
        mUsersContacts.getUserStatus().subscribe(view::updateStatusList, view::onErrorLoading);

    }

    public void getCurrentStatus() {
        try {
            mUsersContacts.getCurrentStatusFromLocal().subscribe(view::ShowCurrentStatus, view::onErrorLoading);
        } catch (Exception e) {
            AppHelper.LogCat("Exception " + e.getMessage());
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
        if (view != null) {
            EventBus.getDefault().unregister(view);
        }
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

    public void DeleteAllStatus() {
        AppHelper.showDialog(view, view.getString(R.string.delete_all_status_dialog));
        mUsersContacts.deleteAllStatus().subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                realm.executeTransaction(realm1 -> realm1.where(StatusModel.class).equalTo("userID", PreferenceManager.getID(view)).findAll().deleteAllFromRealm());
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                view.startActivity(view.getIntent());
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

            }
        }, throwable -> AppHelper.LogCat("Delete Status Error StatusPresenter "));
    }


    public void DeleteStatus(int statusID, String status) {
        APIService mApiServiceDelete = APIService.with(viewDelete);
        UsersService mUsersContactsDelete = new UsersService(realm, viewDelete, mApiServiceDelete);
        AppHelper.showDialog(viewDelete, viewDelete.getString(R.string.deleting));
        mUsersContactsDelete.deleteStatus(status).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_DELETE_STATUS, statusID));
                viewDelete.finish();
            } else {
                AppHelper.hideDialog();
                AppHelper.LogCat("delete  status " + statusResponse.getMessage());
                AppHelper.CustomToast(viewDelete, viewDelete.getString(R.string.oops_something));
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("delete  status " + throwable.getMessage());
            AppHelper.CustomToast(viewDelete, viewDelete.getString(R.string.oops_something));
        });
    }

    public void UpdateCurrentStatus(String status, int statusID) {
        AppHelper.showDialog(view, view.getString(R.string.updating_status));
        mUsersContacts.updateStatus(statusID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_CURRENT_SATUS));
                view.ShowCurrentStatus(status);


            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("update current status " + throwable.getMessage());
        });

    }


    public void EditCurrentStatus(String status, int statusID) {
        mUsersContacts.editStatus(status, statusID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(editStatusActivity.getBaseContext(), editStatusActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_STATUS, status));
                editStatusActivity.finish();
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(editStatusActivity.getBaseContext(), editStatusActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("update current status " + throwable.getMessage());
        });

    }

    public void onEventPush(Pusher pusher) {
        switch (pusher.getAction()) {
            case AppConstants.EVENT_BUS_DELETE_STATUS:
                int id = pusher.getStatusID();
                realm.executeTransactionAsync(realm1 -> {
                    realm1.where(StatusModel.class).equalTo("id", id).findFirst().deleteFromRealm();
                }, () -> {
                    view.deleteStatus(id);
                    AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), view.getString(R.string.your_status_updated_successfully), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                    getStatusFromServer();
                    getStatusFromLocal();
                    getCurrentStatus();
                }, error -> {
                    AppHelper.LogCat(error.getMessage());
                    AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), view.getString(R.string.oops_something), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                });
                break;
            case AppConstants.EVENT_BUS_UPDATE_STATUS:
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_CURRENT_SATUS));
                view.ShowCurrentStatus(pusher.getData());
                getStatusFromServer();
                getStatusFromLocal();
                getCurrentStatus();
                break;
        }
    }
}