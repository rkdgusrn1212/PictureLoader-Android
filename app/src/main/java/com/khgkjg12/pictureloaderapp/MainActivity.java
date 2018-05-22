package com.khgkjg12.pictureloaderapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

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
    }
}
