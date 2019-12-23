package com.setayeshco.rooyesh.api;


import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.models.NetworkModel;
import com.setayeshco.rooyesh.models.auth.JoinModelResponse;
import com.setayeshco.rooyesh.models.auth.LoginModel;
import com.setayeshco.rooyesh.models.auth.VersionApp;
import com.setayeshco.rooyesh.models.messages.ConversationsModel;
import com.setayeshco.rooyesh.models.messages.ConversationsModel2;
import com.setayeshco.rooyesh.models.messages.ListConversation;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Abderrahim El imame on 01/11/2015.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */
public interface APIAuthentication {
    /**
     * method to join
     *
     * @param loginModel this is parameter for join method
     */

//    @POST(EndPoints.JOIN)
//    Observable<JoinModelResponse> join(@Body LoginModel loginModel);

    @FormUrlEncoded
    @POST(EndPoints.JOIN)
    Observable<JoinModelResponse> join(@Field("std_number") String std_number, @Field("pass") String pass);

    /**
     * method to resend SMS request
     *
     * @param phone this is parameter for resend method
     */

    @FormUrlEncoded
    @POST(EndPoints.RESEND_REQUEST_SMS)
    Observable<JoinModelResponse> resend(@Field("phone") String phone);

    /**
     * method to verify the user code
     *
     * @param code this is parameter for verifyUser method
     * @return this is what method will return
     */
    @FormUrlEncoded
    @POST(EndPoints.VERIFY_USER)
    Observable<JoinModelResponse> verifyUser(@Field("code") String code);


    @GET(EndPoints.CHECK_VERSION)
    Call<VersionApp> checkVersion();

    @FormUrlEncoded
    @POST(EndPoints.GET_CONVERSATION)
    Call<ListConversation> conversation2(
            @Field("userID") int userID

    );

    @POST(EndPoints.SEND_CONTACTS)
    Call <List<ContactsModel>> contacts();


    @GET(EndPoints.SEND_TEST)
    Call <ConversationsModel2> test();

    @GET(EndPoints.SEND_TEST)
    Observable <ConversationsModel2> test2();
}
