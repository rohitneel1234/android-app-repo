package com.example.gcpapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity implements AsyncResponse {

    Button resetButton;
    private EditText etNewPassword,etConfirmPassword;
    private String mobileNumber;
    private String newPass,confirmPass;
    private TextInputLayout txtInNewPassword,txtInputConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetButton = findViewById(R.id.btnReset);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNewPassword);
        txtInNewPassword = findViewById(R.id.txtInputResetNewPassword);
        txtInputConfirmPassword = findViewById(R.id.txtInputConfirmPassword);

        Intent intent =getIntent();
        mobileNumber = intent.getStringExtra("mobileNo");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPass = etNewPassword.getText().toString();
                confirmPass = etConfirmPassword.getText().toString();
                submitForm();
            }
        });
    }

    private void submitForm() {

        if (!validateNewPassword()) {
            return;
        }

        if (!validateConfirmPassword()) {
            return;
        }

        if(newPass.equals(confirmPass)) {
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("txtMobile", mobileNumber);
            postData.put("txtPass", newPass);
            PostResponseAsyncTask resetPassTask =
                    new PostResponseAsyncTask(ResetPassword.this, postData, ResetPassword.this);
            resetPassTask.execute("https://vast-service-281617.uc.r.appspot.com/resetPassword.php");
        }
        else {
            Toast.makeText(getApplicationContext(), "Password Does Not match", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void processFinish(String s) {

        if(s.equals("success")) {
            Toast.makeText(getApplicationContext(),"Password has been reset successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
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

    private boolean validateNewPassword() {

        if (newPass.isEmpty()) {
            txtInNewPassword.setError("Enter New Password");
            requestFocus(etNewPassword);
            return false;
        } else {

            if (newPass.length() < 6) {
                txtInNewPassword.setError("Minimum 6 characters");
                requestFocus(etNewPassword);
                return false;
            }
        }
        return true;
    }

    private boolean validateConfirmPassword() {

        if (!(newPass.equals(confirmPass))) {
            txtInputConfirmPassword.setError("New password does not match");
            requestFocus(etConfirmPassword);
            return false;
        }
        return true;
    }
}