package com.setayeshco.rooyesh.presenters.messages;

import android.os.Handler;

import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.RooyeshApplication;
import com.setayeshco.rooyesh.fragments.home.ConversationsFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.interfaces.Presenter;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;
import com.setayeshco.rooyesh.models.users.Pusher;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ConversationsPresenter implements Presenter {
    private final ConversationsFragment conversationsFragmentView;
    private final Realm realm;
    private UsersService mUsersContacts;


    public ConversationsPresenter(ConversationsFragment conversationsFragment) {
        this.conversationsFragmentView = conversationsFragment;
        this.realm = RooyeshApplication.getRealmDatabaseInstance();

    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(conversationsFragmentView))
            EventBus.getDefault().register(conversationsFragmentView);

        APIService mApiService = APIService.with(conversationsFragmentView.getActivity());
        mUsersContacts = new UsersService(realm, conversationsFragmentView.getActivity(), mApiService);
        loadData2(false);


    }


    private void loadData(boolean isRefresh) {
        if (isRefresh)
            conversationsFragmentView.onShowLoading();
        else
            conversationsFragmentView.onProgressShow();
        try {

            APIHelper.initializeApiGroups().updateGroups().subscribe(groupsModelList -> {
                AppHelper.LogCat("groupsModelList " + groupsModelList.size());
            }, throwable -> {

                AppHelper.LogCat("onerror ");

            }, () -> {
                AppHelper.LogCat("oncomplete ");

            });

          //  mUsersContacts.getConversation(1);

//            Observable.create((ObservableOnSubscribe<List<ConversationsModel>>) subscriber -> {
//                try
//                {
//                   // List<ContactsModel> contactsModels = UtilsPhone.GetPhoneContacts();
//                   /// subscriber.onNext(contactsModels);
//                   // subscriber.onComplete();
//                } catch (Exception throwable) {
//                    subscriber.onError(throwable);
//                }
//            }).subscribeOn(Schedulers.computation()).subscribe(contacts -> {
//                mUsersContacts.getConversation(contacts,2).subscribe(conversationsModels -> {
//                    if (conversationsModels != null)
//                        conversationsFragmentView.UpdateConversation(conversationsModels);
//                    new Handler().postDelayed(() ->
//                    {
//                      //  mUsersContacts.getContactInfo(PreferenceManager.getID(contactsFragmentView.getContext())).subscribe(contactsModel -> AppHelper.LogCat("info user ContactsPresenter"), throwable -> AppHelper.LogCat("On error ContactsPresenter"));
//                    }, 2000);
//                }, throwable -> {
//                   // contactsFragmentView.onErrorLoading(throwable);
//                }, () -> {
//
//                });
//            });



            Observable<List<ConversationsModel>> j=   mUsersContacts.getConversation(null,2);

            mUsersContacts.getConversation(null,1).subscribe(conversationsModels -> {
                AppHelper.LogCat("conversationsModels " + conversationsModels.size());
                conversationsFragmentView.UpdateConversation(conversationsModels);
                if (isRefresh)
                    conversationsFragmentView.onHideLoading();
                else
                    conversationsFragmentView.onProgressHide();
            }, conversationsFragmentView::onErrorLoading, () -> {
                if (isRefresh)
                    conversationsFragmentView.onHideLoading();
                else
                    conversationsFragmentView.onProgressHide();
            });

//            List <ConversationsModel> conversationsModels;
//                    conversationsModels=  (List<ConversationsModel>) mUsersContacts.getConversation(1);
//                conversationsFragmentView.UpdateConversation(conversationsModels);
//                if (isRefresh)
//                    conversationsFragmentView.onHideLoading();
//                else
//                    conversationsFragmentView.onProgressHide();

//            }, conversationsFragmentView::onErrorLoading, () -> {
//                if (isRefresh)
//                    conversationsFragmentView.onHideLoading();
//                else
//                    conversationsFragmentView.onProgressHide();
//            });



        }
        catch (Exception e) {
            AppHelper.LogCat("conversation presenter " + e.getMessage());
        }


    }



    private void loadData2(boolean isRefresh) {
        if (isRefresh)
            conversationsFragmentView.onShowLoading();
        else
            conversationsFragmentView.onProgressShow();
        try {

            APIHelper.initializeApiGroups().updateGroups().subscribe(groupsModelList -> {
                AppHelper.LogCat("groupsModelList " + groupsModelList.size());
            }, throwable -> {

                AppHelper.LogCat("onerror ");

            }, () -> {
                AppHelper.LogCat("oncomplete ");

            });

            APIHelper.initializeConversationsService().getConversations().subscribe(conversationsModels -> {

                AppHelper.LogCat("conversationsModels " + conversationsModels.size());
                conversationsFragmentView.UpdateConversation(conversationsModels);
                if (isRefresh)
                    conversationsFragmentView.onHideLoading();
                else
                    conversationsFragmentView.onProgressHide();
            }, conversationsFragmentView::onErrorLoading, () -> {
                if (isRefresh)
                    conversationsFragmentView.onHideLoading();
                else
                    conversationsFragmentView.onProgressHide();
            });

        } catch (Exception e) {
            AppHelper.LogCat("conversation presenter " + e.getMessage());
        }


    }


    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(conversationsFragmentView);
        realm.close();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRefresh() {
        loadData2(true);

    }

    @Override
    public void onStop() {

    }

    public void getGroupInfo(int groupID) {
        AppHelper.LogCat("update image group profile");
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            int ConversationID = getConversationGroupId(groupsModel.getId());
            if (ConversationID != 0) {
                realm.executeTransaction(realm1 -> {
                    ConversationsModel conversationsModel = realm1.where(ConversationsModel.class).equalTo("id", ConversationID).findFirst();
                    conversationsModel.setRecipientImage(groupsModel.getGroupImage());
                    conversationsModel.setRecipientUsername(groupsModel.getGroupName());
                    realm1.copyToRealmOrUpdate(conversationsModel);
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_UPDATE_CONVERSATION_OLD_ROW, ConversationID));
                });
            }
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });
    }

    private int getConversationGroupId(int GroupID) {
        try {
            ConversationsModel conversationsModel = realm.where(ConversationsModel.class).equalTo("groupID", GroupID).findFirst();
            return conversationsModel.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception ContactFragment" + e.getMessage());
            return 0;
        }
    }

    public void getGroupInfo(int groupID, MessagesModel messagesModel) {
        AppHelper.LogCat("group id exited " + groupID);
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            conversationsFragmentView.sendGroupMessage(groupsModel, messagesModel);
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });

    }

    public void getGroupInfo(int groupID, int conversationID) {
        AppHelper.LogCat("group id created " + groupID);
        APIHelper.initializeApiGroups().getGroupInfo(groupID).subscribe(groupsModel -> {
            conversationsFragmentView.sendGroupMessage(groupsModel, conversationID);
        }, throwable -> {
            AppHelper.LogCat("Get group info conversation presenter " + throwable.getMessage());
        });
    }
}
