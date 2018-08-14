package com.cifer.xiaogallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cifer
 * on 8/13/18.
 */

public class NoslideViewpager extends ViewPager {


    //是否可以进行滑动
    private boolean isSlide = false;

    public void setSlide(boolean slide) {
        isSlide = slide;
    }
    public NoslideViewpager(Context context) {
        super(context);
    }

    public NoslideViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlide;
    }
}
