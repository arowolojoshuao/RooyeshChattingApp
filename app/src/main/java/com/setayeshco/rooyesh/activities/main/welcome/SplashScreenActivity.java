package com.setayeshco.rooyesh.activities.main.welcome;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.profile.ProfileActivity;
import com.setayeshco.rooyesh.activities.profile.Selectstatuse;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIAuthentication;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.AuthService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.TopAlignSuperscriptSpan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.setayeshco.rooyesh.BuildConfig;
import com.setayeshco.rooyesh.models.auth.VersionApp;
import com.setayeshco.rooyesh.models.groups.GroupResponse;
import com.setayeshco.rooyesh.services.MainService;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * Created by Abderrahim El imame on 1/9/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class SplashScreenActivity extends AppCompatActivity {

    int SPLASH_TIME_OUT = 1000;
    private APIService mApiService;
    @BindView(R.id.splash_app_name)
    AppCompatTextView splashAppName;
    @BindView(R.id.splash_message)
    AppCompatTextView splashMessage;


    private Dialog checkVersionDialog;
    //private int appVersion = -1;
    private ProgressBar pbDownload;
    private LinearLayout plDownload;
    private TextView tvDownloadPrc;
    private DownloadNewVersion d;
    private Button btnDownloadNow, btnDownloadLater;
    int appVersion=-1;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        //.....................mmmmmmmmmmmm............................

        PreferenceManager.setIsConfrimed(SplashScreenActivity.this, true);

        //.....................mmmmmmmmmmmm............................

        if (AppHelper.isAndroid5()) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(AppHelper.getColor(this, R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        setSuperScriptSpan();
        setTypeFaces();

        getversion();


    }


    private void gotomain()
    {
        new Handler().postDelayed(this::launchWelcomeActivity, SPLASH_TIME_OUT);
    }

    private void setSuperScriptSpan() {
        String s = "Rooyesh";
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new TopAlignSuperscriptSpan((float) 0.35), s.length() - 5, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        splashAppName.setText(spannableString);
    }


    private void setTypeFaces() {
        splashAppName.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        splashMessage.setTypeface(AppHelper.setTypeFace(this, "IranSans"));

    }

    public void launchWelcomeActivity() {

        Intent mainIntent = new Intent(this, Selectstatuse.class);
        if(PreferenceManager.isLogin(this))
        {
          //  mainIntent = new Intent(this, MainActivity.class);
        }


        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
        AnimationsUtil.setSlideInAnimation(this);

    }




    private void getversion() {


        //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
        mApiService = new APIService(this);

        APIAuthentication apiAuthentication = mApiService.RootService(APIAuthentication.class, EndPoints.BACKEND_BASE_URL);
        Call<VersionApp> VersionAppCall = apiAuthentication.checkVersion();
        new AuthService(SplashScreenActivity.this,mApiService).versionAppObservable().enqueue(new Callback<VersionApp>() {
            @Override
            public void onResponse(Call<VersionApp> call, Response<VersionApp> response) {
                if (response.isSuccessful()) {
                    if (response.body().getAppVersion() != null) {
                        appVersion = Integer.parseInt(response.body().getAppVersion());
                        if (appVersion > BuildConfig.VERSION_CODE) {
                            showUpdateDialog();
                        } else {
                            // gotomain();
                            gotomain();
                        }
                    } else {
                        AppHelper.Snackbar(SplashScreenActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        gotomain();
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionApp> call, Throwable t) {
                gotomain();
            }
        });
       /* VersionAppCall.enqueue(new Callback<VersionApp>() {
            @Override
            public void onResponse(Call<VersionApp> call, Response<VersionApp> response) {
                if (response.isSuccessful()) {
                    if (response.body().getAppVersion() != null) {
                        appVersion = Integer.parseInt(response.body().getAppVersion());
                        if (appVersion > BuildConfig.VERSION_CODE) {
                            showUpdateDialog();
                        } else {
                            // gotomain();
                            gotomain();
                        }
                    } else {
                        AppHelper.Snackbar(SplashScreenActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                }

            }
            @Override
            public void onFailure(Call<VersionApp> call, Throwable t) {
                    gotomain();
            }
        });*/


        //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm

      /*  RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://http://185.94.99.189/unichat/version.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //	mTextView.setText("Response is: "+ response.substring(0,500));
                        appVersion = Integer.parseInt(response);
                        if (appVersion > BuildConfig.VERSION_CODE) {
                            showUpdateDialog();
                        } else {
                            // gotomain();
                            gotomain();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //mTextView.setText("That didn't work!");
                // gotomain();
                gotomain();
            }
        });
// Add the request to the RequestQueue.8
        queue.add(stringRequest);

*/

      gotomain();

    }

    private void showUpdateDialog() {
        checkVersionDialog = new Dialog(this);
        checkVersionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkVersionDialog.setContentView(R.layout.dialog_checkversion);
        checkVersionDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        btnDownloadNow = (Button) checkVersionDialog.findViewById(R.id.btnDownloadNow);
        btnDownloadLater = (Button) checkVersionDialog.findViewById(R.id.btnDownloadLater);

        //btnDownloadNow.setOnClickListener(this);
        //	btnDownloadLater.setOnClickListener(this);
        pbDownload = (ProgressBar) checkVersionDialog.findViewById(R.id.pbDownload);
        plDownload = (LinearLayout) checkVersionDialog.findViewById(R.id.plDownload);
        tvDownloadPrc = (TextView) checkVersionDialog.findViewById(R.id.tvDownloadPrc);
        btnDownloadLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ActivitySplash.this,
//                        HomeActivity.class);
//                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//                startActivity(intent);
//                finish();
                checkVersionDialog.dismiss();
                gotomain();

            }
        });
        btnDownloadNow.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                // btnDownloadNow.setEnabled(false);
                if (isExternalStorageWritable()) {

                    if (ActivityCompat.checkSelfPermission(SplashScreenActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                14);
                    }
                    else
                    {

                        DownloadNewVersion d = new DownloadNewVersion();
                        btnDownloadNow.setClickable(false);
                        d.execute();
                    }

                } else {
                    Toast.makeText(SplashScreenActivity.this, "حافظه قابل نوشتن نیست", Toast.LENGTH_SHORT).show();
                }

//				DownloadNewVersion d = new DownloadNewVersion();
//				btnDownloadNow.setClickable(false);
//				d.execute();
            }
        });

        checkVersionDialog.show();
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    void OpenNewVersion(String location) {

//        File file = new File(location, "callaine.apk");
//
//        if (Build.VERSION.SDK_INT >= 22) {
//            Uri apkUri = FileProvider.getUriForFile(ActivitySplash.this, BuildConfig.APPLICATION_ID + ".provider", file);
//            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            intent.setData(apkUri);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);
//        }
//        else
//        {
//            Uri apkUri = Uri.fromFile(file);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        File toInstall = new File(location, "callaine" + ".apk");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(SplashScreenActivity.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent) ;
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", requestCode + " " + resultCode + " " + data.toString());
    }

    class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            checkVersionDialog.setCancelable(false);
            checkVersionDialog.setCanceledOnTouchOutside(false);
            plDownload.setVisibility(View.VISIBLE);
            pbDownload.setIndeterminate(true);
//            pbDownload.setVisibility(View.VISIBLE);

        }


        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            pbDownload.setIndeterminate(false);
            pbDownload.setMax(100);
            pbDownload.setProgress(progress[0]);
            String msg = "";
            if (progress[0] > 99) {

//                msg = "Finishing... ";
                msg = "100%";

            } else {

//                msg = "Downloading... " + progress[0] + "%";
                msg = progress[0] + "%";
            }
            tvDownloadPrc.setText(msg);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            checkVersionDialog.dismiss();

            if (result) {

                Toast.makeText(getApplicationContext(), "با موفقیت دانلود شد.",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(), "خطا دوباره امتحان کنید.",
                        Toast.LENGTH_SHORT).show();

            }

        }


        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;

            try
            {
                URL url = new URL("http://callaine.ir/callaine.apk");


                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();


//                String PATH = Environment.getExternalStorageDirectory() + "/Download/";
//                File file = new File(PATH);
//                file.mkdirs();

                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File outputFile = new File(file, "callaine.apk");

                if (outputFile.exists()) {
//                    OpenNewVersion(PATH);
                    outputFile.delete();
                }


                InputStream is = c.getInputStream();

//                int total_size = 1431692;//size of apk
//                int total_size = c.getContentLength();
                int total_size = 19000000;
                Log.d("DOWNLOAD", "size is : " + total_size);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded = 0;

                FileOutputStream fos = new FileOutputStream(outputFile);

                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = (int) (downloaded * 100 / total_size);
                    Log.d("DOWNLOAD", "precentage is :" + per + " len1 is : " + len1 + " downloaded is :" + downloaded);
                    publishProgress(per);
                }
                fos.close();
                is.close();
                OpenNewVersion(file.getPath());


                flag = true;
            } catch (Exception e) {
                Log.d("DOWNLOAD", "Update Error: " + e.getMessage());
                flag = false;
                //   delay();
            }
            // delay();
            return flag;


        }

    }





}