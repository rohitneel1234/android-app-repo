/**
 * Author: Rohit Neel
 */
package com.example.gcpapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcpapp.R;
import com.example.gcpapp.authentication.VerifyPhoneActivity;
import com.example.gcpapp.helper.SQLiteHandler;
import com.example.gcpapp.helper.SessionManager;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;


public class RegisterActivity extends Activity implements AsyncResponse {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputMobile;
    private EditText inputcnpass;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    String upass,ucpass,userMobile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_register);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputcnpass=(EditText)findViewById(R.id.cnpassword);
        inputMobile = (EditText) findViewById(R.id.mobile);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);
        session = new SessionManager(getApplicationContext());


        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputFullName.getText().toString().isEmpty()) {
                    inputFullName.setError("Enter name");
                    inputFullName.requestFocus();
                } else if (inputMobile.getText().toString().isEmpty()) {
                    inputMobile.setError("Enter Mobile Number");
                    inputMobile.requestFocus();
                } else if (inputPassword.getText().toString().isEmpty()) {
                    inputPassword.setError("Enter Password of atleast 6 characters");
                    inputPassword.requestFocus();

                }
                else if (inputEmail.getText().toString().isEmpty()) {
                    inputEmail.setError("Enter Email ID");
                    inputEmail.requestFocus();
                }
                else {

                    upass = inputPassword.getText().toString();
                    ucpass = inputcnpass.getText().toString();
                    userMobile = inputMobile.getText().toString();

                    if ((upass.equals(ucpass))) {

                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("txtUname", inputFullName.getText().toString());
                        postData.put("txtUpass", inputPassword.getText().toString());
                        postData.put("txtUemail", inputEmail.getText().toString());
                        postData.put("txtUmobile", inputMobile.getText().toString());
                        PostResponseAsyncTask loginTask =
                                new PostResponseAsyncTask(RegisterActivity.this, postData, RegisterActivity.this);
                        loginTask.execute("https://vast-service-281617.uc.r.appspot.com/registration.php");

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Password Does Not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void processFinish(String s) {

        if(s.equals("success"))
        {
           // Toast.makeText(getApplicationContext(),"Account Created Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
            intent.putExtra("mobile", userMobile);
            intent.putExtra("Register","RegisterActivity");
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),s+"User Already Exist", Toast.LENGTH_SHORT).show();
        }

    }
}

