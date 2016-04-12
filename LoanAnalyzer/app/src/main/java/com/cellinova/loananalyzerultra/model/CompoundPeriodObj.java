/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public class CompoundPeriodObj implements FormatObject {
    CompoundPeriodEnum value;
    protected int channelid;


    public CompoundPeriodObj(CompoundPeriodEnum compoundPeriod, int channelid) {
        value = compoundPeriod;
        this.channelid = channelid;
    }

    public void setCompoundValue(CompoundPeriodEnum compoundValue) {
        if (value != compoundValue) {
            value = compoundValue;
            MessageChannel.getInstance().publish(this, channelid);
        }
    }

    public CompoundPeriodEnum getCompoundValue() {
        return value;

    }

    @Override
    public String displayDiffString(FormatObject original) {
        return null;
    }

    @Override
    public String displayString() {
        switch (value) {
            case CMONTHLY:
                return "Monthly";
            case CANNUAL:
                return "Annually";
            default:
                return "Semi-Anual";
        }
    }

    @Override
    public double getDiff(FormatObject original) {
        return 0;
    }
}