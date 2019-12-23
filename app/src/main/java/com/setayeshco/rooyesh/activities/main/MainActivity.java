package com.setayeshco.rooyesh.activities.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.iid.FirebaseInstanceId;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.MakeSmartActivity;
import com.setayeshco.rooyesh.activities.NewConversationContactsActivity;
import com.setayeshco.rooyesh.activities.groups.AddMembersToGroupActivity;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.activities.profile.EditProfileActivity;
import com.setayeshco.rooyesh.activities.profile.Selectstatuse;
import com.setayeshco.rooyesh.activities.search.SearchContactsActivity;
import com.setayeshco.rooyesh.activities.search.SearchConversationsActivity;
import com.setayeshco.rooyesh.activities.settings.SettingsActivity;
import com.setayeshco.rooyesh.adapters.others.HomeTabsAdapter;
import com.setayeshco.rooyesh.api.APIAuthentication;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.AuthService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.OpenDirectoryFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.ForegroundRuning;
import com.setayeshco.rooyesh.helpers.OutDateHelper;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.interfaces.LoadingData;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.RegisterIdModel;
import com.setayeshco.rooyesh.models.calls.CallsInfoModel;
import com.setayeshco.rooyesh.models.calls.CallsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel2;
import com.setayeshco.rooyesh.models.messages.ListConversation;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.PusherContacts;
import com.setayeshco.rooyesh.presenters.users.MainActivityPresenter;
import com.setayeshco.rooyesh.services.MainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_USER_JOINED;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_START_REFRESH;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_STOP_REFRESH;

/**
 * Created by Abderrahim El imame on 01/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class MainActivity extends AppCompatActivity implements LoadingData, NetworkListener {
	
	//'''''''''''''''''
	private ContactsModel mContactsModel;
	TextView txtUser;
	TextView txtEmail;
	public static CircleImageView imgProfile;
	private MemoryCache memoryCache;
	private MainActivityPresenter mEditProfilePresenter;
	
	//'''''''''''''''''
	
	
	private int time_interval = 2000;
	private long oldCurrentTimeMillis;
	DrawerLayout drawer;
	ActionBarDrawerToggle toggle;
	
	
	@BindView(R.id.viewpager)
	ViewPager viewPager;
	@BindView(R.id.main_activity)
	View mView;
	@BindView(R.id.adParentLyout)
	LinearLayout adParentLyout;
	/*  @BindView(R.id.tabs)
	  TabLayout tabLayout;*/
	@BindView(R.id.app_bar)
	Toolbar toolbar;
	@BindView(R.id.main_view)
	LinearLayout MainView;
	@BindView(R.id.toolbar_progress_bar)
	ProgressBar toolbarProgressBar;
	
	
	@BindView(R.id.record_screen)
	Button btn_recordScreen;
	
	InterstitialAd mInterstitialAd;
	boolean actionModeStarted = false;
	
	
	HomeTabsAdapter mFragmentStatePagerAdapter;
	
	private Socket mSocket;
	
	@BindView(R.id.floatingBtnMain)
	FloatingActionButton floatingBtnMain;
	
	
	public static boolean video_isCaptured = true;
	
	//aded by me
	// boolean isRecording = false;
	//  @BindView(R.id.floatingBtnMain)
	// private static final int REQUEST_CODE_CAPTURE_PERM = 1234;
	
	
	private MediaProjectionManager mMediaProjectionManager;
	private static final int REQUEST_CODE = 1000;
	private int mScreenDensity;
	private MediaProjectionManager mProjectionManager;
	
	private MediaProjection mMediaProjection;
	private VirtualDisplay mVirtualDisplay;
	private MediaProjectionCallback mMediaProjectionCallback;
	private ToggleButton mToggleButton;
	private MediaRecorder mMediaRecorder;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	private static final int REQUEST_PERMISSIONS = 10;
	public static String DIRECTORY_CAPTURED_VIDEO;
	private static final int DISPLAY_WIDTH = 920;
	private static final int DISPLAY_HEIGHT = 1680;
	
	public static Activity Act;
	
	Chronometer recordChronometer;
	
	
	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}
	
	
	static {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}
	
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ButterKnife.bind(this);
		getversion3();
		Act = this;
		//  getversion2();
		Permissions();
		initializerView();
		setupToolbar();
		drawer = (DrawerLayout) findViewById(R.id.main_activity);
		EventBus.getDefault().register(this);
		
		//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
		
		if (!AppHelper.isServiceRunning(this, MainService.class)
				&& PreferenceManager.getToken(this) != null
				) {
			Log.d("SERVICCC", "SERVICEEEE .>>>> 23232323");
			this.startService(new Intent(this, MainService.class));
		}
		
		toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, 0, 0);
		toggle.setDrawerIndicatorEnabled(false);
		drawer.addDrawerListener(toggle);
		toggle.setDrawerIndicatorEnabled(true);
		drawer.setDrawerListener(toggle);
		toggle.syncState();
		
		
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		View view = navigationView.getHeaderView(0);
		imgProfile = (CircleImageView) view.findViewById(R.id.imgProfile);
		txtUser = (TextView) view.findViewById(R.id.txtUser);
		txtEmail = (TextView) view.findViewById(R.id.txtEmail);
		txtUser.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
		txtEmail.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
		
		Menu m = navigationView.getMenu();
		
		Log.d("ISTEACHER", "ISTEACHERSMSMain55  >>>  " + PreferenceManager.isTeacher(MainActivity.this));
		//      Log.d("ISTEACHER","mContactsModel  >>>  "+mContactsModel.getIs_prof());
		
		for (int i = 0; i < m.size(); i++) {
			
			if (PreferenceManager.isTeacher(MainActivity.this) == false) {
				if (m.getItem(i).getTitle().equals("دانشجو ها")) {
					m.getItem(i).setVisible(false);
				} else if (m.getItem(i).getTitle().equals("ایجاد گروه")) {
					m.getItem(i).setVisible(false);
				} else if (m.getItem(i).getTitle().equals("کنترل اشیا")) {
					m.getItem(i).setVisible(false);
				}
			}
			
			
			MenuItem mi = m.getItem(i);
			
			SpannableString s = new SpannableString(mi.getTitle());
			Typeface font = Typeface.createFromAsset(getAssets(), "fonts/IranSans.ttf");
			s.setSpan(new CustomTypefaceSpan("", font), 0, s.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mi.setTitle(s);
			
		}
		
		
		memoryCache = new MemoryCache();
		mEditProfilePresenter = new MainActivityPresenter(this);
		mEditProfilePresenter.onCreate();
		
		Configuration configuration = getResources().getConfiguration();
		if (configuration.smallestScreenWidthDp >= 600) {
			ImageView toolbar_image = findViewById(R.id.toolbar_image);
			
			toolbar_image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// Toast.makeText(MainActivity.this,"Navigation",Toast.LENGTH_LONG).show();
					drawer.openDrawer(Gravity.START);
				}
			});
			
			
		}
		
		
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.nav_profile:
						Intent mIntent = new Intent(MainActivity.this, EditProfileActivity.class);
						startActivity(mIntent);
						break;
                 /*   case R.id.nav_new_group:
                        if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.access_denied_create_group), Toast.LENGTH_SHORT).show();
                        }
                        break;*/
					case R.id.nav_students:
						Log.d("ISTEACHER", "ISTEACHERSMSMain1  >>>  " + PreferenceManager.isTeacher(MainActivity.this));
						
						if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
							Toast.makeText(MainActivity.this, getResources().getString(R.string.access_denied_create_std), Toast.LENGTH_SHORT).show();
							break;
						} else {
							Intent myIntent = new Intent(MainActivity.this, ContactActivity.class);
							startActivity(myIntent);
						}
                      /*  FragmentManager fragmentManager = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.frame_layout_conversation,new ContactsFragment());
                        fragmentTransaction.commit();*/
						//  EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
                        /*RateHelper.significantEvent(getApplicationContext());
                        AppHelper.LaunchActivity(MainActivity.this, StatusActivity.class);*/
						
						break;
					case R.id.nav_make_group:
                       /* RateHelper.significantEvent(getApplicationContext());
                        removeCallsLog();*/
						Log.d("ISTEACHER", "ISTEACHERSMSMain22  >>>  " + PreferenceManager.isTeacher(MainActivity.this));
						
						if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
							Toast.makeText(MainActivity.this, getResources().getString(R.string.access_denied_create_group), Toast.LENGTH_SHORT).show();
							break;
						}
						
						
						RateHelper.significantEvent(MainActivity.this);
						PreferenceManager.clearMembers(MainActivity.this);
						AppHelper.LaunchActivity(MainActivity.this, AddMembersToGroupActivity.class);
						
						break;
                   /* case R.id.nav_refresh_contacts:
                        break;*/
					case R.id.nav_setting:
						RateHelper.significantEvent(getApplicationContext());
						if (PreferenceManager.ShowInterstitialrAds(getApplicationContext())) {
							if (mInterstitialAd.isLoaded()) {
								mInterstitialAd.show();
							} else {
								AppHelper.LaunchActivity(MainActivity.this, SettingsActivity.class);
							}
						} else {
							AppHelper.LaunchActivity(MainActivity.this, SettingsActivity.class);
						}
						
						break;
					case R.id.nav_make_smart:
						if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
							Toast.makeText(MainActivity.this, getResources().getString(R.string.access_denied_create_control), Toast.LENGTH_SHORT).show();
							break;
						}
						MainActivity.this.startActivity(new Intent(MainActivity.this, MakeSmartActivity.class));
						break;
					
					case R.id.nav_exit:
						// Toast.makeText(MainActivity.this, "درحال حاضر این گزینه فعال نمی باشد", Toast.LENGTH_SHORT).show();
						
						ShowdialogError();
						
						
						break;
					
					case R.id.nav_alaki1:
						Toast.makeText(MainActivity.this, "درحال حاضر این گزینه فعال نمی باشد", Toast.LENGTH_SHORT).show();
						break;
					
				}
				return false;
			}
		});
		
		//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
		
		
		///////////////////////////////////////////////////////////////////////////////////
		
		RateHelper.appLaunched(this);
		connectToServer();
		PreferenceManager.setIsNeedInfo(this, false);
		
		new Handler().postDelayed(() -> {
			checkIfUserSession();
			registerFCM();
			if (PreferenceManager.ShowInterstitialrAds(MainActivity.this)) {
				if (PreferenceManager.getUnitInterstitialAdID(MainActivity.this) != null) {
					initializerAds();
				}
			}
			
			showMainAds();
			loadCounter();
		}, 1000);


//adede by me
		
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenDensity = metrics.densityDpi;
		
		mMediaRecorder = new MediaRecorder();
		if (android.os.Build.VERSION.SDK_INT > 20) {
			mProjectionManager = (MediaProjectionManager) getSystemService
					(Context.MEDIA_PROJECTION_SERVICE);
		}
		
		
		recordChronometer = (Chronometer) findViewById(R.id.record_chronometer); // initiate a chronometer
		
		
		mToggleButton = (ToggleButton) findViewById(R.id.record_screen);
		
		
		mToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(MainActivity.this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
						.checkSelfPermission(MainActivity.this,
								Manifest.permission.RECORD_AUDIO)
						!= PackageManager.PERMISSION_GRANTED) {
					if (ActivityCompat.shouldShowRequestPermissionRationale
							(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
							ActivityCompat.shouldShowRequestPermissionRationale
									(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
						mToggleButton.setChecked(false);
						Snackbar.make(findViewById(android.R.id.content), "ط§ط¬ط§ط²ظ‡ ط¶ط¨ط· طµظپط­ظ‡ ظ†ظ…ط§غŒط´ ",
								Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ActivityCompat.requestPermissions(MainActivity.this,
												new String[]{Manifest.permission
														.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
												REQUEST_PERMISSIONS);
									}
								}).show();
					} else {
						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission
										.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
								REQUEST_PERMISSIONS);
					}
				} else {
					onToggleScreenShare(v);
					// Toast.makeText(MainActivity.this, "ط¹ط¯ظ… ط¯ط³طھط±ط³غŒ ط¨ظ‡ طµظپط­ظ‡ ظ†ظ…ط§غŒط´", Toast.LENGTH_SHORT);
				}
			}
		});
		
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void onToggleScreenShare(View view) {
		if (((ToggleButton) view).isChecked()) {
			initRecorder();
			shareScreen();
		} else {
			
			try {
				mMediaRecorder.stop();
			} catch (RuntimeException stopException) {
				video_isCaptured = false;
				stopException.printStackTrace();
				
			}
			stopChronoMeter();
			
			mMediaRecorder.reset();
			Log.v("mainActivity", "Stopping Recording");
			stopScreenSharing();
		}
	}
	
	private void takeScreenshot() {
		Date now = new Date();
		android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
		
		try {
			// image naming and path  to include sd card  appending name you choose for file
			String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
			
			// create bitmap screen capture
			View v1 = getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);
			
			File imageFile = new File(mPath);
			
			FileOutputStream outputStream = new FileOutputStream(imageFile);
			int quality = 100;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			outputStream.flush();
			outputStream.close();
			
			//openScreenshot(imageFile);
		} catch (Throwable e) {
			// Several error may come out with file handling or DOM
			e.printStackTrace();
		}
	}
	
	public void startChronoMeter() {
		
		recordChronometer.setBase(SystemClock.elapsedRealtime());
		recordChronometer.setVisibility(View.VISIBLE);
		recordChronometer.start();
		
		
	}
	
	public void stopChronoMeter() {
		
		
		recordChronometer.stop();
		recordChronometer.setVisibility(View.INVISIBLE);
		
		
		new OpenDirectoryFragment().show(getSupportFragmentManager(), "");
		
	}
	
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void shareScreen() {
		if (mMediaProjection == null) {
			startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
			return;
		}
		mVirtualDisplay = createVirtualDisplay();
		mMediaRecorder.start();
		
		startChronoMeter();
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private VirtualDisplay createVirtualDisplay() {
		return mMediaProjection.createVirtualDisplay("MainActivity",
				DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
				DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
				mMediaRecorder.getSurface(), null /*Callbacks*/, null
				/*Handler*/);
	}
	
	private void initRecorder() {
		try {
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			DIRECTORY_CAPTURED_VIDEO = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
			mMediaRecorder.setOutputFile(Environment
					.getExternalStoragePublicDirectory(Environment
							.DIRECTORY_DOWNLOADS) + "/video.mp4");
			mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
			mMediaRecorder.setVideoFrameRate(30);
			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			int orientation = ORIENTATIONS.get(rotation + 90);
			mMediaRecorder.setOrientationHint(orientation);
			mMediaRecorder.prepare();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private class MediaProjectionCallback extends MediaProjection.Callback {
		@Override
		public void onStop() {
			if (mToggleButton.isChecked()) {
				mToggleButton.setChecked(false);
				try {
					mMediaRecorder.stop();
				} catch (RuntimeException stopException) {
					video_isCaptured = false;
					stopException.printStackTrace();
				}
				stopChronoMeter();
				mMediaRecorder.reset();
				Log.v("mainactivity", "Recording Stopped");
			}
			mMediaProjection = null;
			stopScreenSharing();
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void stopScreenSharing() {
		if (mVirtualDisplay == null) {
			return;
		}
		mVirtualDisplay.release();
		//mMediaRecorder.release(); //If used: mMediaRecorder object cannot
		// be reused again
		destroyMediaProjection();
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void destroyMediaProjection() {
		if (mMediaProjection != null) {
			mMediaProjection.unregisterCallback(mMediaProjectionCallback);
			mMediaProjection.stop();
			mMediaProjection = null;
		}
		Log.i("MainActivity", "MediaProjection Stopped");
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
	
	
	private void initializerAds() {
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(PreferenceManager.getUnitInterstitialAdID(this));
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
				AppHelper.LaunchActivity(MainActivity.this, SettingsActivity.class);
			}
		});
		
		requestNewInterstitial();
	}
	
	
	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.search_conversations:
				RateHelper.significantEvent(this);
				if (AppHelper.isAndroid5()) {
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(toolbar, "searchBar"));
					Intent mIntent = new Intent(this, SearchConversationsActivity.class);
					startActivity(mIntent, options.toBundle());
				} else {
					AppHelper.LaunchActivity(this, SearchConversationsActivity.class);
				}
				break;
			case R.id.search_contacts:
				if (AppHelper.isAndroid5()) {
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(toolbar, "searchBar"));
					Intent mIntent = new Intent(this, SearchContactsActivity.class);
					startActivity(mIntent, options.toBundle());
				} else {
					AppHelper.LaunchActivity(this, SearchContactsActivity.class);
				}
				break;
         /*   case R.id.search_calls:
                if (AppHelper.isAndroid5()) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(toolbar, "searchBar"));
                    Intent mIntent = new Intent(this, SearchCallsActivity.class);
                    startActivity(mIntent, options.toBundle());
                } else {
                    AppHelper.LaunchActivity(this, SearchCallsActivity.class);
                }

                break;*/
         /*   case R.id.new_group:

                // if user is student
                if (PreferenceManager.isTeacher(getApplicationContext()) == false) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.access_denied_create_group), Toast.LENGTH_SHORT).show();
                    break;
                }


                RateHelper.significantEvent(this);
                PreferenceManager.clearMembers(this);
                AppHelper.LaunchActivity(this, AddMembersToGroupActivity.class);
                break;*/
     /*       case R.id.settings:
                RateHelper.significantEvent(this);
                if (PreferenceManager.ShowInterstitialrAds(this)) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        AppHelper.LaunchActivity(this, SettingsActivity.class);
                    }
                } else {
                    AppHelper.LaunchActivity(this, SettingsActivity.class);
                }
                break;*/
         /*   case R.id.status:
                RateHelper.significantEvent(this);
                AppHelper.LaunchActivity(this, StatusActivity.class);
                break;*/

      /*      case R.id.clear_log_call:
                RateHelper.significantEvent(this);
                removeCallsLog();
                break;*/
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void removeCallsLog() {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		AppHelper.showDialog(this, getString(R.string.delete_call_dialog));
		realm.executeTransactionAsync(realm1 -> {
			RealmResults<CallsModel> callsModels = realm1.where(CallsModel.class).findAll();
			for (CallsModel callsModel : callsModels) {
				RealmResults<CallsInfoModel> callsInfoModel = realm1.where(CallsInfoModel.class).equalTo("callId", callsModel.getId()).findAll();
				callsInfoModel.deleteAllFromRealm();
			}
			callsModels.deleteAllFromRealm();
		}, () -> {
			AppHelper.hideDialog();
			EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_DELETE_CALL_ITEM, 0));
		}, error -> {
			AppHelper.LogCat(error.getMessage());
			AppHelper.hideDialog();
		});
	}
	
	@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
       /* switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                getMenuInflater().inflate(R.menu.calls_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.conversations_menu, menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.contacts_menu, menu);
                break;
        }*/
		getMenuInflater().inflate(R.menu.conversations_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * method to setup toolbar
	 */
	@SuppressLint("RestrictedApi")
	private void setupToolbar() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(R.string.app_name);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}
	
	
	/**
	 * method to initialize the view
	 */
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void initializerView() {
		
		if (PreferenceManager.isOutDate(this)) {
			OutDateHelper.appLaunched(this);
			OutDateHelper.significantEvent(this);
		}
		mFragmentStatePagerAdapter = new HomeTabsAdapter(getSupportFragmentManager());
		viewPager.setAdapter(mFragmentStatePagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		//   tabLayout.setupWithViewPager(viewPager);
		//   tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		//    tabLayout.setTabMode(TabLayout.MODE_FIXED);
		viewPager.setCurrentItem(1);
		//    tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_calls);
		//   tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_messages);
		//  tabLayout.getTabAt(2).setCustomView(R.layout.custom_tab_contacts);
//        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(this, R.color.colorUnSelected));
		//       ((TextView) findViewById(R.id.title_tabs_calls)).setTextColor(AppHelper.getColor(this, R.color.colorUnSelected));
		//       ((TextView) findViewById(R.id.title_tabs_messages)).setTextSize(24);
      /*  tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable icon = AppHelper.getVectorDrawable(MainActivity.this, R.drawable.ic_chat_white_24dp);
                switch (tab.getPosition()) {
                    case 0:
                        icon = AppHelper.getVectorDrawable(MainActivity.this, R.drawable.ic_call_white_24dp);
                        viewPager.setCurrentItem(0);
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        findViewById(R.id.counterTabCalls).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter));
                        ((TextView) findViewById(R.id.title_tabs_calls)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        ((TextView) findViewById(R.id.title_tabs_calls)).setTextSize(24);
                        break;
                    case 1:
                        icon = AppHelper.getVectorDrawable(MainActivity.this, R.drawable.ic_chat_white_24dp);
                        viewPager.setCurrentItem(1);
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter));
                        findViewById(R.id.counterTabCalls).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextSize(24);
                        break;
                    case 2:
                        icon = AppHelper.getVectorDrawable(MainActivity.this, R.drawable.ic_person_add_24dp);
                        viewPager.setCurrentItem(2);
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        findViewById(R.id.counterTabCalls).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextSize(24);
                        break;
                    default:
                        break;
                }
                floatingBtnMain.setImageDrawable(icon);
                if (tab.getPosition() != 1) {
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ACTION_MODE_FINISHED));
                }
                final Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        floatingBtnMain.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                floatingBtnMain.startAnimation(animation);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        findViewById(R.id.counterTabCalls).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_calls)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        ((TextView) findViewById(R.id.title_tabs_calls)).setTextSize(20);
                        break;
                    case 1:
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextSize(20);
                        break;
                    case 2:
                        findViewById(R.id.counterTabMessages).setBackground(getResources().getDrawable(R.drawable.bg_circle_tab_counter_unselected));
                        findViewById(R.id.counterTabCalls).setBackground(getResources().getDrawable(R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextSize(20);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });*/

        /*floatingBtnMain.setOnClickListener(view -> {
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    RateHelper.significantEvent(this);
                    Intent intent = new Intent(this, TransferMessageContactsActivity.class);
                    intent.putExtra("forCall", true);
                    startActivity(intent);
                    AnimationsUtil.setSlideInAnimation(this);
                    break;
                case 1:
                    RateHelper.significantEvent(this);
                    AppHelper.LaunchActivity(this, NewConversationContactsActivity.class);
                    break;
                case 2:
                    RateHelper.significantEvent(this);
                    try {
                        Intent mIntent = new Intent(Intent.ACTION_INSERT);
                        mIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        mIntent.putExtra(ContactsContract.Intents.Insert.PHONE, "");
                        startActivityForResult(mIntent, 50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });*/
		
		floatingBtnMain.setOnClickListener(view -> {
			
			RateHelper.significantEvent(this);
			AppHelper.LaunchActivity(this, NewConversationContactsActivity.class);
		});
		
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
			}
			
			@Override
			public void onPageSelected(int position) {
				
				if (position == 2) {
					EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_CONTACTS_FRAGMENT_SELECTED));
					
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			
			}
		});
		
		
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
	
	@Override
	protected void onResume() {
		super.onResume();
		MainView.setVisibility(View.GONE);
		RooyeshApplication.getInstance().setConnectivityListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MainView.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		
		DisConnectFromServer();
		
		
		//added
		destroyMediaProjection();
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
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
			AppHelper.hidePermissionsDialog();
			EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CONTACTS_PERMISSION));
		}
		
		//todo uncomment

        if (requestCode != REQUEST_CODE) {
            Log.e("main activity", "Unknown request code: " + requestCode);
            return;
        }


        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            mToggleButton.setChecked(false);
            return;
        }
		mMediaProjectionCallback = new MediaProjectionCallback();
		mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
		mMediaProjection.registerCallback(mMediaProjectionCallback, null);
		mVirtualDisplay = createVirtualDisplay();
		mMediaRecorder.start();
		startChronoMeter();
		
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
				onToggleScreenShare(mToggleButton);
			} else {
				mToggleButton.setChecked(false);
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
	}
	
	/**
	 * method of EventBus
	 *
	 * @param pusher this is parameter of onEventMainThread method
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(Pusher pusher) {
		switch (pusher.getAction()) {
			case EVENT_BUS_START_REFRESH:
				toolbarProgressBar.setVisibility(View.VISIBLE);
				toolbarProgressBar.getIndeterminateDrawable().setColorFilter(AppHelper.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
				break;
			case EVENT_BUS_STOP_REFRESH:
				toolbarProgressBar.setVisibility(View.GONE);
				break;
			case EVENT_BUS_MESSAGE_COUNTER:
				new Handler().postDelayed(this::loadCounter, 500);
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
				if (viewPager.getCurrentItem() == 3)
					viewPager.setCurrentItem(2);
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
	
	
	private void actionModeDestroyed() {
		if (actionModeStarted) {
			actionModeStarted = false;
			//     tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorPrimary));
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
			//   tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorActionMode));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(AppHelper.getColor(this, R.color.colorActionMode));
			}
		}
		
	}
	
	/**
	 * Callback will be triggered when there is change in
	 * network connection
	 */
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
	
	
	/**
	 * methdo to loadCircleImage number of unread messages
	 */
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
				findViewById(R.id.counterTabMessages).setVisibility(View.GONE);
			} else {
				findViewById(R.id.counterTabMessages).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.counterTabMessages)).setText(String.valueOf(messageCounter));
			}
			if (!realm.isClosed())
				realm.close();
			
		} catch (Exception e) {
			AppHelper.LogCat("loadCounter main activity " + e.getMessage());
		}
		NotificationsManager notificationsManager = new NotificationsManager();
		notificationsManager.SetupBadger(this);
		
	}
	
	/**
	 * method to disconnect from socket server
	 */
	private void DisConnectFromServer() {
		
		try {
			JSONObject json = new JSONObject();
			try {
				json.put("connected", false);
				json.put("senderId", PreferenceManager.getID(this));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (mSocket != null)
				mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json);
		} catch (Exception e) {
			AppHelper.LogCat("User is offline  Exception MainActivity" + e.getMessage());
		}
	}
	
	
	/**
	 * method to check if user connect in an other device
	 */
	public void checkIfUserSession() {
		APIHelper.initialApiUsersContacts().checkIfUserSession().subscribe(networkModel -> {
			if (!networkModel.isConnected()) {
				if (ForegroundRuning.get().isForeground()) {
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setMessage(R.string.your_session_expired);
					alert.setPositiveButton(R.string.ok, (dialog, which) -> {
						PreferenceManager.setToken(MainActivity.this, null);
						PreferenceManager.setID(MainActivity.this, 0);
						PreferenceManager.setSocketID(MainActivity.this, null);
						PreferenceManager.setPhone(MainActivity.this, null);
						PreferenceManager.setIsWaitingForSms(MainActivity.this, false);
						PreferenceManager.setMobileNumber(MainActivity.this, null);
						Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
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
	
	private void sendRegistrationToServer(final String token) {
		if (token != null) {
			updateRegisteredId(token);
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
	
	
	public void setTimeInterval(int value) {
		time_interval = value;
	}
	
	
	@Override
	public void onBackPressed() {
		
		if (this.drawer.isDrawerOpen(GravityCompat.START)) {
			
			this.drawer.closeDrawer(GravityCompat.START);
			
		} else {
			
			if (oldCurrentTimeMillis + time_interval > System.currentTimeMillis()) {
				super.onBackPressed();
				return;
			} else {
				onFirstBackPressed();
				
			}
			oldCurrentTimeMillis = System.currentTimeMillis();
		}
	}
	
	
	public void onFirstBackPressed() {
		Toast.makeText(getBaseContext(), "برای خروج دوباره کلیک کنید.", Toast.LENGTH_SHORT).show();
	}
	
	
	//..........................................................
	//..........................................................
	//..........................................................
	
	public void ShowContact(ContactsModel mContactsModel) {
		final String finalName;
		String name = UtilsPhone.getContactName(this, mContactsModel.getPhone());
		if (name != null) {
			finalName = name;
		} else {
			finalName = mContactsModel.getPhone();
		}
		this.mContactsModel = mContactsModel;
		if (mContactsModel.getPhone() != null) {
			txtEmail.setText("");
		}
		if (mContactsModel.getUsername() != null) {
			txtUser.setText(mContactsModel.getUsername());
		} else {
			txtUser.setText(getString(R.string.no_username));
		}
		txtUser.setText(PreferenceManager.getUserName(this));
		
		
		Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);
		if (bitmap != null) {
			ImageLoader.SetBitmapImage(bitmap, imgProfile);
		} else {
			
			BitmapImageViewTarget target = new BitmapImageViewTarget(imgProfile) {
				@Override
				public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
					super.onResourceReady(bitmap, anim);
					imgProfile.setImageBitmap(bitmap);
					ImageLoader.DownloadImage(memoryCache, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), MainActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);
					
				}
				
				@Override
				public void onLoadFailed(Exception e, Drawable errorDrawable) {
					super.onLoadFailed(e, errorDrawable);
					imgProfile.setImageDrawable(errorDrawable);
				}
				
				@Override
				public void onLoadStarted(Drawable placeholder) {
					super.onLoadStarted(placeholder);
					imgProfile.setImageDrawable(placeholder);
				}
			};
			RooyeshImageLoader.loadCircleImage(this, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), target, R.drawable.image_holder_ur_circle, AppConstants.EDIT_PROFILE_IMAGE_SIZE);
		}
		imgProfile.setOnClickListener(v -> {
			if (mContactsModel.getImage() != null) {
				if (FilesManager.isFilePhotoProfileExists(this, FilesManager.getProfileImage(mContactsModel.getImage()))) {
					AppHelper.LaunchImagePreviewActivity(this, AppConstants.PROFILE_IMAGE, mContactsModel.getImage());
				} else {
					AppHelper.LaunchImagePreviewActivity(MainActivity.this, AppConstants.PROFILE_IMAGE_FROM_SERVER, mContactsModel.getImage());
				}
			}
		});
		
		
	}
	
	
	//''''''''''''''''''''';;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	
	
	@Override
	public void onErrorLoading(Throwable throwable) {
		AppHelper.LogCat(throwable.getMessage());
	}
	
	@Override
	public void onShowLoading() {
	
	}
	
	@Override
	public void onHideLoading() {
	
	}
	
	
	public class CustomTypefaceSpan extends TypefaceSpan {
		
		private final Typeface newType;
		
		public CustomTypefaceSpan(String family, Typeface type) {
			super(family);
			newType = type;
		}
		
		@Override
		public void updateDrawState(TextPaint ds) {
			applyCustomTypeFace(ds, newType);
		}
		
		@Override
		public void updateMeasureState(TextPaint paint) {
			applyCustomTypeFace(paint, newType);
		}
		
		public void applyCustomTypeFace(Paint paint, Typeface tf) {
			int oldStyle;
			Typeface old = paint.getTypeface();
			if (old == null) {
				oldStyle = 0;
			} else {
				oldStyle = old.getStyle();
			}
			
			int fake = oldStyle & ~tf.getStyle();
			if ((fake & Typeface.BOLD) != 0) {
				paint.setFakeBoldText(true);
			}
			
			if ((fake & Typeface.ITALIC) != 0) {
				paint.setTextSkewX(-0.25f);
			}
			
			paint.setTypeface(tf);
		}
	}
	
	private void ShowdialogError() {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_exit);
		dialog.setCancelable(true);
		
		final Button btn_continue = (Button) dialog.findViewById(R.id.btn_try);
		final Button btn_exit = (Button) dialog.findViewById(R.id.btn_exit);
		
		
		btn_exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                Intent intent=new Intent(SelfassesmentCreateMatch.this,ReportMatchActivity.class);
//
////                for(int i=currentSoal;i<questions.size();i++)
////                    answers.add("n");
////                calculateCorrectAnswers();
//
//                //   intent.putExtra("page","3");
//                cancelTimer();
//                cancelTimerquize();
//                startActivity(intent);
//
//                finish();
				
				dialog.dismiss();
				
				
			}
		});
		
		btn_continue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				dialog.dismiss();
				PreferenceManager.clearPreferences(MainActivity.this);
				//  PreferenceManager.setIsLogin(MainActivity.this,false);
				////  PreferenceManager.setIsConfrimed(MainActivity.this,false);
				// PreferenceManager.setToken(MainActivity.this,null);
				Intent i = new Intent(MainActivity.this, Selectstatuse.class);
				startActivity(i);
				finish();
				
				
			}
		});
		
		dialog.show();
	}
	
	public static void applyFontForToolbarTitle(Activity context) {
		Toolbar toolbar = (Toolbar) context.findViewById(R.id.app_bar);
		for (int i = 0; i < toolbar.getChildCount(); i++) {
			View view = toolbar.getChildAt(i);
			if (view instanceof TextView) {
				TextView tv = (TextView) view;
				Typeface titleFont = Typeface.
						createFromAsset(context.getAssets(), "font/IRANSans(FaNum)_Bold");
				if (tv.getText().equals(toolbar.getTitle())) {
					tv.setTypeface(titleFont);
					tv.setTextColor(Color.BLUE);
					break;
				}
			}
		}
	}
	
	private void getversion() {
		
		APIService mApiService;
		//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
		mApiService = new APIService(this);
		
		APIAuthentication apiAuthentication = mApiService.RootService(APIAuthentication.class, EndPoints.BACKEND_BASE_URL);
		Call<ListConversation> VersionAppCall = apiAuthentication.conversation2(1);
		new AuthService(MainActivity.this, mApiService).conversation().enqueue(new Callback<ListConversation>() {
			@Override
			public void onResponse(Call<ListConversation> call, Response<ListConversation> response) {
//                if (response.isSuccessful()) {
//
//                }
				if (response.body().getMusics().size() == 0) {
//                        appVersion = Integer.parseInt(response.body().getAppVersion());
//                        if (appVersion > BuildConfig.VERSION_CODE) {
//                            showUpdateDialog();
//                        } else {
//                            // gotomain();
//                            gotomain();
//                        }
				} else {
					AppHelper.Snackbar(MainActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
					//    gotomain();
				}
			}
			
			
			@Override
			public void onFailure(Call<ListConversation> call, Throwable t) {
				// gotomain();
				Toast.makeText(MainActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}
	
	public void getversion3() {
		APIService mApiService;
		//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
		mApiService = new APIService(this);
		APIAuthentication apiAuthentication = mApiService.RootService(APIAuthentication.class, EndPoints.BACKEND_BASE_URL);
		apiAuthentication.test2()
				
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.map(this::copyOrUpdateConversation);
		
	}
	
	private ConversationsModel2 copyOrUpdateConversation(ConversationsModel2 conver) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		realm.beginTransaction();
		// ConversationsModel2 conversationsModel = realm.copyToRealmOrUpdate(conver);
		ConversationsModel2 conversationsModel = null;
		realm.commitTransaction();
		if (!realm.isClosed()) realm.close();
		return conversationsModel;
	}
	
	private void getversion2() {
		
		APIService mApiService;
		//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
		mApiService = new APIService(this);
		
		
		// Call<ConversationsModel> VersionAppCall = apiAuthentication.test();
		new AuthService(MainActivity.this, mApiService).test().enqueue(new Callback<ConversationsModel2>() {
			@Override
			public void onResponse(Call<ConversationsModel2> call, Response<ConversationsModel2> response) {
//                if (response.isSuccessful()) {
//
//                }
				if (response.body().getMessageDate() == "") {
//                        appVersion = Integer.parseInt(response.body().getAppVersion());
//                        if (appVersion > BuildConfig.VERSION_CODE) {
//                            showUpdateDialog();
//                        } else {
//                            // gotomain();
//                            gotomain();
//                        }
				} else {
					AppHelper.Snackbar(MainActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
					//    gotomain();
				}
			}
			
			
			@Override
			public void onFailure(Call<ConversationsModel2> call, Throwable t) {
				// gotomain();
				Toast.makeText(MainActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}
}

