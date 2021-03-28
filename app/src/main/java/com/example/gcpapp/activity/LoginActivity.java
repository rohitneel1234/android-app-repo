/**
 * Author: Rohit Neel
 */

package com.example.gcpapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.gcpapp.authentication.ForgotPassword;
import com.example.gcpapp.helper.SessionManager;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class LoginActivity extends Activity implements AsyncResponse {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnLogin;
    private Button forgotPassword;
    private TextView btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private String useremail, userpass,Mobile_Number;
    private int wantToExit=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        forgotPassword=(Button)findViewById(R.id.btn_reset_password);
        btnLinkToRegister = (TextView) findViewById(R.id.btnLinkToRegisterScreen);
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isUserLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();

                String password = inputPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    inputEmail.setError("Enter Email address");
                    inputEmail.requestFocus();
                } else if (inputPassword.getText().toString().isEmpty()) {
                    inputPassword.setError("Enter Password");
                    inputPassword.requestFocus();
                }
                useremail = inputEmail.getText().toString();
                userpass = inputPassword.getText().toString();

                if(useremail.length() > 0 && userpass.length() >=6)
                {
                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("txtUemail", inputEmail.getText().toString());
                    postData.put("txtUpass", inputPassword.getText().toString());
                    PostResponseAsyncTask loginTask =
                            new PostResponseAsyncTask(LoginActivity.this, postData, LoginActivity.this);
                    loginTask.execute("https://vast-service-281617.uc.r.appspot.com/login.php");
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

    }

    @Override
    public void processFinish(String s) {

        if(s.equals("success")) {
            session.createUserLoginSession(useremail);
            Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

    public void forgotPassword(View view) {
        forgotpasswordUser();
    }

    private void forgotpasswordUser() {
        useremail = inputEmail.getText().toString();
        inputEmail.setError("User name cannot empty");
        inputEmail.setBackground(getDrawable(R.drawable.text_border_error));

    }
    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(wantToExit==1)
        {
            moveTaskToBack(true);
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(0);
        }
        else {
            Toast.makeText(getApplicationContext(), "Press again to exit.", Toast.LENGTH_SHORT).show();
        }
        wantToExit++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wantToExit=0;
            }
        }).start();
    }

}

