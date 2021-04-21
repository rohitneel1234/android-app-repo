package com.example.gcpapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.gcpapp.R
import com.example.gcpapp.helper.SessionManager


class SettingsActivity : AppCompatActivity() {
    private var mSwitchCompat: SwitchCompat? = null
    private var mSession: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mSession = SessionManager(applicationContext)
        mSwitchCompat = findViewById<View>(R.id.switchTheme) as SwitchCompat

        if (mSession!!.loadState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.darkTheme)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.AppTheme)
        }

        if (mSession!!.loadState()) {
            mSwitchCompat!!.isChecked = true
        }

        mSwitchCompat!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mSession!!.saveState(true)
                recreate()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
               mSession!!.saveState(false)
            }
        }

    }
}