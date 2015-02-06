package com.vanzstuff.readdit;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class FeedsItemTouchListener implements View.OnTouchListener{

    private static final float MIN_X_DELTA = 50;
    private static final float MIN_X_VELOCITY = 15;
    private static final long ANIMATION_DURATION = 100;
    private VelocityTracker mVeloTracker;
    private float mDownTouchXPosition;
    private float mDownTouchYPosition;
    private boolean mSwiping;
    private int mViewWidth;

    public FeedsItemTouchListener(){
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mViewWidth = v.getWidth();
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN: {
                Logger.d("DOWN");
                if ( !mSwiping ) {
                    mDownTouchXPosition = event.getRawX();
                    mDownTouchYPosition = event.getRawY();
                    mVeloTracker = VelocityTracker.obtain();
                    mVeloTracker.addMovement(event);
                }
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                Logger.d("MOVE");
                if ( mSwiping ) {
                    float xDelta = event.getRawX() - mDownTouchXPosition;
                    v.setTranslationX(xDelta);
                }
                if(!mSwiping)
                    mSwiping = true;
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Logger.d("UP");
                if ( mSwiping ) {
                    if(mVeloTracker == null)
                        break;
                    float xDelta = event.getRawX() - mDownTouchXPosition;
                    float yDelta = event.getRawY() - mDownTouchYPosition;
                    mVeloTracker.addMovement(event);
                    mVeloTracker.computeCurrentVelocity(1000);
                    if (Math.abs(xDelta) >= (mViewWidth * 0.5)) {
                        if (xDelta > 0) {
                            Logger.d("SWIPE RIGHT");
                            v.animate().translationX(mViewWidth)
                                    .setDuration(ANIMATION_DURATION)
                                    .start();
                        } else {
                            Logger.d("SWIPE LEFT");
                            v.animate().translationX(-mViewWidth)
                                    .setDuration(ANIMATION_DURATION)
                                    .start();

                        }
                        mDownTouchXPosition = 0;
                        mDownTouchYPosition = 0;
                    } else {
                        v.animate().translationX(0)
                                .setDuration(ANIMATION_DURATION)
                                .start();
                    }
                    mSwiping = false;
                }
                return true;
            }
            case MotionEvent.ACTION_CANCEL: {
                Logger.d("CANCEL");
                v.animate().translationX(0)
                        .setDuration(ANIMATION_DURATION)
                        .start();
                mSwiping = false;
                mDownTouchXPosition = 0;
                mDownTouchYPosition = 0;
                return true;
            }

        }
        return false;
    }
}
