package com.setayeshco.rooyesh.presenters.users;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.main.welcome.CompleteRegistrationActivity;
import com.setayeshco.rooyesh.activities.profile.EditProfileActivity;
import com.setayeshco.rooyesh.activities.profile.EditUsernameActivity;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.services.MainService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.realm.Realm;

/**
 * Created by Vahid on 29/01/2018.
 */

public class MainActivityPresenter implements Presenter {
    private MainActivity view;
    private EditUsernameActivity editUsernameActivity;

    private CompleteRegistrationActivity completeRegistrationActivity;
    private Realm realm;
    private UsersService mUsersContacts;
    private boolean isEditUsername = false;
    private APIService mApiService;

    public APIService getmApiService() {
        return mApiService;
    }

    public MainActivityPresenter(CompleteRegistrationActivity completeRegistrationActivity) {
        this.completeRegistrationActivity = completeRegistrationActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }

    public MainActivityPresenter(MainActivity mainActivity) {
        this.view = mainActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();

    }


    public MainActivityPresenter() {
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }

    public MainActivityPresenter(EditUsernameActivity editUsernameActivity, boolean b) {
        this.isEditUsername = b;
        this.editUsernameActivity = editUsernameActivity;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        if (!isEditUsername) {
            if (completeRegistrationActivity != null) {
                APIService mApiService = APIService.with(completeRegistrationActivity);
                mUsersContacts = new UsersService(realm, completeRegistrationActivity, mApiService);
            } else {
                mApiService = APIService.with(view);
                mUsersContacts = new UsersService(realm, view, mApiService);
                loadData();
            }
        }
        else {
            mApiService = APIService.with(editUsernameActivity);
            this.mUsersContacts = new UsersService(realm, editUsernameActivity, mApiService);

        }

    }

    public void loadData() {
        mUsersContacts.getContact(PreferenceManager.getID(view)).subscribe(contactsModel -> {
            view.ShowContact(contactsModel);
        }, throwable -> {
            view.onErrorLoading(throwable);
        });

        mUsersContacts.getContactInfo(PreferenceManager.getID(view)).subscribe(contactsModel -> {
            view.ShowContact(contactsModel);
        }, throwable -> {
            view.onErrorLoading(throwable);
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


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {
            if (PermissionHandler.checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");

                switch (requestCode) {
                    case AppConstants.SELECT_PROFILE_PICTURE:
                        imagePath = FilesManager.getPath(activity, data.getData());
                        break;
                    case AppConstants.SELECT_PROFILE_CAMERA:
                        if (data.getData() != null) {
                            imagePath = FilesManager.getPath(activity, data.getData());
                        } else {
                            try {
                                String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                        .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                        .ImageColumns.MIME_TYPE};
                                final Cursor cursor = activity.getApplicationContext().getContentResolver()
                                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
                                                .DATE_TAKEN + " DESC");

                                if (cursor != null && cursor.moveToFirst()) {
                                    String imageLocation = cursor.getString(1);
                                    cursor.close();
                                    File imageFile = new File(imageLocation);
                                    if (imageFile.exists()) {
                                        imagePath = imageFile.getPath();
                                    }
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat("error" + e);
                            }
                        }
                        break;
                }


                if (imagePath != null) {
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_IMAGE_PROFILE_PATH, imagePath));
                } else {
                    AppHelper.LogCat("imagePath is null");
                }
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                PermissionHandler.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

    }


    public void EditCurrentName(String name,String password,String std_number,int isProf, boolean forComplete) {
        mUsersContacts.editUsername(name,password,std_number,isProf).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                if (forComplete) {
                    AppHelper.Snackbar(completeRegistrationActivity.getBaseContext(), completeRegistrationActivity.findViewById(R.id.completeRegistrationLayout), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                    PreferenceManager.setIsNeedInfo(completeRegistrationActivity, false);

                    if (!AppHelper.isServiceRunning(completeRegistrationActivity, MainService.class)
                            && PreferenceManager.getToken(completeRegistrationActivity) != null
                            && !PreferenceManager.isNeedProvideInfo(completeRegistrationActivity))
                        completeRegistrationActivity.startService(new Intent(completeRegistrationActivity, MainService.class));
                    Intent intent = new Intent(completeRegistrationActivity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    completeRegistrationActivity.startActivity(intent);
                    completeRegistrationActivity.finish();
                    AnimationsUtil.setSlideInAnimation(completeRegistrationActivity);
                } else {
                    AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_USERNAME_PROFILE_UPDATED));
                    editUsernameActivity.finish();
                }
            } else {
                if (!forComplete) {
                    AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                } else {
                    AppHelper.Snackbar(completeRegistrationActivity.getBaseContext(), completeRegistrationActivity.findViewById(R.id.completeRegistrationLayout), completeRegistrationActivity.getString(R.string.oops_something), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                }
            }
        }, AppHelper::LogCat);

    }

}