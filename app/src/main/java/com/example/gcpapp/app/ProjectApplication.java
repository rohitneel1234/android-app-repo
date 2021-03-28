package com.example.gcpapp.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ProjectApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ProjectApplication.context = getApplicationContext();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static Context getAppContext() {
        return ProjectApplication.context;
    }

}
