package com.setayeshco.rooyesh.activities.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.welcome.SplashScreenActivity;
import com.setayeshco.rooyesh.adapters.recyclerView.BackupAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.backup.Backup;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.models.BackupDriveModel;
import com.setayeshco.rooyesh.models.users.Pusher;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_GOOGLE_DRIVE;

/**
 * Created by Abderrahim El imame on 11/1/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class BackupActivity extends AppCompatActivity {

    @BindView(R.id.backup_msg)
    TextView backup_msg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backup_drive_button_backup)
    Button backupButton;


    @BindView(R.id.backup_drive_button_folder)
    LinearLayout selectFolderButton;

    @BindView(R.id.backupRecyclerView)
    RecyclerView backupRecyclerView;

    @BindView(R.id.backup_drive_textview_folder)
    TextView folderTextView;

    private BackupAdapter mBackupAdapter;

    private static final int REQUEST_CODE_PICKER = 2;
    private static final int REQUEST_CODE_PICKER_FOLDER = 4;

    private Backup backup;
    private GoogleApiClient mGoogleApiClient;
    private String backupFolder;
    private IntentSender intentPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        setupToolbar();
        setTypeFaces();
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.chat_backup));
        }

    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            backup_msg.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            folderTextView.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        }
    }


    public void init() {


        RooyeshApplication RooyeshApplication = (RooyeshApplication) getApplication();
        backup = RooyeshApplication.getBackup();
        backup.init(this);
        connectClient();
        mGoogleApiClient = backup.getClient();


        setDetails();
        backupButton.setOnClickListener(v -> {
            // Open Folder picker, then upload the file on Drive
            openFolderPicker(true);
        });

        selectFolderButton.setOnClickListener(v -> {
            // Check first if a folder is already selected
            if (!"".equals(backupFolder)) {
                //Start the picker to choose a folder
                //False because we don't want to upload the backup on drive then
                openFolderPicker(false);
            }
        });

    }

    private void setDetails() {
        // Show backup folder, if exists
        backupFolder = PreferenceManager.getBackupFolder(this);
        if (!("").equals(backupFolder)) {
            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            backupButton.setVisibility(View.VISIBLE);
            APIHelper.initialApiUsersContacts().userHasBackup(backupFolder).subscribe(backupModel -> {

            }, throwable -> {

            });
        }

        // Populate backup list
        if (!("").equals(backupFolder)) {
            getBackupsFromDrive(DriveId.decodeFromString(backupFolder).asDriveFolder());
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
            case EVENT_BUS_GOOGLE_DRIVE:
                setDetails();
                break;
        }
    }

    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    folderTextView.setText(metadata.getTitle());
                }
        );
    }

    private void openFolderPicker(boolean uploadToDrive) {
        if (uploadToDrive) {
            // First we check if a backup folder is set
            if (TextUtils.isEmpty(backupFolder)) {
                try {
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        if (intentPicker == null)
                            intentPicker = buildIntent();
                        //Start the picker to choose a folder
                        startIntentSenderForResult(
                                intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
                    }
                } catch (IntentSender.SendIntentException e) {
                    AppHelper.LogCat("Unable to send intent" + e.getMessage());
                    showErrorDialog();
                }
            } else {
                uploadToDrive(DriveId.decodeFromString(backupFolder));
            }
        } else {
            try {
                intentPicker = null;
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (intentPicker == null)
                        intentPicker = buildIntent();
                    //Start the picker to choose a folder
                    startIntentSenderForResult(
                            intentPicker, REQUEST_CODE_PICKER_FOLDER, null, 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                AppHelper.LogCat("Unable to send intent" + e.getMessage());
                showErrorDialog();
            }
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }

    private void getBackupsFromDrive(DriveFolder folder) {
        final Activity activity = this;
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, AppConstants.EXPORT_REALM_FILE_NAME))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<BackupDriveModel> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i = 0; i < size; i++) {
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            Date modifiedDate = metadata.getModifiedDate();
                            long backupSize = metadata.getFileSize();
                            backupsArray.add(new BackupDriveModel(driveId, modifiedDate, backupSize));
                        }
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        backupRecyclerView.setLayoutManager(mLinearLayoutManager);
                        mBackupAdapter = new BackupAdapter(activity, true);
                        backupRecyclerView.setAdapter(mBackupAdapter);
                        mBackupAdapter.setBackupDriveModelList(backupsArray);
                    }
                });
    }

    public void downloadFromDrive(DriveFile file) {
        AppHelper.showDialog(this, getString(R.string.please_wait_a_moment));
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(result -> {
                    AppHelper.hideDialog();
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }

                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    InputStream input = contents.getInputStream();

                    try {
                        OutputStream output = new FileOutputStream(new File(FilesManager.getImagesCachePath(this), AppConstants.EXPORT_REALM_FILE_NAME));
                        try {
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                safeCloseClosable(input);
                            }
                        } catch (Exception e) {
                            reportToFirebase(e, "Error downloading backup from drive");
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        reportToFirebase(e, "Error downloading backup from drive, file not found");
                        e.printStackTrace();
                    } finally {
                        safeCloseClosable(input);
                    }

                    Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_message_restart, Toast.LENGTH_LONG).show();

                    if (RealmBackupRestore.restore(this)) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setMessage(R.string.you_need_to_restart_the_application);
                        alert.setPositiveButton(R.string.ok, (dialog, which) -> {
                            Intent mStartActivity = new Intent(this, SplashScreenActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        });
                        alert.setCancelable(false);
                        alert.show();
                    }

                });
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            reportToFirebase(e, "Error downloading backup from drive, IO Exception");
            e.printStackTrace();
        }
    }

    private void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(result -> {
                        if (!result.getStatus().isSuccess()) {
                            AppHelper.LogCat("Error while trying to create new file contents");
                            showErrorDialog();
                            return;
                        }
                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                // write content to DriveContents
                                OutputStream outputStream = driveContents.getOutputStream();

                                FileInputStream inputStream = null;
                                try {
                                    inputStream = new FileInputStream(RealmBackupRestore.backup(BackupActivity.this));
                                } catch (FileNotFoundException e) {
                                    reportToFirebase(e, "Error uploading backup from drive, file not found");
                                    showErrorDialog();
                                    e.printStackTrace();
                                }

                                byte[] buf = new byte[1024];
                                int bytesRead;
                                try {
                                    if (inputStream != null) {
                                        while ((bytesRead = inputStream.read(buf)) > 0) {
                                            outputStream.write(buf, 0, bytesRead);
                                        }
                                    }
                                } catch (IOException e) {

                                    showErrorDialog();
                                    e.printStackTrace();
                                }


                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(AppConstants.EXPORT_REALM_FILE_NAME)
                                        .setMimeType("text/plain")
                                        .build();

                                // create a file in selected folder
                                folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                        .setResultCallback(result1 -> {
                                            if (!result1.getStatus().isSuccess()) {
                                                AppHelper.LogCat("Error while trying to create the file");
                                                showErrorDialog();
                                                return;
                                            }
                                            showSuccessDialog();
                                            setDetails();
                                        });
                            }
                        }.start();
                    });
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                AppHelper.LogCat("onActivityResult");
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());

                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case 4:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    // Restart activity to apply changes
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        PreferenceManager.saveBackupFolder(this, folderPath);
    }

    private void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_success, Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    private void reportToFirebase(Exception e, String message) {
        AppHelper.LogCat(e.getMessage() + " " + message);
    }

    public void connectClient() {
        backup.start();
    }

    public void disconnectClient() {
        backup.stop();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectClient();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                AnimationsUtil.setSlideOutAnimation(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationsUtil.setSlideOutAnimation(this);
    }
}
