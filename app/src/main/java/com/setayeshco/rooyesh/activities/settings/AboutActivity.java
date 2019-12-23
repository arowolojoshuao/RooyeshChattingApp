package com.setayeshco.rooyesh.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abderrahim El imame on 8/19/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.about_enjoy_it)
    TextView aboutEnjoyIt;
    @BindView(R.id.about_app_name)
    TextView appName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setTypeFaces();
        String appVersion = AppHelper.getAppVersion(this);
        version.setText(getString(R.string.app_version) + " " + appVersion);

    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            version.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            aboutEnjoyIt.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            appName.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
    }

}
