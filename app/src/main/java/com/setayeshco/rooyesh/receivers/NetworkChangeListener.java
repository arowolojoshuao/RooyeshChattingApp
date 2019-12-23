package com.setayeshco.rooyesh.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.ForegroundRuning;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.services.MainService;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Abderrahim El imame on 8/18/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class NetworkChangeListener extends BroadcastReceiver {

    private boolean is_Connected = false;
    public static NetworkListener networkListener;

    public NetworkChangeListener() {
        super();
    }

    @Override
    public void onReceive(Context mContext, Intent intent) {
        if (PreferenceManager.getToken(mContext) != null) {
            APIHelper.initialApiUsersContacts().checkIfUserSession().subscribe(networkModel -> {
                if (networkModel.isConnected()) {
                    is_Connected = true;
                } else {
                    is_Connected = false;

                    if (ForegroundRuning.get().isForeground()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setMessage(R.string.your_session_expired);
                        alert.setPositiveButton(R.string.ok, (dialog, which) -> {
                            PreferenceManager.setToken(mContext, null);
                            PreferenceManager.setID(mContext, 0);
                            PreferenceManager.setSocketID(mContext, null);
                            PreferenceManager.setPhone(mContext, null);
                            PreferenceManager.setIsWaitingForSms(mContext, false);
                            PreferenceManager.setMobileNumber(mContext, null);
                            RealmBackupRestore.deleteData(mContext);
                            AppHelper.deleteCache(mContext);
                            Intent mIntent1 = new Intent(mContext, WelcomeActivity.class);
                            mIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(RooyeshApplication.getInstance().getBaseContext(), 0, mIntent1, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager mgr = (AlarmManager) RooyeshApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                            System.exit(2);
                        });
                        alert.setCancelable(false);
                        alert.show();
                    }
                }
            }, throwable -> {
                is_Connected = false;
            });


            new Handler().postDelayed(() -> isNetworkAvailable(mContext), 2000);
        }
    }

    private void isNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean is_Connecting = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (networkListener != null) {
            networkListener.onNetworkConnectionChanged(is_Connecting, is_Connected);
        }

        if (!is_Connecting && !is_Connected) {
            mContext.getApplicationContext().stopService(new Intent(mContext.getApplicationContext(), MainService.class));
            AppHelper.LogCat("Connection is not available");
            Realm realm = RooyeshApplication.getRealmDatabaseInstance();
            RealmResults<ContactsModel> contactsModels = realm.where(ContactsModel.class).notEqualTo("id", PreferenceManager.getID(mContext)).equalTo("Exist", true).equalTo("Linked", true).equalTo("Activate", true).findAllSorted("username", Sort.ASCENDING);
            if (contactsModels.size() == 0) return;
            for (ContactsModel contactsModels1 : contactsModels) {
                if (contactsModels1.getUserState() != null && (contactsModels1.getUserState().equals(AppConstants.STATUS_USER_CONNECTED_STATE) || contactsModels1.getUserState().equals(AppConstants.STATUS_USER_LAST_SEEN_STATE))) {
                    realm.executeTransaction(realm1 -> {
                        ContactsModel userModel = realm1.where(ContactsModel.class).equalTo("id", contactsModels1.getId()).findFirst();
                        userModel.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
                        realm1.copyToRealmOrUpdate(userModel);
                    });
                }
            }
            if (!realm.isClosed()) realm.close();
        } else if (is_Connecting && is_Connected) {
            AppHelper.LogCat("Connection is available");
            AppHelper.restartService();
            new Handler().postDelayed(() -> {
                MainService.unSentMessages(mContext);
            }, 2000);
        } else {
            AppHelper.LogCat("Connection is available but waiting for network");
            //mContext.getApplicationContext().stopService(new Intent(mContext.getApplicationContext(), MainService.class));
        }
    }
}
