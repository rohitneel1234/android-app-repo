package com.example.gcpapp.authentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    //The edittext to input the code
    private EditText editTextCode1,editTextCode2,editTextCode3,editTextCode4,editTextCode5,editTextCode6, etOTP;
    private TextView  textView;
    private TextView txtTimer;
    private TextView editClick;
    //firebase auth object
    private FirebaseAuth mAuth;
    private String verificationCode;
    private String otp;
    private String rMobile,fMobile;
    private String activityName = "RegisterActivity", registerActivity = "ForgotPassword", forgotPassword;
    private Button buttonSignIn;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0d0620")));

        requestSMSPermission();

        mAuth = FirebaseAuth.getInstance();
        editTextCode1 = findViewById(R.id.et_otp_1);
        editTextCode2 = findViewById(R.id.et_otp_2);
        editTextCode3 = findViewById(R.id.et_otp_3);
        editTextCode4 = findViewById(R.id.et_otp_4);
        editTextCode5 = findViewById(R.id.et_otp_5);
        editTextCode6 = findViewById(R.id.et_otp_6);
        textView = findViewById(R.id.txtMsg);
        editClick = findViewById(R.id.txtClick);
        txtTimer = findViewById(R.id.timer);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        new OTPReceiver().setEditText(editTextCode1,editTextCode2,editTextCode3,editTextCode4,editTextCode5,editTextCode6);

        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        registerActivity = intent.getStringExtra("Register");
        forgotPassword = intent.getStringExtra("Forgot");
        rMobile = intent.getStringExtra("mobile");
        fMobile = intent.getStringExtra("fMobile");
        StartFirebaseLogin();

        if(activityName.equals(registerActivity)) {
            sendVerificationCode(rMobile);
        }
        else {
            sendVerificationCode(fMobile);
        }

        editTextCursorChangeListener();
        
        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = editTextCode1.getText().toString().trim() + editTextCode2.getText().toString().trim() +editTextCode3.getText().toString().trim() + editTextCode4.getText().toString().trim()+editTextCode5.getText().toString().trim() + editTextCode6.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                SignInWithPhone(credential);
            }
        });

        editClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
            }
        });
    }

    private void requestSMSPermission() {

        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list,1);
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallback);
    }

    private void countDown() {
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Waiting for OTP 00:" + millisUntilFinished / 1000);
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                txtTimer.setText(Html.fromHtml("<u>Resend OTP</u>"));
            }
        }.start();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallback,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void SignInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(activityName.equals(registerActivity)) {
                                Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Mobile Number Verified Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                                intent.putExtra("mobileNo",fMobile);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void StartFirebaseLogin() {

        mAuth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getApplicationContext(), "Verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(getApplicationContext(), e+"Verification failed", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(String s, final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                if(activityName.equals(registerActivity)) {
                    countDown();
                    textView.setText("An OTP has been sent on "+rMobile);
                    txtTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resendVerificationCode(rMobile,forceResendingToken);
                        }
                    });
                }
                else {
                    countDown();
                    textView.setText("An OTP has been sent on "+fMobile);
                    txtTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resendVerificationCode(fMobile,forceResendingToken);
                        }
                    });
                }
            }
        };
    }

    private void editTextCursorChangeListener() {

        final StringBuilder sb = new StringBuilder();

        editTextCode1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editTextCode1.length() == 1) {
                    sb.append(s);
                    editTextCode1.clearFocus();
                    editTextCode2.requestFocus();
                    editTextCode2.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editTextCode1.requestFocus();
                }
            }
        });
        editTextCode2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editTextCode2.length() == 1) {
                    sb.append(s);
                    editTextCode2.clearFocus();
                    editTextCode3.requestFocus();
                    editTextCode3.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editTextCode2.requestFocus();
                }
            }
        });
        editTextCode3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editTextCode3.length() == 1) {
                    sb.append(s);
                    editTextCode3.clearFocus();
                    editTextCode4.requestFocus();
                    editTextCode4.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editTextCode3.requestFocus();
                }
            }
        });
        editTextCode4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editTextCode3.length() == 1) {
                    sb.append(s);
                    editTextCode4.clearFocus();
                    editTextCode5.requestFocus();
                    editTextCode5.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editTextCode4.requestFocus();
                }
            }
        });
        editTextCode5.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editTextCode3.length() == 1) {
                    sb.append(s);
                    editTextCode5.clearFocus();
                    editTextCode6.requestFocus();
                    editTextCode6.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editTextCode5.requestFocus();
                }
            }
        });
    }

}

