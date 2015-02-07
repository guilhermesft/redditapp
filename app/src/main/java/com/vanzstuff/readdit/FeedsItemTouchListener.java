package com.vanzstuff.readdit;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class FeedsItemTouchListener implements View.OnTouchListener{

    private static final long ANIMATION_DURATION = 100;
    private final RecyclerView mRecyclerView;
    private final ViewConfiguration mVc;
    private final int mSlop;
    private final int mMinFlyingVelocity;
    private final int mMaxFlyingVelocity;
    private VelocityTracker mVeloTracker;
    private float mDownTouchXPosition;
    private float mDownTouchYPosition;
    private boolean mSwiping;
    private int mViewWidth;
    private int mSwipingSlop;

    public FeedsItemTouchListener(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
        mVc = ViewConfiguration.get(recyclerView.getContext());
        mSlop = mVc.getScaledTouchSlop();
        mMinFlyingVelocity = mVc.getScaledMinimumFlingVelocity();
        mMaxFlyingVelocity = mVc.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mViewWidth = v.getWidth();
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN: {
                mDownTouchXPosition = event.getRawX();
                mDownTouchYPosition = event.getRawY();
                mVeloTracker = VelocityTracker.obtain();
                mVeloTracker.addMovement(event);
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                mVeloTracker.addMovement(event);
                final float deltaX = event.getRawX() - mDownTouchXPosition;
                final float deltaY = event.getRawY() - mDownTouchYPosition;
                if(!mSwiping && Math.abs(deltaX) < mSlop && Math.abs(deltaY) < Math.abs(deltaX) * 0.5){
                    mSwiping = true;
                    mSwipingSlop = deltaX > 0 ? mSlop : -mSlop;
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);
                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    mRecyclerView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }
                if ( mSwiping ) {
                    v.setTranslationX(deltaX - mSwipingSlop);
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_UP: {
                if(mVeloTracker == null)
                    break;
                final float xDelta = event.getRawX() - mDownTouchXPosition;
                mVeloTracker.addMovement(event);
                mVeloTracker.computeCurrentVelocity(1000);
                final float absVelocityX = Math.abs(mVeloTracker.getXVelocity());
                final float absVelocityY = Math.abs(mVeloTracker.getYVelocity());
                if (Math.abs(xDelta) > (mViewWidth * 0.35) && mSwiping && absVelocityX > absVelocityY
                        && absVelocityX > mMinFlyingVelocity && absVelocityX < mMaxFlyingVelocity) {
                    int animationTranslation;
                    if (xDelta > 0) {
                        animationTranslation = v.getWidth();
                    } else {
                        animationTranslation = -v.getWidth();
                    }
                    v.animate().translationX(animationTranslation)
                            .setDuration(ANIMATION_DURATION)
                            .alpha(0)
                            .start();
                    break;
                }
            }
            case MotionEvent.ACTION_CANCEL: {
                v.animate().translationX(0)
                        .setDuration(ANIMATION_DURATION)
                        .start();
                mSwiping = false;
                mSwipingSlop = 0;
                mDownTouchXPosition = 0;
                mDownTouchYPosition = 0;
                mVeloTracker = null;
                return true;
            }

        }
        return false;
    }
}
