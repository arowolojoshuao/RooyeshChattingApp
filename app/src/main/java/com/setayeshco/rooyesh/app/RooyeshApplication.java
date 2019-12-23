package com.setayeshco.rooyesh.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.accountkit.AccountKit;
import com.orhanobut.logger.Logger;
import com.setayeshco.rooyesh.BuildConfig;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.ExceptionHandler;
import com.setayeshco.rooyesh.helpers.Files.backup.Backup;
import com.setayeshco.rooyesh.helpers.Files.backup.GoogleDriveBackupHandler;
import com.setayeshco.rooyesh.helpers.ForegroundRuning;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.receivers.NetworkChangeListener;
import com.setayeshco.rooyesh.services.BootService;
import com.setayeshco.rooyesh.services.MainService;

import java.net.URISyntaxException;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class RooyeshApplication extends Application {

    public static Context context;

    static RooyeshApplication mInstance;
    public static final long TIMEOUT = 60 * 1000;
    private static Socket mSocket = null;
   // private static Socket reciveSocket = null;

    public static void connectSocket() {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.timeout = TIMEOUT; //set -1 to  disable it
        options.reconnection = true;
        options.reconnectionDelay = (long) 3000;
        options.reconnectionDelayMax = (long) 60000;
        options.reconnectionAttempts = 99999;
        options.query = "token=" + AppConstants.APP_KEY_SECRET;


        try {
            mSocket = IO.socket(EndPoints.BACKEND_CHAT_SERVER_URL, options);
          //  reciveSocket = IO.socket(EndPoints.BACKEND_CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            AppHelper.LogCat("URISyntaxException" + e.getMessage());
        }
        if (!mSocket.connected())
            mSocket.connect();
      /*  if (!reciveSocket.connected())
            reciveSocket.connect();*/

    }


    public Socket getSocket() {
        return mSocket;
    }
 /*   public Socket getReciveSocket() {
        return reciveSocket;
    }
*/
    public static synchronized RooyeshApplication getInstance() {
        return mInstance;
    }

    public void setmInstance(RooyeshApplication mInstance) {
        RooyeshApplication.mInstance = mInstance;
    }

    public static void C(String key,String value){

        Log.i("Majid",key+" ==> "+value);
    }

    public static void setupCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build();
        Fabric.with(mInstance, crashlyticsKit, new Crashlytics());
        Crashlytics.setUserEmail(PreferenceManager.getPhone(getInstance()));
        Crashlytics.setUserName(PreferenceManager.getPhone(getInstance()));
        Crashlytics.setUserIdentifier(String.valueOf(PreferenceManager.getID(getInstance())));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        setmInstance(this);
        if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT)
            AccountKit.initialize(getApplicationContext(), () -> AppHelper.LogCat(" AccountKit onInitialized "));
        if (AppConstants.CRASH_LYTICS)
            RooyeshApplication.setupCrashlytics();
        initRealm();
        ForegroundRuning.init(this);

        startService(new Intent(this, BootService.class));
        if (AppConstants.DEBUGGING_MODE)
            Logger.init(AppConstants.TAG).hideThreadInfo();

        if (AppConstants.ENABLE_CRASH_HANDLER)
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        if (!PreferenceManager.getLanguage(this).equals(""))
            setDefaultLocale(this, new Locale(PreferenceManager.getLanguage(this)));
        else {
            if (Locale.getDefault().toString().startsWith("en_")) {
                PreferenceManager.setLanguage(this, "en");
            }
        }


    }


    @SuppressWarnings("deprecation")
    protected void setDefaultLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        context.getResources().updateConfiguration(appConfig, context.getResources().getDisplayMetrics());

    }

    public void setConnectivityListener(NetworkListener listener) {
        NetworkChangeListener.networkListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MainService.disconnectSocket();
    }

    @NonNull
    public Backup getBackup() {
        return new GoogleDriveBackupHandler();
    }


    private static RealmConfiguration getRealmDatabaseConfiguration() {
        return new RealmConfiguration.Builder().name(getInstance().getString(R.string.app_name) + PreferenceManager.getToken(getInstance()) + ".realm").deleteRealmIfMigrationNeeded().build();
      //  return new RealmConfiguration.Builder().name(getInstance().getString(R.string.app_name) + PreferenceManager.getToken(getInstance()) + ".realm").build();
    }

    public static Realm getRealmDatabaseInstance() {
        return Realm.getInstance(getRealmDatabaseConfiguration());
    }

    public static boolean DeleteRealmDatabaseInstance() {
        return Realm.deleteRealm(getRealmDatabaseConfiguration());
    }

    public void initRealm() {
        Realm.init(this);
    }

}
