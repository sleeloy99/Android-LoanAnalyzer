/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.LoanType;
import com.cellinova.loananalyzerultra.model.PaymentFreqEnum;
import com.cellinova.loananalyzerultra.persist.ModelObject;

public class NumberPeriodKeyboard extends NumberKeyboard {

    private RadioButton mMonthly;
    private RadioButton mBiweekly;
    private RadioButton mBimonthly;
    private RadioButton mWeekly;

    public NumberPeriodKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getSliderId() {
        return R.layout.paymentamountpanel;
    }

    @Override
    protected int getSeekBarId() {
        return R.id.paymentamountSeekBar;
    }

    @Override
    protected void init() {
        super.init();
        mMonthly = (RadioButton) mSliderPanel.findViewById(R.id.monthly);
        mBimonthly = (RadioButton) mSliderPanel.findViewById(R.id.bimonthly);
        mWeekly = (RadioButton) mSliderPanel.findViewById(R.id.weekly);
        mBiweekly = (RadioButton) mSliderPanel.findViewById(R.id.biweekly);

    }

    @Override
    public void updateSlider() {
        super.updateSlider();
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
        RadioButton radioBtn = mBiweekly;

        switch (loanObject.getPaymentFreq().getFreqValue()) {
            case MONTHLY:
                radioBtn = mMonthly;
                break;
            case BIMONTHLY:
                radioBtn = mBimonthly;
                break;
            case WEEKLY:
                radioBtn = mWeekly;
                break;
        }

        radioBtn.setChecked(true);

    }

}

