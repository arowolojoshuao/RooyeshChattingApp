package com.setayeshco.rooyesh.activities.popups;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.presenters.users.StatusPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusDelete extends Activity {

    @BindView(R.id.deleteStatus)
    TextView deleteStatus;

    private StatusPresenter mStatusPresenter;
    private int statusID;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_status_delete);
        ButterKnife.bind(this);
        setTypeFaces();
        if (getIntent().hasExtra("statusID") && getIntent().getExtras().getInt("statusID") != 0) {
            statusID = getIntent().getExtras().getInt("statusID");
            status = getIntent().getExtras().getString("status");
        }
        mStatusPresenter = new StatusPresenter(this);
    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            deleteStatus.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.deleteStatus)
    public void DeleteStatus() {
        mStatusPresenter.DeleteStatus(statusID, status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatusPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStatusPresenter.onDestroy();
    }
}
