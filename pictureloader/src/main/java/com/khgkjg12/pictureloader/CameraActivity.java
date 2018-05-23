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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

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
        if(checkCameraHardware(this)){
            mCamera = getCameraInstance();
            if(mCamera==null){
                Toast.makeText(this, R.string.camera_is_not_available_error, Toast.LENGTH_SHORT).show();
            }else {
                mPreview = new CameraPreview(this, mCamera);
                FrameLayout previewLayout = findViewById(R.id.preview);
                previewLayout.addView(mPreview,0);
                mCamera.setDisplayOrientation(90);
            }
        }else{
            Toast.makeText(this, R.string.no_camera_error, Toast.LENGTH_LONG).show();
        }
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

    public static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open();// attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
