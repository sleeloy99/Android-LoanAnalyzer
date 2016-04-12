/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.Format;

public class HorizontalSlider extends SeekBar {

    protected double minLimit;
    protected double maxLimit;
    protected double minValue;
    protected double maxValue;
    protected double increment;
    protected double numberValue;
    protected TextView maxLbl, minLbl;
    protected Format labelFormatter;

    public HorizontalSlider(Context context) {
        super(context);
    }

    public HorizontalSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public double trueValue() {
        return ((int) (this.getProgress() + .5f)) * increment + minValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        super.onTouchEvent(event);
        if (action == MotionEvent.ACTION_UP) {
            updateMinMaxLabels();
        }


        return true;

    }

    protected void updateMinMaxLabels() {

        if ((getMax() == this.getProgress()) || (0 == this.getProgress())) {
            double newMax = 0;
            double newMin = 0;

            double sliderValue = this.getProgress() * increment + minValue;
            // check if slider is at maximum amount
            if (getMax() == this.getProgress()) {
                newMax = getMax() * increment * 1.5 + minValue;
                newMin = getMax() * increment * .5 + minValue;
            } else if (0 == this.getProgress()) {
                newMax = maxValue - getMax() * increment * .5;
                newMin = minValue - getMax() * increment * .5;
            }
            if (newMin >= 0) {
                if (newMin < minLimit) {
                    double tmpMinValue = Math.floor(minLimit);
                    minValue = tmpMinValue;
                    maxValue = tmpMinValue + increment * getMax();
                } else if ((maxLimit > 0) && (newMax > maxLimit)) {
                    maxValue = Math.floor(maxLimit);
                    minValue = maxValue - increment * getMax();
                } else {
                    minValue = newMin;
                    maxValue = newMax;
                }

                this.setProgress((int) ((sliderValue - minValue) * getMax()
                        / (maxValue - minValue) + .5f));
                // this.getProgress() = ((int)(getMax()/2+.5f));

                // format string
                maxLbl.setText(labelFormatter.format(
                        maxValue));
                minLbl.setText(labelFormatter.format(
                        minValue));

            }
        }

    }

    public void setControls(double initValue) {
        this.initializeSliderMinMax(initValue);
    }

    protected void initializeSliderMinMax(double initValue) {
        increment = 1;
        double tmpLoanValue = initValue;
        double tmpValue = tmpLoanValue;
        while (tmpValue >= 1000) {
            increment *= 10;
            tmpValue = tmpValue / 10.0f;
        }
        double tmpMinValue = 0.0;
        increment = increment / 2.0f;
        while (tmpMinValue < tmpLoanValue) {
            tmpMinValue = tmpMinValue + increment * getMax();
        }

        if (tmpMinValue != 0)
            tmpMinValue = tmpMinValue - increment * getMax();
        if (tmpMinValue < minLimit) {
            tmpMinValue = Math.floor(minLimit);
            minValue = tmpMinValue;
            maxValue = tmpMinValue + increment * getMax();
        } else if ((maxLimit > 0)
                && ((tmpMinValue + increment * getMax()) > maxLimit)) {
            maxValue = Math.floor(maxLimit);
            minValue = maxValue - increment * getMax();
        } else {
            minValue = tmpMinValue;
            maxValue = tmpMinValue + increment * getMax();
        }

        this.setProgress((int) ((tmpLoanValue - minValue) * getMax()
                / (maxValue - minValue) + .5f));

        // format string

        maxLbl.setText(labelFormatter.format(maxValue));
        minLbl.setText(labelFormatter.format(minValue));

    }

    public double getIncrement() {
        return increment;
    }


    public void setIncrement(double increment) {
        this.increment = increment;
    }

    public void setMaxLbl(TextView maxLbl) {
        this.maxLbl = maxLbl;
    }

    public void setMinLbl(TextView minLbl) {
        this.minLbl = minLbl;
    }

    public void setLabelFormatter(Format labelFormatter) {
        this.labelFormatter = labelFormatter;
    }
}