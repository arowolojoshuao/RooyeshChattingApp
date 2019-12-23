package com.setayeshco.rooyesh.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.call.IncomingCallActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.popups.MessagesPopupActivity;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.models.groups.Groupid;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.messages.UpdateMessageModel;
import com.setayeshco.rooyesh.models.notifications.NotificationsModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.PusherContacts;
import com.setayeshco.rooyesh.models.users.contacts.UsersBlockModel;
import com.setayeshco.rooyesh.receivers.MessagesReceiverBroadcast;
import com.setayeshco.rooyesh.services.firebase.GcmServiceListener;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS;


/**
 * Created by Abderrahim El imame on 6/21/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class MainService extends Service {
	
	private Context mContext;
	public static Socket mSocket;
	private MessagesReceiverBroadcast mChangeListener;
	private Intent mIntent;
	private static Handler handler;
	private int mTries = 0;
	private NotificationsManager notificationsManager;
	
	//to keep socket connected
	// PowerManager.WakeLock wakeLock;
	
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 */
	public MainService() {
		// super(AppConstants.TAG);
		Log.i("TestService", "TEST");
	}
	
	/**
	 * method to disconnect user form server
	 */
	public static void disconnectSocket() {
		
		if (mSocket != null) {
			mSocket.off(Socket.EVENT_CONNECT);
			mSocket.off(Socket.EVENT_DISCONNECT);
			mSocket.off(Socket.EVENT_CONNECT_TIMEOUT);
			mSocket.off(Socket.EVENT_RECONNECT);
			
			mSocket.off(AppConstants.SOCKET_PING);
			mSocket.off(AppConstants.SOCKET_PONG);
			mSocket.off(AppConstants.SOCKET_IS_ONLINE);
			//user messages
			
			mSocket.off(AppConstants.SOCKET_SAVE_NEW_MESSAGE);
			mSocket.off(AppConstants.SOCKET_IS_LAST_SEEN);
			mSocket.off(AppConstants.SOCKET_IS_MESSAGE_DELIVERED);
			mSocket.off(AppConstants.SOCKET_IS_MESSAGE_SEEN);
			mSocket.off(AppConstants.SOCKET_UPDATE_REGISTER_ID);
			mSocket.off(AppConstants.SOCKET_IS_STOP_TYPING);
			mSocket.off(AppConstants.SOCKET_IS_TYPING);
			mSocket.off(AppConstants.SOCKET_CONNECTED);
			mSocket.off(AppConstants.SOCKET_DISCONNECTED);
			mSocket.off(AppConstants.SOCKET_NEW_USER_JOINED);
			mSocket.off(AppConstants.SOCKET_IMAGE_PROFILE_UPDATED);
			mSocket.off(AppConstants.SOCKET_IMAGE_GROUP_UPDATED);
			//groups
			mSocket.off(AppConstants.SOCKET_SAVE_NEW_MESSAGE_GROUP);
			mSocket.off(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING);
			mSocket.off(AppConstants.SOCKET_IS_MEMBER_TYPING);
			mSocket.off(AppConstants.SOCKET_IS_MESSAGE_GROUP_DELIVERED);
			mSocket.off(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEEN);
			
			//calls
			mSocket.off(AppConstants.SOCKET_CALL_USER_PING);
			mSocket.off(AppConstants.SOCKET_RESET_SOCKET_ID);
			mSocket.off(AppConstants.SOCKET_SIGNALING_SERVER);
			mSocket.off(AppConstants.SOCKET_MAKE_NEW_CALL);
			mSocket.off(AppConstants.SOCKET_RECEIVE_NEW_CALL);
			mSocket.off(AppConstants.SOCKET_REJECT_NEW_CALL);
			mSocket.off(AppConstants.SOCKET_ACCEPT_NEW_CALL);
			mSocket.off(AppConstants.SOCKET_HANGUP_CALL);
			
			
			mSocket.off(AppConstants.SOCKET_ADD_MEMBER_MSF_ALL);
			mSocket.off(AppConstants.SOCKET_SEND_TO_USER);
			mSocket.off(AppConstants.SOCKET_MEMBER_SEND_MSG_TO_ALL);
			
			
			mSocket.off(AppConstants.SOCKET_SEND_TO_MSG);
			
			mSocket.disconnect();
			mSocket.close();
			mSocket = null;
			
		}
		AppHelper.LogCat("disconnect in service");
	}
	
	/**
	 * method for server connection initialization
	 */
	
	public static void connectToServer2(Context mContext) {
//        mSocket = app.getSocket();
//        if (!mSocket.connected())
//            mSocket.connect();
	
	}
	
	public void connectToServer(Context mContext) {
		
		RooyeshApplication.connectSocket();
		RooyeshApplication app = (RooyeshApplication) getApplication();
		
		Log.i("testB", "connect");
		
		mSocket = app.getSocket();
		if (!mSocket.connected())
			mSocket.connect();
		
		mSocket.once(Socket.EVENT_CONNECT, args -> {
			mTries = 0;
			AppHelper.LogCat("New Connection chat is created " + mSocket.id());
			
			if (mSocket.id() != null) {
				PreferenceManager.setSocketID(this, mSocket.id());
				
				JSONObject json = new JSONObject();
				try {
					json.put("connected", true);
					json.put("connectedId", PreferenceManager.getID(mContext));
					json.put("userToken", PreferenceManager.getToken(mContext));
					json.put("socketId", PreferenceManager.getSocketID(mContext));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mSocket != null)
					mSocket.emit(AppConstants.SOCKET_CONNECTED, json);
				
				JSONObject json2 = new JSONObject();
				try {
					json2.put("connected", true);
					json2.put("senderId", PreferenceManager.getID(this));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mSocket != null)
					mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json2);
			} else {
				reconnect(mContext);
			}
		}).on(Socket.EVENT_CONNECT_ERROR, args -> {
			AppHelper.LogCat("EVENT_CONNECT_ERROR");
			for (Object o : args) {
				AppHelper.LogCat("object: " + o.toString());
				if (o instanceof SocketIOException)
					((SocketIOException) o).printStackTrace();
			}
		}).on(Socket.EVENT_DISCONNECT, args -> {
			AppHelper.LogCat("You  lost connection to chat server " + mSocket.id());
			
			
			JSONObject jsonConnected = new JSONObject();
			try {
				jsonConnected.put("connectedId", PreferenceManager.getID(mContext));
				jsonConnected.put("userToken", PreferenceManager.getToken(mContext));
				jsonConnected.put("socketId", PreferenceManager.getSocketID(mContext));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (mSocket != null)
				mSocket.emit(AppConstants.SOCKET_DISCONNECTED, jsonConnected);
			
			JSONObject json2 = new JSONObject();
			try {
				json2.put("connected", false);
				json2.put("senderId", PreferenceManager.getID(this));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (mSocket != null)
				mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json2);
		}).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
			AppHelper.LogCat("Socket EVENT_CONNECT_TIMEOUT ");
			reconnect(mContext);
		}).on(Socket.EVENT_RECONNECT, args -> {
			AppHelper.LogCat("Reconnect  EVENT_RECONNECT ");
			reconnect(mContext);
		}).on(AppConstants.SOCKET_PING, onPing);
		
		SenderMarkMessageAsDelivered();
		SenderMarkMessageAsSeen();
		MemberMarkMessageAsDelivered();
		notifyOtherUser();
		getNotifyFromOtherNewUser();
		getNotifyForImageProfileChanged();
		onReceiveNewCall();
		
		Storegroupid(mContext);
		getmessage(mContext);
		getmessagegroup(mContext);
		
		isUserConnected(mContext);
		checkIfUserIsOnline();
		updateRegisterId();
		
	}
	
	/**
	 * method to reconnect sockets
	 */
	public void reconnect(Context mContext) {
		if (mTries < 5) {
			mTries++;
			AppHelper.restartService();
			handler.postDelayed(() -> updateStatusDeliveredOffline(mContext), 1500);
		} else {
			RooyeshApplication.getInstance().stopService(new Intent(RooyeshApplication.getInstance(), MainService.class));
		}
		
	}
	
	private Emitter.Listener onPing = args -> {
		// AppHelper.LogCat("socket ping " + mSocket.connected());
		if (!mSocket.connected())
			mSocket.connect();
       /* if (mSocket == null) {
            RooyeshApplication app = (RooyeshApplication) getApplication();
            mSocket = app.getSocket();
        }


        if (mSocket != null) {
            if (!mSocket.connected())
                mSocket.connect();*/
		
		JSONObject data = (JSONObject) args[0];
		String ping;
		try {
			ping = data.getString("beat");
		} catch (JSONException e) {
			return;
		}
		if (ping.equals("1")) {
			mSocket.emit(AppConstants.SOCKET_PONG);
		}

//        }
	
	};
	
	/**
	 * method to receive notification if a new user Joined
	 */
	private void getNotifyFromOtherNewUser() {
		mSocket.on(AppConstants.SOCKET_NEW_USER_JOINED, args -> {
			final JSONObject jsonObject = (JSONObject) args[0];
			try {
				int senderId = jsonObject.getInt("senderId");
				String phone = jsonObject.getString("phone");
				if (senderId == PreferenceManager.getID(mContext)) return;
				if (UtilsPhone.checkIfContactExist(mContext, phone)) {
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_USER_JOINED, jsonObject));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});
	}
	
	private boolean checkIfGroupExist(int groupId, Realm realm) {
		RealmQuery<GroupsModel> query = realm.where(GroupsModel.class).equalTo("id", groupId);
		return query.count() != 0;
		
	}
	
	/**
	 * method when a user change the image profile
	 */
	private void getNotifyForImageProfileChanged() {
		mSocket.on(AppConstants.SOCKET_IMAGE_PROFILE_UPDATED, args -> {
			final JSONObject jsonObject = (JSONObject) args[0];
			try {
				int senderId = jsonObject.getInt("senderId");
				String phone = jsonObject.getString("phone");
				if (senderId == PreferenceManager.getID(mContext)) return;
				if (UtilsPhone.checkIfContactExist(mContext, phone)) {
					EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_IMAGE_PROFILE_UPDATED));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});
		mSocket.on(AppConstants.SOCKET_IMAGE_GROUP_UPDATED, args -> {
			final JSONObject jsonObject = (JSONObject) args[0];
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			try {
				int groupId = jsonObject.getInt("groupId");
				if (!checkIfGroupExist(groupId, realm)) return;
				EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_IMAGE_GROUP_UPDATED, groupId));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!realm.isClosed()) realm.close();
		});
	}
	
	/**
	 * method to send notification if i join to the app
	 */
	private void notifyOtherUser() {
		if (PreferenceManager.isNewUser(mContext)) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("senderId", PreferenceManager.getID(mContext));
				jsonObject.put("phone", PreferenceManager.getPhone(mContext));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mSocket.emit(AppConstants.SOCKET_NEW_USER_JOINED, jsonObject);
			PreferenceManager.setIsNewUser(mContext, false);
		}
	}
	
	
	/**
	 * method to check if user is online or not
	 */
	private void checkIfUserIsOnline() {
		
		if (mSocket != null) {
			mSocket.on(AppConstants.SOCKET_IS_ONLINE, args -> {
				Realm realm = RooyeshApplication.getRealmDatabaseInstance();
				final JSONObject data = (JSONObject) args[0];
				try {
					int senderID = data.getInt("senderId");
					if (senderID == PreferenceManager.getID(mContext)) return;
					if (data.getBoolean("connected")) {
						realm.executeTransaction(realm1 -> {
							ContactsModel userModel = realm1.where(ContactsModel.class).equalTo("id", senderID).findFirst();
							if (userModel != null) {
								userModel.setUserState(AppConstants.STATUS_USER_CONNECTED_STATE);
								realm1.copyToRealmOrUpdate(userModel);
							}
						});
					} else {
						realm.executeTransaction(realm1 -> {
							ContactsModel userModel = realm1.where(ContactsModel.class).equalTo("id", senderID).findFirst();
							if (userModel != null) {
								userModel.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
								realm1.copyToRealmOrUpdate(userModel);
							}
						});
					}
				} catch (JSONException e) {
					AppHelper.LogCat(e);
				}
				if (!realm.isClosed())
					realm.close();
			});
			
			mSocket.on(AppConstants.SOCKET_IS_LAST_SEEN, args -> {
				Realm realm = RooyeshApplication.getRealmDatabaseInstance();
				final JSONObject data = (JSONObject) args[0];
				try {
					
					int senderID = data.getInt("senderId");
					int recipientID = data.getInt("recipientId");
					String lastSeen = data.getString("lastSeen");
					if (recipientID != PreferenceManager.getID(this)) return;
					DateTime messageDate = UtilsTime.getCorrectDate(lastSeen);
					String finalDate = UtilsTime.convertDateToString(this, messageDate);
					realm.executeTransaction(realm1 -> {
						ContactsModel userModel = realm1.where(ContactsModel.class).equalTo("id", senderID).findFirst();
						if (userModel != null) {
							userModel.setUserState(AppConstants.STATUS_USER_LAST_SEEN_STATE + " " + finalDate);
							realm1.copyToRealmOrUpdate(userModel);
						}
					});
				} catch (JSONException e) {
					AppHelper.LogCat(e);
				}
				if (!realm.isClosed())
					realm.close();
			});
		}
	}
	
	/**
	 * method to check if user is connected to server
	 *
	 * @param mContext
	 */
	
	private void getmessagegroup(Context mContext) {
		Log.i("testB", "getmessagegroup");
		if (mSocket != null) {
			mSocket.on(AppConstants.SOCKET_MEMBER_SEND_MSG_TO_ALL, args -> {
				
				handler.postDelayed(() ->
				{
					Log.i("testB", "getmessagegroup2");
					final JSONObject data = (JSONObject) args[0];
					try {
//                    int connectedId = data.getInt("connectedId");
//                    String socketId = data.getString("socketId");
//                    boolean connected = data.getBoolean("connected");
//
//                    if (socketId.equals(PreferenceManager.getSocketID(mContext))) return;
//                    try {
//                        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
//                        ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                        if (contactsModel != null && UtilsPhone.checkIfContactExist(mContext, contactsModel.getPhone())) {
//                            if (connected) {
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is connected <---");
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOnline)));
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(socketId);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_CONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                            } else {
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(null);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOffline)));
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected  <---");
//                            }
//                        }
//                        realm.close();
//                    } catch (Exception e) {
//                        AppHelper.LogCat(" isUserConnected Exception mainService " + e.getMessage());
//                    } //// TODO: 4/7/17 hna luser is connected
						
						
						if (getgroupid((data.getInt("groupID")))) {
							if (data.getBoolean("isGroup")) {
								
								saveNewMessageGroup(data);
								String userphone = data.getString("phone");
								String messageBody = data.getString("messageBody");
								int recipientId = data.getInt("recipientId");
								int senderId = data.getInt("senderId");
								int conversationID = data.getInt("conversationId");
								String userImage = data.getString("image");
								
								
								//    if (Application != null && Application.equals(getActivity().getPackageName())) {
								if (AppHelper.isActivityRunning(mContext, "activities.messages.MessagesActivity")) {
									NotificationsModel notificationsModel = new NotificationsModel();
									notificationsModel.setConversationID(conversationID);
									//   notificationsModel.setFile(file);
									notificationsModel.setGroup(false);
									notificationsModel.setImage(userImage);
									notificationsModel.setPhone(userphone);
									notificationsModel.setMessage(messageBody);
									notificationsModel.setRecipientId(recipientId);
									notificationsModel.setSenderId(senderId);
									
									MessagesModel messagesModel = new MessagesModel();
									
									messagesModel.setMessage(messageBody);
									messagesModel.setSenderID(senderId);
									messagesModel.setRecipientID(recipientId);
									messagesModel.setPhone(userphone);
									// notificationsModel.setAppName(Application);
									EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_USER_NOTIFICATION, notificationsModel));
									EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
									
								} else {
									// if (file != null) {
									//   notificationsManager.showUserNotification(getContext(), conversationID, userphone, file, recipientId, userImage);
									// } else {
									notificationsManager.showUserNotification(mContext, conversationID, userphone, messageBody, recipientId, userImage);
									// }
									// }
								}
							}
						}
						
						
					} catch (JSONException e) {
						AppHelper.LogCat(e);
					}
				}, 500);
				
			});
		}
	}
	
	private void getmessage(Context mContext) {
		if (mSocket != null) {
			Log.i("testB", "getmessage");
			mSocket.on(AppConstants.SOCKET_SEND_TO_USER, args -> {
				
				handler.postDelayed(() ->
				{
					Log.i("testB", "getmessage2");
					final JSONObject data = (JSONObject) args[0];
					try {
//                    int connectedId = data.getInt("connectedId");
//                    String socketId = data.getString("socketId");
//                    boolean connected = data.getBoolean("connected");
//
//                    if (socketId.equals(PreferenceManager.getSocketID(mContext))) return;
//                    try {
//                        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
//                        ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                        if (contactsModel != null && UtilsPhone.checkIfContactExist(mContext, contactsModel.getPhone())) {
//                            if (connected) {
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is connected <---");
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOnline)));
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(socketId);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_CONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                            } else {
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(null);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOffline)));
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected  <---");
//                            }
//                        }
//                        realm.close();
//                    } catch (Exception e) {
//                        AppHelper.LogCat(" isUserConnected Exception mainService " + e.getMessage());
//                    } //// TODO: 4/7/17 hna luser is connected
						
						
						if (!data.getBoolean("isGroup")) {
							
							saveNewMessage(data);
							String userphone = data.getString("phone");
							String messageBody = data.getString("messageBody");
							int recipientId = data.getInt("recipientId");
							int senderId = data.getInt("senderId");
							int conversationID = data.getInt("conversationId");
							String userImage = data.getString("image");
							
							
							//    if (Application != null && Application.equals(getActivity().getPackageName())) {
							if (AppHelper.isActivityRunning(mContext, "activities.messages.MessagesActivity")) {
								NotificationsModel notificationsModel = new NotificationsModel();
								notificationsModel.setConversationID(conversationID);
								//   notificationsModel.setFile(file);
								notificationsModel.setGroup(false);
								notificationsModel.setImage(userImage);
								notificationsModel.setPhone(userphone);
								notificationsModel.setMessage(messageBody);
								notificationsModel.setRecipientId(recipientId);
								notificationsModel.setSenderId(senderId);
								
								MessagesModel messagesModel = new MessagesModel();
								
								messagesModel.setMessage(messageBody);
								messagesModel.setSenderID(senderId);
								messagesModel.setRecipientID(recipientId);
								messagesModel.setPhone(userphone);
								// notificationsModel.setAppName(Application);
								//  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_USER_NOTIFICATION, notificationsModel));
								//   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
								
							} else {
								// if (file != null) {
								//   notificationsManager.showUserNotification(getContext(), conversationID, userphone, file, recipientId, userImage);
								// } else {
								notificationsManager.showUserNotification(mContext, conversationID, userphone, messageBody, recipientId, userImage);
								// }
								// }
							}
						}
						
						
					} catch (JSONException e) {
						AppHelper.LogCat(e);
					}
				}, 500);
				
			});
		}
	}
	
	private void Storegroupid(Context mContext) {
		if (mSocket != null) {
			Log.i("testB", "Storegroupid");
			mSocket.on(AppConstants.SOCKET_ADD_MEMBER_MSF_ALL, args -> {
				
				handler.postDelayed(() ->
				{
					final JSONObject data = (JSONObject) args[0];
					try {
						Log.i("testB", "Storegroupid2");
//                    int connectedId = data.getInt("connectedId");
//                    String socketId = data.getString("socketId");
//                    boolean connected = data.getBoolean("connected");
//
//                    if (socketId.equals(PreferenceManager.getSocketID(mContext))) return;
//                    try {
//                        Realm realm = RooyeshApplication.getRealmDatabaseInstance();
//                        ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                        if (contactsModel != null && UtilsPhone.checkIfContactExist(mContext, contactsModel.getPhone())) {
//                            if (connected) {
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is connected <---");
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOnline)));
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(socketId);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_CONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                            } else {
//                                realm.executeTransactionAsync(realm1 -> {
//                                    ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
//                                    if (contactsModel1 != null) {
//                                        contactsModel1.setSocketId(null);
//                                        contactsModel1.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
//                                        realm1.copyToRealmOrUpdate(contactsModel1);
//                                    }
//                                });
//                                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOffline)));
//                                AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected  <---");
//                            }
//                        }
//                        realm.close();
//                    } catch (Exception e) {
//                        AppHelper.LogCat(" isUserConnected Exception mainService " + e.getMessage());
//                    } //// TODO: 4/7/17 hna luser is connected
						
						
						String stringid = data.getString("id");
						String groupid = data.getString("groupid");
						String name = data.getString("username");
						String[] arrayid = stringid.split(",");
						
						addgroupid(arrayid, groupid, name);
						
						
					} catch (JSONException e) {
						AppHelper.LogCat(e);
					}
				}, 500);
				
			});
		}
	}
	
	private static void isUserConnected(Context mContext) {
		if (mSocket != null) {
			mSocket.on(AppConstants.SOCKET_CONNECTED, args -> {
				final JSONObject data = (JSONObject) args[0];
				try {
					int connectedId = data.getInt("connectedId");
					String socketId = data.getString("socketId");
					boolean connected = data.getBoolean("connected");
					
					if (socketId.equals(PreferenceManager.getSocketID(mContext))) return;
					try {
						Realm realm = RooyeshApplication.getRealmDatabaseInstance();
						ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
						if (contactsModel != null && UtilsPhone.checkIfContactExist(mContext, contactsModel.getPhone())) {
							if (connected) {
								AppHelper.LogCat("User with id  --> " + connectedId + " is connected <---");
								EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOnline)));
								realm.executeTransactionAsync(realm1 -> {
									ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
									if (contactsModel1 != null) {
										contactsModel1.setSocketId(socketId);
										contactsModel1.setUserState(AppConstants.STATUS_USER_CONNECTED_STATE);
										realm1.copyToRealmOrUpdate(contactsModel1);
									}
								});
							} else {
								realm.executeTransactionAsync(realm1 -> {
									ContactsModel contactsModel1 = realm1.where(ContactsModel.class).equalTo("id", connectedId).findFirst();
									if (contactsModel1 != null) {
										contactsModel1.setSocketId(null);
										contactsModel1.setUserState(AppConstants.STATUS_USER_DISCONNECTED_STATE);
										realm1.copyToRealmOrUpdate(contactsModel1);
									}
								});
								EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_UPDATE_USER_STATE, mContext.getString(R.string.isOffline)));
								AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected  <---");
							}
						}
						realm.close();
					} catch (Exception e) {
						AppHelper.LogCat(" isUserConnected Exception mainService " + e.getMessage());
					} //// TODO: 4/7/17 hna luser is connected
					
					
				} catch (JSONException e) {
					AppHelper.LogCat(e);
				}
				
			});
		}
	}
	
	private static boolean checkIfUnsentMessagesExist(int recipientId, Realm realm, Context mContext) {
		RealmQuery<MessagesModel> query = realm.where(MessagesModel.class)
				.equalTo("status", AppConstants.IS_WAITING)
				.equalTo("recipientID", recipientId)
				.equalTo("isGroup", false)
				.equalTo("isFileUpload", true)
				.equalTo("senderID", PreferenceManager.getID(mContext));
		
		return query.count() != 0;
		
	}
	
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
			
			if (!GcmServiceListener.checkIfGroupConversationExist(groupID, realm)) {
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
					//  conversationsModel.getMessages().add(messagesModel);
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
				if (messageBody.equals(AppConstants.CREATE_GROUP) && GcmServiceListener.checkIfCreatedGroupMessageExist(groupID, realm, messageBody))
					return;
				realm.executeTransaction(realm1 -> {
					int lastID = RealmBackupRestore.getMessageLastId();
					int UnreadMessageCounter = 0;
					
					
					ConversationsModel conversationsModel;
					RealmQuery<ConversationsModel> conversationsModelRealmQuery = realm1.where(ConversationsModel.class).equalTo("groupID", groupID);
					conversationsModel = conversationsModelRealmQuery.findAll().first();
					
					
					//  ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("groupID", groupID).findFirst();
					UnreadMessageCounter = Integer.parseInt(conversationsModel.getUnreadMessageCounter());
					UnreadMessageCounter++;
					
					
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
					//  RealmList<MessagesModel> messagesModelRealmList2 = conversationsModel.getMessages();
					//  messagesModelRealmList2.add(messagesModel);
					
					conversationsModel.getMessages().add(messagesModel);
					
					conversationsModel.setLastMessageId(lastID);
					conversationsModel.setRecipientUsername(groupName);
					conversationsModel.setRecipientImage(groupImage);
					conversationsModel.setGroupID(groupID);
					
					Log.i("testgroup", "  " + conversationsModel.getGroupID() + "");
					Log.i("testgroupsize", "  " + conversationsModel.getMessages().size() + "");
					Log.i("testgroupsname", "  " + conversationsModel.getMessages().get(0).getMessage() + "");
					
					conversationsModel.setRecipientID(0);
					
					conversationsModel.setLastMessage(messageBody);
					conversationsModel.setGroup(true);
					conversationsModel.setCreatedOnline(true);
					conversationsModel.setStatus(AppConstants.IS_WAITING);
					conversationsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
					realm1.copyToRealmOrUpdate(conversationsModel);
					
					
					//   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					
					//   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
					
					
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
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationsModel.getId()));
					
					
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
	
	private void saveNewMessage(JSONObject data) {
		
		Log.w("arash message", data.toString());
		
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		try {
			int recipientId = data.getInt("recipientId");
			int senderId = data.getInt("senderId");
			//  int messageId = data.getInt("messageId");
			int messageId = 1000;
			String phone = data.getString("phone");
			String messageBody = data.getString("messageBody");
			String senderName = data.getString("senderName");
			String senderImage = data.getString("senderImage");
			// String senderImage = "";
			String date = data.getString("date");
			String video = data.getString("video");
			String thumbnail = data.getString("thumbnail");
			boolean isGroup = false;

//            if(data.getString("GroupName")!=null && !data.getString("GroupName").equals(""))
//                isGroup = true;
//            else
//                isGroup = false;
			String image = data.getString("image");
			String audio = data.getString("audio");
			String document = data.getString("document");
			String duration = data.getString("duration");
			String fileSize = data.getString("fileSize");
			
			if (senderId == PreferenceManager.getID(this)) return;
			
			int conversationID = getConversationId(recipientId, senderId, realm);
			if (conversationID == 0) {
				boolean finalIsGroup = isGroup;
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
					messagesModel.setGroup(finalIsGroup);
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
					conversationsModel1.setGroup(finalIsGroup);
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
					
					mIntent = new Intent("new_user_message_notification_whatsclone3");
					mIntent.putExtra("conversationID", lastConversationID);
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("senderId", recipientId);
					//   mIntent.putExtra("userImage", senderImage);
					mIntent.putExtra("username", senderName);
					mIntent.putExtra("file", FileType);
					mIntent.putExtra("phone", phone);
					mIntent.putExtra("messageId", messageId);
					mIntent.putExtra("message", messageBody);
					mIntent.putExtra("app", this.getPackageName());
					sendBroadcast(mIntent);
				});
			} else {
				
				boolean finalIsGroup1 = isGroup;
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
					messagesModel.setGroup(finalIsGroup1);
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
					conversationsModel.setGroup(finalIsGroup1);
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
					
					
					mIntent = new Intent("new_user_message_notification_whatsclone3");
					mIntent.putExtra("conversationID", conversationID);
					mIntent.putExtra("recipientID", senderId);
					mIntent.putExtra("senderId", recipientId);
					//  mIntent.putExtra("userImage", senderImage);
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
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d("SERVICCC", "SERVICEEEE .>>>> 1111");
		
		AppHelper.LogCat("MainService  has Created");
		//PowerManager pMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
       /* wakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, getString(R.string.app_name) + "WakeLock");
        wakeLock.acquire();*/
		
		// int[] test = {2,4,6,7};
		//   addgroupid(test);
		//   getgroupid(8);
		
		// int[]  test2 = {8};
		//   addgroupid(test2);
		//   getgroupid(8);
		
		mContext = getApplicationContext();
		handler = new Handler();
		connectToServer(mContext);
		notificationsManager = new NotificationsManager();
		mChangeListener = new MessagesReceiverBroadcast() {
			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			protected void MessageReceived(Context context, Intent intent) {
				String action = intent.getAction();
				switch (action) {
					case "new_user_message_notification_whatsclone3":
                       /* handler.postDelayed(() -> {

                        },500);*/
						handler.postDelayed(() ->
						{
							String Application = intent.getExtras().getString("app");
							String file = intent.getExtras().getString("file");
							String userphone = intent.getExtras().getString("phone");
							String messageBody = intent.getExtras().getString("message");
							int recipientId = intent.getExtras().getInt("recipientID");
							int senderId = intent.getExtras().getInt("senderId");
							int conversationID = intent.getExtras().getInt("conversationID");
							String userImage = intent.getExtras().getString("userImage");
							
							
							if (Application != null && Application.equals(mContext.getPackageName())) {
								if (AppHelper.isActivityRunning(mContext, "activities.messages.MessagesActivity")) {
									NotificationsModel notificationsModel = new NotificationsModel();
									notificationsModel.setConversationID(conversationID);
									notificationsModel.setFile(file);
									notificationsModel.setGroup(false);
									notificationsModel.setImage(userImage);
									notificationsModel.setPhone(userphone);
									notificationsModel.setMessage(messageBody);
									notificationsModel.setRecipientId(recipientId);
									notificationsModel.setSenderId(senderId);
									notificationsModel.setAppName(Application);
									//    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_USER_NOTIFICATION, notificationsModel));
								} else {
									if (file != null) {
										notificationsManager.showUserNotification(mContext, conversationID, userphone, file, recipientId, userImage);
									} else {
										notificationsManager.showUserNotification(mContext, conversationID, userphone, messageBody, recipientId, userImage);
									}
								}
							}
							
						}, 500);
						
						
						break;
					case "new_group_message_notification_whatsclone2":
						
						String application = intent.getExtras().getString("app");
						String File = intent.getExtras().getString("file");
						String userPhone = intent.getExtras().getString("senderPhone");
						String groupName = UtilsString.unescapeJava(intent.getExtras().getString("groupName"));
						String messageGroupBody = intent.getExtras().getString("message");
						int groupID = intent.getExtras().getInt("groupID");
						String groupImage = intent.getExtras().getString("groupImage");
						int conversationId = intent.getExtras().getInt("conversationID");
						String memberName;
						String name = UtilsPhone.getContactName(mContext, userPhone);
						if (name != null) {
							memberName = name;
						} else {
							memberName = userPhone;
						}
						
						
						String message;
						String userName = UtilsPhone.getContactName(mContext, userPhone);
						switch (messageGroupBody) {
							case AppConstants.CREATE_GROUP:
								if (userName != null) {
									message = "" + userName + " " + mContext.getString(R.string.he_created_this_group);
								} else {
									message = "" + userPhone + " " + mContext.getString(R.string.he_created_this_group);
								}
								
								
								break;
							case AppConstants.LEFT_GROUP:
								if (userName != null) {
									message = "" + userName + mContext.getString(R.string.he_left);
								} else {
									message = "" + userPhone + mContext.getString(R.string.he_left);
								}
								
								
								break;
							default:
								message = messageGroupBody;
								break;
						}
						
						/**
						 * this for default activity
						 */
						
						//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
						
						
						Bundle bundle = new Bundle();
						bundle.putInt("conversationID", conversationId);
						bundle.putInt("groupID", groupID);
						bundle.putBoolean("isGroup", true);
						MessagesFragment messageFragmentOk = new MessagesFragment();
						messageFragmentOk.setArguments(bundle);
						
						
						//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
						
						
						Intent messagingGroupIntent = new Intent(mContext, MessagesActivity.class);
						//  Intent messagingGroupPopupIntent = new Intent(mContext, MessagesPopupActivity.class);
						messagingGroupIntent.putExtra("conversationID", conversationId);
						messagingGroupIntent.putExtra("groupID", groupID);
						messagingGroupIntent.putExtra("isGroup", true);
						messagingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						/**
						 * this for popup activity
						 */
						Intent messagingGroupPopupIntent = new Intent(mContext, MessagesPopupActivity.class);
						messagingGroupPopupIntent.putExtra("conversationID", conversationId);
						messagingGroupPopupIntent.putExtra("groupID", groupID);
						messagingGroupPopupIntent.putExtra("isGroup", true);
						messagingGroupPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						
						if (application != null && application.equals(mContext.getPackageName())) {
							if (AppHelper.isActivityRunning(mContext, "activities.messages.MessagesActivity")) {
								NotificationsModel notificationsModel = new NotificationsModel();
								notificationsModel.setConversationID(conversationId);
								notificationsModel.setFile(File);
								notificationsModel.setGroup(true);
								notificationsModel.setImage(groupImage);
								notificationsModel.setPhone(userPhone);
								notificationsModel.setMessage(messageGroupBody);
								notificationsModel.setMemberName(memberName);
								notificationsModel.setGroupID(groupID);
								notificationsModel.setGroupName(groupName);
								notificationsModel.setAppName(application);
								//   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_NOTIFICATION, notificationsModel));
							} else {
								if (File != null) {
									notificationsManager.showGroupNotification(mContext, messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + File, groupID, groupImage);
								} else {
									notificationsManager.showGroupNotification(mContext, messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + message, groupID, groupImage);
								}
							}
						}
						
						
						break;
					case "new_user_joined_notification_whatsclone2":
						String Userphone = intent.getExtras().getString("phone");
						String MessageBody = intent.getExtras().getString("message");
						int RecipientId = intent.getExtras().getInt("recipientID");
						int ConversationID = intent.getExtras().getInt("conversationID");
						
						notificationsManager.showUserNotification(mContext, ConversationID, Userphone, MessageBody, RecipientId, null);
						break;
				}
				
			}
		};
		
		
		getApplication().registerReceiver(mChangeListener, new IntentFilter("new_user_message_notification_whatsclone3"));
		getApplication().registerReceiver(mChangeListener, new IntentFilter("new_group_message_notification_whatsclone3"));
		getApplication().registerReceiver(mChangeListener, new IntentFilter("new_user_joined_notification_whatsclone3"));
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AppHelper.LogCat("MainService  has started");
		return START_STICKY;
	}
	
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

//    @Override
//    protected void onHandleIntent(Intent intent) {
//    }
	
	@Override
	public void onDestroy() {
       /* if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }*/
		super.onDestroy();
		
		AppHelper.LogCat("MainService has stopped");
		notificationsManager.SetupBadger(mContext);
		// service finished
		if (mChangeListener != null)
			mContext.unregisterReceiver(mChangeListener);
		disconnectSocket();
		handler.removeCallbacksAndMessages(null);
		
	}
	
	/**
	 * method to check  for all unsent messages
	 */
	public synchronized static void unSentMessages(Context mContext) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		List<MessagesModel> messagesModelsList = realm.where(MessagesModel.class)
				.equalTo("status", AppConstants.IS_WAITING)
				.equalTo("isGroup", false)
				.equalTo("isFileUpload", true)
				.equalTo("senderID", PreferenceManager.getID(mContext))
				.findAllSorted("id", Sort.ASCENDING);
		
		AppHelper.LogCat("size " + messagesModelsList.size());
		if (messagesModelsList.size() != 0) {
			
			for (MessagesModel messagesModel : messagesModelsList) {
				sendMessages(messagesModel);
			}
		}
		realm.close();
		
	}
	
	/**
	 * method to send unsentMessages
	 *
	 * @param messagesModel this i parameter for sendMessages method
	 */
	public static void sendMessages(MessagesModel messagesModel) {
		
		UpdateMessageModel updateMessageModel = new UpdateMessageModel();
		updateMessageModel.setSenderId(messagesModel.getSenderID());
		updateMessageModel.setRecipientId(messagesModel.getRecipientID());
		updateMessageModel.setMessageId(messagesModel.getId());
		updateMessageModel.setConversationId(messagesModel.getConversationID());
		updateMessageModel.setMessageBody(messagesModel.getMessage());
		updateMessageModel.setSenderName(messagesModel.getUsername());
		updateMessageModel.setSenderImage("null");
		updateMessageModel.setPhone(messagesModel.getPhone());
		updateMessageModel.setDate(messagesModel.getDate());
		updateMessageModel.setVideo(messagesModel.getVideoFile());
		updateMessageModel.setThumbnail(messagesModel.getVideoThumbnailFile());
		updateMessageModel.setImage(messagesModel.getImageFile());
		updateMessageModel.setAudio(messagesModel.getAudioFile());
		updateMessageModel.setDocument(messagesModel.getDocumentFile());
		updateMessageModel.setFileSize(messagesModel.getFileSize());
		updateMessageModel.setDuration(messagesModel.getDuration());
		updateMessageModel.setGroup(messagesModel.isGroup());
		updateMessageModel.setRegistered_id(getRegisteredId(messagesModel.getRecipientID()));
		
		if (!messagesModel.isFileUpload()) return;
		MainService.sendMessage(updateMessageModel, false);
		
	}
	
	public static void sendMessage(UpdateMessageModel updateMessageModel, boolean forGroup) {
		if (forGroup) {
			APIHelper.initialApiUsersContacts().sendGroupMessage(updateMessageModel).subscribe(response -> {
				if (response.isSuccess()) {
					MemberMarkMessageAsSent(updateMessageModel.getGroupID());
				}
			}, throwable -> {
			
			});
		} else {
			APIHelper.initialApiUsersContacts().sendMessage(updateMessageModel).subscribe(response -> {
				if (response.isSuccess()) {
					if (response.isSuccess()) {
						makeMessageAsSent(updateMessageModel.getSenderId(), updateMessageModel.getMessageId());
					}
				}
			}, throwable -> {
			
			});
		}
		
	}
	
	private static String getRegisteredId(int recipientId) {
		String registered_id;
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", recipientId).findFirst();
		registered_id = contactsModel.getRegistered_id();
		if (!realm.isClosed()) realm.close();
		return registered_id;
	}
	
	/**
	 * method to send unsentMessages who has files
	 *
	 * @param messagesModel this i parameter for sendMessages method
	 */
	public static void sendMessagesFiles(MessagesModel messagesModel) {
		UpdateMessageModel updateMessageModel = new UpdateMessageModel();
		updateMessageModel.setSenderId(messagesModel.getSenderID());
		updateMessageModel.setRecipientId(messagesModel.getRecipientID());
		updateMessageModel.setMessageId(messagesModel.getId());
		updateMessageModel.setConversationId(messagesModel.getConversationID());
		updateMessageModel.setMessageBody(messagesModel.getMessage());
		updateMessageModel.setSenderName(messagesModel.getUsername());
		updateMessageModel.setSenderImage("null");
		updateMessageModel.setPhone(messagesModel.getPhone());
		updateMessageModel.setDate(messagesModel.getDate());
		updateMessageModel.setVideo(messagesModel.getVideoFile());
		updateMessageModel.setThumbnail(messagesModel.getVideoThumbnailFile());
		updateMessageModel.setImage(messagesModel.getImageFile());
		updateMessageModel.setAudio(messagesModel.getAudioFile());
		updateMessageModel.setDocument(messagesModel.getDocumentFile());
		updateMessageModel.setFileSize(messagesModel.getFileSize());
		updateMessageModel.setDuration(messagesModel.getDuration());
		updateMessageModel.setGroup(messagesModel.isGroup());
		updateMessageModel.setRegistered_id(getRegisteredId(messagesModel.getRecipientID()));
		
		if (!messagesModel.isFileUpload()) return;
		MainService.sendMessage(updateMessageModel, false);
	}
	
	
	/**
	 * method to  update status delivered when user was offline and come online
	 * and he has a new messages (unread)
	 *
	 * @param mContext
	 */
	
	private static void updateStatusDeliveredOffline(Context mContext) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		List<MessagesModel> messagesModels = realm.where(MessagesModel.class)
				.notEqualTo("recipientID", PreferenceManager.getID(mContext))
				.equalTo("status", AppConstants.IS_WAITING).findAll();
		if (messagesModels.size() != 0) {
			for (MessagesModel messagesModel : messagesModels) {
				RecipientMarkMessageAsDelivered(mContext, messagesModel.getId());
			}
		}
	}
	
	/**
	 * method to mark messages as delivered by recipient
	 *
	 * @param mContext
	 * @param messageId this is the  parameter for RecipientMarkMessageAsDelivered method
	 */
	public static void RecipientMarkMessageAsDelivered(Context mContext, int messageId) {
		try {
			JSONObject json = new JSONObject();
			json.put("senderId", PreferenceManager.getID(mContext));
			json.put("messageId", messageId);
			
			if (mSocket != null) {
				mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_DELIVERED, json);
			}
			
			
		} catch (Exception e) {
			AppHelper.LogCat(e);
		}
		AppHelper.LogCat("--> Recipient mark message as  delivered <--");
	}
	
	/**
	 * method to emit that message are seen by user
	 */
	public static void emitMessageSeen(Context mContext, int senderId) {
		JSONObject json = new JSONObject();
		try {
			json.put("recipientId", senderId);
			json.put("senderId", PreferenceManager.getID(mContext));
			
			if (mSocket != null)
				mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_SEEN, json);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * method to mark messages as delivered by recipient
	 *
	 * @param mContext
	 * @param groupId  this is the  parameter for RecipientMarkMessageAsDeliveredGroup method
	 */
	public static void RecipientMarkMessageAsDeliveredGroup(Context mContext, int groupId) {
		try {
			JSONObject json = new JSONObject();
			json.put("senderId", PreferenceManager.getID(mContext));
			json.put("groupId", groupId);
			
			if (mSocket != null) {
				mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_GROUP_DELIVERED, json);
			}
			
			
		} catch (Exception e) {
			AppHelper.LogCat(e);
		}
		AppHelper.LogCat("--> Recipient mark message as  delivered <--");
	}
	
	/**
	 * method to mark messages as seen by recipient
	 *
	 * @param mContext
	 * @param groupId  this is the  parameter for RecipientMarkMessageAsSeenGroup method
	 */
	public static void RecipientMarkMessageAsSeenGroup(Context mContext, int groupId) {
		try {
			JSONObject json = new JSONObject();
			json.put("senderId", PreferenceManager.getID(mContext));
			json.put("groupId", groupId);
			
			if (mSocket != null) {
				mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEEN, json);
			}
			
			
		} catch (Exception e) {
			AppHelper.LogCat(e);
		}
		AppHelper.LogCat("--> Recipient mark message as  delivered <--");
	}
	
	/**
	 * method to update status for a specific  message (as delivered by sender)
	 */
	private void SenderMarkMessageAsDelivered() {
		
		mSocket.on(AppConstants.SOCKET_IS_MESSAGE_DELIVERED, args -> {
			
			JSONObject data = (JSONObject) args[0];
			try {
				int senderId = data.getInt("senderId");
				if (senderId == PreferenceManager.getID(mContext))
					return;
				updateDeliveredStatus(data);
				AppHelper.LogCat("--> Sender mark message as  delivered: update status  <--");
				
			} catch (Exception e) {
				AppHelper.LogCat(e);
			}
			
		});
	}
	
	
	/**
	 * method to update status for a specific  message (as delivered by sender) in realm database
	 *
	 * @param data this is parameter for  updateDeliveredStatus
	 */
	private void updateDeliveredStatus(JSONObject data) {
		try {
			int messageId = data.getInt("messageId");
			int senderId = data.getInt("senderId");
			if (senderId == PreferenceManager.getID(mContext)) return;
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			realm.executeTransaction(realm1 -> {
				MessagesModel messagesModel = realm1.where(MessagesModel.class).equalTo("id", messageId).equalTo("status", AppConstants.IS_SENT).findFirst();
				if (messagesModel != null) {
					messagesModel.setStatus(AppConstants.IS_DELIVERED);
					realm1.copyToRealmOrUpdate(messagesModel);
					AppHelper.LogCat("Delivered successfully");
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_MESSAGES, messageId));
					EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS, messagesModel.getConversationID()));
				} else {
					AppHelper.LogCat("Delivered failed ");
				}
			});
			realm.close();
		} catch (JSONException e) {
			AppHelper.LogCat("Save data to realm delivered JSONException " + e.getMessage());
		}
	}
	
	
	public static boolean checkIfUserBlockedExist(int userId, Realm realm) {
		RealmQuery<UsersBlockModel> query = realm.where(UsersBlockModel.class).equalTo("contactsModel.id", userId);
		return query.count() != 0;
	}
	
	/**
	 * method to make message as sent
	 */
	private static void makeMessageAsSent(int SenderID, int messageId) {
		if (SenderID != PreferenceManager.getID(RooyeshApplication.getInstance()))
			return;
		updateStatusAsSentBySender(messageId);
		
		
	}
	
	/**
	 * method to update user register id firbase
	 */
	private void updateRegisterId() {
		
		mSocket.on(AppConstants.SOCKET_UPDATE_REGISTER_ID, args -> {
			JSONObject dataOn = (JSONObject) args[0];
			
			try {
				int userId = dataOn.getInt("userId");
				if (userId == PreferenceManager.getID(mContext))
					return;
				updateRegisterId(dataOn);
			} catch (JSONException e) {
				AppHelper.LogCat("Recipient is online  MainService" + e.getMessage());
			}
			
		});
	}
	
	private void updateRegisterId(JSONObject data) {
		try {
			int userId = data.getInt("userId");
			String register_id = data.getString("register_id");
			
			try {
				Realm realm = RooyeshApplication.getRealmDatabaseInstance();
				
				realm.executeTransaction(realm1 -> {
					ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", userId).findFirst();
					contactsModel.setRegistered_id(register_id);
					realm1.copyToRealmOrUpdate(contactsModel);
				});
				if (!realm.isClosed())
					realm.close();
				
			} catch (Exception e) {
				AppHelper.LogCat("null object Exception MainService" + e.getMessage());
			}
			
			
		} catch (JSONException e) {
			AppHelper.LogCat("updateRegisterId error  MainService" + e.getMessage());
		}
	}
	
	/**
	 * method to update status as seen by sender (if recipient have been seen the message)
	 */
	private void SenderMarkMessageAsSeen() {
		mSocket.on(AppConstants.SOCKET_IS_MESSAGE_SEEN, args -> {
			JSONObject data = (JSONObject) args[0];
			updateSeenStatus(data);
		});
		
	}
	
	
	/**
	 * method to get a conversation id by groupId
	 *
	 * @param groupId this is the first parameter for getConversationId method
	 * @param realm   this is the second parameter for getConversationId method
	 * @return conversation id
	 */
	private static int getConversationIdByGroupId(int groupId, Realm realm) {
		try {
			ConversationsModel conversationsModelNew = realm.where(ConversationsModel.class)
					.equalTo("groupID", groupId)
					.findAll().first();
			return conversationsModelNew.getId();
		} catch (Exception e) {
			AppHelper.LogCat("Conversation id  (group) Exception MainService  " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * method to update status as seen by sender (if recipient have been seen the message)
	 */
	private static void MemberMarkMessageAsSent(int groupId) {
		updateGroupSentStatus(groupId);
	}
	
	/**
	 * method to update status as delivered by sender (if recipient have been seen the message)
	 */
	private void MemberMarkMessageAsDelivered() {
		mSocket.on(AppConstants.SOCKET_IS_MESSAGE_GROUP_DELIVERED, args -> {
			JSONObject data = (JSONObject) args[0];
			// AppHelper.LogCat("SOCKET_IS_MESSAGE_GROUP_DELIVERED ");
			updateGroupDeliveredStatus(data);
		});
		mSocket.on(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEEN, args -> {
			JSONObject data = (JSONObject) args[0];
			// AppHelper.LogCat("SOCKET_IS_MESSAGE_GROUP_SEEN ");
			
			updateGroupSeenStatus(data);
		});
		
	}
	
	/**
	 * method to update status as delivered by sender
	 *
	 * @param data this is parameter for updateSeenStatus method
	 */
	private void updateGroupDeliveredStatus(JSONObject data) {
		
		
		try {
			int groupId = data.getInt("groupId");
			int senderId = data.getInt("senderId");
			AppHelper.LogCat("groupId " + groupId);
			AppHelper.LogCat("sen hhh " + senderId);
			if (senderId == PreferenceManager.getID(mContext)) return;
			
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			int ConversationID = getConversationIdByGroupId(groupId, realm);
			AppHelper.LogCat("conversation  id seen " + ConversationID);
			List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
					.equalTo("conversationID", ConversationID)
					.equalTo("isGroup", true)
					.equalTo("groupID", groupId)
					.equalTo("status", AppConstants.IS_SENT)
					.findAll();
			if (messagesModelsRealm.size() != 0) {
				for (MessagesModel messagesModel1 : messagesModelsRealm) {
					
					realm.executeTransaction(realm1 -> {
						MessagesModel messagesModel = realm1.where(MessagesModel.class)
								.equalTo("groupID", groupId)
								.equalTo("id", messagesModel1.getId())
								.equalTo("status", AppConstants.IS_SENT).findFirst();
						if (messagesModel != null) {
							messagesModel.setStatus(AppConstants.IS_DELIVERED);
							realm1.copyToRealmOrUpdate(messagesModel);
							AppHelper.LogCat("Delivered successfully MainService");
							
							EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_MESSAGES, messagesModel.getId()));
							
						} else {
							AppHelper.LogCat("Seen  failed MainService ");
						}
					});
					
				}
			}
			realm.close();
			EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_CONVERSATIONS, ConversationID));
			
		} catch (JSONException e) {
			AppHelper.LogCat("Save to realm seen MainService " + e.getMessage());
		}
		
	}
	
	/**
	 * method to update status as seen by sender (group)
	 *
	 * @param data this is parameter for updateSeenStatus method
	 */
	private void updateGroupSeenStatus(JSONObject data) {
		
		try {
			int groupId = data.getInt("groupId");
			int senderId = data.getInt("senderId");
			AppHelper.LogCat("groupId " + groupId);
			AppHelper.LogCat("sen " + senderId);
			if (senderId == PreferenceManager.getID(mContext)) return;
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			int ConversationID = getConversationIdByGroupId(groupId, realm);
			AppHelper.LogCat("conversation  id seen " + ConversationID);
			List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
					.equalTo("conversationID", ConversationID)
					.equalTo("isGroup", true)
					.equalTo("groupID", groupId)
					.beginGroup()
					.equalTo("status", AppConstants.IS_SENT)
					.or()
					.equalTo("status", AppConstants.IS_DELIVERED)
					.endGroup()
					.findAll();
			if (messagesModelsRealm.size() != 0) {
				for (MessagesModel messagesModel1 : messagesModelsRealm) {
					
					realm.executeTransaction(realm1 -> {
						MessagesModel messagesModel = realm1.where(MessagesModel.class)
								.equalTo("groupID", groupId)
								.equalTo("id", messagesModel1.getId())
								.beginGroup()
								.equalTo("status", AppConstants.IS_SENT)
								.or()
								.equalTo("status", AppConstants.IS_DELIVERED)
								.endGroup()
								.findFirst();
						if (messagesModel != null) {
							messagesModel.setStatus(AppConstants.IS_SEEN);
							realm1.copyToRealmOrUpdate(messagesModel);
							AppHelper.LogCat("seen successfully");
							EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_SEEN_FOR_MESSAGES, messagesModel.getId()));
							
						} else {
							AppHelper.LogCat("Seen  failed MainService (group)");
						}
					});
				}
			}
			realm.close();
			EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS, ConversationID));
			
		} catch (JSONException e) {
			AppHelper.LogCat("Save to realm seen " + e);
		}
		
	}
	
	
	/**
	 * method to update status as sent by sender
	 */
	private static void updateGroupSentStatus(int groupId) {
		int senderId = PreferenceManager.getID(RooyeshApplication.getInstance());
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		int ConversationID = getConversationIdByGroupId(groupId, realm);
		List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
				.equalTo("conversationID", ConversationID)
				.equalTo("isGroup", true)
				.equalTo("groupID", groupId)
				.equalTo("senderID", senderId)
				.equalTo("status", AppConstants.IS_WAITING)
				.findAll();
		if (messagesModelsRealm.size() != 0) {
			for (MessagesModel messagesModel1 : messagesModelsRealm) {
				
				realm.executeTransaction(realm1 -> {
					MessagesModel messagesModel = realm1.where(MessagesModel.class)
							.equalTo("isGroup", true)
							.equalTo("isFileUpload", true)
							.equalTo("groupID", groupId)
							.equalTo("senderID", senderId)
							.equalTo("id", messagesModel1.getId())
							.equalTo("status", AppConstants.IS_WAITING).findFirst();
					if (messagesModel != null) {
						messagesModel.setStatus(AppConstants.IS_SENT);
						realm1.copyToRealmOrUpdate(messagesModel);
						AppHelper.LogCat("Sent successfully MainService");
						EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_SENT_FOR_MESSAGES, messagesModel.getId()));
					} else {
						AppHelper.LogCat("Sent  failed  MainService");
					}
					EventBus.getDefault().post(new Pusher(EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS, ConversationID));
				});
				
			}
		}
		if (!realm.isClosed())
			realm.close();
		
	}
	
	/**
	 * method to update status as seen by sender (if recipient have been seen the message)  in realm database
	 *
	 * @param data this is parameter for updateSeenStatus method
	 */
	private void updateSeenStatus(JSONObject data) {
		
		try {
			int recipientId = data.getInt("recipientId");
			int senderId = data.getInt("senderId");
			if (senderId == PreferenceManager.getID(mContext)) return;
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			int ConversationID = getConversationId(senderId, recipientId, realm);
			List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
					.equalTo("conversationID", ConversationID)
					.equalTo("isGroup", false)
					.beginGroup()
					.equalTo("status", AppConstants.IS_DELIVERED)
					.or()
					.equalTo("status", AppConstants.IS_SENT)
					.endGroup()
					.findAll();
			if (messagesModelsRealm.size() != 0) {
				for (MessagesModel messagesModel1 : messagesModelsRealm) {
					
					realm.executeTransaction(realm1 -> {
						MessagesModel messagesModel = realm1.where(MessagesModel.class)
								.equalTo("recipientID", senderId)
								.equalTo("senderID", recipientId)
								.equalTo("id", messagesModel1.getId())
								.beginGroup()
								.equalTo("status", AppConstants.IS_DELIVERED)
								.or()
								.equalTo("status", AppConstants.IS_SENT)
								.endGroup()
								.findFirst();
						if (messagesModel != null) {
							messagesModel.setStatus(AppConstants.IS_SEEN);
							realm1.copyToRealmOrUpdate(messagesModel);
							AppHelper.LogCat("Seen successfully MainService");
							EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_SEEN_FOR_MESSAGES, messagesModel.getId()));
						} else {
							AppHelper.LogCat("Seen  failed  MainService");
						}
					});
				}
			}
			EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_IS_SEEN_FOR_CONVERSATIONS, ConversationID));
			realm.close();
		} catch (JSONException e) {
			AppHelper.LogCat("Save to realm seen  Exception" + e.getMessage());
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
	public static int getConversationId(int recipientId, int senderId, Realm realm) {
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
	
	
	/**
	 * method to update status for the send message by sender  (as sent message ) in realm  database
	 *
	 * @param messageId this is the first parameter for updateStatusAsSentBySender method
	 */
	private static void updateStatusAsSentBySender(int messageId) {
		
		
		try {
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			try {
				realm.executeTransaction(realm1 -> {
					MessagesModel messagesModel = realm1.where(MessagesModel.class).equalTo("id", messageId).findFirst();
					messagesModel.setStatus(AppConstants.IS_SENT);
					realm1.copyToRealmOrUpdate(messagesModel);
					EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MESSAGE_IS_SENT_FOR_MESSAGES, messageId));
					EventBus.getDefault().post(new Pusher(EVENT_BUS_NEW_MESSAGE_IS_SENT_FOR_CONVERSATIONS, messagesModel.getConversationID()));
				});
			} catch (Exception e) {
				AppHelper.LogCat(" Is sent messages Realm Error" + e.getMessage());
			}
			
			realm.close();
			
		} catch (Exception e) {
			AppHelper.LogCat("null object Exception MainService" + e.getMessage());
		}
		
		
	}
	
	
	/**
	 * method to send group messages
	 *
	 * @param messagesModel this is parameter of sendMessagesGroup method
	 */
	public static void sendMessagesGroup(Activity activity, ContactsModel mUsersModel, GroupsModel mGroupsModel, MessagesModel messagesModel) {
		
		JSONObject message = new JSONObject();
		try {
			
			if (mUsersModel != null && mUsersModel.getUsername() != null) {
				message.put("senderName", mUsersModel.getUsername());
			} else {
				message.put("senderName", "null");
			}
			if (mUsersModel != null)
				message.put("phone", mUsersModel.getPhone());
			else
				message.put("phone", null);
			
			
			if (mGroupsModel != null && mGroupsModel.getGroupImage() != null)
				message.put("GroupImage", mGroupsModel.getGroupImage());
			else
				message.put("GroupImage", "null");
			if (mGroupsModel != null && mGroupsModel.getGroupName() != null)
				message.put("GroupName", mGroupsModel.getGroupName());
			else
				message.put("GroupName", "null");
			
			message.put("messageBody", messagesModel.getMessage());
			message.put("senderId", messagesModel.getSenderID());
			message.put("recipientId", messagesModel.getRecipientID());
			if (mGroupsModel != null && mGroupsModel.getGroupName() != null)
				message.put("groupID", mGroupsModel.getId());
			else
				message.put("groupID", messagesModel.getGroupID());
			message.put("date", messagesModel.getDate());
			message.put("isGroup", true);
			message.put("image", messagesModel.getImageFile());
			message.put("video", messagesModel.getVideoFile());
			message.put("audio", messagesModel.getAudioFile());
			message.put("thumbnail", messagesModel.getVideoThumbnailFile());
			message.put("document", messagesModel.getDocumentFile());
			
			if (!messagesModel.getFileSize().equals("0"))
				message.put("fileSize", messagesModel.getFileSize());
			else
				message.put("fileSize", "0");
			
			if (!messagesModel.getDuration().equals("0"))
				message.put("duration", messagesModel.getDuration());
			else
				message.put("duration", "0");
			
			message.put("userToken", PreferenceManager.getToken(activity));
			
			
			UpdateMessageModel updateMessageModel = new UpdateMessageModel();
			try {
				updateMessageModel.setSenderId(message.getInt("senderId"));
				updateMessageModel.setRecipientId(message.getInt("recipientId"));
				updateMessageModel.setMessageBody(message.getString("messageBody"));
				updateMessageModel.setSenderName(message.getString("senderName"));
				updateMessageModel.setGroupName(message.getString("GroupName"));
				updateMessageModel.setGroupImage(message.getString("GroupImage"));
				updateMessageModel.setGroupID(message.getInt("groupID"));
				updateMessageModel.setDate(message.getString("date"));
				updateMessageModel.setPhone(message.getString("phone"));
				updateMessageModel.setVideo(message.getString("video"));
				updateMessageModel.setThumbnail(message.getString("thumbnail"));
				updateMessageModel.setImage(message.getString("image"));
				updateMessageModel.setAudio(message.getString("audio"));
				updateMessageModel.setDocument(message.getString("document"));
				updateMessageModel.setFileSize(message.getString("fileSize"));
				updateMessageModel.setDuration(message.getString("duration"));
				updateMessageModel.setGroup(message.getBoolean("isGroup"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			MainService.sendMessage(updateMessageModel, true);
		} catch (JSONException e) {
			AppHelper.LogCat(e.getMessage());
		}
		
		
	}
	
	
	private void onReceiveNewCall() {
		mSocket.on(AppConstants.SOCKET_RECEIVE_NEW_CALL, onReceiveNewCall);
	}
	
	/**
	 * Receive call emitter callback when others call you.
	 *
	 * @param args json value contain callerid, userid and caller name
	 */
	private Emitter.Listener onReceiveNewCall = args -> {
		AppHelper.LogCat("onReceiveNewCall called");
		JSONObject data = (JSONObject) args[0];
		try {
			String callerSocketId = data.getString("from");
			String callerPhone = data.getString("callerPhone");
			int callerID = data.getInt("callerID");
			String callerImage = data.getString("callerImage");
			boolean isVideoCall = data.getBoolean("isVideoCall");
			
			Realm realm = RooyeshApplication.getRealmDatabaseInstance();
			if (!checkIfUserBlockedExist(callerID, realm)) {
				if (!realm.isClosed())
					realm.close();
				Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(AppConstants.CALLER_SOCKET_ID, callerSocketId);
				intent.putExtra(AppConstants.USER_SOCKET_ID, PreferenceManager.getSocketID(this));
				intent.putExtra(AppConstants.CALLER_PHONE, callerPhone);
				intent.putExtra(AppConstants.CALLER_IMAGE, callerImage);
				intent.putExtra(AppConstants.CALLER_ID, callerID);
				intent.putExtra(AppConstants.IS_VIDEO_CALL, isVideoCall);
				intent.putExtra(AppConstants.USER_PHONE, PreferenceManager.getPhone(this));
				Log.d("SERVICCC", "SERVICEEEE .>>>> 22222");
				getApplicationContext().startActivity(intent);
			} else {
				try {
					JSONObject message = new JSONObject();
					message.put("userSocketId", PreferenceManager.getSocketID(this));
					message.put("callerSocketId", callerSocketId);
					message.put("reason", AppConstants.NO_ANSWER);
					mSocket.emit(AppConstants.SOCKET_REJECT_NEW_CALL, message);
				} catch (JSONException e) {
					AppHelper.LogCat(" JSONException IncomingCallActivity rejectCall " + e.getMessage());
				}
			}
		} catch (JSONException e) {
			AppHelper.LogCat("JSONException Call" + e.getMessage());
		}
		
	};
	
	private void addgroupid(String[] test, String groupids, String name) throws JSONException {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		for (int i = 0; i < test.length; i++) {
			String myid = String.valueOf(PreferenceManager.getID(mContext));
			if (test[i].equals(myid)) {
				realm.beginTransaction();
				Groupid groupid = realm.createObject(Groupid.class);
				groupid.setIds(Integer.parseInt(groupids));
				
				realm.commitTransaction();
				Intent messagingGroupIntent = new Intent(mContext, MessagesActivity.class);
				Intent messagingGroupPopupIntent = new Intent(mContext, MessagesPopupActivity.class);
				
				if (i != test.length - 1) {
					//  notificationsManager.showGroupNotification(mContext, messagingGroupIntent, messagingGroupPopupIntent, name, " دعوت شده اید " + name + " شما به گروه ", Integer.parseInt(groupids), null);
					
					JSONObject data = new JSONObject();
					data.put("senderId", 0);
					data.put("recipientId", 0);
					data.put("messageBody", "شما به این گروه دعوت شده اید");
					data.put("senderName", "");
					data.put("phone", "");
					data.put("GroupImage", "");
					data.put("GroupName", name);
					data.put("date", "");
					data.put("video", "null");
					data.put("thumbnail", "");
					data.put("image", "null");
					data.put("audio", "null");
					data.put("document", "null");
					data.put("duration", "");
					data.put("fileSize", "");
					data.put("groupID", Integer.parseInt(groupids));
					
					saveNewMessageGroup(data);
					
					
				}
				
				break;
			}
		}
		if (!realm.isClosed())
			realm.close();
		
		
	}
	
	private boolean getgroupid(int id) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		
		RealmQuery<Groupid> query = realm.where(Groupid.class).equalTo("ids", id);
		
		boolean f = query.count() != 0;
		
		return f;
		
	}
	
	
}