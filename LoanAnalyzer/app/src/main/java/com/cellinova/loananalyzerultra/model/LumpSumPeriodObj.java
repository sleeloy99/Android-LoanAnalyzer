/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class LumpSumPeriodObj implements FormatObject {
    LumpSumPeriodEnum value;
    protected int channelid;

    public LumpSumPeriodObj(LumpSumPeriodEnum lumpSumPeriod, int channelid) {
        value = lumpSumPeriod;
        this.channelid = channelid;
    }

    public void setLumpSumValue(LumpSumPeriodEnum lumpSumValue) {
        if (value != lumpSumValue) {
            value = lumpSumValue;
            MessageChannel.getInstance().publish(this, channelid);
        }

    }

    public LumpSumPeriodEnum getLumpSumValue() {
        return value;
    }

    @Override
    public String displayDiffString(FormatObject original) {
        return null;
    }

    @Override
    public String displayString() {
        switch (value) {
            case LUMPMONTHLY:
                return "Monthly";
            case LUMPANNUAL:
                return "Annually";
            default:
                return "None";
        }
    }

    @Override
    public double getDiff(FormatObject original) {
        return 0;
    }

}
