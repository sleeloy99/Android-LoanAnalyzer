/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class PaymentFreqObj implements FormatObject {
    PaymentFreqEnum value;
    int channelid;

    public PaymentFreqObj(PaymentFreqEnum paymentFreq, int channelid) {
        value = paymentFreq;
        this.channelid = channelid;

    }

    public void setFreqValue(PaymentFreqEnum freqValue) {
        if (value != freqValue) {
            value = freqValue;
            MessageChannel.getInstance().publish(this, channelid);
        }
    }

    public PaymentFreqEnum getFreqValue() {
        return value;
    }

    @Override
    public String displayDiffString(FormatObject original) {
        return null;
    }

    @Override
    public String displayString() {
        switch (value) {
            case MONTHLY:
                return "Monthly";
            case BIMONTHLY:
                return "Bi-Monthly";
            case WEEKLY:
                return "Weekly";
            default:
                return "Bi-Weekly";
        }
    }

    @Override
    public double getDiff(FormatObject original) {
        return 0;
    }

}