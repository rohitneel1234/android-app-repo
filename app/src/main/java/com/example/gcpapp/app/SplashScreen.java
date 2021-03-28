package com.example.gcpapp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.LoginActivity;
import com.rbddevs.splashy.Splashy;

import static com.rbddevs.splashy.Splashy.Animation.GROW_LOGO_FROM_CENTER;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setSplashy();
    }

    private void setSplashy() {
        Splashy splashy = new Splashy(this);
        splashy.setLogo(R.drawable.app_logo_circle);
        splashy.setAnimation(GROW_LOGO_FROM_CENTER,1000);
        splashy.setBackgroundResource(R.color.splashColor);
        splashy.setTitleColor(R.color.white);
        splashy.setProgressColor(R.color.white);
        splashy.setTitle("MediaStore");
        splashy.setTitleSize(20);
        splashy.setSubTitle("Made Easy using cloud storage app");
        splashy.setSubTitleColor(R.color.white);
        splashy.setFullScreen(true);
        splashy.setSubTitleFontStyle("fonts/satisfy_regular.ttf");
        splashy.setClickToHide(false);
        splashy.setDuration(2000);
        splashy.show();

        Splashy.Companion.onComplete(new Splashy.OnComplete() {
            @Override
            public void onComplete() {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}