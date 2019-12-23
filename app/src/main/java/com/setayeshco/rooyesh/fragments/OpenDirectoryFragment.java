package com.setayeshco.rooyesh.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by Asrdigital on 11/11/2017.
 */

public class OpenDirectoryFragment  extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (!MainActivity.video_isCaptured) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.error_capture)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // open directory


                        }
                    });

            return builder.create();
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.open_screen_capture_directory)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // open directory

                        if (MainActivity.DIRECTORY_CAPTURED_VIDEO != null) {
                            File file = new File(MainActivity.DIRECTORY_CAPTURED_VIDEO, "/video.mp4");
                            Uri path = Uri.fromFile(file);
                            Intent mp4OpenIntent = new Intent(Intent.ACTION_VIEW);
                            mp4OpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mp4OpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mp4OpenIntent.setDataAndType(path, "application/mp4");
                            try {
                                if(Build.VERSION.SDK_INT>=24){
                                    try{
                                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                                        m.invoke(null);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                startActivity(mp4OpenIntent);
                            } catch (ActivityNotFoundException e) {

                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do not open
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
