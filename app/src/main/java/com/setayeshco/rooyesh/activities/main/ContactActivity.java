package com.setayeshco.rooyesh.activities.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.iid.FirebaseInstanceId;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.activities.settings.SettingsActivity;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.ContactsFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.ForegroundRuning;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.RegisterIdModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.PusherContacts;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.socket.client.Socket;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_USER_JOINED;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_START_REFRESH;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_STOP_REFRESH;

public class ContactActivity extends AppCompatActivity implements NetworkListener {
	
	private static final int REQUEST_PERMISSIONS = 10;
	private static final int REQUEST_CODE = 1000;
	boolean actionModeStarted = false;
	private Socket mSocket;
	InterstitialAd mInterstitialAd;
	
	@BindView(R.id.main_activity)
	View mView;
	@BindView(R.id.adParentLyout)
	LinearLayout adParentLyout;
	@BindView(R.id.main_view)
	LinearLayout MainView;
	
	TextView counterTabMessage;
	
	private ContactsModel mContactsModel;
	
	@SuppressLint("RestrictedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		//  LayoutInflater mInflat = (LayoutInflater) ContactActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		counterTabMessage = findViewById(R.id.counterTabMessages);
		
		ButterKnife.bind(this);
		Permissions();
		EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
		EventBus.getDefault().register(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.contact_container, new ContactsFragment());
		fragmentTransaction.commit();
		EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
		
		EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
		
		
		Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar_contact);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("رویش");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
		
		
		RateHelper.appLaunched(this);
		connectToServer();
		PreferenceManager.setIsNeedInfo(this, false);
		
		new Handler().postDelayed(() -> {
			checkIfUserSession();
			registerFCM();
			if (PreferenceManager.ShowInterstitialrAds(ContactActivity.this)) {
				if (PreferenceManager.getUnitInterstitialAdID(ContactActivity.this) != null) {
					initializerAds();
				}
			}
			
			showMainAds();
			loadCounter();
		}, 1000);
		
		
		//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''


//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			startActivity(new Intent(ContactActivity.this, MainActivity.class));
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				AppHelper.hidePermissionsDialog();
				EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CONTACTS_PERMISSION));
			}
		}
		
		if (requestCode == REQUEST_PERMISSIONS) {
			
			if ((grantResults.length > 0) && (grantResults[0] +
					grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
				
			} else {
				
				Snackbar.make(findViewById(android.R.id.content), "ط´ظ…ط§ ط§ط¬ط§ط²ظ‡ ط¯ط³طھط±ط³غŒ ظ†ط¯ط§ط¯ظ‡ ط§غŒط¯",
						Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:" + getPackageName()));
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								startActivity(intent);
							}
						}).show();
			}
			
		}
		if (requestCode == 1) {
			if (grantResults.length == 1 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
		
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
			AppHelper.hidePermissionsDialog();
			EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CONTACTS_PERMISSION));
		}
		
		if (requestCode != REQUEST_CODE) {
			Log.e("main activity", "Unknown request code: " + requestCode);
			return;
		}
		
		
		if (resultCode != RESULT_OK) {
			Toast.makeText(this,
					"Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		
	}
	
	private void Permissions() {
		if (PermissionHandler.checkPermission(this, Manifest.permission.READ_CONTACTS)) {
			AppHelper.LogCat("Read contact data permission already granted.");
		} else {
			AppHelper.LogCat("Please request Read contact data permission.");
			AppHelper.showPermissionDialog(this);
			PermissionHandler.requestPermission(this, Manifest.permission.READ_CONTACTS);
		}
		if (PermissionHandler.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
			AppHelper.LogCat("Read storage data permission already granted.");
		} else {
			AppHelper.LogCat("Please request Read storage data permission.");
			PermissionHandler.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(Pusher pusher) {
		switch (pusher.getAction()) {
			case EVENT_BUS_START_REFRESH:
				//  toolbarProgressBar.setVisibility(View.VISIBLE);
				//   toolbarProgressBar.getIndeterminateDrawable().setColorFilter(AppHelper.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
				break;
			case EVENT_BUS_STOP_REFRESH:
				//     toolbarProgressBar.setVisibility(View.GONE);
				break;
			case EVENT_BUS_MESSAGE_COUNTER:
				//        new Handler().postDelayed(this::loadCounter, 500);
				break;
			case EVENT_BUS_NEW_USER_JOINED:
				JSONObject jsonObject = pusher.getJsonObject();
				try {
					String phone = jsonObject.getString("phone");
					int senderId = jsonObject.getInt("senderId");
					new Handler().postDelayed(() -> {
						Intent mIntent = new Intent("new_user_joined_notification_whatsclone");
						mIntent.putExtra("conversationID", 0);
						mIntent.putExtra("recipientID", senderId);
						mIntent.putExtra("phone", phone);
						mIntent.putExtra("message", AppConstants.JOINED_MESSAGE_SMS);
						sendBroadcast(mIntent);
					}, 2500);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AppConstants.EVENT_BUS_NEW_CONTACT_ADDED:
				break;
			case AppConstants.EVENT_BUS_START_CONVERSATION:
				
				break;
			case AppConstants.EVENT_BUS_ACTION_MODE_STARTED:
				actionModeStarted();
				break;
			case AppConstants.EVENT_BUS_ACTION_MODE_DESTROYED:
				actionModeDestroyed();
				break;
			case AppConstants.EVENT_BUS_REFRESH_TOKEN_FCM:
				sendRegistrationToServer(pusher.getData());
				break;
			
		}
		
		
	}
	
	
	private void sendRegistrationToServer(final String token) {
		if (token != null) {
			updateRegisteredId(token);
		}
	}
	
	
	private void actionModeDestroyed() {
		if (actionModeStarted) {
			actionModeStarted = false;
			//    tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorPrimary));
			if (AppHelper.isAndroid5()) {
				Window window = getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(AppHelper.getColor(this, R.color.colorPrimaryDark));
			}
		}
	}
	
	private void actionModeStarted() {
		if (!actionModeStarted) {
			actionModeStarted = true;
			//  tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorActionMode));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(AppHelper.getColor(this, R.color.colorActionMode));
			}
		}
	}
	
	
	public void updateRegisteredId(String registeredId) {
		RegisterIdModel registerIdModel = new RegisterIdModel();
		registerIdModel.setRegisteredId(registeredId);
		APIHelper.initialApiUsersContacts().updateRegisteredId(registerIdModel).subscribe(qResponse -> {
			if (qResponse.isSuccess()) {
				
				JSONObject json = new JSONObject();
				try {
					json.put("userId", PreferenceManager.getID(this));
					json.put("register_id", qResponse.getRegistered_id());
					
					mSocket.emit(AppConstants.SOCKET_UPDATE_REGISTER_ID, json);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			AppHelper.LogCat(qResponse.getMessage());
		}, throwable -> {
			AppHelper.LogCat(throwable.getMessage());
		});
	}
	
	
	private void connectToServer() {
		
		RooyeshApplication app = (RooyeshApplication) getApplication();
		mSocket = app.getSocket();
		if (mSocket != null) {
			if (!mSocket.connected())
				mSocket.connect();
			
			JSONObject json = new JSONObject();
			try {
				json.put("connected", true);
				json.put("senderId", PreferenceManager.getID(this));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json);
		}
	}
	
	
	@Override
	public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
		if (!isConnecting && !isConnected) {
			AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
		} else if (isConnecting && isConnected) {
			AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
		} else {
			AppHelper.Snackbar(this, mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
		}
	}
	
	
	public void checkIfUserSession() {
		APIHelper.initialApiUsersContacts().checkIfUserSession().subscribe(networkModel -> {
			if (!networkModel.isConnected()) {
				if (ForegroundRuning.get().isForeground()) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ContactActivity.this);
					alert.setMessage(R.string.your_session_expired);
					alert.setPositiveButton(R.string.ok, (dialog, which) -> {
						PreferenceManager.setToken(ContactActivity.this, null);
						PreferenceManager.setID(ContactActivity.this, 0);
						PreferenceManager.setSocketID(ContactActivity.this, null);
						PreferenceManager.setPhone(ContactActivity.this, null);
						PreferenceManager.setIsWaitingForSms(ContactActivity.this, false);
						PreferenceManager.setMobileNumber(ContactActivity.this, null);
						Intent intent = new Intent(ContactActivity.this, WelcomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					});
					alert.setCancelable(false);
					alert.show();
				}
			}
		}, throwable -> {
			AppHelper.LogCat("checkIfUserSession MainActivity " + throwable.getMessage());
		});
	}
	
	private void registerFCM() {
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		sendRegistrationToServer(refreshedToken);
	}
	
	
	private void showMainAds() {
		if (PreferenceManager.ShowBannerAds(this)) {
			adParentLyout.setVisibility(View.VISIBLE);
			if (PreferenceManager.getUnitBannerAdsID(this) != null) {
				AdView mAdView = new AdView(this);
				mAdView.setAdSize(AdSize.BANNER);
				mAdView.setAdUnitId(PreferenceManager.getUnitBannerAdsID(this));
				AdRequest adRequest = new AdRequest.Builder()
						.build();
				if (mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
					mAdView.loadAd(adRequest);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				adParentLyout.addView(mAdView, params);
			}
		} else {
			adParentLyout.setVisibility(View.GONE);
		}
	}
	
	
	private void loadCounter() {
		int messageCounter = 0;
		try {
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			List<ConversationsModel> conversationsModel1 = realm.where(ConversationsModel.class)
					.notEqualTo("UnreadMessageCounter", "0")
					.findAll();
			if (conversationsModel1.size() != 0) {
				messageCounter = conversationsModel1.size();
			}
			
			if (messageCounter == 0) {
				counterTabMessage.setVisibility(View.GONE);
			} else {
				counterTabMessage.setVisibility(View.VISIBLE);
				counterTabMessage.setText(String.valueOf(messageCounter));
			}
			if (!realm.isClosed())
				realm.close();
			
		} catch (Exception e) {
			AppHelper.LogCat("loadCounter main activity " + e.getMessage());
		}
		NotificationsManager notificationsManager = new NotificationsManager();
		notificationsManager.SetupBadger(this);
		
	}
	
	private void initializerAds() {
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(PreferenceManager.getUnitInterstitialAdID(this));
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
				AppHelper.LaunchActivity(ContactActivity.this, SettingsActivity.class);
			}
		});
		
		requestNewInterstitial();
	}
	
	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MainView.setVisibility(View.GONE);
		RooyeshApplication.getInstance().setConnectivityListener(this);
	}
	
	
}
