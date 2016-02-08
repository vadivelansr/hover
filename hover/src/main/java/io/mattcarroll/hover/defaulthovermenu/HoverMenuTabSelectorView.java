package io.mattcarroll.hover.defaulthovermenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import io.mattcarroll.hover.R;

/**
 * {@code View} that draws a triangle selector icon at a given horizontal position within its bounds.
 */
public class HoverMenuTabSelectorView extends View {

    private static final String TAG = "HoverMenuTabSelectorView";

    private static final int DEFAULT_SELECTOR_WIDTH_DP = 24;
    private static final int DEFAULT_SELECTOR_HEIGHT_DP = 16;

    private int mSelectorWidthPx;
    private int mSelectorHeightPx;
    private int mDesiredSelectorCenterLocationPx; // the selector position that the client wants
    private int mLeftMostSelectorLocationPx; // based on mLeftBoundOffset and mSelectorWidthPx;
    private int mRightMostSelectorLocationPx; // based on mRightBoundOffsetPx and mSelectorWidthPx;

    private Path mSelectorPaintPath;
    private Paint mSelectorPaint;

    public HoverMenuTabSelectorView(Context context) {
        this(context, null);
    }

    public HoverMenuTabSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSelectorWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SELECTOR_WIDTH_DP, getResources().getDisplayMetrics());
        mSelectorHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SELECTOR_HEIGHT_DP, getResources().getDisplayMetrics());
        setSelectorPosition(mSelectorWidthPx/2);

        mSelectorPaint = new Paint();
        mSelectorPaint.setColor(getResources().getColor(R.color.floatingmenu_popup_color));
        mSelectorPaint.setStyle(Paint.Style.FILL);
    }

    public void setSelectorColor(@ColorInt int color) {
        mSelectorPaint.setColor(color);
        invalidate();
    }

    /**
     * Sets the pixel position of the center of the selector icon. The position given will be
     * clamped to available space in this View.
     *
     * @param position horizontal pixel position
     */
    public void setSelectorPosition(int position) {
        mDesiredSelectorCenterLocationPx = position;
        invalidateSelectorPath();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mSelectorHeightPx);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            invalidateSelectorPath();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mSelectorPaintPath, mSelectorPaint);
    }

    private void invalidateSelectorPath() {
        mLeftMostSelectorLocationPx = getPaddingLeft() + (mSelectorWidthPx / 2);
        mRightMostSelectorLocationPx = getWidth() - getPaddingRight() - (mSelectorWidthPx / 2);

        int selectorCenterLocationPx = clampSelectorPosition(mDesiredSelectorCenterLocationPx);

        mSelectorPaintPath = new Path();
        mSelectorPaintPath.moveTo(selectorCenterLocationPx, 0); // top of triangle
        mSelectorPaintPath.lineTo(selectorCenterLocationPx + (mSelectorWidthPx / 2), mSelectorHeightPx); // bottom right of triangle
        mSelectorPaintPath.lineTo(selectorCenterLocationPx - (mSelectorWidthPx / 2), mSelectorHeightPx); // bottom left of triangle
        mSelectorPaintPath.lineTo(selectorCenterLocationPx, 0); // back to origin

        invalidate();
    }

    private int clampSelectorPosition(int position) {
        if (position < mLeftMostSelectorLocationPx) {
            return mLeftMostSelectorLocationPx;
        } else if (position > mRightMostSelectorLocationPx) {
            return mRightMostSelectorLocationPx;
        } else {
            return position;
        }
    }
}