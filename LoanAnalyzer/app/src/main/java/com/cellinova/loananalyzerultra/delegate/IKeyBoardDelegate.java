/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.delegate;

import android.view.View;
import android.view.View.OnClickListener;

import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.NumberFormatObject;

public interface IKeyBoardDelegate {
    void setStatusButtonListener(OnClickListener statusButtonListener);

    int getTitle();

    FormatObject getFormatObject();

    void addButtonListeners(View view);

    void setValue(String value);

    void setFormatObject(NumberFormatObject formatObject);

    void setProgressDelegate(IProgressDelegate progressDelegate);

    void setPrecision(double precision);

    void setMaxValue(int maxValue);

}
