package de.arvato.stratego;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class StartPlayActivity extends StrategoActivity implements GestureDetector.OnGestureListener {

    private StrategoView strategoView;
    private GestureDetector gestureDetector;

    //private int selectedColor;

    private static final String TAG = "StartPlayActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_play);

        strategoView = new StrategoView(this);

        gestureDetector = new GestureDetector(this, this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //Log.d(TAG, "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //Log.d(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //Log.d(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Log.d(TAG, "onFling");

        if (e1.getRawY() > e2.getRawY()) {
            //switcher.showNext();
            strategoView.showNext();
            Log.d(TAG, "Show Next");
        } else {
            //switcher.showPrevious();
            strategoView.showPrevious();
            Log.d(TAG, "Show Previous");
        }

        return false;
    }}