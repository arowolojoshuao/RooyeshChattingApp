package com.setayeshco.rooyesh.services;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.ConfrimedActivity;
import com.setayeshco.rooyesh.activities.call.IncomingCallActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.main.PreMainActivity;
import com.setayeshco.rooyesh.activities.main.welcome.CompleteRegistrationActivity;
import com.setayeshco.rooyesh.activities.main.welcome.PasswordActivity;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.activities.profile.Selectstatuse;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;

/**
 * Created by Abderrahim El imame on 23/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SMSVerificationService extends IntentService {


    public SMSVerificationService() {
        super(SMSVerificationService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String code = intent.getStringExtra("code");
            boolean registration = intent.getBooleanExtra("register", true);
            verifyUser(code, registration);
        }
    }

    private void verifyUser(String code, boolean registration) {
        if (registration) {
            APIHelper.initializeAuthService().verifyUser(code).subscribe(joinModelResponse -> {
                if (!joinModelResponse.isSuccess()) {
                    Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   SMSVerificationService00 ");
                    Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   pass   "+joinModelResponse.getPassword());
                 //   PreferenceManager.setUserPassword(SMSVerificationService.this,joinModelResponse.getPassword());
                    PreferenceManager.setIsNewUser(SMSVerificationService.this, true);
                    PreferenceManager.setID(SMSVerificationService.this, joinModelResponse.getUserID());
                    PreferenceManager.setToken(SMSVerificationService.this, joinModelResponse.getToken());
                    PreferenceManager.setIsWaitingForSms(SMSVerificationService.this, false);
                    PreferenceManager.setPhone(SMSVerificationService.this, PreferenceManager.getMobileNumber(SMSVerificationService.this));
                    PreferenceManager.setUserId(SMSVerificationService.this,joinModelResponse.getUserID());
                    if (joinModelResponse.isHasBackup()) {
                   /*     if (!PreferenceManager.isConfrimed(SMSVerificationService.this)) {
                            Intent intent = new Intent(SMSVerificationService.this, ConfrimedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }else {*/
                            Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   SMSVerificationService11 ");
                            Log.d("ISTEACHER", "ISTEACHERSMSVerif1   >>>  " + PreferenceManager.isTeacher(SMSVerificationService.this));
                            PreferenceManager.saveBackupFolder(SMSVerificationService.this, joinModelResponse.getBackup_hash());
                            PreferenceManager.setHasBackup(SMSVerificationService.this, true);
                            Log.d("PASSWORDTEST", " 44444    >>>  " + PreferenceManager.getPassword(SMSVerificationService.this));
                            Intent intent = new Intent(SMSVerificationService.this, PasswordActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                      //  }
                    } else if (joinModelResponse.isHasProfile()) {

                    /*    if (!PreferenceManager.isConfrimed(SMSVerificationService.this)) {
                            Intent intent = new Intent(SMSVerificationService.this, ConfrimedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }else {*/



                        if(!PreferenceManager.isConfrimed(SMSVerificationService.this)) {
                            Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity8888888888 ");
                            Intent intent1 = new Intent(SMSVerificationService.this, ConfrimedActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SMSVerificationService.this.startActivity(intent1);
                        }else {


                            Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   SMSVerificationService22 ");
                            Log.d("ISTEACHER", "ISTEACHERSMSVerif2   >>>  " + PreferenceManager.isTeacher(SMSVerificationService.this));
                            Log.d("PASSWORDTEST", " 33333    >>>  " + PreferenceManager.getPassword(SMSVerificationService.this));
                            PreferenceManager.setHasBackup(SMSVerificationService.this, false);
                            PreferenceManager.setIsNeedInfo(SMSVerificationService.this, false);
                            Intent intent = new Intent(SMSVerificationService.this, PasswordActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //     Toast.makeText(SMSVerificationService.this,"mmmmmm",Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                      //  }


                    } else {
                        Log.d("CONFRIMED","confrimed SMSVerificationService  >>>>>> "+ PreferenceManager.isConfrimed(SMSVerificationService.this));


                            Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   SMSVerificationService33 ");
                            Log.d("ISTEACHER", "ISTEACHERSMSVerif3   >>>  " + PreferenceManager.isTeacher(SMSVerificationService.this));
                            PreferenceManager.setHasBackup(SMSVerificationService.this, false);
                            PreferenceManager.setIsNeedInfo(SMSVerificationService.this, true);
                            Intent intent = new Intent(SMSVerificationService.this, CompleteRegistrationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                    }
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(SMSVerificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(getPackageName() + "closeWelcomeActivity"));
                    Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   SMSVerificationService44 ");

                } else {
                    AppHelper.CustomToast(SMSVerificationService.this, joinModelResponse.getMessage());
                }
            }, throwable -> {
                AppHelper.LogCat("SMS verification failure  SMSVerificationService" + throwable.getMessage());
            });
        } else {
            APIHelper.initialApiUsersContacts().deleteAccountConfirmation(code).subscribe(statusResponse -> {
                if (statusResponse.isSuccess()) {
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(SMSVerificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(getPackageName() + "closeDeleteAccountActivity"));
                } else {
                    AppHelper.CustomToast(SMSVerificationService.this, statusResponse.getMessage());
                }
            }, throwable -> {
                AppHelper.LogCat("SMS verification failure  SMSVerificationService" + throwable.getMessage());
            });

        }


        UsersService mUsersContacts = new UsersService(SMSVerificationService.this,new APIService(SMSVerificationService.this));
        mUsersContacts.getContactInfo(PreferenceManager.getUserId(SMSVerificationService.this)).subscribe(contactsModel -> {
            new IncomingCallActivity().showUserInfo(contactsModel);
            PreferenceManager.setUserPassword(SMSVerificationService.this,contactsModel.getPassword());

            if (contactsModel.getIs_confirmed().equals("1")) {
                PreferenceManager.setIsConfrimed(SMSVerificationService.this, true);
            }else {
                PreferenceManager.setIsConfrimed(SMSVerificationService.this,false);
            }

        }, throwable -> {
            AppHelper.LogCat(throwable.getMessage());
        });

    }
}
