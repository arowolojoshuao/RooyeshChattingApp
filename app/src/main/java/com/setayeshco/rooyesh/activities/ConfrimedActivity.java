package com.setayeshco.rooyesh.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.welcome.WelcomeActivity;
import com.setayeshco.rooyesh.api.APIGroups;
import com.setayeshco.rooyesh.api.APIHelper;
import com.setayeshco.rooyesh.api.APIService;
import com.setayeshco.rooyesh.api.apiServices.UsersService;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.PreferenceManager;
import com.setayeshco.rooyesh.models.groups.GroupPresent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vahid on 05/03/2018.
 */

public class ConfrimedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confrimed_layout);



            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.is_confrimed_layout, (ViewGroup) findViewById(R.id.root));
        TextView txt2 = layout.findViewById(R.id.txtConfrimed2);
        TextView txt1 = layout.findViewById(R.id.txtConfrimed1);
        txt1.setTypeface(AppHelper.setTypeFace(this, "IranSans"));
        txt2.setTypeface(AppHelper.setTypeFace(this, "IranSans"));

            builder.setView(layout);
            builder.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                 //   startActivity(new Intent(ConfrimedActivity.this,WelcomeActivity.class));
                   // ConfrimedActivity.this.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });



            builder.setCancelable(false);
            builder.create().show();



    }



    void IsPresntSearch(String groupID, int date ){
        APIHelper mApiService=new APIHelper();
        UsersService mApiGroups = mApiService.initialApiUsersContacts();
        Call<List<GroupPresent>> PresentCall = (Call<List<GroupPresent>>) mApiGroups.getContact(30);
        PresentCall.enqueue(new Callback<List<GroupPresent>>() {
            @Override
            public void onResponse(Call<List<GroupPresent>> call, Response<List<GroupPresent>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("PRESENT",response.body().toString());

                    }
                }
            }



            @Override
            public void onFailure(Call<List<GroupPresent>> call, Throwable t)
            {
                //    AppHelper.Snackbar(PresentActivity.this, findViewById(R.id.containerProfile), getString(R.string.failed_to_is_present), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
                Toast.makeText(ConfrimedActivity.this,""+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }



}
