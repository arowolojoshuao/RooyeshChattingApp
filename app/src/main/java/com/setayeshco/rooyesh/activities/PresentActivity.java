package com.setayeshco.rooyesh.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.groups.AddNewMembersToGroupActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.profile.ProfileActivity;
import com.setayeshco.rooyesh.adapters.recyclerView.groups.GroupMembersAdapter;
import com.setayeshco.rooyesh.adapters.recyclerView.groups.GroupMembersAdapterSearch;
import com.setayeshco.rooyesh.adapters.recyclerView.media.MediaProfileAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.bottomSheets.BottomSheetEditGroupImage;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.interfaces.UpdatePresentClickListener;
import com.setayeshco.rooyesh.models.groups.ContactAdapter;
import com.setayeshco.rooyesh.models.groups.GroupPresent;
import com.setayeshco.rooyesh.models.groups.GroupResponse;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.groups.OnLoadMoreListener;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.status.StatusResponse;
import com.setayeshco.rooyesh.presenters.users.PresentsPresenter;
import com.setayeshco.rooyesh.presenters.users.ProfilePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , NetworkListener {

    android.support.v7.widget.Toolbar toolbar;
    LinearLayout layoutShowDatePicker;
    TextView txtName;
    TextView txtStdNumber;
    TextView txt1;
    TextView txt2;
    RecyclerView MembersList;
    CardView participantContainerExit;
    CardView participantContainerDelete;
    LinearLayout addNewParticipant;
    CardView participantContainer;
    CoordinatorLayout containerProfile;
    TextView participantCounter;
    ImageView UserCover;
    LinearLayout GroupTitleContainer;
    EmojiconTextView mCreatedTitle;
    CollapsingToolbarLayout collapsingToolbar;
    android.support.design.widget.AppBarLayout AppBarLayout;
    CardView statusPhoneContainer;
    TextView status;
    TextView numberPhone;
    TextView status_date;
    CardView mediaSection;
    TextView mediaCounter;






    public int groupID;
    public String userID = "30";






    List<GroupPresent> groupPresentList;
    private ContactAdapter contactAdapter;
    private Random random;


    private APIService mApiService=new APIService(PresentActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);




        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar_make_smart);
        layoutShowDatePicker = (LinearLayout) findViewById(R.id.layoutShowDatePicker);
        txtName = (TextView) findViewById(R.id.txtName);
        txtStdNumber = (TextView) findViewById(R.id.txtStdNumber);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        MembersList = (RecyclerView) findViewById(R.id.MembersList);
        participantContainerExit = (CardView) findViewById(R.id.participantContainerExit);
        participantContainerDelete = (CardView) findViewById(R.id.participantContainerDelete);
        addNewParticipant = (LinearLayout) findViewById(R.id.add_contact_participate);
        participantContainer = (CardView) findViewById(R.id.participantContainer);
        participantCounter = (TextView) findViewById(R.id.participantCounter);
        UserCover = (ImageView) findViewById(R.id.cover);
        GroupTitleContainer = (LinearLayout) findViewById(R.id.group_container_title);
        mCreatedTitle = (EmojiconTextView) findViewById(R.id.created_title);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout = (android.support.design.widget.AppBarLayout) findViewById(R.id.appbar);
        statusPhoneContainer = (CardView) findViewById(R.id.statusPhoneContainer);
        status = (TextView) findViewById(R.id.status);
        numberPhone = (TextView) findViewById(R.id.numberPhone);
        status_date = (TextView) findViewById(R.id.status_date);
        mediaSection = (CardView) findViewById(R.id.media_section);
        mediaCounter = (TextView) findViewById(R.id.media_counter);

        txtStdNumber.setTypeface(AppHelper.setTypeFace(this, "IRANSans(FaNum)_Bold"));
        txtName.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        txt1.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        txt2.setTypeface(AppHelper.setTypeFace(this, "IranSans"));


        if (getIntent().hasExtra("groupID")) {
            groupID = getIntent().getExtras().getInt("groupID");
        }


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




         //   actionBarIdForAll();
           //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            getSupportActionBar().setTitle("رویش");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


           /* layoutShowDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                            PresentActivity.this,
                            persianCalendar.getPersianYear(),
                            persianCalendar.getPersianMonth(),
                            persianCalendar.getPersianDay()
                    );

                    datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                }
            });*/

        }





    //    IsPresntSearch(""+groupID,"1396-12-07");

        IsPresntSearch(GroupMembersAdapter.groupID+"",Integer.parseInt(GroupMembersAdapter.userID));
        //.......................................

     //   Toast.makeText(getApplicationContext(),GroupMembersAdapter.groupID+" >>> user id : "+ Integer.parseInt(GroupMembersAdapter.userID),Toast.LENGTH_LONG).show();

    /*    Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("user_id_present");
        Toast.makeText(getApplicationContext(),"user id : "+ userID,Toast.LENGTH_LONG).show();
        if (bundle != null){
            if (bundle.containsKey("user_id_present")){
                userID = bundle.getString("user_id_present");
                Toast.makeText(getApplicationContext(),"user id : "+ userID,Toast.LENGTH_LONG).show();

            }
        }*/




        //.......................................

    }



   /* private void actionBarIdForAll()
    {

        Typeface font = Typeface.createFromAsset(PresentActivity.this.getAssets(), "fonts/IranSans.ttf");

        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

//if you need to customize anything else about the text, do it here.
//I'm using a custom TextView with a custom font in my layout xml so all I need to do is set title
        TextView textView = (TextView)v.findViewById(R.id.title);
        textView.setText(this.getTitle());
        textView.setTypeface(font);

//assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }
*/

    //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm


    private void initialsRecyclerView( List<GroupPresent> groupPresentList1 ){

        groupPresentList1 = groupPresentList;
        random = new Random();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(recyclerView, groupPresentList1, this, new UpdatePresentClickListener() {
            @Override
            public void onItemClick(String pID, String state) {
              //  updatePresntState(pID,state);
                Log.d("ttttt","pID : "+pID+"  state : "+state);
                contactAdapter.notifyDataSetChanged();

            }
        });
        recyclerView.setAdapter(contactAdapter);

        final List<GroupPresent>[] finalGroupPresentList = new List[]{groupPresentList1};
        contactAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (finalGroupPresentList[0].size() <= 20) {
                    finalGroupPresentList[0].add(null);
                    contactAdapter.notifyItemInserted(finalGroupPresentList[0].size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalGroupPresentList[0].remove(finalGroupPresentList[0].size() - 1);
                            contactAdapter.notifyItemRemoved(finalGroupPresentList[0].size());
                            int index = finalGroupPresentList[0].size();
                            int end = index + 10;
                        /*    for (int i = index; i < end; i++) {
                                GroupPresent contact = new GroupPresent();
                                contact.setPhone(phoneNumberGenerating());
                                contact.setEmail("DevExchanges" + i + "@gmail.com");
                                finalGroupPresentList[0].add(contact);
                            }*/
                            finalGroupPresentList[0] = groupPresentList;
                            contactAdapter.notifyDataSetChanged();
                            contactAdapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(PresentActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
           // startActivity(new Intent(PresentActivity.this,ProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
       // String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        String strDayOfMonth;
        String strMonthOfYear;
        if (dayOfMonth<10){
            strDayOfMonth = "0"+dayOfMonth;
        }else {
            strDayOfMonth =""+dayOfMonth;
        }
        if ((monthOfYear+1)<10){
            strMonthOfYear = "0"+(monthOfYear+1);
        }else {
            strMonthOfYear =""+(monthOfYear+1);
        }
        String date = year+"-"+strMonthOfYear+"-"+strDayOfMonth;
      //  IsPresntSearch(""+groupID,30);

      //  dateTextView.setText(date);
    }




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




    void IsPresntSearch(String groupID, int date ){
        APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
        Call<List<GroupPresent>> PresentCall = mApiGroups.listPresentGroup(groupID,date);
        PresentCall.enqueue(new Callback<List<GroupPresent>>() {
            @Override
            public void onResponse(Call<List<GroupPresent>> call, Response<List<GroupPresent>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("PRESENT",response.body().toString());
                        groupPresentList = response.body();
                        txtName.setText(response.body().get(0).getUsername());
                        txtStdNumber.setText(response.body().get(0).getStdNumber()+"");

                        for (GroupPresent g:groupPresentList
                             ) {
                            Log.d("groupPresentList",g.getUsername() + "   phpne : " + g.getPhone());
                        }
                   //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                        //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                     //   Toast.makeText(PresentActivity.this,"111111",Toast.LENGTH_LONG).show();
                        if (groupPresentList != null) {
                            initialsRecyclerView(groupPresentList);
                        }

                    } else {
                 //       AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                     //   Toast.makeText(PresentActivity.this,"22222222",Toast.LENGTH_LONG).show();

                    }
                } else {
               //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
               //     Toast.makeText(PresentActivity.this,"3333333",Toast.LENGTH_LONG).show();

                }
            }



            @Override
            public void onFailure(Call<List<GroupPresent>> call, Throwable t)
            {
            //    AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
Toast.makeText(PresentActivity.this,""+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }







    //.........................kkkkkkkllllllllll

    void updatePresntState(String pID, String state ){
        APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
        Call<List<GroupPresent>> PresentCall = mApiGroups.updateListPresentGroup(pID,state);
        PresentCall.enqueue(new Callback<List<GroupPresent>>() {
            @Override
            public void onResponse(Call<List<GroupPresent>> call, Response<List<GroupPresent>> response) {
                if (response.isSuccessful()) {
                    MembersList.notifyAll();
                    if (response.body() != null) {
              /*          Log.d("PRESENT",response.body().toString());
                        groupPresentList = response.body();
                        txtName.setText(response.body().get(0).getUsername());
                        txtStdNumber.setText(response.body().get(0).getStdNumber()+"");

                        for (GroupPresent g:groupPresentList
                                ) {
                            Log.d("groupPresentList",g.getUsername() + "   phpne : " + g.getPhone());
                        }
                        //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                        //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                        //   Toast.makeText(PresentActivity.this,"111111",Toast.LENGTH_LONG).show();
                        if (groupPresentList != null) {
                            initialsRecyclerView(groupPresentList);
                        }*/
                        MembersList.notifyAll();
                    } else {
                        //       AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        //   Toast.makeText(PresentActivity.this,"22222222",Toast.LENGTH_LONG).show();

                    }
                } else {
                    //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                    //     Toast.makeText(PresentActivity.this,"3333333",Toast.LENGTH_LONG).show();

                }
            }



            @Override
            public void onFailure(Call<List<GroupPresent>> call, Throwable t)
            {
                //    AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                Toast.makeText(PresentActivity.this,""+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }



    //.........................kkkkkkkllllllllll






}




/*


<color name="mdtp_accent_color">#009688</color>
<color name="mdtp_accent_color_dark">#00796b</color>


Theme the pickers
You can theme the pickers by overwriting the color resources mdtp_accent_color and mdtp_accent_color_dark in your project.

<color name="mdtp_accent_color">#009688</color>
<color name="mdtp_accent_color_dark">#00796b</color>
#Additional Options

TimePickerDialog dark theme
The TimePickerDialog has a dark theme that can be set by calling
timePickerDialog.setThemeDark(true);
DatePickerDialog dark theme The DatePickerDialog has a dark theme that can be set by calling
datePickerDialog.setThemeDark(true);
TimePickerDialog setTitle(String title) Shows a title at the top of the TimePickerDialog

setSelectableDays(PersianCalendar[] days) You can pass a PersianCalendar[] to the DatePickerDialog. This values in this list are the only acceptable dates for the picker. It takes precedence over setMinDate(PersianCalendar day) and setMaxDate(PersianCalendar day)

setHighlightedDays(PersianCalendar[] days) You can pass a PersianCalendar[] of days to highlight. They will be rendered in bold. You can tweak the color of the highlighted days by overwriting mdtp_date_picker_text_highlighted

OnDismissListener and OnCancelListener
Both pickers can be passed a DialogInterface.OnDismissLisener or DialogInterface.OnCancelListener which allows you to run code when either of these events occur.

timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialogInterface) {
      Log.d("TimePicker", "Dialog was cancelled");
    }
});
vibrate(boolean vibrate) Set whether the dialogs should vibrate the device when a selection is made. This defaults to true.

*/












//.......................................




/*


package com.setayeshco.rooyesh.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.groups.AddNewMembersToGroupActivity;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.profile.ProfileActivity;
import com.setayeshco.rooyesh.adapters.recyclerView.groups.GroupMembersAdapter;
import com.setayeshco.rooyesh.adapters.recyclerView.groups.GroupMembersAdapterSearch;
import com.setayeshco.rooyesh.adapters.recyclerView.media.MediaProfileAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.bottomSheets.BottomSheetEditGroupImage;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.groups.ContactAdapter;
import com.setayeshco.rooyesh.models.groups.GroupPresent;
import com.setayeshco.rooyesh.models.groups.GroupResponse;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.groups.OnLoadMoreListener;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.status.StatusResponse;
import com.setayeshco.rooyesh.presenters.users.PresentsPresenter;
import com.setayeshco.rooyesh.presenters.users.ProfilePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , NetworkListener {

    android.support.v7.widget.Toolbar toolbar;
    LinearLayout layoutShowDatePicker;
    TextView txtDate;
    RecyclerView MembersList;
    CardView participantContainerExit;
    CardView participantContainerDelete;
    LinearLayout addNewParticipant;
    CardView participantContainer;
    CoordinatorLayout containerProfile;
    TextView participantCounter;
    ImageView UserCover;
    LinearLayout GroupTitleContainer;
    EmojiconTextView mCreatedTitle;
    CollapsingToolbarLayout collapsingToolbar;
    android.support.design.widget.AppBarLayout AppBarLayout;
    CardView statusPhoneContainer;
    TextView status;
    TextView numberPhone;
    TextView status_date;
    CardView mediaSection;
    TextView mediaCounter;




    private GroupMembersAdapterSearch mGroupMembersAdapter;
    private boolean isAnAdmin;
    public int groupID;
    private boolean isGroup;
    private boolean left;
    private GroupsModel mGroupsModel;
    private PresentsPresenter mProfilePresenter;
    List<MembersGroupModel> myMembersGroupModels;
    private int mutedColorStatusBar;
    private MemoryCache memoryCache;
    int numberOfColors = 24;
    private int mutedColor;
    private ContactsModel mContactsModel;
    private MediaProfileAdapter mMediaProfileAdapter;
    private String PicturePath;
    private Socket mSocket;

    List<GroupPresent> groupPresentList;
    private ContactAdapter contactAdapter;
    private Random random;


    private APIService mApiService=new APIService(PresentActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);




        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar_make_smart);
        layoutShowDatePicker = (LinearLayout) findViewById(R.id.layoutShowDatePicker);
        txtDate = (TextView) findViewById(R.id.txtDate);
        MembersList = (RecyclerView) findViewById(R.id.MembersList);
        participantContainerExit = (CardView) findViewById(R.id.participantContainerExit);
        participantContainerDelete = (CardView) findViewById(R.id.participantContainerDelete);
        addNewParticipant = (LinearLayout) findViewById(R.id.add_contact_participate);
        participantContainer = (CardView) findViewById(R.id.participantContainer);
        participantCounter = (TextView) findViewById(R.id.participantCounter);
        UserCover = (ImageView) findViewById(R.id.cover);
        GroupTitleContainer = (LinearLayout) findViewById(R.id.group_container_title);
        mCreatedTitle = (EmojiconTextView) findViewById(R.id.created_title);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout = (android.support.design.widget.AppBarLayout) findViewById(R.id.appbar);
        statusPhoneContainer = (CardView) findViewById(R.id.statusPhoneContainer);
        status = (TextView) findViewById(R.id.status);
        numberPhone = (TextView) findViewById(R.id.numberPhone);
        status_date = (TextView) findViewById(R.id.status_date);
        mediaSection = (CardView) findViewById(R.id.media_section);
        mediaCounter = (TextView) findViewById(R.id.media_counter);



        if (getIntent().hasExtra("groupID")) {
            isGroup = getIntent().getExtras().getBoolean("isGroup");
            groupID = getIntent().getExtras().getInt("groupID");
        }
        mProfilePresenter = new PresentsPresenter(this);
        mProfilePresenter.onCreate();




        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("رویش");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            memoryCache = new MemoryCache();

            layoutShowDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                            PresentActivity.this,
                            persianCalendar.getPersianYear(),
                            persianCalendar.getPersianMonth(),
                            persianCalendar.getPersianDay()
                    );
                    datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                }
            });

        }



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

        addNewParticipant.setOnClickListener(v -> {
            Intent mIntent = new Intent(this, AddNewMembersToGroupActivity.class);
            mIntent.putExtra("groupID", groupID);
            mIntent.putExtra("profileAdd", "add");
            startActivity(mIntent);
        });





        IsPresntSearch(""+groupID,"1396-12-07");


        //.......................................






        //.......................................

    }




    //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm


    private void initialsRecyclerView( List<GroupPresent> groupPresentList1 ){
        groupPresentList1 = groupPresentList;
        random = new Random();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(recyclerView, groupPresentList1, this);
        recyclerView.setAdapter(contactAdapter);

        final List<GroupPresent>[] finalGroupPresentList = new List[]{groupPresentList1};
        contactAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (finalGroupPresentList[0].size() <= 20) {
                    finalGroupPresentList[0].add(null);
                    contactAdapter.notifyItemInserted(finalGroupPresentList[0].size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalGroupPresentList[0].remove(finalGroupPresentList[0].size() - 1);
                            contactAdapter.notifyItemRemoved(finalGroupPresentList[0].size());
                            int index = finalGroupPresentList[0].size();
                            int end = index + 10;
                        /*    for (int i = index; i < end; i++) {
                                GroupPresent contact = new GroupPresent();
                                contact.setPhone(phoneNumberGenerating());
                                contact.setEmail("DevExchanges" + i + "@gmail.com");
                                finalGroupPresentList[0].add(contact);
                            }*/
                  /*          finalGroupPresentList[0] = groupPresentList;
                                    contactAdapter.notifyDataSetChanged();
                                    contactAdapter.setLoaded();
                                    }
                                    }, 5000);
                                    } else {
                                    Toast.makeText(PresentActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                                    }
                                    }
                                    });
                                    }




//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm



public void checkIfIsAnAdmin() {
        if (isGroup) {
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


        } else {
        participantContainerExit.setVisibility(View.GONE);
        participantContainerDelete.setVisibility(View.GONE);
        participantContainer.setVisibility(View.GONE);
        }
        }




@Override
public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
        // startActivity(new Intent(PresentActivity.this,ProfileActivity.class));
        finish();
        }
        return super.onOptionsItemSelected(item);
        }

@Override
public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        txtDate.setText(date);
        //  dateTextView.setText(date);
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
        participantContainerExit.setVisibility(View.GONE);
        participantContainer.setVisibility(View.GONE);
        }


        }


private void initializerGroupMembersView() {
        mApiService = new APIService(PresentActivity.this);
        participantContainer.setVisibility(View.GONE);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGroupMembersAdapter = new GroupMembersAdapterSearch(PresentActivity.this, mApiService, isAnAdmin);
        MembersList.setLayoutManager(mLinearLayoutManager);
        MembersList.setAdapter(mGroupMembersAdapter);
        checkIfIsAnAdmin();

        }


public void UpdateGroupUI(GroupsModel groupsModel) {
        try {
        updateUI(groupsModel, null);
        } catch (Exception e) {
        AppHelper.LogCat("Exception " + e.getMessage());
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
     /*   collapsingToolbar.setTitle(name);


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
        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + mGroupsModel.getGroupImage(), mGroupsModel.getGroupImage(), PresentActivity.this, mGroupsModel.getId(), AppConstants.GROUP, AppConstants.FULL_PROFILE);

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
        BottomSheetEditGroupImage bottomSheetEditGroupImage = new BottomSheetEditGroupImage();
        bottomSheetEditGroupImage.show(getSupportFragmentManager(), bottomSheetEditGroupImage.getTag());
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
  /*      status.setText(Status);
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
        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), PresentActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.FULL_PROFILE);


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
        UserCover.setOnClickListener(view -> AppHelper.LaunchImagePreviewActivity(PresentActivity.this, AppConstants.PROFILE_IMAGE_FROM_SERVER, mContactsModel.getImage()));
        }
        }


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


@SuppressWarnings("unused")
@Subscribe(threadMode = ThreadMode.MAIN)
public void onEventMainThread(Pusher pusher) {

        switch (pusher.getAction()) {
        case AppConstants.EVENT_BUS_EXIT_GROUP:
        participantContainerExit.setVisibility(View.GONE);
        participantContainerDelete.setVisibility(View.VISIBLE);
        AppHelper.Snackbar(this, containerProfile, pusher.getData(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        break;
        case AppConstants.EVENT_BUS_DELETE_GROUP:
        AppHelper.Snackbar(this, containerProfile, pusher.getData(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        new Handler().postDelayed(this::finish, 500);
        break;
        case AppConstants.EVENT_BUS_PATH_GROUP:
        PicturePath = pusher.getData();
        try {
        new PresentActivity.UploadFileToServer().execute();
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
        PresentActivity.this.runOnUiThread(() -> AppHelper.showDialog(PresentActivity.this, "Updating ... "));
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
                                    AppHelper.CustomToast(PresentActivity.this, response.body().getMessage());
                                }, error -> AppHelper.LogCat("error update group image in conversation model " + error.getMessage())),
                                error -> AppHelper.LogCat("error update group image in group model " + error.getMessage()));
                        realm.close();
                    } else {
                        AppHelper.CustomToast(PresentActivity.this, response.body().getMessage());
                    }
                } else {
                    AppHelper.hideDialog();
                    AppHelper.CustomToast(PresentActivity.this, response.message());
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



    private void setImage(String ImageUrl) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId", groupID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket != null)
            mSocket.emit(AppConstants.SOCKET_IMAGE_GROUP_UPDATED, jsonObject);

        ImageLoader.DownloadImage(memoryCache, EndPoints.PROFILE_IMAGE_URL + ImageUrl, ImageUrl, PresentActivity.this, groupID, AppConstants.GROUP, AppConstants.FULL_PROFILE);
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


    void IsPresntSearch(String groupID, String date ){
        APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
        Call<List<GroupPresent>> PresentCall = mApiGroups.listPresentGroup(groupID,date);
        PresentCall.enqueue(new Callback<List<GroupPresent>>() {
            @Override
            public void onResponse(Call<List<GroupPresent>> call, Response<List<GroupPresent>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("PRESENT",response.body().toString());
                        groupPresentList = response.body();

                        for (GroupPresent g:groupPresentList
                                ) {
                            Log.d("groupPresentList",g.getUsername() + "   phpne : " + g.getPhone());
                        }
                        //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                        //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                        Toast.makeText(PresentActivity.this,"111111",Toast.LENGTH_LONG).show();
                        initialsRecyclerView(groupPresentList);

                    } else {
                        //       AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        Toast.makeText(PresentActivity.this,"22222222",Toast.LENGTH_LONG).show();

                    }
                } else {
                    //     AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                    Toast.makeText(PresentActivity.this,"3333333",Toast.LENGTH_LONG).show();

                }
            }



            @Override
            public void onFailure(Call<List<GroupPresent>> call, Throwable t)
            {
                //    AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                Toast.makeText(PresentActivity.this,""+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }




}




/*


<color name="mdtp_accent_color">#009688</color>
<color name="mdtp_accent_color_dark">#00796b</color>


Theme the pickers
You can theme the pickers by overwriting the color resources mdtp_accent_color and mdtp_accent_color_dark in your project.

<color name="mdtp_accent_color">#009688</color>
<color name="mdtp_accent_color_dark">#00796b</color>
#Additional Options

TimePickerDialog dark theme
The TimePickerDialog has a dark theme that can be set by calling
timePickerDialog.setThemeDark(true);
DatePickerDialog dark theme The DatePickerDialog has a dark theme that can be set by calling
datePickerDialog.setThemeDark(true);
TimePickerDialog setTitle(String title) Shows a title at the top of the TimePickerDialog

setSelectableDays(PersianCalendar[] days) You can pass a PersianCalendar[] to the DatePickerDialog. This values in this list are the only acceptable dates for the picker. It takes precedence over setMinDate(PersianCalendar day) and setMaxDate(PersianCalendar day)

setHighlightedDays(PersianCalendar[] days) You can pass a PersianCalendar[] of days to highlight. They will be rendered in bold. You can tweak the color of the highlighted days by overwriting mdtp_date_picker_text_highlighted

OnDismissListener and OnCancelListener
Both pickers can be passed a DialogInterface.OnDismissLisener or DialogInterface.OnCancelListener which allows you to run code when either of these events occur.

timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialogInterface) {
      Log.d("TimePicker", "Dialog was cancelled");
    }
});
vibrate(boolean vibrate) Set whether the dialogs should vibrate the device when a selection is made. This defaults to true.

*/

