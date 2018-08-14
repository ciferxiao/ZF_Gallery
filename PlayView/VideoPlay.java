package com.cifer.xiaogallery.PlayView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.zf_gallery.R;

import java.io.File;

/**
 * Created by zf on 12/22/17.
 */

public class VideoPlay extends Activity implements View.OnClickListener,Animation.AnimationListener{
    private VideoView mView;
    private MediaController mediaController;
    private Handler mHandler;
    private View mPictureLayout;
    private Animation hideAnimation;
    private Animation showAnimation;
    private ImageButton back;
    private LinearLayout flayout;
    private boolean isShowingToolbar = true;
    private View mVideoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_layout);
        mView = (VideoView)findViewById(R.id.rcs_video);
        flayout = (LinearLayout)findViewById(R.id.toolbar);
        mVideoContainer = findViewById(R.id.video_container);
        back = (ImageButton)findViewById(R.id.back);
        initAnimation();
        mHandler = new Handler();
        mediaController=new MediaController(this);
        mView.setMediaController(mediaController);
        //mView.setRotation(90f);
        mediaController.setMediaPlayer(mView);
        Intent intent = getIntent();
        String mpath = intent.getStringExtra("video");

        File vFile=new File(mpath);
        if (vFile.exists()) {//如果文件存在
            mView.setVideoPath(vFile.getAbsolutePath());
            //让videoView获得焦点
            mView.start();
            mView.requestFocus();
        }
        back.setOnClickListener(this);
        mVideoContainer.setOnClickListener(this);
        mView.setOnClickListener(this);
        mHandler.postDelayed(startHidingRunnable, 8000);
    }


    private void initAnimation(){
       hideAnimation = AnimationUtils
              .loadAnimation(this, R.anim.player_out);
        hideAnimation.setAnimationListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.video_container:
                if(isShowingToolbar){
                    startHiding();
                    isShowingToolbar = false;
                }else {
                    flayout.setVisibility(View.VISIBLE);
                    isShowingToolbar = true;
                }
                break;
          /*  case R.id.rcs_video:
                if(isShowingToolbar){
                    startHiding();
                    isShowingToolbar = false;
                }else {
                    flayout.setVisibility(View.VISIBLE);
                    isShowingToolbar = true;
                }
                break;*/

        }

    }

    Runnable  startHidingRunnable = new Runnable() {
        @Override
        public void run() {
            startHiding();
        }
    };

    private void startHiding() {
        startHideAnimation(flayout);
    }
    private void startHideAnimation(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.startAnimation(hideAnimation);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        flayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
