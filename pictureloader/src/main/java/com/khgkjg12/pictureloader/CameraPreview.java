package com.khgkjg12.pictureloader;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created by Hyun on 2018-05-23.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    public CameraPreview(Context context, Camera camera, int cameraId) {
        super(context);
        mCamera = camera;
        mCameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId,mCameraInfo);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.;
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview.
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        //보통 카메라(후면 렌즈) 사용하는 사용자 기준으로 회전 방향 생각. 사용자가 자신 기준에서 카메라를 회전시킨 각도 = 카메라 회전 각도. 디바이스 각도는 디바이스를 사용하는 사람 기준으로 생각. 즉, 화면을 마주보는 사용자가 회전시킨 정도
        //기본 보정값, 카메라가 설치된 회전각도를 보정. -> 입력영상이 카메라 회전각도의 역방향으로 회전되어서 들어오는 것과 같으므로 츨력영상을 카메라 회전 각도만큼 회전시켜 보정.
        //카메라 회전시 좌우 반전으로 인한 효과, 카메라 회전시 카메라 회전 방향과 같은 방향으로 회전된 입력영상 들어오는 것과 같다. 따라서 실제 카메라 회전방향의 역방향으로 출력영상을 회전시켜 보정.
        //전면 카메라 보정 효과, 디바이스 회전 각도의 역방향 회전을 한다 = surface 의 회전 각도와 같다.->위와 합쳐서 surface 의 회전 각도의 역방향으로 출력값 보정..
        int deviceRotate;
        int surfaceRotate = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if(surfaceRotate == Surface.ROTATION_90){
            deviceRotate = 270;
        }else if(surfaceRotate == Surface.ROTATION_180){
            deviceRotate = 180;
        }else if(surfaceRotate == Surface.ROTATION_270){
            deviceRotate = 90;
        }else{
            deviceRotate = 0;
        }
        int cameraRotate, outputRotate;
        if(mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            cameraRotate = mCameraInfo.orientation - deviceRotate;
            outputRotate = -cameraRotate;
        }else{
            cameraRotate = mCameraInfo.orientation + deviceRotate;
            outputRotate = cameraRotate;
        }
        outputRotate%=360;
        if(outputRotate<0){
            outputRotate+=360;
        }
        mCamera.setDisplayOrientation(outputRotate);
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        int previewWidth, previewHeight;
        if(outputRotate==90||outputRotate==270){
            previewWidth = previewSize.height;
            previewHeight = previewSize.width;
        }else{
            previewWidth = previewSize.width;
            previewHeight = previewSize.height;
        }
        //w / h > previewWidth/previewHeight = w*previewHeight > previewWidth*h =>do holderWidth = (previewWidth/previewHeight)*h
        if(w*previewHeight > previewWidth*h){
            getLayoutParams().width = previewWidth*h/previewHeight;
        }else{
            getLayoutParams().height = previewHeight*w/previewWidth;
        }
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}