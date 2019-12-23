package com.setayeshco.rooyesh.activities.settings;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.profile.EditProfileActivity;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.users.SettingsPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Abderrahim El imame on 27/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SettingsActivity extends AppCompatActivity implements NetworkListener {


    @BindView(R.id.userAvatar)
    ImageView userAvatar;

    @BindView(R.id.user_status)
    EmojiconTextView userStatus;

    @BindView(R.id.settingsHead)
    AppCompatTextView settingsHead;

    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.chats_settings_text)
    TextView chats;

    @BindView(R.id.account_settings_text)
    TextView account;

    @BindView(R.id.notifications_settings_text)
    TextView notifications;

    @BindView(R.id.about_help_settings_text)
    TextView aboutHelp;

    @BindView(R.id.mainSettings)
    NestedScrollView mView;
    private ContactsModel mContactsModel;
    private SettingsPresenter mSettingsPresenter;
    private MemoryCache memoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupToolbar();
        setTypeFaces();
        memoryCache = new MemoryCache();
        mSettingsPresenter = new SettingsPresenter(this);
        mSettingsPresenter.onCreate();
        EventBus.getDefault().register(this);

    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            userName.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            userStatus.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            chats.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            account.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            notifications.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            aboutHelp.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.settingsHead)
    public void launchEditProfile(View v) {
        RateHelper.significantEvent(this);
        if (AppHelper.isAndroid5()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    new Pair<>(userAvatar, "userAvatar"),
                    new Pair<>(userName, "userName"),
                    new Pair<>(userStatus, "userStatus"),
                    new Pair<>(settingsHead, "settingsHead")
            );
            Intent mIntent = new Intent(this, EditProfileActivity.class);
            startActivity(mIntent, options.toBundle());
        } else {
            AppHelper.LaunchActivity(this, EditProfileActivity.class);
        }
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.chats_settings)
    public void launchChatsSettings() {
        RateHelper.significantEvent(this);
        AppHelper.LaunchActivity(this, ChatsSettingsActivity.class);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.account_settings)
    public void launchAccountSettings() {
        RateHelper.significantEvent(this);
        AppHelper.LaunchActivity(this, AccountSettingsActivity.class);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.notifications_settings)
    public void launchNotificationSettings() {
        RateHelper.significantEvent(this);
        AppHelper.LaunchActivity(this, NotificationsSettingsActivity.class);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.about_help_settings)
    public void launchAboutSettings() {
        AppHelper.LaunchActivity(this, AboutHelpActivity.class);
    }

    public void ShowContact(ContactsModel contactsModels) {
        mContactsModel = contactsModels;
        try {
            if (mContactsModel.getStatus() != null) {
                String status = UtilsString.unescapeJava(mContactsModel.getStatus());
                userStatus.setText(status);
            } else {
                userStatus.setText(getString(R.string.no_status));
            }
            if (mContactsModel.getUsername() != null) {
                userName.setText(mContactsModel.getUsername());
            } else {
                userName.setText(getString(R.string.no_username));
            }

            userName.setText(PreferenceManager.getUserName(this));

            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.SETTINGS_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userAvatar);
            } else {
                BitmapImageViewTarget target = new BitmapImageViewTarget(userAvatar) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userAvatar.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.SETTINGS_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.SETTINGS_PROFILE);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userAvatar.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userAvatar.setImageDrawable(placeholder);
                    }
                };
                RooyeshImageLoader.loadCircleImage(this, EndPoints.SETTINGS_IMAGE_URL + mContactsModel.getImage(), target, R.drawable.image_holder_ur_circle, AppConstants.SETTINGS_IMAGE_SIZE);

            }
        } catch (Exception e) {
            AppHelper.LogCat("" + e);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_BACK_ACTIVITY));
                finish();
                AnimationsUtil.setSlideOutAnimation(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSettingsPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case AppConstants.EVENT_BUS_USERNAME_PROFILE_UPDATED:
            case AppConstants.EVENT_BUS_UPDATE_CURRENT_SATUS:
            case AppConstants.EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED:
                mSettingsPresenter.loadData();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_BACK_ACTIVITY));

    }


    @Override
    protected void onResume() {
        super.onResume();
        RooyeshApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        }
    }
}
