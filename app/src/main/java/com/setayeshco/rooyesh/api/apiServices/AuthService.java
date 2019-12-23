package com.setayeshco.rooyesh.api.apiServices;

import android.content.Context;

import com.setayeshco.rooyesh.api.APIAuthentication;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.models.auth.JoinModelResponse;
import com.setayeshco.rooyesh.models.auth.LoginModel;
import com.setayeshco.rooyesh.models.auth.VersionApp;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel2;
import com.setayeshco.rooyesh.models.messages.ListConversation;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

/**
 * Created by Abderrahim El imame on 10/4/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class AuthService {

    private APIAuthentication apiAuthentication;
    private Context mContext;
    private APIService mApiService;


    public AuthService(Context context, APIService mApiService) {
        this.mContext = context;
        this.mApiService = mApiService;
    }

    /**
     * method to initialize the api auth
     *
     * @return return value
     */
    private APIAuthentication initializeApiAuth() {
        if (apiAuthentication == null) {
            apiAuthentication = this.mApiService.AuthService(APIAuthentication.class, EndPoints.BACKEND_BASE_URL);
        }
        return apiAuthentication;
    }

    public Observable<JoinModelResponse> join(String std_number, String pass) {
        return initializeApiAuth().join(std_number,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<JoinModelResponse> resend(String phone) {
        return initializeApiAuth().resend(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<JoinModelResponse> verifyUser(String code) {
        return initializeApiAuth().verifyUser(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

   public Call<VersionApp> versionAppObservable() {
        return initializeApiAuth().checkVersion();
    }

    public Call<ListConversation> conversation() {
        return initializeApiAuth().conversation2(1);
    }

    public Call<List<ContactsModel>> contact() {
        return initializeApiAuth().contacts();
    }

    public Call<ConversationsModel2> test() {
        return initializeApiAuth().test();
    }


    public Observable<ConversationsModel2> test2() {
        return initializeApiAuth().test2();
    }

}
