package com.cifer.xiaogallery.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cifer.xiaogallery.MainActivity;
import com.cifer.xiaogallery.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zf on 12/21/17.
 */

public class SDImageAdapter extends BaseAdapter {

    private ArrayList<Map<String, Object>> list;
    private static HashMap<Integer, Boolean> isSelected;
    private Context context;
    private LayoutInflater inflater = null;
    private MainActivity mMainActivity;
    private static Typeface mPictureNameType;

    public SDImageAdapter(ArrayList<Map<String, Object>> list,Context context,MainActivity mainActivity){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.mMainActivity = mainActivity;
        isSelected = new HashMap<Integer,Boolean>();
        initData();
        if (mPictureNameType==null){
            mPictureNameType = Typeface.createFromAsset(context.getAssets(), "fonts/SamsungNeoNumCond-3T.ttf");
        }
    }

    private void initData(){
        for (int i = 0;i<list.size();i++){
            getIsSelected().put(i,false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_layout,null);
            viewHolder.tv = (TextView) view.findViewById(R.id.item_tv);
            viewHolder.cb = (CheckBox) view.findViewById(R.id.item_cb);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(!mMainActivity.isEditing){
            viewHolder.cb.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.cb.setVisibility(View.VISIBLE);
        }
        //jia gong
        String item = list.get(position).toString();
        String items[] = item.split(",");
        String itemname = items[0];
        String itemmames[] = itemname.split("=");
        String aaa =itemmames[1].substring(0,itemmames[1].length()-4);
        viewHolder.tv.setText(aaa);
        /*if (mPictureNameType!=null){
            viewHolder.tv.setTypeface(mPictureNameType);
        }*/
        Log.d("wangchao","viewHolder===="+viewHolder +"   getIsSelected().get(position)=="+getIsSelected().get(position));
        if (getIsSelected().get(position) != null) {
        	viewHolder.cb.setChecked(getIsSelected().get(position));
		}
        return view;
    }
 

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        SDImageAdapter.isSelected = isSelected;
    }

    public static class ViewHolder{
        TextView tv;
        public CheckBox cb;
    }
}
