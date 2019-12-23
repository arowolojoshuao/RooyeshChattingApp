package com.setayeshco.rooyesh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.profile.Selectstatuse;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abderrahim El imame on 11/2/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class CrashActivity extends AppCompatActivity {
    private static Boolean ENABLE_RESTART = false;
    @BindView(R.id.opsText)
    TextView opsText;
    @BindView(R.id.emoText)
    TextView emoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        ButterKnife.bind(this);
        setTypeFaces();
        final Animation animTranslate_progress_layout = AnimationUtils.loadAnimation(this, R.anim.crash_anim);
        animTranslate_progress_layout.setDuration(1200);
        emoText.startAnimation(animTranslate_progress_layout);
        final Animation animTranslate_text_layout = AnimationUtils.loadAnimation(this, R.anim.crash_anim);
        animTranslate_text_layout.setDuration(1400);
        opsText.startAnimation(animTranslate_text_layout);
        ENABLE_RESTART = true;
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(this::restartMain, SPLASH_TIME_OUT);
    }


    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            opsText.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        restartMain();
    }

    /**
     * method to restart Main Activity
     */
    public void restartMain() {
        if (ENABLE_RESTART) {
            Log.d("CONFRIMED","confrimed CrashActivity >>>>>> "+ PreferenceManager.isConfrimed(CrashActivity.this));
          /*  if (!PreferenceManager.isConfrimed(CrashActivity.this)) {
                Intent intent = new Intent(CrashActivity.this, ConfrimedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            }else {*/
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
          //  }
        } else {
            finish();
        }
        ENABLE_RESTART = false;
    }
}