package com.example.gcpapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gcpapp.R;
import com.example.gcpapp.api.APIService;
import com.example.gcpapp.models.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserProfileInfo extends AppCompatActivity {

    private TextView textView, userNameToolbar;
    private EditText etMobileNo,etUserName,etEmail;
    private String nameLetter,userName, userMobile,userEmail;
    private ImageView imgAccountBack;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_info);

        textView = findViewById(R.id.textUserLetter);
        etUserName = findViewById(R.id.etName);
        etMobileNo = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        imgAccountBack = findViewById(R.id.accountBack);
        saveBtn = findViewById(R.id.saveButton);
        userNameToolbar = findViewById(R.id.userNameToolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameLetter = extras.getString("nameLetter");
            userName = extras.getString("username");
            userMobile = extras.getString("mobile");
            userEmail = extras.getString("email");
        }
        textView.setText(nameLetter);
        etUserName.setText(userName);
        etMobileNo.setText(userMobile);
        etEmail.setText(userEmail);
        userNameToolbar.setText(userName);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtName = etUserName.getText().toString().trim();
                String txtEmail = etEmail.getText().toString().trim();
                String txtMobile = etMobileNo.getText().toString().trim();

                if (txtName.isEmpty()) {
                    etUserName.setError("Profile name can't be empty");
                    etUserName.requestFocus();
                } else if (txtMobile.isEmpty()) {
                    etMobileNo.setError("Mobile number can't be empty");
                    etMobileNo.requestFocus();
                } else if (txtEmail.isEmpty()) {
                    etEmail.setError("Email can't be empty");
                    etEmail.requestFocus();
                }  else {

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APIService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    APIService service = retrofit.create(APIService.class);

                    Call<Result> call = service.updateUser(
                            txtName,
                            txtEmail,
                            txtMobile
                    );

                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Result result = response.body();
                            Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),AccountActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "error:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        imgAccountBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackAccountInformation();
            }
        });

    }

    private void sendBackAccountInformation() {

        Intent intent = new Intent();
        intent.putExtra("name",userName);
        setResult(RESULT_OK,intent);
        finish();
    }
}
