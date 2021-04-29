package com.example.gcpapp.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ly.img.android.ImgLySdk;

public class ProjectApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ProjectApplication.context = getApplicationContext();
        ImgLySdk.init(this);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static Context getAppContext() {
        return ProjectApplication.context;
    }

}
