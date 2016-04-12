/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.text.NumberFormat;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class DollarObject implements NumberFormatObject {
    protected double value;
    protected int channelid;

    public DollarObject(double dollar, int channelid) {
        value = dollar;
        this.channelid = channelid;
    }

    public void setValue(double dollar) {
        if (value != dollar) {
            value = dollar;
            MessageChannel.getInstance().publish(this, channelid);
        }

    }

    public double getValue() {
        return value;
    }

    @Override
    public String displayDiffString(FormatObject original) {
        double diff = value - ((DollarObject) original).getValue();
        String returnValue;

        if (diff == 0)
            returnValue = "";
        else if (diff < 0) {
            returnValue = "-" + NumberFormat.getCurrencyInstance().format(diff * (-1));
        } else {
            returnValue = "+" + NumberFormat.getCurrencyInstance().format(diff);
        }
        return returnValue;
    }

    @Override
    public String displayString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(value);
    }

    @Override
    public double getDiff(FormatObject original) {
        return value - ((DollarObject) original).getValue();
    }

}