package com.setayeshco.rooyesh.app;

/**
 * Created by Abderrahim El imame on 02/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EndPoints {

   public static final String BACKEND_BASE_URL = "http://192.168.1.11/unichat/";
// public static final String BACKEND_BASE_URL = "http://185.94.99.189/unichat/";
   // public static final String BACKEND_BASE_URL = "http://bestdroid.ir/sss/";

 //   public static final String BACKEND_BASE_URLt = "http://callaine.ir/";
    /**
     * Chat server URLs
     */
    private static final String CHAT_SERVER_URL = "http://192.168.1.11:9001";
//    private static final String CHAT_SERVER_URL = "http://185.94.99.189:9001";
    static final String BACKEND_CHAT_SERVER_URL = CHAT_SERVER_URL;


    public static final String SEND_MESSAGE = "Messages/send";
    public static final String SEND_GROUP_MESSAGE = "Groups/saveMessage";
    /**
     * Authentication
     */
    public static final String JOIN = "Join";
    public static final String RESEND_REQUEST_SMS = "Resend";
    public static final String VERIFY_USER = "VerifyUser";
    public static final String CHECK_NETWORK = "CheckNetwork";
    public static final String CHECK_VERSION = "version";


    /**
     * Firebase
     */
    public static final String UPDATE_REGISTERED_ID = "updateRegisteredId";

    /**
     * Groups
     */
    public static final String CREATE_GROUP = "Groups/createGroup";
    public static final String ADD_MEMBERS_TO_GROUP = "Groups/addMembersToGroup";
    public static final String REMOVE_MEMBER_FROM_GROUP = "Groups/removeMemberFromGroup";
    public static final String MAKE_MEMBER_AS_ADMIN = "Groups/makeMemberAdmin";
    public static final String REMOVE_MEMBER_AS_ADMIN = "Groups/makeAdminMember";
    public static final String IS_PRESENT = "Groups/submitPresents";
    public static final String IS_PRESENT_SEARCH = "Groups/submitPresentsSearch";
    public static final String GROUPS_lIST = "Groups/all";
    public static final String GROUP_MEMBERS_lIST = "GetGroupMembers/{groupID}";
    public static final String EXIT_GROUP = "ExitGroup/{groupID}";
    public static final String PRESENT_LIST_GROUP = "GetGroupMembersAbsence/{groupID}/{date}";
    public static final String UPDATE_PRESENT_LIST_GROUP = "UpdateGroupMembersAbsence/{pID}/{state}";
    public static final String DELETE_GROUP = "DeleteGroup/{groupID}";
    public static final String GET_GROUP = "GetGroup/{groupID}";
    public static final String UPLOAD_GROUP_PROFILE_IMAGE = "uploadGroupImage";
    public static final String EDIT_GROUP_NAME = "EditGroupName";


    /**
     * Download and upload files
     */
    public static final String UPLOAD_MESSAGES_IMAGE = "uploadMessagesImage";
    public static final String UPLOAD_MESSAGES_VIDEO = "uploadMessagesVideo";
    public static final String UPLOAD_MESSAGES_AUDIO = "uploadMessagesAudio";
    public static final String UPLOAD_MESSAGES_DOCUMENT = "uploadMessagesDocument";
    public static final String HAS_BACKUP = "userHasBackup";


    /**
     * Contacts
     */
    public static final String SEND_CONTACTS = "SendContacts";
    //public static final String SEND_TEST = "test.json";
    public static final String SEND_TEST = "ii.php";

    public static final String GET_CONTACT = "GetContact/{userID}";
    public static final String GET_CONVERSATION = "GetConversationList/{userID}";
    public static final String GET_STATUS = "GetStatus";
    public static final String SAVE_EMITTED_CALL = "saveEmittedCall";
    public static final String SAVE_ACCEPTED_CALL = "saveAcceptedCall";
    public static final String SAVE_RECEIVED_CALL = "saveReceivedCall";
    public static final String BLOCK_USER = "blockUser/{userId}";
    public static final String UN_BLOCK_USER = "unBlockUser/{userId}";
    public static final String DELETE_ALL_STATUS = "DeleteAllStatus";
    public static final String DELETE_STATUS = "DeleteStatus/{status}";
    public static final String UPDATE_STATUS = "UpdateStatus/{statusID}";
    public static final String EDIT_STATUS = "EditStatus";
    public static final String EDIT_NAME = "EditName";
    public static final String UPLOAD_PROFILE_IMAGE = "uploadImage";
    public static final String DELETE_ACCOUNT = "DeleteAccount";
    public static final String DELETE_ACCOUNT_CONFIRMATION = "DeleteUserAccountConfirmation";

    /**
     * Files Get URL
     */
    public static final String PROFILE_IMAGE_URL = BACKEND_BASE_URL + "image/profile/";
    public static final String PROFILE_PREVIEW_IMAGE_URL = BACKEND_BASE_URL + "image/profilePreview/";
    public static final String PROFILE_PREVIEW_HOLDER_IMAGE_URL = BACKEND_BASE_URL + "image/profilePreviewHolder/";
    public static final String ROWS_IMAGE_URL = BACKEND_BASE_URL + "image/rowImage/";
    public static final String SETTINGS_IMAGE_URL = BACKEND_BASE_URL + "image/settings/";
    public static final String EDIT_PROFILE_IMAGE_URL = BACKEND_BASE_URL + "image/editProfile/";

    public static final String MESSAGE_DOCUMENT_URL = BACKEND_BASE_URL + "document/messageDocument/";
    public static final String MESSAGE_HOLDER_IMAGE_URL = BACKEND_BASE_URL + "image/messageImageHolder/";
    public static final String MESSAGE_IMAGE_URL = BACKEND_BASE_URL + "image/messageImage/";
    public static final String MESSAGE_VIDEO_THUMBNAIL_URL = BACKEND_BASE_URL + "video/messageVideoThumbnail/";
    public static final String MESSAGE_AUDIO_URL = BACKEND_BASE_URL + "audio/messageAudio/";

    /**
     * Files Downloads URL
     */
    public static final String MESSAGE_DOCUMENT_DOWNLOAD_URL = "document/messageDocument/";
    public static final String MESSAGE_BACKUP_DOWNLOAD_URL = "backup/messageBackup/";
//    public static final String MESSAGE_IMAGE_DOWNLOAD_URL = "image/messageImage/";
    public static final String MESSAGE_IMAGE_DOWNLOAD_URL = "uploads/imagesFiles/after/";
    public static final String MESSAGE_VIDEO_DOWNLOAD_THUMBNAIL_URL = "video/messageVideoThumbnail/";
    public static final String MESSAGE_VIDEO_DOWNLOAD_URL = "video/messageVideo/";
    public static final String MESSAGE_AUDIO_DOWNLOAD_URL = "audio/messageAudio/";

    /**
     * APPLICATION
     */
    public static final String GET_APPLICATION_SETTINGS = "GetAppSettings";
    public static final String GET_APPLICATION_PRIVACY = "GetApplicationPrivacy";
}
