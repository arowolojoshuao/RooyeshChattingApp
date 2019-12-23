package com.setayeshco.rooyesh.fragments.home;


import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.messages.TransferMessageContactsActivity;
import com.setayeshco.rooyesh.activities.popups.MessagesPopupActivity;
import com.setayeshco.rooyesh.activities.profile.ProfileActivity;
import com.setayeshco.rooyesh.activities.settings.PreferenceSettingsManager;
import com.setayeshco.rooyesh.adapters.others.TextWatcherAdapter;
import com.setayeshco.rooyesh.adapters.recyclerView.messages.MessagesAdapter;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.animations.ViewAudioProxy;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.FilesManager;
import com.setayeshco.rooyesh.helpers.Files.backup.RealmBackupRestore;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.PermissionHandler;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.UtilsTime;
import com.setayeshco.rooyesh.helpers.call.CallManager;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.helpers.notifications.NotificationsManager;
import com.setayeshco.rooyesh.interfaces.LoadingData;
import com.setayeshco.rooyesh.interfaces.NetworkListener;
import com.setayeshco.rooyesh.models.groups.GroupsModel;
import com.setayeshco.rooyesh.models.groups.MembersGroupModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.messages.UpdateMessageModel;
import com.setayeshco.rooyesh.models.notifications.NotificationsModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.models.users.contacts.UsersBlockModel;
import com.setayeshco.rooyesh.presenters.messages.MessagesPresenterFrg;
import com.setayeshco.rooyesh.services.MainService;
import com.setayeshco.rooyesh.ui.HideShowScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.socket.client.Socket;

import static android.app.Activity.RESULT_OK;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_DELETE_CONVERSATION_ITEM;
import static com.setayeshco.rooyesh.app.AppConstants.EVENT_BUS_MESSAGE_COUNTER;

//import static com.google.android.gms.internal.zzagz.getActivity().runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements LoadingData, RecyclerView.OnItemTouchListener, ActionMode.Callback, View.OnClickListener, NetworkListener {
	
	
	public MessagesFragment() {
		// Required empty public constructor
	}
	
	View frgView;
	
	FloatingActionButton fabScrollDown;
	
	LinearLayout mView;
	RecyclerView messagesList;
	TextView AddContactBtn;
	TextView BlockContactBtn;
	TextView UnBlockContactBtn;
	FrameLayout blockLayout;
	ImageButton SendButton;
	ImageButton SendRecordButton;
	ImageButton PictureButton;
	
	ImageView EmoticonButton;
	ImageView btnAttach;
	
	ImageView keyboradBtn;
	
	EmojiconEditText messageWrapper;
	
	EmojiconTextView ToolbarTitle;
	
	Toolbar toolbar;
	
	ImageView ToolbarImage;
	
	TextView statusUser;
	LinearLayout ToolbarLinearLayout;
	ProgressBar mProgressBar;
	LinearLayout BackButton;
	LinearLayout SendMessageLayout;
	LinearLayout groupLeftSendMessageLayout;
	View sendMessagePanel;
	
	
	EmojIconActions emojIcon;
	
	final int MIN_INTERVAL_TIME = 2000;
	long mStartTime;
	private boolean emoticonShown = false;
	public Intent mIntent = null;
	private MessagesAdapter mMessagesAdapter;
	//public Context context;
	private String messageTransfer = null;
	private ContactsModel mUsersModel;
	private GroupsModel mGroupsModel;
	private ContactsModel mUsersModelRecipient;
	private String FileSize = "0";
	private String Duration = "0";
	private String FileImagePath = null;
	private String FileVideoThumbnailPath = null;
	private String FileVideoPath = null;
	private String FileAudioPath = null;
	private String FileDocumentPath = null;
	private MessagesPresenterFrg mMessagesPresenterFrg;
	private int ConversationID;
	private int groupID;
	private boolean isGroup;
	
	
	//for sockets
	private Socket mSocket;
	private int senderId;
	private int recipientId;
	private static final int TYPING_TIMER_LENGTH = 600;
	private boolean isTyping = false;
	private Handler mTypingHandler = new Handler();
	//   private Handler mTypingHandler ;
	private boolean isSeen = false;
	private boolean isOpen;
	private Realm realm;
	
	//for audio
	TextView recordTimeText;
	View recordPanel;
	View slideTextContainer;
	TextView slideToCancelText;
	private MediaRecorder mMediaRecorder = null;
	private float startedDraggingX = -1;
	private float distCanMove = convertToDp(80);
	private long startTime = 0L;
	private Timer recordTimer;
	
	/* for serach */
	ImageView closeBtn;
	TextInputEditText searchInput;
	ImageView clearBtn;
	View searchView;
	
	
	/**
	 * For Attachment container
	 */
	LinearLayout mFrameLayoutReveal;
	ImageView attachCamera;
	ImageView attachImage;
	ImageView attachAudio;
	ImageView attachDocument;
	ImageView attachVideo;
	ImageView attachRecordVideo;
	
	private Animator.AnimatorListener mAnimatorListenerOpen, mAnimatorListenerClose;
	private GestureDetectorCompat gestureDetector;
	private ActionMode actionMode;
	private boolean isLeft = false;
	private MemoryCache memoryCache;
	private PackageManager packageManager;
	private NotificationsManager notificationsManager;
	
	private Uri mProcessingPhotoUri;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		frgView = inflater.inflate(R.layout.fragment_messages, container, false);
		
		
		fabScrollDown = frgView.findViewById(R.id.fab_scroll);
		mView = frgView.findViewById(R.id.activity_messages);
		messagesList = frgView.findViewById(R.id.listMessages);
		AddContactBtn = frgView.findViewById(R.id.add_contact);
		BlockContactBtn = frgView.findViewById(R.id.block_user);
		UnBlockContactBtn = frgView.findViewById(R.id.unblock_user);
		blockLayout = frgView.findViewById(R.id.block_layout);
		SendButton = frgView.findViewById(R.id.send_button);
		SendRecordButton = frgView.findViewById(R.id.send_record_button);
		PictureButton = frgView.findViewById(R.id.pictureBtn);
		EmoticonButton = frgView.findViewById(R.id.emoticonBtn);
		keyboradBtn = frgView.findViewById(R.id.keyboradBtn);
		messageWrapper = frgView.findViewById(R.id.MessageWrapper);
		ToolbarTitle = frgView.findViewById(R.id.toolbar_title);
		toolbar = frgView.findViewById(R.id.toolbar);
		ToolbarImage = frgView.findViewById(R.id.toolbar_image);
		statusUser = frgView.findViewById(R.id.toolbar_status);
		ToolbarLinearLayout = frgView.findViewById(R.id.toolbarLinear);
		mProgressBar = frgView.findViewById(R.id.progress_bar);
		BackButton = frgView.findViewById(R.id.arrow_back);
		SendMessageLayout = frgView.findViewById(R.id.send_message);
		groupLeftSendMessageLayout = frgView.findViewById(R.id.groupSend);
		sendMessagePanel = frgView.findViewById(R.id.send_message_panel);
		btnAttach = frgView.findViewById(R.id.btnAttach);
		
		
		//for audio
		recordTimeText = frgView.findViewById(R.id.recording_time_text);
		recordPanel = frgView.findViewById(R.id.record_panel);
		slideTextContainer = frgView.findViewById(R.id.slide_text_container);
		slideToCancelText = frgView.findViewById(R.id.slideToCancelText);
		
		
		
		/* for serach */
		closeBtn = frgView.findViewById(R.id.close_btn_search_view);
		searchInput = frgView.findViewById(R.id.search_input);
		clearBtn = frgView.findViewById(R.id.clear_btn_search_view);
		searchView = frgView.findViewById(R.id.app_bar_search_view);
		
		
		/**
		 * For Attachment container
		 */
		mFrameLayoutReveal = frgView.findViewById(R.id.items_container);
		attachCamera = frgView.findViewById(R.id.attach_camera);
		attachImage = frgView.findViewById(R.id.attach_image);
		attachAudio = frgView.findViewById(R.id.attach_audio);
		attachDocument = frgView.findViewById(R.id.attach_document);
		attachVideo = frgView.findViewById(R.id.attach_video);
		attachRecordVideo = frgView.findViewById(R.id.attach_record_video);
		setHasOptionsMenu(true);
		
		
		return frgView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		Configuration configuration = getActivity().getResources().getConfiguration();
		if (configuration.smallestScreenWidthDp >= 600) {
			toolbar.setVisibility(View.GONE);
		} else {
			toolbar.setVisibility(View.VISIBLE);
		}
		
		btnAttach.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View view1 = getActivity().getCurrentFocus();
				if (view1 != null) {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
				}
				
				if (!isOpen) {
					isOpen = true;
					animateItems(true);
					
				} else {
					isOpen = false;
					animateItems(false);
				}
			}
		});
		
		setHasOptionsMenu(true);
		//  ButterKnife.bind(getActivity());
		realm = RooyeshApplication.getRealmDatabaseInstance();
		memoryCache = new MemoryCache();
		
		if (this.getArguments() != null) {
			if (this.getArguments().containsKey("recipientID")) {
				recipientId = this.getArguments().getInt("recipientID");
			}
			if (this.getArguments().containsKey("groupID")) {
				groupID = this.getArguments().getInt("groupID");
			}
			
			if (this.getArguments().containsKey("conversationID")) {
				ConversationID = this.getArguments().getInt("conversationID");
			}
			if (this.getArguments().containsKey("isGroup")) {
				isGroup = this.getArguments().getBoolean("isGroup");
			}
			
		} else if (getActivity().getIntent().getExtras() != null) {
			if (getActivity().getIntent().hasExtra("recipientID")) {
				recipientId = getActivity().getIntent().getExtras().getInt("recipientID");
			}
			if (getActivity().getIntent().hasExtra("groupID")) {
				groupID = getActivity().getIntent().getExtras().getInt("groupID");
			}
			
			if (getActivity().getIntent().hasExtra("conversationID")) {
				ConversationID = getActivity().getIntent().getExtras().getInt("conversationID");
			}
			if (getActivity().getIntent().hasExtra("isGroup")) {
				isGroup = getActivity().getIntent().getExtras().getBoolean("isGroup");
			}
			
		}
		
		
		connectToChatServer();
		senderId = PreferenceManager.getID(getActivity());
		initializerSearchView(searchInput, clearBtn);
		initializerView();
		setTypeFaces();
		packageManager = getActivity().getPackageManager();
		mMessagesPresenterFrg = new MessagesPresenterFrg(this);
		mMessagesPresenterFrg.onCreate();
		notificationsManager = new NotificationsManager();
		initializerMessageWrapper();
		
		if (this.getArguments() != null) {
			if (this.getArguments().containsKey("messageCopied")) {
				ArrayList<String> messageCopied = this.getArguments().getStringArrayList("messageCopied");
				for (String message : messageCopied) {
					messageTransfer = message;
					new Handler().postDelayed(this::sendMessage2, 50);
				}
			} else if (this.getArguments().containsKey("filePathList")) {
				ArrayList<String> filePathList = this.getArguments().getStringArrayList("filePathList");
				File fileVideo = null;
				for (String filepath : filePathList) {
					if (FilesManager.getMimeType(filepath).equals("video/mp4")) {
						FileVideoPath = filepath;
						MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
						int duration = mp.getDuration();
						Duration = String.valueOf(duration);
						mp.release();
						File file = new File(FileVideoPath);
						FileSize = String.valueOf(file.length());
						Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
						try {
							fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
						} catch (IOException e) {
							AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
						}
						FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
					} else if (FilesManager.getMimeType(filepath).equals("audio/mp3")) {
						FileAudioPath = filepath;
						MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileAudioPath));
						int duration = mp.getDuration();
						Duration = String.valueOf(duration);
						mp.release();
					} else if (FilesManager.getMimeType(filepath).equals("application/pdf")) {
						FileDocumentPath = filepath;
						File file = null;
						if (FileDocumentPath != null) {
							file = new File(FileDocumentPath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
							
						}
					} else if (FilesManager.getMimeType(filepath).equals("image/jpeg") || FilesManager.getMimeType(filepath).equals("image/png")) {
						FileImagePath = filepath;
						File file = null;
						if (FileImagePath != null) {
							file = new File(FileImagePath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
							
						}
						
						
					}
					sendMessage2();
				}
			} else if (this.getArguments().containsKey("filePath")) {
				String filepath = this.getArguments().getString("filePath");
				File fileVideo = null;
				if (FilesManager.getMimeType(filepath).equals("video/mp4")) {
					FileVideoPath = filepath;
					MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
					int duration = mp.getDuration();
					Duration = String.valueOf(duration);
					mp.release();
					File file = new File(FileVideoPath);
					FileSize = String.valueOf(file.length());
					Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
					try {
						fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
					} catch (IOException e) {
						AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
					}
					FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
				} else if (FilesManager.getMimeType(filepath).equals("audio/mp3")) {
					FileAudioPath = filepath;
					MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileAudioPath));
					int duration = mp.getDuration();
					Duration = String.valueOf(duration);
					mp.release();
				} else if (FilesManager.getMimeType(filepath).equals("application/pdf")) {
					FileDocumentPath = filepath;
					File file = null;
					if (FileDocumentPath != null) {
						file = new File(FileDocumentPath);
					}
					if (file != null) {
						FileSize = String.valueOf(file.length());
						
					}
				} else if (FilesManager.getMimeType(filepath).equals("image/jpeg") || FilesManager.getMimeType(filepath).equals("image/png")) {
					FileImagePath = filepath;
					File file = null;
					if (FileImagePath != null) {
						file = new File(FileImagePath);
					}
					if (file != null) {
						FileSize = String.valueOf(file.length());
						
					}
				}
				sendMessage2();
			}
			
		} else if (getActivity().getIntent().getExtras() != null) {
			if (getActivity().getIntent().hasExtra("messageCopied")) {
				ArrayList<String> messageCopied = getActivity().getIntent().getExtras().getStringArrayList("messageCopied");
				for (String message : messageCopied) {
					messageTransfer = message;
					new Handler().postDelayed(this::sendMessage2, 50);
				}
			} else if (getActivity().getIntent().hasExtra("filePathList")) {
				ArrayList<String> filePathList = getActivity().getIntent().getExtras().getStringArrayList("filePathList");
				File fileVideo = null;
				for (String filepath : filePathList) {
					if (FilesManager.getMimeType(filepath).equals("video/mp4")) {
						FileVideoPath = filepath;
						MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
						int duration = mp.getDuration();
						Duration = String.valueOf(duration);
						mp.release();
						File file = new File(FileVideoPath);
						FileSize = String.valueOf(file.length());
						Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
						try {
							fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
						} catch (IOException e) {
							AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
						}
						FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
					} else if (FilesManager.getMimeType(filepath).equals("audio/mp3")) {
						FileAudioPath = filepath;
						MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileAudioPath));
						int duration = mp.getDuration();
						Duration = String.valueOf(duration);
						mp.release();
					} else if (FilesManager.getMimeType(filepath).equals("application/pdf")) {
						FileDocumentPath = filepath;
						File file = null;
						if (FileDocumentPath != null) {
							file = new File(FileDocumentPath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
							
						}
					} else if (FilesManager.getMimeType(filepath).equals("image/jpeg") || FilesManager.getMimeType(filepath).equals("image/png")) {
						FileImagePath = filepath;
						File file = null;
						if (FileImagePath != null) {
							file = new File(FileImagePath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
							
						}
						
						
					}
					sendMessage2();
				}
			} else if (getActivity().getIntent().hasExtra("filePath")) {
				String filepath = getActivity().getIntent().getExtras().getString("filePath");
				File fileVideo = null;
				if (FilesManager.getMimeType(filepath).equals("video/mp4")) {
					FileVideoPath = filepath;
					MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
					int duration = mp.getDuration();
					Duration = String.valueOf(duration);
					mp.release();
					File file = new File(FileVideoPath);
					FileSize = String.valueOf(file.length());
					Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
					try {
						fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
					} catch (IOException e) {
						AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
					}
					FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
				} else if (FilesManager.getMimeType(filepath).equals("audio/mp3")) {
					FileAudioPath = filepath;
					MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileAudioPath));
					int duration = mp.getDuration();
					Duration = String.valueOf(duration);
					mp.release();
				} else if (FilesManager.getMimeType(filepath).equals("application/pdf")) {
					FileDocumentPath = filepath;
					File file = null;
					if (FileDocumentPath != null) {
						file = new File(FileDocumentPath);
					}
					if (file != null) {
						FileSize = String.valueOf(file.length());
						
					}
				} else if (FilesManager.getMimeType(filepath).equals("image/jpeg") || FilesManager.getMimeType(filepath).equals("image/png")) {
					FileImagePath = filepath;
					File file = null;
					if (FileImagePath != null) {
						file = new File(FileImagePath);
					}
					if (file != null) {
						FileSize = String.valueOf(file.length());
						
					}
				}
				sendMessage2();
			}
			
		}
		
		
		//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
		
		
		mAnimatorListenerOpen = new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			
			}
			
			@Override
			public void onAnimationEnd(Animator animator) {
				mFrameLayoutReveal.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationCancel(Animator animator) {
			
			}
			
			@Override
			public void onAnimationRepeat(Animator animator) {
			}
		};
		
		mAnimatorListenerClose = new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			}
			
			@Override
			public void onAnimationEnd(Animator animator) {
				mFrameLayoutReveal.setVisibility(View.GONE);
			}
			
			@Override
			public void onAnimationCancel(Animator animator) {
			
			}
			
			@Override
			public void onAnimationRepeat(Animator animator) {
			
			}
		};
		
		mFrameLayoutReveal.setOnClickListener(view -> {
			if (isOpen) {
				isOpen = false;
				animateItems(false);
				
			}
		});
		
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		
		if (isGroup)
			new Handler().postDelayed(() -> unSentMessagesGroup(groupID), 1000);
		else
			new Handler().postDelayed(() -> unSentMessagesForARecipient(recipientId, false), 1000);
		
		
		//end onActivityCreated...............................................
		
	}


//.................................
	
	private void socket_recieve_msg() {
		
		Log.d("vvvvv", "vvvvvvvvvvvvvvvvv");
		//......................?.


/*       JSONObject json = new JSONObject();
       try {
           json.put("recipientId", recipientId);
           json.put("senderId", senderId);

           mSocket.emit(AppConstants.SOCKET_RECIVE_MSG, json);

       } catch (JSONException e) {
           e.printStackTrace();
       }*/
		
		
		mSocket.on(AppConstants.SOCKET_RECIVE_MSG, args -> getActivity().runOnUiThread(() -> {
			AppHelper.LogCat("SOCKET_RECIVE_MSG ");
			JSONObject data = (JSONObject) args[0];
			try {
				
				int senderID = data.getInt("senderId");
				int recipientID = data.getInt("recipientId");
				String messageBody = data.getString("messageBody");
				Toast.makeText(getContext(), "1234 : " + data, Toast.LENGTH_LONG).show();
				
				
			} catch (Exception e) {
				AppHelper.LogCat(e);
			}
		}));
		
		
		//......................
		
		
	}
//.................................
	
	/**
	 * method to connect to the chat sever by socket
	 */
	private void connectToChatServer() {
		
		RooyeshApplication app = (RooyeshApplication) getActivity().getApplication();
		mSocket = app.getSocket();
		
		if (mSocket == null) {
			RooyeshApplication.connectSocket();
			mSocket = app.getSocket();
		}
		
		if (!mSocket.connected())
			mSocket.connect();
		emitUserIsOnline();
		socket_recieve_msg();
		setTypingEvent();
		setTypingEventmessage();
		if (isGroup) {
			//  AppHelper.LogCat("here group seen");
		} else {
			if (!checkIfUserBlockedExist(recipientId, realm)) {
				checkIfUserIsOnline();
				emitMessageSeen();
				socket_recieve_msg();
			}
		}
		
		
	}
	
	
	private void emitUserIsOnline() {
		
		if (!checkIfUserBlockedExist(recipientId, realm)) {
			
			JSONObject json = new JSONObject();
			try {
				json.put("connected", true);
				json.put("senderId", senderId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json);
			
		}
	}
	
	
	private boolean checkIfUserBlockedExist(int userId, Realm realm) {
		RealmQuery<UsersBlockModel> query = realm.where(UsersBlockModel.class).equalTo("contactsModel.id", userId);
		return query.count() != 0;
	}
	
	
	/**
	 * method to set user typing event
	 */
	private void setTypingEvent() {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		if (isGroup) {
			mSocket.on(AppConstants.SOCKET_IS_MEMBER_TYPING, args -> getActivity().runOnUiThread(() -> {
				
				JSONObject data = (JSONObject) args[0];
				try {
					
					int senderID = data.getInt("senderId");
					
					if (!checkIfUserBlockedExist(senderID, realm)) {
						ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", senderID).findFirst();
						String finalName;
						String name = UtilsPhone.getContactName(getActivity(), contactsModel.getPhone());
						if (name != null) {
							finalName = name;
						} else {
							finalName = contactsModel.getPhone();
						}
						int groupId = data.getInt("groupId");
						if (groupId == groupID) {
							if (senderID == PreferenceManager.getID(getActivity())) return;
							updateGroupMemberStatus(AppConstants.STATUS_USER_TYPING, finalName);
						}
					}
					
				} catch (Exception e) {
					AppHelper.LogCat(e);
				}
			}));
			
			mSocket.on(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING, args -> getActivity().runOnUiThread(() -> {
				updateGroupMemberStatus(AppConstants.STATUS_USER_STOP_TYPING, null);
				
			}));
		} else {
			if (!checkIfUserBlockedExist(recipientId, realm)) {
				mSocket.on(AppConstants.SOCKET_IS_TYPING, args -> getActivity().runOnUiThread(() -> {
					AppHelper.LogCat("SOCKET_IS_TYPING ");
					JSONObject data = (JSONObject) args[0];
					try {
						
						int senderID = data.getInt("senderId");
						int recipientID = data.getInt("recipientId");
						if (senderID == recipientId && recipientID == senderId) {
							updateUserStatus(AppConstants.STATUS_USER_TYPING, null);
						}
						
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				}));
				
				mSocket.on(AppConstants.SOCKET_IS_STOP_TYPING, args -> getActivity().runOnUiThread(() -> {
					AppHelper.LogCat("SOCKET_IS_STOP_TYPING ");
					JSONObject data = (JSONObject) args[0];
					try {
						int senderID = data.getInt("senderId");
						int recipientID = data.getInt("recipientId");
						if (senderID == recipientId && recipientID == senderId) {
							updateUserStatus(AppConstants.STATUS_USER_STOP_TYPING, null);
						}
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				}));
				mSocket.on(AppConstants.SOCKET_IS_LAST_SEEN, args -> getActivity().runOnUiThread(() -> {
					JSONObject data = (JSONObject) args[0];
					try {
						int senderID = data.getInt("senderId");
						int recipientID = data.getInt("recipientId");
						String lastSeen = data.getString("lastSeen");
						if (senderID == recipientId && recipientID == senderId) {
							DateTime messageDate = UtilsTime.getCorrectDate(lastSeen);
							String finalDate = UtilsTime.convertDateToString(getActivity(), messageDate);
							realm.executeTransaction(realm1 -> {
								ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", recipientId).findFirst();
								contactsModel.setUserState(AppConstants.STATUS_USER_LAST_SEEN_STATE + " " + finalDate);
								realm1.copyToRealmOrUpdate(contactsModel);
								
							});
							updateUserStatus(AppConstants.STATUS_USER_LAST_SEEN, finalDate);
						}
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				}));
			}
		}
		if (!realm.isClosed())
			realm.close();
		
	}
	
	private void setTypingEventmessage() {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		if (isGroup) {
			mSocket.on(AppConstants.SOCKET_MEMBER_SEND_MSG_TO_ALL2, args -> getActivity().runOnUiThread(() -> {
				
				JSONObject data = (JSONObject) args[0];
//                try {
//
//                    int senderID = data.getInt("senderId");
//
//                    if (!checkIfUserBlockedExist(senderID, realm)) {
//                        ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", senderID).findFirst();
//                        String finalName;
//                        String name = UtilsPhone.getContactName(getActivity(), contactsModel.getPhone());
//                        if (name != null) {
//                            finalName = name;
//                        } else {
//                            finalName = contactsModel.getPhone();
//                        }
//                        int groupId = data.getInt("groupId");
//                        if (groupId == groupID) {
//                            if (senderID == PreferenceManager.getID(getActivity())) return;
//                            updateGroupMemberStatus(AppConstants.STATUS_USER_TYPING, finalName);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    AppHelper.LogCat(e);
//                }
				
				String userPhone = null;
				try {
					userPhone = data.getString("phone");
					
					String groupName = UtilsString.unescapeJava(data.getString("GroupName"));
					String messageGroupBody = data.getString("messageBody");
					int groupID = data.getInt("groupID");
					String groupImage = data.getString("GroupImage");
					//    int conversationId = data.getInt("conversationID");
					
					int conversationId = 2;
					
					String memberName;
					String name = UtilsPhone.getContactName(getContext(), userPhone);
					if (name != null) {
						memberName = name;
					} else {
						memberName = userPhone;
					}
					
					
					String message;
					String userName = UtilsPhone.getContactName(getContext(), userPhone);
					switch (messageGroupBody) {
						case AppConstants.CREATE_GROUP:
							if (userName != null) {
								message = "" + userName + " " + getContext().getString(R.string.he_created_this_group);
							} else {
								message = "" + userPhone + " " + getContext().getString(R.string.he_created_this_group);
							}
							
							
							break;
						case AppConstants.LEFT_GROUP:
							if (userName != null) {
								message = "" + userName + getContext().getString(R.string.he_left);
							} else {
								message = "" + userPhone + getContext().getString(R.string.he_left);
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
					
					
					Intent messagingGroupIntent = new Intent(getContext(), MessagesActivity.class);
					messagingGroupIntent.putExtra("conversationID", conversationId);
					messagingGroupIntent.putExtra("groupID", groupID);
					messagingGroupIntent.putExtra("isGroup", true);
					messagingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					/**
					 * this for popup activity
					 */
					Intent messagingGroupPopupIntent = new Intent(getContext(), MessagesPopupActivity.class);
					messagingGroupPopupIntent.putExtra("conversationID", conversationId);
					messagingGroupPopupIntent.putExtra("groupID", groupID);
					messagingGroupPopupIntent.putExtra("isGroup", true);
					messagingGroupPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					//   if (application != null && application.equals(getContext().getPackageName())) {
					
					
					if (AppHelper.isActivityRunning(getContext(), "activities.messages.MessagesActivity")) {
						NotificationsModel notificationsModel = new NotificationsModel();
						notificationsModel.setConversationID(conversationId);
						//  notificationsModel.setFile(File);
						notificationsModel.setGroup(true);
						notificationsModel.setImage(groupImage);
						notificationsModel.setPhone(userPhone);
						notificationsModel.setMessage(messageGroupBody);
						notificationsModel.setMemberName(memberName);
						notificationsModel.setGroupID(groupID);
						notificationsModel.setGroupName(groupName);
						
						
						MessagesModel messagesModel = new MessagesModel();
						
						messagesModel.setMessage(messageGroupBody);
						messagesModel.setSenderID(senderId);
						messagesModel.setRecipientID(recipientId);
						messagesModel.setPhone(userPhone);
						messagesModel.setGroupID(groupID);
						messagesModel.setGroup(true);
						
						
						//  notificationsModel.setAppName(application);
						//  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_NOTIFICATION, notificationsModel));
						//  EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
					} else {
						// if (File != null)
						// {
						//     notificationsManager.showGroupNotification(getContext(), messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + File, groupID, groupImage);
						// }
						// else
						//      {
						notificationsManager.showGroupNotification(getContext(), messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + message, groupID, groupImage);
						//  }
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}));
			
			mSocket.on(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING, args -> getActivity().runOnUiThread(() -> {
				updateGroupMemberStatus(AppConstants.STATUS_USER_STOP_TYPING, null);
				
			}));
		} else {
			if (!checkIfUserBlockedExist(recipientId, realm)) {
				mSocket.on(AppConstants.SOCKET_SEND_TO_USER2, args -> getActivity().runOnUiThread(() -> {
					AppHelper.LogCat("SOCKET_IS_TYPING ");
					JSONObject data = (JSONObject) args[0];
					try {

//                        int senderID = data.getInt("senderId");
//                        int recipientID = data.getInt("recipientId");
//                        if (senderID == recipientId && recipientID == senderId) {
//                            updateUserStatus(AppConstants.STATUS_USER_TYPING, null);
						//  handler.postDelayed(() ->
						//  {
						//    String Application = data.getString("app");
						//  String file = data.getString("file");
						
						if (!data.getBoolean("isGroup")) {
							
							String userphone = data.getString("phone");
							String messageBody = data.getString("messageBody");
							int recipientId = data.getInt("recipientId");
							int senderId = data.getInt("senderId");
							int conversationID = data.getInt("conversationId");
							String userImage = data.getString("image");
							
							
							//    if (Application != null && Application.equals(getActivity().getPackageName())) {
							if (AppHelper.isActivityRunning(getContext(), "activities.messages.MessagesActivity")) {
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
								//   EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_USER_NOTIFICATION, notificationsModel));
								//    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW, messagesModel));
								
							} else {
								// if (file != null) {
								//   notificationsManager.showUserNotification(getContext(), conversationID, userphone, file, recipientId, userImage);
								// } else {
								notificationsManager.showUserNotification(getContext(), conversationID, userphone, messageBody, recipientId, userImage);
								// }
								// }
							}
						} else {
							
							//    String application = data.getString("app");
							//     String File = data.getString("file");
							String userPhone = data.getString("senderPhone");
							String groupName = UtilsString.unescapeJava(data.getString("groupName"));
							String messageGroupBody = data.getString("message");
							int groupID = data.getInt("groupID");
							String groupImage = data.getString("groupImage");
							int conversationId = data.getInt("conversationID");
							String memberName;
							String name = UtilsPhone.getContactName(getContext(), userPhone);
							if (name != null) {
								memberName = name;
							} else {
								memberName = userPhone;
							}
							
							
							String message;
							String userName = UtilsPhone.getContactName(getContext(), userPhone);
							switch (messageGroupBody) {
								case AppConstants.CREATE_GROUP:
									if (userName != null) {
										message = "" + userName + " " + getContext().getString(R.string.he_created_this_group);
									} else {
										message = "" + userPhone + " " + getContext().getString(R.string.he_created_this_group);
									}
									
									
									break;
								case AppConstants.LEFT_GROUP:
									if (userName != null) {
										message = "" + userName + getContext().getString(R.string.he_left);
									} else {
										message = "" + userPhone + getContext().getString(R.string.he_left);
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
							
							
							Intent messagingGroupIntent = new Intent(getContext(), MessagesActivity.class);
							messagingGroupIntent.putExtra("conversationID", conversationId);
							messagingGroupIntent.putExtra("groupID", groupID);
							messagingGroupIntent.putExtra("isGroup", true);
							messagingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							/**
							 * this for popup activity
							 */
							Intent messagingGroupPopupIntent = new Intent(getContext(), MessagesPopupActivity.class);
							messagingGroupPopupIntent.putExtra("conversationID", conversationId);
							messagingGroupPopupIntent.putExtra("groupID", groupID);
							messagingGroupPopupIntent.putExtra("isGroup", true);
							messagingGroupPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							
							//   if (application != null && application.equals(getContext().getPackageName())) {
							
							
							if (AppHelper.isActivityRunning(getContext(), "activities.messages.MessagesActivity")) {
								NotificationsModel notificationsModel = new NotificationsModel();
								notificationsModel.setConversationID(conversationId);
								//  notificationsModel.setFile(File);
								notificationsModel.setGroup(true);
								notificationsModel.setImage(groupImage);
								notificationsModel.setPhone(userPhone);
								notificationsModel.setMessage(messageGroupBody);
								notificationsModel.setMemberName(memberName);
								notificationsModel.setGroupID(groupID);
								notificationsModel.setGroupName(groupName);
								//  notificationsModel.setAppName(application);
								EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GROUP_NOTIFICATION, notificationsModel));
							} else {
								// if (File != null)
								// {
								//     notificationsManager.showGroupNotification(getContext(), messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + File, groupID, groupImage);
								// }
								// else
								//      {
								notificationsManager.showGroupNotification(getContext(), messagingGroupIntent, messagingGroupPopupIntent, groupName, memberName + " : " + message, groupID, groupImage);
								//  }
							}
							//    }
							
							
						}
						
						//  },500);
						
						
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				}));
				
				mSocket.on(AppConstants.SOCKET_SEND_TO_USER2, args -> getActivity().runOnUiThread(() -> {
					AppHelper.LogCat("SOCKET_IS_STOP_TYPING ");
					JSONObject data = (JSONObject) args[0];
					try {
						int senderID = data.getInt("senderId");
						int recipientID = data.getInt("recipientId");
						if (senderID == recipientId && recipientID == senderId) {
							updateUserStatus(AppConstants.STATUS_USER_STOP_TYPING, null);
						}
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				}));
//                mSocket.on(AppConstants.SOCKET_SEND_TO_USER2, args -> getActivity().runOnUiThread(() -> {
//                    JSONObject data = (JSONObject) args[0];
//                    try {
//                        int senderID = data.getInt("senderId");
//                        int recipientID = data.getInt("recipientId");
//                        String lastSeen = data.getString("lastSeen");
//                        if (senderID == recipientId && recipientID == senderId) {
//                            DateTime messageDate = UtilsTime.getCorrectDate(lastSeen);
//                            String finalDate = UtilsTime.convertDateToString(getActivity(), messageDate);
//                            realm.executeTransaction(realm1 -> {
//                                ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", recipientId).findFirst();
//                                contactsModel.setUserState(AppConstants.STATUS_USER_LAST_SEEN_STATE + " " + finalDate);
//                                realm1.copyToRealmOrUpdate(contactsModel);
//
//                            });
//                            updateUserStatus(AppConstants.STATUS_USER_LAST_SEEN, finalDate);
//                        }
//                    } catch (Exception e) {
//                        AppHelper.LogCat(e);
//                    }
//                }));
			}
		}
		if (!realm.isClosed())
			realm.close();
		
	}
	
	
	private void updateUserStatus(int statusUserTyping, String lastTime) {
		if (isGroup) return;
		Activity activity = getActivity();
		if (activity != null && isAdded()) {
			
			
			if (!checkIfUserBlockedExist(recipientId, realm)) {
				switch (statusUserTyping) {
					case AppConstants.STATUS_USER_TYPING:
						showStatus();
						statusUser.setText(getString(R.string.isTyping));
						AppHelper.LogCat("typing...");
						break;
					case AppConstants.STATUS_USER_DISCONNECTED:
						showStatus();
						statusUser.setText(getString(R.string.isOffline));
						AppHelper.LogCat("Offline...");
						break;
					case AppConstants.STATUS_USER_CONNECTED:
						showStatus();
						statusUser.setText(getString(R.string.isOnline));
						AnimationsUtil.slideStatus(statusUser);
						AppHelper.LogCat("Online...");
						break;
					case AppConstants.STATUS_USER_STOP_TYPING:
						showStatus();
						statusUser.setText(getString(R.string.isOnline));
						break;
					case AppConstants.STATUS_USER_LAST_SEEN:
						showStatus();
						statusUser.setText(getString(R.string.lastSeen) + " " + lastTime);
						AnimationsUtil.slideStatus(statusUser);
						break;
					
					
				}
			}
		}
	}
	
	
	private void showStatus() {
		TransitionManager.beginDelayedTransition(mView);
		statusUser.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * method to update  group members  to show them on toolbar status
	 *
	 * @param statusUserTyping this is the first parameter for  updateGroupMemberStatus method
	 * @param memberName       this is the second parameter for updateGroupMemberStatus method
	 */
	private void updateGroupMemberStatus(int statusUserTyping, String memberName) {
		StringBuilder names = new StringBuilder();
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		List<MembersGroupModel> groups = realm.where(MembersGroupModel.class).equalTo("groupID", groupID).equalTo("Deleted", false).equalTo("isLeft", false).findAll();
		
		int arraySize = groups.size();
		if (arraySize != 0) {
			for (int x = 0; x < arraySize; x++) {
				if (x <= 1) {
					String finalName;
					if (groups.get(x).getUserId() == PreferenceManager.getID(getActivity())) {
						finalName = "You";
						
					} else {
						String phone = UtilsPhone.getContactName(getActivity(), groups.get(x).getPhone());
						if (phone != null) {
							try {
								finalName = phone.substring(0, 7);
							} catch (Exception e) {
								AppHelper.LogCat(e);
								finalName = phone;
							}
							
						} else {
							finalName = groups.get(x).getPhone().substring(0, 7);
						}
						
					}
					names.append(finalName);
					names.append(",");
					
				}
				
			}
		} else {
			names.append("");
		}
		
		String groupsNames = UtilsString.removelastString(names.toString());
		switch (statusUserTyping) {
			case AppConstants.STATUS_USER_TYPING:
				statusUser.setVisibility(View.VISIBLE);
				statusUser.setText(memberName + " " + getString(R.string.isTyping));
				break;
			case AppConstants.STATUS_USER_STOP_TYPING:
				statusUser.setVisibility(View.VISIBLE);
				statusUser.setText(groupsNames);
				break;
			default:
				statusUser.setVisibility(View.VISIBLE);
				statusUser.setText(groupsNames);
				break;
		}
		
		if (!realm.isClosed()) realm.close();
	}
	
	
	/**
	 * method to emit that message are seen by user
	 */
	private void emitMessageSeen() {
		if (isGroup) {
			MainService.RecipientMarkMessageAsSeenGroup(getActivity(), groupID);
		} else {
			JSONObject json = new JSONObject();
			try {
				json.put("recipientId", recipientId);
				json.put("senderId", senderId);
				
				mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_SEEN, json);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * method to check if user is online or not
	 */
	private void checkIfUserIsOnline() {
		mSocket.on(AppConstants.SOCKET_IS_ONLINE, args -> getActivity().runOnUiThread(() -> {
			final JSONObject data = (JSONObject) args[0];
			try {
				int senderID = data.getInt("senderId");
				if (senderID == recipientId) {
					if (data.getBoolean("connected")) {
						updateUserStatus(AppConstants.STATUS_USER_CONNECTED, null);
					} else {
						updateUserStatus(AppConstants.STATUS_USER_DISCONNECTED, null);
					}
				}
			} catch (JSONException e) {
				AppHelper.LogCat(e);
			}
			
		}));
	}
	
	
	/**
	 * method to initialize the search view
	 *
	 * @param searchInput    this is the  first parameter for initializerSearchView method
	 * @param clearSearchBtn this is the second parameter for initializerSearchView method
	 */
	public void initializerSearchView(TextInputEditText searchInput, ImageView clearSearchBtn) {
		
		final Context context = getActivity();
		searchInput.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				AppHelper.LogCat("Has focused");
				emitMessageSeen();
				socket_recieve_msg();
				if (isGroup) {
					new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
				} else {
					new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
				}
			}
			
		});
		searchInput.addTextChangedListener(new TextWatcherAdapter() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				clearSearchBtn.setVisibility(View.GONE);
			}
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mMessagesAdapter.setString(s.toString());
				Search(s.toString().trim());
				clearSearchBtn.setVisibility(View.VISIBLE);
			}
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				
				if (s.length() == 0) {
					clearSearchBtn.setVisibility(View.GONE);
				}
			}
		});
		
	}
	
	
	/**
	 * method to start searching
	 *
	 * @param string this  is the parameter for Search method
	 */
	public void Search(String string) {
		final List<MessagesModel> filteredModelList;
		filteredModelList = FilterList(string);
		if (filteredModelList.size() != 0) {
			mMessagesAdapter.animateTo(filteredModelList);
			messagesList.scrollToPosition(0);
		}
	}
	
	
	/**
	 * method to filter the list
	 *
	 * @param query this is parameter for FilterList method
	 * @return this what method will return
	 */
	private List<MessagesModel> FilterList(String query) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		List<MessagesModel> messagesModels = null;
		if (isGroup) {
			
			messagesModels = realm.where(MessagesModel.class)
					.contains("message", query, Case.INSENSITIVE)
					.equalTo("conversationID", ConversationID)
					.equalTo("isGroup", true).findAllSorted("id", Sort.ASCENDING);
		} else {
			
			
			if (ConversationID == 0) {
				try {
					ConversationsModel conversationsModel = realm.where(ConversationsModel.class)
							.beginGroup()
							.equalTo("RecipientID", recipientId)
							.or()
							.equalTo("RecipientID", senderId)
							.endGroup().findAll().first();
					
					conversationsModel.setSenderID(senderId);
					
					messagesModels = realm.where(MessagesModel.class)
							.contains("message", query, Case.INSENSITIVE)
							.equalTo("conversationID", conversationsModel.getId())
							.equalTo("isGroup", false)
							.findAllSorted("id", Sort.ASCENDING);
					
				} catch (Exception e) {
					AppHelper.LogCat(" Conversation Exception MessagesPopupActivity" + e.getMessage());
				}
			} else {
				messagesModels = realm.where(MessagesModel.class)
						.equalTo("conversationID", ConversationID)
						.contains("message", query, Case.INSENSITIVE)
						.equalTo("isGroup", false)
						.findAllSorted("id", Sort.ASCENDING);
			}
			
		}
		realm.close();
		
		return messagesModels;
	}
	
	private int convertToDp(float value) {
		return (int) Math.ceil(1 * value);
	}
	
	
	/**
	 * method initialize the view
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void initializerView() {
		// setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar();
		if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
		}
		mMessagesAdapter = new MessagesAdapter(realm);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager.setStackFromEnd(true);
		messagesList.setLayoutManager(layoutManager);
		messagesList.setAdapter(mMessagesAdapter);
		messagesList.setItemAnimator(new DefaultItemAnimator());
		messagesList.getItemAnimator().setChangeDuration(0);
		
		//fix slow recyclerview start
		messagesList.setHasFixedSize(true);
		messagesList.setItemViewCacheSize(10);
		messagesList.setDrawingCacheEnabled(true);
		messagesList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		///fix slow recyclerview end
		
		messagesList.addOnItemTouchListener(this);
		fabScrollDown.setOnClickListener(v -> messagesList.smoothScrollToPosition(messagesList.getAdapter().getItemCount()));
		messagesList.addOnScrollListener(
				new HideShowScrollListener() {
					@Override
					public void onHide() {
						fabScrollDown.hide();
					}
					
					@Override
					public void onShow() {
						fabScrollDown.show();
					}
				});
		
		gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewBenOnGestureListener());
		String ImageUrl = PreferenceManager.getWallpaper(getActivity());
		if (ImageUrl != null) {
			Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, getActivity(), PreferenceManager.getID(getActivity()), AppConstants.USER, AppConstants.ROW_WALLPAPER);
			if (bitmap != null) {
				BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
				mView.setBackground(bitmapDrawable);
			} else {
				mView.setBackground(AppHelper.getDrawable(getActivity(), R.drawable.bg_msgs_rect));
			}
		} else {
			mView.setBackground(AppHelper.getDrawable(getActivity(), R.drawable.bg_msgs_rect));
		}
		
		
		EmoticonButton.setOnClickListener(v -> {
			if (!emoticonShown) {
				emoticonShown = true;
				emojIcon = new EmojIconActions(getActivity(), mView, messageWrapper, EmoticonButton);
				emojIcon.setIconsIds(R.drawable.ic_keyboard_gray_24dp, R.drawable.ic_emoticon_24dp);
				emojIcon.ShowEmojIcon();
				
			}
			
		});
		slideToCancelText.setText(R.string.slide_to_cancel_audio);
		
		
		SendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//  sendMessage();
				sendMessage2();
			}
		});
//        SendButton.setOnClickListener(v ->
//
//                sendMessage();
//                sendMessage2();
//
//
//        );
		AddContactBtn.setOnClickListener(v -> addNewContact());
		
		BlockContactBtn.setOnClickListener(v -> {
			blockContact();
		});
		UnBlockContactBtn.setOnClickListener(v -> {
			unBlockContact();
		});
		SendRecordButton.setOnTouchListener((view, motionEvent) -> {
			setDraggingAnimation(motionEvent, view);
			return true;
			
		});
		PictureButton.setOnClickListener(v -> launchAttachCamera());
		attachCamera.setOnClickListener(view -> launchAttachCamera());
		attachImage.setOnClickListener(view -> launchImageChooser());
		attachVideo.setOnClickListener(view -> launchVideoChooser());
		attachRecordVideo.setOnClickListener(view -> launchAttachRecordVideo());
		attachDocument.setOnClickListener(view -> launchDocumentChooser());
		attachAudio.setOnClickListener(view -> launchAudioChooser());
		
		ToolbarLinearLayout.setOnClickListener(v -> {
			if (isGroup) {
				if (AppHelper.isAndroid5()) {
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), new Pair<>(ToolbarImage, "userAvatar"), new Pair<>(ToolbarTitle, "userName"));
					mIntent = new Intent(getActivity(), ProfileActivity.class);
					mIntent.putExtra("groupID", groupID);
					mIntent.putExtra("isGroup", true);
					
					startActivity(mIntent, options.toBundle());
					AnimationsUtil.setSlideInAnimation(getActivity());
				} else {
					mIntent = new Intent(getActivity(), ProfileActivity.class);
					mIntent.putExtra("groupID", groupID);
					mIntent.putExtra("isGroup", true);
					startActivity(mIntent);
					AnimationsUtil.setSlideInAnimation(getActivity());
				}
			} else {
				if (AppHelper.isAndroid5()) {
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), new Pair<>(ToolbarImage, "userAvatar"), new Pair<>(ToolbarTitle, "userName"));
					mIntent = new Intent(getActivity(), ProfileActivity.class);
					mIntent.putExtra("userID", recipientId);
					mIntent.putExtra("isGroup", false);
					startActivity(mIntent, options.toBundle());
					AnimationsUtil.setSlideInAnimation(getActivity());
				} else {
					mIntent = new Intent(getActivity(), ProfileActivity.class);
					mIntent.putExtra("userID", recipientId);
					mIntent.putExtra("isGroup", false);
					startActivity(mIntent);
					AnimationsUtil.setSlideInAnimation(getActivity());
				}
			}
		});
		BackButton.setOnClickListener(v -> {
			mMessagesAdapter.stopAudio();
			
			if (notificationsManager.getManager()) {
				if (isGroup)
					notificationsManager.cancelNotification(groupID);
				else
					notificationsManager.cancelNotification(recipientId);
			}
			
			if (isGroup) {
				mMessagesPresenterFrg.updateGroupConversationStatus();
			} else {
				mMessagesPresenterFrg.updateConversationStatus();
			}
			getActivity().finish();
			AnimationsUtil.setSlideOutAnimation(getActivity());
			
			
		});
		
		
	}
	
	
	private class RecyclerViewBenOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}
		
		public void onLongPress(MotionEvent e) {
			AppHelper.LogCat(" onLongPress ");
			try {
				View view = messagesList.findChildViewUnder(e.getX(), e.getY());
				int currentPosition = messagesList.getChildAdapterPosition(view);
				MessagesModel messagesModel = mMessagesAdapter.getItem(currentPosition);
				if (messagesModel.isFileUpload() && messagesModel.isFileDownLoad()) {
					if (actionMode != null) {
						return;
					}
					actionMode = getActivity().startActionMode(MessagesFragment.this);
					ToggleSelection(currentPosition);
				}
				super.onLongPress(e);
			} catch (Exception e1) {
				AppHelper.LogCat(" onLongPress " + e1.getMessage());
			}
		}
		
	}
	
	
	/**
	 * method to toggle the selection
	 *
	 * @param position this is parameter for  ToggleSelection method
	 */
	private void ToggleSelection(int position) {
		mMessagesAdapter.toggleSelection(position);
		String title = String.format("%s selected", mMessagesAdapter.getSelectedItemCount());
		actionMode.setTitle(title);
	}
	
	/**
	 * method to launch audio chooser
	 */
	private void launchAudioChooser() {
		if (isOpen) {
			isOpen = false;
			animateItems(false);
		}
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
			AppHelper.LogCat("Read data permission already granted.");
			Intent intent = new Intent();
			intent.setType("audio/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Choose an audio"),
					AppConstants.UPLOAD_AUDIO_REQUEST_CODE);
		} else {
			AppHelper.LogCat("Please request Read data permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		
	}
	
	
	/**
	 * method to animate the attachment items
	 *
	 * @param opened
	 */
	private void animateItems(boolean opened) {
		float startRadius = 0.0f;
		float endRadius = Math.max(mFrameLayoutReveal.getWidth(), mFrameLayoutReveal.getHeight());
		if (opened) {
			int cy = mFrameLayoutReveal.getLeft();
			int dx = mFrameLayoutReveal.getBottom();
			Animator supportAnimator = ViewAnimationUtils.createCircularReveal(mFrameLayoutReveal, cy, dx, startRadius, endRadius);
			supportAnimator.setInterpolator(new AccelerateInterpolator());
			supportAnimator.setDuration(400);
			supportAnimator.addListener(mAnimatorListenerOpen);
			supportAnimator.start();
		} else {
			int cy = mFrameLayoutReveal.getLeft();
			int dx = mFrameLayoutReveal.getBottom();
			Animator supportAnimator2 = ViewAnimationUtils.createCircularReveal(mFrameLayoutReveal, cy, dx, endRadius, startRadius);
			supportAnimator2.setInterpolator(new DecelerateInterpolator());
			supportAnimator2.setDuration(400);
			supportAnimator2.addListener(mAnimatorListenerClose);
			supportAnimator2.start();
		}
	}
	
	/**
	 * method to launch a document chooser
	 */
	private void launchDocumentChooser() {
		if (isOpen) {
			isOpen = false;
			animateItems(false);
		}
		
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
			AppHelper.LogCat("Read data permission already granted.");
			Intent intent = new Intent();
			intent.setType("application/pdf");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			try {
				startActivityForResult(
						Intent.createChooser(intent, "Choose  document"),
						AppConstants.UPLOAD_DOCUMENT_REQUEST_CODE);
			} catch (ActivityNotFoundException ex) {
				AppHelper.CustomToast(getActivity(), "Please install a File Manager.");
			}
		} else {
			AppHelper.LogCat("Please request Read data permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		
	}
	
	
	/**
	 * method  to launch a video preview
	 */
	private void launchAttachRecordVideo() {
		if (isOpen) {
			isOpen = false;
			animateItems(false);
		}
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.CAMERA)) {
			AppHelper.LogCat("Camera permission already granted.");
			
			Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			
			mProcessingPhotoUri = FilesManager.getVideoFile(getActivity());
			
			if (mProcessingPhotoUri != null)
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
			startActivityForResult(cameraIntent, AppConstants.SELECT_MESSAGES_RECORD_VIDEO);
		} else {
			AppHelper.LogCat("Please request camera  permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.CAMERA);
		}
	}
	
	
	/**
	 * method to launch a video chooser
	 */
	private void launchVideoChooser() {
		if (isOpen) {
			isOpen = false;
			animateItems(false);
		}
		
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
			AppHelper.LogCat("Read data permission already granted.");
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Choose video"),
					AppConstants.UPLOAD_VIDEO_REQUEST_CODE);
		} else {
			AppHelper.LogCat("Please request Read data permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		
	}
	
	/**
	 * method to launch the image chooser
	 */
	private void launchImageChooser() {
		if (isOpen) {
			isOpen = false;
			animateItems(false);
		}
		
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
			AppHelper.LogCat("Read data permission already granted.");
			
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Choose An image"),
					AppConstants.UPLOAD_PICTURE_REQUEST_CODE);
		} else {
			AppHelper.LogCat("Please request Read data permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		
	}
	
	/**
	 * method to launch the camera preview
	 */
	private void launchAttachCamera() {
		if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) return;
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.CAMERA)) {
			AppHelper.LogCat("camera permission already granted.");
			
			if (isOpen) {
				isOpen = false;
				animateItems(false);
			}
			
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			
			mProcessingPhotoUri = FilesManager.getImageFile(getActivity());
			
			if (mProcessingPhotoUri != null) {
				AppHelper.LogCat("mProcessingPhotoUri " + mProcessingPhotoUri);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
			}
			startActivityForResult(cameraIntent, AppConstants.SELECT_MESSAGES_CAMERA);
		} else {
			AppHelper.LogCat("Please request camera  permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.CAMERA);
		}
		
		
	}
	
	
	/**
	 * method to set teh draging animation for audio layout
	 *
	 * @param motionEvent this is the first parameter for setDraggingAnimation  method
	 * @param view        this the second parameter for  setDraggingAnimation  method
	 * @return this is what method will return
	 */
	private boolean setDraggingAnimation(MotionEvent motionEvent, View view) {
		
		sendMessagePanel.setVisibility(View.GONE);
		recordPanel.setVisibility(View.VISIBLE);
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideTextContainer
					.getLayoutParams();
			params.leftMargin = convertToDp(30);
			slideTextContainer.setLayoutParams(params);
			ViewAudioProxy.setAlpha(slideTextContainer, 1);
			startedDraggingX = -1;
			mStartTime = System.currentTimeMillis();
			startRecording();
			SendRecordButton.getParent().requestDisallowInterceptTouchEvent(true);
			recordPanel.setVisibility(View.VISIBLE);
		} else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
			startedDraggingX = -1;
			recordPanel.setVisibility(View.GONE);
			sendMessagePanel.setVisibility(View.VISIBLE);
			
			long intervalTime = System.currentTimeMillis() - mStartTime;
			if (intervalTime < MIN_INTERVAL_TIME) {
				
				messageWrapper.setError(getString(R.string.hold_to_record));
				try {
					if (FilesManager.isFileRecordExists(FileAudioPath)) {
						boolean deleted = FilesManager.getFileRecord(FileAudioPath).delete();
						if (deleted)
							FileAudioPath = null;
					}
				} catch (Exception e) {
					AppHelper.LogCat("Exception record path file  MessagesPopupActivity");
				}
			} else {
				
				sendMessage2();
				FileAudioPath = null;
				
			}
			stopRecording();
		} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
			float x = motionEvent.getX();
			if (x < -distCanMove) {
				AppHelper.LogCat("here we will delete  the file ");
				try {
					if (FilesManager.isFileRecordExists(FileAudioPath)) {
						boolean deleted = FilesManager.getFileRecord(FileAudioPath).delete();
						if (deleted)
							FileAudioPath = null;
					}
					
					
				} catch (Exception e) {
					AppHelper.LogCat("Exception exist record  " + e.getMessage());
				}
				FileAudioPath = null;
				stopRecording();
			}
			x = x + ViewAudioProxy.getX(SendRecordButton);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideTextContainer
					.getLayoutParams();
			if (startedDraggingX != -1) {
				float dist = (x - startedDraggingX);
				params.leftMargin = convertToDp(30) + (int) dist;
				slideTextContainer.setLayoutParams(params);
				float alpha = 1.0f + dist / distCanMove;
				if (alpha > 1) {
					alpha = 1;
				} else if (alpha < 0) {
					alpha = 0;
				}
				ViewAudioProxy.setAlpha(slideTextContainer, alpha);
			}
			if (x <= ViewAudioProxy.getX(slideTextContainer) + slideTextContainer.getWidth()
					+ convertToDp(30)) {
				if (startedDraggingX == -1) {
					startedDraggingX = x;
					distCanMove = (recordPanel.getMeasuredWidth()
							- slideTextContainer.getMeasuredWidth() - convertToDp(48)) / 2.0f;
					if (distCanMove <= 0) {
						distCanMove = convertToDp(80);
					} else if (distCanMove > convertToDp(80)) {
						distCanMove = convertToDp(80);
					}
				}
			}
			if (params.leftMargin > convertToDp(30)) {
				params.leftMargin = convertToDp(30);
				slideTextContainer.setLayoutParams(params);
				ViewAudioProxy.setAlpha(slideTextContainer, 1);
				startedDraggingX = -1;
			}
		}
		
		view.onTouchEvent(motionEvent);
		return true;
	}
	
	
	/**
	 * method to start recording audio
	 */
	private void startRecording() {
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.RECORD_AUDIO)) {
			AppHelper.LogCat("Record audio permission already granted.");
		} else {
			AppHelper.LogCat("Please request Record audio permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
		}
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
			AppHelper.LogCat("Record audio permission already granted.");
		} else {
			AppHelper.LogCat("Please request Record audio permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.MODIFY_AUDIO_SETTINGS);
		}
		
		
		if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.VIBRATE)) {
			AppHelper.LogCat("Vibrate permission already granted.");
		} else {
			AppHelper.LogCat("Please request Vibrate permission.");
			PermissionHandler.requestPermission(getActivity(), Manifest.permission.VIBRATE);
		}
		try {
			startRecordingAudio();
			startTime = SystemClock.uptimeMillis();
			recordTimer = new Timer();
			MessagesFragment.UpdaterTimerTask updaterTimerTask = new UpdaterTimerTask();
			recordTimer.schedule(updaterTimerTask, 1000, 1000);
			vibrate();
		} catch (Exception e) {
			AppHelper.LogCat("IOException start audio " + e.getMessage());
		}
		
		
	}
	
	
	/**
	 * method to initialize the audio for start recording
	 *
	 * @throws IOException
	 */
	@SuppressLint("SetTextI18n")
	private void startRecordingAudio() throws IOException {
		stopRecordingAudio();
		FileAudioPath = FilesManager.getFileRecordPath(getActivity());
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setOutputFile(FileAudioPath);
		mMediaRecorder.setOnErrorListener(errorListener);
		mMediaRecorder.setOnInfoListener(infoListener);
		mMediaRecorder.prepare();
		mMediaRecorder.start();
		
	}
	
	
	/**
	 * method to reset and clear media recorder
	 */
	private void stopRecordingAudio() {
		try {
			if (mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				mMediaRecorder.release();
				mMediaRecorder = null;
				FileAudioPath = null;
			}
		} catch (Exception e) {
			AppHelper.LogCat("Exception stop recording " + e.getMessage());
		}
		
	}
	
	private MediaRecorder.OnErrorListener errorListener = (mr, what, extra) -> AppHelper.LogCat("Error: " + what + ", " + extra);
	
	private MediaRecorder.OnInfoListener infoListener = (mr, what, extra) -> AppHelper.LogCat("Warning: " + what + ", " + extra);
	
	
	/**
	 * method to make device vibrate when user start recording
	 */
	private void vibrate() {
		try {
			Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class UpdaterTimerTask extends TimerTask {
		
		@Override
		public void run() {
			long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			long timeSwapBuff = 0L;
			long updatedTime = timeSwapBuff + timeInMilliseconds;
			Duration = String.valueOf(updatedTime);
			final String recordTime = UtilsTime.getFileTime(updatedTime);
			getActivity().runOnUiThread(() -> {
				try {
					if (recordTimeText != null) {
						recordTimeText.setText(recordTime);
					}
					
				} catch (Exception e) {
					AppHelper.LogCat("Exception record MessagesPopupActivity");
				}
				
			});
		}
	}
	
	/**
	 * method to stop recording auido
	 */
	@SuppressLint("SetTextI18n")
	private void stopRecording() {
		if (recordTimer != null) {
			recordTimer.cancel();
		}
		if (recordTimeText.getText().toString().equals("00:00")) {
			return;
		}
		recordTimeText.setText("00:00");
		vibrate();
		recordPanel.setVisibility(View.GONE);
		sendMessagePanel.setVisibility(View.VISIBLE);
		stopRecordingAudio();
		
		
	}
	
	
	/**
	 * method to send the new message
	 */
//    private void sendMessage() {
//
//        isSeen = false;
//        if (isGroup) {
//            new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
//        } else {
//            new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
//        }
//
//        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_START_CONVERSATION));//for change viewpager current item to 0
//        String messageBody = UtilsString.escapeJava(messageWrapper.getText().toString().trim());
//        if (messageTransfer != null)
//            messageBody = messageTransfer;
//
//        if (FileImagePath == null && FileAudioPath == null && FileDocumentPath == null && FileVideoPath == null) {
//            if (messageBody.isEmpty()) return;
//        }
//        DateTime current = new DateTime();
//        String sendTime = String.valueOf(current);
//
//        if (isGroup) {
//            final JSONObject messageGroup = new JSONObject();
//            try {
//                messageGroup.put("messageBody", messageBody);
//                messageGroup.put("senderId", senderId);
//                messageGroup.put("recipientId", 0);
//                try {
//
//                    messageGroup.put("senderName", "null");
//
//                    messageGroup.put("phone", mUsersModel.getPhone());
//                    if (mGroupsModel.getGroupImage() != null)
//                        messageGroup.put("GroupImage", mGroupsModel.getGroupImage());
//                    else
//                        messageGroup.put("GroupImage", "null");
//                    if (mGroupsModel.getGroupName() != null)
//                        messageGroup.put("GroupName", mGroupsModel.getGroupName());
//                    else
//                        messageGroup.put("GroupName", "null");
//                } catch (Exception e) {
//                    AppHelper.LogCat(e);
//                }
//
//                messageGroup.put("groupID", groupID);
//                messageGroup.put("date", sendTime);
//                messageGroup.put("isGroup", true);
//
//                if (FileImagePath != null)
//                    messageGroup.put("image", FileImagePath);
//                else
//                    messageGroup.put("image", "null");
//
//                if (FileVideoPath != null)
//                    messageGroup.put("video", FileVideoPath);
//                else
//                    messageGroup.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    messageGroup.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    messageGroup.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    messageGroup.put("audio", FileAudioPath);
//                else
//                    messageGroup.put("audio", "null");
//
//                if (FileDocumentPath != null)
//                    messageGroup.put("document", FileDocumentPath);
//                else
//                    messageGroup.put("document", "null");
//
//                if (!FileSize.equals("0"))
//                    messageGroup.put("fileSize", FileSize);
//                else
//                    messageGroup.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    messageGroup.put("duration", Duration);
//                else
//                    messageGroup.put("duration", "0");
//
//                messageGroup.put("userToken", PreferenceManager.getToken(getActivity()));
//            } catch (JSONException e) {
//                AppHelper.LogCat("send group message " + e.getMessage());
//            }
//            unSentMessagesGroup(groupID);
//            new Handler().postDelayed(() -> setStatusAsWaiting(messageGroup, true), 100);
//            AppHelper.LogCat("send group message to");
//
//        } else {
//            final JSONObject message = new JSONObject();
//            try {
//                message.put("messageBody", messageBody);
//                message.put("recipientId", recipientId);
//                message.put("senderId", senderId);
//                try {
//
//                    message.put("senderName", "null");
//
//                    if (mUsersModel.getImage() != null)
//                        message.put("senderImage", mUsersModel.getImage());
//                    else
//                        message.put("senderImage", "null");
//                    message.put("phone", mUsersModel.getPhone());
//                } catch (Exception e) {
//                    AppHelper.LogCat("Sender name " + e.getMessage());
//                }
//
//
//                message.put("date", sendTime);
//                message.put("isGroup", false);
//                message.put("conversationId", ConversationID);
//                if (FileImagePath != null)
//                    message.put("image", FileImagePath);
//                else
//                    message.put("image", "null");
//
//                if (FileVideoPath != null)
//                    message.put("video", FileVideoPath);
//                else
//                    message.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    message.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    message.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    message.put("audio", FileAudioPath);
//                else
//                    message.put("audio", "null");
//
//
//                if (FileDocumentPath != null)
//                    message.put("document", FileDocumentPath);
//                else
//                    message.put("document", "null");
//
//
//                if (!FileSize.equals("0"))
//                    message.put("fileSize", FileSize);
//                else
//                    message.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    message.put("duration", Duration);
//                else
//                    message.put("duration", "0");
//
//                message.put("userToken", PreferenceManager.getToken(getActivity()));
//            } catch (JSONException e) {
//                AppHelper.LogCat("send message " + e.getMessage());
//            }
//            unSentMessagesForARecipient(recipientId, false);
//            new Handler().postDelayed(() -> setStatusAsWaiting(message, false), 100);
//        }
//        messageWrapper.setText("");
//        messageTransfer = null;
//        mProcessingPhotoUri = null;
//
//
//    }
	
	
	/**
	 * /**
	 * method to send the new message
	 */
	
	private void sendMessage2() {
		
		
		isSeen = false;
		if (isGroup) {
			new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
		} else {
			new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
		}
		
		EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_START_CONVERSATION));//for change viewpager current item to 0
		String messageBody = UtilsString.escapeJava(messageWrapper.getText().toString().trim());
		if (messageTransfer != null)
			messageBody = messageTransfer;
		
		if (FileImagePath == null && FileAudioPath == null && FileDocumentPath == null && FileVideoPath == null) {
			if (messageBody.isEmpty()) return;
		}
		DateTime current = new DateTime();
		String sendTime = String.valueOf(current);
		
		if (isGroup) {
			final JSONObject messageGroup = new JSONObject();
			try {
				messageGroup.put("messageBody", messageBody);
				messageGroup.put("senderId", senderId);
				messageGroup.put("recipientId", 0);
				try {
					
					messageGroup.put("senderName", "null");
					
					messageGroup.put("phone", mUsersModel.getPhone());
					if (mGroupsModel.getGroupImage() != null)
						messageGroup.put("GroupImage", mGroupsModel.getGroupImage());
					else
						messageGroup.put("GroupImage", "null");
					if (mGroupsModel.getGroupName() != null)
						messageGroup.put("GroupName", mGroupsModel.getGroupName());
					else
						messageGroup.put("GroupName", "null");
				} catch (Exception e) {
					AppHelper.LogCat(e);
				}
				
				messageGroup.put("groupID", groupID);
				messageGroup.put("date", sendTime);
				messageGroup.put("isGroup", true);
				
				if (FileImagePath != null)
					messageGroup.put("image", FileImagePath);
				else
					messageGroup.put("image", "null");
				
				if (FileVideoPath != null)
					messageGroup.put("video", FileVideoPath);
				else
					messageGroup.put("video", "null");
				
				if (FileVideoThumbnailPath != null)
					messageGroup.put("thumbnail", FileVideoThumbnailPath);
				else
					messageGroup.put("thumbnail", "null");
				
				if (FileAudioPath != null)
					messageGroup.put("audio", FileAudioPath);
				else
					messageGroup.put("audio", "null");
				
				if (FileDocumentPath != null)
					messageGroup.put("document", FileDocumentPath);
				else
					messageGroup.put("document", "null");
				
				if (!FileSize.equals("0"))
					messageGroup.put("fileSize", FileSize);
				else
					messageGroup.put("fileSize", "0");
				
				if (!Duration.equals("0"))
					messageGroup.put("duration", Duration);
				else
					messageGroup.put("duration", "0");
				
				messageGroup.put("userToken", PreferenceManager.getToken(getActivity()));
			} catch (JSONException e) {
				AppHelper.LogCat("send group message " + e.getMessage());
			}
			unSentMessagesGroup(groupID);
			new Handler().postDelayed(() -> setStatusAsWaiting(messageGroup, true), 100);
			mSocket.emit(AppConstants.SOCKET_SEND_TO_MSG, messageGroup);
			AppHelper.LogCat("send group message to");
			
		} else {
			
			
			JSONObject data = new JSONObject();
			
			//...............
			
			try {
				data.put("messageBody", messageBody);
				data.put("recipientId", recipientId);
				data.put("senderId", senderId);
				try {
					
					data.put("senderName", "null");
					
					if (mUsersModel.getImage() != null)
						data.put("senderImage", mUsersModel.getImage());
					else
						data.put("senderImage", "null");
					data.put("phone", mUsersModel.getPhone());
				} catch (Exception e) {
					AppHelper.LogCat("Sender name " + e.getMessage());
				}
				
				
				data.put("date", sendTime);
				data.put("isGroup", false);
				data.put("conversationId", ConversationID);
				if (FileImagePath != null)
					data.put("image", FileImagePath);
				else
					data.put("image", "null");
				
				if (FileVideoPath != null)
					data.put("video", FileVideoPath);
				else
					data.put("video", "null");
				
				if (FileVideoThumbnailPath != null)
					data.put("thumbnail", FileVideoThumbnailPath);
				else
					data.put("thumbnail", "null");
				
				if (FileAudioPath != null)
					data.put("audio", FileAudioPath);
				else
					data.put("audio", "null");
				
				
				if (FileDocumentPath != null)
					data.put("document", FileDocumentPath);
				else
					data.put("document", "null");
				
				
				if (!FileSize.equals("0"))
					data.put("fileSize", FileSize);
				else
					data.put("fileSize", "0");
				
				if (!Duration.equals("0"))
					data.put("duration", Duration);
				else
					data.put("duration", "0");
				
				data.put("userToken", PreferenceManager.getToken(getActivity()));
//get
				unSentMessagesForARecipient(recipientId, false);
				new Handler().postDelayed(() -> setStatusAsWaiting(data, false), 100);
				mSocket.emit(AppConstants.SOCKET_SEND_MESSAGE_SET, data);
			} catch (JSONException e) {
				AppHelper.LogCat("send message " + e.getMessage());
			}
			
			
			//...............


//            mSocket.emit(AppConstants.SOCKET_SENDING_MSG, data);
//
//
//
//
//            final JSONObject message = new JSONObject();
//            try {
//                message.put("messageBody", messageBody);
//                message.put("recipientId", recipientId);
//                message.put("senderId", senderId);
//                try {
//
//                    message.put("senderName", "null");
//
//                    if (mUsersModel.getImage() != null)
//                        message.put("senderImage", mUsersModel.getImage());
//                    else
//                        message.put("senderImage", "null");
//                    message.put("phone", mUsersModel.getPhone());
//                } catch (Exception e) {
//                    AppHelper.LogCat("Sender name " + e.getMessage());
//                }
//
//
//                message.put("date", sendTime);
//                message.put("isGroup", false);
//                message.put("conversationId", ConversationID);
//                if (FileImagePath != null)
//                    message.put("image", FileImagePath);
//                else
//                    message.put("image", "null");
//
//                if (FileVideoPath != null)
//                    message.put("video", FileVideoPath);
//                else
//                    message.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    message.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    message.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    message.put("audio", FileAudioPath);
//                else
//                    message.put("audio", "null");
//
//
//                if (FileDocumentPath != null)
//                    message.put("document", FileDocumentPath);
//                else
//                    message.put("document", "null");
//
//
//                if (!FileSize.equals("0"))
//                    message.put("fileSize", FileSize);
//                else
//                    message.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    message.put("duration", Duration);
//                else
//                    message.put("duration", "0");
//
//                message.put("userToken", PreferenceManager.getToken(getActivity()));
//            } catch (JSONException e) {
//                AppHelper.LogCat("send message " + e.getMessage());
//            }
//            //get
//            unSentMessagesForARecipient(recipientId, false);
//            new Handler().postDelayed(() -> setStatusAsWaiting(message, false), 100);
		}
		messageWrapper.setText("");
		messageTransfer = null;
		mProcessingPhotoUri = null;
		
		
	}
	
	private void sendMessage4() {
		
		isSeen = false;
		if (isGroup) {
			new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
		} else {
			new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
		}
		
		EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_START_CONVERSATION));//for change viewpager current item to 0
		String messageBody = UtilsString.escapeJava(messageWrapper.getText().toString().trim());
		if (messageTransfer != null)
			messageBody = messageTransfer;
		
		if (FileImagePath == null && FileAudioPath == null && FileDocumentPath == null && FileVideoPath == null) {
			if (messageBody.isEmpty()) return;
		}
		DateTime current = new DateTime();
		String sendTime = String.valueOf(current);
		
		if (isGroup) {
			final JSONObject messageGroup = new JSONObject();
			try {
				messageGroup.put("messageBody", messageBody);
				messageGroup.put("senderId", senderId);
				messageGroup.put("recipientId", 0);
				try {
					
					messageGroup.put("senderName", "null");
					
					messageGroup.put("phone", mUsersModel.getPhone());
					if (mGroupsModel.getGroupImage() != null)
						messageGroup.put("GroupImage", mGroupsModel.getGroupImage());
					else
						messageGroup.put("GroupImage", "null");
					if (mGroupsModel.getGroupName() != null)
						messageGroup.put("GroupName", mGroupsModel.getGroupName());
					else
						messageGroup.put("GroupName", "null");
				} catch (Exception e) {
					AppHelper.LogCat(e);
				}
				
				messageGroup.put("groupID", groupID);
				messageGroup.put("date", sendTime);
				messageGroup.put("isGroup", true);
				
				if (FileImagePath != null)
					messageGroup.put("image", FileImagePath);
				else
					messageGroup.put("image", "null");
				
				if (FileVideoPath != null)
					messageGroup.put("video", FileVideoPath);
				else
					messageGroup.put("video", "null");
				
				if (FileVideoThumbnailPath != null)
					messageGroup.put("thumbnail", FileVideoThumbnailPath);
				else
					messageGroup.put("thumbnail", "null");
				
				if (FileAudioPath != null)
					messageGroup.put("audio", FileAudioPath);
				else
					messageGroup.put("audio", "null");
				
				if (FileDocumentPath != null)
					messageGroup.put("document", FileDocumentPath);
				else
					messageGroup.put("document", "null");
				
				if (!FileSize.equals("0"))
					messageGroup.put("fileSize", FileSize);
				else
					messageGroup.put("fileSize", "0");
				
				if (!Duration.equals("0"))
					messageGroup.put("duration", Duration);
				else
					messageGroup.put("duration", "0");
				
				messageGroup.put("userToken", PreferenceManager.getToken(getActivity()));
			} catch (JSONException e) {
				AppHelper.LogCat("send group message " + e.getMessage());
			}
			unSentMessagesGroup(groupID);
			new Handler().postDelayed(() -> setStatusAsWaiting(messageGroup, true), 100);
			AppHelper.LogCat("send group message to");
			
		} else {
			
			
			JSONObject data = new JSONObject();
			
			//...............
			
			try {
				data.put("messageBody", messageBody);
				data.put("recipientId", recipientId);
				data.put("senderId", senderId);
				try {
					
					data.put("senderName", "null");
					
					if (mUsersModel.getImage() != null)
						data.put("senderImage", mUsersModel.getImage());
					else
						data.put("senderImage", "null");
					data.put("phone", mUsersModel.getPhone());
				} catch (Exception e) {
					AppHelper.LogCat("Sender name " + e.getMessage());
				}
				
				
				data.put("date", sendTime);
				data.put("isGroup", false);
				data.put("conversationId", ConversationID);
				if (FileImagePath != null)
					data.put("image", FileImagePath);
				else
					data.put("image", "null");
				
				if (FileVideoPath != null)
					data.put("video", FileVideoPath);
				else
					data.put("video", "null");
				
				if (FileVideoThumbnailPath != null)
					data.put("thumbnail", FileVideoThumbnailPath);
				else
					data.put("thumbnail", "null");
				
				if (FileAudioPath != null)
					data.put("audio", FileAudioPath);
				else
					data.put("audio", "null");
				
				
				if (FileDocumentPath != null)
					data.put("document", FileDocumentPath);
				else
					data.put("document", "null");
				
				
				if (!FileSize.equals("0"))
					data.put("fileSize", FileSize);
				else
					data.put("fileSize", "0");
				
				if (!Duration.equals("0"))
					data.put("duration", Duration);
				else
					data.put("duration", "0");
				
				data.put("userToken", PreferenceManager.getToken(getActivity()));
				unSentMessagesForARecipient(recipientId, false);
				new Handler().postDelayed(() -> setStatusAsWaiting(data, false), 100);
			} catch (JSONException e) {
				AppHelper.LogCat("send message " + e.getMessage());
			}
			
			
			//...............


//            mSocket.emit(AppConstants.SOCKET_SENDING_MSG, data);
//
//
//
//
//           final JSONObject message = new JSONObject();
//            try {
//                message.put("messageBody", messageBody);
//                message.put("recipientId", recipientId);
//                message.put("senderId", senderId);
//                try {
//
//                    message.put("senderName", "null");
//
//                    if (mUsersModel.getImage() != null)
//                        message.put("senderImage", mUsersModel.getImage());
//                    else
//                        message.put("senderImage", "null");
//                    message.put("phone", mUsersModel.getPhone());
//                } catch (Exception e) {
//                    AppHelper.LogCat("Sender name " + e.getMessage());
//                }
//
//
//                message.put("date", sendTime);
//                message.put("isGroup", false);
//                message.put("conversationId", ConversationID);
//                if (FileImagePath != null)
//                    message.put("image", FileImagePath);
//                else
//                    message.put("image", "null");
//
//                if (FileVideoPath != null)
//                    message.put("video", FileVideoPath);
//                else
//                    message.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    message.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    message.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    message.put("audio", FileAudioPath);
//                else
//                    message.put("audio", "null");
//
//
//                if (FileDocumentPath != null)
//                    message.put("document", FileDocumentPath);
//                else
//                    message.put("document", "null");
//
//
//                if (!FileSize.equals("0"))
//                    message.put("fileSize", FileSize);
//                else
//                    message.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    message.put("duration", Duration);
//                else
//                    message.put("duration", "0");
//
//                message.put("userToken", PreferenceManager.getToken(getActivity()));
//            } catch (JSONException e) {
//                AppHelper.LogCat("send message " + e.getMessage());
//            }
//            unSentMessagesForARecipient(recipientId, false);
//            new Handler().postDelayed(() -> setStatusAsWaiting(message, false), 100);
		}
		messageWrapper.setText("");
		messageTransfer = null;
		mProcessingPhotoUri = null;
		
		
	}
	
	
	/**
	 * method to check  for unsent messages group
	 *
	 * @param groupID this parameter of  unSentMessagesGroup  method
	 */
	private void unSentMessagesGroup(int groupID) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		
		List<MessagesModel> messagesModelsList = realm.where(MessagesModel.class)
				.notEqualTo("id", 0)
				.equalTo("status", AppConstants.IS_WAITING)
				.equalTo("isGroup", true)
				.equalTo("groupID", groupID)
				.equalTo("conversationID", ConversationID)
				.equalTo("isFileUpload", true)
				.equalTo("senderID", PreferenceManager.getID(getActivity()))
				.findAllSorted("id", Sort.ASCENDING);
		AppHelper.LogCat("size " + messagesModelsList.size());
		if (messagesModelsList.size() != 0) {
			for (MessagesModel messagesModel : messagesModelsList) {
				MainService.sendMessagesGroup(getActivity(), mUsersModel, mGroupsModel, messagesModel);
			}
		}
		if (!realm.isClosed())
			realm.close();
	}
	
	
	/**
	 * method to save new message as waitng messages
	 *
	 * @param data    this is the first parameter for setStatusAsWaiting method
	 * @param isgroup this is the second parameter for setStatusAsWaiting method
	 */
	private void setStatusAsWaiting(JSONObject data, boolean isgroup) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		try {
			if (isgroup) {
				int senderId = data.getInt("senderId");
				String messageBody = data.getString("messageBody");
				String senderName = data.getString("senderName");
				String senderPhone = data.getString("phone");
				String GroupImage = data.getString("GroupImage");
				String GroupName = data.getString("GroupName");
				String dateTmp = data.getString("date");
				String video = data.getString("video");
				String thumbnail = data.getString("thumbnail");
				boolean isGroup = data.getBoolean("isGroup");
				String image = data.getString("image");
				String audio = data.getString("audio");
				String document = data.getString("document");
				String fileSize = data.getString("fileSize");
				String duration = data.getString("duration");
				int groupID = data.getInt("groupID");
				realm.executeTransactionAsync(realm1 -> {
					
					int lastID = RealmBackupRestore.getMessageLastId();
					ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("groupID", groupID).findFirst();
					conversationsModel.setSenderID(senderId);
					RealmList<MessagesModel> messagesModelRealmList = conversationsModel.getMessages();
					MessagesModel messagesModel = new MessagesModel();
					messagesModel.setId(lastID);
					messagesModel.setDate(dateTmp);
					messagesModel.setStatus(AppConstants.IS_WAITING);
					messagesModel.setUsername(senderName);
					messagesModel.setSenderID(PreferenceManager.getID(getActivity()));
					messagesModel.setGroup(isGroup);
					messagesModel.setMessage(messageBody);
					messagesModel.setGroupID(groupID);
					messagesModel.setImageFile(image);
					messagesModel.setVideoFile(video);
					messagesModel.setAudioFile(audio);
					messagesModel.setDocumentFile(document);
					messagesModel.setFileSize(fileSize);
					messagesModel.setDuration(duration);
					messagesModel.setVideoThumbnailFile(thumbnail);
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
						messagesModel.setFileUpload(false);
						
					} else {
						messagesModel.setFileUpload(true);
					}
					messagesModel.setFileDownLoad(true);
					messagesModel.setConversationID(conversationsModel.getId());
					messagesModelRealmList.add(messagesModel);
					conversationsModel.setLastMessage(messageBody);
					conversationsModel.setLastMessageId(lastID);
					conversationsModel.setMessages(messagesModelRealmList);
					conversationsModel.setStatus(AppConstants.IS_WAITING);
					conversationsModel.setUnreadMessageCounter("0");
					conversationsModel.setRecipientID(0);
					conversationsModel.setSenderID(senderId);
					realm1.copyToRealmOrUpdate(conversationsModel);
					getActivity().runOnUiThread(() -> addMessage(messagesModel));
					
					
				}, () -> {
					if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null"))
						return;
					
					UpdateMessageModel updateMessageModel = new UpdateMessageModel();
					try {
						updateMessageModel.setSenderId(data.getInt("senderId"));
						updateMessageModel.setRecipientId(data.getInt("recipientId"));
						updateMessageModel.setMessageBody(data.getString("messageBody"));
						updateMessageModel.setSenderName(data.getString("senderName"));
						updateMessageModel.setGroupName(data.getString("GroupName"));
						updateMessageModel.setGroupImage(data.getString("GroupImage"));
						updateMessageModel.setGroupID(data.getInt("groupID"));
						updateMessageModel.setDate(data.getString("date"));
						updateMessageModel.setPhone(data.getString("phone"));
						updateMessageModel.setVideo(data.getString("video"));
						updateMessageModel.setThumbnail(data.getString("thumbnail"));
						updateMessageModel.setImage(data.getString("image"));
						updateMessageModel.setAudio(data.getString("audio"));
						updateMessageModel.setDocument(data.getString("document"));
						updateMessageModel.setFileSize(data.getString("fileSize"));
						updateMessageModel.setDuration(data.getString("duration"));
						updateMessageModel.setGroup(data.getBoolean("isGroup"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					MainService.sendMessage(updateMessageModel, true);
					
				}, error -> {
					AppHelper.LogCat("Save group message failed MessagesPopupActivity " + error.getMessage());
				});
				
				
			} else {
				AppHelper.LogCat("esedd message ");
				
				int senderId = data.getInt("senderId");
				int recipientId = data.getInt("recipientId");
				String messageBody = data.getString("messageBody");
				String senderName = data.getString("senderName");
				String dateTmp = data.getString("date");
				String video = data.getString("video");
				String thumbnail = data.getString("thumbnail");
				boolean isGroup = data.getBoolean("isGroup");
				String image = data.getString("image");
				String audio = data.getString("audio");
				String document = data.getString("document");
				String phone = data.getString("phone");
				String fileSize = data.getString("fileSize");
				String duration = data.getString("duration");
				
				String recipientName = mUsersModelRecipient.getUsername();
				String recipientImage = mUsersModelRecipient.getImage();
				String recipientPhone = mUsersModelRecipient.getPhone();
				String registered_id = mUsersModelRecipient.getRegistered_id();
				int conversationID = getConversationId(recipientId, senderId, realm);
				if (conversationID == 0) {
					realm.executeTransactionAsync(realm1 -> {
						
						
						int lastConversationID = RealmBackupRestore.getConversationLastId();
						int lastID = RealmBackupRestore.getMessageLastId();
						RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
						MessagesModel messagesModel = new MessagesModel();
						messagesModel.setId(lastID);
						messagesModel.setUsername(senderName);
						messagesModel.setRecipientID(recipientId);
						messagesModel.setDate(dateTmp);
						messagesModel.setStatus(AppConstants.IS_WAITING);
						messagesModel.setGroup(isGroup);
						messagesModel.setSenderID(senderId);
						messagesModel.setConversationID(lastConversationID);
						messagesModel.setMessage(messageBody);
						messagesModel.setImageFile(image);
						messagesModel.setVideoFile(video);
						messagesModel.setAudioFile(audio);
						messagesModel.setDocumentFile(document);
						messagesModel.setFileSize(fileSize);
						messagesModel.setDuration(duration);
						messagesModel.setVideoThumbnailFile(thumbnail);
						if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
							messagesModel.setFileUpload(false);
							
						} else {
							messagesModel.setFileUpload(true);
						}
						messagesModel.setFileDownLoad(true);
						messagesModel.setPhone(phone);
						messagesModelRealmList.add(messagesModel);
						ConversationsModel conversationsModel1 = new ConversationsModel();
						conversationsModel1.setRecipientID(recipientId);
						conversationsModel1.setLastMessage(messageBody);
						conversationsModel1.setSenderID(senderId);
						conversationsModel1.setRecipientUsername(recipientName);
						conversationsModel1.setRecipientImage(recipientImage);
						conversationsModel1.setMessageDate(dateTmp);
						conversationsModel1.setId(lastConversationID);
						conversationsModel1.setStatus(AppConstants.IS_WAITING);
						conversationsModel1.setRecipientPhone(recipientPhone);
						conversationsModel1.setMessages(messagesModelRealmList);
						conversationsModel1.setUnreadMessageCounter("0");
						conversationsModel1.setLastMessageId(lastID);
						conversationsModel1.setCreatedOnline(true);
						conversationsModel1.setSenderID(senderId);
						realm1.copyToRealmOrUpdate(conversationsModel1);
						ConversationID = lastConversationID;
						getActivity().runOnUiThread(() -> addMessage(messagesModel));
						try {
							data.put("messageId", lastID);
							data.put("registered_id", registered_id);
						} catch (JSONException e) {
							AppHelper.LogCat("last id");
						}
					}, () -> {
						
						if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null"))
							return;
						UpdateMessageModel updateMessageModel = new UpdateMessageModel();
						
						try {
							updateMessageModel.setSenderId(data.getInt("senderId"));
							updateMessageModel.setRecipientId(data.getInt("recipientId"));
							updateMessageModel.setMessageId(data.getInt("messageId"));
							updateMessageModel.setConversationId(data.getInt("conversationId"));
							updateMessageModel.setMessageBody(data.getString("messageBody"));
							updateMessageModel.setSenderName(data.getString("senderName"));
							updateMessageModel.setSenderImage(data.getString("senderImage"));
							updateMessageModel.setPhone(data.getString("phone"));
							updateMessageModel.setDate(data.getString("date"));
							updateMessageModel.setVideo(data.getString("video"));
							updateMessageModel.setThumbnail(data.getString("thumbnail"));
							updateMessageModel.setImage(data.getString("image"));
							updateMessageModel.setAudio(data.getString("audio"));
							updateMessageModel.setDocument(data.getString("document"));
							updateMessageModel.setFileSize(data.getString("fileSize"));
							updateMessageModel.setDuration(data.getString("duration"));
							updateMessageModel.setGroup(data.getBoolean("isGroup"));
							updateMessageModel.setRegistered_id(data.getString("registered_id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						MainService.sendMessage(updateMessageModel, false);
						EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_NEW_ROW, ConversationID));
					}, error -> AppHelper.LogCat("Error  conversation id MessagesActivity " + error.getMessage()));
					
					
				} else {
					
					realm.executeTransactionAsync(realm1 -> {
						try {
							
							
							int lastID = RealmBackupRestore.getMessageLastId();
							
							AppHelper.LogCat("last ID  message   MessagesActivity" + lastID);
							ConversationsModel conversationsModel;
							RealmQuery<ConversationsModel> conversationsModelRealmQuery = realm1.where(ConversationsModel.class).equalTo("id", conversationID);
							conversationsModel = conversationsModelRealmQuery.findAll().first();
							MessagesModel messagesModel = new MessagesModel();
							messagesModel.setId(lastID);
							messagesModel.setUsername(senderName);
							messagesModel.setRecipientID(recipientId);
							messagesModel.setDate(dateTmp);
							messagesModel.setStatus(AppConstants.IS_WAITING);
							messagesModel.setGroup(isGroup);
							messagesModel.setSenderID(senderId);
							messagesModel.setConversationID(conversationID);
							messagesModel.setMessage(messageBody);
							messagesModel.setImageFile(image);
							messagesModel.setVideoFile(video);
							messagesModel.setAudioFile(audio);
							messagesModel.setDocumentFile(document);
							messagesModel.setFileSize(fileSize);
							messagesModel.setDuration(duration);
							messagesModel.setVideoThumbnailFile(thumbnail);
							if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null")) {
								messagesModel.setFileUpload(false);
								
							} else {
								messagesModel.setFileUpload(true);
							}
							messagesModel.setFileDownLoad(true);
							messagesModel.setPhone(phone);
							conversationsModel.getMessages().add(messagesModel);
							conversationsModel.setLastMessageId(lastID);
							conversationsModel.setLastMessage(messageBody);
							conversationsModel.setMessageDate(dateTmp);
							conversationsModel.setCreatedOnline(true);
							conversationsModel.setSenderID(senderId);
							realm1.copyToRealmOrUpdate(conversationsModel);
							getActivity().runOnUiThread(() -> addMessage(messagesModel));
							try {
								data.put("messageId", lastID);
								data.put("registered_id", registered_id);
							} catch (JSONException e) {
								AppHelper.LogCat("last id");
							}
						} catch (Exception e) {
							AppHelper.LogCat("Exception  last id message  MessagesActivity " + e.getMessage());
						}
					}, () -> {
						
						if (!image.equals("null") || !video.equals("null") || !audio.equals("null") || !document.equals("null") || !thumbnail.equals("null"))
							return;
						UpdateMessageModel updateMessageModel = new UpdateMessageModel();
						try {
							updateMessageModel.setSenderId(data.getInt("senderId"));
							updateMessageModel.setRecipientId(data.getInt("recipientId"));
							updateMessageModel.setMessageId(data.getInt("messageId"));
							updateMessageModel.setConversationId(data.getInt("conversationId"));
							updateMessageModel.setMessageBody(data.getString("messageBody"));
							updateMessageModel.setSenderName(data.getString("senderName"));
							updateMessageModel.setSenderImage(data.getString("senderImage"));
							updateMessageModel.setPhone(data.getString("phone"));
							updateMessageModel.setDate(data.getString("date"));
							updateMessageModel.setVideo(data.getString("video"));
							updateMessageModel.setThumbnail(data.getString("thumbnail"));
							updateMessageModel.setImage(data.getString("image"));
							updateMessageModel.setAudio(data.getString("audio"));
							updateMessageModel.setDocument(data.getString("document"));
							updateMessageModel.setFileSize(data.getString("fileSize"));
							updateMessageModel.setDuration(data.getString("duration"));
							updateMessageModel.setGroup(data.getBoolean("isGroup"));
							updateMessageModel.setRegistered_id(data.getString("registered_id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						MainService.sendMessage(updateMessageModel, false);
						EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_MESSAGE_CONVERSATION_OLD_ROW, conversationID));
					}, error -> AppHelper.LogCat("Error  last id  MessagesActivity " + error.getMessage()));
				}
			}
			
			
		} catch (JSONException e) {
			
			Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_LONG);
			AppHelper.LogCat("JSONException  MessagesActivity " + e);
		}
		
		FileAudioPath = null;
		FileVideoPath = null;
		FileDocumentPath = null;
		FileImagePath = null;
		FileVideoThumbnailPath = null;
		FileSize = "0";
		Duration = "0";
		if (!realm.isClosed())
			realm.close();
	}
	
	
	/**
	 * method to add a new message to list messages
	 *
	 * @param newMsg this is the parameter for addMessage
	 */
	
	private void addMessage(MessagesModel newMsg) {
		
		mMessagesAdapter.addMessage(newMsg);
		scrollToBottom();
	}
	
	/**
	 * method to scroll to the bottom of list
	 */
	private void scrollToBottom() {
		messagesList.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
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
					.endGroup().findFirst();
			return conversationsModelNew.getId();
		} catch (Exception e) {
			AppHelper.LogCat("Get conversation id Exception MessagesPopupActivity " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * method to check for unsent user messages
	 *
	 * @param recipientID this is parameter of unSentMessagesForARecipient method
	 */
	private void unSentMessagesForARecipient(int recipientID, boolean forFiles) {
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		List<MessagesModel> messagesModelsList = realm.where(MessagesModel.class)
				.notEqualTo("id", 0)
				.equalTo("status", AppConstants.IS_WAITING)
				.equalTo("recipientID", recipientID)
				.equalTo("isFileUpload", true)
				.equalTo("isGroup", false)
				.equalTo("senderID", PreferenceManager.getID(getActivity()))
				.findAllSorted("id", Sort.ASCENDING);
		
		AppHelper.LogCat("size " + messagesModelsList.size());
		if (messagesModelsList.size() != 0) {
			if (forFiles) {
				for (MessagesModel messagesModel : messagesModelsList) {
					MainService.sendMessagesFiles(messagesModel);
				}
			} else {
				for (MessagesModel messagesModel : messagesModelsList) {
					MainService.sendMessages(messagesModel);
				}
			}
		}
		realm.close();
		
	}
	
	
	private void unBlockContact() {
		
		Realm realmUnblock = RooyeshApplication.getRealmDatabaseInstance();
		AlertDialog.Builder builderUnblock = new AlertDialog.Builder(getActivity());
		builderUnblock.setMessage(R.string.unblock_user_make_sure);
		builderUnblock.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
			
			APIHelper.initialApiUsersContacts().unbBlock(recipientId).subscribe(blockResponse -> {
				if (blockResponse.isSuccess()) {
					realmUnblock.executeTransactionAsync(realm1 -> {
						UsersBlockModel usersBlockModel = realm1.where(UsersBlockModel.class).equalTo("contactsModel.id", recipientId).findFirst();
						usersBlockModel.deleteFromRealm();
						
					}, () -> {
						refreshMenu();
						if (AddContactBtn.getVisibility() == View.VISIBLE) {
							UnBlockContactBtn.setVisibility(View.GONE);
							BlockContactBtn.setVisibility(View.VISIBLE);
						}
					}, error -> {
						AppHelper.LogCat("Block user" + error.getMessage());
						
					});
					AppHelper.Snackbar(getActivity(), mView, blockResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
				} else {
					AppHelper.Snackbar(getActivity(), mView, blockResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
				}
			}, throwable -> {
				AppHelper.CustomToast(getActivity(), getString(R.string.oops_something));
			});
			
			
		});
		
		builderUnblock.setNegativeButton(R.string.No, (dialog, whichButton) -> {
		
		});
		
		builderUnblock.show();
		if (!realmUnblock.isClosed())
			realmUnblock.close();
	}
	
	
	/**
	 * refresh the menu for new contact
	 * doesn't exist in contactModel
	 */
	public void refreshMenu() {
		// invalidateOptionsMenu();
		ActivityCompat.invalidateOptionsMenu(getActivity());
		//  supportInvalidateOptionsMenu();
		
	}
	
	
	private void blockContact() {
		
		Realm realm2 = RooyeshApplication.getRealmDatabaseInstance();
		AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
		builder2.setMessage(R.string.block_user_make_sure);
		builder2.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
			APIHelper.initialApiUsersContacts().block(recipientId).subscribe(blockResponse -> {
				if (blockResponse.isSuccess()) {
					realm2.executeTransactionAsync(realm1 -> {
						ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", recipientId).findFirst();
						UsersBlockModel usersBlockModel = new UsersBlockModel();
						usersBlockModel.setId(RealmBackupRestore.getBlockUserLastId());
						usersBlockModel.setContactsModel(contactsModel);
						realm1.copyToRealmOrUpdate(usersBlockModel);
					}, () -> {
						refreshMenu();
						if (AddContactBtn.getVisibility() == View.VISIBLE) {
							BlockContactBtn.setVisibility(View.GONE);
							UnBlockContactBtn.setVisibility(View.VISIBLE);
						}
					}, error -> {
						AppHelper.LogCat("Block user" + error.getMessage());
						
					});
					AppHelper.Snackbar(getActivity(), mView, blockResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
				} else {
					AppHelper.Snackbar(getActivity(), mView, blockResponse.getMessage(), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
				}
			}, throwable -> {
				AppHelper.CustomToast(getActivity(), getString(R.string.oops_something));
			});
			
			
		});
		
		builder2.setNegativeButton(R.string.No, (dialog, whichButton) -> {
		
		});
		
		builder2.show();
		if (!realm2.isClosed())
			realm2.close();
	}
	
	
	private void addNewContact() {
		try {
			Intent mIntent = new Intent(Intent.ACTION_INSERT);
			mIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			mIntent.putExtra(ContactsContract.Intents.Insert.PHONE, mUsersModelRecipient.getPhone());
			startActivityForResult(mIntent, AppConstants.SELECT_ADD_NEW_CONTACT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void setTypeFaces() {
		if (AppConstants.ENABLE_FONTS_TYPES) {
			slideToCancelText.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
			messageWrapper.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
			searchInput.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
			ToolbarTitle.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
			statusUser.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
			recordTimeText.setTypeface(AppHelper.setTypeFace(getActivity(), "IranSans"));
		}
	}
	
	
	/**
	 * method to initialize the massage wrapper
	 */
	private void initializerMessageWrapper() {
		
		
		final Context context = getActivity();
		messageWrapper.setFocusable(true);
		messageWrapper.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				AppHelper.LogCat("Has focused");
				emitMessageSeen();
				socket_recieve_msg();
				if (isGroup) {
					new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
				} else {
					new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
				}
			}
			
		});
		
		messageWrapper.setOnClickListener(v1 -> {
			if (emoticonShown) {
				emoticonShown = false;
				emojIcon.closeEmojIcon();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
			
		});
		messageWrapper.addTextChangedListener(new TextWatcherAdapter() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				SendRecordButton.setVisibility(View.VISIBLE);
				SendButton.setVisibility(View.GONE);
				PictureButton.setVisibility(View.GONE);
				
			}
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (messageWrapper.getLineCount() >= 6) {
					messageWrapper.setScroller(new Scroller(getActivity()));
					messageWrapper.setMaxLines(6);
					messageWrapper.setVerticalScrollBarEnabled(true);
					messageWrapper.setMovementMethod(new ScrollingMovementMethod());
				}
				
				if (!isSeen)
					emitMessageSeen();
				socket_recieve_msg();
				isSeen = true;
				SendRecordButton.setVisibility(View.GONE);
				SendButton.setVisibility(View.VISIBLE);
				PictureButton.setVisibility(View.GONE);
				
				
				if (!mSocket.connected()) return;
				if (isGroup) {
					try {
						if (mGroupsModel.getMembers() != null && mGroupsModel.getMembers().size() != 0) {
							for (MembersGroupModel membersGroupModel : mGroupsModel.getMembers()) {
								if (!isTyping && s.length() != 0) {
									isTyping = true;
									JSONObject data = new JSONObject();
									try {
										data.put("recipientId", membersGroupModel.getUserId());
										data.put("senderId", senderId);
										data.put("groupId", groupID);
									} catch (JSONException e) {
										AppHelper.LogCat(e);
									}
									mSocket.emit(AppConstants.SOCKET_IS_MEMBER_TYPING, data);
								}

//                                mTypingHandler =new Handler(Looper.getMainLooper())
//                                {
//
//                                };
								
								mTypingHandler.removeCallbacks(onTypingTimeout);
								mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
							}
						}
					} catch (Exception e) {
						AppHelper.LogCat(e);
					}
				} else {
					
					if (!isTyping && s.length() != 0) {
						isTyping = true;
						JSONObject data = new JSONObject();
						try {
							data.put("recipientId", recipientId);
							data.put("senderId", senderId);
						} catch (JSONException e) {
							AppHelper.LogCat(e);
						}
						mSocket.emit(AppConstants.SOCKET_IS_TYPING, data);
					}
					
					mTypingHandler.removeCallbacks(onTypingTimeout);
					mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
					
				}
				
				if (PreferenceSettingsManager.enter_send(getActivity())) {
					messageWrapper.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
					messageWrapper.setSingleLine(true);
					messageWrapper.setOnEditorActionListener((v, actionId, event) -> {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND)) {
							sendMessage2();
						}
						return false;
					});
				}
				
			}
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				
				if (s.length() == 0) {
					SendRecordButton.setVisibility(View.VISIBLE);
					SendButton.setVisibility(View.GONE);
					PictureButton.setVisibility(View.GONE);
				}
				
			}
		});
		messageWrapper.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		messageWrapper.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		messageWrapper.setSingleLine(false);
	}
	
	
	private Runnable onTypingTimeout = new Runnable() {
		@Override
		public void run() {
			if (!isTyping) return;
			
			isTyping = false;
			if (isGroup) {
				
				for (MembersGroupModel membersGroupModel : mGroupsModel.getMembers()) {
					JSONObject json = new JSONObject();
					try {
						json.put("recipientId", membersGroupModel.getUserId());
						json.put("senderId", senderId);
						json.put("groupId", groupID);
					} catch (JSONException e) {
						AppHelper.LogCat(e);
					}
					mSocket.emit(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING, json);
					isTyping = false;
					
					
				}
			} else {
				JSONObject json = new JSONObject();
				try {
					json.put("recipientId", recipientId);
					json.put("senderId", senderId);
				} catch (JSONException e) {
					AppHelper.LogCat(e);
				}
				mSocket.emit(AppConstants.SOCKET_IS_STOP_TYPING, json);
				isTyping = false;
				
			}
		}
	};
	
	
	public void ShowGroupMembers(List<MembersGroupModel> membersGroupModels) {
		if (isGroup) {
			try {
				if (membersGroupModels.size() != 0) {
					int arraySize = membersGroupModels.size();
					for (int x = 0; x < arraySize; x++) {
						if (membersGroupModels.get(x).getUserId() == PreferenceManager.getID(getActivity())) {
							if (membersGroupModels.get(x).isLeft()) {
								isLeft = true;
								groupLeftSendMessageLayout.setVisibility(View.VISIBLE);
								SendMessageLayout.setVisibility(View.GONE);
							} else {
								isLeft = false;
								groupLeftSendMessageLayout.setVisibility(View.GONE);
								SendMessageLayout.setVisibility(View.VISIBLE);
							}
							break;
							
						}
						
					}
				}
			} catch (Exception e) {
				AppHelper.LogCat(e.getMessage());
			}
			
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		File fileVideo = null;
		// Get file from file name
		File file = null;
		Bitmap thumbnailBitmap;
		if (resultCode == RESULT_OK) {
			if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
				AppHelper.LogCat("Read contact data permission already granted.");
				switch (requestCode) {
					
					case AppConstants.UPLOAD_PICTURE_REQUEST_CODE:
						FileImagePath = FilesManager.getPath(getActivity().getApplicationContext(), data.getData());
						if (FileImagePath != null) {
							file = new File(FileImagePath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
							
						}
						sendMessage2();
						break;
					case AppConstants.SELECT_MESSAGES_CAMERA:
						AppHelper.LogCat("mProcessingPhotoUri " + mProcessingPhotoUri);
						try {
							if (AppHelper.isAndroid7())
								FileImagePath = FilesManager.convertImageFile(mProcessingPhotoUri, getActivity());
							else
								FileImagePath = FilesManager.getPath(getActivity().getApplicationContext(), mProcessingPhotoUri);
							if (FileImagePath != null) {
								file = new File(FileImagePath);
							}
							if (file != null) {
								FileSize = String.valueOf(file.length());
								
							}
							
							sendMessage2();
							
						} catch (Exception e) {
							AppHelper.LogCat(" Exception " + e.getMessage());
							return;
						}
						break;
					case AppConstants.UPLOAD_VIDEO_REQUEST_CODE:
						try {
							if (AppHelper.isAndroid7()) {
								FileVideoPath = FilesManager.convertVideoFile(data.getData(), getActivity());
							} else {
								FileVideoPath = FilesManager.getPath(getActivity().getApplicationContext(), data.getData());
							}
							if (FileVideoPath != null) {
								file = new File(FileVideoPath);
								MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
								int duration = mp.getDuration();
								Duration = String.valueOf(duration);
								mp.release();
							}
							if (file != null) {
								FileSize = String.valueOf(file.length());
							}
							thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
							try {
								fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
							} catch (IOException e) {
								AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
							}
							if (AppHelper.isAndroid7()) {
								FileVideoThumbnailPath = fileVideo.getPath();
							} else {
								FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
							}
							
							sendMessage2();
						} catch (Exception e) {
							AppHelper.LogCat(" Exception " + e.getMessage());
							return;
						}
						
						break;
					case AppConstants.SELECT_MESSAGES_RECORD_VIDEO:
						AppHelper.LogCat("data " + mProcessingPhotoUri);
						try {
							if (AppHelper.isAndroid7())
								FileVideoPath = FilesManager.convertVideoFile(mProcessingPhotoUri, getActivity());
							else
								FileVideoPath = FilesManager.getPath(getActivity().getApplicationContext(), mProcessingPhotoUri);
							if (FileVideoPath != null) {
								file = new File(FileVideoPath);
								MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileVideoPath));
								int duration = mp.getDuration();
								Duration = String.valueOf(duration);
								mp.release();
							}
							if (file != null) {
								FileSize = String.valueOf(file.length());
							}
							thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(FileVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
							try {
								fileVideo = FilesManager.getFileThumbnail(getActivity(), thumbnailBitmap);
							} catch (IOException e) {
								AppHelper.LogCat("IOException video thumbnail " + e.getMessage());
							}
							if (AppHelper.isAndroid7()) {
								FileVideoThumbnailPath = fileVideo.getPath();
							} else {
								FileVideoThumbnailPath = FilesManager.getPath(getActivity().getApplicationContext(), FilesManager.getFile(fileVideo));
							}
							
							sendMessage2();
						} catch (Exception e) {
							AppHelper.LogCat(" Exception " + e.getMessage());
							return;
						}
						
						break;
					case AppConstants.UPLOAD_AUDIO_REQUEST_CODE:
						try {
							FileAudioPath = FilesManager.getPath(getActivity().getApplicationContext(), data.getData());
							MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(FileAudioPath));
							int duration = mp.getDuration();
							Duration = String.valueOf(duration);
							mp.release();
							sendMessage2();
						} catch (Exception e) {
							AppHelper.LogCat(" Exception " + e.getMessage());
							return;
						}
						
						
						break;
					case AppConstants.UPLOAD_DOCUMENT_REQUEST_CODE:
						FileDocumentPath = FilesManager.getPath(getActivity().getApplicationContext(), data.getData());
						if (FileDocumentPath != null) {
							file = new File(FileDocumentPath);
						}
						if (file != null) {
							FileSize = String.valueOf(file.length());
						}
						sendMessage2();
						break;
					case AppConstants.SELECT_ADD_NEW_CONTACT:
						EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_CONTACT_ADDED));
						mMessagesPresenterFrg.getRecipientInfo();
						break;
					
				}
			} else {
				AppHelper.LogCat("Please request Read contact data permission.");
				PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
			}
			
			
		}
	}


//......................mm................
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

//......................mm................
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.messages_menu, menu);
		
		getActivity().invalidateOptionsMenu();
		ActivityCompat.invalidateOptionsMenu(getActivity());
		getActivity().supportInvalidateOptionsMenu();
		if (isGroup) {
			if (isLeft)
				inflater.inflate(R.menu.groups_menu_user_left, menu);
			else
				inflater.inflate(R.menu.groups_menu, menu);
		} else {
			if (mUsersModelRecipient != null && mUsersModelRecipient.isValid())
				if (mUsersModelRecipient.getPhone() != null && UtilsPhone.checkIfContactExist(getActivity(), mUsersModelRecipient.getPhone())) {
					if (checkIfUserBlockedExist(recipientId, realm)) {
						inflater.inflate(R.menu.messages_menu_unblock, menu);
					} else {
						inflater.inflate(R.menu.messages_menu, menu);
					}
					
				} else {
					if (checkIfUserBlockedExist(recipientId, realm)) {
						inflater.inflate(R.menu.messages_menu_user_not_exist_unblock, menu);
					} else {
						inflater.inflate(R.menu.messages_menu_user_not_exist, menu);
					}
					
				}
			
			super.onCreateOptionsMenu(menu, inflater);
		}
		
		
	}
	
	
	private void makeCall() {
		AlertDialog myDialog;
		String[] items = {getString(R.string.voice_call_dialog_title), getString(R.string.video_call_dialog_title)};
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setItems(items, (dialog, which) -> {
			if (items.length > 0) {
				if (items[which].equals(getString(R.string.voice_call_dialog_title))) {
					CallManager.callContact(getActivity(), false, false, recipientId);
				} else if (items[which].equals(getString(R.string.video_call_dialog_title))) {
					CallManager.callContact(getActivity(), false, true, recipientId);
				}
			}
		});
		builder.setCancelable(true);
		myDialog = builder.create();
		myDialog.show();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (isGroup) {
			
			switch (item.getItemId()) {
				case R.id.attach_file:
					if (!isOpen) {
						isOpen = true;
						animateItems(true);
						
					} else {
						isOpen = false;
						animateItems(false);
					}
					break;
				case R.id.search_messages_group:
					launcherSearchView();
					break;
				case R.id.view_group:
					if (AppHelper.isAndroid5()) {
						mIntent = new Intent(getActivity(), ProfileActivity.class);
						mIntent.putExtra("groupID", groupID);
						mIntent.putExtra("isGroup", true);
						startActivity(mIntent);
					} else {
						mIntent = new Intent(getActivity(), ProfileActivity.class);
						mIntent.putExtra("groupID", groupID);
						mIntent.putExtra("isGroup", true);
						startActivity(mIntent);
					}
					break;
			}
		} else {
			
			switch (item.getItemId()) {
				case R.id.attach_file:
					if (!isOpen) {
						isOpen = true;
						animateItems(true);
						
					} else {
						isOpen = false;
						animateItems(false);
					}
					break;
				case R.id.call_contact:
					if (isOpen) {
						isOpen = false;
						animateItems(false);
					}
					makeCall();
					break;
              /*  case R.id.search_messages:
                    launcherSearchView();
                    break;*/
				case R.id.add_contact:
					addNewContact();
					break;
				case R.id.view_contact:
					mIntent = new Intent(getActivity(), ProfileActivity.class);
					mIntent.putExtra("userID", recipientId);
					mIntent.putExtra("isGroup", false);
					startActivity(mIntent);
					break;
				case R.id.clear_chat:
					Realm realm = RooyeshApplication.getRealmDatabaseInstance();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(R.string.clear_chat);
					builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
						AppHelper.showDialog(getActivity(), getString(R.string.clear_chat));
						EventBus.getDefault().post(new Pusher(EVENT_BUS_DELETE_CONVERSATION_ITEM, ConversationID));
						realm.executeTransactionAsync(realm1 -> {
							RealmResults<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).equalTo("conversationID", ConversationID).findAll();
							messagesModel1.deleteAllFromRealm();
						}, () -> {
							AppHelper.LogCat("Message Deleted  successfully  MessagesPopupActivity");
							
							RealmResults<MessagesModel> messagesModel1 = realm.where(MessagesModel.class).equalTo("conversationID", ConversationID).findAll();
							if (messagesModel1.size() == 0) {
								realm.executeTransactionAsync(realm1 -> {
									ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
									conversationsModel1.deleteFromRealm();
								}, () -> {
									AppHelper.LogCat("Conversation deleted successfully MessagesPopupActivity");
									
									EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
									notificationsManager.SetupBadger(getActivity());
									getActivity().finish();
								}, error -> {
									AppHelper.LogCat("Delete conversation failed  MessagesPopupActivity" + error.getMessage());
									
								});
							} else {
								MessagesModel lastMessage = realm.where(MessagesModel.class).equalTo("conversationID", ConversationID).findAll().last();
								realm.executeTransactionAsync(realm1 -> {
									ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
									conversationsModel1.setLastMessage(lastMessage.getMessage());
									conversationsModel1.setLastMessageId(lastMessage.getId());
									conversationsModel1.setSenderID(senderId);
									realm1.copyToRealmOrUpdate(conversationsModel1);
								}, () -> {
									AppHelper.LogCat("Conversation deleted successfully MessagesPopupActivity ");
									EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
									notificationsManager.SetupBadger(getActivity());
									getActivity().finish();
								}, error -> {
									AppHelper.LogCat("Delete conversation failed  MessagesPopupActivity" + error.getMessage());
									
								});
							}
						}, error -> {
							AppHelper.LogCat("Delete message failed MessagesPopupActivity" + error.getMessage());
							
						});
						AppHelper.hideDialog();
						
					});
					
					builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {
					
					});
					
					builder.show();
					
					if (!realm.isClosed())
						realm.close();
					break;
				case R.id.block_user:
					blockContact();
					break;
				case R.id.unblock_user:
					unBlockContact();
					break;
				
				
			}
		}
		
		return true;
	}
	
	
	private void launcherSearchView() {
		final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_for_button_animtion_enter);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				searchView.setVisibility(View.VISIBLE);
				toolbar.setVisibility(View.GONE);
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			
			}
		});
		searchView.startAnimation(animation);
	}
	
	
	/**
	 * method to close the searchview with animation
	 */
	@SuppressWarnings("unused")
	@OnClick(R.id.close_btn_search_view)
	public void closeSearchView() {
		final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_for_button_animtion_exit);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				searchView.setVisibility(View.GONE);
				toolbar.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			
			}
		});
		searchView.startAnimation(animation);
	}
	
	
	/**
	 * method to show all user messages
	 *
	 * @param messagesModels this is parameter for ShowMessages method
	 */
	public void ShowMessages(List<MessagesModel> messagesModels) {
		
		RealmList<MessagesModel> mMessagesList = new RealmList<MessagesModel>();
		for (MessagesModel messagesModel : messagesModels) {
			mMessagesList.add(messagesModel);
			ConversationID = messagesModel.getConversationID();
		}
		mMessagesAdapter.setMessages(mMessagesList);
	}
	
	
	/**
	 * method to update group information
	 *
	 * @param groupsModel
	 */
	public void updateGroupInfo(GroupsModel groupsModel) {
		mGroupsModel = groupsModel;
		Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, groupsModel.getGroupImage(), getActivity(), groupID, AppConstants.GROUP, AppConstants.ROW_PROFILE);
		if (bitmap != null) {
			ImageLoader.SetBitmapImage(bitmap, ToolbarImage);
		} else {
			RooyeshImageLoader.loadCircleImageGroup(getActivity(), EndPoints.ROWS_IMAGE_URL + groupsModel.getGroupImage(), ToolbarImage, R.drawable.image_holder_gr_circle, AppConstants.ROWS_IMAGE_SIZE);
		}
		String name = UtilsString.unescapeJava(groupsModel.getGroupName());
		if (name.length() > 13) {
			ToolbarTitle.setText(name.substring(0, 10) + "... " + "");
		} else {
			ToolbarTitle.setText(name);
		}
		Realm realm = RooyeshApplication.getRealmDatabaseInstance();
		List<MembersGroupModel> groups = realm.where(MembersGroupModel.class).equalTo("groupID", groupID).equalTo("Deleted", false).findAll();
		int arraySize = groups.size();
		StringBuilder names = new StringBuilder();
		for (int x = 0; x <= arraySize - 1; x++) {
			if (x <= 1) {
				String finalName;
				if (groups.get(x).getUserId() == PreferenceManager.getID(getActivity())) {
					finalName = getString(R.string.you);
				} else {
					String phone = UtilsPhone.getContactName(getActivity(), groups.get(x).getPhone());
					if (phone != null) {
						try {
							finalName = phone.substring(0, 5);
						} catch (Exception e) {
							AppHelper.LogCat(e);
							finalName = phone;
						}
					} else {
						finalName = groups.get(x).getPhone().substring(0, 5);
					}
					
				}
				names.append(finalName);
				names.append(",");
			}
			
		}
		String groupsNames = UtilsString.removelastString(names.toString());
		statusUser.setVisibility(View.VISIBLE);
		statusUser.setText(groupsNames);
		AnimationsUtil.slideStatus(statusUser);
		
		if (!realm.isClosed()) realm.close();
	}
	
	
	private void updateUStatus(String statusUserTyping) {
		if (isGroup) return;
		if (statusUserTyping.contains(AppConstants.STATUS_USER_DISCONNECTED_STATE)) {
			showStatus();
			statusUser.setText(getString(R.string.isOffline));
			AppHelper.LogCat("Offline...");
		} else if (statusUserTyping.contains(AppConstants.STATUS_USER_CONNECTED_STATE)) {
			showStatus();
			statusUser.setText(getString(R.string.isOnline));
			AnimationsUtil.slideStatus(statusUser);
			AppHelper.LogCat("Online...");
		} else if (statusUserTyping.contains(AppConstants.STATUS_USER_LAST_SEEN_STATE)) {
			showStatus();
			AppHelper.LogCat("lastSeen...");
			statusUser.setText(getString(R.string.lastSeen) + " " + statusUserTyping.substring(statusUserTyping.indexOf(' '), statusUserTyping.length()));
			AnimationsUtil.slideStatus(statusUser);
		}
		
	}
	
	
	public void updateContactRecipient(ContactsModel contactsModels) {
		mUsersModelRecipient = contactsModels;
		if (contactsModels.getUserState() != null) {
			updateUStatus(contactsModels.getUserState());
		}
		refreshMenu();
		try {
			
			if (UtilsPhone.checkIfContactExist(getActivity(), contactsModels.getPhone())) {
				AddContactBtn.setVisibility(View.GONE);
				blockLayout.setVisibility(View.GONE);
			} else {
				// AddContactBtn.setVisibility(View.VISIBLE);
				// blockLayout.setVisibility(View.VISIBLE);
				if (checkIfUserBlockedExist(recipientId, realm)) {
					UnBlockContactBtn.setVisibility(View.VISIBLE);
					BlockContactBtn.setVisibility(View.GONE);
				} else {
					UnBlockContactBtn.setVisibility(View.GONE);
					BlockContactBtn.setVisibility(View.VISIBLE);
				}
			}
			
			String name = UtilsPhone.getContactName(getActivity(), contactsModels.getPhone());
			if (name != null) {
				ToolbarTitle.setText(name);
			} else {
				ToolbarTitle.setText(contactsModels.getPhone());
			}
			
		} catch (Exception e) {
			AppHelper.LogCat(" Recipient username  is null MessagesPopupActivity" + e.getMessage());
		}
		
		Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, contactsModels.getImage(), getActivity(), recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);
		if (bitmap != null) {
			ImageLoader.SetBitmapImage(bitmap, ToolbarImage);
		} else {
			RooyeshImageLoader.loadCircleImage(getActivity(), EndPoints.ROWS_IMAGE_URL + contactsModels.getImage(), ToolbarImage, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
		}
		
	}


   /* @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (isOpen) {
            isOpen = false;
            animateItems(false);
        } else if (emoticonShown) {
            emoticonShown = false;
            emojIcon.closeEmojIcon();
            SendMessageLayout.setBackground(getResources().getDrawable(android.R.color.transparent));
        } else {
            mMessagesAdapter.stopAudio();
            if (notificationsManager.getManager()) {
                if (isGroup)
                    notificationsManager.cancelNotification(groupID);
                else
                    notificationsManager.cancelNotification(recipientId);
            }
            if (isGroup) {
                mMessagesPresenterFrg.updateGroupConversationStatus();
            } else {
                mMessagesPresenterFrg.updateConversationStatus();
            }

            super.onBackPressed();
            AnimationsUtil.setSlideOutAnimation(this);
        }


    }*/
	
	
	@Override
	public void onPause() {
		super.onPause();
		if (!isGroup)
			LastSeenTimeEmit();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		realm.close();
		mMessagesPresenterFrg.onDestroy();
		if (emojIcon != null) {
			emojIcon.closeEmojIcon();
			emojIcon = null;
		}
		
	}
	
	@Override
	public void onShowLoading() {
	
	}
	
	@Override
	public void onHideLoading() {
	
	}
	
	@Override
	public void onErrorLoading(Throwable throwable) {
		AppHelper.LogCat("Messages " + throwable.getMessage());
	}
	
	
	/**
	 * method to emit last seen of conversation
	 */
	private void LastSeenTimeEmit() {
		DateTime current = new DateTime();
		String lastTime = String.valueOf(current);
		JSONObject data = new JSONObject();
		try {
			data.put("senderId", PreferenceManager.getID(getActivity()));
			data.put("recipientId", recipientId);
			data.put("lastSeen", lastTime);
		} catch (JSONException e) {
			AppHelper.LogCat(e);
		}
		mSocket.emit(AppConstants.SOCKET_IS_LAST_SEEN, data);
	}
	
	
	private void hideStatus() {
		TransitionManager.beginDelayedTransition(mView);
		statusUser.setVisibility(View.GONE);
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
			case AppConstants.EVENT_BUS_NEW_MESSAGE_MESSAGES_NEW_ROW:
				MessagesModel messagesModel = pusher.getMessagesModel();
				if (messagesModel.getSenderID() == recipientId && messagesModel.getRecipientID() == senderId) {
					
					//  AppHelper.playSound(getActivity(), "audio/incoming_message.wav");
					addMessage(messagesModel);
					new Handler().postDelayed(() -> mMessagesPresenterFrg.updateConversationStatus(), 500);
					
				}
				break;
			case AppConstants.EVENT_BUS_NEW_GROUP_MESSAGE_MESSAGES_NEW_ROW:
				if (isGroup) {
					MessagesModel messagesModel1 = pusher.getMessagesModel();
					if (messagesModel1.getSenderID() != PreferenceManager.getID(getActivity())) {
						
						if (groupID == messagesModel1.getGroupID())
							addMessage(messagesModel1);
						new Handler().postDelayed(() -> mMessagesPresenterFrg.updateGroupConversationStatus(), 500);
					}
				}
				break;
			
			
			case AppConstants.EVENT_BUS_MESSAGE_IS_DELIVERED_FOR_MESSAGES:
			case AppConstants.EVENT_BUS_MESSAGE_IS_SENT_FOR_MESSAGES:
			case AppConstants.EVENT_BUS_MESSAGE_IS_SEEN_FOR_MESSAGES:
				new Handler().postDelayed(() -> mMessagesAdapter.updateStatusMessageItem(pusher.getMessageId()), 500);
				break;
			case AppConstants.EVENT_BUS_UPLOAD_MESSAGE_FILES:
				if (pusher.getMessagesModel().isGroup())
					unSentMessagesGroup(pusher.getMessagesModel().getGroupID());
				else
					unSentMessagesForARecipient(pusher.getMessagesModel().getRecipientID(), true);
				break;
			case AppConstants.EVENT_BUS_UPDATE_USER_STATE:
				if (pusher.getData().equals(getString(R.string.isOnline)))
					updateUserStatus(AppConstants.STATUS_USER_CONNECTED, null);
				else if (pusher.getData().equals(getString(R.string.isOffline)))
					updateUserStatus(AppConstants.STATUS_USER_DISCONNECTED, null);
				break;
			case AppConstants.EVENT_BUS_ITEM_IS_ACTIVATED_MESSAGES:
				int idx = messagesList.getChildAdapterPosition(pusher.getView());
				if (actionMode != null) {
					ToggleSelection(idx);
					return;
				}
				break;
			
			case AppConstants.EVENT_BUS_NEW_USER_NOTIFICATION:
				NotificationsModel newUserNotification = pusher.getNotificationsModel();
				if (newUserNotification.getRecipientId() == recipientId) {
					return;
				} else {
					
					//tagir
					
					// if (newUserNotification.getAppName() != null && newUserNotification.getAppName().equals(getActivity().getApplicationContext().getPackageName())) {
					
					if (newUserNotification.getFile() != null) {
						notificationsManager.showUserNotification(getActivity().getApplicationContext(), newUserNotification.getConversationID(), newUserNotification.getPhone(), newUserNotification.getFile(), recipientId, newUserNotification.getImage());
					} else {
						notificationsManager.showUserNotification(getActivity().getApplicationContext(), newUserNotification.getConversationID(), newUserNotification.getPhone(), newUserNotification.getMessage(), recipientId, newUserNotification.getImage());
					}
					//  }
					
				}
				
				break;
			case AppConstants.EVENT_BUS_NEW_GROUP_NOTIFICATION:
				NotificationsModel newGroupNotification = pusher.getNotificationsModel();
				if (newGroupNotification.getGroupID() == groupID) {
					return;
				} else {
					if (newGroupNotification.getAppName() != null && newGroupNotification.getAppName().equals(getActivity().getApplicationContext().getPackageName())) {
						
						/**
						 * this for default activity
						 */
						
						//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
						
						
						Bundle bundle = new Bundle();
						bundle.putInt("conversationID", newGroupNotification.getConversationID());
						bundle.putBoolean("isGroup", newGroupNotification.isGroup());
						bundle.putInt("groupID", newGroupNotification.getGroupID());
						MessagesFragment messageFragmentOk = new MessagesFragment();
						messageFragmentOk.setArguments(bundle);
						
						
						//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
						
						Intent messagingGroupIntent = new Intent(getActivity().getApplicationContext(), MessagesActivity.class);
						messagingGroupIntent.putExtra("conversationID", newGroupNotification.getConversationID());
						messagingGroupIntent.putExtra("groupID", newGroupNotification.getGroupID());
						messagingGroupIntent.putExtra("isGroup", newGroupNotification.isGroup());
						messagingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						/**
						 * this for popup activity
						 */
						Intent messagingGroupPopupIntent = new Intent(getActivity().getApplicationContext(), MessagesPopupActivity.class);
						messagingGroupPopupIntent.putExtra("conversationID", newGroupNotification.getConversationID());
						messagingGroupPopupIntent.putExtra("groupID", newGroupNotification.getGroupID());
						messagingGroupPopupIntent.putExtra("isGroup", newGroupNotification.isGroup());
						messagingGroupPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						
						String message;
						String userName = UtilsPhone.getContactName(getActivity().getApplicationContext(), newGroupNotification.getPhone());
						switch (newGroupNotification.getMessage()) {
							case AppConstants.CREATE_GROUP:
								if (userName != null) {
									message = "" + getActivity().getApplicationContext().getString(R.string.he_created_this_group) + " " + userName;
								} else {
									message = "" + getActivity().getApplicationContext().getString(R.string.he_created_this_group) + " " + newGroupNotification.getPhone();
								}
								
								
								break;
							case AppConstants.LEFT_GROUP:
								if (userName != null) {
									message = "" + userName + getActivity().getApplicationContext().getString(R.string.he_left);
								} else {
									message = "" + newGroupNotification.getPhone() + getActivity().getApplicationContext().getString(R.string.he_left);
								}
								
								break;
							default:
								message = newGroupNotification.getMessage();
								break;
						}
						if (newGroupNotification.getFile() != null) {
							notificationsManager.showGroupNotification(getActivity().getApplicationContext(), messagingGroupIntent, messagingGroupPopupIntent, newGroupNotification.getGroupName(), newGroupNotification.getMemberName() + " : " + newGroupNotification.getFile(), newGroupNotification.getGroupID(), newGroupNotification.getImage());
						} else {
							notificationsManager.showGroupNotification(getActivity().getApplicationContext(), messagingGroupIntent, messagingGroupPopupIntent, newGroupNotification.getGroupName(), newGroupNotification.getMemberName() + " : " + message, newGroupNotification.getGroupID(), newGroupNotification.getImage());
						}
					}
				}
				
				break;
			
		}
		//  });
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
	
	
	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		MenuInflater inflater = actionMode.getMenuInflater();
       /* if (isGroup)
            inflater.inflate(R.menu.select_share_messages_group_menu, menu);
        else*/
		inflater.inflate(R.menu.select_share_messages_menu, menu);
		
		((AppCompatActivity) getActivity()).getSupportActionBar().hide();
		if (AppHelper.isAndroid5()) {
			Window window = getActivity().getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(AppHelper.getColor(getActivity(), R.color.colorActionMode));
		}
		return true;
	}
	
	
	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		
		int arraySize = mMessagesAdapter.getSelectedItems().size();
		int currentPosition;
		switch (menuItem.getItemId()) {
			case R.id.share_content:
				if (arraySize != 0 && arraySize == 1) {
					for (int x = 0; x < arraySize; x++) {
						currentPosition = mMessagesAdapter.getSelectedItems().get(x);
						MessagesModel messagesModel = mMessagesAdapter.getItem(currentPosition);
						if (messagesModel.getMessage() != null) {
							if (messagesModel.getSenderID() == PreferenceManager.getID(getActivity())) {
								if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
									if (FilesManager.isFileVideosSentExists(getActivity(), FilesManager.getVideo(messagesModel.getVideoFile()))) {
										File file = FilesManager.getFileVideoSent(getActivity(), messagesModel.getVideoFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_VIDEOS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_video_is_not_exist));
									}
								} else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
									if (FilesManager.isFileAudiosSentExists(getActivity(), FilesManager.getAudio(messagesModel.getAudioFile()))) {
										File file = FilesManager.getFileAudioSent(getActivity(), messagesModel.getAudioFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_AUDIO);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_audio_is_not_exist));
									}
								} else if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
									if (FilesManager.isFileImagesSentExists(getActivity(), FilesManager.getImage(messagesModel.getImageFile()))) {
										File file = FilesManager.getFileImageSent(getActivity(), messagesModel.getImageFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_IMAGES);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_image_is_not_exist));
									}
								} else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
									if (FilesManager.isFileDocumentsSentExists(getActivity(), FilesManager.getDocument(messagesModel.getDocumentFile()))) {
										File file = FilesManager.getFileDocumentSent(getActivity(), messagesModel.getDocumentFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_DOCUMENTS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_document_is_not_exist));
									}
								} else {
									AppHelper.shareIntent(null, getActivity(), messagesModel.getMessage(), AppConstants.SENT_TEXT);
								}
							} else {
								if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
									if (FilesManager.isFileVideosExists(getActivity(), FilesManager.getVideo(messagesModel.getVideoFile()))) {
										File file = FilesManager.getFileVideo(getActivity(), messagesModel.getVideoFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_VIDEOS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_video_is_not_exist));
									}
								} else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
									if (FilesManager.isFileAudioExists(getActivity(), FilesManager.getAudio(messagesModel.getAudioFile()))) {
										File file = FilesManager.getFileAudio(getActivity(), messagesModel.getAudioFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_AUDIO);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_audio_is_not_exist));
									}
								} else if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
									if (FilesManager.isFileImagesExists(getActivity(), FilesManager.getImage(messagesModel.getImageFile()))) {
										File file = FilesManager.getFileImage(getActivity(), messagesModel.getImageFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_IMAGES);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_image_is_not_exist));
									}
								} else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
									if (FilesManager.isFileDocumentsExists(getActivity(), FilesManager.getDocument(messagesModel.getDocumentFile()))) {
										File file = FilesManager.getFileDocument(getActivity(), messagesModel.getDocumentFile());
										AppHelper.shareIntent(file, getActivity(), messagesModel.getMessage(), AppConstants.SENT_DOCUMENTS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_document_is_not_exist));
									}
								} else {
									AppHelper.shareIntent(null, getActivity(), messagesModel.getMessage(), AppConstants.SENT_TEXT);
								}
							}
							
						} else {
							if (messagesModel.getSenderID() == PreferenceManager.getID(getActivity())) {
								if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
									if (FilesManager.isFileVideosSentExists(getActivity(), FilesManager.getVideo(messagesModel.getVideoFile()))) {
										File file = FilesManager.getFileVideoSent(getActivity(), messagesModel.getVideoFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_VIDEOS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_video_is_not_exist));
									}
								} else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
									if (FilesManager.isFileAudiosSentExists(getActivity(), FilesManager.getAudio(messagesModel.getAudioFile()))) {
										File file = FilesManager.getFileAudioSent(getActivity(), messagesModel.getAudioFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_AUDIO);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_audio_is_not_exist));
									}
								} else if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
									if (FilesManager.isFileImagesSentExists(getActivity(), FilesManager.getImage(messagesModel.getImageFile()))) {
										File file = FilesManager.getFileImageSent(getActivity(), messagesModel.getImageFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_IMAGES);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_image_is_not_exist));
									}
								} else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
									if (FilesManager.isFileDocumentsSentExists(getActivity(), FilesManager.getDocument(messagesModel.getDocumentFile()))) {
										File file = FilesManager.getFileDocumentSent(getActivity(), messagesModel.getDocumentFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_DOCUMENTS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_document_is_not_exist));
									}
								}
							} else {
								if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
									if (FilesManager.isFileVideosExists(getActivity(), FilesManager.getVideo(messagesModel.getVideoFile()))) {
										File file = FilesManager.getFileVideo(getActivity(), messagesModel.getVideoFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_VIDEOS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_video_is_not_exist));
									}
								} else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
									if (FilesManager.isFileAudioExists(getActivity(), FilesManager.getAudio(messagesModel.getAudioFile()))) {
										File file = FilesManager.getFileAudio(getActivity(), messagesModel.getAudioFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_AUDIO);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_audio_is_not_exist));
									}
								} else if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
									if (FilesManager.isFileImagesExists(getActivity(), FilesManager.getImage(messagesModel.getImageFile()))) {
										File file = FilesManager.getFileImage(getActivity(), messagesModel.getImageFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_IMAGES);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_image_is_not_exist));
									}
								} else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
									if (FilesManager.isFileDocumentsExists(getActivity(), FilesManager.getDocument(messagesModel.getDocumentFile()))) {
										File file = FilesManager.getFileDocument(getActivity(), messagesModel.getDocumentFile());
										AppHelper.shareIntent(file, getActivity(), null, AppConstants.SENT_DOCUMENTS);
									} else {
										AppHelper.CustomToast(getActivity(), getString(R.string.this_document_is_not_exist));
									}
								}
							}
						}
						
						break;
					}
				} else {
					AppHelper.CustomToast(getActivity(), getString(R.string.you_can_share_more_then_one));
				}
				
				break;
			case R.id.copy_message:
				if (arraySize != 0 && arraySize == 1) {
					for (int x = 0; x < arraySize; x++) {
						currentPosition = mMessagesAdapter.getSelectedItems().get(x);
						MessagesModel messagesModel = mMessagesAdapter.getItem(currentPosition);
						if (messagesModel.getMessage() != null) {
							if (AppHelper.copyText(getActivity(), messagesModel)) {
								AppHelper.CustomToast(getActivity(), getString(R.string.message_is_copied));
								if (actionMode != null) {
									mMessagesAdapter.clearSelections();
									actionMode.finish();
									//  getSupportActionBar().show();
									((AppCompatActivity) getActivity()).getSupportActionBar().show();
								}
							}
						} else {
							AppHelper.CustomToast(getActivity(), getString(R.string.this_message_empty));
						}
					}
					
				} else {
					if (actionMode != null) {
						mMessagesAdapter.clearSelections();
						actionMode.finish();
						//  getSupportActionBar().show();
						((AppCompatActivity) getActivity()).getSupportActionBar().show();
						
					}
					AppHelper.CustomToast(getActivity(), getString(R.string.you_can_copy_more_then_one));
				}
				
				break;
			case R.id.delete_message:
				if (arraySize != 0) {
					Realm realm = RooyeshApplication.getRealmDatabaseInstance();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(R.string.message_delete);
					
					builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
						AppHelper.showDialog(getActivity(), getString(R.string.deleting_chat));
						for (int x = 0; x < arraySize; x++) {
							int currentPosition1 = mMessagesAdapter.getSelectedItems().get(x);
							MessagesModel messagesModel = mMessagesAdapter.getItem(currentPosition1);
							EventBus.getDefault().post(new Pusher(EVENT_BUS_DELETE_CONVERSATION_ITEM, ConversationID));
							int messageId = messagesModel.getId();
							realm.executeTransactionAsync(realm1 -> {
								MessagesModel messagesModel1 = realm1.where(MessagesModel.class).equalTo("id", messageId).equalTo("conversationID", ConversationID).findFirst();
								messagesModel1.deleteFromRealm();
							}, () -> {
								AppHelper.LogCat("Message deleted successfully MessagesActivity ");
								mMessagesAdapter.removeMessageItem(currentPosition1);
								RealmResults<MessagesModel> messagesModel1 = realm.where(MessagesModel.class).equalTo("conversationID", ConversationID).findAll();
								if (messagesModel1.size() == 0) {
									realm.executeTransactionAsync(realm1 -> {
										ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
										conversationsModel1.deleteFromRealm();
									}, () -> {
										AppHelper.LogCat("Conversation deleted successfully MessagesActivity ");
										EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
										notificationsManager.SetupBadger(getActivity());
									}, error -> {
										AppHelper.LogCat("delete conversation failed MessagesActivity " + error.getMessage());
										
									});
								} else {
									MessagesModel lastMessage = realm.where(MessagesModel.class).equalTo("conversationID", ConversationID).findAll().last();
									if (!lastMessage.isValid()) return;
									realm.executeTransactionAsync(realm1 -> {
										ConversationsModel conversationsModel1 = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
										conversationsModel1.setLastMessage(lastMessage.getMessage());
										conversationsModel1.setLastMessageId(lastMessage.getId());
										conversationsModel1.setSenderID(senderId);
										realm1.copyToRealmOrUpdate(conversationsModel1);
									}, () -> {
										AppHelper.LogCat("Conversation deleted successfully  MessagesActivity ");
										EventBus.getDefault().post(new Pusher(EVENT_BUS_MESSAGE_COUNTER));
										notificationsManager.SetupBadger(getActivity());
									}, error -> {
										AppHelper.LogCat("delete conversation failed  MessagesActivity" + error.getMessage());
										
									});
								}
							}, error -> {
								AppHelper.LogCat("delete message failed  MessagesActivity" + error.getMessage());
								
							});
							
						}
						AppHelper.hideDialog();
						
						if (actionMode != null) {
							mMessagesAdapter.clearSelections();
							actionMode.finish();
							// getSupportActionBar().show();
							((AppCompatActivity) getActivity()).getSupportActionBar().show();
						}
						
					});
					
					builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {
					
					});
					
					builder.show();
					realm.close();
				}
				break;
			case R.id.transfer_message:
				if (arraySize != 0) {
					ArrayList<String> messagesModelList = new ArrayList<>();
					for (int x = 0; x < arraySize; x++) {
						currentPosition = mMessagesAdapter.getSelectedItems().get(x);
						MessagesModel messagesModel = mMessagesAdapter.getItem(currentPosition);
						String message = UtilsString.unescapeJava(messagesModel.getMessage());
						messagesModelList.add(message);
					}
					if (messagesModelList.size() != 0) {
						Intent intent = new Intent(getActivity(), TransferMessageContactsActivity.class);
						intent.putExtra("messageCopied", messagesModelList);
						startActivity(intent);
						getActivity().finish();
						// getActivity().getSupportFragmentManager().popBackStack();
					} else {
						AppHelper.CustomToast(getActivity(), getString(R.string.this_message_empty));
					}
				}
				break;
			default:
				return false;
		}
		
		
		return false;
	}
	
	
	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		this.actionMode = null;
		mMessagesAdapter.clearSelections();
		//getSupportActionBar().show();
		((AppCompatActivity) getActivity()).getSupportActionBar().show();
		if (AppHelper.isAndroid5()) {
			Window window = getActivity().getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(AppHelper.getColor(getActivity(), R.color.colorPrimaryDark));
		}
	}
	
	
	@Override
	public void onClick(View view) {
		int position = messagesList.getChildAdapterPosition(view);
		if (actionMode != null) {
			ToggleSelection(position);
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		RooyeshApplication.getInstance().setConnectivityListener(this);
		connectToChatServer();
	}
	
	
	/**
	 * Callback will be triggered when there is change in
	 * network connection
	 */
	@Override
	public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
		if (!isConnecting && !isConnected) {
			AppHelper.Snackbar(getActivity(), mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
		} else if (isConnecting && isConnected) {
			AppHelper.Snackbar(getActivity(), mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);/*
            if (isGroup)
                new Handler().postDelayed(() -> unSentMessagesGroup(groupID), 1000);
            else
                new Handler().postDelayed(() -> unSentMessagesForARecipient(recipientId, false), 1000);*/
		} else {
			AppHelper.Snackbar(getActivity(), mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
			
		}
	}
	
	
	/**
	 * method to clear/reset search view
	 */
	@SuppressWarnings("unused")
	@OnClick(R.id.clear_btn_search_view)
	public void clearSearchView() {
		searchInput.setText("");
	}
	
	
	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}
	
	
	/**
	 * method to update  contact information
	 *
	 * @param contactsModels this is parameter for updateContact method
	 */
	public void updateContact(ContactsModel contactsModels) {
		mUsersModel = contactsModels;
	}
	
	
}

