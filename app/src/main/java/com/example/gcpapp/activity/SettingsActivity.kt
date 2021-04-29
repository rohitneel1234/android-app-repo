package com.example.gcpapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gcpapp.R
import com.example.gcpapp.helper.SessionManager
import com.example.gcpapp.tutorials.TutorialActivity


class SettingsActivity : AppCompatActivity() {
    private var mSwitchCompat: SwitchCompat? = null
    private var mSession: SessionManager? = null
    private var constraintLayout: ConstraintLayout? = null
    private var mSwitchFullScreen: SwitchCompat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mSession = SessionManager(applicationContext)
        mSwitchCompat = findViewById<View>(R.id.switchTheme) as SwitchCompat
        mSwitchFullScreen = findViewById<View>(R.id.switchFullScreen) as SwitchCompat
        constraintLayout = findViewById<ConstraintLayout>(R.id.clTutorial)
        constraintLayout!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, TutorialActivity::class.java))
        })

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

        mSwitchFullScreen!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                this.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

    }
}