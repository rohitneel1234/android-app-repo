package com.example.gcpapp.download;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gcpapp.R;

public class FullView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        ImageView imageView = findViewById(R.id.img_full);

        String img_id = getIntent().getStringExtra("img_id");

        Glide.with(getApplicationContext())
                .load(img_id)
                .fitCenter()
                .into(imageView);
    }
}