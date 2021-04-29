package com.example.gcpapp.app;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.LoginActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons

        //addSlide(secondFragment);
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("PHOTO EDITOR APP",
                "Photo Editor App is the complete toolkit you need to make each picture look beautiful. Comes with tools for cropping, trimming, stretching, cloning, and adding text!",
                R.drawable.photeditor, Color.parseColor("#FFA726")));
        /*addSlide(AppIntroFragment.newInstance(
                "DOYASIYA EĞLEN !",
                "Fotoğraflarını İster Caps Yap ve Galerine Kaydet İster Diğer Kullanıcılar ile Paylaş !",
                R.drawable.imgshare, Color.parseColor("#FB8C00")));*/
        addSlide(AppIntroFragment.newInstance("UPLOAD VIDEOS",
                "Upload videos on cloud through gallery and file downloads and view them on list view",R.drawable.upload_videos,Color.parseColor("#FB8C00")));
        addSlide(AppIntroFragment.newInstance("NOW IT'S YOUR TURN",
                "You can take pictures or choose from gallery and apply effects using editor tools where\n" +
                        " it offers a massive of popular photo filters",
                R.drawable.photographerimg, Color.parseColor("#EF6C00")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.TRANSPARENT);
        setSeparatorColor(Color.parseColor("#ee7600"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
