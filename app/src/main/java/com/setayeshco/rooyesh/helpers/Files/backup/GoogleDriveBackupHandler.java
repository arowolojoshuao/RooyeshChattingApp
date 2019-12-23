package com.setayeshco.rooyesh.helpers.Files.backup;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.models.users.Pusher;

import java.lang.ref.WeakReference;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Abderrahim El imame on 6/16/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class GoogleDriveBackupHandler implements Backup, GoogleApiClient.OnConnectionFailedListener {
    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private WeakReference<Activity> activityRef;


    @Override
    public void init(@NonNull final Activity activity) {
        this.activityRef = new WeakReference<>(activity);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // Do nothing
                        AppHelper.LogCat("connected to google drive");
                        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_GOOGLE_DRIVE));
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public GoogleApiClient getClient() {
        return googleApiClient;
    }

    @Override
    public void start() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void stop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult result) {
        AppHelper.LogCat("Connection Failed" + " GoogleApiClient connection failed: " + result.toString());
        if (result.hasResolution() && activityRef != null && activityRef.get() != null) {
            Activity a = activityRef.get();
            // show the localized error dialog.
            try {
                result.startResolutionForResult(a, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(a, result.getErrorCode(), 0).show();
            }
        } else {
            AppHelper.LogCat("error" + " cannot resolve connection issue");
        }
    }
}
