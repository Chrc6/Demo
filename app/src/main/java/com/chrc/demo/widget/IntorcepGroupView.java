package com.chrc.demo.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author : chrc
 * date   : 2021/3/10  5:12 PM
 * desc   :
 */
public class IntorcepGroupView extends RelativeLayout {

    private boolean hasIntercoptor = false;

    public IntorcepGroupView(Context context) {
        super(context);
    }

    public IntorcepGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntorcepGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("interceptor===","dispatchTouchEvent hasIntercoptor="+hasIntercoptor);
        if (!hasIntercoptor) {
            return true;
        } else {
            hasIntercoptor = false;
            return super.onInterceptTouchEvent(ev);
        }
    }

//    @Override
//    public boolean on(MotionEvent ev) {
//        Log.i("interceptor===","dispatchTouchEvent hasIntercoptor="+hasIntercoptor);
//        if (!hasIntercoptor) {
//            return true;
//        } else {
//            hasIntercoptor = false;
//            return super.dispatchTouchEvent(ev);
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("interceptor===","onTouchEvent hasIntercoptor="+hasIntercoptor);
        if (!hasIntercoptor) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hasIntercoptor = true;
                    Log.i("interceptor===","onTouchEvent postDelayed");
                    if (getChildCount() != 0) {
                        Log.i("interceptor===","child performClick");
                        getChildAt(0).performClick();
                    }
                }
            }, 3000);
        }
        return super.onTouchEvent(event);
    }
}
