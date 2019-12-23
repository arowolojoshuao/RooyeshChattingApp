package com.setayeshco.rooyesh.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.ConfrimedActivity;
import com.setayeshco.rooyesh.activities.call.IncomingCallActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.main.PreMainActivity;
import com.setayeshco.rooyesh.activities.main.welcome.CompleteRegistrationActivity;
import com.setayeshco.rooyesh.activities.main.welcome.PasswordActivity;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.activities.main.welcome.welcomActivityStudent;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.services.SMSVerificationService;

public class Selectstatuse extends AppCompatActivity {


    Button btn_teacher,btn_student,btn_modir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectstatuse);


        UsersService mUsersContacts = new UsersService(Selectstatuse.this,new APIService(Selectstatuse.this));
        mUsersContacts.getContactInfo(PreferenceManager.getUserId(Selectstatuse.this)).subscribe(contactsModel -> {
            new IncomingCallActivity().showUserInfo(contactsModel);
            PreferenceManager.setUserPassword(Selectstatuse.this,contactsModel.getPassword());
            if (contactsModel.getIs_confirmed().equals("1"))
            {
                PreferenceManager.setIsConfrimed(Selectstatuse.this, true);
            }else {
                PreferenceManager.setIsConfrimed(Selectstatuse.this,false);
            }

            Log.d("PHONEE","PHONEE    >>>  "+contactsModel.getPhone());
            Log.d("PHONEE","isCONFIRMED    >>>  "+contactsModel.getIs_confirmed());
        }, throwable -> {
            AppHelper.LogCat(throwable.getMessage());
        });


        btn_modir=(Button)findViewById(R.id.btn_modir);
        btn_teacher=(Button)findViewById(R.id.btn_teacher);
        btn_student=(Button)findViewById(R.id.btn_student);
        initializerView();

        btn_modir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Selectstatuse.this, "درحال حاضر این گزینه فعال نمی باشد", Toast.LENGTH_SHORT).show();
            }
        });

        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.setUserRole(getApplicationContext(), true);
                Log.d("ISTEACHER","ISTEACHER    >>>  "+PreferenceManager.isTeacher(Selectstatuse.this));

                Intent i=new Intent(Selectstatuse.this,WelcomeActivity.class);
                i.putExtra("is_prof","1");
                startActivity(i);
                finish();


            }
        });

        btn_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.setUserRole(getApplicationContext(), false);
                Intent i=new Intent(Selectstatuse.this,WelcomeActivity.class);
                i.putExtra("is_prof","0");
                startActivity(i);
                finish();
            }
        });
    }

    private  void initializerView()
    {
        if(!PreferenceManager.isLogin(this)) {
            if (PreferenceManager.getToken(this) != null) {
                NotificationsManager notificationsManager = new NotificationsManager();
                notificationsManager.SetupBadger(this);
                if (PreferenceManager.isHasBackup(this)) {
                    Intent intent = new Intent(this, PreMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //  startActivity(intent);
                    //   finish();
                    AnimationsUtil.setSlideInAnimation(this);
                }
                else
                    {
                    getAppSettings();
                    if (PreferenceManager.isNeedProvideInfo(this)) {
                        Log.d("CONFRIMED", "confrimed >>>>>> " + PreferenceManager.isConfrimed(Selectstatuse.this));
                        Log.d("CONFRIMED", "confrimed Selectstatus >>>>>> " + PreferenceManager.isConfrimed(Selectstatuse.this));

                        Intent intent = new Intent(this, CompleteRegistrationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        AnimationsUtil.setSlideInAnimation(this);

                    }
                    else
                        {

                        Log.d("CONFRIMED", "confrimed Selectstatus >>>>>> " + PreferenceManager.isConfrimed(Selectstatuse.this));
           /*         if (!PreferenceManager.isConfrimed(Selectstatuse.this)) {
                        Intent intent = new Intent(Selectstatuse.this, ConfrimedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {*/


                        if (!PreferenceManager.isConfrimed(Selectstatuse.this)) {
                            Intent intent1 = new Intent(Selectstatuse.this, ConfrimedActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Selectstatuse.this.startActivity(intent1);
                            Selectstatuse.this.finish();
                            AnimationsUtil.setSlideInAnimation(Selectstatuse.this);
                        } else {
                            if (!PreferenceManager.isLogin(this)) {
                                Intent intent = new Intent(this, PasswordActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                AnimationsUtil.setSlideInAnimation(this);
                            } else
                                {
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                AnimationsUtil.setSlideInAnimation(this);
                            }
                        }
                        //  }
                    }
                }
            }
        }
        else
        {



            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            AnimationsUtil.setSlideInAnimation(this);
        }
    }

    public void getAppSettings() {
        APIHelper.initialApiUsersContacts().getAppSettings().subscribe(settingsResponse ->
                {
                    PreferenceManager.setUnitBannerAdsID(this, settingsResponse.getUnitBannerID());
                    PreferenceManager.setShowBannerAds(this, settingsResponse.isAdsBannerStatus());
                    PreferenceManager.setUnitVideoAdsID(this, settingsResponse.getUnitVideoID());
                    PreferenceManager.setAppVideoAdsID(this, settingsResponse.getAppID());
                    PreferenceManager.setShowVideoAds(this, settingsResponse.isAdsVideoStatus());
                    PreferenceManager.setUnitInterstitialAdID(this, settingsResponse.getUnitInterstitialID());
                    PreferenceManager.setShowInterstitialAds(this, settingsResponse.isAdsInterstitialStatus());


                    int currentAppVersion;
                    if (PreferenceManager.getVersionApp(RooyeshApplication.getInstance()) != 0) {
                        currentAppVersion = PreferenceManager.getVersionApp(RooyeshApplication.getInstance());
                    } else {
                        currentAppVersion = AppHelper.getAppVersionCode(RooyeshApplication.getInstance());
                    }
                    if (currentAppVersion != 0 && currentAppVersion < settingsResponse.getAppVersion()) {
                        PreferenceManager.setVersionApp(this, currentAppVersion);
                        PreferenceManager.setIsOutDate(this, true);
                    } else {
                        PreferenceManager.setIsOutDate(this, false);
                    }
                },
                throwable -> {
                    //    AppHelper.LogCat("Error get settings info Welcome " + throwable.getMessage());
                    Log.i("erorrrrrrr", throwable.getMessage());
                });
    }
}
