package com.example.gcpapp.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.gcpapp.R;
import com.example.gcpapp.download.DownloadMedia;
import com.example.gcpapp.fragments.ChangeBackground;
import com.example.gcpapp.fragments.TriggerChange;
import com.example.gcpapp.helper.SessionManager;
import com.example.gcpapp.ui.home.HomeFragment;
import com.example.gcpapp.upload.MediaActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    private AppBarConfiguration mAppBarConfiguration;
    private  FloatingActionButton fabUploadMediaBtn;
    private  FloatingActionButton fabDownloadMediaBtn;
    private SessionManager session;
    Toolbar toolbar;
    private int wantToExit = 0;
    private String userNameLetter;
    private TextView headerTextTitle;
    private String UserName,MobileNo,Password,txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        TextView mTextMessage = (TextView) findViewById(R.id.message);

        session = new SessionManager(getApplicationContext());
        if (!session.isUserLoggedIn()) {
            logoutUser();
        }

        loadFragment(new HomeFragment());

        NavigationView navigationView = findViewById(R.id.nav_view);
        headerTextTitle  = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtHeader);

        navigationView.getMenu().findItem(R.id.nav_rate_us).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showRateUs(MainActivity.this);
                return true;
            }
        });

        HashMap<String, String> user = session.getUserDetails();
        txtEmail = user.get(SessionManager.KEY_NAME);
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView);
        txtProfileName.setText(Html.fromHtml("\t"+ txtEmail));

        if (txtEmail != null) {
            char name = txtEmail.charAt(0);
            userNameLetter = Character.toString(name);
            headerTextTitle.setText(userNameLetter);
        }

        headerTextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("txtEmail", txtEmail);
                PostResponseAsyncTask emailTask =
                        new PostResponseAsyncTask(MainActivity.this, postData, MainActivity.this);
                emailTask.execute("https://mobile-app-gcp.wl.r.appspot.com/getUserInformation.php");
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.bottom_home,R.id.bottom_dashboard,R.id.bottom_notifications)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.nav_upload:
                        startActivity(new Intent(getApplicationContext(),MediaActivity.class));
                        break;
                    case R.id.nav_download:
                        startActivity(new Intent(getApplicationContext(),DownloadMedia.class));
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        break;
                    case R.id.nav_exit:
                        moveTaskToBack(true);
                        finish();
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                       break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

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

        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment=null;

                switch (menuItem.getItemId()) {
                    case R.id.bottom_home:
                        //    mTextMessage.setText(R.string.title_home)
                        fragment =new HomeFragment();
                        toolbar.setTitle("Home");
                        break;
                    case R.id.bottom_dashboard:
                        //   mTextMessage.setText(R.string.title_dashboard);
                        fragment =new ChangeBackground();
                        toolbar.setTitle("Change Background");
                        break;
                    case R.id.bottom_notifications:
                        // mTextMessage.setText(R.string.title_notifications);
                        fragment = new TriggerChange();
                        toolbar.setTitle("Change Trigger");
                        break;
                }
                return loadFragment(fragment);
            }
        });
    }



    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle(Html.fromHtml("<font color='#ffffff'>GCP Media App</font>"));
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to exit?</font>"))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.dialogBox)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logoutUser() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to log out?</font>"))
                .setCancelable(false)
                .setNegativeButton(Html.fromHtml("<font color='#ffffff'>Logout</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.logoutUser();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                })

                .setPositiveButton(Html.fromHtml("<font color='#ffffff'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.dialogBox)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public void showRateUs(Context context) {

        final Dialog open1 = new Dialog(context);
        open1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View popup = getLayoutInflater().inflate(R.layout.rate_us, null);
        LinearLayout happy = popup.findViewById(R.id.happy);
        LinearLayout bad = popup.findViewById(R.id.bad);
        open1.setContentView(popup);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels);
        open1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        open1.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lparam = new WindowManager.LayoutParams();
        lparam.copyFrom(open1.getWindow().getAttributes());

        open1.getWindow().setLayout((int) (width - 40), ActionBar.LayoutParams.WRAP_CONTENT);
        open1.show();
        bad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try{
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "rohitneel007@gmail.com" });
                    startActivity(Intent.createChooser(intent, ""));
                }
                catch(NullPointerException e){
                    Toast.makeText(getApplicationContext(), "No application can perform this operation", Toast.LENGTH_SHORT).show();
                }
            }
        });
        happy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=co.rohitneel.gcpmedia"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void processFinish(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                 UserName = jsonobject.getString("name");
                 Password = jsonobject.getString("password");
                 MobileNo = jsonobject.getString("mobile");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
        intent.putExtra("nameLetter", userNameLetter);
        intent.putExtra("username",UserName);
        intent.putExtra("password",Password);
        intent.putExtra("mobile",MobileNo);
        intent.putExtra("email",txtEmail);
        startActivity(intent);
    }
}
