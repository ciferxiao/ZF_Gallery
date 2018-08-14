package com.cifer.xiaogallery;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaFile;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cifer.xiaogallery.Adapter.ImageAdapter;
import com.mediatek.storage.StorageManagerEx;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private ImageAdapter imageAdapter;
    private ImageButton rcs_delete;
    private ImageButton rcs_select_all;
    private ImageButton rcs_radio;
    private int checkNum;
    public  boolean isEditing = false;

    private LinearLayout linearLayout;
    private ImageButton mBackbutton;

    public static final String DCIM = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM).toString();
    public static final String DIRECTORY = DCIM + "/Camera";
    public String FOLDER_PATH ;//= "/" + Environment.DIRECTORY_DCIM + "/Camera";
    public File file;
    private File mFile;
    private String mName;
    public static final String MIMETYPE_EXTENSION_NULL = "unknown_ext_null_mimeType";
    public static final String MIMETYPE_EXTENSION_UNKONW = "unknown_ext_mimeType";
    private String mpicture;

    private final static String helper = "记录管理主界面";
    private String sMountPoint ;//= StorageManagerEx.getDefaultPath();

    private PhoneFragment phoneFragment;
    private SdFragment sdFragment;

    private TextView phone;
    private TextView sdcard;
    private ImageView imageview;

    private int mCurrentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGrantExternalRW(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mBackbutton = (ImageButton)findViewById(R.id.rcs_back);
        linearLayout = (LinearLayout)findViewById(R.id.buttonlinear);
        rcs_delete = (ImageButton)findViewById(R.id.rcs_delete);
        rcs_select_all = (ImageButton)findViewById(R.id.rcs_select_all);
        rcs_radio = (ImageButton)findViewById(R.id.rcs_radio);

        phone = (TextView)findViewById(R.id.phone);
        sdcard = (TextView)findViewById(R.id.sdcard);
        //imageview = (ImageView)findViewById(R.id.imageview);

        phone.setOnClickListener(this);
        sdcard.setOnClickListener(this);
        rcs_delete.setOnClickListener(this);
        rcs_select_all.setOnClickListener(this);
        rcs_radio.setOnClickListener(this);
        mBackbutton.setOnClickListener(this);

        Log.d("xiao333", "activity oncreate ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册sd卡插拔广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.setPriority(1000);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        Log.d("xiao333", "activity onResume ");
    }

    private void refresh(){
        //TODO 判断sd卡

        FOLDER_PATH = "/" + Environment.DIRECTORY_DCIM;
        
        sMountPoint = StorageManagerEx.getDefaultPath();

        if(ExistSDCard()){
            //imageview.setVisibility(View.VISIBLE);
            sdcard.setVisibility(View.VISIBLE);
            mCurrentTab = 1;
            FrameClick(mCurrentTab);
        }else{
            //TODO:
            //imageview.setVisibility(View.GONE);
            sdcard.setVisibility(View.GONE);
            mCurrentTab = 0;
            FrameClick(mCurrentTab);
            
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        ComponentName mComp = new ComponentName("com.example.zf_instructions","com.example.zf_instructions.MainActivity");
        intent.setComponent(mComp);
        intent.putExtra("key",helper);
        startActivity(intent);
        finish();
    }

    private void FrameClick(int postion){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (postion){
            case 0:
                if(phoneFragment == null){
                    phoneFragment = new PhoneFragment();
                }
                
                phone.setBackground(getResources().getDrawable(R.drawable.clicked));
                sdcard.setBackground(null);
                transaction.replace(R.id.frame,phoneFragment);
                break;

            case 1:
                if(sdFragment == null){
                    sdFragment = new SdFragment();
                }
                sdcard.setBackground(getResources().getDrawable(R.drawable.clicked));
                phone.setBackground(null);
                transaction.replace(R.id.frame,sdFragment);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rcs_delete:
                if(mCurrentTab == 0){
                    phoneFragment.updateList(0);
                }else{
                    Log.d("xiao444","sdFragment == " + sdFragment);
                    sdFragment.updateList(0);
                }
                break;
            case R.id.rcs_select_all:
                if(mCurrentTab == 0){
                    phoneFragment.updateList(1);
                }else{
                    sdFragment.updateList(1);
                }
                break;
            case R.id.rcs_radio:
                if(mCurrentTab == 0){
                    phoneFragment.updateList(2);
                }else{
                    sdFragment.updateList(0);
                }
                break;
            case R.id.rcs_back:
                Intent intenta = new Intent(); //调用照相机
                intenta.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intenta);
                finish();
                break;
            case R.id.phone:
                //FrameClick(0);
                mCurrentTab = 0;
                FrameClick(mCurrentTab);
                
                break;
            case R.id.sdcard:
                mCurrentTab = 1;
                FrameClick(mCurrentTab);
                //refresh();
                break;
        }
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("xiao222"," sd card broadcast ==" + intent.getAction());
            if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
                Log.d("xiao222"," sd card broadcast  MOUNTED");
                
                refresh();
            }else if(intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)){
                Log.d("xiao222"," sd card broadcast  UNMOUNTED");
                
                refresh();
            }
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    public ArrayList<Map<String, Object>> getMapData(ArrayList<File> list){
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        HashMap<String,Object> item;
        int i = 0 ;
        for(i=0;i<list.size();i++){
            item = new HashMap<String,Object>();
            String path  = list.get(i).toString();
            String name = path.substring(path.lastIndexOf("/")+1,path.length());
            //保存每一格list单元格的数据 ，
            item.put("ItemText",name);
            item.put("ItemTitle", path);

            data.add(item);
        }
        return data;
    }

    private void deleteFiles(String mPath) {
        File file = new File(mPath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            file.delete();
        }
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    public Uri getItemContentUri(Context context,String path) {
        final String[] projection = {MediaColumns._ID};
        final String where = MediaColumns.DATA + " = ?";
        Uri baseUri = MediaStore.Files.getContentUri("external");
        Cursor c = null;
        String provider = "com.android.providers.media.MediaProvider";
        Uri itemUri = null;
        context.grantUriPermission(provider, baseUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.i("gallery_log", "getItemContentUri, filePath = " + path+
                ", projection = "+projection+
                ", where = "+where+
                ", baseUri = "+baseUri);
        try {
            c = context.getContentResolver().query(baseUri,
                    projection,
                    where,
                    new String[]{path},
                    null);
            if (c != null && c.moveToNext()) {
                int type = c.getInt(c.getColumnIndexOrThrow(MediaColumns._ID));
                if (type != 0) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaColumns._ID));
                    Log.i("gallery_log", "getItemContentUri, item id = " + id);
                    itemUri =  Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }
        } catch (Exception e) {
            Log.i("gallery_log", "getItemContentUri Exception", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return itemUri;
    }

    private String getMimeType(File file, Context context, String absPath) {
        mFile = new File(absPath);
        mName = mFile.getName();
        String fileName = mFile.getName();
        String extension = getFileExtension(fileName);
        Log.d("gallery_log", "getMimeType fileName=" + fileName + ",extension = " + extension);
        if (extension == null) {
            return MIMETYPE_EXTENSION_NULL;
        }
        String mimeType = MediaFile.getMimeTypeForFile(fileName);
        if ((mimeType == null) ||
                (mimeType != null && mimeType.endsWith("3gpp"))) {
            final String[] projection = {MediaColumns.MIME_TYPE};
            final String where = MediaColumns.DATA + " = ?";
            Uri baseUri = MediaStore.Files.getContentUri("external");
            String provider = "com.android.providers.media.MediaProvider";
            Cursor c = null;
            try {
                c = context.getContentResolver().query(baseUri,
                        projection,
                        where,
                        new String[]{absPath},
                        null);
                if (c != null && c.moveToNext()) {
                    String type = c.getString(c.getColumnIndexOrThrow(
                            MediaColumns.MIME_TYPE));
                    if (type != null) {
                        mimeType = type;
                    }
                }
            } catch (Exception e) {
                Log.i("gallery_log", "getMimeType Exception", e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        Log.d("gallery_log", "getMimeType mimeType =" + mimeType);
        if (mimeType == null) {
            return MIMETYPE_EXTENSION_UNKONW;
        }
        return mimeType;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String extension = null;
        final int lastDot = fileName.lastIndexOf('.');
        if ((lastDot >= 0)) {
            extension = fileName.substring(lastDot + 1).toLowerCase();
        }
        return extension;
    }


    public void OnIntentGallery(String path){
        mFile = new File(path);
        String mimeType = getMimeType(mFile,this,path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri =getItemContentUri(this,path);
        if (uri!=null) {
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            startActivity(intent);
            finish();
        } catch (android.content.ActivityNotFoundException  e) {
            // TODO: handle exception
        }
    }

    private boolean ExistSDCard() {
        //Log.i("xiao222","DCIM                    == " + DCIM);
        //Log.i("xiao222","sMountPoint+FOLDER_PATH == " + sMountPoint+FOLDER_PATH);
        if (DCIM.equals(sMountPoint+FOLDER_PATH)) {
            return false;
        } else{
            return true;
        }
    }

    public void refreshUi(boolean isEditing){
        if(isEditing){
            this.isEditing = isEditing;
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            this.isEditing = isEditing;
            linearLayout.setVisibility(View.INVISIBLE);
        }
    }
}
