package com.setayeshco.rooyesh.api;

import com.setayeshco.rooyesh.api.apiServices.AuthService;
import com.setayeshco.rooyesh.api.apiServices.ConversationsService;
import com.setayeshco.rooyesh.api.apiServices.GroupsService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.RooyeshApplication;

/**
 * Created by Abderrahim El imame on 4/11/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class APIHelper {

    public static UsersService initialApiUsersContacts() {
        APIService mApiService = APIService.with(RooyeshApplication.getInstance());
        return new UsersService(RooyeshApplication.getRealmDatabaseInstance(), RooyeshApplication.getInstance(), mApiService);
    }


    public static GroupsService initializeApiGroups() {
        APIService mApiService = APIService.with(RooyeshApplication.getInstance());
        return new GroupsService(RooyeshApplication.getRealmDatabaseInstance(), RooyeshApplication.getInstance(), mApiService);
    }

    public static ConversationsService initializeConversationsService() {
        return new ConversationsService(RooyeshApplication.getRealmDatabaseInstance());
    }

    public static AuthService initializeAuthService() {
        APIService mApiService = APIService.with(RooyeshApplication.getInstance());
        return new AuthService(RooyeshApplication.getInstance(), mApiService);
    }
}
