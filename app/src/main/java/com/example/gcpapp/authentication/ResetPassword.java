package com.example.gcpapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.LoginActivity;
import com.example.gcpapp.util.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity implements AsyncResponse {

    Button resetButton;
    private EditText etNewPassword,etConfirmPassword;
    private String mobileNumber;
    private String newPass,confirmPass;
    private TextInputLayout txtInNewPassword,txtInputConfirmPassword;
    private ProgressBar progressBar;
    private TextView strengthView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetButton = findViewById(R.id.btnReset);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNewPassword);
        txtInNewPassword = findViewById(R.id.txtInputResetNewPassword);
        txtInputConfirmPassword = findViewById(R.id.txtInputConfirmPassword);
        progressBar = (ProgressBar) findViewById(R.id.strengthProgressBar);
        strengthView = (TextView) findViewById(R.id.password_strength);

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                strengthView.setVisibility(View.VISIBLE);
                updatePasswordStrengthView(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                progressBar.setVisibility(View.INVISIBLE);
                strengthView.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

        if (Utils.getInstance(getApplicationContext()).isNetworkAvailable()) {
            if (newPass.equals(confirmPass)) {
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("txtMobile", mobileNumber);
                postData.put("txtPass", newPass);
                PostResponseAsyncTask resetPassTask =
                        new PostResponseAsyncTask(ResetPassword.this, postData, ResetPassword.this);
                resetPassTask.execute("https://mobile-app-gcp.wl.r.appspot.com/resetPassword.php");
            } else {
                Toast.makeText(getApplicationContext(), "Password Does Not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection and try again !!", Toast.LENGTH_SHORT).show();
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

    private void updatePasswordStrengthView(String password) {

        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }
        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());
        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }
}