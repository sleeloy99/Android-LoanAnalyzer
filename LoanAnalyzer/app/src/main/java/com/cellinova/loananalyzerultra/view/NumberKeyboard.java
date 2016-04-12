/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.delegate.IKeyBoardDelegate;
import com.cellinova.loananalyzerultra.delegate.IProgressDelegate;
import com.cellinova.loananalyzerultra.delegate.NumKeyPadDelegate;
import com.cellinova.loananalyzerultra.model.LoanFormat;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.LoanType;
import com.cellinova.loananalyzerultra.model.LumpSum;
import com.cellinova.loananalyzerultra.model.NumberFormatObject;
import com.cellinova.loananalyzerultra.persist.ModelObject;

import java.text.Format;
import java.text.NumberFormat;

public class NumberKeyboard extends LinearLayout implements IProgressDelegate {

    protected IKeyBoardDelegate mKeyBoardDelegate;
    protected String mTitle;
    protected LoanType mType;
    protected TextView mTitleView;
    protected LoanFormat mFormat;
    protected HorizontalSlider mSlider;
    protected ViewGroup mSliderPanel;

    public NumberKeyboard(Context context) {
        super(context);
        init();
        updateSlider();
    }

    public NumberKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NumberKeyboard,
                0, 0
        );


        try {
            mTitle = a.getString(R.styleable.NumberKeyboard_title);
            int t = a.getType(R.styleable.NumberKeyboard_type);
            TypedValue tv = a.peekValue(R.styleable.NumberKeyboard_type);
            mType = LoanType.valueOf(a.getInteger(R.styleable.NumberKeyboard_type, LoanType.LOANAMT.getValue()));
            mFormat = LoanFormat.valueOf(a.getInteger(R.styleable.NumberKeyboard_format, LoanFormat.DOUBLE.getValue()));

        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        mKeyBoardDelegate = new NumKeyPadDelegate();
        init();
        updateSlider();
    }

    protected int getSliderId() {
        return R.layout.sliderpanel;
    }

    protected int getSeekBarId() {
        return R.id.loanAmtSeekBar;
    }

    public void setTitleViewOnClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    protected void init() {
        inflate(getContext(), R.layout.numberslider, this);


        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(mTitle);

        final AutoTransition transition = new AutoTransition();
        transition.setDuration(100);
        transition.setInterpolator(new AccelerateDecelerateInterpolator());

        LayoutInflater factory = LayoutInflater.from(getContext());

        FrameLayout baseLayout = (FrameLayout) findViewById(R.id.keypadrootpanel);

        mSliderPanel = (ViewGroup) factory.inflate(getSliderId(), baseLayout, false);
        // set up min max
        mSlider = (HorizontalSlider) mSliderPanel.findViewById(getSeekBarId());
        TextView minLbl = (TextView) mSliderPanel.findViewById(R.id.minLbl);
        TextView maxLbl = (TextView) mSliderPanel.findViewById(R.id.maxLbl);
        mSlider.setMinLbl(minLbl);
        mSlider.setMaxLbl(maxLbl);

        switch (mFormat) {
            case DOUBLE:
                mSlider.setLabelFormatter(NumberFormat.getCurrencyInstance());
                mSlider.setMax(200);
                break;
            case PERCENTAGE:
                mSlider.setLabelFormatter(NumberFormat.getPercentInstance());
                mSlider.setIncrement(4);
                mSlider.setMax(80);
                break;
        }

        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int arg1, boolean arg2) {
                LoanObject loan = ModelObject.getInstance().getSelectedObject();
                NumberFormatObject initValue = null;
                switch (mType) {
                    case LOANAMT:
                        loan.getLoanAmount().setValue(((HorizontalSlider) seekbar).trueValue());
                        initValue = ModelObject.getInstance().getSelectedObject().getLoanAmount();
                        break;
                    case PAYMENTAMT:
                        loan.getPaymentAmount().setValue(((HorizontalSlider) seekbar).trueValue());
                        initValue = ModelObject.getInstance().getSelectedObject().getPaymentAmount();
                        break;
                    case INTERESTRATE:
                        loan.getInterestRate().setValue(((HorizontalSlider) seekbar).trueValue());
                        initValue = ModelObject.getInstance().getSelectedObject().getInterestRate();
                        break;
                    case LUMPSUM:
                        LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
                        if (lumpSum != null) {
                            lumpSum.getValue().setValue(((HorizontalSlider) seekbar).trueValue());
                            initValue = ModelObject.getInstance().getSelectedLumpSump().getValue();
                        }
                        break;
                }
                if (initValue != null) {
                    mKeyBoardDelegate.setFormatObject(initValue);
                    mKeyBoardDelegate.setValue(initValue.displayString());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calculateLoan();
            }

        });


        ViewGroup calculatorPanel = (ViewGroup) factory.inflate(R.layout.calculator, baseLayout, false);
        mKeyBoardDelegate.addButtonListeners(calculatorPanel);
        mKeyBoardDelegate.setProgressDelegate(this);
        if (mFormat == LoanFormat.PERCENTAGE) {
            mKeyBoardDelegate.setPrecision(10000);
            mKeyBoardDelegate.setMaxValue(100);
        }

        final Scene calculatorScene = new Scene(baseLayout, calculatorPanel);
        final Scene sliderScene = new Scene(baseLayout, mSliderPanel);
        TransitionManager.go(sliderScene, transition);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
        final Switch switchBtn = (Switch) findViewById(R.id.switchkeypad);
        switchBtn.setSwitchTypeface(font);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    TransitionManager.go(calculatorScene, transition);
                } else {
                    TransitionManager.go(sliderScene, transition);
                }

            }
        });

    }

    @Override
    public void onProgressChanged(double value) {

    }

    @Override
    public void onProgressDone() {
        updateSlider();
        calculateLoan();
    }

    public void updateSlider() {
        NumberFormatObject initValue = null;

        switch (mType) {
            case LOANAMT:
                initValue = ModelObject.getInstance().getSelectedObject().getLoanAmount();
                mSlider.setControls(initValue.getValue());
                break;
            case PAYMENTAMT:
                initValue = ModelObject.getInstance().getSelectedObject().getPaymentAmount();
                mSlider.setControls(initValue.getValue());
                break;
            case INTERESTRATE:
                initValue = ModelObject.getInstance().getSelectedObject().getInterestRate();
                mSlider.setControls(initValue.getValue());
                break;
            case LUMPSUM:
                if (ModelObject.getInstance().getSelectedLumpSump() != null) {
                    initValue = ModelObject.getInstance().getSelectedLumpSump().getValue();
                    mSlider.setControls(initValue.getValue());
                }
                break;
        }
        if (initValue != null) {
            mKeyBoardDelegate.setFormatObject(initValue);
            mKeyBoardDelegate.setValue(initValue.displayString());
        }
    }

    private void calculateLoan() {
        LoanObject loan = ModelObject.getInstance().getSelectedObject();
        if (mType != LoanType.PAYMENTAMT) {
            loan.calculatePaymentAmt();
        } else {
            loan.calculateAmortization();
        }
        loan.calculatePrincipalInterestAmt();
    }

}
