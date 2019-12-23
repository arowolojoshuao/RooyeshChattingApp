package com.setayeshco.rooyesh.fragments.home;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.adapters.recyclerView.messages.ConversationsAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.interfaces.FragmentCommunication;
import com.setayeshco.rooyesh.interfaces.LoadingData;
import com.setayeshco.rooyesh.interfaces.OnItemClickListener;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.presenters.messages.ConversationsPresenter;
import com.setayeshco.rooyesh.services.MainService;
import com.setayeshco.rooyesh.ui.CustomProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.socket.client.Socket;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_ACTION_MODE_FINISHED;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_CONTACTS_PERMISSION;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_CREATE_NEW_GROUP;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_DELETE_CONVERSATION_ITEM;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_EXIT_NEW_GROUP;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_IMAGE_GROUP_UPDATED;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_ITEM_IS_ACTIVATED;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_READ;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW;

/**
 * Created by Abderrahim El imame  on 20/01/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ConversationsFragment extends Fragment implements LoadingData, RecyclerView.OnItemTouchListener, ActionMode.Callback {




    FragmentCommunication communication;
    @BindView(R.id.ConversationsList)
    RecyclerView ConversationList;
    @BindView(R.id.empty)
    LinearLayout emptyConversations;

    @BindView(R.id.swipeConversations)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.progress_bar_load)
    CustomProgressView progressBarLoad;

    private ConversationsAdapter mConversationsAdapter;
    private NotificationsManager notificationsManager;
    private ConversationsPresenter mConversationsPresenter;
    private GestureDetectorCompat gestureDetector;
    private ActionMode actionMode;
    private Socket mSocket;

    private View mView;
    ImageView toolbar_image ;
    EmojiconTextView toolbar_title ;

    int conversationID1;
    int groupID1;
    int recipientID1;
    boolean isGroup1;

    private UsersService mUsersContacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_conversations, container, false);
        ButterKnife.bind(this, mView);
        initializerView();
        mConversationsPresenter = new ConversationsPresenter(this);
        mConversationsPresenter.onCreate();
        notificationsManager = new NotificationsManager();
        return mView;
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        connectToChatServer();
        setHasOptionsMenu(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Configuration configuration = getActivity().getResources().getConfiguration();
        mConversationsAdapter = new ConversationsAdapter(mSocket, ConversationList, new OnItemClickListener() {
            @Override
            public void onItemClick(int conversationID, int groupID, int recipientID, boolean isGroup ,String userName , int position) {
                if (configuration.smallestScreenWidthDp >= 600){

                    toolbar_image.setVisibility(View.VISIBLE);

                }

               /* for (int i = 0; i < ConversationList.getChildCount(); i++) {
                    if(position == i ){
                        ConversationList.getChildAt(i).setBackgroundColor(Color.BLUE);
                    }else{
                        ConversationList.getChildAt(i).setBackgroundColor(Color.RED);
                    }
                }*/
                for (int i = 0; i < ConversationList.getChildCount(); i++) {
                    if(position == i ){

                        // view.setBackgroundColor(Color.parseColor("#B6B6B6"));
                        ConversationList.getChildAt(i).setBackgroundColor(Color.parseColor("#DADADA"));
                    }else{
                        ConversationList.getChildAt(i).setBackgroundColor(Color.WHITE);
                        //  view.setBackgroundColor(Color.WHITE);

                    }
                }


                if (userName != null && userName != "" && !userName.equals("null") ) {

                    //Configuration configuration = getActivity().getResources().getConfiguration();
                    if (configuration.smallestScreenWidthDp >= 600){
                        toolbar_title.setText(userName + "");
                    }


                }
             //   if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                if((getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                        Configuration.SCREENLAYOUT_SIZE_LARGE  || (getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                        Configuration.SCREENLAYOUT_SIZE_XLARGE ){
                    //Do some stuff
                    MessagesFragment messagesFragment = new MessagesFragment();
                    Bundle bundle=new Bundle();
                    bundle.putInt("conversationID",conversationID );
                    bundle.putBoolean("isGroup", isGroup);
                    if (groupID != -1) {
                        bundle.putInt("groupID", groupID);
                    }
                    bundle.putInt("recipientID", recipientID);
                    messagesFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction1 =fragmentManager1.beginTransaction();
                    //  fragmentTransaction1.add(R.id.fragment_messages_main,messagesFragment);
                    fragmentTransaction1.addToBackStack("");
                    fragmentTransaction1.replace(R.id.fragment_messages_main,messagesFragment);
                    fragmentTransaction1.commit();


                }else {
                    ConversationList.addOnItemTouchListener(ConversationsFragment.this);
                    RateHelper.significantEvent(getActivity());
                    Intent messagingIntent = new Intent(getActivity(), MessagesActivity.class);
                    messagingIntent.putExtra("conversationID",conversationID);
                    if (groupID != -1) {
                        messagingIntent.putExtra("groupID", groupID);
                    }
                    messagingIntent.putExtra("isGroup", isGroup);
                    messagingIntent.putExtra("recipientID", recipientID);
                    getActivity().startActivity(messagingIntent);
                    AnimationsUtil.setSlideInAnimation(getActivity());
                }


            }
        });
        ConversationList.setLayoutManager(mLinearLayoutManager);
        ConversationList.setAdapter(mConversationsAdapter);
        ConversationList.setItemAnimator(new DefaultItemAnimator());
        ConversationList.getItemAnimator().setChangeDuration(0);

        //fix slow recyclerview start
        ConversationList.setHasFixedSize(true);
        ConversationList.setItemViewCacheSize(10);
        ConversationList.setDrawingCacheEnabled(true);
        ConversationList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ///fix slow recyclerview end
        //ConversationList.addOnItemTouchListener(this);
     //   if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
           /* ConversationList.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), ConversationList ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            // do whatever
                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                //Do some stuff
                                MessagesFragment messagesFragment = new MessagesFragment();
                                Bundle bundle=new Bundle();
                                bundle.putInt("conversationID",conversationID1 );
                                bundle.putBoolean("isGroup", isGroup1);
                                if (groupID1 != -1) {
                                    bundle.putInt("groupID", groupID1);
                                }
                                bundle.putInt("recipientID", recipientID1);
                                messagesFragment.setArguments(bundle);
                                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction1 =fragmentManager1.beginTransaction();
                                //  fragmentTransaction1.add(R.id.fragment_messages_main,messagesFragment);
                                fragmentTransaction1.replace(R.id.fragment_messages_main,messagesFragment);
                                fragmentTransaction1.commit();


                            }

                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever

                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                //Do some stuff
                                MessagesFragment messagesFragment = new MessagesFragment();
                                Bundle bundle=new Bundle();
                                bundle.putInt("conversationID",conversationID1 );
                                bundle.putBoolean("isGroup", isGroup1);
                                if (groupID1 != -1) {
                                    bundle.putInt("groupID", groupID1);
                                }
                                bundle.putInt("recipientID", recipientID1);
                                messagesFragment.setArguments(bundle);
                                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction1 =fragmentManager1.beginTransaction();
                                //  fragmentTransaction1.add(R.id.fragment_messages_main,messagesFragment);
                                fragmentTransaction1.replace(R.id.fragment_messages_main,messagesFragment);
                                fragmentTransaction1.commit();


                            }
                        }
                    })
            );*/
      //  }


      /*  ConversationList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new OnItemClickListener (){
                    @Override
                    public void onItemClick(int conversationID, int groupID, int recipientID, boolean isGroup) {

                    }
                })
        );*/
        gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewBenOnGestureListener());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorGreenLight);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mConversationsPresenter.onRefresh());

    }

    private void connectToChatServer() {
        RooyeshApplication app = (RooyeshApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        if (mSocket == null) {
            RooyeshApplication.connectSocket();
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            if (!mSocket.connected())
                mSocket.connect();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Configuration configuration = getActivity().getResources().getConfiguration();
        if (configuration.smallestScreenWidthDp >= 600){
            toolbar_image =(ImageView) getActivity().findViewById(R.id.toolbar_image);
          //  toolbar_image.setVisibility(View.VISIBLE);
              }


        toolbar_title =(EmojiconTextView) getActivity().findViewById(R.id.toolbar_title);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    /**
     * method to toggle the selection
     *
     * @param position
     */
    private void ToggleSelection(int position) {
        mConversationsAdapter.toggleSelection(position);
        String title = String.format("%s selected", mConversationsAdapter.getSelectedItemCount());
        actionMode.setTitle(title);


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.select_conversation_menu, menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ACTION_MODE_STARTED));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


        switch (item.getItemId()) {
            case R.id.delete_conversations:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                builder.setMessage(R.string.alert_message_delete_conversation);

                builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
                    int arraySize = mConversationsAdapter.getSelectedItems().size();
                    Realm realm = RooyeshApplication.getRealmDatabaseInstance();
                    AppHelper.LogCat("start delete " + arraySize);
                    if (arraySize != 0) {

                        AppHelper.showDialog(getActivity(), getString(R.string.deleting_chat));

                        for (int x = 0; x < arraySize; x++) {
                            int currentPosition = mConversationsAdapter.getSelectedItems().get(x);
                            try {
                                ConversationsModel conversationsModel = mConversationsAdapter.getItem(currentPosition);
                                int conversationID = getConversationId(conversationsModel.getRecipientID(), PreferenceManager.getID(getActivity()), realm);
                                realm.executeTransactionAsync(realm1 -> {
                                    RealmResults<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).equalTo("conversationID", conversationID).findAll();
                                    messagesModel1.deleteAllFromRealm();
                                }, () -> {
                                    AppHelper.LogCat("Message Deleted  successfully  ConversationsFragment");
                                    mConversationsAdapter.removeConversationItem(currentPosition);
                                    realm.executeTransactionAsync(realm1 -> {
                                        ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", conversationID).findFirst();
                                        conversationsModel1.deleteFromRealm();
                                    }, () -> {
                                        AppHelper.LogCat("Conversation deleted successfully ConversationsFragment");
                                        EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
                                        EventBus.getDefault().post(new Pusher(EVENT_BUS_DELETE_CONVERSATION_ITEM, conversationID));
                                        notificationsManager.SetupBadger(getActivity());
                                    }, error -> {
                                        AppHelper.LogCat("Delete conversation failed  ConversationsFragment" + error.getMessage());

                                    });
                                }, error -> {
                                    AppHelper.LogCat("Delete message failed ConversationsFragment" + error.getMessage());

                                });

                            } catch (Exception e) {
                                AppHelper.LogCat(e);
                            }
                        }
                        AppHelper.LogCat("finish delete");
                        AppHelper.hideDialog();
                    } else {
                        AppHelper.CustomToast(getActivity(), "Delete conversation failed  ");
                    }
                    if (actionMode != null) {
                        mConversationsAdapter.clearSelections();
                        actionMode.finish();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }
                    realm.close();
                });


                builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {

                });

                builder.show();
                return true;
            default:
                return false;
        }
    }

    /**
     * method to get a conversation id
     *
     * @param recipientId this is the first parameter for getConversationId method
     * @param senderId    this is the second parameter for getConversationId method
     * @param realm       this is the thirded parameter for getConversationId method
     * @return conversation id
     */
    private int getConversationId(int recipientId, int senderId, Realm realm) {
        try {
            ConversationsModel conversationsModelNew = realm.where(ConversationsModel.class)
                    .beginGroup()
                    .equalTo("RecipientID", recipientId)
                    .or()
                    .equalTo("RecipientID", senderId)
                    .endGroup().findAll().first();
            return conversationsModelNew.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception MainService" + e.getMessage());
            return 0;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        mConversationsAdapter.clearSelections();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_ACTION_MODE_DESTROYED));
    }

    public void sendGroupMessage(GroupsModel groupsModel, MessagesModel messagesModel) {
        Realm realmCreateGroup = RooyeshApplication.getRealmDatabaseInstance();
        ContactsModel contactsModel = realmCreateGroup.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(getActivity())).findFirst();
        new Handler().postDelayed(() -> MainService.sendMessagesGroup(getActivity(), contactsModel, groupsModel, messagesModel), 500);
        realmCreateGroup.close();
    }

    public void sendGroupMessage(GroupsModel groupsModel, int conversationID) {
        new Handler().postDelayed(() -> {
            Realm realmCreateGroup = RooyeshApplication.getRealmDatabaseInstance();
            ContactsModel contactsModel = realmCreateGroup.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(getActivity())).findFirst();
            MessagesModel messagesModel = realmCreateGroup.where(MessagesModel.class).notEqualTo("id", 0).equalTo("conversationID", conversationID).equalTo("isGroup", true).findFirst();
            MainService.sendMessagesGroup(getActivity(), contactsModel, groupsModel, messagesModel);
            realmCreateGroup.close();
        }, 500);

    }


    private class RecyclerViewBenOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            try {
                View view = ConversationList.findChildViewUnder(e.getX(), e.getY());
                int currentPosition = ConversationList.getChildAdapterPosition(view);
                ConversationsModel conversationsModel = mConversationsAdapter.getItem(currentPosition);
                if (!conversationsModel.isGroup()) {
                    if (actionMode != null) {
                        return;
                    }
                    actionMode = getActivity().startActionMode(ConversationsFragment.this);
                    ToggleSelection(currentPosition);
                }
                super.onLongPress(e);
            } catch (Exception e1) {
                AppHelper.LogCat(" onLongPress " + e1.getMessage());
            }


        }

    }

    public void onProgressShow() {
        progressBarLoad.setVisibility(View.VISIBLE);
        progressBarLoad.setColor(AppHelper.getColor(getActivity(), R.color.colorPrimaryDark));
    }

    public void onProgressHide() {
        progressBarLoad.setVisibility(View.GONE);
    }

    /**
     * method to show conversation list
     *
     * @param conversationsModels this is parameter for  ShowConversation  method
     */
    public void UpdateConversation(List<ConversationsModel> conversationsModels) {
        int  senderId = PreferenceManager.getID(getContext());
        // TODO: 19/04/2018 Inja
        if (conversationsModels.size() != 0) {
            ConversationList.setVisibility(View.VISIBLE);
            emptyConversations.setVisibility(View.GONE);
            RealmList<ConversationsModel> conversationsModels1 = new RealmList<ConversationsModel>();
            for (ConversationsModel conversationsModel : conversationsModels) {
               // if(conversationsModel.getMessages().get(0).getSenderID()== senderId)
                  conversationsModels1.add(conversationsModel);
            }
            mConversationsAdapter.setConversations(conversationsModels1);
        } else {
            ConversationList.setVisibility(View.GONE);
            emptyConversations.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConversationsPresenter != null)
            mConversationsPresenter.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onShowLoading() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable);
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * method to add a new message to list messages
     *
     * @param conversationId this is the parameter for addConversationEventMainThread
     */

    private void addConversationEventMainThread(int conversationId) {
        mConversationsAdapter.addConversationItem(conversationId);
        ConversationList.setVisibility(View.VISIBLE);
        emptyConversations.setVisibility(View.GONE);
        ConversationList.scrollToPosition(0);
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Pusher pusher) {
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
            case EVENT_BUS_ITEM_IS_ACTIVATED:
                int idx = ConversationList.getChildAdapterPosition(pusher.getView());
                if (actionMode != null) {
                    ToggleSelection(idx);
                    return;
                }

                break;
            case EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW:
                new Handler().postDelayed(() -> addConversationEventMainThread(pusher.getConversationId()), 500);
                break;
/*
            case EVENT_BUS_NEW_MESSAGE_GROUP_CONVERSATION_NEW_ROW:
                mConversationsPresenter.getGroupInfo(pusher.getGroupID());
                break;*/
            case EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW:
                new Handler().postDelayed(() -> mConversationsAdapter.updateConversationItem(pusher.getConversationId()), 500);
                break;
            case EVENT_BUS_MESSAGE_IS_READ:
            case EVENT_UPDATE_CONVERSATION_OLD_ROW:
            case EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS:
            case EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS:
            case EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS:
                new Handler().postDelayed(() -> mConversationsAdapter.updateStatusConversationItem(pusher.getConversationId()), 500);
                break;
            case EVENT_BUS_DELETE_CONVERSATION_ITEM:
                mConversationsAdapter.DeleteConversationItem(pusher.getConversationId());
                showEmptyView();
                break;
            case EVENT_BUS_CREATE_NEW_GROUP:
                mConversationsPresenter.getGroupInfo(pusher.getGroupID(), pusher.getConversationId());
                break;
            case EVENT_BUS_EXIT_NEW_GROUP:
                MessagesModel messagesModel2 = pusher.getMessagesModel();
                mConversationsPresenter.getGroupInfo(pusher.getGroupID(), messagesModel2);
                break;
            case EVENT_BUS_CONTACTS_PERMISSION:
                mConversationsPresenter.onRefresh();
                break;
            case EVENT_BUS_IMAGE_GROUP_UPDATED:
                mConversationsPresenter.getGroupInfo(pusher.getGroupID());
                break;
            case EVENT_BUS_ACTION_MODE_FINISHED:
                if (actionMode != null) {
                    mConversationsAdapter.clearSelections();
                    actionMode.finish();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                }
                break;

        }
    }

    private void showEmptyView() {
        if (mConversationsAdapter.getItemCount() == 0) {
            ConversationList.setVisibility(View.GONE);
            emptyConversations.setVisibility(View.VISIBLE);
        }
    }


    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
    }


}
