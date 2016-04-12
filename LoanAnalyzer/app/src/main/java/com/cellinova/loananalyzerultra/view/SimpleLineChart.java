/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.cellinova.loananalyzerultra.R;

public class SimpleLineChart extends View implements Runnable {

    protected int mBackgroundColor;
    protected int mWidth;
    protected int mHeight;
    protected int mYNumOfIntervals = 5;
    private float[] yValues = {0.0f, 23.0f, 12.3f, 45.3f, 100.0f, 32.3f};
    private float[] yValuesState = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    protected float minY = 0.0f;
    protected float maxY = 100.0f;
    private String[] xValues = {"2011", "2012", "2013", "2014", "2015", "2016"};
    protected int mIntervalState = 0;
    protected int mInterval = 10;
    protected int mDelay = 30;
    protected float mTopInset;
    protected float mBottomInset;
    protected float mRightInset;
    protected float mLeftInset;
    protected float mPointRadius;
    protected float mLineWidth;
    protected float mYAxisGap;
    protected float mXAxisGap;
    protected float mAxisTextSize;
    protected float mGridLineWidth;
    protected int mLineColor;
    protected int mGridLineColor;
    protected String mYAxisFormatter;

    public SimpleLineChart(Context context) {
        super(context);
    }

    public SimpleLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SimpleLineChart,
                0, 0
        );

        try {
            mTopInset = a.getFloat(R.styleable.SimpleLineChart_topInset, 10.0f);
            mBottomInset = a.getFloat(R.styleable.SimpleLineChart_bottomInset, 10.0f);
            mRightInset = a.getFloat(R.styleable.SimpleLineChart_rightInset, 5.0f);
            mLeftInset = a.getFloat(R.styleable.SimpleLineChart_leftInset, 0.0f);
            mPointRadius = a.getFloat(R.styleable.SimpleLineChart_pointRadius, 3.0f);
            mLineWidth = a.getFloat(R.styleable.SimpleLineChart_lineWidth, 1.0f);
            mGridLineWidth = a.getFloat(R.styleable.SimpleLineChart_gridLineWidth, 1.0f);
            mYAxisGap = a.getFloat(R.styleable.SimpleLineChart_yAxisGap, 10.0f);
            mXAxisGap = a.getFloat(R.styleable.SimpleLineChart_xAxisGap, 10.0f);
            mAxisTextSize = a.getFloat(R.styleable.SimpleLineChart_axisTextSize, 30.0f);
            mInterval = a.getInteger(R.styleable.SimpleLineChart_interval, 10);
            mDelay = a.getInteger(R.styleable.SimpleLineChart_delay, 30);
            mLineColor = a.getColor(R.styleable.SimpleLineChart_lineColor, 0xFF47b7bF);
            mGridLineColor = a.getColor(R.styleable.SimpleLineChart_gridLineColor, Color.BLACK);
            mBackgroundColor = a.getColor(R.styleable.SimpleLineChart_backgroundColor, 0xff000000);
            mYAxisFormatter = a.getString(R.styleable.SimpleLineChart_yAxisFormatter);
            mBackgroundColor = a.getColor(R.styleable.SimpleLineChart_backgroundColor, 0xffffffff);

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        setFocusable(true);
    }

    /**
     * Resets the state of the line chart.
     */
    public void resetState() {
        for (int x = 0; x < yValues.length; x++) {
            yValuesState[x] = 0;
        }
        mIntervalState = 0;
    }

    @Override
    public void run() {
        // Update state of what we draw
        mIntervalState++;
        for (int x = 0; x < yValues.length; x++) {
            yValuesState[x] = yValues[x] / mInterval * mIntervalState;
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mWidth = xNew;
        mHeight = yNew;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();


        final float topInset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTopInset, metrics);
        final float bottomInset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBottomInset, metrics);
        final float rightInset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRightInset, metrics);
        final float leftInset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLeftInset, metrics);
        final float pointRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPointRadius, metrics);
        final float lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLineWidth, metrics);
        final float gridLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mGridLineWidth, metrics);
        final float yAxisGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mYAxisGap, metrics);
        final float xAxisGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mXAxisGap, metrics);

        // paint background color
        canvas.drawColor(mBackgroundColor);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(gridLineWidth);
        p.setColor(mGridLineColor);
        p.setTextSize(mAxisTextSize);
        p.setStyle(Paint.Style.FILL);

        String maxYstr = String.format(mYAxisFormatter, maxY);
        Rect maxYRectBounds = new Rect(0, 0, 0, 0);
        p.getTextBounds(maxYstr, 0, maxYstr.length(), maxYRectBounds);

        final float xAxisHeight = bottomInset + maxYRectBounds.height() + xAxisGap;
        final float yAxisWidth = rightInset + maxYRectBounds.width() + yAxisGap;
        final float chartCanvasWidth = mWidth - leftInset - yAxisWidth;
        final float chartCanvasHeight = mHeight - topInset - xAxisHeight;
        final float yAxisInterval = (chartCanvasHeight) / mYNumOfIntervals;
        final float yValueInterval = (maxY - minY) / mYNumOfIntervals;
        final float midTextHeight = maxYRectBounds.height() / 2.0f;

        // draw y-axis grid lines and labels
        for (int x = 0; x < mYNumOfIntervals + 1; x++) {
            // draw y-axis label
            canvas.drawText(String.format(mYAxisFormatter, maxY - x * yValueInterval), rightInset, topInset + (x * yAxisInterval) + midTextHeight, p);

            // draw grid line
            canvas.drawLine(yAxisWidth, topInset + (x * yAxisInterval), chartCanvasWidth, topInset + (x * yAxisInterval), p);
        }

        float xAxisInterval = chartCanvasWidth / xValues.length;

        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(lineWidth);

        // draw points
        float prevX = 0;
        float prevY = 0;
        for (int x = 0; x < yValuesState.length; x++) {
            float y = (chartCanvasHeight) / (maxY - minY) * (maxY - yValuesState[x]);
            float currentX = yAxisWidth + x * xAxisInterval;
            float currentY = topInset + y;
            p.setColor(mLineColor);
            canvas.drawCircle(currentX, currentY, pointRadius, p);
            if (x != 0) {
                canvas.drawLine(prevX, prevY, currentX, currentY, p);
            }

            Rect textBounds = new Rect(0, 0, 0, 0);
            p.getTextBounds(maxYstr, 0, maxYstr.length(), textBounds);

            p.setColor(mGridLineColor);
            // draw x-axis label
            canvas.drawText(xValues[x], currentX - textBounds.width() / 2.0f, topInset + chartCanvasHeight + xAxisGap + textBounds.height(), p);

            prevX = currentX;
            prevY = currentY;
        }

        // draw inner point circle
        p.setColor(mBackgroundColor);
        for (int x = 0; x < yValuesState.length; x++) {
            float y = (chartCanvasHeight) / (maxY - minY) * (maxY - yValuesState[x]);
            float currentX = yAxisWidth + x * xAxisInterval;
            float currentY = topInset + y;
            canvas.drawCircle(currentX, currentY, pointRadius - lineWidth, p);
        }

        if (mIntervalState < mInterval) {
            postDelayed(this, mDelay);
        }
    }

    public void setYValues(float[] values) {
        assert values != null;
        assert values.length > 0;
        this.yValues = values;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setXValues(String[] values) {
        assert values != null;
        assert values.length > 0;
        this.xValues = values;
    }

    public void setDelay(int mDelay) {
        this.mDelay = mDelay;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }
}
