package com.khgkjg12.pictureloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.hardware.Camera.open;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by Hyun on 2018-05-22.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, Camera.PictureCallback, SensorEventListener {

    private Camera mCamera;
    private Uri mOutputUri;
    private final int REQUEST_PERMISSION_WRITE_STORAGE = 0x0102;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int mDeviceRotation = 0;
    private int mCameraOriginalRotate;
    private int mCameraFacing;
    private int mOutputRotation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(checkCameraHardware(this)){
            int cameraId = getCameraInstanceId();
            if(cameraId==-1){
                Toast.makeText(getApplicationContext(), R.string.camera_is_not_available_error, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }else {
                try {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(cameraId, cameraInfo);
                    mCameraFacing = cameraInfo.facing;
                    mCameraOriginalRotate = cameraInfo.orientation;
                    mCamera = Camera.open(cameraId);
                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.camera_is_not_available_error, Toast.LENGTH_SHORT).show();
                    finish();
                }
                CameraPreview mPreview = new CameraPreview(this, mCamera, cameraId);
                FrameLayout previewLayout = findViewById(R.id.preview);
                previewLayout.addView(mPreview, 0);
                ((FrameLayout.LayoutParams) mPreview.getLayoutParams()).gravity = Gravity.CENTER;
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.no_camera_error, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageButton pictureButton = findViewById(R.id.picture_button);
        pictureButton.setOnClickListener(this);
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


    @Override
    protected void onResume() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String appName = getApplicationInfo().name;
        String imageFileName = appName+"-" + timeStamp;
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            FileOutputStream outputStream = new FileOutputStream(image);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.picture_save_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Uri originalUri = Uri.fromFile(image);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(originalUri);
        sendBroadcast(mediaScanIntent);

        mOutputUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));
        Crop.of(originalUri, mOutputUri).asSquare().start(this);
    }

    private void takePicture(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    PermissionExplainDialog.newInstance("외부 저장소 쓰기 권한", "사진을 공유 저장소에 저장하기 위해 필요합니다.", new PermissionExplainDialog.OnResultListener() {
                        @Override
                        public void agreeToPermissionExplainDialog() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSION_WRITE_STORAGE);
                            }
                        }
                    }).show(getSupportFragmentManager(), "permissionexplaindialog");
                    return;
                } else {

                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_WRITE_STORAGE);
                    return;
                }

            }
        }
        int cameraRotate;
        //전면 카메라가 촬영시에는 좌우대칭이 아님 따라서 두 경우 모두 카메라 회전각만큼 출력을 회전해 보정.
        if(mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            cameraRotate = mCameraOriginalRotate - mDeviceRotation;
        }else{
            cameraRotate = mCameraOriginalRotate + mDeviceRotation;
        }
        mOutputRotation = cameraRotate;
        mOutputRotation%=360;
        if(mOutputRotation<0){
            mOutputRotation+=360;
        }

        Camera.Parameters parameter = mCamera.getParameters();
        parameter.setRotation(mOutputRotation);
        mCamera.setParameters(parameter);
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.picture_button){
            takePicture();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("picture",mOutputUri);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode==REQUEST_PERMISSION_WRITE_STORAGE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.values[0]>Math.abs(event.values[1])){
            mDeviceRotation = 270;
        }else if(Math.abs(event.values[0])<=-event.values[1]){
            mDeviceRotation = 180;
        }else if(-event.values[0]>Math.abs(event.values[1])){
            mDeviceRotation = 90;
        }else{
            mDeviceRotation = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
