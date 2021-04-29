package com.example.gcpapp.upload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.gcpapp.R;
import com.example.gcpapp.constant.StorageConstants;
import com.example.gcpapp.storage.StorageUtils;
import com.example.gcpapp.util.FileUtils;
import com.example.gcpapp.util.Utils;
import com.github.clans.fab.FloatingActionButton;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewIntent;
import ly.img.android.ui.activities.PhotoEditorIntent;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MediaActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION_CODE = 7;
    private Uri uri;
    private String storagePath;
    private FloatingActionButton fabCameraUploadBtn;
    private FloatingActionButton fabGalleryUploadBtn;
    private FloatingActionButton fabVideoUploadBtn;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    private Bitmap bitmap;
    private ImageView imageView, imgActionBarUpload ;
    private ProgressDialog progressDialog;
    public static int CAMERA_PREVIEW_RESULT = 2;
    private File dir;
    private AlertDialog alertDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media);

        clickToUploadImage();

        imgActionBarUpload.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imgView);
        fabCameraUploadBtn = findViewById(R.id.fab1);
        fabGalleryUploadBtn = findViewById(R.id.fab2);
        fabVideoUploadBtn = findViewById(R.id.fab3);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + getString(R.string.directory_name);
        dir = new File(path);
        // If All permission is enabled successfully then this block will execute.
        if(CheckingPermissionIsEnabledOrNot())
        {
            Toast.makeText(MediaActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        }
        else {                                                // If permission is not enabled then else condition will execute.
            //Calling method to enable permission.
            RequestMultiplePermission();
        }
        addListeners();
    }

    /**
     * Checking Camera and Write External Storage Permission if granted or not.
     * @return
     */
    private boolean CheckingPermissionIsEnabledOrNot() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requesting multiple permission
     */
    private void RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MediaActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE
                }, REQUEST_PERMISSION_CODE);
    }

    private void clickToUploadImage() {
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        imgActionBarUpload = new ImageView(actionBar.getThemedContext());
        imgActionBarUpload.setScaleType(ImageView.ScaleType.CENTER);
        imgActionBarUpload.setImageResource(R.drawable.baseline_cloud_upload_white_24dp);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 40;
        imgActionBarUpload.setLayoutParams(layoutParams);
        actionBar.setCustomView(imgActionBarUpload);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addListeners() {
        fabCameraUploadBtn.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        // Your action here on button click
                        new CameraPreviewIntent(MediaActivity.this)
                                .setEditorIntent(
                                        new PhotoEditorIntent(MediaActivity.this)
                                                .setExportDir(dir.getPath())
                                                .setExportPrefix(getString(R.string.photo_result_prefix))
                                                .destroySourceAfterSave(true)
                                )
                                .startActivityForResult(CAMERA_PREVIEW_RESULT);

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
        fabGalleryUploadBtn.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        new ImagePicker.Builder(MediaActivity.this)
                                .mode(ImagePicker.Mode.GALLERY)
                                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                                .directory(ImagePicker.Directory.DEFAULT)
                                .extension(ImagePicker.Extension.PNG)
                                .scale(600, 600)
                                .allowMultipleImages(false)
                                .enableDebuggingMode(true)
                                .build();

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        fabVideoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Utils.TAG, "Selecting Video...");
                selectVideo();
            }
        });
    }

    /**
     * Select an image from the gallery via an implicit intent
     */
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CAMERA_PREVIEW_RESULT);
    }

    /**
     * Select a video from the gallery via an implicit intent
     */
    public void selectVideo(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, 0);
    }


    /**
     * Once the user has returned from choosing a video / image, upload it to the cloud.
     * @param requestCode - request made from calling an intent
     * @param resultCode - result from the intent being called.
     * @param data    - Data the user has selected from their intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (requestCode == CAMERA_PREVIEW_RESULT) {
                String path = data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                uri = Uri.parse("content:/"+path);
                storagePath = path;
                Toast.makeText(this, getString(R.string.photo_saved_toast_string), Toast.LENGTH_LONG).show();
                Glide.with(this)
                        .load(path)
                        .into(imageView);
                imgActionBarUpload.setVisibility(View.VISIBLE);
            }
            if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE) {
                List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
                new PhotoEditorIntent(MediaActivity.this)
                        .setSourceImagePath(mPaths.get(0))
                        .setExportDir(dir.getPath())
                        .setExportPrefix(getString(R.string.photo_result_prefix))
                        .destroySourceAfterSave(true)
                        .startActivityForResult(CAMERA_PREVIEW_RESULT);
                String imgPath = mPaths.get(0);
                storagePath = imgPath;
                uri = Uri.parse("content:/"+imgPath);
            }
            if (uri.toString().contains("video")) {
                new UploadTask(getApplicationContext()).execute(FileUtils.getPath(MediaActivity.this, uri));
            }
            imgActionBarUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uri != null && Utils.getInstance(getApplicationContext()).isNetworkAvailable()) {
                        new UploadTask(getApplicationContext()).execute(FileUtils.getPath(MediaActivity.this, uri));
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Please check your internet connection and try again !!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * AsyncTask to upload a media element to the cloud.
     */
    private class UploadTask extends AsyncTask<String, Integer, List<String>> {

        Context mContext;

        public UploadTask(Context context){
            this.mContext =context;
        }

        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
            progressDialog = new ProgressDialog(MediaActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading... media to cloud");
            progressDialog.show();
        }

        /**
         * Do the task in background/non UI thread
         */
        @Override
        protected List<String> doInBackground(String... params) {

            int count = params.length;
            StorageConstants.CONTEXT = getApplicationContext();
            List<String> taskList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String currentTask = params[i];
                taskList.add(currentTask);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress((int) (((i + 1) / (float) count) * 100));
                try {
                    if(uri.toString().contains(".jpg") || uri.toString().contains(".png")) {
                        StorageUtils.uploadImages(StorageConstants.BUCKET_NAME, storagePath);
                    } else {
                        StorageUtils.uploadVideos(StorageConstants.BUCKET_NAME, uri, mContext);
                    }
                } catch (Exception e) {
                    Log.d("Failure", "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    break;
                }
            }
            return taskList;
        }

        // After each task done
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<String> result){
            Utils.createToast("Upload complete!", MediaActivity.this);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION_CODE) {

            if (grantResults.length > 0) {

                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean WriteStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission && WriteStoragePermission ) {
                    Toast.makeText(MediaActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MediaActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    alertDialog = new AlertDialog.Builder(MediaActivity.this).create();
                    alertDialog.setMessage(getString(R.string.permissions_message));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                            CheckingPermissionIsEnabledOrNot();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                            Toast.makeText(MediaActivity.this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

}
