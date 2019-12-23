package com.setayeshco.rooyesh.adapters.recyclerView.messages;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.profile.ProfilePreviewActivity;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.ConversationsFragment;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.call.CallManager;
import com.setayeshco.rooyesh.helpers.images.ImageCompressionAsyncTask;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.interfaces.FragmentCommunication;
import com.setayeshco.rooyesh.interfaces.OnItemClickListener;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.services.MainService;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_ITEM_IS_ACTIVATED;


/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // protected final Activity context;
    private OnItemClickListener mListener;
    private FragmentCommunication mCommunicator;
    private RealmList<ConversationsModel> mConversations;
    private Realm realm;
    //private APIService mApiService;
    private String SearchQuery;
    private SparseBooleanArray selectedItems;
    private boolean isActivated = false;
    private RecyclerView conversationList;
    private Socket mSocket;
    private MemoryCache memoryCache;

    int mSelectedItem;

    public ConversationsAdapter() {
        //  this.context = context;
        this.mConversations = new RealmList<>();
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        //  this.mApiService = new APIService(context);
        this.selectedItems = new SparseBooleanArray();
        this.memoryCache = new MemoryCache();

    }





    public ConversationsAdapter(@NonNull Socket mSocket, RecyclerView conversationList,OnItemClickListener listener) {
        this.mSocket = mSocket;
        this.mConversations = new RealmList<>();
        this.realm = RooyeshApplication.getRealmDatabaseInstance();
        // this.mApiService = new APIService(context);
        this.selectedItems = new SparseBooleanArray();
        this.conversationList = conversationList;
        this.memoryCache = new MemoryCache();
        //mCommunicator=communication;
        mListener = listener;
        // connectToChatServer();


    }

//    private void connectToChatServer() {
//
//        RooyeshApplication app = (RooyeshApplication) .getApplication();
//        mSocket = app.getSocket();
//
//        if (mSocket == null) {
//            RooyeshApplication.connectSocket();
//            mSocket = app.getSocket();
//        }
//
//        if (!mSocket.connected())
//            mSocket.connect();
//        emitUserIsOnline();
//        socket_recieve_msg();
//        setTypingEvent();
//        setTypingEventmessage();
//        if (isGroup) {
//            //  AppHelper.LogCat("here group seen");
//        } else {
//            if (!checkIfUserBlockedExist(recipientId, realm)) {
//                checkIfUserIsOnline();
//                emitMessageSeen();
//                socket_recieve_msg();
//            }
//        }
//
//
//    }

    public void setConversations(RealmList<ConversationsModel> conversationsModelList) {
        this.mConversations = conversationsModelList;
        notifyDataSetChanged();
    }
/*
    */

    /**
     * method to connect to the chat sever by socket
     *//*
    private void connectToChatServer() {
        RooyeshApplication app = (RooyeshApplication) context.getApplication();
        mSocket = app.getSocket();
        if (mSocket == null) {
            RooyeshApplication.connectSocket();
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            if (!mSocket.connected())
                mSocket.connect();
        }

    }*/

    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<ConversationsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ConversationsModel> newModels) {
        int arraySize = mConversations.size();
        for (int i = arraySize - 1; i >= 0; i--) {
            final ConversationsModel model = mConversations.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ConversationsModel> newModels) {
        int arraySize = newModels.size();
        for (int i = 0; i < arraySize; i++) {
            final ConversationsModel model = newModels.get(i);
            if (!mConversations.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ConversationsModel> newModels) {
        int arraySize = newModels.size();
        for (int toPosition = arraySize - 1; toPosition >= 0; toPosition--) {
            final ConversationsModel model = newModels.get(toPosition);
            final int fromPosition = mConversations.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ConversationsModel removeItem(int position) {
        final ConversationsModel model = mConversations.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ConversationsModel model) {
        mConversations.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ConversationsModel model = mConversations.remove(fromPosition);
        mConversations.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    //Methods for search end

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new ConversationViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ConversationViewHolder) {

            final ConversationViewHolder conversationViewHolder = (ConversationViewHolder) holder;

            Activity mActivity = (Activity) conversationViewHolder.itemView.getContext();
            final ConversationsModel conversationsModel = this.mConversations.get(position);


            try {
                MessagesModel messagesModel;
                RealmQuery<MessagesModel> callsModelRealmQuery = realm.where(MessagesModel.class).equalTo("conversationID", conversationsModel.getId());
                messagesModel = callsModelRealmQuery.findAllAsync().last();
                String Username = null;

                if (conversationsModel.isGroup()) {
                    if (conversationsModel.getRecipientUsername() != null) {
                        String groupName = UtilsString.unescapeJava(conversationsModel.getRecipientUsername());
                        conversationViewHolder.setUsername(groupName);
                        Username = groupName;
                    }
                } else {
                    String name = UtilsPhone.getContactName(mActivity, conversationsModel.getRecipientPhone());
                    if (name != null) {
                        conversationViewHolder.setUsername(name);
                        Username = name;
                    } else {
                        conversationViewHolder.setUsername(conversationsModel.getRecipientPhone());
                        Username = conversationsModel.getRecipientPhone();
                    }

                }


                SpannableString recipientUsername = SpannableString.valueOf(Username);
                if (SearchQuery == null) {
                    conversationViewHolder.username.setText(recipientUsername, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Username.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorSpanSearch)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    conversationViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
                }


                if (conversationsModel.isGroup() || messagesModel.isGroup()) {
                    if (!conversationsModel.getCreatedOnline()) {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorGray2));

                    } else {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                    }
                    if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("image");
                    } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("video");
                    } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("audio");
                    } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("document");
                    } else {

                        conversationViewHolder.isFile.setVisibility(View.GONE);
                        conversationViewHolder.FileContent.setVisibility(View.GONE);
                        conversationViewHolder.lastMessage.setVisibility(View.VISIBLE);
                        switch (messagesModel.getMessage()) {
                            case AppConstants.CREATE_GROUP:
                                if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                                    if (!conversationsModel.getCreatedOnline()) {
                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.tap_to_create_group));
                                    } else {
                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_created_this_group));
                                    }

                                } else {
                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                    if (name != null) {
                                        conversationViewHolder.setLastMessage("" + name + " " + mActivity.getString(R.string.he_created_this_group));
                                    } else {
                                        conversationViewHolder.setLastMessage("" + " " + messagesModel.getPhone() + mActivity.getString(R.string.he_created_this_group));
                                    }
                                }


                                break;
                            case AppConstants.LEFT_GROUP:
                                if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_left));
                                } else {
                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                    if (name != null) {
                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_left));
                                    } else {
                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_left));
                                    }


                                }

                                break;
                            default:

                                conversationViewHolder.isFile.setVisibility(View.GONE);
                                conversationViewHolder.FileContent.setVisibility(View.GONE);
                                conversationViewHolder.lastMessage.setVisibility(View.VISIBLE);
                                if (conversationsModel.getLastMessage() != null)
                                    conversationViewHolder.setLastMessage(conversationsModel.getLastMessage());
                                else
                                    conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                break;
                        }
                    }

                    if (messagesModel.getDate() != null) {
                        DateTime messageDate = UtilsTime.getCorrectDate(messagesModel.getDate());
                        String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                        conversationViewHolder.setMessageDate(finalDate);
                    }

                    if (conversationsModel.getCreatedOnline()) {
                        conversationViewHolder.setGroupImage(conversationsModel.getRecipientImage(), conversationsModel.getGroupID());
                    } else {
                        conversationViewHolder.setGroupImageOffline(conversationsModel.getRecipientImage());
                    }

                    if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                        conversationViewHolder.showSent(messagesModel.getStatus());
                    } else {
                        conversationViewHolder.hideSent();
                    }
                    if (conversationsModel.getStatus() == AppConstants.IS_WAITING && !conversationsModel.getUnreadMessageCounter().equals("0")) {
                        conversationViewHolder.ChangeStatusUnread();
                        conversationViewHolder.showCounter();
                        conversationViewHolder.setCounter(conversationsModel.getUnreadMessageCounter());

                    } else {
                        conversationViewHolder.ChangeStatusRead();
                        conversationViewHolder.hideCounter();

                    }
             /*       if (mSocket != null) {
                        if (!conversationsModel.isGroup()) return;
                        mSocket.on(AppConstants.SOCKET_IS_MEMBER_TYPING, args -> mActivity.runOnUiThread(() -> {

                            JSONObject data = (JSONObject) args[0];
                            try {

                                int senderID = data.getInt("senderId");
                                int groupId = data.getInt("groupId");
                                ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", senderID).findFirst();
                                String finalName;
                                if (contactsModel.getUsername() != null) {
                                    finalName = UtilsString.unescapeJava(contactsModel.getUsername());
                                } else {
                                    String name = UtilsPhone.getContactName(mActivity, contactsModel.getPhone());
                                    if (name != null) {
                                        finalName = name;
                                    } else {
                                        finalName = contactsModel.getPhone();
                                    }

                                }
                                if (groupId == conversationsModel.getGroupID()) {
                                    if (senderID == PreferenceManager.getID(mActivity)) return;
                                    conversationViewHolder.lastMessage.setTextColor(AppHelper.getColor(mActivity, R.color.colorGreenVeryDark));
                                    conversationViewHolder.lastMessage.setText(finalName + " " + mActivity.getString(R.string.isTyping));
                                }

                            } catch (Exception e) {
                                AppHelper.LogCat(e);
                            }
                        }));


                        mSocket.on(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING, args -> mActivity.runOnUiThread(() -> {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                int senderID = data.getInt("senderId");
                                if (senderID == PreferenceManager.getID(mActivity)) return;
                                if (conversationsModel.isGroup() || messagesModel.isGroup()) {
                                    if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("image");
                                    } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("video");
                                    } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("audio");
                                    } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("document");
                                    } else {

                                        conversationViewHolder.isFile.setVisibility(View.GONE);
                                        conversationViewHolder.FileContent.setVisibility(View.GONE);
                                        switch (messagesModel.getMessage()) {
                                            case AppConstants.CREATE_GROUP:
                                                if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                                                    if (!conversationsModel.getCreatedOnline()) {
                                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.tap_to_create_group));
                                                    } else {
                                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_created_this_group));
                                                    }

                                                } else {
                                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                    if (name != null) {
                                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_created_this_group));
                                                    } else {
                                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_created_this_group));
                                                    }
                                                }


                                                break;
                                            case AppConstants.LEFT_GROUP:
                                                if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_left));
                                                } else {
                                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                    if (name != null) {
                                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_left));
                                                    } else {
                                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_left));
                                                    }


                                                }

                                                break;
                                            default:
                                                conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                                break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                AppHelper.LogCat("ex member stop typing " + e.getMessage());
                            }

                        }));
                    }*/


                    if (CallManager.isNetworkAvailable(mActivity)) {
                        if (!conversationsModel.getCreatedOnline()) {
                            try {

                                ContactsModel membersGroupModel1 = realm.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(mActivity)).findFirst();
                                MembersGroupModel membersGroupModel = new MembersGroupModel();
                                String role = "admin";
                                membersGroupModel.setUserId(membersGroupModel1.getId());
                                membersGroupModel.setGroupID(conversationsModel.getId());
                                membersGroupModel.setUsername(membersGroupModel1.getUsername());
                                membersGroupModel.setPhone(membersGroupModel1.getPhone());
                                membersGroupModel.setStatus(membersGroupModel1.getStatus());
                                membersGroupModel.setStatus_date(membersGroupModel1.getStatus_date());
                                membersGroupModel.setImage(membersGroupModel1.getImage());
                                membersGroupModel.setRole(role);
                                PreferenceManager.addMember(mActivity, membersGroupModel);
                                StringBuilder ids = new StringBuilder();
                                int arraySize = PreferenceManager.getMembers(mActivity).size();
                                String arrayjson = "";
                                for (int x = 0; x <= arraySize - 1; x++) {
                                    ids.append(PreferenceManager.getMembers(mActivity).get(x).getUserId());
                                    arrayjson += PreferenceManager.getMembers(mActivity).get(x).getUserId();

                                    if(x!=arraySize - 1)
                                      arrayjson += ",";


                                    //ids.append(",");
                                }
                              //  arrayjson += PreferenceManager.getID(mActivity);

                                JSONObject json = new JSONObject();
                                json.put("id", arrayjson);


                                String id = UtilsString.removelastString(ids.toString());
                                AppHelper.LogCat("ids " + id);
                                // create RequestBody instance from file
                                RequestBody requestIds = RequestBody.create(MediaType.parse("multipart/form-data"), id);
                                conversationViewHolder.getProgressBarGroup();
                                ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                                    @Override
                                    protected void onPostExecute(byte[] imageBytes) {
                                        // image here is compressed & ready to be sent to the server
                                        // create RequestBody instance from file
                                        RequestBody requestFile;
                                        if (imageBytes == null)
                                            requestFile = null;
                                        else
                                            requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                                        // create RequestBody instance from file
                                        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), conversationsModel.getRecipientUsername());



                                        if (CallManager.isNetworkAvailable(mActivity)) {

                                            APIHelper.initializeApiGroups().createGroup(PreferenceManager.getID(mActivity), requestName, requestFile, requestIds, conversationsModel.getMessageDate()).subscribe(groupResponse -> {


                                                if (groupResponse.isSuccess()) {
                                                    conversationViewHolder.setProgressBarGroup();
                                                    Realm realm = RooyeshApplication.getRealmDatabaseInstance();
                                                    realm.executeTransaction(realm1 -> {
                                                        ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", conversationsModel.getId()).findFirst();
                                                        conversationsModel1.setCreatedOnline(true);
                                                        conversationsModel1.setGroupID(groupResponse.getGroupID());
                                                      //  conversationsModel.getRecipientUsername();
                                                        try {
                                                            Socket d = MainService.mSocket;
                                                            json.put("groupid", groupResponse.getGroupID());

                                                            String s= conversationsModel.getRecipientUsername();

                                                            json.put("username", s);


                                                            RooyeshApplication app = (RooyeshApplication) mActivity.getApplication();
                                                            mSocket = app.getSocket();

                                                            if (mSocket == null) {
                                                                RooyeshApplication.connectSocket();
                                                                mSocket = app.getSocket();
                                                            }

                                                            if (!mSocket.connected())
                                                                mSocket.connect();



                                                            mSocket.emit(AppConstants.EVENT_ADD_MEMBER_SEND, json);
//                                                    if(MainService.mSocket==null || !MainService.mSocket.connected())
//                                                    {
//                                                        RooyeshApplication.connectSocket();
//                                                        if (!mSocket.connected())
//                                                            mSocket.connect();
//                                                    }
//                                                    else {
//                                                        mSocket.emit(AppConstants.EVENT_ADD_MEMBER_SEND, json);
//                                                    }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        conversationsModel1.setRecipientImage(groupResponse.getGroupImage());
                                                        realm1.copyToRealmOrUpdate(conversationsModel1);


                                                        MessagesModel messagesModel1 = realm1.where(MessagesModel.class).equalTo("conversationID", conversationsModel.getId()).findFirst();
                                                        messagesModel1.setGroup(true);
                                                        messagesModel1.setGroupID(groupResponse.getGroupID());
                                                        realm1.copyToRealmOrUpdate(messagesModel1);

                                                        GroupsModel groupsModel = new GroupsModel();
                                                        groupsModel.setId(groupResponse.getGroupID());
                                                        groupsModel.setMembers(groupResponse.getMembersGroupModels());
                                                        if (groupResponse.getGroupImage() != null)
                                                            groupsModel.setGroupImage(groupResponse.getGroupImage());
                                                        else
                                                            groupsModel.setGroupImage("null");
                                                        groupsModel.setGroupName(conversationsModel.getRecipientUsername());
                                                        groupsModel.setCreatorID(PreferenceManager.getID(mActivity));
                                                        realm1.copyToRealmOrUpdate(groupsModel);
                                                    });
                                                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
                                                    AppHelper.LogCat("group id created 2 e " + groupResponse.getGroupID());
                                                    new Handler().postDelayed(() -> {
                                                        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_NEW_GROUP, groupResponse.getGroupID(), conversationsModel.getId()));
                                                    }, 1000);
                                                    PreferenceManager.clearMembers(mActivity);
                                                    realm.close();
                                                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.main_activity), groupResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                                                    AppHelper.CustomToast(mActivity, groupResponse.getMessage());
                                                } else {
                                                    conversationViewHolder.setProgressBarGroup();
                                                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.main_activity), groupResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                                                    AppHelper.CustomToast(mActivity, groupResponse.getMessage());
                                                }
                                            });
                                        } else {
                                            Toast.makeText(mActivity, "اشکالی در ارتباط با شبکه بوجود آمده است", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                };

                                imageCompression.execute(conversationsModel.getRecipientImage());
                            } catch (Exception e) {
                                AppHelper.LogCat("execption  ids " + e.getMessage());
                            }
                        }
                    } else {

                        if (!conversationsModel.getCreatedOnline()) {
                            conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                        } else {
                            conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                            if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("image");
                            } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("video");
                            } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("audio");
                            } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("document");
                            } else {
                                conversationViewHolder.isFile.setVisibility(View.GONE);
                                conversationViewHolder.FileContent.setVisibility(View.GONE);
                                if (conversationsModel.getLastMessage() != null)
                                    conversationViewHolder.setLastMessage(conversationsModel.getLastMessage());
                                else
                                    conversationViewHolder.setLastMessage(messagesModel.getMessage());
                            }

                            if (messagesModel.getDate() != null) {
                                DateTime messageDate = UtilsTime.getCorrectDate(messagesModel.getDate());
                                String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                                conversationViewHolder.setMessageDate(finalDate);
                            } else {
                                DateTime messageDate = UtilsTime.getCorrectDate(conversationsModel.getMessageDate());
                                String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                                conversationViewHolder.setMessageDate(finalDate);
                            }
                        }


                        if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                            conversationViewHolder.showSent(messagesModel.getStatus());
                        } else {
                            conversationViewHolder.hideSent();
                        }
                 /*   if (mSocket != null) {
                        if (conversationsModel.isGroup()) return;
                        mSocket.on(AppConstants.SOCKET_IS_TYPING, args -> mActivity.runOnUiThread(() -> {

                            JSONObject data = (JSONObject) args[0];
                            try {

                                int senderID = data.getInt("senderId");
                                int recipientID = data.getInt("recipientId");
                                if (senderID == messagesModel.getSenderID() && recipientID == messagesModel.getRecipientID() && (!messagesModel.isGroup() || !conversationsModel.isGroup())) {
                                    conversationViewHolder.lastMessage.setTextColor(AppHelper.getColor(mActivity, R.color.colorGreenVeryDark));
                                    conversationViewHolder.lastMessage.setText(mActivity.getString(R.string.isTyping));
                                }

                            } catch (Exception e) {
                                AppHelper.LogCat(e);
                            }
                        }));

                        mSocket.on(AppConstants.SOCKET_IS_STOP_TYPING, args -> mActivity.runOnUiThread(() -> {
                            try {
                                JSONObject data = (JSONObject) args[0];
                                int senderID = data.getInt("senderId");
                                if (senderID == messagesModel.getSenderID()) {
                                    if (!conversationsModel.isGroup() || !messagesModel.isGroup()) {
                                        if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                            conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                            conversationViewHolder.setTypeFile("image");
                                        } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                            conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                            conversationViewHolder.setTypeFile("video");
                                        } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                            conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                            conversationViewHolder.setTypeFile("audio");
                                        } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                            conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                            conversationViewHolder.setTypeFile("document");
                                        } else {
                                            conversationViewHolder.isFile.setVisibility(View.GONE);
                                            conversationViewHolder.FileContent.setVisibility(View.GONE);
                                            if (messagesModel.getMessage() != null) {
                                                conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                            } else {
                                                conversationViewHolder.setLastMessage(conversationsModel.getLastMessage());
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                AppHelper.LogCat("stop typing");
                            }
                        }));
                    }*/

                        if (conversationsModel.getStatus() == AppConstants.IS_WAITING && !conversationsModel.getUnreadMessageCounter().equals("0")) {
                            conversationViewHolder.ChangeStatusUnread();
                            conversationViewHolder.showCounter();
                            conversationViewHolder.setCounter(conversationsModel.getUnreadMessageCounter());

                        } else {
                            conversationViewHolder.ChangeStatusRead();
                            conversationViewHolder.hideCounter();

                        }
                        conversationViewHolder.setUserImage(conversationsModel.getRecipientImage(), conversationsModel.getRecipientID());


                    }
                }

                else
                {
                    Toast.makeText(mActivity,"اتصال شما به شبکه برقرار نیست",Toast.LENGTH_LONG).show();
                }


                conversationViewHolder.setOnClickListener(view -> {
                   // mSelectedItem = position;



                    if (!isActivated) {
                      //  if (conversationsModel.isValid())
                            if (conversationsModel.isGroup()) {
                                if (!conversationsModel.getCreatedOnline()) {
                                    try {

                                        ContactsModel membersGroupModel1 = realm.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(mActivity)).findFirst();
                                        MembersGroupModel membersGroupModel = new MembersGroupModel();
                                        String role = "admin";
                                        membersGroupModel.setUserId(membersGroupModel1.getId());
                                        membersGroupModel.setGroupID(conversationsModel.getId());
                                        membersGroupModel.setUsername(membersGroupModel1.getUsername());
                                        membersGroupModel.setPhone(membersGroupModel1.getPhone());
                                        membersGroupModel.setStatus(membersGroupModel1.getStatus());
                                        membersGroupModel.setStatus_date(membersGroupModel1.getStatus_date());
                                        membersGroupModel.setImage(membersGroupModel1.getImage());
                                        membersGroupModel.setRole(role);
                                        PreferenceManager.addMember(mActivity, membersGroupModel);
                                        StringBuilder ids = new StringBuilder();
                                        int arraySize = PreferenceManager.getMembers(mActivity).size();
                                        for (int x = 0; x <= arraySize - 1; x++) {
                                            ids.append(PreferenceManager.getMembers(mActivity).get(x).getUserId());
                                            ids.append(",");
                                        }
                                        String id = UtilsString.removelastString(ids.toString());
                                        // create RequestBody instance from file
                                        RequestBody requestIds =
                                                RequestBody.create(MediaType.parse("multipart/form-data"), id);
                                        conversationViewHolder.getProgressBarGroup();
                                        ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                                            @Override
                                            protected void onPostExecute(byte[] imageBytes) {
                                                // image here is compressed & ready to be sent to the server
                                                // create RequestBody instance from file
                                                RequestBody requestFile;
                                                if (imageBytes == null)
                                                    requestFile = null;
                                                else
                                                    requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                                                // create RequestBody instance from file
                                                RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), conversationsModel.getRecipientUsername());
                                                APIHelper.initializeApiGroups().createGroup(PreferenceManager.getID(mActivity), requestName, requestFile, requestIds, conversationsModel.getMessageDate()).subscribe(groupResponse -> {
                                                    if (groupResponse.isSuccess()) {
                                                        conversationViewHolder.setProgressBarGroup();
                                                        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
                                                        realm.executeTransaction(realm1 -> {
                                                            ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", conversationsModel.getId()).findFirst();
                                                            conversationsModel1.setCreatedOnline(true);
                                                            conversationsModel1.setGroupID(groupResponse.getGroupID());
                                                            conversationsModel1.setRecipientImage(groupResponse.getGroupImage());
                                                            realm1.copyToRealmOrUpdate(conversationsModel1);


                                                            MessagesModel messagesModel1 = realm1.where(MessagesModel.class).equalTo("conversationID", conversationsModel.getId()).findFirst();
                                                            messagesModel1.setGroup(true);
                                                            messagesModel1.setGroupID(groupResponse.getGroupID());
                                                            realm1.copyToRealmOrUpdate(messagesModel1);

                                                            GroupsModel groupsModel = new GroupsModel();
                                                            groupsModel.setId(groupResponse.getGroupID());
                                                            groupsModel.setMembers(groupResponse.getMembersGroupModels());
                                                            if (groupResponse.getGroupImage() != null)
                                                                groupsModel.setGroupImage(groupResponse.getGroupImage());
                                                            else
                                                                groupsModel.setGroupImage("null");
                                                            groupsModel.setGroupName(conversationsModel.getRecipientUsername());
                                                            groupsModel.setCreatorID(PreferenceManager.getID(mActivity));
                                                            realm1.copyToRealmOrUpdate(groupsModel);
                                                        });
                                                        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
                                                        AppHelper.LogCat("group id created 2 e " + groupResponse.getGroupID());
                                                        new Handler().postDelayed(() -> {
                                                            EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_CREATE_NEW_GROUP, groupResponse.getGroupID(), conversationsModel.getId()));
                                                        }, 1000);
                                                        PreferenceManager.clearMembers(mActivity);
                                                        realm.close();
                                                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.main_activity), groupResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                                                        AppHelper.CustomToast(mActivity, groupResponse.getMessage());
                                                    } else {
                                                        conversationViewHolder.setProgressBarGroup();
                                                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.main_activity), groupResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                                                        AppHelper.CustomToast(mActivity, groupResponse.getMessage());
                                                    }
                                                });
                                            }
                                        };
                                        imageCompression.execute(conversationsModel.getRecipientImage());
                                    } catch (Exception e) {
                                        AppHelper.LogCat("execption  ids " + e.getMessage());
                                    }
                                } else
                                    {
                                    if (view.getId() == R.id.user_image) {
                                        if (AppHelper.isAndroid5()) {
                                            Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                            mIntent.putExtra("conversationID", conversationsModel.getId());
                                            mIntent.putExtra("groupID", conversationsModel.getGroupID());
                                            mIntent.putExtra("isGroup", conversationsModel.isGroup());
                                            mIntent.putExtra("userId", messagesModel.getRecipientID());
                                            mActivity.startActivity(mIntent);
                                        } else {
                                            Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                            mIntent.putExtra("conversationID", conversationsModel.getId());
                                            mIntent.putExtra("groupID", conversationsModel.getGroupID());
                                            mIntent.putExtra("isGroup", conversationsModel.isGroup());
                                            mIntent.putExtra("userId", messagesModel.getRecipientID());
                                            mActivity.startActivity(mIntent);
                                            mActivity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                        }
                                    } else {


                                        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                                      /*  Bundle bundle = new Bundle();
                                        bundle.putInt("conversationID", conversationsModel.getId());
                                        bundle.putBoolean("isGroup", true);
                                        bundle.putInt("groupID", conversationsModel.getGroupID());
                                        bundle.putInt("recipientID", conversationsModel.getRecipientID());
                                        MessagesFragment messageFragment = new MessagesFragment();
                                        messageFragment.setArguments(bundle);*/


                                        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                                      //  mCommunicator.respond(conversationsModel.getId(),conversationsModel.getGroupID(),conversationsModel.getRecipientID(),true);
                                        mListener.onItemClick(conversationsModel.getId(),conversationsModel.getGroupID(),conversationsModel.getRecipientID(),true , conversationsModel.getRecipientUsername(),position);
                                        Log.d("mmm","conversationID  "+conversationsModel.getId() +"//// groupID  " + conversationsModel.getGroupID() + "   /////  recipientID  " +conversationsModel.getRecipientID()+ "  //// isGroup " + true);







                                   /*     RateHelper.significantEvent(mActivity);
                                        Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                                        messagingIntent.putExtra("conversationID", conversationsModel.getId());
                                        messagingIntent.putExtra("groupID", conversationsModel.getGroupID());
                                        messagingIntent.putExtra("isGroup", true);
                                        messagingIntent.putExtra("recipientID", conversationsModel.getRecipientID());
                                        if(mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                                            //Do some stuff

                                            mActivity.startActivity(messagingIntent);
                                            AnimationsUtil.setSlideInAnimation(mActivity);
                                        }*//*else {
                                            RateHelper.significantEvent(mActivity);
                                            Intent messagingIntent = new Intent(mActivity, MainActivity.class);
                                            messagingIntent.putExtra("conversationID", conversationsModel.getId());
                                            messagingIntent.putExtra("groupID", conversationsModel.getGroupID());
                                            messagingIntent.putExtra("isGroup", true);
                                            messagingIntent.putExtra("recipientID", conversationsModel.getRecipientID());
                                            mActivity.startActivity(messagingIntent);
                                            AnimationsUtil.setSlideInAnimation(mActivity);
                                        }*/

                                    }
                                }

                            } else {
                                if (view.getId() == R.id.user_image) {

                                    if (AppHelper.isAndroid5()) {
                                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                        mIntent.putExtra("userID", conversationsModel.getRecipientID());
                                        mIntent.putExtra("isGroup", false);
                                        mActivity.startActivity(mIntent);
                                    } else {
                                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                        mIntent.putExtra("userID", conversationsModel.getRecipientID());
                                        mIntent.putExtra("isGroup", false);
                                        mActivity.startActivity(mIntent);
                                        mActivity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                    }
                                } else {

                                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                                 /*   Bundle bundle = new Bundle();
                                    bundle.putInt("conversationID", conversationsModel.getId());
                                    bundle.putBoolean("isGroup", false);
                                    bundle.putInt("recipientID", conversationsModel.getRecipientID());
                                    MessagesFragment messageFragmentOk = new MessagesFragment();
                                    messageFragmentOk.setArguments(bundle);*/


                                  //  mCommunicator.respond(conversationsModel.getId(),-1,conversationsModel.getRecipientID(),false);
                                    mListener.onItemClick(conversationsModel.getId(),-1,conversationsModel.getRecipientID(),false, conversationsModel.getRecipientUsername(),position);
                                    Log.d("mmm","conversationID2  "+conversationsModel.getId() +"//// groupID2  " + -1 + "   /////  recipientID2  " +conversationsModel.getRecipientID()+ "  //// isGroup2 " + false);
                                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                                 /*   RateHelper.significantEvent(mActivity);
                                    Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                                    messagingIntent.putExtra("conversationID", conversationsModel.getId());
                                    messagingIntent.putExtra("groupID", conversationsModel.getGroupID());
                                    messagingIntent.putExtra("isGroup", true);
                                    messagingIntent.putExtra("recipientID", conversationsModel.getRecipientID());
                                    if(mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                                        //Do some stuff

                                        mActivity.startActivity(messagingIntent);
                                        AnimationsUtil.setSlideInAnimation(mActivity);
                                    }*//*else {
                                        RateHelper.significantEvent(mActivity);
                                        Intent messagingIntent = new Intent(mActivity, MainActivity.class);
                                        messagingIntent.putExtra("conversationID", conversationsModel.getId());
                                        messagingIntent.putExtra("groupID", conversationsModel.getGroupID());
                                        messagingIntent.putExtra("isGroup", true);
                                        messagingIntent.putExtra("recipientID", conversationsModel.getRecipientID());
                                        mActivity.startActivity(messagingIntent);
                                        AnimationsUtil.setSlideInAnimation(mActivity);
                                    }*/
                                }
                            }
                    } else {
                        if (conversationsModel.isGroup()) {
                            AppHelper.LogCat("This is a group you cannot delete this conversation now");
                        } else {
                            EventBus.getDefault().post(new Pusher(EVENT_BUS_ITEM_IS_ACTIVATED, view));
                        }

                    }


                });
            } catch (Exception e) {
                AppHelper.LogCat("Conversations Adapter  Exception" + e.getMessage());
            }

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.itemView.isActivated()) {

                final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        conversationViewHolder.selectIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                conversationViewHolder.selectIcon.startAnimation(animation);
            } else {


                final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_for_button_animtion_exit);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        conversationViewHolder.selectIcon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                conversationViewHolder.selectIcon.startAnimation(animation);
            }
            //conversationViewHolder.userImage.setTag(conversationsModel);
        }
    }

    @Override
    public int getItemCount() {
        if (mConversations != null) return mConversations.size();
        return 0;
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {

            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
            if (!isActivated)
                isActivated = true;

        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        if (isActivated)
            isActivated = false;
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        int arraySize = selectedItems.size();
        for (int i = 0; i < arraySize; i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public ConversationsModel getItem(int position) {
        return mConversations.get(position);
    }


    /**
     * method to check if a  conversation exist
     *
     * @param conversationId this is the first parameter for  checkIfGroupConversationExist method
     * @param realm          this is the second parameter for  checkIfGroupConversationExist  method
     * @return return value
     */
    private boolean checkIfConversationExist(int conversationId, Realm realm) {
        RealmQuery<ConversationsModel> query = realm.where(ConversationsModel.class).equalTo("id", conversationId);
        return query.count() != 0;

    }

    public void addConversationItem(int conversationId) {
        try {
            Realm realm = RooyeshApplication.getRealmDatabaseInstance();
            ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("id", conversationId).findFirst();
            if (!isConversationExistInList(conversationsModel.getId())) {
                addConversationItem(0, conversationsModel);
            } else {
                return;
            }
            realm.close();

        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
    }

    private boolean isConversationExistInList(int conversationId) {
        int arraySize = mConversations.size();
        boolean conversationExist = false;
        for (int i = 0; i < arraySize; i++) {
            ConversationsModel model = mConversations.get(i);
            if (conversationId == model.getId()) {
                conversationExist = true;
                break;
            }
        }
        return conversationExist;
    }

    private void addConversationItem(int position, ConversationsModel conversationsModel) {
        // if (position != 0) {
        try {
            this.mConversations.add(position, conversationsModel);
            notifyItemInserted(position);
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
        // }
    }

    public void removeConversationItem(int position) {
        //if (position != 0) {
        try {
            mConversations.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
        //  }
    }

    public void DeleteConversationItem(int ConversationID) {
        try {
            int arraySize = mConversations.size();
            for (int i = 0; i < arraySize; i++) {
                ConversationsModel model = mConversations.get(i);
                if (model.isValid()) {
                    if (ConversationID == model.getId()) {
                        removeConversationItem(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
    }

    public void updateStatusConversationItem(int ConversationID) {
        try {
            Realm realm = RooyeshApplication.getRealmDatabaseInstance();
            int arraySize = mConversations.size();
            for (int i = 0; i < arraySize; i++) {
                ConversationsModel model = mConversations.get(i);
                try {
                    if (ConversationID == model.getId()) {
                        ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                        changeItemAtPosition(i, conversationsModel);
                        break;
                    }
                } catch (Exception e) {
                    AppHelper.LogCat(e);
                }


            }
            realm.close();
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
    }

    public void updateConversationItem(int ConversationID) {
        try {
            Realm realm = RooyeshApplication.getRealmDatabaseInstance();
            int arraySize = mConversations.size();
            for (int i = 0; i < arraySize; i++) {
                ConversationsModel model = mConversations.get(i);
                if (ConversationID == model.getId()) {
                    ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                    changeItemAtPosition(i, conversationsModel);
                    if (i != 0)
                        MoveItemToPosition(i, 0);
                    break;
                }

            }
            realm.close();
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
    }

    private void changeItemAtPosition(int position, ConversationsModel conversationsModel) {
        mConversations.set(position, conversationsModel);
        notifyItemChanged(position);
    }

    private void MoveItemToPosition(int fromPosition, int toPosition) {
        ConversationsModel model = mConversations.remove(fromPosition);
        mConversations.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
        conversationList.scrollToPosition(fromPosition);
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        Context context;
        @BindView(R.id.user_image)
        ImageView userImage;

        @BindView(R.id.username)
        EmojiconTextView username;

        @BindView(R.id.last_message)
        EmojiconTextView lastMessage;

        @BindView(R.id.counter)
        TextView counter;

        @BindView(R.id.date_message)
        TextView messageDate;

        @BindView(R.id.status_messages)
        ImageView status_messages;
        @BindView(R.id.file_types)
        ImageView isFile;
        @BindView(R.id.file_types_text)
        TextView FileContent;

        @BindView(R.id.create_group_pro_bar)
        ProgressBar progressBarGroup;

        @BindView(R.id.conversation_row)
        LinearLayout ConversationRow;


        @BindView(R.id.select_icon)
        LinearLayout selectIcon;





        ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            setTypeFaces();
        }

        private void setTypeFaces() {
            if (AppConstants.ENABLE_FONTS_TYPES) {
                username.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                lastMessage.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                counter.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                messageDate.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                FileContent.setTypeface(AppHelper.setTypeFace(context, "IranSans"));

            }
        }

        void getProgressBarGroup() {
            progressBarGroup.setVisibility(View.VISIBLE);
        }

        void setProgressBarGroup() {
            progressBarGroup.setVisibility(View.GONE);
        }

        @SuppressLint("SetTextI18n")
        void setTypeFile(String type) {
            isFile.setVisibility(View.VISIBLE);
            FileContent.setVisibility(View.VISIBLE);
            switch (type) {
                case "image":
                    isFile.setImageResource(R.drawable.ic_photo_camera_gray_24dp);
                    FileContent.setText("Image");
                    break;
                case "video":
                    isFile.setImageResource(R.drawable.ic_videocam_gray_24dp);
                    FileContent.setText("Video");
                    break;
                case "audio":
                    isFile.setImageResource(R.drawable.ic_headset_gray_24dp);
                    FileContent.setText("Audio");
                    break;
                case "document":
                    isFile.setImageResource(R.drawable.ic_document_file_gray_24dp);
                    FileContent.setText("Document");
                    break;
            }

        }

        void setGroupImageOffline(String ImageUrl) {
            RooyeshImageLoader.loadCircleImageGroup(context, ImageUrl, userImage, R.drawable.image_holder_gr_circle, AppConstants.ROWS_IMAGE_SIZE);
        }

        void setGroupImage(String ImageUrl, int groupId) {
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, context, groupId, AppConstants.GROUP, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {
                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ROWS_IMAGE_URL + ImageUrl, ImageUrl, context, groupId, AppConstants.GROUP, AppConstants.ROW_PROFILE);

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
                RooyeshImageLoader.loadCircleImageGroup(context, EndPoints.ROWS_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_gr_circle, AppConstants.ROWS_IMAGE_SIZE);
            }

        }

        void setUserImage(String ImageUrl, int recipientId) {
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, context, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {

                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ROWS_IMAGE_URL + ImageUrl, ImageUrl, context, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);

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
                RooyeshImageLoader.loadCircleImage(context, EndPoints.ROWS_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
            }
        }

        void setUsername(String user) {

            if (user.length() > 16)
                username.setText(user.substring(0, 16) + "... " + "");
            else
                username.setText(user);

        }

        void setLastMessage(String LastMessage) {
            lastMessage.setVisibility(View.VISIBLE);
            lastMessage.setTextColor(AppHelper.getColor(context, R.color.colorGray2));
            String last = UtilsString.unescapeJava(LastMessage);
            if (last.length() > 18)
                lastMessage.setText(last.substring(0, 18) + "... " + "");
            else
                lastMessage.setText(last);

        }

        void setMessageDate(String MessageDate) {
            messageDate.setText(MessageDate);
        }

        void hideSent() {
            status_messages.setVisibility(View.GONE);
        }

        void showSent(int status) {
            status_messages.setVisibility(View.VISIBLE);
            switch (status) {
                case AppConstants.IS_WAITING:
                    status_messages.setImageResource(R.drawable.ic_access_time_gray_24dp);
                    break;
                case AppConstants.IS_SENT:
                    status_messages.setImageResource(R.drawable.ic_done_gray_24dp);
                    break;
                case AppConstants.IS_DELIVERED:
                    status_messages.setImageResource(R.drawable.ic_done_all_gray_24dp);
                    break;
                case AppConstants.IS_SEEN:
                    status_messages.setImageResource(R.drawable.ic_done_all_blue_24dp);
                    break;

            }

        }

        void setCounter(String Counter) {
            counter.setText(Counter.toUpperCase());
        }

        void hideCounter() {
            counter.setVisibility(View.GONE);
        }


        void showCounter() {
            counter.setVisibility(View.VISIBLE);
        }

        void ChangeStatusUnread() {
            messageDate.setTypeface(null, Typeface.BOLD);
            username.setTypeface(null, Typeface.BOLD);
            if (AppConstants.ENABLE_FONTS_TYPES)
                username.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
            messageDate.setTextColor(ContextCompat.getColor(context, R.color.colorAccentSecondary));
        }

        void ChangeStatusRead() {
            messageDate.setTypeface(null, Typeface.NORMAL);
            username.setTypeface(null, Typeface.BOLD);
            if (AppConstants.ENABLE_FONTS_TYPES)
                username.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
            messageDate.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
            username.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));

        }

        void setOnClickListener(View.OnClickListener listener) {

            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }





    }





}
