package com.cifer.xiaogallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.cifer.xiaogallery.Adapter.SDImageAdapter;
import com.mediatek.storage.StorageManagerEx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by cifer
 * on 8/10/18.
 */

public class SdFragment extends Fragment {

    private SDImageAdapter sdImageAdapter;
    private ListView listView;
    private ArrayList<File> mlist;
    private MainActivity mainActivity;

    public boolean isEditing = false;
    private String mpicture;
    private int checkNum;
    private String filepath;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phonefragment, container,false);
        listView = (ListView)view.findViewById(R.id.listview);

        Log.d("xiao333", "sd fragment onCreateView ");
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!isEditing) {
                    mpicture = mlist.get(position).toString();
                    mainActivity.OnIntentGallery(mpicture);
                } else {
                    SDImageAdapter.ViewHolder holder = (SDImageAdapter.ViewHolder) view.getTag();
                    holder.cb.toggle();
                    SDImageAdapter.getIsSelected().put(position, holder.cb.isChecked());
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
                sdImageAdapter.notifyDataSetChanged();
                return true;
            }
        });

        initData();

        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Log.d("xiao333", "sd fragment onStart ");
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mlist = null;
        Log.d("xiao333", "sd fragment onStop ");
    }


    public void initData() {
        mainActivity = (MainActivity) this.getActivity();
        mlist = new ArrayList<File>();

        String sMountPoint = StorageManagerEx.getDefaultPath();
        String FOLDER_PATH = "/" + Environment.DIRECTORY_DCIM + "/Camera";
        filepath = sMountPoint + FOLDER_PATH;

        Log.d("xiao222", " filepath == "+ filepath);
        getAllFiles(new File(filepath));
        
        Log.d("xiao222", " mlist == "+ mlist);
        Log.d("xiao222", " mainActivity == "+ mainActivity);

        sdImageAdapter = new SDImageAdapter(mainActivity.getMapData(mlist), mainActivity, mainActivity);
        listView.setAdapter(sdImageAdapter);

    }

    private void getAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
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

    public void updateList(int position) {
        switch (position) {
            case 0:
                isEditing = false;
                mainActivity.refreshUi(isEditing);
                File currentfile;
                //linearLayout.setVisibility(View.INVISIBLE);
                for (int i = 0; i < mlist.size(); i++) {
                    if (SDImageAdapter.getIsSelected().get(i)) {
                        Log.d("xiao222", "mlist.get(i).toString() == " + mlist.get(i).toString());

                        //deletePicture(mainActivity, mlist.get(i).toString());
                        currentfile = new File(mlist.get(i).toString());
                        Log.d("xiao222"," currentfile === " + currentfile);
                        deleteFile(currentfile);
                        //  list.remove(i);
                    }
                }
                mlist = new ArrayList<File>();
                //判断sd卡是否存在，选择文件路径
                getAllFiles(new File(filepath));
                sdImageAdapter = new SDImageAdapter(mainActivity.getMapData(mlist), mainActivity, mainActivity);
                listView.setAdapter(sdImageAdapter);
                dataChanged();
                break;
            case 1:
                for (int i = 0; i < mlist.size(); i++) {
                    if (SDImageAdapter.getIsSelected().get(i)) {
                        SDImageAdapter.getIsSelected().put(i, false);
                        checkNum--;
                    } else {
                        SDImageAdapter.getIsSelected().put(i, true);
                        checkNum++;
                    }
                }
                dataChanged();
                break;
            case 2:
                for (int i = 0; i < mlist.size(); i++) {
                    if (SDImageAdapter.getIsSelected().get(i)) {
                        SDImageAdapter.getIsSelected().put(i, false);
                        checkNum--;
                    }
                }
                dataChanged();

                break;
        }


    }

    private void dataChanged() {
        sdImageAdapter.notifyDataSetChanged();
    }

    
    public static void deleteFile(File curFile) {
        if (!curFile.exists()) {
            return;
        }
       
        if (curFile.isDirectory()) {
            File[] files = curFile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
            curFile.delete();
            Log.d("xiao222"," boolean 111 === " + curFile.delete());
        } else {
            curFile.delete();
            Log.d("xiao222"," boolean  === " + curFile.delete());
        }
    }

    public Uri getItemContentUri(Context context, String path) {
        final String[] projection = {MediaStore.MediaColumns._ID};
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        Uri baseUri = MediaStore.Files.getContentUri("external");
        Cursor c = null;
        String provider = "com.android.providers.media.MediaProvider";
        Uri itemUri = null;
        context.grantUriPermission(provider, baseUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.i("gallery_log", "getItemContentUri, filePath = " + path +
                ", projection = " + projection +
                ", where = " + where +
                ", baseUri = " + baseUri);
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
                    itemUri = Uri.withAppendedPath(baseUri, String.valueOf(id));
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

