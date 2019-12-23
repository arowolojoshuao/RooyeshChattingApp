package com.setayeshco.rooyesh.activities.main.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.helpers.PreferenceManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vahid on 01/03/2018.
 */

public class PasswordActivity  extends AppCompatActivity {

  //  private EditText emailEditText;
    private EditText passEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   passActivity1");
        PreferenceManager.setIsLogin(PasswordActivity.this,true);
        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        PasswordActivity.this.finish();
        //..........................................

        /*Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   passActivity1");
        PreferenceManager.setIsLogin(PasswordActivity.this,true);
        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();*/


        //..........................................



     //   emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);

    }



    public void checkLogin(View arg0) {

    /*    final String email = emailEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailEditText.setError("Invalid Email");
        }*/

        String pass = passEditText.getText().toString();
        pass = md5(pass);
        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("پسورد مورد نظر را وارد کنید !");
        }

      //  if(isValidEmail(email) && isValidPassword(pass))
        if(isValidPassword(pass))

        {
            // Validation Completed
            Log.d("PASSWORDLOG","pass >>>>>>>>>>>>>>  " + pass);
            Log.d("PASSWORDLOG","getPassword >>>>>>>>>>>>>>  " + PreferenceManager.getPassword(PasswordActivity.this));
            if (pass.equals(PreferenceManager.getPassword(PasswordActivity.this))) {

                Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   passActivity1");
                PreferenceManager.setIsLogin(PasswordActivity.this,true);
                Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                PasswordActivity.this.finish();

            }else {
                Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   pass  "+pass);
                Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   PreferenceManager.getPassword(PasswordActivity.this)"+PreferenceManager.getPassword(PasswordActivity.this));
                Log.d("PASSWORDLOG",">>>>>>>>>>>>>>   passActivity2");
                PreferenceManager.setIsLogin(PasswordActivity.this,false);
                Intent intent = new Intent(PasswordActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                PasswordActivity.this.finish();
            }

        }
    }

    // validating email id
  /*  private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }*/

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }



    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
