/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.Message;

import java.util.Observable;

public class DiffTextViewObserver extends TextViewObserver {
    protected IGetOriginalLoan original;

    public DiffTextViewObserver(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void update(Observable observable, Object data) {
        if (original == null) {
            return;
        }
        if (data instanceof Message) {
            Message messageObj = (Message) data;
            if (this.mChannelIds.contains(messageObj.getChannelid())) {

                if (messageObj.getMessage() instanceof FormatObject) {
                    FormatObject formatObject = (FormatObject) messageObj.getMessage();
                    updateText(formatObject);
                }
            }
        }
    }

    public void updateText(FormatObject formatObject) {
        double diff = formatObject.getDiff(original.getFormatObject());
        if (diff < 0)
            setTextColor(0xFF006400);
        else
            setTextColor(Color.RED);

        updateText(diff, formatObject);
    }

    protected void updateText(double diff, FormatObject formatObject) {
        if (diff > -0.01 && diff < 0.01) {
            setText("");
        } else {
            setText(formatObject.displayDiffString(original.getFormatObject()));
        }

    }

    public IGetOriginalLoan getOriginal() {
        return original;
    }

    public void setOriginal(IGetOriginalLoan original) {
        this.original = original;
    }
}
