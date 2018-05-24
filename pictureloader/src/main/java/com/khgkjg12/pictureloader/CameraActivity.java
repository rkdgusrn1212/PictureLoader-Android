package com.khgkjg12.pictureloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import static android.hardware.Camera.open;

/**
 * Created by Hyun on 2018-05-22.
 */

public class CameraActivity extends AppCompatActivity{

    Camera mCamera;
    CameraPreview mPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.hide();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        if(checkCameraHardware(this)){
            int cameraId = getCameraInstanceId();
            if(cameraId==-1){
                Toast.makeText(getApplicationContext(), R.string.camera_is_not_available_error, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }else {
                try {
                    mCamera = Camera.open(cameraId);
                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.camera_is_not_available_error, Toast.LENGTH_SHORT).show();
                    finish();
                }
                mPreview = new CameraPreview(this, mCamera, cameraId);
                FrameLayout previewLayout = findViewById(R.id.preview);
                previewLayout.addView(mPreview, 0);
                ((FrameLayout.LayoutParams)mPreview.getLayoutParams()).gravity = Gravity.CENTER;
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.no_camera_error, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageButton pictureButton = findViewById(R.id.picture_button);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CLICKED ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static int getCameraInstanceId() {
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return i;
                }
            }
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return i;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return -1; // returns -1 if camera is unavailable
    }
}
