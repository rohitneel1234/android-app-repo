package com.example.gcpapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity implements AsyncResponse {

    private Button btnSave;
    private ImageView ivChangePassBack;
    private EditText etCurrentPassword,etNewPassword,etReNewPassword;
    private String currPass,newPass,reNewPass, userMobile;
    private TextInputLayout txtInCurrPass,txtInNewPass,txtInReNewPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnSave = findViewById(R.id.btn_submit);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etCNewPassword);
        etReNewPassword = findViewById(R.id.etReNewPassword);
        txtInCurrPass = findViewById(R.id.txtInputCurrentPassword);
        txtInNewPass = findViewById(R.id.txtInputNewPassword);
        txtInReNewPass = findViewById(R.id.txtInputReNewPassword);

        ivChangePassBack =  findViewById(R.id.changePassBack);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
             userMobile = extras.getString("mobile");
        }


        ivChangePassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currPass = etCurrentPassword.getText().toString();
                newPass = etNewPassword.getText().toString();
                reNewPass = etReNewPassword.getText().toString();
                submitForm();
            }
        });

    }

    private void submitForm() {

        if (!validateCurrentPassword()) {
            return;
        }

        if (!validateNewPassword()) {
            return;
        }

        if (!validateReNewPassword()) {
            return;
        }

        if (newPass.equals(reNewPass)) {

            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("txtMobile", userMobile);
            postData.put("txtPass", newPass);
            PostResponseAsyncTask resetPassTask =
                    new PostResponseAsyncTask(ChangePassword.this, postData, ChangePassword.this);
            resetPassTask.execute("https://vast-service-281617.uc.r.appspot.com/resetPassword.php");
        } else {
            Toast.makeText(getApplicationContext(), "Password Does Not match", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String s) {

        if(s.equals("success")) {
            Toast.makeText(getApplicationContext(),"Password Changed Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),s+" failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void requestFocus(View view) {

        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateCurrentPassword() {

        if (currPass.isEmpty()) {
            txtInCurrPass.setError("Enter Current Password");
            requestFocus(etCurrentPassword);
            return false;
        } else {

            if (currPass.length() < 6) {
                txtInCurrPass.setError("Minimum 6 characters");
                requestFocus(etCurrentPassword);
                return false;
            }
        }
        return true;
    }

    private boolean validateNewPassword() {

        if (newPass.isEmpty()) {
            txtInNewPass.setError("Enter New Password");
            requestFocus(etNewPassword);
            return false;
        } else {

            if (newPass.length() < 6) {
                txtInNewPass.setError("Minimum 6 characters");
                requestFocus(etNewPassword);
                return false;
            }
        }
        return true;
    }

    private boolean validateReNewPassword() {

        if (!(newPass.equals(reNewPass))) {
            txtInReNewPass.setError("New password does not match");
            requestFocus(etReNewPassword);
            return false;
        }
        return true;
    }


}