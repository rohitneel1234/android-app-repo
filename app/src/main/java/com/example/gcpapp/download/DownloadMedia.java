package com.example.gcpapp.download;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gcpapp.R;
import com.example.gcpapp.adapters.GlideApp;
import com.example.gcpapp.adapters.ImageListAdapter;
import com.example.gcpapp.constant.StorageConstants;
import com.example.gcpapp.storage.StorageUtils;
import com.example.gcpapp.util.Utils;
import com.google.api.services.storage.Storage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.gcpapp.storage.StorageUtils.getStorage;


public class DownloadMedia extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private File mDirectory;
    private ArrayList<String> list;
    private ArrayList<String> fileList;
    private String mOutputPath;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_media);
        list = new ArrayList<>();
        fileList = new ArrayList<>();
        mDirectory = Utils.getApplicationDirectory();
        mOutputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "MediaStore" + File.separator;
        CheckRequestPermissions();
        try {
            populateRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Requesting user to grant write external storage permission.
     */
    private void CheckRequestPermissions() {
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(DownloadMedia.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    /**
     * For every file in the current directory of our application, get a image or video to display in our RecyclerView.
     */
    public void populateRecyclerView() throws Exception {

        final Storage storage = getStorage();
        final Handler handler = new Handler();
        file = new File(mOutputPath);

        checkWarningForFileNotExist();

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (file.exists()) {

                    for (File filePath : Objects.requireNonNull(file.listFiles())) {
                        fileList.add(filePath.getPath());
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new GridLayoutManager(DownloadMedia.this, 3));
                           /* recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                                    StaggeredGridLayoutManager.VERTICAL));*/
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                    DividerItemDecoration.VERTICAL));
                            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                    DividerItemDecoration.HORIZONTAL));

                            if (fileList != null) {
                                ImageListAdapter adapter = new ImageListAdapter(fileList);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        ShowDialogBox(fileList.get(position));
                                    }
                                });
                            }
                        }
                    });
                }
                // downloadMediaInStorage();  // called to download images/videos from Google cloud storage bucket
            }
        }).start();
    }

    private void checkWarningForFileNotExist() {
        if (!file.exists()) {
            AlertDialog.Builder warning = new AlertDialog.Builder(this);
            warning.setTitle(Html.fromHtml("<font color='#ffffff'>Warning</font>"));
            warning.setMessage(Html.fromHtml("<font color='#ffffff'>To view gallery first click picture or upload images " +
                    "from file manager using Upload menu from side bar.</font>"))
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("OK", null);
            AlertDialog alert = warning.create();
            alert.show();
            final Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
            positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
            positiveButton.setLayoutParams(positiveButtonLL);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.black)));
        }
    }

    /**
     *  Download all media elements from cloud storage bucket in internal storage location
     *  where image and videos don't already have in our directory.
     */
    private void downloadMediaInStorage() {

        String outputPath = mDirectory.getAbsolutePath();
        try {
            List<String> fileNames = StorageUtils.listBucket(StorageConstants.BUCKET_NAME);

            for (String file : fileNames) {
                //Download the file only if it isn't already in our directory.
                if (!Utils.inDirectory(mDirectory, file)) {
                    StorageUtils.downloadFile(StorageConstants.BUCKET_NAME, file, outputPath);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create a dialog to view an image or video element. A video element allows the user to
     * watch the video, and the image element just displays the image larger.
     * @param mediaPath - mediapath the user wishes to view
     */
    private void ShowDialogBox(final String mediaPath) {

        final Dialog dialog = new Dialog(this);

        //Check the type of our element (video or image)
        if (mediaPath.contains(".mp4")) {
            Intent intent = new Intent(DownloadMedia.this, ShowVideo.class);
            intent.putExtra("PATH", mediaPath);
            startActivity(intent);
        }
        else {
            dialog.setContentView(R.layout.custom_dialog);
            ImageView Image = dialog.findViewById(R.id.img);
            Button btn_Full = dialog.findViewById(R.id.btn_full);
            Button btn_Close = dialog.findViewById(R.id.btn_close);
            //extracting name
            GlideApp.with(getApplicationContext())
                    .load(mediaPath)
                    .fitCenter()
                    .into(Image);

            btn_Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_Full.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DownloadMedia.this, FullView.class);
                    intent.putExtra("img_id", mediaPath);
                    startActivity(intent);
                }
            });

            dialog.show();
        }
    }

    /**
     * @param requestCode - The requestCode help to identify from which Intent you came back
     * @param permissions - To check if write external storage permission granted.
     * @param grantResults - To compare with write permission is granted or not.
     */
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
