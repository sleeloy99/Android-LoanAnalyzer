/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public class NotifyPaymentAmt extends DollarObject {

    public NotifyPaymentAmt(double dollar, int channelid) {
        super(dollar, channelid);
    }

    public String monthlyAmtDisplayStringShort(PaymentFreqEnum paymentFreq) {
        return null;

    }

    public String monthlyAmtDisplayString(PaymentFreqEnum paymentFreq) {
        return null;

    }

    public double monthlyAmt(PaymentFreqEnum paymentFreq) {
        return 0;

    }
}
