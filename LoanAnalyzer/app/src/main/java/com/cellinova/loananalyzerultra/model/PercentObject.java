/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.text.NumberFormat;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class PercentObject implements NumberFormatObject {
    double value;
    int channelid;
    final int PRECISION = 4;

    public PercentObject(double percent, int channelid) {
        value = percent;
        this.channelid = channelid;

    }

    public void setValue(double percent) {
        if (value != percent) {
            value = percent;
            MessageChannel.getInstance().publish(this, channelid);
        }
    }

    public double getValue() {
        return value;
    }

    @Override
    public String displayDiffString(FormatObject original) {
        double diff = value - ((PercentObject) original).getValue();
        if (diff == 0)
            return "";
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(PRECISION);
        if (diff < 0)
            return nf.format(diff / 100);
        return "+" + nf.format(diff / 100);
    }

    @Override
    public String displayString() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(PRECISION);
        return nf.format(value / 100);
    }

    @Override
    public double getDiff(FormatObject original) {
        return value - ((PercentObject) original).getValue();
    }


}
