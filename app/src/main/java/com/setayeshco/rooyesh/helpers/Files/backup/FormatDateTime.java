package com.setayeshco.rooyesh.helpers.Files.backup;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Abderrahim El imame on 6/17/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class FormatDateTime {
    private Context context;

    public FormatDateTime(Context mContext) {
        this.context = mContext;
    }

    public String formatDate(Date date) {
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        String finalData = finalDataFormat.format(date);
        String finalTime = finalTimeFormat.format(date);
        return finalData + " " + finalTime;
    }
}
