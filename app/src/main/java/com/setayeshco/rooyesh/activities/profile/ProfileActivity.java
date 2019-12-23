package com.setayeshco.rooyesh.activities.profile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.PresentActivity;
import com.setayeshco.rooyesh.activities.groups.AddNewMembersToGroupActivity;
import com.setayeshco.rooyesh.activities.groups.EditGroupActivity;
import com.setayeshco.rooyesh.activities.media.MediaActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.adapters.recyclerView.groups.GroupMembersAdapter;
import com.setayeshco.rooyesh.adapters.recyclerView.media.MediaProfileAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.bottomSheets.BottomSheetEditGroupImage;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.call.CallManager;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.Roozh;
import com.setayeshco.rooyesh.models.groups.GroupResponse;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.status.StatusResponse;
import com.setayeshco.rooyesh.presenters.users.ProfilePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import info.hoang8f.widget.FButton;
import io.realm.Realm;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Abderrahim El imame on 27/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ProfileActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.cover)
    ImageView UserCover;
    @BindView(R.id.anim_toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout AppBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.containerProfile)
    CoordinatorLayout containerProfile;
    @BindView(R.id.created_title)
    EmojiconTextView mCreatedTitle;
    @BindView(R.id.group_container_title)
    LinearLayout GroupTitleContainer;
    @BindView(R.id.group_edit)
    ImageView EditGroupBtn;
    @BindView(R.id.statusPhoneContainer)
    CardView statusPhoneContainer;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.numberPhone)
    TextView numberPhone;
    @BindView(R.id.status_date)
    TextView status_date;
    @BindView(R.id.send_message)
    ImageView sendMessageBtn;
    @BindView(R.id.call)
    ImageView callBtn;
    @BindView(R.id.MembersList)
    RecyclerView MembersList;
    @BindView(R.id.participantContainer)
    CardView participantContainer;
    @BindView(R.id.participantContainerExit)
    CardView participantContainerExit;
    @BindView(R.id.participantContainerDelete)
    CardView participantContainerDelete;
    @BindView(R.id.participantCounter)
    TextView participantCounter;
    @BindView(R.id.add_contact_participate)
    LinearLayout addNewParticipant;
    @BindView(R.id.media_counter)
    TextView mediaCounter;
    @BindView(R.id.media_section)
    CardView mediaSection;
    @BindView(R.id.btnTaeed)
    FButton btnTaeed;
    @BindView(R.id.btnShowPresentList)
    FButton btnShowPresentList;

    @BindView(R.id.mediaProfileList)
    RecyclerView mediaList;


    private MediaProfileAdapter mMediaProfileAdapter;
    private GroupMembersAdapter mGroupMembersAdapter;
    private ContactsModel mContactsModel;
    private GroupsModel mGroupsModel;
    public int userID;
    public int groupID;
    private boolean isGroup;
    private int mutedColor;
    private int mutedColorStatusBar;
    int numberOfColors = 24;
    private ProfilePresenter mProfilePresenter;
    private boolean left;
    private boolean isAnAdmin;
    private boolean isAnProf = false;
    private APIService mApiService;
    private String PicturePath;
    private MemoryCache memoryCache;
    private Intent mIntent;
    private Socket mSocket;
    List<MembersGroupModel> myMembersGroupModels;
    ArrayList<Integer> absentUsers = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        isAnProf = PreferenceManager.isTeacher(ProfileActivity.this);

        ButterKnife.bind(this);
        initializerView();
        connectToChatServer();
        setTypeFaces();

        memoryCache = new MemoryCache();
        if (getIntent().hasExtra("userID")) {
            isGroup = getIntent().getExtras().getBoolean("isGroup");
            userID = getIntent().getExtras().getInt("userID");
        }


        if (getIntent().hasExtra("groupID")) {
            isGroup = getIntent().getExtras().getBoolean("isGroup");
            groupID = getIntent().getExtras().getInt("groupID");
        }
        mProfilePresenter = new ProfilePresenter(this);
        mProfilePresenter.onCreate();

        if (userID == PreferenceManager.getID(this)) {
            sendMessageBtn.setVisibility(View.GONE);
            callBtn.setVisibility(View.GONE);
        } else {
            sendMessageBtn.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.VISIBLE);
        }
        addNewParticipant.setOnClickListener(v -> {
            Intent mIntent = new Intent(this, AddNewMembersToGroupActivity.class);
            mIntent.putExtra("groupID", groupID);
            mIntent.putExtra("profileAdd", "add");
            startActivity(mIntent);
        });
        participantContainerExit.setOnClickListener(v -> {

            String name = UtilsString.unescapeJava(mGroupsModel.getGroupName());
            if (name.length() > 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.exit_group) + name.substring(0, 10) + "... " + "" + getString(R.string.group_ex))
                        .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                            AppHelper.showDialog(this, getString(R.string.exiting_group_dialog));
                            mProfilePresenter.ExitGroup();
                        }).setNegativeButton(getString(R.string.cancel), null).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.exit_group) + name + "" + getString(R.string.group_ex))
                        .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                            AppHelper.showDialog(this, getString(R.string.exiting_group_dialog));
                            mProfilePresenter.ExitGroup();
                        }).setNegativeButton(getString(R.string.cancel), null).show();
            }


        });

        participantContainerDelete.setOnClickListener(v -> {
            String name = UtilsString.unescapeJava(mGroupsModel.getGroupName());
            if (name.length() > 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete) + name.substring(0, 10) + "... " + "" + getString(R.string.group_ex))
                        .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                            AppHelper.showDialog(this, getString(R.string.deleting_group_dialog));
                            mProfilePresenter.DeleteGroup();
                        }).setNegativeButton(getString(R.string.cancel), null).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete) + name + "" + getString(R.string.group_ex))
                        .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                            AppHelper.showDialog(this, getString(R.string.deleting_group_dialog));
                            mProfilePresenter.DeleteGroup();
                        }).setNegativeButton(getString(R.string.cancel), null).show();
            }
        });
        callBtn.setOnClickListener(view -> makeCall());
        sendMessageBtn.setOnClickListener(view -> sendMessage(mContactsModel));



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(new Date());
        SimpleDateFormat m_year1 = new SimpleDateFormat("yyyy");
        int m_year = Integer.parseInt(m_year1.format(new Date()));
        SimpleDateFormat m_Month1 = new SimpleDateFormat("MM");
        int m_Month = Integer.parseInt(m_Month1.format(new Date()));
        SimpleDateFormat m_day1 = new SimpleDateFormat("dd");
        int m_day = Integer.parseInt(m_day1.format(new Date()));
        String currentDateandTime = sdf.format(new Date());

        Roozh jCal = new Roozh();
        jCal.GregorianToPersian(m_year, m_Month, m_day);


        String date = jCal.toString() + "" ;

        GroupMembersAdapter.presenetUsers.clear();
        if (myMembersGroupModels != null) {
            for (MembersGroupModel membersGroupModel1 : myMembersGroupModels) {
                GroupMembersAdapter.presenetUsers.add(membersGroupModel1.getUserId());
            }
        }


        btnTaeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                absentUsers.clear();
                for (MembersGroupModel membersGroupModel1 : myMembersGroupModels) {
                    absentUsers.add(membersGroupModel1.getUserId())  ;
                }
                for (int i=0; i <  GroupMembersAdapter.presenetUsers.size() ; i++) {

                    if (absentUsers.contains(GroupMembersAdapter.presenetUsers.get(i))){
                        absentUsers.remove(absentUsers.indexOf(GroupMembersAdapter.presenetUsers.get(i)));
                    }

                }
                for (int i = 0 ; i<GroupMembersAdapter.presenetUsers.size();i++) {
                    Log.d("list", "Present User Id : " + GroupMembersAdapter.presenetUsers.get(i));
                }
                for (int i = 0 ; i<absentUsers.size();i++) {
                    Log.d("list", "Absent User Id : " + absentUsers.get(i));
                }

              /*  if (GroupMembersAdapter.presenetUsers.size() == 0){
                    for (MembersGroupModel membersGroupModel1 : myMembersGroupModels) {
                        GroupMembersAdapter.presenetUsers.add(membersGroupModel1.getUserId())  ;
                    }
                }*/
              ArrayList<Integer> temp=new ArrayList<>();

              for(int i=0;i<absentUsers.size();i++)
              {
                  temp.add( Integer.parseInt( absentUsers.get(i).toString().replace(" ","")));
              }

                ArrayList<Integer> temp2=new ArrayList<>();

                for(int i=0;i<GroupMembersAdapter.presenetUsers.size();i++)
                {
                    temp2.add( Integer.parseInt( GroupMembersAdapter.presenetUsers.get(i).toString().replace(" ","")));
                }
                IsPresnt(groupID,temp2,temp,date);
                Log.i("pressent",temp2+"");
                Log.i("pressent",temp+"");
                Log.i("date11",date+"   ****    groupID  " + groupID);

            }
        });



        btnShowPresentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this,PresentActivity.class);
                intent.putExtra("groupID",groupID);
                startActivity(intent);

            }
        });










    }


    //kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk


    void IsPresnt(int groupID, ArrayList<Integer> presentId , ArrayList<Integer> absentId , String date ){
        APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
        Call<GroupResponse> PresentCall = mApiGroups.isPresent(groupID,presentId,absentId,date);
        Log.d("ARRAY",presentId.toString());
        PresentCall.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        AppHelper.Snackbar(ProfileActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                        //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                    } else {
                        AppHelper.Snackbar(ProfileActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                    }
                } else {
                    AppHelper.Snackbar(ProfileActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                AppHelper.Snackbar(ProfileActivity.this, findViewById(R.id.containerProfile), getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

            }
        });
    }




    //kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk

    /**
     * method to connect to the chat sever by socket
     */
    private void connectToChatServer() {

        RooyeshApplication app = (RooyeshApplication) getApplication();
        mSocket = app.getSocket();

        if (mSocket == null) {
            RooyeshApplication.connectSocket();
            mSocket = app.getSocket();
        }
        if (!mSocket.connected())
            mSocket.connect();


    }

    private void setTypeFaces() {
        if (AppConstants.ENABLE_FONTS_TYPES) {
            mCreatedTitle.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            status.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            numberPhone.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            status_date.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            participantCounter.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            mediaCounter.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            btnTaeed.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
            btnShowPresentList.setTypeface(AppHelper.setTypeFace(this, "IranSans"));

        }
    }

    private void makeCall() {
        AlertDialog myDialog;
        String[] items = {"Voice", "Video"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, which) -> {
            if (items.length > 0) {
                if (items[which].equals("Voice")) {
                    CallManager.callContact(ProfileActivity.this, true, false, userID);
                } else if (items[which].equals("Video")) {
                    CallManager.callContact(ProfileActivity.this, true, true, userID);
                }
            }
        });

        builder.setCancelable(true);
        myDialog = builder.create();
        myDialog.show();
    }

    public void checkIfIsAnAdmin() {
        if (isGroup) {
            if (isAnProf) {
                if (isAnAdmin) {
                    AppHelper.LogCat("Admin left " + left);
                    if (left) {

                            participantContainerExit.setVisibility(View.GONE);
                            participantContainerDelete.setVisibility(View.VISIBLE);
                            addNewParticipant.setVisibility(View.GONE);

                    } else {

                            participantContainerExit.setVisibility(View.VISIBLE);
                            participantContainerDelete.setVisibility(View.GONE);
                            addNewParticipant.setVisibility(View.VISIBLE);

                    }
                } else {
                    AppHelper.LogCat("Creator left" + left);
                    if (left) {

                            participantContainerExit.setVisibility(View.GONE);
                            participantContainerDelete.setVisibility(View.VISIBLE);
                            addNewParticipant.setVisibility(View.GONE);

                    } else {

                            participantContainerExit.setVisibility(View.VISIBLE);
                            participantContainerDelete.setVisibility(View.GONE);
                            addNewParticipant.setVisibility(View.GONE);

                    }
                }
            }


        } else {
            if (isAnProf && isAnAdmin) {
                participantContainerExit.setVisibility(View.GONE);
                participantContainerDelete.setVisibility(View.GONE);
                participantContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * method to initialize group members view
     */
    private void initializerGroupMembersView() {
        mApiService = new APIService(this);
        if (isAnProf && isAnAdmin) {
            participantContainer.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGroupMembersAdapter = new GroupMembersAdapter(this, mApiService, isAnAdmin);
        MembersList.setLayoutManager(mLinearLayoutManager);
        MembersList.setAdapter(mGroupMembersAdapter);
        checkIfIsAnAdmin();

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mediaList.setLayoutManager(linearLayoutManager);
        mMediaProfileAdapter = new MediaProfileAdapter(this);
        mediaList.setAdapter(mMediaProfileAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isGroup) {
            if (isAnAdmin) {
                getMenuInflater().inflate(R.menu.profile_menu_group_add, menu);
            } else {
                if (!left)
                    getMenuInflater().inflate(R.menu.profile_menu_group, menu);
            }
        } else {
            if (mContactsModel != null)
                if (UtilsPhone.checkIfContactExist(this, mContactsModel.getPhone())) {
                    if (userID == PreferenceManager.getID(this)) {
                        getMenuInflater().inflate(R.menu.profile_menu_mine, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.profile_menu, menu);
                    }
                } else if (userID == PreferenceManager.getID(this)) {
                    getMenuInflater().inflate(R.menu.profile_menu_mine, menu);
                } else {
                    getMenuInflater().inflate(R.menu.profile_menu_user_not_exist, menu);
                }

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_BACK_ACTIVITY));
            if (AppHelper.isAndroid5()) {
                ActivityCompat.finishAfterTransition(ProfileActivity.this);
            } else {
                finish();
                AnimationsUtil.setSlideOutAnimation(this);
            }

        } else if (item.getItemId() == R.id.add_contact) {
            Intent mIntent = new Intent(this, AddNewMembersToGroupActivity.class);
            mIntent.putExtra("groupID", groupID);
            mIntent.putExtra("profileAdd", "add");
            startActivity(mIntent);
        } else if (item.getItemId() == R.id.share) {
            shareContact(mContactsModel);
        } else if (item.getItemId() == R.id.edit_contact) {
            editContact(mContactsModel);
        } else if (item.getItemId() == R.id.view_contact) {
            viewContact(mContactsModel);
        } else if (item.getItemId() == R.id.add_new_contact) {
            addNewContact();
        } else if (item.getItemId() == R.id.edit_group_name) {
            //,,,,,,,,,,,,,,,,,,,,,,,,,hhhh.....

            if (isAnProf && isAnAdmin) {
                launchEditGroupName();
            }

            //,,,,,,,,,,,,,,,,,,,,,,,,,hhhh.....
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.media_selection)
    public void launchMediaActivity() {

        if (isGroup) {
            mIntent = new Intent(this, MediaActivity.class);
            mIntent.putExtra("groupID", groupID);
            mIntent.putExtra("isGroup", true);
            mIntent.putExtra("Username", mGroupsModel.getGroupName());
            startActivity(mIntent);
            AnimationsUtil.setSlideInAnimation(this);

        } else {
            String finalName;
            String name = UtilsPhone.getContactName(this, mContactsModel.getPhone());
            if (name != null) {
                finalName = name;
            } else {
                finalName = mContactsModel.getPhone();
            }
            mIntent = new Intent(this, MediaActivity.class);
            mIntent.putExtra("userID", userID);
            mIntent.putExtra("isGroup", false);
            mIntent.putExtra("Username", finalName);
            startActivity(mIntent);
            AnimationsUtil.setSlideInAnimation(this);

        }
    }

    private void addNewContact() {
        try {
            Intent mIntent = new Intent(Intent.ACTION_INSERT);
            mIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            mIntent.putExtra(ContactsContract.Intents.Insert.PHONE, mContactsModel.getPhone());
            startActivityForResult(mIntent, AppConstants.SELECT_ADD_NEW_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void launchEditGroupName() {
        Intent mIntent = new Intent(this, EditGroupActivity.class);
        mIntent.putExtra("currentGroupName", mGroupsModel.getGroupName());
        mIntent.putExtra("groupID", mGroupsModel.getId());
        startActivity(mIntent);
    }

    public void ShowContact(ContactsModel contactsModel) {
        mContactsModel = contactsModel;
        try {
            updateUI(null, mContactsModel);
        } catch (Exception e) {
            AppHelper.LogCat("Error ContactsModel in profile UI Exception " + e.getMessage());
        }
    }

    public void ShowMedia(List<MessagesModel> messagesModel) {
        if (messagesModel.size() != 0) {
            mediaSection.setVisibility(View.VISIBLE);
            mediaCounter.setText(String.valueOf(messagesModel.size()));
            mMediaProfileAdapter.setMessages(messagesModel);

        } else {
            mediaSection.setVisibility(View.GONE);
        }

    }

    public void ShowGroup(GroupsModel groupsModel) {
        mGroupsModel = groupsModel;
        try {
            updateUI(mGroupsModel, null);
        } catch (Exception e) {
            AppHelper.LogCat("Error GroupsModel in profile UI Exception " + e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateUI(GroupsModel mGroupsModel, ContactsModel mContactsModel) {


        if (isGroup) {
            GroupTitleContainer.setVisibility(View.VISIBLE);
            DateTime messageDate = UtilsTime.getCorrectDate(mGroupsModel.getCreatedDate());
            String groupDate = UtilsTime.convertDateToString(this, messageDate);
            if (mGroupsModel.getCreatorID() == PreferenceManager.getID(this)) {
                mCreatedTitle.setText(String.format(getString(R.string.created_by_you_at) + " %s", groupDate));
            } else {
                String name = UtilsPhone.getContactName(this, mGroupsModel.getCreator());
                if (name != null) {
                    mCreatedTitle.setText(String.format(getString(R.string.created_by) + " %s " + getString(R.string.group_at) + " %s ", name, groupDate));
                } else {
                    mCreatedTitle.setText(String.format(getString(R.string.created_by) + " %s " + getString(R.string.group_at) + " %s ", mGroupsModel.getCreator(), groupDate));
                }
            }
            String name = UtilsString.unescapeJava(mGroupsModel.getGroupName());
            /*if (name.length() > 10)
                collapsingToolbar.setTitle(name.substring(0, 10) + "... " + "");
            else*/
            collapsingToolbar.setTitle(name);


            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mGroupsModel.getGroupImage(), this, mGroupsModel.getId(), AppConstants.GROUP, AppConstants.FULL_PROFILE);
            if (bitmap != null) {
                AnimationsUtil.expandToolbar(containerProfile, bitmap, AppBarLayout);
                UserCover.setImageBitmap(bitmap);
                Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                    Palette.Swatch swatchColor = palette.getVibrantSwatch();
                    Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                    if (swatchColor != null && swatchColorDark != null) {
                        try {
                            mutedColor = swatchColor.getRgb();
                            mutedColorStatusBar = swatchColorDark.getRgb();
                            collapsingToolbar.setContentScrimColor(mutedColor);
                            if (AppHelper.isAndroid5()) {
                                getWindow().setStatusBarColor(mutedColorStatusBar);
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat(" " + e.getMessage());
                        }
                    } else {
                        List<Palette.Swatch> swatches = palette.getSwatches();
                        for (Palette.Swatch swatch : swatches) {
                            if (swatch != null) {
                                mutedColor = swatch.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                float[] hsv = new float[3];
                                Color.colorToHSV(mutedColor, hsv);
                                hsv[2] *= 0.8f; // value component
                                mutedColorStatusBar = Color.HSVToColor(hsv);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                                break;
                            }
                        }

                    }
                });
            } else {
                Bitmap holderBitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mGroupsModel.getGroupImage(), this, mGroupsModel.getId(), AppConstants.GROUP, AppConstants.ROW_PROFILE);
                Drawable drawable;
                if (holderBitmap != null)
                    drawable = new BitmapDrawable(getResources(), holderBitmap);
                else
                    drawable = AppHelper.getDrawable(this, R.drawable.image_holder_gp);

                BitmapImageViewTarget target = new BitmapImageViewTarget(UserCover) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        AnimationsUtil.expandToolbar(containerProfile, bitmap, AppBarLayout);
                        UserCover.setImageBitmap(bitmap);
                        Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                            Palette.Swatch swatchColor = palette.getVibrantSwatch();
                            Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                            if (swatchColor != null && swatchColorDark != null) {
                                try {
                                    mutedColor = swatchColor.getRgb();
                                    mutedColorStatusBar = swatchColorDark.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                } catch (Exception e) {
                                    AppHelper.LogCat(" " + e.getMessage());
                                }
                            } else {
                                List<Palette.Swatch> swatches = palette.getSwatches();
                                for (Palette.Swatch swatch : swatches) {
                                    if (swatch != null) {
                                        mutedColor = swatch.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        float[] hsv = new float[3];
                                        Color.colorToHSV(mutedColor, hsv);
                                        hsv[2] *= 0.8f; // value component
                                        mutedColorStatusBar = Color.HSVToColor(hsv);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                        break;
                                    }
                                }

                            }
                        });
                        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + mGroupsModel.getGroupImage(), mGroupsModel.getGroupImage(), ProfileActivity.this, mGroupsModel.getId(), AppConstants.GROUP, AppConstants.FULL_PROFILE);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (holderBitmap != null) {
                            AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                            UserCover.setImageBitmap(holderBitmap);
                            Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                                Palette.Swatch swatchColor = palette.getVibrantSwatch();
                                Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                                if (swatchColor != null && swatchColorDark != null) {
                                    try {
                                        mutedColor = swatchColor.getRgb();
                                        mutedColorStatusBar = swatchColorDark.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                    } catch (Exception ex) {
                                        AppHelper.LogCat(" " + e.getMessage());
                                    }
                                } else {
                                    List<Palette.Swatch> swatches = palette.getSwatches();
                                    for (Palette.Swatch swatch : swatches) {
                                        if (swatch != null) {
                                            mutedColor = swatch.getRgb();
                                            collapsingToolbar.setContentScrimColor(mutedColor);
                                            float[] hsv = new float[3];
                                            Color.colorToHSV(mutedColor, hsv);
                                            hsv[2] *= 0.8f; // value component
                                            mutedColorStatusBar = Color.HSVToColor(hsv);
                                            if (AppHelper.isAndroid5()) {
                                                getWindow().setStatusBarColor(mutedColorStatusBar);
                                            }
                                            break;
                                        }
                                    }

                                }
                            });
                        } else {
                            UserCover.setImageDrawable(drawable);
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (holderBitmap != null) {
                            AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                            UserCover.setImageBitmap(holderBitmap);
                            Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                                Palette.Swatch swatchColor = palette.getVibrantSwatch();
                                Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                                if (swatchColor != null && swatchColorDark != null) {
                                    try {
                                        mutedColor = swatchColor.getRgb();
                                        mutedColorStatusBar = swatchColorDark.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                    } catch (Exception e) {
                                        AppHelper.LogCat(" " + e.getMessage());
                                    }
                                } else {
                                    List<Palette.Swatch> swatches = palette.getSwatches();
                                    for (Palette.Swatch swatch : swatches) {
                                        if (swatch != null) {
                                            mutedColor = swatch.getRgb();
                                            collapsingToolbar.setContentScrimColor(mutedColor);
                                            float[] hsv = new float[3];
                                            Color.colorToHSV(mutedColor, hsv);
                                            hsv[2] *= 0.8f; // value component
                                            mutedColorStatusBar = Color.HSVToColor(hsv);
                                            if (AppHelper.isAndroid5()) {
                                                getWindow().setStatusBarColor(mutedColorStatusBar);
                                            }
                                            break;
                                        }
                                    }

                                }
                            });
                        } else {
                            UserCover.setImageDrawable(drawable);
                        }
                    }
                };
                RooyeshImageLoader.loadSimpleImageGroup(this, EndPoints.PROFILE_IMAGE_URL + mGroupsModel.getGroupImage(), target, drawable, AppConstants.PROFILE_IMAGE_SIZE);
            }

            UserCover.setOnClickListener(view -> {
                //......................hhhh

                if (isAnProf && isAnAdmin) {
                    BottomSheetEditGroupImage bottomSheetEditGroupImage = new BottomSheetEditGroupImage();
                    bottomSheetEditGroupImage.show(getSupportFragmentManager(), bottomSheetEditGroupImage.getTag());
                }

                //......................hhhh
            });
        } else {

            String name = UtilsPhone.getContactName(this, mContactsModel.getPhone());
            if (name != null) {
                collapsingToolbar.setTitle(name);
            } else {
                collapsingToolbar.setTitle(mContactsModel.getPhone());
            }

            statusPhoneContainer.setVisibility(View.VISIBLE);
            String Status = UtilsString.unescapeJava(mContactsModel.getStatus());
           /* if (Status.length() > 18)
                status.setText(Status.substring(0, 18) + "... " + "");
            else*/
            status.setText(Status);
            numberPhone.setText(mContactsModel.getPhone());
            status_date.setText(mContactsModel.getStatus_date());

            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.FULL_PROFILE);
            if (bitmap != null) {
                AnimationsUtil.expandToolbar(containerProfile, bitmap, AppBarLayout);
                UserCover.setImageBitmap(bitmap);
                Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                    Palette.Swatch swatchColor = palette.getVibrantSwatch();
                    Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                    if (swatchColor != null && swatchColorDark != null) {
                        try {
                            mutedColor = swatchColor.getRgb();
                            mutedColorStatusBar = swatchColorDark.getRgb();
                            collapsingToolbar.setContentScrimColor(mutedColor);
                            if (AppHelper.isAndroid5()) {
                                getWindow().setStatusBarColor(mutedColorStatusBar);
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat(" " + e.getMessage());
                        }
                    } else {
                        List<Palette.Swatch> swatches = palette.getSwatches();
                        for (Palette.Swatch swatch : swatches) {
                            if (swatch != null) {
                                mutedColor = swatch.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                float[] hsv = new float[3];
                                Color.colorToHSV(mutedColor, hsv);
                                hsv[2] *= 0.8f; // value component
                                mutedColorStatusBar = Color.HSVToColor(hsv);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                                break;
                            }
                        }

                    }
                });
            } else {
                Bitmap holderBitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.ROW_PROFILE);
                Drawable drawable;
                if (holderBitmap != null)
                    drawable = new BitmapDrawable(getResources(), holderBitmap);
                else
                    drawable = AppHelper.getDrawable(this, R.drawable.image_holder_up);

                BitmapImageViewTarget target = new BitmapImageViewTarget(UserCover) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        AnimationsUtil.expandToolbar(containerProfile, bitmap, AppBarLayout);
                        UserCover.setImageBitmap(bitmap);
                        Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                            Palette.Swatch swatchColor = palette.getVibrantSwatch();
                            Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                            if (swatchColor != null && swatchColorDark != null) {
                                try {
                                    mutedColor = swatchColor.getRgb();
                                    mutedColorStatusBar = swatchColorDark.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                } catch (Exception e) {
                                    AppHelper.LogCat(" " + e.getMessage());
                                }
                            } else {
                                List<Palette.Swatch> swatches = palette.getSwatches();
                                for (Palette.Swatch swatch : swatches) {
                                    if (swatch != null) {
                                        mutedColor = swatch.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        float[] hsv = new float[3];
                                        Color.colorToHSV(mutedColor, hsv);
                                        hsv[2] *= 0.8f; // value component
                                        mutedColorStatusBar = Color.HSVToColor(hsv);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                        break;
                                    }
                                }

                            }
                        });
                        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), ProfileActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.FULL_PROFILE);


                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (holderBitmap != null) {
                            AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                            UserCover.setImageBitmap(holderBitmap);
                            Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                                Palette.Swatch swatchColor = palette.getVibrantSwatch();
                                Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                                if (swatchColor != null && swatchColorDark != null) {
                                    try {
                                        mutedColor = swatchColor.getRgb();
                                        mutedColorStatusBar = swatchColorDark.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                    } catch (Exception ex) {
                                        AppHelper.LogCat(" " + e.getMessage());
                                    }
                                } else {
                                    List<Palette.Swatch> swatches = palette.getSwatches();
                                    for (Palette.Swatch swatch : swatches) {
                                        if (swatch != null) {
                                            mutedColor = swatch.getRgb();
                                            collapsingToolbar.setContentScrimColor(mutedColor);
                                            float[] hsv = new float[3];
                                            Color.colorToHSV(mutedColor, hsv);
                                            hsv[2] *= 0.8f; // value component
                                            mutedColorStatusBar = Color.HSVToColor(hsv);
                                            if (AppHelper.isAndroid5()) {
                                                getWindow().setStatusBarColor(mutedColorStatusBar);
                                            }
                                            break;
                                        }
                                    }

                                }
                            });
                        } else {
                            UserCover.setImageDrawable(drawable);
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (holderBitmap != null) {
                            AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                            UserCover.setImageBitmap(holderBitmap);
                            Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                                Palette.Swatch swatchColor = palette.getVibrantSwatch();
                                Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                                if (swatchColor != null && swatchColorDark != null) {
                                    try {
                                        mutedColor = swatchColor.getRgb();
                                        mutedColorStatusBar = swatchColorDark.getRgb();
                                        collapsingToolbar.setContentScrimColor(mutedColor);
                                        if (AppHelper.isAndroid5()) {
                                            getWindow().setStatusBarColor(mutedColorStatusBar);
                                        }
                                    } catch (Exception e) {
                                        AppHelper.LogCat(" " + e.getMessage());
                                    }
                                } else {
                                    List<Palette.Swatch> swatches = palette.getSwatches();
                                    for (Palette.Swatch swatch : swatches) {
                                        if (swatch != null) {
                                            mutedColor = swatch.getRgb();
                                            collapsingToolbar.setContentScrimColor(mutedColor);
                                            float[] hsv = new float[3];
                                            Color.colorToHSV(mutedColor, hsv);
                                            hsv[2] *= 0.8f; // value component
                                            mutedColorStatusBar = Color.HSVToColor(hsv);
                                            if (AppHelper.isAndroid5()) {
                                                getWindow().setStatusBarColor(mutedColorStatusBar);
                                            }
                                            break;
                                        }
                                    }

                                }
                            });
                        } else {
                            UserCover.setImageDrawable(drawable);
                        }
                    }
                };

                RooyeshImageLoader.loadSimpleImage(this, EndPoints.PROFILE_IMAGE_URL + mContactsModel.getImage(), target, drawable, AppConstants.PROFILE_IMAGE_SIZE);
            }
            if (mContactsModel.getImage() != null) {
                if (FilesManager.isFilePhotoProfileExists(this, FilesManager.getProfileImage(mContactsModel.getImage()))) {
                    UserCover.setOnClickListener(view -> AppHelper.LaunchImagePreviewActivity(this, AppConstants.PROFILE_IMAGE, mContactsModel.getImage()));
                } else {
                    UserCover.setOnClickListener(view -> AppHelper.LaunchImagePreviewActivity(ProfileActivity.this, AppConstants.PROFILE_IMAGE_FROM_SERVER, mContactsModel.getImage()));
                }
            }


        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfilePresenter.onDestroy();
    }


    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Profile throwable " + throwable.getMessage());
    }

    public void onErrorDeleting() {
        AppHelper.Snackbar(this, containerProfile, getString(R.string.failed_to_delete_this_group_check_connection), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

    }

    public void onErrorExiting() {
        AppHelper.Snackbar(this, containerProfile, getString(R.string.failed_to_exit_this_group_check_connection), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

    }

    /**
     * method to show group members list
     *
     * @param membersGroupModels this is parameter for ShowGroupMembers  method
     */
    public void ShowGroupMembers(List<MembersGroupModel> membersGroupModels) {

        if (membersGroupModels.size() != 0) {
            for (MembersGroupModel membersGroupModel : membersGroupModels) {
                if (membersGroupModel.getUserId() == PreferenceManager.getID(this)) {
                    left = membersGroupModel.isLeft();
                    isAnAdmin = membersGroupModel.isAdmin();
                    break;
                }
            }
            initializerGroupMembersView();
            mGroupMembersAdapter.setContacts(membersGroupModels);
            myMembersGroupModels=membersGroupModels;
            participantCounter.setText(String.valueOf(membersGroupModels.size()));
        } else {
            if (isAnProf && isAnAdmin) {
                participantContainerExit.setVisibility(View.GONE);
                participantContainer.setVisibility(View.GONE);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {
            if (PermissionHandler.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read storage data permission already granted.");
                switch (requestCode) {
                    case AppConstants.SELECT_ADD_NEW_CONTACT:
                        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_CONTACT_ADDED));
                        break;
                    case AppConstants.SELECT_PROFILE_PICTURE:
                        imagePath = FilesManager.getPath(this, data.getData());
                        break;
                    case AppConstants.SELECT_PROFILE_CAMERA:
                        if (data.getData() != null) {
                            imagePath = FilesManager.getPath(this, data.getData());
                        } else {
                            try {
                                String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                        .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                        .ImageColumns.MIME_TYPE};
                                final Cursor cursor = this.getContentResolver()
                                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
                                                .DATE_TAKEN + " DESC");

                                if (cursor != null && cursor.moveToFirst()) {
                                    String imageLocation = cursor.getString(1);
                                    cursor.close();
                                    File imageFile = new File(imageLocation);
                                    if (imageFile.exists()) {
                                        imagePath = imageFile.getPath();
                                    }
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat("error" + e);
                            }
                        }
                        break;
                }


                if (imagePath != null) {
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_PATH_GROUP, imagePath));
                } else {
                    AppHelper.LogCat("imagePath is null");
                }
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                PermissionHandler.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Pusher pusher) {

        switch (pusher.getAction()) {
            case AppConstants.EVENT_BUS_EXIT_GROUP:
                if (isAnProf && isAnAdmin) {
                    participantContainerExit.setVisibility(View.GONE);
                    participantContainerDelete.setVisibility(View.VISIBLE);
                    AppHelper.Snackbar(this, containerProfile, pusher.getData(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                }
                break;
            case AppConstants.EVENT_BUS_DELETE_GROUP:
                AppHelper.Snackbar(this, containerProfile, pusher.getData(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                new Handler().postDelayed(this::finish, 500);
                break;
            case AppConstants.EVENT_BUS_PATH_GROUP:
                PicturePath = pusher.getData();
                try {
                    new UploadFileToServer().execute();
                } catch (Exception e) {
                    AppHelper.LogCat(e);
                    AppHelper.CustomToast(this, getString(R.string.oops_something));
                }
                break;
            case AppConstants.EVENT_BUS_ADD_MEMBER:
                new Handler().postDelayed(() -> mProfilePresenter.updateUIGroupData(pusher.getGroupID()), 500);
                break;
            case AppConstants.EVENT_BUS_EXIT_THIS_GROUP:
            case AppConstants.EVENT_BUS_UPDATE_GROUP_NAME:
                new Handler().postDelayed(() -> mProfilePresenter.updateUIGroupData(pusher.getGroupID()), 500);
                break;

        }


    }


    private void editContact(ContactsModel mContactsModel) {
        if (userID == PreferenceManager.getID(this)) {
            AppHelper.LaunchActivity(this, EditProfileActivity.class);
        } else {
            long ContactID = UtilsPhone.getContactID(this, mContactsModel.getPhone());
            try {
                if (ContactID != 0) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, ContactID));
                    startActivity(intent);
                }
            } catch (Exception e) {
                AppHelper.LogCat("error edit contact " + e.getMessage());
            }
        }
    }

    private void viewContact(ContactsModel mContactsModel) {
        long ContactID = UtilsPhone.getContactID(this, mContactsModel.getPhone());
        try {
            if (ContactID != 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, ContactID));
                startActivity(intent);
            }
        } catch (Exception e) {
            AppHelper.LogCat("error view contact " + e.getMessage());
        }
    }

    private void sendMessage(ContactsModel mContactsModel) {


        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


        Bundle bundle = new Bundle();
        bundle.putInt("conversationID", 0);
        bundle.putInt("recipientID", mContactsModel.getId());
        bundle.putBoolean("isGroup", false);
        MessagesFragment messageFragmentOk = new MessagesFragment();
        messageFragmentOk.setArguments(bundle);


        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



        Intent messagingIntent = new Intent(this, MessagesActivity.class);
        messagingIntent.putExtra("conversationID", 0);
        messagingIntent.putExtra("recipientID", mContactsModel.getId());
        messagingIntent.putExtra("isGroup", false);
        startActivity(messagingIntent);
    }


    private void shareContact(ContactsModel mContactsModel) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        String subject = null;
        if (mContactsModel.getUsername() != null) {
            subject = mContactsModel.getUsername();
        }
        if (mContactsModel.getPhone() != null) {
            if (subject != null) {
                subject = subject + " " + mContactsModel.getPhone();
            } else {
                subject = mContactsModel.getPhone();
            }
        }
        if (subject != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, subject);
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareContact)));
    }


    private void setImage(String ImageUrl) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId", groupID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket != null)
            mSocket.emit(AppConstants.SOCKET_IMAGE_GROUP_UPDATED, jsonObject);

        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + ImageUrl, ImageUrl, ProfileActivity.this, groupID, AppConstants.GROUP, AppConstants.FULL_PROFILE);
        Bitmap holderBitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mGroupsModel.getGroupImage(), this, mGroupsModel.getId(), AppConstants.GROUP, AppConstants.FULL_PROFILE);
        if (holderBitmap != null) {
            Drawable drawable;
            drawable = new BitmapDrawable(getResources(), holderBitmap);
            BitmapImageViewTarget target = new BitmapImageViewTarget(UserCover) {
                @Override
                public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                    AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                    UserCover.setImageBitmap(bitmap);
                    Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                        Palette.Swatch swatchColor = palette.getVibrantSwatch();
                        Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                        if (swatchColor != null && swatchColorDark != null) {
                            try {
                                mutedColor = swatchColor.getRgb();
                                mutedColorStatusBar = swatchColorDark.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat(" " + e.getMessage());
                            }
                        } else {
                            List<Palette.Swatch> swatches = palette.getSwatches();
                            for (Palette.Swatch swatch : swatches) {
                                if (swatch != null) {
                                    mutedColor = swatch.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    float[] hsv = new float[3];
                                    Color.colorToHSV(mutedColor, hsv);
                                    hsv[2] *= 0.8f; // value component
                                    mutedColorStatusBar = Color.HSVToColor(hsv);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                    break;
                                }
                            }

                        }
                    });
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                    UserCover.setImageBitmap(holderBitmap);
                    Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                        Palette.Swatch swatchColor = palette.getVibrantSwatch();
                        Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                        if (swatchColor != null && swatchColorDark != null) {
                            try {
                                mutedColor = swatchColor.getRgb();
                                mutedColorStatusBar = swatchColorDark.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                            } catch (Exception ex) {
                                AppHelper.LogCat(" " + e.getMessage());
                            }
                        } else {
                            List<Palette.Swatch> swatches = palette.getSwatches();
                            for (Palette.Swatch swatch : swatches) {
                                if (swatch != null) {
                                    mutedColor = swatch.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    float[] hsv = new float[3];
                                    Color.colorToHSV(mutedColor, hsv);
                                    hsv[2] *= 0.8f; // value component
                                    mutedColorStatusBar = Color.HSVToColor(hsv);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                    break;
                                }
                            }

                        }
                    });
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    AnimationsUtil.expandToolbar(containerProfile, holderBitmap, AppBarLayout);
                    UserCover.setImageBitmap(holderBitmap);
                    Palette.from(holderBitmap).maximumColorCount(numberOfColors).generate(palette -> {
                        Palette.Swatch swatchColor = palette.getVibrantSwatch();
                        Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                        if (swatchColor != null && swatchColorDark != null) {
                            try {
                                mutedColor = swatchColor.getRgb();
                                mutedColorStatusBar = swatchColorDark.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat(" " + e.getMessage());
                            }
                        } else {
                            List<Palette.Swatch> swatches = palette.getSwatches();
                            for (Palette.Swatch swatch : swatches) {
                                if (swatch != null) {
                                    mutedColor = swatch.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    float[] hsv = new float[3];
                                    Color.colorToHSV(mutedColor, hsv);
                                    hsv[2] *= 0.8f; // value component
                                    mutedColorStatusBar = Color.HSVToColor(hsv);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                    break;
                                }
                            }

                        }
                    });
                }
            };
            RooyeshImageLoader.loadSimpleImage(this, EndPoints.PROFILE_IMAGE_URL + ImageUrl, target, drawable, AppConstants.PROFILE_IMAGE_SIZE);
        } else {
            Drawable drawable;
            drawable = AppHelper.getDrawable(this, R.drawable.image_holder_gp);
            BitmapImageViewTarget target = new BitmapImageViewTarget(UserCover) {
                @Override
                public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                    UserCover.setImageBitmap(bitmap);
                    Palette.from(bitmap).maximumColorCount(numberOfColors).generate(palette -> {
                        Palette.Swatch swatchColor = palette.getVibrantSwatch();
                        Palette.Swatch swatchColorDark = palette.getDarkVibrantSwatch();
                        if (swatchColor != null && swatchColorDark != null) {
                            try {
                                mutedColor = swatchColor.getRgb();
                                mutedColorStatusBar = swatchColorDark.getRgb();
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (AppHelper.isAndroid5()) {
                                    getWindow().setStatusBarColor(mutedColorStatusBar);
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat(" " + e.getMessage());
                            }
                        } else {
                            List<Palette.Swatch> swatches = palette.getSwatches();
                            for (Palette.Swatch swatch : swatches) {
                                if (swatch != null) {
                                    mutedColor = swatch.getRgb();
                                    collapsingToolbar.setContentScrimColor(mutedColor);
                                    float[] hsv = new float[3];
                                    Color.colorToHSV(mutedColor, hsv);
                                    hsv[2] *= 0.8f; // value component
                                    mutedColorStatusBar = Color.HSVToColor(hsv);
                                    if (AppHelper.isAndroid5()) {
                                        getWindow().setStatusBarColor(mutedColorStatusBar);
                                    }
                                    break;
                                }
                            }

                        }
                    });
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    UserCover.setImageDrawable(errorDrawable);
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    UserCover.setImageDrawable(placeholder);
                }
            };
            RooyeshImageLoader.loadSimpleImage(this, EndPoints.PROFILE_IMAGE_URL + ImageUrl, target, drawable, AppConstants.PROFILE_IMAGE_SIZE);
        }
    }


    public void UpdateGroupUI(GroupsModel groupsModel) {
        try {
            updateUI(groupsModel, null);
        } catch (Exception e) {
            AppHelper.LogCat("Exception " + e.getMessage());
        }

    }


    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, StatusResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected StatusResponse doInBackground(Void... params) {
            return uploadFile();
        }

        private StatusResponse uploadFile() {
            RequestBody requestFile;
            final StatusResponse statusResponse = null;
            if (PicturePath != null) {
                // use the FileUtils to get the actual file by uri
                File file = new File(PicturePath);
                // create RequestBody instance from file
                requestFile =
                        RequestBody.create(MediaType.parse("image/*"), file);
            } else {
                requestFile = null;
            }
            APIGroups apiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
            ProfileActivity.this.runOnUiThread(() -> AppHelper.showDialog(ProfileActivity.this, "Updating ... "));
            Call<GroupResponse> statusResponseCall = apiGroups.uploadImage(requestFile, groupID);
            statusResponseCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        AppHelper.hideDialog();
                        if (response.body().isSuccess()) {
                            int groupId = response.body().getGroupID();
                            Realm realm = RooyeshApplication.getRealmDatabaseInstance();
                            realm.executeTransactionAsync(realm1 -> {
                                        GroupsModel groupsModel = realm1.where(GroupsModel.class).equalTo("id", groupId).findFirst();
                                        groupsModel.setGroupImage(response.body().getGroupImage());
                                        realm1.copyToRealmOrUpdate(groupsModel);

                                    }, () -> realm.executeTransactionAsync(realm1 -> {
                                        ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("groupID", groupId).findFirst();
                                        conversationsModel.setRecipientImage(response.body().getGroupImage());
                                        realm1.copyToRealmOrUpdate(conversationsModel);
                                        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
                                    }, () -> {
                                        setImage(response.body().getGroupImage());
                                        AppHelper.CustomToast(ProfileActivity.this, response.body().getMessage());
                                    }, error -> AppHelper.LogCat("error update group image in conversation model " + error.getMessage())),
                                    error -> AppHelper.LogCat("error update group image in group model " + error.getMessage()));
                            realm.close();
                        } else {
                            AppHelper.CustomToast(ProfileActivity.this, response.body().getMessage());
                        }
                    } else {
                        AppHelper.hideDialog();
                        AppHelper.CustomToast(ProfileActivity.this, response.message());
                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.hideDialog();
                    AppHelper.LogCat("Failed  upload your image " + t.getMessage());
                }
            });
            return statusResponse;
        }


        @Override
        protected void onPostExecute(StatusResponse response) {
            super.onPostExecute(response);
            // AppHelper.LogCat("Response from server: " + response);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (AppHelper.isAndroid5()) {
            ActivityCompat.finishAfterTransition(ProfileActivity.this);
        } else {
            finish();
            AnimationsUtil.setSlideOutAnimation(this);
        }
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_BACK_ACTIVITY));
    }


    @Override
    protected void onResume() {
        super.onResume();
        RooyeshApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, containerProfile, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, containerProfile, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, containerProfile, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

        }
    }
}
