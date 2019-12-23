package com.setayeshco.rooyesh.adapters.recyclerView.groups;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.PresentActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.models.Roozh;
import com.setayeshco.rooyesh.models.groups.GroupResponse;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */


public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter, View.OnClickListener {
    protected Activity mActivity;
    private List<MembersGroupModel> mContactsModel;
    private APIService mApiService;
    private Realm realm;
    private boolean isAdmin;

    private MemoryCache memoryCache;

    public static ArrayList<Integer> presenetUsers = new ArrayList<Integer>();



    private static Context mContext;
    private static int mPosition;
    private static SparseBooleanArray sSelectedItems;
    public static String userID;
    public static int groupID;







    public GroupMembersAdapter(@NonNull Activity mActivity, APIService mApiService, boolean isAdmin) {
        this.mActivity = mActivity;
        this.isAdmin = isAdmin;
        this.mApiService = mApiService;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        this.memoryCache = new MemoryCache();

    }

    public void setContacts(List<MembersGroupModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_group_members, parent, false);

        return new ContactsViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
        final MembersGroupModel membersGroupModel = this.mContactsModel.get(position);
        try {











            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

            //if is present
            //presenetUsers.add(1);
   /*         presenetUsers.add(0);
            if (presenetUsers.contains(position)) {
                //membersGroupModel
                ((ContactsViewHolder) holder).presentToggle.setChecked(true);

            }

*/
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv


            if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                contactsViewHolder.itemView.setEnabled(false);
            }
            if (membersGroupModel.getUsername() != null) {
                if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                    contactsViewHolder.setUsername(mActivity.getString(R.string.you));
                } else {
                    contactsViewHolder.setUsername(membersGroupModel.getUsername());
                }

            } else {
                try {
                    if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                        contactsViewHolder.setUsername(mActivity.getString(R.string.you));
                    } else {
                        String name = UtilsPhone.getContactName(mActivity, membersGroupModel.getPhone());
                        if (name != null) {
                            contactsViewHolder.setUsername(name);
                        } else {
                            contactsViewHolder.setUsername(membersGroupModel.getPhone());
                        }

                    }
                } catch (Exception e) {
                    AppHelper.LogCat(" " + e.getMessage());
                }

            }

            if (membersGroupModel.getStatus() != null) {
                contactsViewHolder.setStatus(membersGroupModel.getStatus());
            } else {
                contactsViewHolder.setStatus(membersGroupModel.getPhone());
            }
            if (membersGroupModel.getRole().equals("member")) {
                contactsViewHolder.hideAdmin();
            } else {
                contactsViewHolder.showAdmin();
            }

            contactsViewHolder.setUserImage(membersGroupModel.getImage(), membersGroupModel.getId());

        } catch (Exception e) {
            AppHelper.LogCat("Exception" + e.getMessage());
        }

        contactsViewHolder.layoutShowPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membersGroupModel.getUserId();
                Log.d("onClick",">>>>>>   onclick  "+membersGroupModel.getUserId());
                userID = membersGroupModel.getUserId() + "";
                groupID = membersGroupModel.getGroupID();
                Intent intent = new Intent(mActivity,PresentActivity.class);
                intent.putExtra("user_id_present",membersGroupModel.getUserId());
                mActivity.startActivity(intent);

            }
        });


        contactsViewHolder.presentToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (view instanceof ToggleButton) {

                    ToggleButton clickedToggle = (ToggleButton) view;
                    boolean togglePresent = clickedToggle.isChecked();

             /*       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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


                    String date = jCal.toString() + " " + currentTime ;

                    Log.i("Toggle", "currentDateandTime " + currentDateandTime);
                    Log.i("Toggle", "Shamsi " + jCal.toString() + " " + currentTime);
                    Log.i("Toggle", "clicked " + togglePresent + " row# " + position);
                    Log.i("Toggle", "UserId " + membersGroupModel.getUserId() + " GroupID " + membersGroupModel.getGroupID());
              //      IsPresnt(membersGroupModel.getUserId() , membersGroupModel.getGroupID() , date , togglePresent);
*/
                    if (togglePresent) {
                        presenetUsers.add(membersGroupModel.getUserId());


                    } else {

                            if (presenetUsers.contains(membersGroupModel.getUserId())) {
                                presenetUsers.remove(presenetUsers.indexOf(membersGroupModel.getUserId()));
                        }
                    }
                }

            }
        });

        contactsViewHolder.setOnClickListener(view -> {


            if (isAdmin) {
                String TheName;
                String name = UtilsPhone.getContactName(mActivity, membersGroupModel.getPhone());
                if (name != null) {
                    TheName = name;
                } else {
                    TheName = membersGroupModel.getPhone();
                }
                CharSequence options[] = new CharSequence[0];
                if (membersGroupModel.isAdmin()) {
                    options = new CharSequence[]{mActivity.getString(R.string.message_group_option) + TheName + "", mActivity.getString(R.string.view_group_option) + TheName + "", mActivity.getString(R.string.make_group_option) + " " + TheName + " " + mActivity.getString(R.string.make_member_group_option), mActivity.getString(R.string.remove_group_option) + TheName + ""};
                } else {
                    options = new CharSequence[]{mActivity.getString(R.string.message_group_option) + TheName + "", mActivity.getString(R.string.view_group_option) + TheName + "", mActivity.getString(R.string.make_group_option) + " " + TheName + " " + mActivity.getString(R.string.make_admin_group_option), mActivity.getString(R.string.remove_group_option) + TheName + ""};
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:


                            //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                            Bundle bundle = new Bundle();
                            bundle.putInt("conversationID", 0);
                            bundle.putInt("recipientID", membersGroupModel.getUserId());
                            bundle.putBoolean("isGroup", false);
                            MessagesFragment messageFragmentOk = new MessagesFragment();
                            messageFragmentOk.setArguments(bundle);


                            //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                            Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                            messagingIntent.putExtra("conversationID", 0);
                            messagingIntent.putExtra("recipientID", membersGroupModel.getUserId());
                            messagingIntent.putExtra("isGroup", false);
                            mActivity.startActivity(messagingIntent);
                            mActivity.finish();
                            break;
                        case 1:
                            contactsViewHolder.viewContact(membersGroupModel.getPhone());
                            break;
                        case 2:
                            if (membersGroupModel.isAdmin()) {
                                contactsViewHolder.RemoveMemberAsAdmin(membersGroupModel.getUserId(), membersGroupModel.getGroupID());
                            } else {
                                contactsViewHolder.MakeMemberAsAdmin(membersGroupModel.getUserId(), membersGroupModel.getGroupID());
                            }
                            break;
                        case 3:
                            AlertDialog.Builder builderDelete = new AlertDialog.Builder(mActivity);
                            builderDelete.setMessage(mActivity.getString(R.string.remove_from_group) + TheName + mActivity.getString(R.string.from_group))
                                    .setPositiveButton(mActivity.getString(R.string.ok), (dialog1, which1) -> {
                                        AppHelper.showDialog(mActivity, mActivity.getString(R.string.deleting_group));
                                        contactsViewHolder.RemoveMemberFromGroup(membersGroupModel.getUserId(), membersGroupModel.getGroupID());
                                    }).setNegativeButton(mActivity.getString(R.string.cancel), null).show();
                            break;
                    }

                });
                builder.show();

            }
            return true;
        });


    }


    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

 /*   void IsPresnt(int groupID, ArrayList presentId , ArrayList absentId , String date ){
        APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
        Call<GroupResponse> PresentCall = mApiGroups.isPresent(id,groupID,date,present);
        PresentCall.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                        //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                    } else {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                    }
                } else {
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

            }
        });
    }

*/
    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



    @Override
    public int getItemCount() {
        if (mContactsModel != null) return mContactsModel.size();
        return 0;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return mContactsModel.size() > pos ? Character.toString(mContactsModel.get(pos).getUsername().charAt(0)) : null;
        } catch (Exception e) {
            AppHelper.LogCat(e.getMessage());
            return e.getMessage();
        }

    }

    @Override
    public void onClick(View view) {


        Log.i("gmAdapter  Toggle", "clicked " + view.getId() + " " + getItemCount());

    }


    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        ImageView userImage;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.status)
        EmojiconTextView status;
        @BindView(R.id.admin)
        TextView admin;

        @BindView(R.id.member)
        TextView member;

        @BindView(R.id.toggle_presenet)
        ToggleButton presentToggle;
        @BindView(R.id.mBackground)
        LinearLayout mBackground;


        LinearLayout layoutShowPA;


        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutShowPA = itemView.findViewById(R.id.layoutShowPA);
            setTypeFaces();

        }


        private void setTypeFaces() {
            if (AppConstants.ENABLE_FONTS_TYPES) {
                status.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                username.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                member.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                admin.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                presentToggle.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
            }
        }

        void setUserImage(String ImageUrl, int recipientId) {
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, mActivity, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {

                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userImage.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeHolderDrawable) {
                        super.onLoadStarted(placeHolderDrawable);
                        userImage.setImageDrawable(placeHolderDrawable);
                    }
                };
                RooyeshImageLoader.loadCircleImage(mActivity, EndPoints.ROWS_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
            }
        }

        void hideAdmin() {
            admin.setVisibility(View.GONE);
            member.setVisibility(View.VISIBLE);
        }

        void showAdmin() {
            admin.setVisibility(View.VISIBLE);
            member.setVisibility(View.GONE);
        }

        void showAbsent() {

            presentToggle.setChecked(false);

        }

        void showPresent() {
            presentToggle.setChecked(true);

        }


        void setUsername(String Username) {
            username.setText(Username);
        }

        void setStatus(String Status) {
            String statu = UtilsString.unescapeJava(Status);
            if (statu.length() > 18)
                status.setText(statu.substring(0, 18) + "... " + "");
            else
                status.setText(statu);
        }


        void setOnClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);
        }

        void viewContact(String phone) {
            long ContactID = UtilsPhone.getContactID(mActivity, phone);
            try {
                if (ContactID != 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, ContactID));
                    mActivity.startActivity(intent);
                }
            } catch (Exception e) {
                AppHelper.LogCat("Error view contact  Exception" + e.getMessage());
            }
        }


        void MakeMemberAsAdmin(int id, int groupID) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.makeAdmin(groupID, id);
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                        } else {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_make_member_as_admin), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                }
            });


        }

        void RemoveMemberAsAdmin(int id, int groupID) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.removeAdmin(groupID, id);
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                        } else {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_make_member_as_admin), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                }
            });


        }

        /*void IsPresnt(int id, int groupID , String date , boolean present){
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
            Call<GroupResponse> PresentCall = mApiGroups.isPresent(id,groupID,date,present);
            PresentCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                          //  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                         //   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                        } else {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                }
            });
        }*/



        void RemoveMemberFromGroup(int id, int groupID) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, EndPoints.BACKEND_BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.removeMember(groupID, id);
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        AppHelper.hideDialog();
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            realm.executeTransaction(realm1 -> {
                                MembersGroupModel membersGroupModel = realm1.where(MembersGroupModel.class).equalTo("userId", id).equalTo("groupID", groupID).findFirst();
                                membersGroupModel.deleteFromRealm();
                            });
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_GROUP));
                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ADD_MEMBER, groupID));
                            AppHelper.reloadActivity(mActivity);
                        } else {
                            AppHelper.hideDialog();
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.hideDialog();
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.hideDialog();
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_remove_member), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                }
            });


        }


    }


}
