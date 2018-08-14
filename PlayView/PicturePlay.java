package com.cifer.xiaogallery.PlayView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zf_gallery.R;
import android.graphics.Matrix;

/**
 * Created by zf on 12/22/17.
 */

public class PicturePlay extends Activity implements View.OnClickListener,Animation.AnimationListener {
    private ImageView nPictureView ;
    String mPath;
    private ImageButton mPictureBack;
    private Handler mHandler;
    private View mPictureLayout;
    private Animation hideAnimation;
    private Animation showAnimation;
    private ImageButton back;
    private LinearLayout flayout;
    private boolean isShowingToolbar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.show_bitmap);
        nPictureView = (ImageView)findViewById(R.id.imageview);
        back = (ImageButton)findViewById(R.id.back);
        flayout = (LinearLayout)findViewById(R.id.toolbar);
        initAnimation();
        nPictureView.setOnClickListener(this);
        mHandler = new Handler();
        Intent intent = getIntent();
        mPath = intent.getStringExtra("picture");
        Bitmap bitmap =  getPicture(mPath);
        nPictureView.setImageBitmap(bitmap);

        back.setOnClickListener(this);
        mHandler.postDelayed(startHidingRunnable, 8000);

    }
    
    private void initAnimation(){
        hideAnimation = AnimationUtils
              .loadAnimation(this, R.anim.player_out);
        hideAnimation.setAnimationListener(this);

    }

    private Bitmap getPicture(String mPath){
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        //bitmap = rotateBitmap(bitmap,-90);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.imageview:
                if(isShowingToolbar){
                    startHiding();
                    isShowingToolbar = false;
                }else {
                    flayout.setVisibility(View.VISIBLE);
                    isShowingToolbar = true;
                }
                //startHiding();
                break;
            case R.id.back:
                finish();
                break;
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
        // Do nothing.
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Do nothing.
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        flayout.setVisibility(View.INVISIBLE);
    }
    
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
