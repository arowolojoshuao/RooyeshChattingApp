package com.setayeshco.rooyesh.helpers.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.popups.MessagesPopupActivity;
import com.setayeshco.rooyesh.activities.settings.PreferenceSettingsManager;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.ui.CropSquareTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import io.realm.Realm;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Abderrahim El imame on 6/19/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class NotificationsManager {


    private NotificationManager mNotificationManager;
    private String username;
    // private int numMessages = 0;
    private MemoryCache memoryCache;

    public NotificationsManager() {
    }

    public void showUserNotification(Context mContext, int conversationID, String phone, String message, int userId, String Avatar) {
        memoryCache = new MemoryCache();
        //  String text = UtilsString.unescapeJava(message);
        Intent messagingIntent = new Intent(mContext, MainActivity.class);
        messagingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent messagingPopupIntent = new Intent(mContext, MainActivity.class);
        messagingPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int counterUnreadConversation = getUnreadConversationsCounter();


        int counterUnreadMessages = getUnreadMessagesCounter();
        List<MessagesModel> msgs = getNotificationMessages(userId);

        if (counterUnreadConversation == 1) {
            /**
             * this for default activity
             */



            //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


            Bundle bundle = new Bundle();
            bundle.putInt("conversationID", conversationID);
            bundle.putBoolean("isGroup", false);
            bundle.putInt("recipientID", userId);
            MessagesFragment messageFragmentOk = new MessagesFragment();
            messageFragmentOk.setArguments(bundle);




            //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

            messagingIntent = new Intent(mContext, MessagesActivity.class);
            messagingIntent.putExtra("conversationID", conversationID);
            messagingIntent.putExtra("recipientID", userId);
            messagingIntent.putExtra("isGroup", false);
            /**
             * this for popup activity
             */
            messagingPopupIntent = new Intent(mContext, MessagesPopupActivity.class);
            messagingPopupIntent.putExtra("conversationID", conversationID);
            messagingPopupIntent.putExtra("recipientID", userId);
            messagingPopupIntent.putExtra("isGroup", false);

        }


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack
        stackBuilder.addParentStack(MessagesActivity.class);
        stackBuilder.addParentStack(MessagesActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(messagingIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        TaskStackBuilder stackPopupBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack

        stackPopupBuilder.addParentStack(MessagesPopupActivity.class);
        // Adds the Intent to the top of the stack
        stackPopupBuilder.addNextIntent(messagingPopupIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultMessagingPopupIntent = stackPopupBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mNotifyBuilder;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(AppHelper.getColor(mContext, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        if (counterUnreadConversation == 1) {
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, mContext.getString(R.string.reply_message), resultMessagingPopupIntent).build();
            mNotifyBuilder.addAction(action);
            try {

                String name = UtilsPhone.getContactName(mContext, msgs.get(0).getPhone());
                if (name != null) {
                    username = name;
                } else {
                    username = phone;
                }

            } catch (Exception e) {
                AppHelper.LogCat(" " + e.getMessage());
            }

            inboxStyle.setBigContentTitle(username);

            mNotifyBuilder.setContentTitle(username)

                    .setContentText(UtilsString.unescapeJava("پیام جدید"));
                    //.setContentText(UtilsString.unescapeJava(msgs.get(msgs.size() - 1).getMessage()));
            inboxStyle.setSummaryText(counterUnreadMessages + mContext.getString(R.string.new_messages_notify));
            for (MessagesModel m : msgs) {
                inboxStyle.addLine(UtilsString.unescapeJava(m.getMessage()));
            }

        } else {
            inboxStyle.setBigContentTitle(mContext.getResources().getString(R.string.app_name));

            mNotifyBuilder.setContentTitle(username)
                   // .setContentText(UtilsString.unescapeJava(msgs.get(msgs.size() - 1).getMessage()));
                    .setContentText(UtilsString.unescapeJava("پیام جدید"));
            inboxStyle.setSummaryText(counterUnreadMessages + mContext.getString(R.string.messages_from_notify) + counterUnreadConversation + mContext.getString(R.string.chats_notify));
            for (MessagesModel m : msgs) {

                if (m.getUsername() != null)
                    inboxStyle.addLine("".concat(m.getUsername()).concat(" : ").concat(UtilsString.unescapeJava(m.getMessage())));
                else
                    inboxStyle.addLine("".concat(m.getPhone()).concat(" : ").concat(UtilsString.unescapeJava(m.getMessage())));

            }
        }
        mNotifyBuilder.setStyle(inboxStyle);
        Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, Avatar, mContext, userId, AppConstants.USER, AppConstants.ROW_PROFILE);
        if (bitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, AppConstants.NOTIFICATIONS_IMAGE_SIZE, AppConstants.NOTIFICATIONS_IMAGE_SIZE, false);
            Bitmap circleBitmap = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2, scaledBitmap.getWidth() / 2, paint);
            mNotifyBuilder.setLargeIcon(circleBitmap);
        } else {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mNotifyBuilder.setLargeIcon(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_holder_ur_circle);
                    mNotifyBuilder.setLargeIcon(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_holder_ur_circle);
                    mNotifyBuilder.setLargeIcon(bitmap);
                }
            };
            Picasso.with(mContext)
                    .load(EndPoints.ROWS_IMAGE_URL + Avatar)
                    .transform(new CropSquareTransformation())
                    .resize(AppConstants.NOTIFICATIONS_IMAGE_SIZE, AppConstants.NOTIFICATIONS_IMAGE_SIZE)
                    .into(target);
        }
        if (PreferenceSettingsManager.conversation_tones(mContext)) {

            Uri uri = PreferenceSettingsManager.getDefault_message_notifications_settings_tone(mContext);
            if (uri != null)
                mNotifyBuilder.setSound(uri);
            else {
                int defaults = 0;
                defaults = defaults | Notification.DEFAULT_SOUND;
                mNotifyBuilder.setDefaults(defaults);
            }


        }

        if (PreferenceSettingsManager.getDefault_message_notifications_settings_vibrate(mContext)) {
            long[] vibrate = new long[]{2000, 2000, 2000, 2000, 2000};
            mNotifyBuilder.setVibrate(vibrate);
        } else {
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            mNotifyBuilder.setDefaults(defaults);
        }


        String colorLight = PreferenceSettingsManager.getDefault_message_notifications_settings_light(mContext);
        if (colorLight != null) {
            mNotifyBuilder.setLights(Color.parseColor(colorLight), 1500, 1500);
        } else {
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            mNotifyBuilder.setDefaults(defaults);
        }


        mNotifyBuilder.setAutoCancel(true);

        mNotificationManager.notify(userId, mNotifyBuilder.build());

    }

    private int getUnreadMessagesCounter() {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
        ConversationsModel conversationsModel1 = realm.where(ConversationsModel.class).findFirst();
        if (!realm.isClosed()) realm.close();
        return Integer.parseInt(conversationsModel1 != null ? conversationsModel1.getUnreadMessageCounter() : "0");
    }

    private int getUnreadConversationsCounter() {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
        List<ConversationsModel> conversationsModel1 = realm.where(ConversationsModel.class)
                .notEqualTo("UnreadMessageCounter", "0")
                .findAll();
        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
       //  if (!realm.isClosed()) realm.close();
        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

        return conversationsModel1.size() != 0 ? conversationsModel1.size() : 0;

    }

    private List<MessagesModel> getNotificationMessages(int userId) {
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
        List<MessagesModel> messagesModels = realm.where(MessagesModel.class)
                .equalTo("status", AppConstants.IS_WAITING)
                .equalTo("senderID", userId).findAll();
        if (!realm.isClosed()) realm.close();
        return messagesModels;
    }


    public void showGroupNotification(Context mContext, Intent resultIntent, Intent messagingGroupPopupIntent, String groupName, String message, int groupId, String Avatar) {
        memoryCache = new MemoryCache();

        String text = UtilsString.unescapeJava(message);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack
        stackBuilder.addParentStack(MessagesActivity.class);
        stackBuilder.addParentStack(MessagesActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackGroupPopupBuilder = TaskStackBuilder.create(mContext);
        stackGroupPopupBuilder.addParentStack(MessagesPopupActivity.class);
        // Adds the Intent to the top of the stack
        stackGroupPopupBuilder.addNextIntent(messagingGroupPopupIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultMessagingGroupPopupIntent = stackGroupPopupBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        final NotificationCompat.Builder mNotifyBuilder;


        //   ++numMessages;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, mContext.getString(R.string.reply_message), resultMessagingGroupPopupIntent).build();
        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(action)
                .setContentTitle(groupName)
                .setContentText(text)
                .setColor(AppHelper.getColor(mContext, R.color.colorAccent))
                //  .setNumber(numMessages)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);
        Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, Avatar, mContext, groupId, AppConstants.USER, AppConstants.ROW_PROFILE);
        if (bitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, AppConstants.NOTIFICATIONS_IMAGE_SIZE, AppConstants.NOTIFICATIONS_IMAGE_SIZE, false);
            Bitmap circleBitmap = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2, scaledBitmap.getWidth() / 2, paint);
            mNotifyBuilder.setLargeIcon(circleBitmap);
        } else {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mNotifyBuilder.setLargeIcon(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_holder_gr_circle);
                    mNotifyBuilder.setLargeIcon(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_holder_gr_circle);
                    mNotifyBuilder.setLargeIcon(bitmap);
                }
            };
            Picasso.with(mContext)
                    .load(EndPoints.ROWS_IMAGE_URL + Avatar)
                    .transform(new CropSquareTransformation())
                    .resize(AppConstants.NOTIFICATIONS_IMAGE_SIZE, AppConstants.NOTIFICATIONS_IMAGE_SIZE)
                    .into(target);
        }
        mNotifyBuilder.setAutoCancel(true);


        if (PreferenceSettingsManager.conversation_tones(mContext)) {

            Uri uri = PreferenceSettingsManager.getDefault_message_group_notifications_settings_tone(mContext);
            if (uri != null)
                mNotifyBuilder.setSound(uri);
            else {
                int defaults = 0;
                defaults = defaults | Notification.DEFAULT_SOUND;
                mNotifyBuilder.setDefaults(defaults);
            }


        }

        if (PreferenceSettingsManager.getDefault_message_group_notifications_settings_vibrate(mContext)) {
            long[] vibrate = new long[]{2000, 2000, 2000, 2000, 2000};
            mNotifyBuilder.setVibrate(vibrate);
        } else {
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            mNotifyBuilder.setDefaults(defaults);
        }


        String colorLight = PreferenceSettingsManager.getDefault_message_group_notifications_settings_light(mContext);
        if (colorLight != null) {
            mNotifyBuilder.setLights(Color.parseColor(colorLight), 1500, 1500);
        } else {
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            mNotifyBuilder.setDefaults(defaults);
        }


        mNotificationManager.notify(groupId, mNotifyBuilder.build());

    }

    /**
     * method to get manager for notification
     */
    public boolean getManager() {
        if (mNotificationManager != null) {
            return true;
        } else {
            return false;
        }

    }

    /***
     * method to cancel a specific notification
     *
     * @param index
     */
    public void cancelNotification(int index) {
        //    numMessages = 0;
        mNotificationManager.cancel(index);
    }

    /**
     * method to set badger counter for the app
     */
    public void SetupBadger(Context mContext) {

        int messageBadgeCounter = 0;
        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
        String DeviceName = android.os.Build.MANUFACTURER;
        String[] DevicesName = {
                "Sony",
                "Samsung",
                "LG",
                "HTC",
                "Xiaomi",
                "ASUS",
                "ADW",
                "NOVA",
                "Huawei",
                "ZUK",
                "APEX",
                "OPPO",
                "ZTE",
                "EverythingMe"
        };

        for (String device : DevicesName) {
            if (DeviceName.equals(device.toLowerCase())) {
                try {
                    List<MessagesModel> messagesModels = realm.where(MessagesModel.class)
                            .notEqualTo("id", 0)
                            .equalTo("status", AppConstants.IS_WAITING)
                            .notEqualTo("senderID", PreferenceManager.getID(mContext))
                            .findAll();

                    if (messagesModels.size() != 0) {
                        messageBadgeCounter = messagesModels.size();
                    }
                    try {
                        ShortcutBadger.applyCount(mContext.getApplicationContext(), messageBadgeCounter);
                    } catch (Exception e) {
                        AppHelper.LogCat(" ShortcutBadger Exception " + e.getMessage());
                    }
                } catch (Exception e) {
                    AppHelper.LogCat(" ShortcutBadger Exception " + e.getMessage());
                }
                break;
            }
        }
        if (!realm.isClosed())
            realm.close();

    }

}
