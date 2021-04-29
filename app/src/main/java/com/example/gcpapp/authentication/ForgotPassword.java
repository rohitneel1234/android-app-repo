package com.example.gcpapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.example.gcpapp.util.Utils;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPassword extends AppCompatActivity {

    private Button btnContinue;
    private EditText etMobileNo;
    private String userMobile;
    private TextInputLayout txtInputMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnContinue = findViewById(R.id.btnContinue);
        etMobileNo = findViewById(R.id.etMobileNumber);
        txtInputMobileNumber = findViewById(R.id.txtInputMobileNumber);
        etMobileNo.setHint("+91");
        etMobileNo.setHintTextColor(getColor(R.color.white));


        etMobileNo.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (TextUtils.isEmpty(etMobileNo.getText().toString())) {
                   etMobileNo.setHint("+91");
                   etMobileNo.setHintTextColor(getColor(R.color.white));
               } else {
                   etMobileNo.setHint(null);
               }
           }
           @Override
           public void afterTextChanged(Editable s) {

           }
       });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMobile = etMobileNo.getText().toString();
                submitForm();
            }
        });
    }

    private void submitForm() {

        if (!validateInputMobileNumber()) {
            return;
        }
        if (Utils.getInstance(getApplicationContext()).isNetworkAvailable()) {
            Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
            intent.putExtra("fMobile", userMobile);
            intent.putExtra("Forgot", "ForgotPassword");
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection and try again !!", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestFocus(View view) {

        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateInputMobileNumber() {
        if (userMobile.isEmpty()) {
            txtInputMobileNumber.setError("Enter registered Mobile Number");
            requestFocus(etMobileNo);
            return false;
        } else {

            if (userMobile.length() < 10 ) {
                txtInputMobileNumber.setError("Please enter a valid Mobile Number");
                requestFocus(etMobileNo);
                return false;
            }
        }
        return true;
    }
}