package com.vanzstuff.readdit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BackgroundContainer extends FrameLayout {

    private boolean mShouldDraw;
    private Drawable mBackground;
    private int mOpenAreaTop;
    private int mOpenAreaHeight;
    private boolean mUpdateBounds;

    public BackgroundContainer(Context context) {
        super(context);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShouldDraw = false;
    }

    public void setBackground( int drawableID ){
        mBackground = getContext().getResources().getDrawable(drawableID);
    }

    public void show(int top, int bottom){
        setWillNotDraw(false);
        mOpenAreaTop = top;
        mOpenAreaHeight = bottom;
        mShouldDraw = true;
        mUpdateBounds = true;
    }

    public void hide(){
        setWillNotDraw(true);
        mShouldDraw = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShouldDraw) {
            if (mUpdateBounds)
                mBackground.setBounds(0,0, getWidth(), mOpenAreaHeight);
            canvas.save();
            canvas.translate(0, mOpenAreaTop);
            mBackground.draw(canvas);
            canvas.restore();
        }
    }
}
