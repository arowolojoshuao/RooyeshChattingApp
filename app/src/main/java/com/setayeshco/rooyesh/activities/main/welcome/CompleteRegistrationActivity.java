package com.setayeshco.rooyesh.activities.main.welcome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.ConfrimedActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.profile.Selectstatuse;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.fragments.bottomSheets.BottomSheetEditProfile;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.images.ImageUtils;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.CompleteRegistration;
import com.setayeshco.rooyesh.models.users.contacts.ProfileResponse;
import com.setayeshco.rooyesh.presenters.users.EditProfilePresenter;
import com.setayeshco.rooyesh.services.MainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.ghyeok.stickyswitch.widget.StickySwitch;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_IMAGE_PROFILE_PATH;

/**
 * Created by Abderrahim El imame on 4/1/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class CompleteRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.username_input)
    AppCompatEditText usernameInput;

    AppCompatEditText username_std_number;
    AppCompatEditText passwordInput;


    @BindView(R.id.userAvatar)
    ImageView userAvatar;

    @BindView(R.id.addAvatar)
    FloatingActionButton addAvatar;

    @BindView(R.id.progress_bar_edit_profile)
    ProgressBar progressBar;

    @BindView(R.id.completeRegistration)
    TextView completeRegistration;


    @BindView(R.id.registerBtn)
    TextView registerBtn;

    @BindView(R.id.completeRegistrationLayout)
    NestedScrollView mView;


    @BindView(R.id.chose_role_switch)
    StickySwitch roleSwitch;


    private String PicturePath;
    private EditProfilePresenter mEditProfilePresenter;
    //  private boolean IS_TEACHER = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_registration_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);


        username_std_number = findViewById(R.id.username_std_number);
        passwordInput = findViewById(R.id.password_input);

        if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
            username_std_number.setHint("شماره دانشجویی");
        } else {
            username_std_number.setHint("شماره پرسنلی");
        }

        setTypeFaces();
        mEditProfilePresenter = new EditProfilePresenter(this);
        mEditProfilePresenter.onCreate();
        registerBtn.setOnClickListener(this);
        addAvatar.setOnClickListener(v -> {

            BottomSheetEditProfile bottomSheetEditProfile = new BottomSheetEditProfile();
            bottomSheetEditProfile.show(getSupportFragmentManager(), bottomSheetEditProfile.getTag());
        });


        roleSwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
                Log.d("CompleteRegActivity", "Now Selected : " + direction.name() + ", Current Text : " + text);

/*
                if (direction == StickySwitch.Direction.LEFT) {
                    IS_TEACHER = false;

                } else {
                    IS_TEACHER = true;
                }*/


            }
        });

    }


    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            completeRegistration.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            registerBtn.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            usernameInput.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            username_std_number.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            passwordInput.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerBtn:
                complete();
                break;

        }
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
                        AppHelper.CustomToast(this, getString(R.string.oops_something));
                    }

                }
                break;

        }

    }

    private void complete() {
        String username = usernameInput.getText().toString().trim();
        String std_number = username_std_number.getText().toString().trim();
        String passwordInput1 = passwordInput.getText().toString().trim();
        Log.d("ISTEACHER", "ISTEACHERCompletReg0   >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));



        if (username.isEmpty() || std_number.isEmpty() || passwordInput1.isEmpty()){
            Toast.makeText(CompleteRegistrationActivity.this,"لطفا اطلاعات مورد نظر را تکمیل کنید !",Toast.LENGTH_LONG).show();
            return;
        }

        if (!username.isEmpty()) {
            PreferenceManager.setIsNeedInfo(this, false);
            if (!AppHelper.isServiceRunning(this, MainService.class)
                    && PreferenceManager.getToken(this) != null
                    && !PreferenceManager.isNeedProvideInfo(this)) {
                Log.d("SERVICCC","SERVICEEEE .>>>> 3333");
                this.startService(new Intent(this, MainService.class));
            }

            Log.d("ISTEACHER", "ISTEACHERCompletReg1   >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(!PreferenceManager.isConfrimed(CompleteRegistrationActivity.this)) {
                Log.d("SERVICCC","SERVICEEEE .>>>> 44444");
                startActivity(new Intent(CompleteRegistrationActivity.this, ConfrimedActivity.class));
               finish();
            }
            PreferenceManager.setIsLogin(CompleteRegistrationActivity.this, true);

            Log.d("SERVICCC","SERVICEEEE .>>>> 55555");
            this.startActivity(intent);
            this.finish();
            AnimationsUtil.setSlideInAnimation(this);
        }
        else if (std_number.isEmpty()) {
        } else if (passwordInput1.isEmpty()) {
        } else {
            Log.d("ISTEACHER", "ISTEACHERCompletReg7777  >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
            Log.d("SERVICCC","SERVICEEEE .>>>> 666666");
            mEditProfilePresenter.EditCurrentName(username, std_number, passwordInput1, PreferenceManager.isTeacher(getApplicationContext()) == true ? 1 : 0, true);
           PreferenceManager.setUserPassword(CompleteRegistrationActivity.this,std_number);
            Log.d("ISTEACHER", "ISTEACHERCompletReg8888   >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
          /*  mEditProfilePresenter.EditCurrentPassword(passwordInput1, true);
            mEditProfilePresenter.EditCurrentStdNumber(std_number, true);*/
            Log.d("SERVICCC","SERVICEEEE .>>>> 8888888");
        }


        //save user role
        //  PreferenceManager.setUserRole(getApplicationContext(), false);
        //  Log.d("CompleteRegActivity", "sp saved isTeacher : " + IS_TEACHER);





//        if (username.isEmpty()) {
//
//            if(std_number.isEmpty() || std_number.length() < 5){
//                Toast.makeText(getApplicationContext(),"اطلاعات مورد نظر بدرستی وارد نشده !!!",Toast.LENGTH_LONG).show();
//                return;
//            }else if(passwordInput1.isEmpty() || passwordInput1.length() < 6){
//                Toast.makeText(getApplicationContext(),"اطلاعات مورد نظر بدرستی وارد نشده !!!",Toast.LENGTH_LONG).show();
//                return;
//            }
//
//                PreferenceManager.setIsNeedInfo(this, false);
//                if (!AppHelper.isServiceRunning(this, MainService.class)
//                        && PreferenceManager.getToken(this) != null
//                        && !PreferenceManager.isNeedProvideInfo(this))
//                    this.startService(new Intent(this, MainService.class));
//
//
//        } else {
//
//
//            Log.d("ISTEACHER", "ISTEACHERCompletReg7777  >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
//            mEditProfilePresenter.EditCurrentName(username, std_number, passwordInput1, PreferenceManager.isTeacher(getApplicationContext()) == true ? 1 : 0, true);
//            Log.d("ISTEACHER", "ISTEACHERCompletReg8888   >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
//
//            Log.d("CONFRIMED","confrimed Selectstatus >>>>>> "+ PreferenceManager.isConfrimed(CompleteRegistrationActivity.this));
//  /*          if (!PreferenceManager.isConfrimed(CompleteRegistrationActivity.this)) {
//                Intent intent = new Intent(CompleteRegistrationActivity.this, ConfrimedActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }else {*/
//
//
//
//          /*  mEditProfilePresenter.EditCurrentPassword(passwordInput1, true);
//            mEditProfilePresenter.EditCurrentStdNumber(std_number, true);*/
//                if (!AppHelper.isServiceRunning(this, MainService.class)
//                        && PreferenceManager.getToken(this) != null
//                        && !PreferenceManager.isNeedProvideInfo(this))
//                    this.startService(new Intent(this, MainService.class));
//
//                PreferenceManager.setIsLogin(CompleteRegistrationActivity.this, true);
//                Log.d("ISTEACHER", "ISTEACHERCompletReg1   >>>  " + PreferenceManager.isTeacher(CompleteRegistrationActivity.this));
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                this.startActivity(intent);
//                this.finish();
//                AnimationsUtil.setSlideInAnimation(this);
//
//
//          //  }

}


        //save user role
      //  PreferenceManager.setUserRole(getApplicationContext(), false);
      //  Log.d("CompleteRegActivity", "sp saved isTeacher : " + IS_TEACHER);




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditProfilePresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setImage(String ImageUrl) {
        BitmapImageViewTarget target = new BitmapImageViewTarget(userAvatar) {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                userAvatar.setImageBitmap(bitmap);

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
            APIHelper.initialApiUsersContacts().uploadImage(requestFile).subscribe(response -> {
                if (response.isSuccess()) {

                    if (PicturePath != null) {
                        File file = new File(PicturePath);
                        file.delete();
                    }


                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        AppHelper.CustomToast(CompleteRegistrationActivity.this, response.getMessage());
                        setImage(response.getUserImage());
                    });
                } else {
                    AppHelper.CustomToast(CompleteRegistrationActivity.this, response.getMessage());
                }
            }, throwable -> {
                AppHelper.CustomToast(CompleteRegistrationActivity.this, getString(R.string.failed_upload_image));
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

}





