package com.khgkjg12.pictureloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hyun on 2018-05-22.
 */

public class PictureLoaderDialog extends DialogFragment implements View.OnClickListener{

    private final int request_load_pictrue = 0;
    private OnLoadPictureListener mOnLoadPictureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(android.app.DialogFragment.STYLE_NO_TITLE, 0);
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

    public interface OnFailDialogClickListener{
        void onFailDialogClick();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.camera_button){
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent, request_load_pictrue);
        }else{
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            startActivityForResult(intent, request_load_pictrue);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request_load_pictrue&&resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            Uri uri = bundle.getParcelable("picture");
            mOnLoadPictureListener.onLoadPicture(uri);
            dismiss();
        }
    }

    public interface OnLoadPictureListener{
        void onLoadPicture(Uri uri);
    }

    public void setOnLoadPictureListener(OnLoadPictureListener listener){
        mOnLoadPictureListener = listener;
    }
}