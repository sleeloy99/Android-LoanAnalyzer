/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class DateObject implements FormatObject {
    Date value;
    protected int channelid;

    public String getSQLDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyLocalizedPattern("yyyy-MM-dd");
        return sdf.format(value);
    }

    public Date getDateValue() {
        return value;
    }

    public DateObject(Date dateVal, int channelid) {
        value = dateVal;
        this.channelid = channelid;
    }

    public DateObject(String dateVal, int channelid) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyLocalizedPattern("yyyy-MM-dd");
        this.channelid = channelid;
        try {
            value = sdf.parse(dateVal);
        } catch (Exception e) {

        }

    }

    public void setDateValue(Date dateVal) {
        if ((value != null) && (!value.equals(dateVal))) {
            value = dateVal;
            MessageChannel.getInstance().publish(this, channelid);
        } else {
            value = dateVal;
        }
    }

    @Override
    public String displayDiffString(FormatObject original) {
        return null;
    }

    @Override
    public String displayString() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyLocalizedPattern("yyyy-MM-dd");
        return sdf.format(value);
    }

    @Override
    public double getDiff(FormatObject original) {
        return 0;
    }

}