/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.delegate;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.NumberFormatObject;

public class NumKeyPadDelegate implements IKeyBoardDelegate {

    protected TextView screen;
    protected OnClickListener statusButtonListener;
    protected Button title;
    protected double numberValue;
    protected NumberFormatObject formatObject;
    protected IProgressDelegate progressDelegate;
    protected double precision = 100.0f;
    protected int maxValue = -1;

    public OnClickListener getStatusButtonListener() {
        return statusButtonListener;
    }

    public void setStatusButtonListener(OnClickListener statusButtonListener) {
        this.statusButtonListener = statusButtonListener;
    }

    @Override
    public void addButtonListeners(View view) {
        screen = (TextView) view.findViewById(R.id.screen);
        addListener(view, R.id.button7, 7);
        addListener(view, R.id.button8, 8);
        addListener(view, R.id.button9, 9);

        addListener(view, R.id.button4, 4);
        addListener(view, R.id.button5, 5);
        addListener(view, R.id.button6, 6);

        addListener(view, R.id.button1, 1);
        addListener(view, R.id.button2, 2);
        addListener(view, R.id.button3, 3);

        addListener(view, R.id.buttonClear, -2);
        addListener(view, R.id.button0, 0);
        addListener(view, R.id.buttonDel, -1);
    }

    protected void keyPressed(int value) {
        double oldValue = numberValue;
        if (value == -1) {
            double numValueLessOne = Math.floor(numberValue * precision / 10);
            numberValue = numValueLessOne / precision;
        } else if (value == -2) {
            numberValue = 0;
        } else {
            numberValue = ((numberValue * 10 * precision) + value) / precision;
        }

        if (maxValue != -1) {

            if (numberValue <= maxValue) {
                formatObject.setValue(numberValue);
                screen.setText(formatObject.displayString());
                //notify recalculation
                progressDelegate.onProgressDone();
            } else {
                numberValue = oldValue;
            }
        } else {
            formatObject.setValue(numberValue);
            screen.setText(formatObject.displayString());
            //notify recalculation
            progressDelegate.onProgressDone();

        }

    }

    protected void addListener(View view, int id, int value) {
        Button button = (Button) view.findViewById(id);

        final int val = value;
        OnClickListener buttonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(val);

            }
        };
        button.setOnClickListener(buttonListener);
    }

    @Override
    public FormatObject getFormatObject() {
        return formatObject;
    }

    public void setFormatObject(NumberFormatObject formatObject) {
        this.formatObject = formatObject;
        numberValue = formatObject.getValue();
        setValue(formatObject.displayString());

    }

    public int getTitle() {
        return 0;
    }


    public void setTitle(int titleVal) {
        this.title.setText(titleVal);
    }

    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        this.screen.setText(value);
    }

    public IProgressDelegate getProgressDelegate() {
        return progressDelegate;
    }

    public void setProgressDelegate(IProgressDelegate progressDelegate) {
        this.progressDelegate = progressDelegate;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

}
