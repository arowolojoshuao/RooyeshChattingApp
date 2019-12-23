package com.setayeshco.rooyesh.activities.main.welcome;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.CountryActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.adapters.others.TextWatcherAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.CountriesFetcher;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.models.CountriesModel;
import com.setayeshco.rooyesh.models.auth.LoginModel;
import com.setayeshco.rooyesh.services.SMSVerificationService;
import com.setayeshco.rooyesh.ui.CustomProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.accountkit.ui.SkinManager.Skin.CLASSIC;

/**
 * Created by Abderrahim El imame on 09/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
	
	
	@BindView(R.id.numberpass)
	AppCompatEditText numberpass;
	
	@BindView(R.id.numberPhone)
	AppCompatEditText phoneNumberWrapper;
	@BindView(R.id.inputOtpWrapper)
	TextInputEditText inputOtpWrapper;
	@BindView(R.id.btn_request_sms)
	AppCompatTextView btnNext;
	@BindView(R.id.btn_request_sms_kit)
	AppCompatTextView btnNextKit;
	
	@BindView(R.id.progress_bar_load)
	CustomProgressView progressBarLoad;
	
	@BindView(R.id.progress_bar_load_kit)
	CustomProgressView progressBarLoadKit;
	
	@BindView(R.id.btn_change_number)
	AppCompatImageView changeNumberBtn;
	@BindView(R.id.btn_verify_otp)
	AppCompatImageView btnVerifyOtp;
	@BindView(R.id.viewPagerVertical)
	ViewPager viewPager;
	@BindView(R.id.TimeCount)
	TextView textViewShowTime;
	@BindView(R.id.Resend)
	TextView ResendBtn;
	
	@BindView(R.id.country_code)
	AppCompatTextView countryCode;
	@BindView(R.id.short_description_phone)
	AppCompatTextView shortDescriptionPhone;
	@BindView(R.id.country_name)
	AppCompatTextView countryName;
	
	@BindView(R.id.current_mobile_number)
	TextView currentMobileNumber;
	@BindView(R.id.numberPhone_layout_sv)
	NestedScrollView numberPhoneLayoutSv;
	@BindView(R.id.layout_verification_sv)
	NestedScrollView layoutVerificationSv;
	
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.logo)
	LinearLayout LogoWelcome;
	
	private CountDownTimer countDownTimer;
	private long totalTimeCountInMilliseconds;
	
	@BindView(R.id.registrationTerms)
	TextView registrationTerms;
	
	@BindView(R.id.txtv_titr)
	TextView txtv_titr;
	
	
	private String verifyCode;
	String pass;
	boolean prof = false;
	String is_confirmed;
	
	
	private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
	private CountriesModel mSelectedCountry;
	private CountriesFetcher.CountryList mCountries;
	LocalBroadcastManager mLocalBroadcastManager;
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(getPackageName() + "closeWelcomeActivity")) {
				finish();
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		ButterKnife.bind(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("is_prof")) {
				if (bundle.getString("is_prof").equals("1")) {
					prof = true;
				}
				
				if (prof) {
					txtv_titr.setText("کد پرسنلی و رمز عبور خود را وارد کنید");
					phoneNumberWrapper.setHint("کد پرسنلی");
				} else {
					txtv_titr.setText("شماره دانشجویی و رمز عبور خود را وارد کنید");
					phoneNumberWrapper.setHint("شماره دانشجویی");
				}
			}
		}
		initializerView();
		
		getPermissions(this, getApplicationContext());
	}
	
	
	private void setTypeFaces() {
		if (AppConstants.ENABLE_FONTS_TYPES) {
			//  toolbarTitle.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			//  textViewShowTime.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			//   ResendBtn.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			//   countryCode.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			//    currentMobileNumber.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			//   registrationTerms.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
			
		}
	}
	
	
	public void getAppSettings() {
		APIHelper.initialApiUsersContacts().getAppSettings().subscribe(settingsResponse ->
				{
					PreferenceManager.setUnitBannerAdsID(this, settingsResponse.getUnitBannerID());
					PreferenceManager.setShowBannerAds(this, settingsResponse.isAdsBannerStatus());
					PreferenceManager.setUnitVideoAdsID(this, settingsResponse.getUnitVideoID());
					PreferenceManager.setAppVideoAdsID(this, settingsResponse.getAppID());
					PreferenceManager.setShowVideoAds(this, settingsResponse.isAdsVideoStatus());
					PreferenceManager.setUnitInterstitialAdID(this, settingsResponse.getUnitInterstitialID());
					PreferenceManager.setShowInterstitialAds(this, settingsResponse.isAdsInterstitialStatus());
					
					
					int currentAppVersion;
					if (PreferenceManager.getVersionApp(RooyeshApplication.getInstance()) != 0) {
						currentAppVersion = PreferenceManager.getVersionApp(RooyeshApplication.getInstance());
					} else {
						currentAppVersion = AppHelper.getAppVersionCode(RooyeshApplication.getInstance());
					}
					if (currentAppVersion != 0 && currentAppVersion < settingsResponse.getAppVersion()) {
						PreferenceManager.setVersionApp(this, currentAppVersion);
						PreferenceManager.setIsOutDate(this, true);
					} else {
						PreferenceManager.setIsOutDate(this, false);
					}
				},
				throwable -> {
					//    AppHelper.LogCat("Error get settings info Welcome " + throwable.getMessage());
					Log.i("erorrrrrrr", throwable.getMessage());
				});
	}
	
	
	/*  method to initialize the view
	 */
	
	private void initializerView() {
		
		//   Checking if user already connected
		
		
		if (PreferenceManager.getToken(this) != null) {
			Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity00 ");
			
			NotificationsManager notificationsManager = new NotificationsManager();
			notificationsManager.SetupBadger(this);
			if (PreferenceManager.isHasBackup(this)) {
              /*  if (!PreferenceManager.isConfrimed(WelcomeActivity.this)) {
                    Intent intent = new Intent(WelcomeActivity.this, ConfrimedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {*/
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity11 ");
				Log.d("ISTEACHER", "ISTEACHERWelcom1    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
				Log.d("PASSWORDTEST", " 22222    >>>  " + PreferenceManager.getPassword(WelcomeActivity.this));
				Intent intent = new Intent(this, PasswordActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				AnimationsUtil.setSlideInAnimation(this);
				// }
			} else {
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity22 ");
				getAppSettings();
				if (PreferenceManager.isNeedProvideInfo(this)) {
					Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity33 ");
					Log.d("ISTEACHER", "ISTEACHERWelcom2    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
					//ccccccccccccccccccccccccccccccccccccccccccccccccccc
					Log.d("CONFRIMED", "confrimed WelcomeActivity>>>>>> " + PreferenceManager.isConfrimed(WelcomeActivity.this));
					
					
					//ccccccccccccccccccccccccccccccccccccccccccccccccccc
					
					
					Intent intent = new Intent(this, CompleteRegistrationActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					AnimationsUtil.setSlideInAnimation(this);
					
				} else {
                        /*if (!PreferenceManager.isConfrimed(WelcomeActivity.this)) {
                            Intent intent = new Intent(WelcomeActivity.this, ConfrimedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else {*/
					
					
					Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity44 ");
					//................................
					Log.d("ISTEACHER", "ISTEACHERWelcom3    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
					
					
					Log.d("PASSWORDTEST", " 11111    >>>  " + PreferenceManager.getPassword(WelcomeActivity.this));
					
					
					//lllllll  error ,,,,,,,,,,,,,,,,,,,,,,
					
					// PreferenceManager.isTeacher(WelcomeActivity.this);
					
					//lllllll  error ,,,,,,,,,,,,,,,,,,,,,,
					
					
					Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity99999999999999");
					Intent intent = new Intent(WelcomeActivity.this, PasswordActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					AnimationsUtil.setSlideInAnimation(WelcomeActivity.this);
					WelcomeActivity.this.finish();
					
					
					//................................




/*                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final View layout = inflater.inflate(R.layout.password_dialog, (ViewGroup) findViewById(R.id.root));
                        final EditText password1 = (EditText) layout.findViewById(R.id.EditText_Pwd1);
                        final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);

                        builder.setTitle(" رمز عبور ");
                        builder.setView(layout);
                        builder.setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                WelcomeActivity.this.finish();
                            }
                        });

                        builder.setPositiveButton(" تایید ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String strPassword = password1.getText().toString();

                                if (strPassword.equals(pass)) {


                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        AnimationsUtil.setSlideInAnimation(WelcomeActivity.this);

                    }

                    }
                });

            builder.setCancelable(false);
            builder.create().show();*/
					
					
					//    }
					
				}
			}
		} else {
			
			Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity55 ");
			Log.d("ISTEACHER", "ISTEACHERWelcom4    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
			mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(getPackageName() + "closeWelcomeActivity");
			mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
			setTypeFaces();
			if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity66 ");
				btnNextKit.setText(getString(R.string.get_started));
				btnNextKit.setEnabled(true);
				btnNextKit.setVisibility(View.VISIBLE);
				LogoWelcome.setVisibility(View.VISIBLE);
				layoutVerificationSv.setVisibility(View.GONE);
				numberPhoneLayoutSv.setVisibility(View.GONE);
				viewPager.setVisibility(View.GONE);
			} else {
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity77 ");
				btnNextKit.setVisibility(View.GONE);
				// btnNext.setText(getString(R.string.next));
				btnNext.setText("ورود");
				btnNext.setEnabled(true);
				btnNext.setVisibility(View.VISIBLE);
				LogoWelcome.setVisibility(View.GONE);
				layoutVerificationSv.setVisibility(View.VISIBLE);
				numberPhoneLayoutSv.setVisibility(View.VISIBLE);
				viewPager.setVisibility(View.VISIBLE);
			}
			hideKeyboard();
			
			mCountries = CountriesFetcher.getCountries(this);
			
			int defaultIdx = mCountries.indexOfIso(AppConstants.DEFAULT_COUNTRY_CODE);
			mSelectedCountry = mCountries.get(defaultIdx);
			////  countryCode.setText(mSelectedCountry.getDial_code());
			
			countryName.setText(mSelectedCountry.getName());
			//  shortDescriptionPhone.setText(getString(R.string.click_on) + " " + mSelectedCountry.getDial_code() + " " + getString(R.string.to_choose_your_country_n_and_enter_your_phone_number));
			setHint();
			
			btnNext.setOnClickListener(this);
			btnNextKit.setOnClickListener(this);
			countryCode.setOnClickListener(this);
			btnVerifyOtp.setOnClickListener(this);
			ResendBtn.setOnClickListener(this);
			changeNumberBtn.setOnClickListener(this);
			ViewPagerAdapter adapter = new ViewPagerAdapter();
			viewPager.setAdapter(adapter);
			inputOtpWrapper.addTextChangedListener(new TextWatcherAdapter() {
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
				}
				
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				}
				
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void afterTextChanged(Editable s) {
					if (s.length() == 6) {
						verificationOfCode();
					}
				}
			});


             /* Checking if the device is waiting for sms
             showing the user Code screen
*/
			if (PreferenceManager.isWaitingForSms(this)) {
				viewPager.setCurrentItem(1);
				setTimer();
				resumeTimer();
			}
			
			if (viewPager.getCurrentItem() == 1) {
				setOnKeyboardCodeDone();
				
				if (PermissionHandler.checkPermission(this, Manifest.permission.RECEIVE_SMS)) {
					AppHelper.LogCat("RECEIVE SMS permission already granted.");
				} else {
					AppHelper.LogCat("Please request RECEIVE SMS permission.");
					PermissionHandler.requestPermission(this, Manifest.permission.RECEIVE_SMS);
				}
				if (PermissionHandler.checkPermission(this, Manifest.permission.READ_SMS)) {
					AppHelper.LogCat("READ SMS permission already granted.");
				} else {
					AppHelper.LogCat("Please request READ SMS permission.");
					PermissionHandler.requestPermission(this, Manifest.permission.READ_SMS);
				}
				
			} else {
				setOnKeyboardDone();
			}
			
		}
		
	}
	
	public void onSMSLoginKit() {
		if (PermissionHandler.checkPermission(this, Manifest.permission.RECEIVE_SMS) || PermissionHandler.checkPermission(this, Manifest.permission.READ_SMS)) {
			AppHelper.LogCat(" SMS permission already granted.");
			Log.d("ISTEACHER", "ISTEACHERWelcom5    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
			Intent intent = new Intent(this, AccountKitActivity.class);
			UIManager uiManager;
			uiManager = new SkinManager(CLASSIC, AppHelper.getColor(this, R.color.colorPrimary));
			AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
					new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
							AccountKitActivity.ResponseType.TOKEN);
			configurationBuilder.setUIManager(uiManager);
			intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
			startActivityForResult(intent, AppConstants.APP_REQUEST_CODE);
			
		} else {
			AppHelper.LogCat("Please request RECEIVE SMS permission.");
			PermissionHandler.requestPermission(this, Manifest.permission.RECEIVE_SMS);
			PermissionHandler.requestPermission(this, Manifest.permission.READ_SMS);
		}
		
	}

// method to validate user information
	
	
	private void validateInformation() {
		hideKeyboard();
		Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
		if (phoneNumber != null) {
			String phoneNumberFinal = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
			String password = numberpass.getText().toString().trim();
			String std_number = phoneNumberWrapper.getText().toString().trim();
			//  if (isValid())
			if (isValidNew(std_number, password)) {
				String internationalFormat = phoneNumberFinal.replace("-", "");
				//  String password = numberpass.getText().toString().trim();
				String finalResult = internationalFormat.replace(" ", "");
				PreferenceManager.setMobileNumber(this, finalResult);
				//  onSMSLoginKit();
				requestForSMS(std_number, mSelectedCountry.getName(), password);
			} else {
				phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
				Toast.makeText(this, "همه ورودی ها را کامل کنید.", Toast.LENGTH_SHORT).show();
			}
		} else {
			phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
		}
	}
	
	/*
	 *
	 * method to resend a request for SMS
	 *
	 * @param mobile this is parameter of ResendRequestForSMS method
	 
	 */
	
	private void ResendRequestForSMS(String mobile) {
		
		APIHelper.initializeAuthService().resend(mobile).subscribe(joinModelResponse -> {
			if (joinModelResponse.isSuccess()) {
				//  PreferenceManager.setUserPassword(getApplicationContext(), joinModelResponse.getPassword());
				pass = joinModelResponse.getPassword();
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   pass   " + joinModelResponse.getPassword());
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity pass sss");
				
				ResendBtn.setVisibility(View.GONE);
				textViewShowTime.setVisibility(View.VISIBLE);
				setTimer();
				startTimer();
				PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, true);
				viewPager.setCurrentItem(1);
				currentMobileNumber.setText(PreferenceManager.getMobileNumber(WelcomeActivity.this));
			} else {
				AppHelper.CustomToast(WelcomeActivity.this, joinModelResponse.getMessage());
			}
		}, throwable -> {
			AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.unexpected_reponse_from_server));
		});
		
	}
	
	/*
	 *
	 * method to send an SMS request to provider
	 *
	 * @param mobile  this the first parameter of  requestForSMS method
	 * @param country this the second parameter of requestForSMS  method
	 
	 */
	
	private void requestForSMS(String mobile, String country, String passw) {
		LoginModel loginModel = new LoginModel();
		loginModel.setCountry(country);
		loginModel.setMobile(mobile);
		loginModel.setPassword(passw);
		
		
		if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
			progressBarLoadKit.setVisibility(View.VISIBLE);
			progressBarLoadKit.setColor(AppHelper.getColor(this, R.color.colorWhite));
			btnNextKit.setText(getString(R.string.set_back_and_keep_calm_you_will_receive_an_sms_of_verification_kit));
			btnNextKit.setEnabled(false);
		} else {
			
			progressBarLoad.setVisibility(View.VISIBLE);
			progressBarLoad.setColor(AppHelper.getColor(this, R.color.colorWhite));
			//   btnNext.setText(getString(R.string.set_back_and_keep_calm_you_will_receive_an_sms_of_verification));
			btnNext.setEnabled(false);
		}
		
		APIHelper.initializeAuthService().join(mobile, passw).subscribe(joinModelResponse -> {
			if (joinModelResponse.isSuccess()) {
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   WelcomeActivity pass sss");
				Log.d("PASSWORDLOG", ">>>>>>>>>>>>>>   pass   " + joinModelResponse.getPassword());
				PreferenceManager.setUserPassword(getApplicationContext(), joinModelResponse.getPassword());
				PreferenceManager.setUserId(getApplicationContext(), joinModelResponse.getUserID());
				PreferenceManager.setID(getApplicationContext(), joinModelResponse.getUserID());

//                PreferenceManager.setUserPassword(getApplicationContext(), "123");
//               PreferenceManager.setUserId(getApplicationContext(), 1);
				
				if (!prof) {
					PreferenceManager.setUserRole(WelcomeActivity.this, (joinModelResponse.getIs_prof().equals("1") ? true : false));
				}
				PreferenceManager.setUserPassword(WelcomeActivity.this, joinModelResponse.getPassword());
				if (joinModelResponse.getIs_confirmed().equals("1")) {
					PreferenceManager.setIsConfrimed(WelcomeActivity.this, true);
				} else {
					PreferenceManager.setIsConfrimed(WelcomeActivity.this, false);
				}
				
				PreferenceManager.setIsLogin(WelcomeActivity.this, true);
				PreferenceManager.setMobileNumber(WelcomeActivity.this, joinModelResponse.getMobile());
				PreferenceManager.setIsConfrimed(WelcomeActivity.this, true);
				// PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, false);
				PreferenceManager.setIsNeedInfo(WelcomeActivity.this, false);
				PreferenceManager.setHasBackup(WelcomeActivity.this, false);
				PreferenceManager.setToken(WelcomeActivity.this, joinModelResponse.getToken());
//				Log.w("arash_token", joinModelResponse.getToken());
				PreferenceManager.setUsername(WelcomeActivity.this, joinModelResponse.getUsername());

//                if (!AppHelper.isServiceRunning(this, MainService.class)
//                        && PreferenceManager.getToken(this) != null
//                        ) {
//                    Log.d("SERVICCC","SERVICEEEE .>>>> 10101010");
//                    this.startService(new Intent(this, MainService.class));
//                }
				
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				finish();
				
				//mmmmmmmmmmmmmmmmmmmmmmmmmmm
				
				//  PreferenceManager.setIsConfrimed(getApplicationContext(), (joinModelResponse.getIs_confirmed().equals("1") ? true : false));
				Log.d("ISCONFRIMED", ">>>>>>>>>>>>>>   ISCONFRIMED : " + PreferenceManager.isConfrimed(WelcomeActivity.this));
				
				
				//''''''''''''''''''''''''''''''''''''''''''

        /*        if (joinModelResponse.getIs_confirmed().equals("0") || joinModelResponse.getIs_confirmed().equals(0) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View layout = inflater.inflate(R.layout.is_confrimed_layout, (ViewGroup) findViewById(R.id.root));

                    builder.setTitle(" دریافت تاییدیه ... ");
                    builder.setView(layout);
                    builder.setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            WelcomeActivity.this.finish();
                        }
                    });

        *//*        builder.setPositiveButton(" تایید ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String strPassword = password1.getText().toString();
                        //   Toast.makeText(MainActivity.this,"strPassword  "+strPassword,Toast.LENGTH_LONG).show();
                        //    Toast.makeText(MainActivity.this,"Password22  "+PreferenceManager.getPassword(MainActivity.this),Toast.LENGTH_LONG).show();
                        if (strPassword.equals(PreferenceManager.getPassword(WelcomeActivity.this))) {


                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }else {
                            WelcomeActivity.this.finish();
                        }

                    }
                });*//*

                    builder.setCancelable(false);
                    builder.create().show();


                }*/
				
				//''''''''''''''''''''''''''''''''''''''''''
				
				
				//mmmmmmmmmmmmmmmmmmmmmmmmmmm


             /*   if (PreferenceManager.getToken(this) != null) {
                      PreferenceManager.setUserRole(getApplicationContext(), ((joinModelResponse.getIs_prof().equals("1") ||joinModelResponse.getIs_prof().equals(1))? true : false));
                }*/
//                    pass = joinModelResponse.getPassword();
//
//                if (joinModelResponse.isSmsVerification()) {
//                    PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, false);
//                    smsVerification(joinModelResponse.getCode());
//                } else {
//                    setTimer();
//                    startTimer();
//                    PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, true);
//                    viewPager.setCurrentItem(1);
//                    currentMobileNumber.setText(PreferenceManager.getMobileNumber(WelcomeActivity.this));
				// smsVerification(joinModelResponse.getCode());

//                    Intent i = new Intent(this,MainActivity.class);
//                    startActivity(i);
//                    finish();
				//  }
			} else {
				if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
					btnNextKit.setText(getString(R.string.get_started));
					btnNextKit.setEnabled(true);
					progressBarLoadKit.setVisibility(View.GONE);
				} else {
					
					//   btnNext.setText(getString(R.string.next));
					btnNext.setText("ورود");
					btnNext.setEnabled(true);
					progressBarLoad.setVisibility(View.GONE);
					Toast.makeText(this, "شماره دانشجویی یا رمز عبور اشتباه است", Toast.LENGTH_LONG).show();
				}
			}
			
		}, throwable -> {
			if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
				btnNextKit.setText(getString(R.string.get_started));
				btnNextKit.setEnabled(true);
				progressBarLoadKit.setVisibility(View.GONE);
			} else {
				
				// btnNext.setText(getString(R.string.next));
				btnNext.setText("ورود");
				btnNext.setEnabled(true);
				progressBarLoad.setVisibility(View.GONE);
			}
			AppHelper.LogCat("Failed to login into  account " + throwable.getMessage());
			AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.unexpected_reponse_from_server));
			hideKeyboard();
		});
		
	}
	
	/**
	 * this if you disabled verification by sms
	 *
	 * @param code
	 */
	
	private void smsVerification(String code) {
		if (!code.isEmpty()) {
			Log.d("ISTEACHER", "ISTEACHERWelcom6    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
			Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
			otpIntent.putExtra("code", code);
			otpIntent.putExtra("register", true);
			startService(otpIntent);
		} else {
			AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.please_enter_your_ver_code));
		}
	}

// method to verify the code received by user then activating the user
	
	
	private void verificationOfCode() {
		hideKeyboard();
		String code = inputOtpWrapper.getText().toString().trim();
		if (!code.isEmpty()) {
			Log.d("ISTEACHER", "ISTEACHERWelcom7    >>>  " + PreferenceManager.isTeacher(WelcomeActivity.this));
			Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
			otpIntent.putExtra("code", code);
			otpIntent.putExtra("register", true);
			startService(otpIntent);
		} else {
			AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.please_enter_your_ver_code));
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_request_sms:
				validateInformation();
				break;
			case R.id.btn_request_sms_kit:
				onSMSLoginKit();
				break;
			case R.id.country_code:
				Intent mIntent = new Intent(this, CountryActivity.class);
				//   startActivityForResult(mIntent, AppConstants.SELECT_COUNTRY);
				break;
			
			case R.id.btn_verify_otp:
				verificationOfCode();
				break;
			
			case R.id.btn_change_number:
				viewPager.setCurrentItem(0);
				stopTimer();
				PreferenceManager.setID(this, 0);
				PreferenceManager.setToken(this, null);
				PreferenceManager.setMobileNumber(this, null);
				PreferenceManager.setIsWaitingForSms(this, false);
				break;
			
			case R.id.Resend:
				viewPager.setCurrentItem(1);
				ResendRequestForSMS(PreferenceManager.getMobileNumber(this));
				break;
		}
	}
	
	private class ViewPagerAdapter extends PagerAdapter {
		
		@Override
		public int getCount() {
			return 2;
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}
		
		public Object instantiateItem(View collection, int position) {
			
			int resId = 0;
			switch (position) {
				case 0:
					resId = R.id.numberPhone_layout;
					break;
				case 1:
					resId = R.id.layout_verification;
					break;
			}
			return findViewById(resId);
		}
	}
	
	private void setTimer() {
		int time = 1;
		totalTimeCountInMilliseconds = 60 * time * 1000;
		
	}
	
	private void startTimer() {
		countDownTimer = new RooyeshCounter(totalTimeCountInMilliseconds, 500).start();
	}
	
	public void stopTimer() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
	}
	
	public void resumeTimer() {
		textViewShowTime.setVisibility(View.VISIBLE);
		countDownTimer = new RooyeshCounter(totalTimeCountInMilliseconds, 500).start();
	}
	
	
	public class RooyeshCounter extends CountDownTimer {
		
		RooyeshCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onTick(long leftTimeInMilliseconds) {
			long seconds = leftTimeInMilliseconds / 1000;
			textViewShowTime.setText(String.format(Locale.getDefault(), "%02d", seconds / 60) + ":" + String.format(Locale.getDefault(), "%02d", seconds % 60));
		}
		
		@Override
		public void onFinish() {
			textViewShowTime.setVisibility(View.GONE);
			ResendBtn.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocalBroadcastManager != null)
			mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConstants.SELECT_COUNTRY) {
				phoneNumberWrapper.setEnabled(true);
				numberPhoneLayoutSv.pageScroll(View.FOCUS_DOWN);
				String codeIso = data.getStringExtra("countryIso");
				String countryName = data.getStringExtra("countryCode");
				int defaultIdx = mCountries.indexOfIso(codeIso);
				mSelectedCountry = mCountries.get(defaultIdx);
				//  this.countryCode.setText(mSelectedCountry.getDial_code());
				this.countryName.setText(mSelectedCountry.getName());
				shortDescriptionPhone.setText(getString(R.string.click_on) + " " + mSelectedCountry.getDial_code() + " " + getString(R.string.to_choose_your_country_n_and_enter_your_phone_number));
				setHint();
			} else if (requestCode == AppConstants.APP_REQUEST_CODE) {
				AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
				if (loginResult.getError() != null) {
					AppHelper.hideDialog();
					AppHelper.CustomToast(WelcomeActivity.this, loginResult.getError().getErrorType().getMessage());
				} else if (loginResult.wasCancelled()) {
					AppHelper.hideDialog();
					AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.oops_something));
				} else {
					
					AccountKit.getCurrentAccount(new AccountKitCallback<com.facebook.accountkit.Account>() {
						@Override
						public void onSuccess(final com.facebook.accountkit.Account account) {
							// Get phone number
							PhoneNumber phoneNumber = account.getPhoneNumber();
							
							
							String code = "+" + account.getPhoneNumber().getCountryCode();
							int defaultIdx = mCountries.indexOfDialCode(code);
							mSelectedCountry = mCountries.get(defaultIdx);
							
							String phoneNumberString = phoneNumber.toString();
							PreferenceManager.setMobileNumber(WelcomeActivity.this, phoneNumberString);
							requestForSMS(phoneNumberString, mSelectedCountry.getName(), "");
							AccessToken accessToken = AccountKit.getCurrentAccessToken();
							if (accessToken != null) {
								
								AccountKit.logOut();
							}
						}
						
						@Override
						public void onError(final AccountKitError error) {
							AppHelper.CustomToast(WelcomeActivity.this, error.getErrorType().getMessage());
						}
					});
					
				}
			}
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

// Hide keyboard from phoneEdit field
	
	
	public void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(phoneNumberWrapper.getWindowToken(), 0);
	}


// Set hint number for country
	
	
	private void setHint() {
		
		if (phoneNumberWrapper != null && mSelectedCountry != null && mSelectedCountry.getCode() != null) {
			Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.getExampleNumberForType(mSelectedCountry.getCode(), PhoneNumberUtil.PhoneNumberType.MOBILE);
			if (phoneNumber != null) {
				String internationalNumber = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
				String finalPhone = internationalNumber.substring(mSelectedCountry.getDial_code().length());
				
				
				//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				
				//  phoneNumberWrapper.setHint(finalPhone);
				//  phoneNumberWrapper.setHint("9876543210");
				
				//int numberLength = internationalNumber.length();
				int numberLength = 20; // input phone number size
				
				//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				
				InputFilter[] fArray = new InputFilter[1];
				fArray[0] = new InputFilter.LengthFilter(numberLength);
				phoneNumberWrapper.setFilters(fArray);
				
			}
		}
		
	}
	
	
	/**
	 * Get PhoneNumber object
	 *
	 * @return PhoneNumber | null on error
	 */
	
	
	@SuppressWarnings("unused")
	public Phonenumber.PhoneNumber getPhoneNumber() {
		try {
			String iso = null;
			if (mSelectedCountry != null) {
				iso = mSelectedCountry.getCode();
			}
			String phone = countryCode.getText().toString().concat(phoneNumberWrapper.getText().toString());
			return mPhoneUtil.parse(phone, iso);
		} catch (NumberParseException ignored) {
			return null;
		}
	}
	
	/*
	 *
	 * Check if number is valid
	 *
	 * @return boolean
	 */
	
	@SuppressWarnings("unused")
	public boolean isValid() {
		Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
		return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
	}
	
	public boolean isValidNew(String std_number, String password) {
		if (std_number == null || std_number.equals(""))
			return false;
		if (password == null || password.equals(""))
			return false;
		
		return true;
	}
	
	public void setOnKeyboardDone() {
		phoneNumberWrapper.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				hideKeyboard();
			}
			return false;
		});
	}
	
	public void setOnKeyboardCodeDone() {
		inputOtpWrapper.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				verificationOfCode();
			}
			return false;
		});
	}
	
	
	static public void getPermissions(Activity activity, Context context) {
		final int REQUEST_ID_MULTIPLE_PERMISSIONS = 4611;
		
		int location2 = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
		int location = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
		int network = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
		int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
		int internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
		int vibration = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
		int read_storage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
		int write_storage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		
		List<String> listPermissionsNeeded = new ArrayList<>();
		
		if (location != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
		if (location2 != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		if (network != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
		if (bluetooth != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
		if (internet != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.INTERNET);
		if (vibration != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.VIBRATE);
		if (read_storage != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		if (write_storage != PackageManager.PERMISSION_GRANTED)
			listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		
		if (!listPermissionsNeeded.isEmpty())
			ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray
					(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
	}
	
}


