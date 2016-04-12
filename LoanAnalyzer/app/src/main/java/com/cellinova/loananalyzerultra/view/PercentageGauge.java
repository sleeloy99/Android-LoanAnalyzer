/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;

import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.LoanType;
import com.cellinova.loananalyzerultra.model.Message;
import com.cellinova.loananalyzerultra.model.MessageChannel;
import com.cellinova.loananalyzerultra.model.NumberFormatObject;
import com.cellinova.loananalyzerultra.persist.ModelObject;

/**
 * Custom view that shows a percentage gauge.  The gauge
 * animates the percentage from the start pecentage to
 * the final percentage value.
 */
public class PercentageGauge extends View implements Runnable {

    protected float mPercentageInterval;
    protected float mArcInterval;
    protected float mCurrentPercentage;
    protected float mArc;
    protected float mPercentage;
    protected int mInterval;
    protected int mIntervalState;
    protected int mAlphaColor;
    protected int mBetaColor;
    protected int mBackgroundColor;
    protected int mDelay;
    protected String mText;
    protected int mStartDegree;
    protected float mTextSize;
    protected float mArcStrokeWidth;
    protected float mArcSize;
    protected float mXinset;
    protected float mYinset;
    protected LoanType mType;

    /**
     * Class constructor taking only a context. Use this constructor to create
     * {@link PercentageGauge} objects.
     *
     * @param context
     */
    public PercentageGauge(Context context) {
        super(context);
        setFocusable(true);
        resetState();
    }

    /**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a {@link PercentageGauge} from a set of
     * XML attributes.
     *
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                {@link com.cellinova.loananalyzerultra.R.styleable.PercentageGauge} as well as attributes inherited
     *                from {@link android.view.View}.
     */
    public PercentageGauge(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PercentageGauge,
                0, 0
        );


        try {
            mPercentage = a.getFloat(R.styleable.PercentageGauge_percentage, 0.0f);
            mInterval = a.getInteger(R.styleable.PercentageGauge_interval, 10);
            mDelay = a.getInteger(R.styleable.PercentageGauge_delay, 30);
            mText = a.getString(R.styleable.PercentageGauge_label);
            mAlphaColor = a.getColor(R.styleable.PercentageGauge_alphaColor, 0xff000000);
            mBetaColor = a.getColor(R.styleable.PercentageGauge_betaColor, 0xff000000);
            mBackgroundColor = a.getColor(R.styleable.PercentageGauge_backgroundColor, 0xff000000);
            mStartDegree = a.getInteger(R.styleable.PercentageGauge_startDegree, 30);
            mTextSize = a.getDimensionPixelSize(R.styleable.PercentageGauge_textSize, 60);
            mArcStrokeWidth = a.getDimensionPixelSize(R.styleable.PercentageGauge_strokeWidth, 10);
            mXinset = a.getFloat(R.styleable.PercentageGauge_xInset, 0.0f);
            mYinset = a.getFloat(R.styleable.PercentageGauge_yInset, 0.0f);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        setFocusable(true);
    }


    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mXinset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mXinset, metrics) + mArcStrokeWidth;
        mYinset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mYinset, metrics) + mArcStrokeWidth;
        float width = xNew - mXinset * 2;
        float height = yNew - mYinset * 2;
        mArcSize = Math.min(width, height);
        if (mArcSize == width) {
            mYinset = (height - mArcSize) / 2;
        } else {
            mXinset = (width - mArcSize) / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(mBackgroundColor);

        Paint p = new Paint();
        // smooths
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        ;
        p.setStrokeWidth(mArcStrokeWidth);

        RectF rectF = new RectF(mXinset, mYinset, (mXinset + mArcSize), (mYinset + mArcSize));
        p.setColor(mBetaColor);
        canvas.drawArc(rectF, mStartDegree + mArc, 360 - mArc, false, p);
        p.setColor(mAlphaColor);
        canvas.drawArc(rectF, mStartDegree, mArc, false, p);
        p.setTextSize(mTextSize);
        p.setStrokeWidth(1);
        p.setStyle(Paint.Style.FILL);

        String percentageStr = String.format("%.1f", mCurrentPercentage) + "%";
        Rect textBounds = new Rect(0, 0, 0, 0);
        p.getTextBounds(percentageStr, 0, percentageStr.length(), textBounds);

        int h = textBounds.height();
        canvas.drawText(percentageStr, mXinset + (mArcSize - textBounds.width()) / 2, mYinset + (mArcSize + textBounds.height()) / 2, p);

        if (mIntervalState < mInterval) {
            postDelayed(this, mDelay);
        }

    }

    /**
     * Resets the state of the gauge.
     */
    public void resetState() {
        mPercentageInterval = mPercentage / mInterval;
        mArcInterval = mPercentage * 3.6f / mInterval;
        mCurrentPercentage = mPercentageInterval;
        mArc = mArcInterval;
        mIntervalState = 0;
    }

    @Override
    public void run() {
        // Update state of what we draw
        mIntervalState++;
        if (mIntervalState == mInterval) {
            mCurrentPercentage = mPercentage;
            mArc = mPercentage * 3.6f;
        } else {
            mArc += mArcInterval;
            mCurrentPercentage += mPercentageInterval;
        }
        invalidate();
    }

    public void setPercentageInterval(float mPercentageInterval) {
        this.mPercentageInterval = mPercentageInterval;
    }

    public void setArcInterval(float mArcInterval) {
        this.mArcInterval = mArcInterval;
    }

    public void setArc(float mArc) {
        this.mArc = mArc;
    }

    public void setPercentage(float mPercentage) {
        this.mPercentage = mPercentage;
    }

    public void setInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public void setIntervalState(int mIntervalState) {
        this.mIntervalState = mIntervalState;
    }

    public void setAlphaColor(int mAlphaColor) {
        this.mAlphaColor = mAlphaColor;
    }

    public void setBetaColor(int mBetaColor) {
        this.mBetaColor = mBetaColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public void setDelay(int mDelay) {
        this.mDelay = mDelay;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setStartDegree(int mStartDegree) {
        this.mStartDegree = mStartDegree;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setArcStrokeWidth(float mArcStrokeWidth) {
        this.mArcStrokeWidth = mArcStrokeWidth;
    }

    public void setArcSize(float mArcSize) {
        this.mArcSize = mArcSize;
    }

    public void setXinset(float mXinset) {
        this.mXinset = mXinset;
    }

    public void setYinset(float mYinset) {
        this.mYinset = mYinset;
    }


}