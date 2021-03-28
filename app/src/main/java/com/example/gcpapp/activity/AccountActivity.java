package com.example.gcpapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.example.gcpapp.authentication.ChangePassword;

public class AccountActivity extends AppCompatActivity {

    private String nameLetter,userName, userMobile,userEmail,userPassword;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;
    private TextView tvNameLetter,tvAccountName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvNameLetter = findViewById(R.id.tvAccountLetter);
        tvAccountName = findViewById(R.id.tv_name);
        ImageView imgBack = findViewById(R.id.accountBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        RelativeLayout profileLayout = findViewById(R.id.manage_profile_layout);
        RelativeLayout changePassLayout = findViewById(R.id.change_password_layout);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            nameLetter = extras.getString("nameLetter");
            userName = extras.getString("username");
            userMobile = extras.getString("mobile");
            userEmail = extras.getString("email");
            userPassword = extras.getString("password");
        }

        tvNameLetter.setText(nameLetter);
        tvAccountName.setText(userName);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserProfileInformation();
            }
        });

        changePassLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                intent.putExtra("mobile",userMobile);
                startActivity(intent);
            }
        });
    }


    private void setUserProfileInformation() {

        Intent intent = new Intent(getApplicationContext(),UserProfileInfo.class);
        intent.putExtra("nameLetter", nameLetter);
        intent.putExtra("username",userName);
        intent.putExtra("password",userPassword);
        intent.putExtra("mobile",userMobile);
        intent.putExtra("email",userEmail);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                String returnString = data.getStringExtra("name");
                // Set text view with string
                tvAccountName.setText(returnString);
            }
        }
    }
}