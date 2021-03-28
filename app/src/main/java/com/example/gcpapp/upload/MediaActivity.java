package com.example.gcpapp.upload;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gcpapp.R;
import com.example.gcpapp.constant.StorageConstants;
import com.example.gcpapp.storage.StorageUtils;
import com.example.gcpapp.util.FileUtils;
import com.example.gcpapp.util.Utils;
import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MediaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private Uri uri;
    private FloatingActionButton fabImageUploadBtn;
    private FloatingActionButton fabVideoUploadBtn;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    private Bitmap bitmap;
    private ImageView imageView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_media);
        fabImageUploadBtn = findViewById(R.id.fab1);
        fabVideoUploadBtn = findViewById(R.id.fab2);
        imageView = findViewById(R.id.img_file);
        addListeners();

        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MediaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public void addListeners(){
        fabImageUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Utils.TAG, "Selecting image...");
                selectImage();
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
        startActivityForResult(intent, CHOOSING_IMAGE_REQUEST);
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
     * @param reqCode - request made from calling an intent
     * @param resCode - result from the intent being called.
     * @param data    - Data the user has selected from their intent
     */
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == Activity.RESULT_OK && data != null) {
            uri = data.getData();
            if(reqCode == CHOOSING_IMAGE_REQUEST && uri != null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
            }
            if (uri != null)
                new UploadTask(getApplicationContext()).execute(FileUtils.getPath(MediaActivity.this, uri));
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
                    StorageUtils.uploadFile(StorageConstants.BUCKET_NAME, uri, mContext);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You granted write external storage permission", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
