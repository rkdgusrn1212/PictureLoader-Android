package com.khgkjg12.pictureloader;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hyun on 2018-05-22.
 */

public class PictureLoaderDialog extends DialogFragment implements View.OnClickListener {

    private final int REQUEST_PERMISSION_READ_STORAGE = 0x0100;
    private final int REQUEST_PERMISSION_CAMERA = 0x0101;

    private final int request_load_picture = 0x0200;
    private OnLoadPictureListener mOnLoadPictureListener;

    public static PictureLoaderDialog newInstance(@NonNull OnLoadPictureListener listener) {
        if(listener == null){
            throw new RuntimeException("no listener on PictureLoaderDialog");
        }
        PictureLoaderDialog f = new PictureLoaderDialog();
        f.setOnLoadPictureListener(listener);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.PictureLoaderDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewHierarchy = inflater.inflate(R.layout.picture_loader_dialog, container, false);
        View cameraButton = viewHierarchy.findViewById(R.id.camera_button);
        View albumButton = viewHierarchy.findViewById(R.id.album_button);
        cameraButton.setOnClickListener(this);
        albumButton.setOnClickListener(this);
        return viewHierarchy;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.camera_button){
            startCameraActivity();
        }else{
            startAlbumActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request_load_picture&&resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            Uri uri = bundle.getParcelable("picture");
            mOnLoadPictureListener.onLoadPicture(uri);
            dismiss();
        }
    }

    public interface OnLoadPictureListener{
        void onLoadPicture(Uri uri);
    }

    private void setOnLoadPictureListener(OnLoadPictureListener listener){
        mOnLoadPictureListener = listener;
    }

    private void startAlbumActivity() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionExplainDialog.newInstance("외부 저장소 읽기 권한", "디바이스에 저장된 사진들을 불러오기 위해 필요합니다.", new PermissionExplainDialog.OnResultListener() {
                        @Override
                        public void agreeToPermissionExplainDialog() {
                            requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION_READ_STORAGE);
                        }
                    }).show(getFragmentManager(), "permissionexplaindialog");

                    return;
                } else {

                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_READ_STORAGE);
                    return;
                }

            }
        }
        Intent intent = new Intent(getActivity(), AlbumActivity.class);
        startActivityForResult(intent, request_load_picture);
    }

    private void startCameraActivity() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    PermissionExplainDialog.newInstance("카메라 권한", "사진을 촬영하기위해 카메라 권한이 필요합니다.", new PermissionExplainDialog.OnResultListener() {
                        @Override
                        public void agreeToPermissionExplainDialog() {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_PERMISSION_CAMERA);
                        }
                    }).show(getFragmentManager(), "dialog");
                    return;
                } else {

                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_PERMISSION_CAMERA);
                    return;
                }

            }
        }
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivityForResult(intent, request_load_picture);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode==REQUEST_PERMISSION_READ_STORAGE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                startActivityForResult(intent, request_load_picture);
            }
        }else if(requestCode==REQUEST_PERMISSION_CAMERA){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(intent, request_load_picture);
            }
        }
    }
}