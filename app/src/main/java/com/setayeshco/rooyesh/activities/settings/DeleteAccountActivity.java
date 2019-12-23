package com.setayeshco.rooyesh.activities.settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.adapters.others.TextWatcherAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.CountriesFetcher;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.models.CountriesModel;
import com.setayeshco.rooyesh.services.SMSVerificationService;
import com.setayeshco.rooyesh.ui.CustomProgressView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.facebook.accountkit.ui.SkinManager.Skin.CLASSIC;

/**
 * Created by Abderrahim El imame on 09/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class DeleteAccountActivity extends AppCompatActivity implements View.OnClickListener {


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
    @BindView(R.id.account_kit_layout)
    LinearLayout accountKitLayout;
    @BindView(R.id.layout_verification_sv)
    NestedScrollView layoutVerificationSv;

    @BindView(R.id.app_bar)
    Toolbar toolbar;

    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
    private CountriesModel mSelectedCountry;
    private CountriesFetcher.CountryList mCountries;
    private Realm realm;
    private UsersService mUsersContactsDelete;
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(getPackageName() + "closeDeleteAccountActivity")) {
                AppHelper.showDialog(DeleteAccountActivity.this, getString(R.string.deleting));
                new Handler().postDelayed(() -> {
                    AppHelper.hideDialog();
                    PreferenceManager.setToken(DeleteAccountActivity.this, null);
                    PreferenceManager.setID(DeleteAccountActivity.this, 0);
                    PreferenceManager.setSocketID(DeleteAccountActivity.this, null);
                    PreferenceManager.setPhone(DeleteAccountActivity.this, null);
                    PreferenceManager.setIsWaitingForSms(DeleteAccountActivity.this, false);
                    PreferenceManager.setMobileNumber(DeleteAccountActivity.this, null);
                    PreferenceManager.setLastBackup(DeleteAccountActivity.this, null);
                    NotificationsManager notificationsManager = new NotificationsManager();
                    notificationsManager.SetupBadger(DeleteAccountActivity.this);
                    RealmBackupRestore.deleteData(DeleteAccountActivity.this);
                    AppHelper.deleteCache(DeleteAccountActivity.this);
                    Intent mIntent1 = new Intent(DeleteAccountActivity.this, WelcomeActivity.class);
                    mIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(RooyeshApplication.getInstance().getBaseContext(), 0, mIntent1, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager mgr = (AlarmManager) RooyeshApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                    finish();
                    System.exit(2);
                }, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        ButterKnife.bind(this);
        realm = RooyeshApplication.getRealmDatabaseInstance();
        initializerView();
        APIService mApiServiceDelete = APIService.with(this);
        mUsersContactsDelete = new UsersService(realm, this, mApiServiceDelete);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + "closeDeleteAccountActivity");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }


    /**
     * method to initialize the view
     */
    private void initializerView() {

        if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
            btnNextKit.setText(getString(R.string.get_started));
            btnNextKit.setEnabled(true);
            btnNextKit.setVisibility(View.VISIBLE);
            accountKitLayout.setVisibility(View.VISIBLE);
            layoutVerificationSv.setVisibility(View.GONE);
            numberPhoneLayoutSv.setVisibility(View.GONE);
        } else {
            btnNextKit.setVisibility(View.GONE);
            btnNext.setText(getString(R.string.get_started));
            btnNext.setEnabled(true);
            btnNext.setVisibility(View.VISIBLE);
            accountKitLayout.setVisibility(View.GONE);
            layoutVerificationSv.setVisibility(View.VISIBLE);
            numberPhoneLayoutSv.setVisibility(View.VISIBLE);
        }
        hideKeyboard();
        mCountries = CountriesFetcher.getCountries(this);

        int defaultIdx = mCountries.indexOfIso(AppConstants.DEFAULT_COUNTRY_CODE);
        mSelectedCountry = mCountries.get(defaultIdx);
        countryCode.setText(mSelectedCountry.getDial_code());
        countryName.setText(mSelectedCountry.getName());
        shortDescriptionPhone.setText(getString(R.string.click_on) + " " + mSelectedCountry.getDial_code() + " " + getString(R.string.to_choose_your_country_n_and_enter_your_phone_number));
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
        setupToolbar();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void onSMSLoginKit() {
        if (PermissionHandler.checkPermission(this, Manifest.permission.RECEIVE_SMS) || PermissionHandler.checkPermission(this, Manifest.permission.READ_SMS)) {
            AppHelper.LogCat(" SMS permission already granted.");

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


    /**
     * method to validate user information
     */
    private void validateInformation() {
        hideKeyboard();
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        if (phoneNumber != null) {
            String phoneNumberFinal = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            if (isValid()) {
                String internationalFormat = phoneNumberFinal.replace("-", "");
                String finalResult = internationalFormat.replace(" ", "");
                PreferenceManager.setMobileNumber(this, finalResult);
                requestForSMS(finalResult, mSelectedCountry.getName());
            } else {
                phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
            }
        } else {
            phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
        }
    }


    /**
     * method to send an SMS request to provider
     *
     * @param mobile this the first parameter of  requestForSMS method
     */
    private void requestForSMS(String mobile, String country) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_message_delete_account);
        builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
            if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {

                progressBarLoadKit.setVisibility(View.VISIBLE);
                progressBarLoadKit.setColor(AppHelper.getColor(this, R.color.colorWhite));
                btnNextKit.setText(getString(R.string.set_back_and_keep_calm_you_will_receive_an_sms_of_verification_kit));
                btnNextKit.setEnabled(false);
            } else {

                progressBarLoad.setVisibility(View.VISIBLE);
                progressBarLoad.setColor(AppHelper.getColor(this, R.color.colorWhite));
                btnNext.setText(getString(R.string.set_back_and_keep_calm_you_will_receive_an_sms_of_verification));
                btnNext.setEnabled(false);
            }

            mUsersContactsDelete.deleteAccount(mobile, country).subscribe(response -> {
                if (response.isSuccess()) {
                    if (!response.isSmsVerification()) {
                        smsVerification(response.getCode());
                    } else {
                        setTimer();
                        startTimer();
                        viewPager.setCurrentItem(1);
                    }
                } else {

                    if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
                        btnNextKit.setText(getString(R.string.next));
                        btnNextKit.setEnabled(true);
                        progressBarLoadKit.setVisibility(View.GONE);
                    } else {

                        btnNext.setText(getString(R.string.next));
                        btnNext.setEnabled(true);
                        progressBarLoad.setVisibility(View.GONE);
                    }
                    AppHelper.CustomToast(DeleteAccountActivity.this, response.getMessage());
                }
            }, throwable -> {

                if (AppConstants.ENABLE_FACEBOOK_ACCOUNT_KIT) {
                    btnNextKit.setText(getString(R.string.next));
                    btnNextKit.setEnabled(true);
                    progressBarLoadKit.setVisibility(View.GONE);
                } else {

                    btnNext.setText(getString(R.string.next));
                    btnNext.setEnabled(true);
                    progressBarLoad.setVisibility(View.GONE);
                }
                hideKeyboard();
                AppHelper.LogCat("delete  account " + throwable.getMessage());
                AppHelper.CustomToast(DeleteAccountActivity.this, getString(R.string.delete_account_failed_please_try_later));
            });
        });
        builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {

        });

        builder.show();
    }

    /**
     * this if you disabled verification by sms
     *
     * @param code
     */
    private void smsVerification(String code) {
        if (!code.isEmpty()) {
            Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
            otpIntent.putExtra("code", code);
            otpIntent.putExtra("register", false);
            startService(otpIntent);
        } else {
            AppHelper.CustomToast(this, getString(R.string.please_enter_your_ver_code));
        }
    }

    /**
     * method to verify the code received by user then activating the user
     */

    private void verificationOfCode() {
        String code = inputOtpWrapper.getText().toString().trim();
        if (!code.isEmpty()) {
            Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
            otpIntent.putExtra("code", code);
            otpIntent.putExtra("register", false);
            startService(otpIntent);
        } else {
            AppHelper.CustomToast(DeleteAccountActivity.this, getString(R.string.please_enter_your_ver_code));
        }
    }


    @Override
    public void onClick(View view) {
        Intent mIntent;
        switch (view.getId()) {
            case R.id.btn_request_sms:
                validateInformation();
                break;
            case R.id.btn_request_sms_kit:
                onSMSLoginKit();
                break;
            case R.id.country_code:
                mIntent = new Intent(this, CountryActivity.class);
                startActivityForResult(mIntent, AppConstants.SELECT_COUNTRY);
                break;
            case R.id.btn_verify_otp:
                verificationOfCode();
                break;
            case R.id.country_name:
                mIntent = new Intent(this, CountryActivity.class);
                startActivityForResult(mIntent, AppConstants.SELECT_COUNTRY);
                break;
            case R.id.btn_change_number:
                PreferenceManager.setMobileNumber(this, null);
                viewPager.setCurrentItem(0);
                stopTimer();
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
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        realm.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.SELECT_COUNTRY) {

                numberPhoneLayoutSv.pageScroll(View.FOCUS_DOWN);
                String codeIso = data.getStringExtra("countryIso");
                String countryName = data.getStringExtra("countryCode");
                int defaultIdx = mCountries.indexOfIso(codeIso);
                mSelectedCountry = mCountries.get(defaultIdx);
                this.countryCode.setText(mSelectedCountry.getDial_code());
                this.countryName.setText(mSelectedCountry.getName());
                shortDescriptionPhone.setText(getString(R.string.click_on) + " " + mSelectedCountry.getDial_code() + " " + getString(R.string.to_choose_your_country_n_and_enter_your_phone_number));
                setHint();
            } else if (requestCode == AppConstants.APP_REQUEST_CODE) {
                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                if (loginResult.getError() != null) {
                    AppHelper.hideDialog();
                    AppHelper.CustomToast(DeleteAccountActivity.this, loginResult.getError().getErrorType().getMessage());
                } else if (loginResult.wasCancelled()) {
                    AppHelper.hideDialog();
                    AppHelper.CustomToast(DeleteAccountActivity.this, getString(R.string.oops_something));
                } else {

                    AccountKit.getCurrentAccount(new AccountKitCallback<com.facebook.accountkit.Account>() {
                        @Override
                        public void onSuccess(final com.facebook.accountkit.Account account) {
                            // Get phone number
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            String phoneNumberString = phoneNumber.toString();
                            if (phoneNumberString.equals(PreferenceManager.getPhone(DeleteAccountActivity.this))) {
                                requestForSMS(phoneNumberString, mSelectedCountry.getName());
                            } else {
                                AppHelper.Snackbar(DeleteAccountActivity.this, findViewById(R.id.viewContainer), getString(R.string.number_not_match), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                            }

                            AccessToken accessToken = AccountKit.getCurrentAccessToken();
                            if (accessToken != null) {
                                AccountKit.logOut();
                            }
                        }

                        @Override
                        public void onError(final AccountKitError error) {
                            AppHelper.CustomToast(DeleteAccountActivity.this, error.getErrorType().getMessage());
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

    /**
     * Hide keyboard from phoneEdit field
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(phoneNumberWrapper.getWindowToken(), 0);
    }


    /**
     * Set hint number for country
     */
    private void setHint() {
        if (phoneNumberWrapper != null && mSelectedCountry != null && mSelectedCountry.getCode() != null) {
            Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.getExampleNumberForType(mSelectedCountry.getCode(), PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                String internationalNumber = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                String finalPhone = internationalNumber.substring(mSelectedCountry.getDial_code().length());
                phoneNumberWrapper.setHint(finalPhone);
                int numberLength = internationalNumber.length();
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


    /**
     * Check if number is valid
     *
     * @return boolean
     */
    @SuppressWarnings("unused")
    public boolean isValid() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    public void setOnKeyboardDone() {
        phoneNumberWrapper.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateInformation();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            AnimationsUtil.setSlideOutAnimation(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
    }

}
