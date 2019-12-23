package com.setayeshco.rooyesh.activities.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.status.StatusActivity;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.bottomSheets.BottomSheetEditProfile;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.images.ImageUtils;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.LoadingData;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.ProfileResponse;
import com.setayeshco.rooyesh.presenters.users.EditProfilePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_IMAGE_PROFILE_PATH;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_UPDATE_CURRENT_SATUS;


/**
 * Created by Abderrahim El imame on 27/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditProfileActivity extends AppCompatActivity implements LoadingData, NetworkListener {

    @BindView(R.id.userAvatar)
    ImageView userAvatar;
    @BindView(R.id.addAvatar)
    FloatingActionButton addAvatar;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.status)
    EmojiconTextView status;
    @BindView(R.id.numberPhone)
    TextView numberPhone;
    @BindView(R.id.editProfile)
    NestedScrollView mView;
    @BindView(R.id.progress_bar_edit_profile)
    ProgressBar progressBar;

    CircleImageView imgProfile;
    private ContactsModel mContactsModel;
    private EditProfilePresenter mEditProfilePresenter;
    private APIService mApiService;
    private String PicturePath;
    private MemoryCache memoryCache;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initializerView();
        connectToChatServer();
        setTypeFaces();
        memoryCache = new MemoryCache();
        mEditProfilePresenter = new EditProfilePresenter(this);
        mEditProfilePresenter.onCreate();
        ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                addAvatar.setVisibility(View.GONE);
                final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addAvatar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                addAvatar.startAnimation(animation);

            }
        });

    /*    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        imgProfile = (CircleImageView)view.findViewById(R.id.imgProfile);*/

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addAvatar.setOnClickListener(v -> {
            BottomSheetEditProfile bottomSheetEditProfile = new BottomSheetEditProfile();
            bottomSheetEditProfile.show(getSupportFragmentManager(), bottomSheetEditProfile.getTag());
        });
        mApiService = new APIService(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.statusLayout)
    public void launchStatus() {
        Intent mIntent = new Intent(this, StatusActivity.class);
        startActivity(mIntent);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.editUsernameBtn)
    public void launchEditUsername() {
        if (mContactsModel.getUsername() != null) {
            Intent mIntent = new Intent(this, EditUsernameActivity.class);
            mIntent.putExtra("currentUsername", mContactsModel.getUsername());
            startActivity(mIntent);
        } else {
            Intent mIntent = new Intent(this, EditUsernameActivity.class);
            mIntent.putExtra("currentUsername", "");
            startActivity(mIntent);
        }
    }

    /**
     * method to show contact info
     *
     * @param mContactsModel this is parameter for ShowContact  method
     */
    public void ShowContact(ContactsModel mContactsModel) {
        final String finalName;
        String name = UtilsPhone.getContactName(this, mContactsModel.getPhone());
        if (name != null) {
            finalName = name;
        } else {
            finalName = mContactsModel.getPhone();
        }
        this.mContactsModel = mContactsModel;
        if (mContactsModel.getPhone() != null) {
            numberPhone.setText(mContactsModel.getPhone());
        }
        if (mContactsModel.getStatus() != null) {
            String state = UtilsString.unescapeJava(mContactsModel.getStatus());
            status.setText(state);
        } else {
            status.setText(getString(R.string.no_status));
        }
        if (mContactsModel.getUsername() != null) {
            username.setText(mContactsModel.getUsername());
        } else {
            username.setText(getString(R.string.no_username));
        }

        username.setText(PreferenceManager.getUserName(this));

        Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);
        if (bitmap != null) {
            ImageLoader.SetBitmapImage(bitmap, userAvatar);
        } else {

            BitmapImageViewTarget target = new BitmapImageViewTarget(userAvatar) {
                @Override
                public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                 //   userAvatar.setImageBitmap(bitmap);
                 //   ImageLoader.DownloadImage(memoryCache, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), EditProfileActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                    userAvatar.post(new Runnable() {
                        @Override
                        public void run() {
                            userAvatar.setImageBitmap(loadBitmapFromView(userAvatar));
                            ImageLoader.DownloadImage(memoryCache, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), EditProfileActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                        }
                    });

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
            RooyeshImageLoader.loadCircleImage(this, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), target, R.drawable.image_holder_ur_circle, AppConstants.EDIT_PROFILE_IMAGE_SIZE);
        }
        userAvatar.setOnClickListener(v -> {
            if (mContactsModel.getImage() != null) {
                if (FilesManager.isFilePhotoProfileExists(this, FilesManager.getProfileImage(mContactsModel.getImage()))) {
                    AppHelper.LaunchImagePreviewActivity(this, AppConstants.PROFILE_IMAGE, mContactsModel.getImage());
                } else {
                    AppHelper.LaunchImagePreviewActivity(EditProfileActivity.this, AppConstants.PROFILE_IMAGE_FROM_SERVER, mContactsModel.getImage());
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_exit);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addAvatar.setVisibility(View.GONE);
                        if (AppHelper.isAndroid5()) {
                            ActivityCompat.finishAfterTransition(EditProfileActivity.this);
                        } else {
                            finish();

                            AnimationsUtil.setSlideOutAnimation(EditProfileActivity.this);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                addAvatar.startAnimation(animation);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_exit);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addAvatar.setVisibility(View.GONE);

                if (AppHelper.isAndroid5()) {
                    ActivityCompat.finishAfterTransition(EditProfileActivity.this);
                } else {
                    finish();
                    AnimationsUtil.setSlideOutAnimation(EditProfileActivity.this);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        addAvatar.startAnimation(animation);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditProfilePresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // mEditProfilePresenter.onActivityResult(this, requestCode, resultCode, data);

        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


       // data = getIntent();
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {
            if (PermissionHandler.checkPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");

                switch (requestCode) {
                    case AppConstants.SELECT_PROFILE_PICTURE:
                        imagePath = FilesManager.getPath(EditProfileActivity.this, data.getData());
                        break;
                    case AppConstants.SELECT_PROFILE_CAMERA:
                        data = getIntent();
                        if (data.getData() != null) {
                            imagePath = FilesManager.getPath(EditProfileActivity.this, data.getData());
                        } else {
                            try {
                                String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                        .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                        .ImageColumns.MIME_TYPE};
                                final Cursor cursor = getApplicationContext().getContentResolver()
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
                PermissionHandler.requestPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }



        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case EVENT_BUS_IMAGE_PROFILE_PATH:
                progressBar.setVisibility(View.VISIBLE);
                PicturePath = String.valueOf(pusher.getData());
                if (PicturePath != null) {
                    try {
                        new UploadFileToServer().execute();
                    } catch (Exception e) {
                        AppHelper.LogCat(e);
                        AppHelper.CustomToast(EditProfileActivity.this, getString(R.string.oops_something));
                    }

                }
                break;
            case EVENT_BUS_UPDATE_CURRENT_SATUS:
                mEditProfilePresenter.loadData();
                break;
            case AppConstants.EVENT_BUS_USERNAME_PROFILE_UPDATED:
                mEditProfilePresenter.loadData();
                break;
        }

    }


    /**
     * method to connect to the chat sever by socket
     */
    private void connectToChatServer() {

        RooyeshApplication app = (RooyeshApplication) getApplication();
        mSocket = app.getSocket();

        if (mSocket == null) {
            RooyeshApplication.connectSocket();
            mSocket = app.getSocket();
        }
        if (!mSocket.connected())
            mSocket.connect();


    }

    private void setImage(String ImageUrl) {
        BitmapImageViewTarget target = new BitmapImageViewTarget(userAvatar) {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);

              //  userAvatar.setImageBitmap(bitmap);

                userAvatar.post(new Runnable() {
                    @Override
                    public void run() {
                        userAvatar.setImageBitmap(loadBitmapFromView(userAvatar));

                    }
                });


                MainActivity.imgProfile.setImageBitmap(bitmap);
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("senderId", PreferenceManager.getID(EditProfileActivity.this));
                    jsonObject.put("phone", PreferenceManager.getPhone(EditProfileActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mSocket != null)
                    mSocket.emit(AppConstants.SOCKET_IMAGE_PROFILE_UPDATED, jsonObject);

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
        RooyeshImageLoader.loadCircleImage(this, EndPoints.EDIT_PROFILE_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.EDIT_PROFILE_IMAGE_SIZE);

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

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable.getMessage());
    }


    /**
     * Uploading the image  to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, ProfileResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppHelper.LogCat("onPreExecute  image ");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            AppHelper.LogCat("progress image " + (int) (progress[0]));
        }

        @Override
        protected ProfileResponse doInBackground(Void... params) {
            return uploadFile();
        }

        private ProfileResponse uploadFile() {

            RequestBody requestFile;
            final ProfileResponse profileResponse = null;
            if (PicturePath != null) {
                byte[] imageByte = ImageUtils.compressImage(PicturePath);
                // create RequestBody instance from file
                requestFile = RequestBody.create(MediaType.parse("image/*"), imageByte);
            } else {
                requestFile = null;
            }
            EditProfileActivity.this.runOnUiThread(() -> AppHelper.showDialog(EditProfileActivity.this, "Updating ... "));
            APIHelper.initialApiUsersContacts().uploadImage(requestFile).subscribe(response -> {
                if (response.isSuccess()) {
                    if (PicturePath != null) {
                        File file = new File(PicturePath);
                      //  file.delete();
                    }
                    runOnUiThread(() -> {
                        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
                        realm.executeTransactionAsync(realm1 -> {
                            ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(EditProfileActivity.this)).findFirst();
                            contactsModel.setImage(response.getUserImage());
                            realm1.copyToRealmOrUpdate(contactsModel);

                        }, () -> new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            AppHelper.hideDialog();
                            AppHelper.CustomToast(EditProfileActivity.this, response.getMessage());
                            setImage(response.getUserImage());
                        }, 700), error -> AppHelper.LogCat("error update group image in group model " + error.getMessage()));
                        realm.close();
                    });
                } else {
                    AppHelper.CustomToast(EditProfileActivity.this, response.getMessage());
                    AppHelper.hideDialog();
                }
            }, throwable -> {
                AppHelper.hideDialog();
                AppHelper.CustomToast(EditProfileActivity.this, getString(R.string.failed_upload_image));
                AppHelper.LogCat("Failed  upload your image " + throwable.getMessage());
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            });
            return profileResponse;
        }


        @Override
        protected void onPostExecute(ProfileResponse response) {
            super.onPostExecute(response);
            // AppHelper.LogCat("Response from server: " + response);

        }


    }


    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            status.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            username.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            numberPhone.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }



    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return b;
    }


}
