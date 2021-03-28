package com.example.gcpapp.download;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gcpapp.R;
import com.example.gcpapp.adapters.ImageListAdapter;
import com.example.gcpapp.constant.StorageConstants;
import com.example.gcpapp.storage.StorageUtils;
import com.example.gcpapp.util.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.gcpapp.storage.StorageUtils.getStorage;


public class DownloadMedia extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private FloatingActionButton mFabMediaDownloadBtn;
    private File mDirectory;
    ArrayList<String> list;
    List<String> fileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_media);

        mFabMediaDownloadBtn = findViewById(R.id.fabDownloadMedia);
        list = new ArrayList<String>();
        fileList = new ArrayList<>();
        mDirectory = Utils.getApplicationDirectory();

        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(DownloadMedia.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

        try {
            populateRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addListeners();

    }

    public void addListeners(){
        mFabMediaDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /**
     * For every file in the current directory of our application, get a image or video to display in our RecyclerView.
     */
    public void populateRecyclerView() throws Exception {
        final Storage storage = getStorage();
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<StorageObject> objects = null;
                try {
                    objects = storage.objects().list(StorageConstants.BUCKET_NAME).execute().getItems();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (objects != null) {
                    for (StorageObject o : objects) {
                        list.add(o.getMediaLink());
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

                            ImageListAdapter adapter = new ImageListAdapter(list);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {

                                    File[] files = mDirectory.listFiles();
                                    if (files != null) {
                                        for (File file : files) {
                                            String path = file.getAbsolutePath();
                                            fileList.add(path);
                                        }
                                    }
                                    ShowDialogBox(fileList.get(position));
                                }
                            });
                        }
                    });
                }
                downloadMediaInStorage();
            }
        }).start();

    }

    /**
     *  Download all media elements in internal storage location where image and videos don't already have in our directory.
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
            Glide.with(getApplicationContext())
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
