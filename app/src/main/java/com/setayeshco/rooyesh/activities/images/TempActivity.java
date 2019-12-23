package com.setayeshco.rooyesh.activities.images;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * Created by Abderrahim El imame on 1/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class TempActivity extends AppCompatActivity {

    PickerManager pickerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TempActivity.this.pickerManager = GlobalHolder.getInstance().getPickerManager();
        TempActivity.this.pickerManager.setActivity((Activity) TempActivity.this);
        TempActivity.this.pickerManager.pickPhotoWithPermission();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // data= getIntent();
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case PickerManager.REQUEST_CODE_SELECT_IMAGE:
                Uri uri;
                if (data != null)
                    uri = data.getData();
                else
                    uri = pickerManager.getImageFile();

                pickerManager.setUri(uri);
                pickerManager.startCropActivity();
                break;
            case REQUEST_CROP:
                if (data != null) {
                    pickerManager.handleCropResult(data);
                } else
                    finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PickerManager.REQUEST_CODE_IMAGE_PERMISSION)
            pickerManager.handlePermissionResult(grantResults);
        else
            finish();

    }

}