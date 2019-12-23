package com.setayeshco.rooyesh.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.services.SMSVerificationService;


/**
 * Created by Abderrahim El imame on 23/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SmsReceiverBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String message = currentMessage.getDisplayMessageBody();

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);
                    AppHelper.LogCat("code received SmsReceiverBroadCast : " + verificationCode);

                    if (PreferenceManager.getToken(context) != null) return;
                    if (verificationCode != null && verificationCode.length() == 6) {
                        if (PreferenceManager.getID(context) != 0 || PreferenceManager.getToken(context) != null) {
                            Intent mIntent = new Intent(context, SMSVerificationService.class);
                            mIntent.putExtra("code", verificationCode);
                            mIntent.putExtra("register", false);
                            context.startService(mIntent);
                        } else {
                            Intent mIntent = new Intent(context, SMSVerificationService.class);
                            mIntent.putExtra("code", verificationCode);
                            mIntent.putExtra("register", true);
                            context.startService(mIntent);
                        }

                    }

                }
            }
        } catch (Exception e) {
            AppHelper.LogCat("Exception : SmsReceiverBroadCast " + e.getMessage());
        }
    }

    /**
     * Getting the Code from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message this is parameter for  getVerificationCodemethod
     * @return return value
     */
    private String getVerificationCode(String message) {
        String code;
        int index = message.indexOf(AppConstants.CODE_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return null;
    }
}