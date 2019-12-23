package com.setayeshco.rooyesh.app;


import com.setayeshco.rooyesh.R;

/**
 * Created by Abderrahim on 09/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AppConstants {

    /* debugging constants  for developer */
    public static final String TAG = "benCherif";
    public static final boolean ENABLE_FONTS_TYPES = true; //this to changes fonts of the application
    public static final boolean DEBUGGING_MODE = true;
    static final boolean CRASH_LYTICS = false;//this is crashlytics if you have an account on fabric.io but don't forget to change the key on manifests file
    static final boolean ENABLE_CRASH_HANDLER = false; // this for the crash activity  you can turn on this so when user get a crash this activity will appear instead of stop the app
    public static final boolean ENABLE_ANIMATIONS = false;// this for activities animations
    public static final boolean ENABLE_FACEBOOK_ACCOUNT_KIT = false;// this for fb account kit
    public static final String DEFAULT_COUNTRY_CODE = "IR";//this for country code

    /**
     * for the application
     */
    static final String APP_KEY_SECRET = "7d3d3b6c2d3683bf25bbb51533ec6dab";// make sure this one is the same that you put on your server side (for security reasons)
    public static final String INVITE_MESSAGE_SMS = "Hello get " + RooyeshApplication.getInstance().getString(R.string.app_name) + " so we can easily chat  with each other !  ";// this is the sms to invite  friends
    public static final String JOINED_MESSAGE_SMS = "Hi there! i'm using " + RooyeshApplication.getInstance().getString(R.string.app_name);// this is the msg when the user join to the app

    /**
     * those for create and left group (you can change them for security reasons)
     */
    public static final String CREATE_GROUP = "0x0i3del0x0";  // and for those you must change them for  security  reasons but make sure to put string
    public static final String LEFT_GROUP = "0x0i5ela0x0";

    /**
     * for toast and snackbar
     */
    public static final int MESSAGE_COLOR_ERROR = R.color.colorOrangeLight;
    public static final int MESSAGE_COLOR_WARNING = R.color.colorOrange;
    public static final int MESSAGE_COLOR_SUCCESS = R.color.colorGreenDark;
    public static final int TEXT_COLOR = R.color.colorWhite;


    // special character to prefix the code. Make sure this character appears only once in the sms
    public static final String CODE_DELIMITER = ":";

    /**
     * upload image or video constants
     */
    public static final int UPLOAD_PICTURE_REQUEST_CODE = 0x001;
    public static final int UPLOAD_VIDEO_REQUEST_CODE = 0x002;
    public static final int UPLOAD_AUDIO_REQUEST_CODE = 0x003;
    public static final int UPLOAD_DOCUMENT_REQUEST_CODE = 0x004;
    public static final int SELECT_PROFILE_PICTURE = 0x005;
    public static final int SELECT_PROFILE_CAMERA = 0x006;
    public static final int SELECT_MESSAGES_CAMERA = 0x007;
    public static final int SELECT_MESSAGES_RECORD_VIDEO = 0x008;
    public static final int PERMISSION_REQUEST_CODE = 0x009;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 0x009;
    public static final int SELECT_ADD_NEW_CONTACT = 0x010;
    public static final int SELECT_COUNTRY = 0x011;
    public static final int APP_REQUEST_CODE = 0x012;

    /**
     * Chat socket constants (be careful if u want to change them !!)
     */
    //ping/pong socket constants:
    public static final String SOCKET_PING = "socket_ping";
    public static final String SOCKET_PONG = "socket_pong";
    //user socket constants:
    public static final int STATUS_USER_TYPING = 0x011;
    public static final int STATUS_USER_STOP_TYPING = 0x012;
    public static final int STATUS_USER_CONNECTED = 0x013;
    public static final int STATUS_USER_DISCONNECTED = 0x014;
    public static final int STATUS_USER_LAST_SEEN = 0x015;

    //user status
    public static final String STATUS_USER_CONNECTED_STATE = "STATUS_USER_CONNECTED_STATE";
    public static final String STATUS_USER_DISCONNECTED_STATE = "STATUS_USER_DISCONNECTED_STATE";
    public static final String STATUS_USER_LAST_SEEN_STATE = "STATUS_USER_LAST_SEEN_STATE";
    //single user socket constants:

    public static final String SOCKET_UPDATE_REGISTER_ID = "socket_update_register_id";


    public static final String SOCKET_SAVE_NEW_MESSAGE = "socket_save_new_message";
    public static final String SOCKET_NEW_MESSAGE_SERVER = "socket_new_message_server";
    //for app messages status
    public static final String SOCKET_IS_MESSAGE_DELIVERED = "socket_delivered";
    public static final String SOCKET_IS_MESSAGE_SEEN = "socket_seen";

    public static final String SOCKET_IS_TYPING = "socket_typing";
    public static final String SOCKET_SEND_MESSAGE_SET = "socket_send_msg_set";
    public static final String SOCKET_SEND_TO_USER = "socket_send_to_user_set";
    public static final String SOCKET_ADD_MEMBER_MSF_ALL = "socket_add_member_sent_msg_to_all";
    public static final String SOCKET_SEND_TO_USER2 = "socket_send_to_user_set2";
    public static final String SOCKET_SEND_TO_MSG = "socket_member_send_msg";
    public static final String SOCKET_MEMBER_SEND_MSG_TO_ALL = "socket_member_sent_msg_to_all";
    public static final String SOCKET_MEMBER_SEND_MSG_TO_ALL2 = "socket_member_sent_msg_to_all2";
    public static final String SOCKET_RECIVE_MSG = "socket_recieve_msg";
    public static final String SOCKET_RECIVE_MSG2 = "socket_recieve_msg2";
    public static final String SOCKET_SENDING_MSG = "socket_sending_msg";
    public static final String SOCKET_IS_STOP_TYPING = "socket_stop_typing";
    public static final String SOCKET_IS_ONLINE = "socket_is_online";
    public static final String SOCKET_IS_LAST_SEEN = "socket_last_seen";
    public static final String SOCKET_CONNECTED = "socket_user_connect";
    public static final String SOCKET_DISCONNECTED = "socket_user_disconnect";
    public static final String SOCKET_NEW_USER_JOINED = "socket_new_user_has_joined";
    public static final String SOCKET_IMAGE_PROFILE_UPDATED = "socket_profileImageUpdated";
    public static final String SOCKET_IMAGE_GROUP_UPDATED = "socket_groupImageUpdated";
    //group socket constants:
    public static final String SOCKET_SAVE_NEW_MESSAGE_GROUP = "socket_save_group_message";
    public static final String SOCKET_NEW_MESSAGE_GROUP_SERVER = "socket_new_group_message_server";

    //for app group messages status
    public static final String SOCKET_IS_MESSAGE_GROUP_DELIVERED = "socket_group_delivered";
    public static final String SOCKET_IS_MESSAGE_GROUP_SEEN = "socket_group_seen";

    public static final String SOCKET_IS_MEMBER_TYPING = "socket_member_typing";
    public static final String SOCKET_IS_MEMBER_STOP_TYPING = "socket_member_stop_typing";


    //for calls
    public static final String SOCKET_CALL_USER_PING = "socket_call_user_ping";
    public static final String SOCKET_RESET_SOCKET_ID = "reset_socket_id";
    public static final String SOCKET_SIGNALING_SERVER = "signaling_server";
    public static final String SOCKET_MAKE_NEW_CALL = "make_new_call";
    public static final String SOCKET_RECEIVE_NEW_CALL = "receive_new_call";
    public static final String SOCKET_REJECT_NEW_CALL = "reject_new_call";
    public static final String SOCKET_ACCEPT_NEW_CALL = "accept_new_call";
    public static final String SOCKET_HANGUP_CALL = "hang_up_call";


    /**
     * Status constants
     */

    public static final int IS_WAITING = 0;
    public static final int IS_SENT = 1;
    public static final int IS_DELIVERED = 2;
    public static final int IS_SEEN = 3;
    /**
     * images size
     */
    public static final int NOTIFICATIONS_IMAGE_SIZE = 150;
    public static final int ROWS_IMAGE_SIZE = 90;
    public static final int PROFILE_PREVIEW_IMAGE_SIZE = 500;
    public static final int PROFILE_PREVIEW_BLUR_IMAGE_SIZE = 50;
    public static final int PROFILE_IMAGE_SIZE = 500;
    public static final int SETTINGS_IMAGE_SIZE = 150;
    public static final int EDIT_PROFILE_IMAGE_SIZE = 500;
    public static final int MESSAGE_IMAGE_SIZE = 300;
    public static final int PRE_MESSAGE_IMAGE_SIZE = 40;
    public static final int FULL_SCREEN_IMAGE_SIZE = 640;
    public static final int BLUR_RADIUS = 1;

    /**
     * images tags
     */
    public static final String CONVERSATIONS_IMAGE_TAG = "Conversation Image";
    public static final String CONTACTS_IMAGE_TAG = "Contacts Image";
    public static final String GROUP_MEMBERS_IMAGE_TAG = "Group members Image";
    public static final String CREATE_GROUP_MEMBERS_IMAGE_TAG = "Create group members Image";

    /**
     * those for EventBus tool
     */
    public static final String EVENT_UPDATE_CONVERSATION_OLD_ROW = "update_message_conversation";
    public static final String EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW = "new_message_conversation";
    public static final String EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW = "new_message_conversation_new_row";
    public static final String EVENT_BUS_NEW_MESSAGE_GROUP_CONVERSATION_NEW_ROW = "new_message_group_conversation_new_row";
    public static final String EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW = "new_message_messages_new_row";
    public static final String EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW = "new_group_message_messages_new_row";
    public static final String EVENT_BUS_CALL_NEW_ROW = "new_call_new_row";//
    public static final String EVENT_UPDATE_CALL_OLD_ROW = "update_call_row";
    public static final String EVENT_BUS_DELETE_CALL_ITEM = "delete_call_row";
    public static final String EVENT_BUS_ITEM_IS_ACTIVATED = "ItemIsActivated";
    public static final String EVENT_ADD_MEMBER_SEND = "socket_add_member_send_msg";
    public static final String EVENT_BUS_MESSAGE_IS_READ = "messages_read";
    public static final String EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS = "new_message_sent_for_conversation";
    public static final String EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS = "messages_seen_for_conversation";
    public static final String EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS = "messages_delivered_for_conversation";
    public static final String EVENT_BUS_DELETE_CONVERSATION_ITEM = "deleteConversation";
    public static final String EVENT_BUS_CREATE_NEW_GROUP = "createNewGroup";
    public static final String EVENT_BUS_EXIT_NEW_GROUP = "exitNewGroup";
    public static final String EVENT_BUS_MESSAGE_COUNTER = "MessagesCounter";
    public static final String EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_MESSAGES = "messages_delivered_for_messages";
    public static final String EVENT_BUS_MESSAGE_IS_SEEN_FOR_MESSAGES = "messages_seen_for_messages";
    public static final String EVENT_BUS_MESSAGE_IS_SENT_FOR_MESSAGES = "new_message_sent_for_messages";
    public static final String EVENT_BUS_NEW_USER_JOINED = "newUserJoined";
    public static final String EVENT_BUS_STOP_REFRESH = "stopRefresh";
    public static final String EVENT_BUS_START_REFRESH = "startRefresh";
    public static final String EVENT_BUS_IMAGE_PROFILE_PATH = "ImageProfilePath";
    public static final String EVENT_BUS_UPDATE_CURRENT_SATUS = "updateCurrentStatus";
    public static final String EVENT_BUS_UPDATE_STATUS = "updateStatus";
    public static final String EVENT_BUS_DELETE_STATUS = "deleteStatus";
    public static final String EVENT_BUS_NEW_GROUP_NOTIFICATION = "NewGroupNotification";
    public static final String EVENT_BUS_NEW_USER_NOTIFICATION = "NewUserNotification";
    public static final String EVENT_BUS_UPDATE_USER_STATE = "updateUserState";
    public static final String EVENT_BUS_CREATE_GROUP = "createGroup";
    public static final String EVENT_BUS_DELETE_GROUP = "deleteGroup";
    public static final String EVENT_BUS_EXIT_GROUP = "exitGroup";
    public static final String EVENT_BUS_EXIT_THIS_GROUP = "exitThisGroup";
    public static final String EVENT_BUS_ADD_MEMBER = "addMember";
    public static final String EVENT_BUS_REMOVE_CREATE_MEMBER = "removeCreateMember";
    public static final String EVENT_BUS_ADD_CREATE_MEMBER = "addCreateMember";
    public static final String EVENT_BUS_DELETE_CREATE_MEMBER = "deleteCreateMember";
    public static final String EVENT_BUS_UPDATE_GROUP_NAME = "updateGroupName";
    public static final String EVENT_BUS_PATH_GROUP = "PathGroup";
    public static final String EVENT_BUS_NEW_CONTACT_ADDED = "newContactAdded";
    public static final String EVENT_BUS_UPLOAD_MESSAGE_FILES = "uploadMessageFiles";
    public static final String EVENT_BUS_ITEM_IS_ACTIVATED_MESSAGES = "ItemIsActivatedMessages";
    public static final String EVENT_BUS_CONTACTS_PERMISSION = "ContactsPermission";
    public static final String EVENT_BUS_UPDATE_CONTACTS_LIST = "updatedContactsList";
    public static final String EVENT_BUS_UPDATE_CONTACTS_LIST_THROWABLE = "updatedContactsListThrowable";
    //new
    public static final String EVENT_BUS_IMAGE_PROFILE_UPDATED = "profileImageUpdated";
    public static final String EVENT_BUS_IMAGE_GROUP_UPDATED = "groupImageUpdated";
    public static final String EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED = "mine_profileImageUpdated";
    public static final String EVENT_BUS_USERNAME_PROFILE_UPDATED = "updateUserName";
    public static final String EVENT_BUS_START_CONVERSATION = "startConversation";
    public static final String EVENT_BUS_ACTION_MODE_STARTED = "actionModeStarted";
    public static final String EVENT_BUS_ACTION_MODE_DESTROYED = "actionModeDestroyed";
    public static final String EVENT_BUS_ACTION_MODE_FINISHED = "actionModeFinished";
    public static final String EVENT_BUS_BACK_ACTIVITY = "back_activity";

    public static final String EVENT_BUS_GOOGLE_DRIVE = "GOOGLE_DRIVE";
    public static final String EVENT_BUS_REFRESH_TOKEN_FCM = "REFRESH_TOKEN_FCM";
    public static final String EVENT_BUS_CONTACTS_FRAGMENT_SELECTED = "CONTACTS_FRAGMENT_SELECTED";
    //CALLS EVENT BUS
    public static final String EVENT_BUS_CALL_READY = "onCallReady";
    public static final String EVENT_BUS_STATUS_CHANGED = "onStatusChanged";
    public static final String EVENT_BUS_LOCAL_STREAM = "onLocalStream";
    public static final String EVENT_BUS_ADD_REMOTE_STREAM = "onAddRemoteStream";
    public static final String EVENT_BUS_REMOVE_REMOTE_STREAM = "onRemoveRemoteStream";
    public static final String EVENT_BUS_ACCEPT_CALL = "onAcceptCall";
    public static final String EVENT_BUS_REJECT_CALL = "onReject";
    public static final String EVENT_BUS_HANG_UP = "onHangUp";
    public static final String EVENT_BUS_ON_PEER_CLOSED = "onPeerConnectionClosed";


    /**
     * Media type
     */

    public static final String RECEIVED_IMAGE = "RECEIVED_IMAGE";
    public static final String SENT_IMAGE = "SENT_IMAGE";
    public static final String PROFILE_IMAGE = "PROFILE_IMAGE";

    public static final String RECEIVED_IMAGE_FROM_SERVER = "RECEIVED_IMAGE_FROM_SERVER";
    public static final String SENT_IMAGE_FROM_SERVER = "SENT_IMAGE_FROM_SERVER";
    public static final String PROFILE_IMAGE_FROM_SERVER = "PROFILE_IMAGE_FROM_SERVER";

    // FOR DOWNLOAD FILES
    public static final String SENT_AUDIO = "SENT_AUDIO";
    public static final String SENT_TEXT = "SENT_TEXT";
    public static final String SENT_IMAGES = "SENT_IMAGES";
    public static final String SENT_VIDEOS = "SENT_VIDEOS";
    public static final String SENT_DOCUMENTS = "SENT_DOCUMENTS";
    /**
     * for cache
     */
    public static final String DATA_CACHED = "DATA_CACHED";
    public static final String GROUP = "gp";
    public static final String USER = "ur";
    public static final String PROFILE_PREVIEW = "prp";
    public static final String FULL_PROFILE = "fp";
    public static final String SETTINGS_PROFILE = "spr";
    public static final String EDIT_PROFILE = "epr";
    public static final String ROW_PROFILE = "rpr";
    public static final String ROW_MESSAGES_BEFORE = "rmebe";
    public static final String ROW_WALLPAPER = "rwppr";
    public static final String ROW_MESSAGES_AFTER = "rmeaf";
    public static String EXPORT_REALM_FILE_NAME = RooyeshApplication.getInstance().getString(R.string.app_name) + "_msgstore.realm";


    /**
     * WebRtc
     */
    public static final String CALLER_SOCKET_ID = "CALLER_SOCKET_ID";
    public static final String USER_SOCKET_ID = "USER_SOCKET_ID";
    public static final String CALLER_PHONE = "CALLER_PHONE";
    public static final String CALLER_ID = "CALLER_ID";
    public static final String CALLER_PHONE_ACCEPT = "CALLER_PHONE_ACCEPT";
    public static final String CALLER_IMAGE = "CALLER_IMAGE";
    public static final String USER_IMAGE = "USER_IMAGE";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String IS_VIDEO_CALL = "IS_VIDEO_CALL";
    public static final String IS_ACCEPTED_CALL = "IS_ACCEPTED_CALL";
    //for calls list
    public static final String VIDEO_CALL = "VIDEO_CALL";
    public static final String VOICE_CALL = "VOICE_CALL";
    //reasons
    public static final String NO_CAMERA = "NO_CAMERA";
    public static final String IGNORED = "IGNORED";
    public static final String NO_ANSWER = "NO_ANSWER";
    public static final String AN_EXECPTION = "AN_EXECPTION";

    //status
    public static final String USER_CONNECTING = "Connecting";
    public static final String USER_CONNECTED = "Connected";
    public static final String USER_COMPLETED = "COMPLETED";
    public static final String USER_DISCONNECT = "Disconnect";
    public static final String USER_CHECKING = "CHECKING";
    public static final String USER_CLOSED = "CLOSED";

}
