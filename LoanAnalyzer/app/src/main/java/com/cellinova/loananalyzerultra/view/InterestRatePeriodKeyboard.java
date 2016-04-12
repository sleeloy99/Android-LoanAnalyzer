/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.persist.ModelObject;

public class InterestRatePeriodKeyboard extends NumberKeyboard {

    private RadioButton mMonthly;
    private RadioButton mAnnual;
    private RadioButton mSemiAnnual;

    public InterestRatePeriodKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getSliderId() {
        return R.layout.interestratepanel;
    }

    @Override
    protected int getSeekBarId() {
        return R.id.interestRateSeekBar;
    }

    @Override
    protected void init() {
        super.init();
        mMonthly = (RadioButton) mSliderPanel.findViewById(R.id.monthly);
        mAnnual = (RadioButton) mSliderPanel.findViewById(R.id.annually);
        mSemiAnnual = (RadioButton) mSliderPanel.findViewById(R.id.semiannual);

    }

    @Override
    public void updateSlider() {
        super.updateSlider();
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
        RadioButton radioBtn = mMonthly;

        switch (loanObject.getCompoundPeriod().getCompoundValue()) {
            case CANNUAL:
                radioBtn = mAnnual;
                break;
            case CSEMIANNUAL:
                radioBtn = mSemiAnnual;
                break;
        }

        radioBtn.setChecked(true);

    }

}

