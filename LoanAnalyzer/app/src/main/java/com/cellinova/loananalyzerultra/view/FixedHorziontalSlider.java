/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.Format;

public class FixedHorziontalSlider extends HorizontalSlider {

    public FixedHorziontalSlider(Context context) {
        super(context);
    }

    public FixedHorziontalSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void updateMinMaxLabels() {

        if ((getMax() == this.getProgress()) || (0 == this.getProgress())) {
            double newMax = 0;
            double newMin = 0;

            double sliderValue = trueValue() + minValue;
            // check if slider is at maximum amount
            if (getMax() == this.getProgress()) {
                newMax = (trueValue(getMax()) - minValue) * 1.5 + minValue;
                newMin = (trueValue(getMax()) - minValue) * .5 + minValue;
            } else if (0 == this.getProgress()) {
                newMax = maxValue - (trueValue(getMax()) - minValue) * .5;
                newMin = minValue - (trueValue(getMax()) - minValue) * .5;
            }
            if ((newMin >= 0) && (newMax <= 100)) {
                minValue = newMin;
                maxValue = newMax;

                this.setProgress(getMax() / 2);
                // this.getProgress() = ((int)(getMax()/2+.5f));

                // format string
                maxLbl.setText(labelFormatter.format(
                        maxValue / 100));
                minLbl.setText(labelFormatter.format(
                        minValue / 100));

            }
        }

    }

    protected void initializeSliderMinMax(double initValue) {
        minValue = Math.floor((initValue - 10) / 10) * 10;
        if (minValue < 0) minValue = 0;
        maxValue = minValue + 20;
        maxLbl.setText(labelFormatter.format(maxValue / 100));
        minLbl.setText(labelFormatter.format(minValue / 100));
        this.setProgress((int) ((initValue - minValue) * increment));

    }

    public double trueValue(double value) {
        return ((int) (value)) / increment + minValue;
    }

    public double trueValue() {
        return ((int) (this.getProgress())) / increment + minValue;
    }


}
