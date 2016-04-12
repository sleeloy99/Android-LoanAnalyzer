/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DiffArrowViewObserver extends DiffTextViewObserver {

    public DiffArrowViewObserver(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void updateText(double diff, FormatObject formatObject) {
        if (diff > -0.01 && diff < 0.01) {
            setText("");
        } else if (diff < 0) {
            setText("\uf063");
        } else {
            setText("\uf062");
        }
    }
}
