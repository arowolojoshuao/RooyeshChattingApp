package com.setayeshco.rooyesh.activities.images;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by Abderrahim El imame on 1/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class CameraPickerManager extends PickerManager {

    public CameraPickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mProcessingPhotoUri = getImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
}