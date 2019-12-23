package com.setayeshco.rooyesh.activities.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.adapters.recyclerView.StatusAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.status.StatusModel;
import com.setayeshco.rooyesh.presenters.users.StatusPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusActivity extends AppCompatActivity implements NetworkListener, RewardedVideoAdListener {
	private RewardedVideoAd mAd;
	
	
	@BindView(R.id.currentStatus)
	EmojiconTextView currentStatus;
	@BindView(R.id.editCurrentStatusBtn)
	AppCompatTextView editCurrentStatusBtn;
	@BindView(R.id.StatusList)
	RecyclerView StatusList;
	@BindView(R.id.ParentLayoutStatus)
	LinearLayout ParentLayoutStatus;
	
	private List<StatusModel> mStatusModelList;
	private StatusAdapter mStatusAdapter;
	private StatusPresenter mStatusPresenter;
	private int statusID;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		ButterKnife.bind(this);
		
		if (PreferenceManager.ShowVideoAds(this)) {
			if (PreferenceManager.getUnitVideoAdsID(this) != null) {
				// Initialize the Mobile Ads SDK.
				MobileAds.initialize(this, PreferenceManager.getAppVideoAdsID(this));
				
				// Use an activity context to get the rewarded video instance.
				mAd = MobileAds.getRewardedVideoAdInstance(this);
				mAd.setRewardedVideoAdListener(this);
				loadRewardedVideoAd();
			}
		}
		setTypeFaces();
		mStatusPresenter = new StatusPresenter(this);
		mStatusPresenter.onCreate();
		initializerView();
		setupToolbar();
		
		
	}
	
	private void showRewardedVideo() {
		if (mAd != null)
			if (mAd.isLoaded()) {
				mAd.show();
			}
	}
	
	private void loadRewardedVideoAd() {
		if (mAd != null)
			if (!mAd.isLoaded()) {
				mAd.loadAd(PreferenceManager.getUnitVideoAdsID(this), new AdRequest.Builder().build());
			}
		
		
	}
	
	private void setTypeFaces() {
		if (AppConstants.ENABLE_FONTS_TYPES) {
			currentStatus.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
		}
	}
	
	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * method to initialize the view
	 */
	public void initializerView() {
		LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
		mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mStatusAdapter = new StatusAdapter(this, mStatusModelList, mStatusPresenter);
		StatusList.setLayoutManager(mLinearLayoutManager);
		StatusList.setAdapter(mStatusAdapter);
	}
	
	@SuppressWarnings("unused")
	@OnClick(R.id.editCurrentStatusBtn)
	void launchEditStatus(View v) {
		Intent mIntent = new Intent(this, EditStatusActivity.class);
		mIntent.putExtra("statusID", statusID);
		mIntent.putExtra("currentStatus", currentStatus.getText().toString().trim());
		startActivity(mIntent);
	}
	
	/**
	 * method to show status list
	 *
	 * @param statusModels this is parameter for  ShowStatus   method
	 */
	public void ShowStatus(List<StatusModel> statusModels) {
		mStatusModelList = statusModels;
		mStatusPresenter.getCurrentStatus();
	}
	
	/**
	 * method to update status list
	 *
	 * @param statusModels this is parameter for  updateStatusList   method
	 */
	public void updateStatusList(List<StatusModel> statusModels) {
		mStatusModelList = statusModels;
		mStatusAdapter.notifyDataSetChanged();
		mStatusPresenter.getCurrentStatus();
	}
	
	
	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(Pusher pusher) {
		mStatusPresenter.onEventPush(pusher);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.status_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				AnimationsUtil.setSlideOutAnimation(this);
				break;
			case R.id.deleteStatus:
				mStatusPresenter.DeleteAllStatus();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mAd != null)
			mAd.destroy(this);
		mStatusPresenter.onDestroy();
	}
	
	/**
	 * method to show the current status
	 *
	 * @param statusModel this is parameter for  ShowCurrentStatus   method
	 */
	public void ShowCurrentStatus(String statusModel) {
		String status = UtilsString.unescapeJava(statusModel);
		currentStatus.setText(status);
	}
	
	/**
	 * method to show the current status
	 *
	 * @param statusModel this is parameter for  ShowCurrentStatus   method
	 */
	public void ShowCurrentStatus(StatusModel statusModel) {
		if (statusModel.getStatus() != null) {
			statusID = statusModel.getCurrentStatusID();
			String status = UtilsString.unescapeJava(statusModel.getStatus());
			currentStatus.setText(status);
		} else {
			AppHelper.Snackbar(this, ParentLayoutStatus, getString(R.string.failed_to_the_current_status), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
		}
	}
	
	public void onErrorLoading(Throwable throwable) {
		AppHelper.LogCat("status error" + throwable.getMessage());
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AnimationsUtil.setSlideOutAnimation(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mAd != null)
			mAd.pause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mAd != null)
			mAd.resume(this);
		mStatusPresenter.onResume();
		RooyeshApplication.getInstance().setConnectivityListener(this);
	}
	
	/**
	 * Callback will be triggered when there is change in
	 * network connection
	 */
	@Override
	public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
		if (!isConnecting && !isConnected) {
			AppHelper.Snackbar(this, ParentLayoutStatus, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
		} else if (isConnecting && isConnected) {
			AppHelper.Snackbar(this, ParentLayoutStatus, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
		} else {
			AppHelper.Snackbar(this, ParentLayoutStatus, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
			
		}
	}
	
	public void deleteStatus(int statusID) {
		mStatusAdapter.DeleteStatusItem(statusID);
	}
	
	@Override
	public void onRewardedVideoAdLoaded() {
		showRewardedVideo();
	}
	
	@Override
	public void onRewardedVideoAdOpened() {
	
	}
	
	@Override
	public void onRewardedVideoStarted() {
	
	}
	
	@Override
	public void onRewardedVideoAdClosed() {
		loadRewardedVideoAd();// Preload the next video ad.
	}
	
	@Override
	public void onRewarded(RewardItem rewardItem) {
	}
	
	@Override
	public void onRewardedVideoAdLeftApplication() {
	}
	
	@Override
	public void onRewardedVideoAdFailedToLoad(int i) {
	}
	
	@Override
	public void onRewardedVideoCompleted() {
	}
}
