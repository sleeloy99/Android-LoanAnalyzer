
/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewWrapper {
    TextView title = null;
    TextView loanAmount = null;
    TextView amortization = null;
    TextView paymentAmount = null;
    ViewGroup base;
    View guts;

    public ViewWrapper(ViewGroup base) {
        this.base = base;
    }

    public TextView getTitle() {
        if (title == null) {
            title = (TextView) base.getChildAt(0);
        }
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getLoanAmount() {
        if (loanAmount == null) {
            loanAmount = (TextView) base.getChildAt(1);
        }
        return loanAmount;
    }

    public void setLoanAmount(TextView loanAmount) {
        this.loanAmount = loanAmount;
    }

    public TextView getAmortization() {
        if (amortization == null) {
            amortization = (TextView) ((ViewGroup) base.getChildAt(2)).getChildAt(0);
        }
        return amortization;
    }

    public void setAmortization(TextView amortization) {
        this.amortization = amortization;
    }

    public TextView getPaymentAmount() {
        if (paymentAmount == null) {
            paymentAmount = (TextView) ((ViewGroup) base.getChildAt(2)).getChildAt(1);
        }
        return paymentAmount;
    }

    public void setPaymentAmount(TextView paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

}
