package com.khgkjg12.pictureloader;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionExplainDialog extends DialogFragment {

    private String mPermissionName;
    private String mText;
    private OnResultListener mListener;

    public static PermissionExplainDialog newInstance(String permissionName, String text) {
        PermissionExplainDialog f = new PermissionExplainDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("permission", permissionName);
        args.putString("text",text);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mPermissionName = bundle.getString("permission");
        mText = bundle.getString("text");
        setStyle(DialogFragment.STYLE_NORMAL, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("권한 요청");
        builder.setMessage("해당 서비스를 사용하려면 \""+mPermissionName+"\" 권한이 필요합니다.\n"+mText)
                .setPositiveButton("허가", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.agreeToPermissionExplainDialog();
                    }
                })
                .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResultListener) {
            mListener = (OnResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResultListener");
        }
    }

    public interface OnResultListener{
        void agreeToPermissionExplainDialog();
    }
}
