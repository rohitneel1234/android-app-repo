package com.example.gcpapp.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.gcpapp.R
import com.example.gcpapp.download.DownloadMedia
import com.example.gcpapp.helper.SessionManager
import com.example.gcpapp.upload.MediaActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.kosalgeek.asynctask.AsyncResponse
import com.kosalgeek.asynctask.PostResponseAsyncTask
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity(), AsyncResponse {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var mSession: SessionManager? = null
    var toolbar: Toolbar? = null
    private val wantToExit = 0
    private var userNameLetter: String? = null
    private var headerTextTitle: TextView? = null
    private var UserName: String? = null
    private var MobileNo: String? = null
    private var Password: String? = null
    private var txtEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        mSession = SessionManager(applicationContext)

        if (mSession!!.loadState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (!mSession!!.isUserLoggedIn) {
            logoutUser()
        }
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        headerTextTitle = navigationView.getHeaderView(0).findViewById<View>(R.id.txtHeader) as TextView
        navigationView.menu.findItem(R.id.nav_rate_us).setOnMenuItemClickListener {
            showRateUs(this@MainActivity)
            true
        }
        val user = mSession!!.userDetails
        txtEmail = user[SessionManager.KEY_NAME]
        val txtProfileName = navigationView.getHeaderView(0).findViewById<View>(R.id.textView) as TextView
        txtProfileName.text = Html.fromHtml("\t" + txtEmail)
        if (txtEmail != null) {
            val name = txtEmail!![0]
            userNameLetter = Character.toString(name)
            headerTextTitle!!.text = userNameLetter
        }
        headerTextTitle!!.setOnClickListener {
            val postData = HashMap<String, String?>()
            postData["txtEmail"] = txtEmail
            val emailTask = PostResponseAsyncTask(this@MainActivity, postData, this@MainActivity)
            emailTask.execute("https://mobile-app-gcp.wl.r.appspot.com/getUserInformation.php")
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.bottom_home, R.id.bottom_dashboard, R.id.bottom_notifications)
                .setDrawerLayout(drawer)
                .build()
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupWithNavController(bottomNavigationView,navController)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_upload -> startActivity(Intent(applicationContext, MediaActivity::class.java))
                R.id.nav_download -> startActivity(Intent(applicationContext, DownloadMedia::class.java))
                R.id.nav_settings -> startActivity(Intent(applicationContext, SettingsActivity::class.java))
                R.id.nav_about -> startActivity(Intent(applicationContext, AboutActivity::class.java))
                R.id.nav_logout -> logoutUser()
                R.id.nav_exit -> {
                    moveTaskToBack(true)
                    finish()
                    val i = Intent(Intent.ACTION_MAIN)
                    i.addCategory(Intent.CATEGORY_HOME)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }
            }
            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        /*
        // Navcontroller for fragments navigation
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                int id = destination.getId();

                if (id == R.id.nav_download) {
                    Intent intent = new Intent(getApplicationContext(),DownloadMedia.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_settings) {
                    Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_about) {
                    Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
                    startActivity(intent);
                }
            }
        });*/

     /*   val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            var fragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    fragment = HomeFragment()
                }
                R.id.bottom_dashboard -> {
                    fragment = ChangeBackground()
                }
                R.id.bottom_notifications -> {
                    fragment = TriggerChange()
                }
            }
            loadFragment(fragment)
        }*/
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(Html.fromHtml("<font color='#ffffff'>GCP Media App</font>"))
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to exit?</font>"))
                .setCancelable(false)
                .setIcon(R.drawable.exitpopup)
                .setPositiveButton("Ok") { dialog, which -> super@MainActivity.onBackPressed() }
                .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.btn_login)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    private fun logoutUser() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to log out?</font>"))
                .setCancelable(false)
                .setNegativeButton(Html.fromHtml("<font color='#ffffff'>Logout</font>")) { dialog, which ->
                    mSession!!.logoutUser()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
                .setPositiveButton(Html.fromHtml("<font color='#ffffff'>Cancel</font>")) { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.dialogBox)))
    }

    private fun showRateUs(context: Context?) {
        val open1 = Dialog(context!!)
        open1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val popup = layoutInflater.inflate(R.layout.rate_us, null)
        val happy = popup.findViewById<LinearLayout>(R.id.happy)
        val bad = popup.findViewById<LinearLayout>(R.id.bad)
        open1.setContentView(popup)
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        open1.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        open1.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        val lparam = WindowManager.LayoutParams()
        lparam.copyFrom(open1.window!!.attributes)
        open1.window!!.setLayout((displaymetrics.widthPixels - 40), ActionBar.LayoutParams.WRAP_CONTENT)
        open1.show()
        bad.setOnClickListener { // TODO Auto-generated method stub
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "plain/text"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rohitneel007@gmail.com"))
                startActivity(Intent.createChooser(intent, ""))
            } catch (e: NullPointerException) {
                Toast.makeText(applicationContext, "No application can perform this operation", Toast.LENGTH_SHORT).show()
            }
        }
        happy.setOnClickListener { // TODO Auto-generated method stub
            val browserIntent = Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=co.rohitneel.gcpmedia"))
            startActivity(browserIntent)
        }
    }

    override fun processFinish(s: String) {
        try {
            val jsonArray = JSONArray(s)
            for (i in 0 until jsonArray.length()) {
                val jsonobject = jsonArray.getJSONObject(i)
                UserName = jsonobject.getString("name")
                Password = jsonobject.getString("password")
                MobileNo = jsonobject.getString("mobile")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val intent = Intent(applicationContext, AccountActivity::class.java)
        intent.putExtra("nameLetter", userNameLetter)
        intent.putExtra("username", UserName)
        intent.putExtra("password", Password)
        intent.putExtra("mobile", MobileNo)
        intent.putExtra("email", txtEmail)
        startActivity(intent)
    }

}