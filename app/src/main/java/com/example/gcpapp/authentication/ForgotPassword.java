package com.example.gcpapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
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
        Intent intent =new Intent(getApplicationContext(),VerifyPhoneActivity.class);
        intent.putExtra("fMobile",userMobile);
        intent.putExtra("Forgot", "ForgotPassword");
        startActivity(intent);
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