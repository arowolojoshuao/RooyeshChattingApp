package com.setayeshco.rooyesh.activities.call;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.settings.PreferenceSettingsManager;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.models.calls.CallSaverModel;
import com.setayeshco.rooyesh.models.calls.CallsInfoModel;
import com.setayeshco.rooyesh.models.calls.CallsModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.calls.CallsPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Abderrahim El imame on 10/13/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class IncomingCallActivity extends AppCompatActivity {


    private String userPhone;
    private String userSocketId;
    private String callerSocketId;
    private Vibrator vibrator;
    private MediaPlayer mMediaPlayer;
    private Socket mSocket;
    private boolean isVideoCall;
    private boolean isSaved = false;
    Timer timer;
    long autoRejectDelay = 15000;
    @BindView(R.id.caller_name)
    TextView callerName;

    @BindView(R.id.caller_image)
    ImageView callerImageView;


    @BindView(R.id.caller_phone)
    TextView callerPhoneField;

    @BindView(R.id.incoming_type)
    TextView incomingCallType;

    @BindView(R.id.accept_call)
    AppCompatImageButton sliderAccept;

    @BindView(R.id.reject_call)
    AppCompatImageButton sliderReject;


    String callerPhone;
    String callerImage;
    private CallsPresenter callsPresenter;
    private int callerID;
    private ContactsModel mContactsModel;
    private AudioManager mAudioManager;
    private int originalVolume;
    private int lastID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (AppHelper.isAndroid5()) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_incoming_call);
        ButterKnife.bind(this);
        setTypeFaces();
        connectToChatServer();

        Bundle extras = getIntent().getExtras();
        callerSocketId = extras.getString(AppConstants.CALLER_SOCKET_ID);
        userSocketId = extras.getString(AppConstants.USER_SOCKET_ID);
        callerPhone = extras.getString(AppConstants.CALLER_PHONE);
        callerImage = extras.getString(AppConstants.CALLER_IMAGE);
        userPhone = extras.getString(AppConstants.USER_PHONE);
        callerID = extras.getInt(AppConstants.CALLER_ID);
        isVideoCall = extras.getBoolean(AppConstants.IS_VIDEO_CALL);
        callsPresenter = new CallsPresenter(this, callerID);
        callsPresenter.onCreate();
        getUserInfo();
        saveToDataBase();
        String name = UtilsPhone.getContactName(this, callerPhone);
        if (name != null) {
            callerName.setText(name);
            callerPhoneField.setVisibility(View.VISIBLE);
            callerPhoneField.setText(callerPhone);
        } else {
            callerPhoneField.setVisibility(View.GONE);
            callerName.setText(callerPhone);
        }

        if (isVideoCall) {
            incomingCallType.setText(getString(R.string.video_call));
        } else {
            incomingCallType.setText(getString(R.string.voice_call));
        }

        sliderReject.setOnClickListener(v -> {
            AppHelper.LogCat("clicked sliderReject");
            rejectCall(false);

        });

        sliderAccept.setOnClickListener(v -> {
            AppHelper.LogCat("clicked sliderAccept");
            acceptCall();
        });


        AnimationsUtil.ShakeAnimation(this, findViewById(R.id.accept_call));
        AnimationsUtil.ShakeAnimation(this, findViewById(R.id.reject_call));
        if (PreferenceSettingsManager.conversation_tones(this)) {
            Uri uri = PreferenceSettingsManager.getDefault_calls_notifications_settings_tone(this);
            if (uri != null) {
                try {
                    mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    if (mMediaPlayer == null) return;
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                } catch (Exception e) {
                    AppHelper.LogCat(e.getMessage());
                }
            }
        }

        if (PreferenceSettingsManager.getDefault_calls_notifications_settings_vibrate(this)) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] vibrate = new long[]{2000, 2000, 2000, 2000, 2000};
            vibrator.vibrate(vibrate, 1);
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                rejectCall(true);
            }

        }, autoRejectDelay);


    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            callerName.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            callerPhoneField.setTypeface(AppHelper.setTypeFace(this, "IranSans"));

        }
    }


    public void getUserInfo() {
        try {
            MemoryCache memoryCache = new MemoryCache();
            Realm realm = RooyeshApplication.getRealmDatabaseInstance();

            mContactsModel = realm.where(ContactsModel.class).equalTo("phone", callerPhone).findFirst();
            Bitmap bitmap;
            bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, callerImage, this, mContactsModel.getId(), AppConstants.USER, AppConstants.FULL_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, callerImageView);
            } else {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        callerImageView.setImageBitmap(bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        callerImageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        callerImageView.setImageDrawable(placeHolderDrawable);
                    }
                };
                callerImageView.setTag(target);
                Picasso.with(this)
                        .load(EndPoints.PROFILE_IMAGE_URL + callerImage)
                        .transform(new CropCircleTransformation())
                        .placeholder(R.drawable.image_holder_ur_circle)
                        .error(R.drawable.image_holder_ur_circle)
                        .into(target);
            }

            if (!realm.isClosed())
                realm.close();
        } catch (Exception e) {
            AppHelper.LogCat(e.getMessage());
        }

    }

    private int getHistoryCallId(int fromId, int toId, boolean isVideoCall, Realm realm) {
        String type;
        if (isVideoCall)
            type = AppConstants.VIDEO_CALL;
        else
            type = AppConstants.VOICE_CALL;


        try {
            CallsModel callsModel = realm.where(CallsModel.class)
                    .equalTo("from", fromId)
                    .equalTo("to", toId)
                    .equalTo("received", true)
                    .equalTo("type", type)
                    .findAll().first();
            return callsModel.getId();
        } catch (Exception e) {
            AppHelper.LogCat("call history id Exception MainService" + e.getMessage());
            return 0;
        }
    }


    public void saveToDataBase() {
        if (mContactsModel == null) return;
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
        DateTime current = new DateTime();
        String callTime = String.valueOf(current);

        int historyCallId = getHistoryCallId(mContactsModel.getId(), PreferenceManager.getID(this), isVideoCall, realm);

        if (historyCallId == 0) {
            realm.executeTransactionAsync(realm1 -> {
                ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("phone", callerPhone).findFirst();

                int lastID = RealmBackupRestore.getCallLastId();
                CallsModel callsModel = new CallsModel();
                callsModel.setId(lastID);
                if (isVideoCall)
                    callsModel.setType(AppConstants.VIDEO_CALL);
                else
                    callsModel.setType(AppConstants.VOICE_CALL);
                callsModel.setContactsModel(contactsModel1);
                callsModel.setPhone(callerPhone);
                callsModel.setCounter(1);
                callsModel.setFrom(contactsModel1.getId());
                callsModel.setTo(PreferenceManager.getID(this));
                callsModel.setDuration("00:00");
                callsModel.setDate(callTime);
                callsModel.setReceived(true);

                CallsInfoModel callsInfoModel = new CallsInfoModel();
                RealmList<CallsInfoModel> callsInfoModelRealmList = new RealmList<CallsInfoModel>();
                int lastInfoID = RealmBackupRestore.getCallInfoLastId();
                callsInfoModel.setId(lastInfoID);
                if (isVideoCall)
                    callsInfoModel.setType(AppConstants.VIDEO_CALL);
                else
                    callsInfoModel.setType(AppConstants.VOICE_CALL);
                callsInfoModel.setContactsModel(contactsModel1);
                callsInfoModel.setPhone(callerPhone);
                callsInfoModel.setCallId(lastID);
                callsInfoModel.setFrom(contactsModel1.getId());
                callsInfoModel.setTo(PreferenceManager.getID(this));
                callsInfoModel.setDuration("00:00");
                callsInfoModel.setDate(callTime);
                callsInfoModel.setReceived(true);
                callsInfoModelRealmList.add(callsInfoModel);
                callsModel.setCallsInfoModels(callsInfoModelRealmList);
                realm1.copyToRealmOrUpdate(callsModel);
                this.lastID = lastID;
                //EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CALL_NEW_ROW, lastID));
            }, () -> {
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CALL_NEW_ROW, this.lastID));
            });
        } else {

            realm.executeTransactionAsync(realm1 -> {
                ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("phone", callerPhone).findFirst();

                int callCounter;
                CallsModel callsModel;
                RealmQuery<CallsModel> callsModelRealmQuery = realm1.where(CallsModel.class).equalTo("id", historyCallId);
                callsModel = callsModelRealmQuery.findAll().first();

                callCounter = callsModel.getCounter();
                callCounter++;
                callsModel.setDate(callTime);
                callsModel.setCounter(callCounter);
                callsModel.setDuration("00:00");
                CallsInfoModel callsInfoModel = new CallsInfoModel();
                RealmList<CallsInfoModel> callsInfoModelRealmList = callsModel.getCallsInfoModels();
                int lastInfoID = RealmBackupRestore.getCallInfoLastId();
                callsInfoModel.setId(lastInfoID);
                if (isVideoCall)
                    callsInfoModel.setType(AppConstants.VIDEO_CALL);
                else
                    callsInfoModel.setType(AppConstants.VOICE_CALL);
                callsInfoModel.setContactsModel(contactsModel1);
                callsInfoModel.setPhone(callerPhone);
                callsInfoModel.setCallId(callsModel.getId());
                callsInfoModel.setFrom(contactsModel1.getId());
                callsInfoModel.setTo(PreferenceManager.getID(this));
                callsInfoModel.setDuration("00:00");
                callsInfoModel.setDate(callTime);
                callsInfoModel.setReceived(true);
                callsInfoModelRealmList.add(callsInfoModel);
                callsModel.setCallsInfoModels(callsInfoModelRealmList);

                realm1.copyToRealmOrUpdate(callsModel);
                //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CALL_OLD_ROW, historyCallId));
            }, () -> {
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CALL_OLD_ROW, historyCallId));
            });
        }

        if (!isSaved) {
            try {
                RealmQuery<CallsModel> callsModelRealmQuery;
                String isVideo;
                if (isVideoCall) {
                    isVideo = "video";
                    callsModelRealmQuery = realm.where(CallsModel.class).equalTo("type", AppConstants.VIDEO_CALL).equalTo("from", mContactsModel.getId());
                } else {
                    isVideo = "audio";
                    callsModelRealmQuery = realm.where(CallsModel.class).equalTo("type", AppConstants.VOICE_CALL).equalTo("from", mContactsModel.getId());
                }
                CallsModel callsModel = null;
                if (callsModelRealmQuery != null && callsModelRealmQuery.isValid())
                    callsModel = callsModelRealmQuery.findAll().first();
                if (callsModel != null && callsModel.isValid()) {
                    CallSaverModel callSaverModel = new CallSaverModel();
                    callSaverModel.setToId(callsModel.getTo());
                    callSaverModel.setFromId(callsModel.getFrom());

                    callSaverModel.setDate(callsModel.getDate());
                    callSaverModel.setDuration(callsModel.getDuration());
                    callSaverModel.setIsVideo(isVideo);
                    APIHelper.initialApiUsersContacts().saveReceivedCall(callSaverModel).subscribe(Response -> {
                        isSaved = Response.isSuccess();
                    }, throwable -> {
                        isSaved = false;
                    });
                }
            } catch (Exception e) {
                AppHelper.LogCat(e.getMessage());
            }

        }

        if (!realm.isClosed()) realm.close();
    }

    public void showUserInfo(ContactsModel contactsModel) {

    }


    /**
     * method to accept incoming calls
     */
    public void acceptCall() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }

        if (mMediaPlayer != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
        Intent intent = new Intent(IncomingCallActivity.this, CallActivity.class);
        intent.putExtra(AppConstants.USER_SOCKET_ID, userSocketId);
        intent.putExtra(AppConstants.USER_PHONE, userPhone);
        intent.putExtra(AppConstants.CALLER_PHONE_ACCEPT, callerPhone);
        intent.putExtra(AppConstants.CALLER_IMAGE, callerImage);
        intent.putExtra(AppConstants.CALLER_SOCKET_ID, callerSocketId);
        intent.putExtra(AppConstants.IS_VIDEO_CALL, isVideoCall);
        intent.putExtra(AppConstants.IS_ACCEPTED_CALL, true);
        intent.putExtra(AppConstants.CALLER_ID, callerID);
        startActivity(intent);
        AnimationsUtil.setSlideInAnimation(this);
        if (!isVideoCall) {
            try {
                JSONObject message = new JSONObject();
                message.put("userSocketId", userSocketId);
                message.put("callerSocketId", callerSocketId);
                mSocket.emit(AppConstants.SOCKET_ACCEPT_NEW_CALL, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * Publish a hangUp command if rejecting call.
     *
     * @param noAnswer
     */
    public void rejectCall(boolean noAnswer) {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        if (mMediaPlayer != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }

        try {
            JSONObject message = new JSONObject();
            message.put("userSocketId", userSocketId);
            message.put("callerSocketId", callerSocketId);
            if (noAnswer)
                message.put("reason", AppConstants.NO_ANSWER);
            else
                message.put("reason", AppConstants.IGNORED);
            mSocket.emit(AppConstants.SOCKET_REJECT_NEW_CALL, message);
        } catch (JSONException e) {
            AppHelper.LogCat(" JSONException IncomingCallActivity rejectCall " + e.getMessage());
        }
        finish();
        AnimationsUtil.setSlideOutAnimation(this);


    }


    private void connectToChatServer() {
        RooyeshApplication app = (RooyeshApplication) getApplication();
        mSocket = app.getSocket();
       if (mSocket == null) {
            RooyeshApplication.connectSocket();
            mSocket = app.getSocket();
        }
        mSocket.on(AppConstants.SOCKET_HANGUP_CALL, onHangUpCallResponse);
        if (!mSocket.connected())
            mSocket.connect();

    }

    private Emitter.Listener onHangUpCallResponse = args -> {

        JSONObject data = (JSONObject) args[0];
        try {
            String from = data.getString("userSocketId");
            if (from.equals(PreferenceManager.getSocketID(RooyeshApplication.getInstance())))
                return;
            finish();
            AnimationsUtil.setSlideOutAnimation(this);
        } catch (JSONException e) {
            AppHelper.LogCat(" onHangUpCallResponse JSONException " + e.getMessage());
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        callsPresenter.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        if (mMediaPlayer != null) {
            try {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer = null;
            } catch (Exception e) {
                AppHelper.LogCat(e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        rejectCall(false);
    }


}
