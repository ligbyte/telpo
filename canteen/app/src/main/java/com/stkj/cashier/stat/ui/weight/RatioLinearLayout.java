package com.stkj.cashier.stat.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.stkj.cashier.R;

/**
 * 比例布局工具
 */
public class RatioLinearLayout extends LinearLayout {

    private float mRatioWH = 1.0f;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    public RatioLinearLayout(Context context) {
        super(context);
        readRatioAttr(context,null);
    }

    public RatioLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readRatioAttr(context,attrs);
    }

    public RatioLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readRatioAttr(context,attrs);
    }

    public void readRatioAttr(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.RatioLinearLayout);
        mRatioWH = a.getFloat(R.styleable.RatioLinearLayout_ratio_w_h, 1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measuredDimension(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    public void measuredDimension(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatioWH <= 0) {
            mRatioWH = 1;
        }
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            if (widthSize > 0) {
                int heightSize = (int) (widthSize * 1.0f / mRatioWH);
                mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            }
        } else if (heightMode == MeasureSpec.EXACTLY) {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (heightSize > 0) {
                int widthSize = (int) (heightSize * mRatioWH);
                mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            }
        }
    }

    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }

    public void setRatioWH(float mRatioWH) {
        this.mRatioWH = mRatioWH;
    }
}