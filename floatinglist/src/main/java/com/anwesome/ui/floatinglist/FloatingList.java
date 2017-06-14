package com.anwesome.ui.floatinglist;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.ViewGroup;

/**
 * Created by anweshmishra on 15/06/17.
 */

public class FloatingList {
    private Activity activity;
    private int w,h;
    private boolean isShown = false;
    private FloatingListView floatingListView;
    private AnimationHandler animationHandler;
    public FloatingList(Activity activity) {
        this.activity = activity;
        floatingListView = new FloatingListView(activity,this);
        initDimension(activity);
        animationHandler = new AnimationHandler();
    }
    public void initDimension(Context context) {
        DisplayManager displayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(0);
        if(display != null) {
            Point size = new Point();
            display.getRealSize(size);
            w = size.x;
            h = size.y;
        }
    }
    public void setOnItemSelectedListener(FloatingListView.OnItemSelectedListener onItemSelectedListener) {
        if(floatingListView != null) {
            floatingListView.setOnItemSelectedListener(onItemSelectedListener);
        }
    }
    public void addItem(String text) {
        if(!isShown) {
            floatingListView.addItem(text);
        }
    }

    public void addToParent() {
        if(!isShown) {
            floatingListView.setY(-h);
            activity.addContentView(floatingListView,new ViewGroup.LayoutParams(w,h));
            isShown = true;
        }
    }
    public void show() {
        if(animationHandler != null) {
            animationHandler.startShowing();
        }
    }
    public void hide() {
        if(animationHandler != null) {
            animationHandler.startHiding();
        }
    }
    private class AnimationHandler implements ValueAnimator.AnimatorUpdateListener{
        private ValueAnimator startAnim = ValueAnimator.ofFloat(0,1),endAnim = ValueAnimator.ofFloat(1,0);
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(floatingListView != null) {
                float factor = (float)valueAnimator.getAnimatedValue();
                floatingListView.setY(h*(1-factor));
            }
        }
        public void startShowing() {
            startAnim.start();
        }
        public void startHiding() {
            endAnim.start();
        }
        public AnimationHandler() {
            startAnim.setDuration(500);
            endAnim.setDuration(500);
            startAnim.addUpdateListener(this);
            endAnim.addUpdateListener(this);
        }
    }

}
