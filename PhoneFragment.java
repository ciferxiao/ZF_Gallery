package com.cifer.xiaogallery;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cifer.xiaogallery.Adapter.ImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by cifer
 * on 8/10/18.
 */

public class PhoneFragment extends Fragment {

    private ImageAdapter imageAdapter;
    private ListView listView;
    private ArrayList<File> mlist;
    private MainActivity mainActivity;
    private final static String filepath = MainActivity.DIRECTORY;

    public  boolean isEditing = false;
    private String mpicture;
    private int checkNum;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phonefragment, container,false);
        listView = (ListView)view.findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!isEditing){
                    mpicture = mlist.get(position).toString();
                    mainActivity.OnIntentGallery(mpicture);
                }else {
                    ImageAdapter.ViewHolder holder = (ImageAdapter.ViewHolder) view.getTag();
                    holder.cb.toggle();
                    ImageAdapter.getIsSelected().put(position, holder.cb.isChecked());
                    if (holder.cb.isChecked()) {
                        checkNum++;
                    } else {
                        checkNum--;
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //linearLayout.setVisibility(View.VISIBLE); 更新activity ui
                isEditing = true;
                mainActivity.refreshUi(isEditing);
                imageAdapter.notifyDataSetChanged();
                return true;
            }
        });
        Log.d("xiao333", "fragment onCreateView ");
        initData();

        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        if(mlist == null){
            initData();
        }
        Log.d("xiao333", "phone fragment onStart ");
        
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mlist = null;
        Log.d("xiao333", "phone fragment onStop ");
    }

    public void initData(){
        mainActivity = (MainActivity) this.getActivity();
        mlist = new ArrayList<File>();

        getAllFiles(new File(filepath));

        imageAdapter = new ImageAdapter(mainActivity.getMapData(mlist),mainActivity,mainActivity);
        
        Log.d("xiao222"," mlist phonefragment == " + mlist.size() );
        Log.d("xiao222"," mlist listView == " + listView );
        
        listView.setAdapter(imageAdapter);

    }

    private void getAllFiles(File root){
        File files[] = root.listFiles();
        if(files != null)
            for(File f:files){
                if(f.isDirectory()){
                    getAllFiles(f);
                }
                else{
                    if(f.getName().contains("videorecorder")){
                        //break;
                    }else{
                        this.mlist.add(f);
                    }                                        
                }
            }
        //Collections.reverse(list); // 倒序排列
        Collections.sort(mlist, new FileComparator());
    }

    public void updateList(int position){
        switch (position){
            case 0:
                isEditing =false;
                mainActivity.refreshUi(isEditing);
                //linearLayout.setVisibility(View.INVISIBLE);
                for(int i = 0;i<mlist.size();i++){
                    if(ImageAdapter.getIsSelected().get(i)){
                        Log.d("xiao222","mlist.get(i).toString() == " + mlist.get(i).toString());

                            deletePicture(mainActivity,mlist.get(i).toString());
                        //  list.remove(i);
                    }
                }
                mlist = new ArrayList<File>();
                //判断sd卡是否存在，选择文件路径
                    getAllFiles(new File(filepath));
                imageAdapter = new ImageAdapter(mainActivity.getMapData(mlist),mainActivity,mainActivity);
                listView.setAdapter(imageAdapter);
                dataChanged();
                break;
            case 1:
                for (int i = 0; i < mlist.size(); i++) {
                    if (ImageAdapter.getIsSelected().get(i)) {
                        ImageAdapter.getIsSelected().put(i, false);
                        checkNum--;
                    } else {
                        ImageAdapter.getIsSelected().put(i, true);
                        checkNum++;
                    }
                }
                dataChanged();
                break;
            case 2:
                for (int i = 0; i < mlist.size(); i++) {
                if (ImageAdapter.getIsSelected().get(i)) {
                    ImageAdapter.getIsSelected().put(i, false);
                    checkNum--;
                }
            }
                dataChanged();

                break;
        }



    }

    private void dataChanged(){
        imageAdapter.notifyDataSetChanged();
    }


    public void deletePicture(Context context, String filePath){
        Log.i("ddddddddsss", " filePath ==== " + filePath);
        Uri uri =getItemContentUri(context,filePath);
        Log.i("ddddddddsss", " uri ==== "  + uri);
        ContentResolver mContentResolver = context.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + filePath + "'";
        //删除图片
        mContentResolver.delete(uri,where, null);
        Log.i("ddddddddsss", " sendBroadcast ==== " );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            mainActivity.sendBroadcast(mediaScanIntent);
        } else {
            mainActivity.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    public Uri getItemContentUri(Context context,String path) {
        final String[] projection = {MediaStore.MediaColumns._ID};
        final String where = MediaStore.MediaColumns.DATA + " = ?";
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
                int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                if (type != 0) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
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

}
