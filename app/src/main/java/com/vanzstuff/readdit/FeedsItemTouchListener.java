package com.vanzstuff.readdit;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.vanzstuff.readdit.data.FeedsAdapter;

public class FeedsItemTouchListener implements View.OnTouchListener{

    private static final long ANIMATION_DURATION = 100;
    private final RecyclerView mRecyclerView;
    private final ViewConfiguration mVc;
    private final int mSlop;
    private final int mMinFlyingVelocity;
    private final int mMaxFlyingVelocity;
    private final FeedsAdapter.ItemSelectedListener mListener;
    private final GestureDetector mDetector;
    private VelocityTracker mVeloTracker;
    private float mDownTouchXPosition;
    private float mDownTouchYPosition;
    private boolean mSwiping;
    private int mViewWidth;
    private int mSwipingSlop;

    public FeedsItemTouchListener(RecyclerView recyclerView, FeedsAdapter.ItemSelectedListener listener){
        mRecyclerView = recyclerView;
        mVc = ViewConfiguration.get(recyclerView.getContext());
        mSlop = mVc.getScaledTouchSlop();
        mMinFlyingVelocity = mVc.getScaledMinimumFlingVelocity();
        mMaxFlyingVelocity = mVc.getScaledMaximumFlingVelocity();
        mListener = listener;
        mDetector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Rect rect = null;
                int[] location = new int[2];
                mRecyclerView.getLocationOnScreen(location);
                for(int i = 0; i < mRecyclerView.getChildCount(); i++){
                    rect = new Rect();
                    View child = mRecyclerView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains((int)e.getRawX() - location[0], (int)e.getRawY() - location[1])){
                        mListener.onLinkClicked(mRecyclerView.getChildItemId(child));
                        return true;
                    }
                }
                return false;
            }
        });
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
                mDetector.onTouchEvent(event);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                boolean detectorConsume = mDetector.onTouchEvent(event);
                if (detectorConsume)
                    return true;
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
                }
                return false;
            }
            case MotionEvent.ACTION_UP: {
                boolean detectorConsume = mDetector.onTouchEvent(event);
                if (detectorConsume)
                    return true;
                if(mVeloTracker == null){
                    break;
                }
                if ( !mSwiping && mDownTouchXPosition != 0 && mDownTouchYPosition != 0)
                    v.performClick();
                final float xDelta = event.getRawX() - mDownTouchXPosition;
                mVeloTracker.addMovement(event);
                mVeloTracker.computeCurrentVelocity(1000);
                final float absVelocityX = Math.abs(mVeloTracker.getXVelocity());
                final float absVelocityY = Math.abs(mVeloTracker.getYVelocity());
                if (Math.abs(xDelta) > (mViewWidth * 0.35) && mSwiping && absVelocityX > absVelocityY
                        && absVelocityX > mMinFlyingVelocity && absVelocityX < mMaxFlyingVelocity) {
                    if (xDelta > 0) {
                        slideRight(v);
                    } else {
                        slideLeft(v);
                    }
                    mRecyclerView.requestDisallowInterceptTouchEvent(false);
                    return true;
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

    public void slideRight(View v){
        v.animate().translationX(v.getWidth())
                .setDuration(ANIMATION_DURATION)
                .alpha(0)
                .start();
        mListener.saveLink(mRecyclerView.getChildItemId(v));
    }

    public void slideLeft(View v){
        v.animate().translationX(-v.getWidth())
                .setDuration(ANIMATION_DURATION)
                .alpha(0)
                .start();
        mListener.onLinkHidden(mRecyclerView.getChildItemId(v));
    }
}
