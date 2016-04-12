/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.controller;

import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.LumpSum;
import com.cellinova.loananalyzerultra.view.NumberKeyboard;
public class LumpSumController {

    private ViewGroup mContainer;
    public LumpSumController(ViewGroup container) {
        mContainer = container;
    }

    public void onCreate(LumpSum lumpSum) {
        TextView lumpSumAmt = (TextView)mContainer.findViewById(R.id.lumpSum);
        lumpSumAmt.setText(lumpSum.getValue().displayString());
        TextView lumpSumPeriod = (TextView)mContainer.findViewById(R.id.lumpSumPeriod);
        lumpSumPeriod.setText(lumpSum.getLumpsumPeriod().displayString());
        TextView lumpStartDate = (TextView)mContainer.findViewById(R.id.lumpSumStartDate);
        lumpStartDate.setText(lumpSum.getDuration().getStartDate().displayString());
        TextView lumpEndDate = (TextView)mContainer.findViewById(R.id.lumpSumEndDate);
        lumpEndDate.setText(lumpSum.getDuration().getEndDate().displayString());
        NumberKeyboard lumpSumAmtKeyboard = (NumberKeyboard)mContainer.findViewById(R.id.lumpSumAmtKeyboard);
        lumpSumAmtKeyboard.updateSlider();

        RadioButton radioButton = null;
        switch (lumpSum.getLumpsumPeriod().getLumpSumValue()) {
            case LUMPANNUAL:
                radioButton = (RadioButton) mContainer.findViewById(R.id.lumpSumAnnual);
                break;
            case LUMPMONTHLY:
                radioButton = (RadioButton) mContainer.findViewById(R.id.lumpSumMonthly);
                break;
            default:
                radioButton = (RadioButton) mContainer.findViewById(R.id.lumpsumNone);
                break;
        }
        radioButton.setChecked(true);

    }
}
