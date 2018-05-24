package com.khgkjg12.pictureloaderapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.khgkjg12.pictureloader.AlbumActivity;
import com.khgkjg12.pictureloader.PictureLoaderDialog;

/**
 * Created by Hyun on 2018-05-22.
 */

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        mImageView = findViewById(R.id.imageview);
        mButton = findViewById(R.id.button);

        mImageView.getLayoutParams().height = screenWidth;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPicture();
            }
        });
    }

    private void loadPicture(){
        PictureLoaderDialog.newInstance(new PictureLoaderDialog.OnLoadPictureListener() {
            @Override
            public void onLoadPicture(Uri uri) {
                mImageView.setImageURI(uri);
            }
        }).show(getSupportFragmentManager(), "dialog");
    }
}
