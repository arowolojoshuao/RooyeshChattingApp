package com.setayeshco.rooyesh.services.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.services.MainService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;

/**
 * Created by Abderrahim El imame on 4/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class GcmServiceListener extends FirebaseMessagingService {
	
	private Intent mIntent;
	
	private NotificationsManager notificationsManager = new NotificationsManager();
	
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		//AppHelper.LogCat("GcmServiceListener  From: " + remoteMessage.getFrom());
//        AppHelper.LogCat("GcmServiceListener Notification Message Body: " + remoteMessage.getNotification().getBody());
		// AppHelper.LogCat("GcmServiceListener Notification: " + remoteMessage.getNotification().toString());
		if (remoteMessage.getData().size() > 0) {
			parseRequest(remoteMessage.getData());
		}
	}
	
	@Override
	public void onMessageSent(String msgId) {
		super.onMessageSent(msgId);
		AppHelper.LogCat("onMessageSent " + msgId);
	}
	
	@Override
	public void onDeletedMessages() {
		super.onDeletedMessages();
		AppHelper.LogCat("onDeletedMessages ");
		
	}
	
	@Override
	public void onSendError(String msgId, Exception e) {
		super.onSendError(msgId, e);
		AppHelper.LogCat("onSendError " + msgId);
		
	}
	
	private void parseRequest(Map<String, String> extras) {
		
		//AppHelper.LogCat("extras " + extras.toString());
		if (extras.get("actionType") == null) return;
		//   AppHelper.LogCat("actionType" + extras.get("actionType"));
		removeMessagesWithZeroID();
		switch (extras.get("actionType")) {
			
			case AppConstants.SOCKET_NEW_MESSAGE_SERVER:
				
				try {
					JSONObject data = new JSONObject();
					
					data.put("recipientId", extras.get("recipientId"));
					data.put("messageId", extras.get("messageId"));
					data.put("messageBody", extras.get("messageBody"));
					data.put("senderId", extras.get("senderId"));
					data.put("phone", extras.get("phone"));
					data.put("senderName", extras.get("senderName"));
					data.put("date", extras.get("date"));
					data.put("isGroup", extras.get("isGroup"));
					data.put("image", extras.get("image"));
					data.put("video", extras.get("video"));
					data.put("audio", extras.get("audio"));
					data.put("document", extras.get("document"));
					data.put("thumbnail", extras.get("thumbnail"));
					data.put("duration", extras.get("duration"));
					data.put("fileSize", extras.get("fileSize"));
					data.put("senderImage", extras.get("senderImage"));
					
					//    AppHelper.LogCat("new_message_server_whatsclone");
					Realm realm = RooyeshApplication.getRealmDatabaseInstance();
					if (!MainService.checkIfUserBlockedExist(data.getInt("senderId"), realm)) {
						if (!realm.isClosed())
							realm.close();
						saveNewMessage(data);
					}
				} catch (JSONException e) {
					AppHelper.LogCat("JSONException " + e.getMessage());
				}
				break;
			
			
			case AppConstants.SOCKET_NEW_MESSAGE_GROUP_SERVER:
				
				// AppHelper.LogCat("SOCKET_NEW_MESSAGE_GROUP_SERVER ");
				try {
					JSONObject data = new JSONObject();
					data.put("senderId", extras.get("senderId"));
					data.put("recipientId", extras.get("recipientId"));
					data.put("messageBody", extras.get("messageBody"));
					data.put("senderName", extras.get("senderName"));
					data.put("phone", extras.get("phone"));
					data.put("GroupImage", extras.get("GroupImage"));
					data.put("GroupName", extras.get("GroupName"));
					data.put("date", extras.get("date"));
					data.put("video", extras.get("video"));
					data.put("thumbnail", extras.get("thumbnail"));
					data.put("image", extras.get("image"));
					data.put("audio", extras.get("audio"));
					data.put("document", extras.get("document"));
					data.put("duration", extras.get("duration"));
					data.put("fileSize", extras.get("fileSize"));
					data.put("groupID", extras.get("groupID"));
					
					Realm realm = RooyeshApplication.getRealmDatabaseInstance();
					if (!MainService.checkIfUserBlockedExist(data.getInt("senderId"), realm)) {
						if (!realm.isClosed())
							realm.close();
						saveNewMessageGroup(data);
					}
				} catch (JSONException e) {
					AppHelper.LogCat("JSONException " + e.getMessage());
				}
				break;
		}
	}
	
	private void removeMessagesWithZeroID() {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		if (checkIfZeroExist(realm)) {
			realm.executeTransaction(realm1 -> {
				RealmResults<MessagesModel> messagesModel = realm1.where(MessagesModel.class).equalTo("id", 0).findAll();
				for (MessagesModel messagesModel1 : messagesModel) {
					messagesModel1.deleteFromRealm();
				}
			});
			notificationsManager.SetupBadger(this);
		}
		if (!realm.isClosed()) realm.close();
	}
	
	
	/**
	 * method to save the incoming message and mark him as waiting
	 *
	 * @param data this is the parameter for saveNewMessage method
	 */
	private void saveNewMessage(JSONObject data) {
		
		Log.w("arash message", data.toString());
		
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		try {
			int recipientId = data.getInt("recipientId");
			int senderId = data.getInt("senderId");
			int messageId = data.getInt("messageId");
			String phone = data.getString("phone");
			String messageBody = data.getString("messageBody");
			String senderName = data.getString("senderName");
			String senderImage = data.getString("senderImage");
			String date = data.getString("date");
			String video = data.getString("video");
			String thumbnail = data.getString("thumbnail");
			boolean isGroup = false;
			String image = data.getString("image");
			String audio = data.getString("audio");
			String document = data.getString("document");
			String duration = data.getString("duration");
			String fileSize = data.getString("fileSize");
			
			if (senderId == PreferenceManager.getID(this)) return;
			
			int conversationID = MainService.getConversationId(recipientId, senderId, realm);
			if (conversationID == 0) {
				realm.executeTransaction(realm1 -> {
					
					int lastConversationID = RealmBackupRestore.getConversationLastId();
					int lastID = RealmBackupRestore.getMessageLastId();
					int UnreadMessageCounter = 0;
					UnreadMessageCounter++;
					
					
					RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
					MessagesModel messagesModel = new MessagesModel();
					messagesModel.setId(lastID);
					messagesModel.setUsername(senderName);
					messagesModel.setRecipientID(recipientId);
					messagesModel.setDate(date);
					messagesModel.setStatus(AppConstants.IS_WAITING);
					messagesModel.setGroup(isGroup);
					messagesModel.setSenderID(senderId);
					messagesModel.setFileUpload(true);
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
						messagesModel.setFileDownLoad(false);
						
					} else {
						messagesModel.setFileDownLoad(true);
					}
					
					messagesModel.setDuration(duration);
					messagesModel.setFileSize(fileSize);
					messagesModel.setConversationID(lastConversationID);
					messagesModel.setMessage(messageBody);
					messagesModel.setImageFile(image);
					messagesModel.setVideoFile(video);
					messagesModel.setAudioFile(audio);
					messagesModel.setDocumentFile(document);
					messagesModel.setVideoThumbnailFile(thumbnail);
					messagesModel.setPhone(phone);
					messagesModelRealmList.add(messagesModel);
					ConversationsModel conversationsModel1 = new ConversationsModel();
					conversationsModel1.setRecipientID(senderId);
					conversationsModel1.setLastMessage(messageBody);
					conversationsModel1.setRecipientUsername(senderName);
					if (!UtilsPhone.checkIfContactExist(this, phone)) {
						if (!senderImage.equals("null"))
							conversationsModel1.setRecipientImage(senderImage);
						else
							conversationsModel1.setRecipientImage(null);
					}
					conversationsModel1.setMessageDate(date);
					conversationsModel1.setId(lastConversationID);
					conversationsModel1.setStatus(AppConstants.IS_WAITING);
					conversationsModel1.setRecipientPhone(phone);
					conversationsModel1.setGroup(isGroup);
					conversationsModel1.setMessages(messagesModelRealmList);
					conversationsModel1.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
					conversationsModel1.setLastMessageId(lastID);
					conversationsModel1.setCreatedOnline(true);
					realm1.copyToRealmOrUpdate(conversationsModel1);
					
					
					String FileType = null;
					if (!messagesModel.getImageFile().equals("null")) {
						FileType = "Image";
					} else if (!messagesModel.getVideoFile().equals("null")) {
						FileType = "Video";
					} else if (!messagesModel.getAudioFile().equals("null")) {
						FileType = "Audio";
					} else if (!messagesModel.getDocumentFile().equals("null")) {
						FileType = "Document";
					}
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW, lastConversationID));
					
					mIntent = new Intent("new_user_message_notification_whatsclone");
					mIntent.putExtra("conversationID", lastConversationID);
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("senderId", recipientId);
					mIntent.putExtra("userImage", senderImage);
					mIntent.putExtra("username", senderName);
					mIntent.putExtra("file", FileType);
					mIntent.putExtra("phone", phone);
					mIntent.putExtra("messageId", messageId);
					mIntent.putExtra("message", messageBody);
					mIntent.putExtra("app", this.getPackageName());
					sendBroadcast(mIntent);
				});
			} else {
				
				realm.executeTransaction(realm1 -> {
					
					int UnreadMessageCounter = 0;
					int lastID = RealmBackupRestore.getMessageLastId();
					
					ConversationsModel conversationsModel;
					RealmQuery<ConversationsModel> conversationsModelRealmQuery = realm1.where(ConversationsModel.class).equalTo("id", conversationID);
					conversationsModel = conversationsModelRealmQuery.findAll().first();
					
					UnreadMessageCounter = Integer.parseInt(conversationsModel.getUnreadMessageCounter());
					UnreadMessageCounter++;
					MessagesModel messagesModel = new MessagesModel();
					messagesModel.setId(lastID);
					messagesModel.setUsername(senderName);
					messagesModel.setRecipientID(recipientId);
					messagesModel.setDate(date);
					messagesModel.setStatus(AppConstants.IS_WAITING);
					messagesModel.setGroup(isGroup);
					messagesModel.setSenderID(senderId);
					messagesModel.setFileUpload(true);
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
						messagesModel.setFileDownLoad(false);
						
					} else {
						messagesModel.setFileDownLoad(true);
					}
					messagesModel.setFileSize(fileSize);
					messagesModel.setDuration(duration);
					messagesModel.setConversationID(conversationID);
					messagesModel.setMessage(messageBody);
					messagesModel.setImageFile(image);
					messagesModel.setVideoFile(video);
					messagesModel.setAudioFile(audio);
					messagesModel.setDocumentFile(document);
					messagesModel.setVideoThumbnailFile(thumbnail);
					messagesModel.setPhone(phone);
					conversationsModel.getMessages().add(messagesModel);
					conversationsModel.setLastMessageId(lastID);
					conversationsModel.setRecipientID(senderId);
					conversationsModel.setLastMessage(messageBody);
					conversationsModel.setMessageDate(date);
					conversationsModel.setCreatedOnline(true);
					conversationsModel.setRecipientUsername(senderName);
					if (!UtilsPhone.checkIfContactExist(this, phone)) {
						if (!senderImage.equals("null"))
							conversationsModel.setRecipientImage(senderImage);
						else
							conversationsModel.setRecipientImage(null);
					}
					conversationsModel.setRecipientPhone(phone);
					conversationsModel.setGroup(isGroup);
					conversationsModel.setStatus(AppConstants.IS_WAITING);
					conversationsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
					realm1.copyToRealmOrUpdate(conversationsModel);
					
					
					String FileType = null;
					if (!messagesModel.getImageFile().equals("null")) {
						FileType = "Image";
					} else if (!messagesModel.getVideoFile().equals("null")) {
						FileType = "Video";
					} else if (!messagesModel.getAudioFile().equals("null")) {
						FileType = "Audio";
					} else if (!messagesModel.getDocumentFile().equals("null")) {
						FileType = "Document";
					}
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationID));
					
					
					mIntent = new Intent("new_user_message_notification_whatsclone");
					mIntent.putExtra("conversationID", conversationID);
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("senderId", recipientId);
					mIntent.putExtra("userImage", senderImage);
					mIntent.putExtra("username", senderName);
					mIntent.putExtra("file", FileType);
					mIntent.putExtra("phone", phone);
					mIntent.putExtra("messageId", messageId);
					mIntent.putExtra("message", messageBody);
					mIntent.putExtra("app", this.getPackageName());
					sendBroadcast(mIntent);
					
				});
				
			}
			
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
			}
			MainService.RecipientMarkMessageAsDelivered(this, messageId);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (AppHelper.isActivityRunning(this, "activities.messages.MessagesActivity")) {
				AppHelper.LogCat("MessagesActivity running");
				MainService.emitMessageSeen(this, senderId);
			}
			
		} catch (JSONException e) {
			AppHelper.LogCat("save message Exception MainService" + e.getMessage());
		}
		if (!realm.isClosed())
			realm.close();
		EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
		notificationsManager.SetupBadger(this);
	}
	
	
	/**
	 * method to save the incoming message and mark him as waiting
	 *
	 * @param data this is the parameter for saveNewMessage method
	 */
	private void saveNewMessageGroup(JSONObject data) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		try {
			
			int senderId = data.getInt("senderId");
			int recipientId = data.getInt("recipientId");
			String messageBody = data.getString("messageBody");
			String senderName = data.getString("senderName");
			String senderPhone = data.getString("phone");
			String groupImage = data.getString("GroupImage");
			String groupName = data.getString("GroupName");
			String dateTmp = data.getString("date");
			String video = data.getString("video");
			String thumbnail = data.getString("thumbnail");
			boolean isGroup = true;
			String image = data.getString("image");
			String audio = data.getString("audio");
			String document = data.getString("document");
			String duration = data.getString("duration");
			String fileSize = data.getString("fileSize");
			int groupID = data.getInt("groupID");
			
			if (senderId == PreferenceManager.getID(this)) return;
			
			if (!checkIfGroupConversationExist(groupID, realm)) {
				realm.executeTransaction(realm1 -> {
					int lastConversationID = RealmBackupRestore.getConversationLastId();
					int lastID = RealmBackupRestore.getMessageLastId();
					int UnreadMessageCounter = 0;
					UnreadMessageCounter++;
					ConversationsModel conversationsModel = new ConversationsModel();
					RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
					MessagesModel messagesModel = null;
					messagesModel = new MessagesModel();
					messagesModel.setId(lastID);
					messagesModel.setDate(dateTmp);
					messagesModel.setSenderID(senderId);
					messagesModel.setUsername(senderName);
					messagesModel.setPhone(senderPhone);
					messagesModel.setRecipientID(0);
					messagesModel.setStatus(AppConstants.IS_WAITING);
					messagesModel.setGroup(true);
					messagesModel.setGroupID(groupID);
					messagesModel.setImageFile(image);
					messagesModel.setVideoFile(video);
					messagesModel.setAudioFile(audio);
					messagesModel.setDocumentFile(document);
					messagesModel.setVideoThumbnailFile(thumbnail);
					messagesModel.setFileUpload(true);
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
						messagesModel.setFileDownLoad(false);
						
					} else {
						messagesModel.setFileDownLoad(true);
					}
					messagesModel.setDuration(duration);
					messagesModel.setFileSize(fileSize);
					messagesModel.setConversationID(lastConversationID);
					messagesModel.setMessage(messageBody);
					messagesModelRealmList.add(messagesModel);
					conversationsModel.setLastMessageId(lastID);
					conversationsModel.setRecipientID(0);
					conversationsModel.setRecipientUsername(groupName);
					conversationsModel.setRecipientImage(groupImage);
					conversationsModel.setGroupID(groupID);
					conversationsModel.setMessageDate(dateTmp);
					conversationsModel.setId(lastConversationID);
					conversationsModel.setGroup(isGroup);
					conversationsModel.setMessages(messagesModelRealmList);
					conversationsModel.setLastMessage(messageBody);
					conversationsModel.setStatus(AppConstants.IS_WAITING);
					conversationsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
					conversationsModel.setCreatedOnline(true);
					realm1.copyToRealmOrUpdate(conversationsModel);
					
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW, lastConversationID));
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_GROUP_CONVERSATION_NEW_ROW, groupID));
					
					String FileType = null;
					if (!messagesModel.getImageFile().equals("null")) {
						FileType = "Image";
					} else if (!messagesModel.getVideoFile().equals("null")) {
						FileType = "Video";
					} else if (!messagesModel.getAudioFile().equals("null")) {
						FileType = "Audio";
					} else if (!messagesModel.getDocumentFile().equals("null")) {
						FileType = "Document";
					}
					
					
					mIntent = new Intent("new_group_message_notification_whatsclone");
					mIntent.putExtra("conversationID", lastConversationID);
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("groupID", groupID);
					mIntent.putExtra("groupImage", groupImage);
					mIntent.putExtra("username", senderName);
					mIntent.putExtra("file", FileType);
					mIntent.putExtra("senderPhone", senderPhone);
					mIntent.putExtra("groupName", groupName);
					mIntent.putExtra("message", messageBody);
					mIntent.putExtra("app", getPackageName());
					sendBroadcast(mIntent);
				});
				
			} else {
				if (messageBody.equals(AppConstants.CREATE_GROUP) && checkIfCreatedGroupMessageExist(groupID, realm, messageBody))
					return;
				realm.executeTransaction(realm1 -> {
					int lastID = RealmBackupRestore.getMessageLastId();
					int UnreadMessageCounter = 0;
					
					
					ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("groupID", groupID).findFirst();
					UnreadMessageCounter = Integer.parseInt(conversationsModel.getUnreadMessageCounter());
					UnreadMessageCounter++;
					
					RealmList<MessagesModel> messagesModelRealmList = conversationsModel.getMessages();
					MessagesModel messagesModel = new MessagesModel();
					messagesModel.setId(lastID);
					messagesModel.setDate(dateTmp);
					messagesModel.setRecipientID(0);
					messagesModel.setStatus(AppConstants.IS_WAITING);
					messagesModel.setUsername(senderName);
					messagesModel.setPhone(senderPhone);
					messagesModel.setSenderID(senderId);
					messagesModel.setGroup(true);
					messagesModel.setMessage(messageBody);
					messagesModel.setImageFile(image);
					messagesModel.setVideoFile(video);
					messagesModel.setAudioFile(audio);
					messagesModel.setDocumentFile(document);
					messagesModel.setVideoThumbnailFile(thumbnail);
					messagesModel.setFileUpload(true);
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
						messagesModel.setFileDownLoad(false);
						
					} else {
						messagesModel.setFileDownLoad(true);
					}
					messagesModel.setFileSize(fileSize);
					messagesModel.setDuration(duration);
					messagesModel.setGroupID(groupID);
					messagesModel.setConversationID(conversationsModel.getId());
					messagesModelRealmList.add(messagesModel);
					conversationsModel.setLastMessageId(lastID);
					conversationsModel.setRecipientUsername(groupName);
					conversationsModel.setRecipientImage(groupImage);
					conversationsModel.setGroupID(groupID);
					conversationsModel.setRecipientID(0);
					conversationsModel.setMessages(messagesModelRealmList);
					conversationsModel.setLastMessage(messageBody);
					conversationsModel.setGroup(true);
					conversationsModel.setCreatedOnline(true);
					conversationsModel.setStatus(AppConstants.IS_WAITING);
					conversationsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
					realm1.copyToRealmOrUpdate(conversationsModel);
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
					
					
					String FileType = null;
					if (!messagesModel.getImageFile().equals("null")) {
						FileType = "Image";
					} else if (!messagesModel.getVideoFile().equals("null")) {
						FileType = "Video";
					} else if (!messagesModel.getAudioFile().equals("null")) {
						FileType = "Audio";
					} else if (!messagesModel.getDocumentFile().equals("null")) {
						FileType = "Document";
					}
					
					mIntent = new Intent("new_group_message_notification_whatsclone");
					mIntent.putExtra("conversationID", conversationsModel.getId());
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("groupID", groupID);
					mIntent.putExtra("groupImage", groupImage);
					mIntent.putExtra("username", senderName);
					mIntent.putExtra("file", FileType);
					mIntent.putExtra("senderPhone", senderPhone);
					mIntent.putExtra("groupName", groupName);
					mIntent.putExtra("message", messageBody);
					mIntent.putExtra("app", getPackageName());
					sendBroadcast(mIntent);
					
					
				});
			}
			
			
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
			}
			MainService.RecipientMarkMessageAsDeliveredGroup(this, groupID);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (AppHelper.isActivityRunning(this, "activities.messages.MessagesActivity")) {
				AppHelper.LogCat("MessagesActivity running");
				MainService.RecipientMarkMessageAsSeenGroup(this, groupID);
			}
			
		} catch (JSONException e) {
			AppHelper.LogCat(e.getMessage());
		}
		
		EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
		notificationsManager.SetupBadger(this);
		
		
		if (!realm.isClosed())
			realm.close();
		
	}
	
	/**
	 * method to check for id 0
	 *
	 * @return return value
	 */
	public boolean checkIfZeroExist(Realm realm) {
		RealmQuery<MessagesModel> query = realm.where(MessagesModel.class).equalTo("id", 0);
		return query.count() != 0;
	}
	
	public static boolean checkIfCreatedGroupMessageExist(int groupId, Realm realm, String message) {
		RealmQuery<MessagesModel> query = realm.where(MessagesModel.class).equalTo("groupID", groupId).equalTo("isGroup", true).equalTo("message", message);
		
		return query.count() != 0;
		
	}
	
	/**
	 * method to check if a group conversation exist
	 *
	 * @param groupID this is the first parameter for  checkIfGroupConversationExist method
	 * @param realm   this is the second parameter for  checkIfGroupConversationExist  method
	 * @return return value
	 */
	public static boolean checkIfGroupConversationExist(int groupID, Realm realm) {
		RealmQuery<ConversationsModel> query = realm.where(ConversationsModel.class).equalTo("groupID", groupID);
		return query.count() != 0;
		
	}
	
	
}