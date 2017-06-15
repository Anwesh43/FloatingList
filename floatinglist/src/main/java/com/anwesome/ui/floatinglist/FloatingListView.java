package com.anwesome.ui.floatinglist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by anweshmishra on 15/06/17.
 */

public class FloatingListView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int time = 0,w,h,size;
    private FloatingList floatingList;
    private AnimationHandler animationHandler;
    private CloseButton closeButton;
    private ConcurrentLinkedQueue<FloatingListItem> floatingListItems = new ConcurrentLinkedQueue<>();
    public FloatingListView(Context context,FloatingList floatingList) {
        super(context);
        this.floatingList = floatingList;
    }
    public void addItem(String text) {
        floatingListItems.add(new FloatingListItem(text));
    }
    public void onDraw(Canvas canvas) {
        if(time == 0) {
            w = canvas.getWidth();
            h = canvas.getHeight();
            size = h/10;
            paint.setTextSize(size/2);
            int x = w/10,y = size;
            closeButton = new CloseButton(9*w/10,size,w/15);
            for(FloatingListItem floatingListItem:floatingListItems) {
                floatingListItem.setDimension(x,y);
                y += size;
            }
            animationHandler = new AnimationHandler();
        }
        canvas.drawColor(Color.argb(150,0,0,0));
        for(FloatingListItem item:floatingListItems) {
            item.draw(canvas);
        }
        closeButton.draw(canvas);
        time++;
        if(animationHandler != null) {
            animationHandler.animate();
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && animationHandler != null) {
            float x = event.getX(),y = event.getY();
            FloatingListItem currItem = null;
            for(FloatingListItem floatingListItem:floatingListItems) {
                if(floatingListItem.handleTap(event.getX(),event.getY())) {
                    currItem = floatingListItem;
                    break;
                }
            }
            if(currItem != null) {
                animationHandler.startAnimating(currItem);
            }
            else {
                if (closeButton != null && closeButton.handleTap(x,y)) {
                    floatingList.hide();
                }
            }
        }
        return true;
    }
    private class FloatingListItem {
        private String text;
        private float x,y,wItem,hItem,scale = 0;
        public FloatingListItem(String text) {
            this.text = text;
        }
        public void setDimension(float x,float y) {
            this.x = x;
            this.y = y;
            this.wItem = paint.measureText(text);
            this.hItem = size;
        }
        public void draw(Canvas canvas) {
            paint.setColor(Color.WHITE);
            canvas.drawText(this.text,x,y,paint);
            canvas.save();
            canvas.translate(x+wItem/2,y+hItem/4);
            paint.setStrokeWidth(hItem/12);
            canvas.drawLine(-(wItem/2)*scale,0,(wItem/2)*scale,0,paint);
            canvas.restore();
        }
        public void update(float factor) {
            scale = factor;
        }
        public boolean handleTap(float x,float y) {
            return x>=this.x && x<=this.x+wItem && y>=this.y-hItem/2 && y<=this.y+hItem/2;
        }
        public String getText() {
            return text;
        }
        public int hashCode() {
            return text.hashCode()+(int)(y);
        }
        public void reset() {
            scale = 0;
        }
    }
    private class AnimationHandler {
        private FloatingListItem currItem;
        private boolean isAnimated = false;
        private float factor = 0;
        private void animate() {
            if(isAnimated && currItem != null) {
                currItem.update(factor);
                factor += 0.1f;
                if(factor > 1) {
                    isAnimated = false;
                    currItem.reset();
                    if(onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(currItem.getText());
                    }
                    currItem = null;
                    invalidate();
                    floatingList.hide();
                }
                try {
                   Thread.sleep(50);
                   invalidate();
                }
                catch (Exception ex) {

                }
            }
        }
        public void startAnimating(FloatingListItem floatingListItem) {
            if(!isAnimated && currItem == null) {
                isAnimated = true;
                currItem = floatingListItem;
                factor = 0;
                postInvalidate();
            }
        }
    }
    private OnItemSelectedListener onItemSelectedListener;
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
    public interface OnItemSelectedListener {
        void onItemSelected(String item);
    }
    private class CloseButton {
        private float x,y,size;
        public CloseButton(float x,float y,float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
        public void draw(Canvas canvas) {
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(Math.min(w,h)/80);
            for(int i=0;i<2;i++) {
                canvas.save();
                canvas.translate(x,y);
                canvas.rotate(i*90+45);
                canvas.drawLine(0,-size/2,0,size/2,paint);
                canvas.restore();
            }
        }
        public boolean handleTap(float x,float y) {
            boolean condition = x>=this.x-size/2 && x<=this.x+size/2 && y>=this.y -size/2 && y<=this.y+size/2;
            return condition;
        }
    }
}
