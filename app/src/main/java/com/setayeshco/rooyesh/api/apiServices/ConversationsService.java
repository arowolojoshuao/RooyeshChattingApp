package com.setayeshco.rooyesh.api.apiServices;

import android.app.Activity;

import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.MessagesModel;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by Abderrahim El imame on 20/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ConversationsService {
    private Realm realm;
    Activity act;

    public ConversationsService(Realm realm) {
        this.realm = realm;


    }

    /**
     * method to get Conversations list
     *
     * @return return value
     */
    public Observable<RealmResults<ConversationsModel>> getConversations() {
      int  senderId = PreferenceManager.getID(MainActivity.Act);

     //   RealmResults<ConversationsModel> conversationsModels2 = realm.where(ConversationsModel.class).findAll();

        RealmResults<ConversationsModel> conversationsModels = realm.where(ConversationsModel.class).findAllSorted("LastMessageId", Sort.DESCENDING);
      //  conversationsModels.get(0).getMessages().removeIf(messagesModel -> messagesModel.getSenderID()!= senderId);
     //   conversationsModels2.clear();
//        for(int i=0 ; i<conversationsModels.size();i++)
//        {
//            if(conversationsModels.get(i).getMessages().get(0).getSenderID()!=3)
//            {
//                conversationsModels.remove(i);
//            }
//        }


        RealmQuery<ConversationsModel> query = realm.where(ConversationsModel.class);
// categoryList is a list of the categories
//        for(int catId : categoryList) {
//            query = query.or().equalTo("categories.id", catId);
//        }
//        RealmResults<Product> results = query.findAll();
//
//        for (int i=0;i<conversationsModels.size();i++) {
//            int finalI = i;
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    // remove single match
//                   // results.deleteFirstFromRealm();
//                  //  results.deleteLastFromRealm();
//
//                    // remove a single object
//                    ConversationsModel model = conversationsModels.get(finalI);
//                    if(model.getMessages().get(0).getSenderID()!=senderId)
//                        model.deleteFromRealm();
//                    conversationsModels.removeIf(conversationsModel -> conversationsModel.getMessages().get(0).getSenderID()!=senderId);
//
//                    // Delete all matches
//
//                }
//            });
//        }


//        for (ConversationsModel conversationsModel : conversationsModels) {
//          //  EventBus.getDefault().post(new Pusher(EVENT_BUS_DELETE_CONVERSATION_ITEM, conversationsModel.getId()));
//            RealmResults<MessagesModel> messagesModel1 = realm.where(MessagesModel.class).equalTo("senderID", senderId).findAll();
//            messagesModel1.deleteAllFromRealm();
//            conversationsModel.deleteFromRealm();
//        }

        return Observable.just(conversationsModels).filter(RealmResults::isLoaded);

       // RealmList<ConversationsModel> g= Observable.just(conversationsModels).filter(RealmResults::isLoaded);
    }




}
