package com.setayeshco.rooyesh.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abderrahim El imame on 20/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class PreferenceManager {


    private static SharedPreferences mSharedPreferences;
    private static final String KEY_USER_PREF = "KEY_USER_PREFERENCES";


    private static final String KEY_MEMBERS_SELECTED = "KEY_MEMBERS_SELECTED";
    private static final String KEY_IS_WAITING_FOR_SMS = "KEY_IS_WAITING_FOR_SMS";
    private static final String KEY_MOBILE_NUMBER = "KEY_MOBILE_NUMBER";
    private static final String KEY_LAST_BACKUP = "KEY_LAST_BACKUP";
    private static final String KEY_VERSION_APP = "KEY_VERSION_APP";
    private static final String KEY_NEW_USER = "KEY_NEW_USER";
    private static final String KEY_WALLPAPER_USER = "KEY_WALLPAPER_USER";
    private static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    private static final String KEY_APP_IS_OUT_DATE = "KEY_APP_IS_OUT_DATE";
    private static final String KEY_NEED_MORE_INFO = "KEY_NEED_MORE_INFO";
    private static final String BACKUP_FOLDER_KEY = "BACKUP_FOLDER_KEY";
    private static final String HAS_BACKUP = "HAS_BACKUP";
    private static final String IS_TEACHER = "IS_TEACHER";
    private static final String IS_CONFRIMED= "is_confirmed";
    private static final String IS_PASSWORD = "is_password";
    private static final String IS_STDNUMBER = "std_number";
    private static final String IS_LOGIN = "is_login";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";


    //teacher or student
    public static boolean setUserRole(Context mContext, boolean isTeacher) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_TEACHER, isTeacher);
        return editor.commit();
    }
    public static boolean setUserId(Context mContext, int userId) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(USER_ID, userId);
        return editor.commit();
    }

    public static boolean setUsername(Context mContext, String usernamne) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(USER_NAME, usernamne);
        return editor.commit();
    }

    public static boolean setIsConfrimed(Context mContext, boolean is_confirmed) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_CONFRIMED, is_confirmed);
        return editor.commit();
    }


    public static boolean setIsLogin(Context mContext, boolean isLogin) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_LOGIN, isLogin);
        return editor.commit();
    }


    public static boolean setUserPassword(Context mContext, String password) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(IS_PASSWORD, password);
        return editor.commit();
    }


    public static boolean setStdNumber(Context mContext, String std_number) {

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(IS_STDNUMBER, std_number);
        return editor.commit();
    }



    public static boolean setHasBackup(Context mContext, boolean hasBackup) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(HAS_BACKUP, hasBackup);
        return editor.commit();
    }


    public static boolean isHasBackup(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(HAS_BACKUP, false);
    }

    public static boolean saveBackupFolder(Context mContext, String folderPath) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(BACKUP_FOLDER_KEY, folderPath);
        return editor.commit();
    }


    public static String getBackupFolder(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(BACKUP_FOLDER_KEY, "");
    }

    /**
     * method to set Language
     *
     * @param lang     this is the first parameter for setLanguage  method
     * @param mContext this is the second parameter for setLanguage  method
     * @return return value
     */
    public static boolean setLanguage(Context mContext, String lang) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LANGUAGE, lang);
        return editor.commit();
    }

    /**
     * method to get Language
     *
     * @return return value
     */
    public static String getLanguage(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_LANGUAGE, "");
    }

    public static String getUserName(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(USER_NAME, "");
    }

    /**
     * method to set wallpaper
     *
     * @param wallpaper this is the first parameter for setWallpaper  method
     * @param mContext  this is the second parameter for setWallpaper  method
     * @return return value
     */
    public static boolean setWallpaper(Context mContext, String wallpaper) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_WALLPAPER_USER, wallpaper);
        return editor.commit();
    }

    /**
     * method to get wallpaper
     *
     * @return return value
     */
    public static String getWallpaper(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_WALLPAPER_USER, null);
    }

    /**
     * method to set token
     *
     * @param token    this is the first parameter for setToken  method
     * @param mContext this is the second parameter for setToken  method
     * @return return value
     */
    public static boolean setToken(Context mContext, String token) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token", token);
        return editor.commit();
    }

    /**
     * method to get token
     *
     * @return return value
     */
    public static String getToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("token", null);
    }


    /**
     * method to setID
     *
     * @param ID this is the first parameter for setID  method
     * @return return value
     */
    public static boolean setID(Context mContext, int ID) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("id", ID);
        return editor.commit();
    }

    /**
     * method to getID
     *
     * @return return value
     */
    public static int getID(Context mContext) {
        mSharedPreferences = RooyeshApplication.context.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
         int id= mSharedPreferences.getInt("id", 0);
        return  id;
    }


    /**
     * method to getPhone
     *
     * @return return value
     */
    public static String getPhone(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("phone", null);
    }

    /**
     * method to setPhone
     *
     * @param Phone this is the first parameter for setID  method
     * @return return value
     */
    public static boolean setPhone(Context mContext, String Phone) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phone", Phone);
        return editor.commit();
    }

    /**
     * method to setSocketID
     *
     * @param ID this is the first parameter for setID  method
     * @return return value
     */
    public static boolean setSocketID(Context mContext, String ID) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("socketId", ID);
        return editor.commit();
    }

    /**
     * method to getID
     *
     * @return return value
     */
    public static String getSocketID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("socketId", null);
    }

    /**
     * method to set contacts size
     *
     * @param size this is the first parameter for setContactSize  method
     * @return return value
     */
    public static boolean setContactSize(Context mContext, int size) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("size", size);
        return editor.commit();
    }

    /**
     * method to get contacts size
     *
     * @return return value
     */
    public static int getContactSize(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("size", 0);

    }


    /**
     * method to save new members to group
     *
     * @param membersGroupModels this is the second parameter for saveMembers  method
     */
    private static void saveMembers(Context mContext, List<MembersGroupModel> membersGroupModels) {
        //SharedPreferences settings;
        // SharedPreferences.Editor editor;

        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        Gson gson = new Gson();
        String jsonMembers = gson.toJson(membersGroupModels);

        editor.putString(KEY_MEMBERS_SELECTED, jsonMembers);

        editor.apply();
    }

    /**
     * method to add member
     *
     * @param membersGroupModel this is the second parameter for addMember  method
     */
    public static void addMember(Context mContext, MembersGroupModel membersGroupModel) {
        List<MembersGroupModel> membersGroupModelArrayList = getMembers(mContext);
        if (membersGroupModelArrayList == null)
            membersGroupModelArrayList = new ArrayList<MembersGroupModel>();
        membersGroupModelArrayList.add(membersGroupModel);
        saveMembers(mContext, membersGroupModelArrayList);
    }

    /**
     * method to remove member
     *
     * @param membersGroupModel this is the second parameter for removeMember  method
     */
    public static void removeMember(Context mContext, MembersGroupModel membersGroupModel) {
        ArrayList<MembersGroupModel> membersGroupModelArrayList = getMembers(mContext);
        if (membersGroupModelArrayList != null) {
            membersGroupModelArrayList.remove(membersGroupModel);
            saveMembers(mContext, membersGroupModelArrayList);
        }
    }

    /**
     * method to clear members
     */
    public static void clearMembers(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_MEMBERS_SELECTED, null);
        editor.apply();
    }

    /**
     * method to get all members
     *
     * @return return value
     */
    public static ArrayList<MembersGroupModel> getMembers(Context mContext) {
        try {
            List<MembersGroupModel> membersGroupModels;
            mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
            if (mSharedPreferences.contains(KEY_MEMBERS_SELECTED)) {
                String jsonMembers = mSharedPreferences.getString(KEY_MEMBERS_SELECTED, null);
                Gson gson = new Gson();
                MembersGroupModel[] membersItems = gson.fromJson(jsonMembers, MembersGroupModel[].class);
                membersGroupModels = Arrays.asList(membersItems);
                return new ArrayList<>(membersGroupModels);
            } else {
                return null;
            }

        } catch (Exception e) {
            AppHelper.LogCat("getMembers Exception " + e.getMessage());
            return null;
        }
    }


    /**
     * method to setUnitInterstitialAdID
     *
     * @param UnitId this is the first parameter for setUnitInterstitialAdID  method
     * @return return value
     */
    public static boolean setUnitInterstitialAdID(Context mContext, String UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("InterstitialUnitId", UnitId);
        return editor.commit();
    }

    /**
     * method to getUnitInterstitialAdID
     *
     * @return return value
     */
    public static String getUnitInterstitialAdID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("InterstitialUnitId", null);
    }

    /**
     * method to setShowInterstitialAds
     *
     * @param UnitId this is the first parameter for setShowInterstitialAds  method
     * @return return value
     */
    public static boolean setShowInterstitialAds(Context mContext, Boolean UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ShowInterstitialAds", UnitId);
        return editor.commit();
    }

    /**
     * method to ShowInterstitialrAds
     *
     * @return return value
     */
    public static boolean ShowInterstitialrAds(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("ShowInterstitialAds", false);
    }

    /**
     * method to setUnitBannerAdsID
     *
     * @param UnitId this is the first parameter for setUnitBannerAdsID  method
     * @return return value
     */
    public static boolean setUnitBannerAdsID(Context mContext, String UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("BannerUnitId", UnitId);
        return editor.commit();
    }

    /**
     * method to getUnitBannerAdsID
     *
     * @return return value
     */
    public static String getUnitBannerAdsID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("BannerUnitId", null);
    }


    /**
     * method to setShowBannerAds
     *
     * @param UnitId this is the first parameter for setShowBannerAds  method
     * @return return value
     */
    public static boolean setShowBannerAds(Context mContext, Boolean UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ShowBannerAds", UnitId);
        return editor.commit();
    }

    /**
     * method to ShowBannerAds
     *
     * @return return value
     */
    public static boolean ShowBannerAds(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("ShowBannerAds", false);
    }


    /**
     * method to setUnitVideoAdsID
     *
     * @param UnitId this is the first parameter for setUnitVideoAdsID  method
     * @return return value
     */
    public static boolean setUnitVideoAdsID(Context mContext, String UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("VideoUnitId", UnitId);
        return editor.commit();
    }

    /**
     * method to getUnitVideoAdsID
     *
     * @return return value
     */
    public static String getUnitVideoAdsID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("VideoUnitId", null);
    }

    /**
     * method to setAppVideoAdsID
     *
     * @param UnitId this is the first parameter for setAppVideoAdsID  method
     * @return return value
     */
    public static boolean setAppVideoAdsID(Context mContext, String UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("VideoAppId", UnitId);
        return editor.commit();
    }

    /**
     * method to getAppVideoAdsID
     *
     * @return return value
     */
    public static String getAppVideoAdsID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("VideoAppId", null);
    }


    /**
     * method to setShowVideoAds
     *
     * @param UnitId this is the first parameter for setShowVideoAds  method
     * @return return value
     */
    public static boolean setShowVideoAds(Context mContext, Boolean UnitId) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ShowVideoAds", UnitId);
        return editor.commit();
    }

    /**
     * method to ShowVideoAds
     *
     * @return return value
     */
    public static boolean ShowVideoAds(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("ShowVideoAds", false);
    }

    /**
     * method to set var as the info aren't incomplete
     *
     * @param isNew this is parameter for setIsNewUser  method
     */
    public static boolean setIsNeedInfo(Context mContext, Boolean isNew) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_NEED_MORE_INFO, isNew);
        return editor.commit();
    }

    /**
     * method to check if user is provide more info
     *
     * @return return value
     */
    public static boolean isNeedProvideInfo(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_NEED_MORE_INFO, false);
    }

    /**
     * method to set user waiting for SMS (code verification)
     *
     * @param isWaiting this is parameter for setIsWaitingForSms  method
     */
    public static boolean setIsWaitingForSms(Context mContext, Boolean isWaiting) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        return editor.commit();
    }

    /**
     * method to check if user is waiting for SMS
     *
     * @return return value
     */
    public static boolean isWaitingForSms(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    /**
     * method to set mobile phone
     *
     * @param mobileNumber this is parameter for setMobileNumber  method
     */
    public static boolean setMobileNumber(Context mContext, String mobileNumber) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        return editor.commit();
    }

    /**
     * method to get mobile phone
     *
     * @return return value
     */
    public static String getMobileNumber(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_MOBILE_NUMBER, null);
    }


    /**
     * method to set last backup
     *
     * @param hasBackup this is parameter for setLastBackup  method
     */
    public static boolean setLastBackup(Context mContext, String hasBackup) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LAST_BACKUP, hasBackup);
        return editor.commit();
    }

    /**
     * method to get last backup
     *
     * @return return value
     */
    public static String lastBackup(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_LAST_BACKUP, null);
    }

    /**
     * method to set var as the user is new on the app
     *
     * @param isNew this is parameter for setIsNewUser  method
     */
    public static boolean setIsNewUser(Context mContext, Boolean isNew) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_NEW_USER, isNew);
        return editor.commit();
    }

    /**
     * method to check if user is new here the app
     *
     * @return return value
     */
    public static boolean isNewUser(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_NEW_USER, false);
    }


    /**
     * method to set last backup
     *
     * @param version this is parameter for setLastBackup  method
     */
    public static boolean setVersionApp(Context mContext, int version) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_VERSION_APP, version);
        return editor.commit();
    }

    /**
     * method to get last backup
     *
     * @return return value
     */
    public static int getVersionApp(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(KEY_VERSION_APP, 0);
    }

    /**
     * method to set the app is out date
     *
     * @param isNew this is parameter for setIsOutDate  method
     */
    public static boolean setIsOutDate(Context mContext, Boolean isNew) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_APP_IS_OUT_DATE, isNew);
        return editor.commit();
    }

    /**
     * method to check if the app is out date
     *
     * @return return value
     */
    public static boolean isOutDate(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_APP_IS_OUT_DATE, false);
    }




    public static boolean isTeacher(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(IS_TEACHER, false);

    }

    public static boolean isConfrimed(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(IS_CONFRIMED, false);

    }

    public static boolean isLogin(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(IS_LOGIN, false);

    }

    public static String getPassword(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IS_PASSWORD, "no_password");

    }
    public static String getStdNumber(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IS_STDNUMBER, "no_std_number");

    }
    public static int getUserId(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(USER_ID, -1);

    }

    /**
     * method to check if the app is out date
     *
     * @return return value
     */
    public static void clearPreferences(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String token= mSharedPreferences.getString("token", null);
        editor.clear();
       // editor.putString("token",token);
       // editor.putBoolean(IS_LOGIN,false);
        editor.apply();
    }
}
