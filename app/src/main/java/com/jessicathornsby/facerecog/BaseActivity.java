package com.jessicathornsby.facerecog;

import android.app.Activity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.FileProvider;


import android.net.Uri;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    public static final int WRITE_STORAGE = 100;
    public static final int CAMERA = 102;
    public static final int SELECT_PHOTO = 103;
    public static final int TAKE_PHOTO = 104;
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public File photoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(ACTION_BAR_TITLE));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                checkPermission(CAMERA);
                break;
            case R.id.action_gallery:
                checkPermission(WRITE_STORAGE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                   requestPermission(this, requestCode, R.string.camera_denied);
                }
                break;
            case WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhoto();
                } else {
                    requestPermission(this, requestCode, R.string.storage_denied);
                }
                break;
        }
    }

    public static void requestPermission(final Activity activity, final int requestCode, int message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(message);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, requestCode);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }


    public void checkPermission(int requestCode) {
        switch (requestCode) {
            case CAMERA:
                int hasCameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestCode);
                }
                break;
            case WRITE_STORAGE:
                int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    selectPhoto();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;
        }
    }


    private void selectPhoto() {
        photoFile = MyHelper.createTempFile(photoFile);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    private void launchCamera() {
        photoFile = MyHelper.createTempFile(photoFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photo = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photo);
        startActivityForResult(intent, TAKE_PHOTO);
    }
}


