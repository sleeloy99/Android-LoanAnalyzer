/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public class AmortizationObj implements FormatObject {
    private int months;
    private int years;
    private int channelid;

    public AmortizationObj(int yearsAttr, int monthsAttr, int channelid) {
        years = yearsAttr;
        months = monthsAttr;
        this.channelid = channelid;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        if (this.months != months) {
            this.months = months;
            MessageChannel.getInstance().publish(this, channelid);
        }
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        if (this.years != years) {
            this.years = years;
            MessageChannel.getInstance().publish(this, channelid);
        }
    }

    @Override
    public String displayDiffString(FormatObject original) {
        double diff = getDiff(original);

        double dyear = 0;
        double value = diff / 12.0f;
        if (value < 0)
            dyear = Math.ceil(value);
        else
            dyear = Math.floor(value);
        double dmonth = (value - dyear) * 12;
        int diffY = (int) dyear;
        int diffM = (int) dmonth;
        if ((diffY == 0) && (diffM != 0)) {
            if (diffM > 0)
                return String.format("+%d Months", diffM);
            return String.format("-%d Months", diffM);
        } else if (diffY != 0) {
            if (diffM > 0)
                return String.format("+%d Years %d Months", diffY, diffM);
            return String.format("%d Years %d Months", diffY, diffM);
        }
        return "";

    }

    public String displayStringShort() {
        return getYears() + "Y " + getMonths() + "M";

    }

    @Override
    public String displayString() {
        return getYears() + "Years " + getMonths() + "Months";
    }


    @Override
    public double getDiff(FormatObject original) {
        return years * 12 + months - (((AmortizationObj) original).getYears() * 12 + ((AmortizationObj) original).getMonths());
    }

}