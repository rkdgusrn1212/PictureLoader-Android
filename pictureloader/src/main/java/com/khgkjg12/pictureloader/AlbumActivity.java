package com.khgkjg12.pictureloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Hyun on 2018-05-22.
 */

public class AlbumActivity extends AppCompatActivity implements  View.OnClickListener, PermissionExplainDialog.OnResultListener, LoaderManager.LoaderCallbacks<Cursor> {

    private int RESULT_PERMISSIONS = 0;
    private int REQUSET_FOR_SETTING = 1;

    private AlbumRecyclerViewAdapter mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private Cursor mCursor;
    private ArrayList<Long> bucketIdList;//안드로이드 Images.media의 Bucket ID 들을 저장-> 같은 BucketName을 구분하기 위함
    private ArrayList<CharSequence> bucketNameList;//안드로이드 Images.media의 Bucket Name 들을 저장
    private ArrayAdapter<CharSequence> mAdapter;
    private ImageView mImageView;
    private Button mNextButton;
    private Spinner mSpinner;
    private Uri mSelectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);
        requestPermissionCamera();

        ActionBar actionBar = getSupportActionBar();
        View view = getLayoutInflater().inflate(R.layout.album_activity_actionbar,null);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mSpinner = getSupportActionBar().getCustomView().findViewById(R.id.spinner);
        mNextButton = getSupportActionBar().getCustomView().findViewById(R.id.button_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });

        bucketNameList = new ArrayList<>();
        bucketNameList.add("전체");//버킷이 없어도 꼭 있어야하는 기본 옵션이 전체 보기이기 때문이다.
        bucketIdList = new ArrayList<>();
        bucketIdList.add(0L);//전체보기 옵션에 해당하는 버킷아이디는 없기 때문에 null에 해당하는 0L을 넣는다.
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bucketNameList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("bucketId",bucketIdList.get(position));
                getLoaderManager().restartLoader(1, bundle, mThis);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getLoaderManager().initLoader(0, null, this);
        mRecyclerViewAdapter = new AlbumRecyclerViewAdapter(getContext(), mCursor, this );
        mRecyclerView.setAdapter(mRecyclerViewAdapter);


        View viewHierarchy = inflater.inflate(R.layout.fragment_album, container, false);
        mRecyclerView = viewHierarchy.findViewById(R.id.recycler_view);
        mImageView = viewHierarchy.findViewById(R.id.image_view);
        mImageView.getLayoutParams().height=container.getMeasuredWidth();

    }

    private void startCrop(){
        if(mSelectedUri==null){
            return;
        }
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));
        Crop.of(mSelectedUri, outputUri).asSquare().start(this);
    }
    /**
     *
     * @param id 생성 아이디가 0일때 버킷 리스트들의 id와 name들을 로드한다, 1일떄는 선택된 버킷 아이디에 따라 해당 버킷에 포함된 이미지들을 로드한다.
     * @param args 로드할 버킷 아이디를 전달한다. id=1일때한 유효하다.
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0){
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            };
            Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            return new CursorLoader(getActivity(), baseUri,
                    projection, null, null,
                    MediaStore.Images.Media.BUCKET_ID + " DESC");
        }else{
            long bucketId = args.getLong("bucketId");
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA
            };
            Uri baseUri;

            baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String select = null;
            if(bucketId!=0L){
                select = " bucket_id = " + bucketId;
            }

            return new CursorLoader(getActivity(), baseUri,
                    projection, select, null,
                    MediaStore.Images.Media.BUCKET_ID + " DESC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==0){
            data.moveToFirst();
            long preBKID;
            long BKID = 0;
            do{
                preBKID = BKID;
                BKID = data.getLong(1);
                if(preBKID != BKID){
                    bucketNameList.add(data.getString(3));
                    bucketIdList.add(BKID);
                }
            } while(data.moveToNext());
            mAdapter.notifyDataSetChanged();
        }else{
            if(data.moveToFirst()){
                selectedUri = Uri.parse("file:///"+data.getString(2));
                Picasso.with(getContext()).load(selectedUri).centerCrop().fit().memoryPolicy(MemoryPolicy.NO_STORE).into(mImageView);
            }
            mRecyclerViewAdapter.swapCursor(data);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == 0) {
            bucketIdList.clear();
            bucketNameList.clear();
            bucketIdList.add(null);
            bucketNameList.add("전체");
            mAdapter.notifyDataSetChanged();
        } else {
            mRecyclerViewAdapter.swapCursor(null);
        }
    }

    @Override
    public void onClick(View v) {
        selectedUri = (Uri)v.getTag();
        Picasso.with(getContext()).load(selectedUri).centerCrop().fit().memoryPolicy(MemoryPolicy.NO_STORE).into(mImageView);
    }

    @Override
    public void agreeToPermissionExplainDialog() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getApplication().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(i, REQUSET_FOR_SETTING);
    }
    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, UploadDetailActivity.class);
            intent.putExtra("imageUri", mOutputUri);
            intent.putExtra("originPostId", mOriginPostId);
            startActivity(intent);
        } else if (requestCode == REQUSET_FOR_SETTING) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    RESULT_PERMISSIONS);
        }
    }

    public boolean requestPermissionCamera() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    PermissionExplainDialogFragment.newInstance("카메라 권한", "사진을 촬영하기위해 카메라 권한이 필요합니다.").show(getSupportFragmentManager(), "dialog");
                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            RESULT_PERMISSIONS);
                }

            } else {
                mFragments.add(new CameraFragment());
                mPageAdapter.notifyDataSetChanged();
                mTabLayout.getTabAt(0).setIcon(R.drawable.icon_album);
                mTabLayout.getTabAt(1).setIcon(R.drawable.icon_camera);
                mTabLayout.setVisibility(View.VISIBLE);
            }
        } else {  // version 6 이하일때
            mFragments.add(new CameraFragment());
            mPageAdapter.notifyDataSetChanged();
            mTabLayout.getTabAt(0).setIcon(R.drawable.icon_album);
            mTabLayout.getTabAt(1).setIcon(R.drawable.icon_camera);
            mTabLayout.setVisibility(View.VISIBLE);
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (RESULT_PERMISSIONS == requestCode) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mFragments.add(new CameraFragment());
                mPageAdapter.notifyDataSetChanged();
                mTabLayout.getTabAt(0).setIcon(R.drawable.icon_album);
                mTabLayout.getTabAt(1).setIcon(R.drawable.icon_camera);
                mTabLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}

