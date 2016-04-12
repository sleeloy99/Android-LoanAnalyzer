/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleElement {
    protected Date paymentDate;
    protected double interestAmount;
    protected double pricipalAmount;
    protected double balance;
    protected double lumpsum;
    protected boolean isEmpty;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();

    public ScheduleElement() {
        isEmpty = true;
        pricipalAmount = 0;
        interestAmount = 0;
        balance = 0;
        lumpsum = 0;
    }


    public ScheduleElement(double interestVal, double pricipalValue, double balanceValue, double lumpsumValue) {
        super();
        isEmpty = false;
        pricipalAmount = pricipalValue;
        interestAmount = interestVal;
        balance = balanceValue;
        lumpsum = lumpsumValue;
    }

    public double getInterestAmt() {
        return interestAmount;
    }

    public double getPrincipalAmt() {
        return pricipalAmount;
    }

    public double getBalanceAmt() {
        return balance;
    }

    public String getMonthDisplay() {
        if (this.paymentDate == null)
            return "";
        SIMPLE_DATE_FORMAT.applyLocalizedPattern("MMM");
        return SIMPLE_DATE_FORMAT.format(paymentDate);
    }

    public String getMonthYearDisplay() {
        if (this.paymentDate == null)
            return "";
        SIMPLE_DATE_FORMAT.applyLocalizedPattern("MMM yyyy");
        return SIMPLE_DATE_FORMAT.format(paymentDate);
    }

    public String getDateDisplay() {
        if (this.paymentDate == null)
            return "";
        SIMPLE_DATE_FORMAT.applyLocalizedPattern("MMM dd");
        return SIMPLE_DATE_FORMAT.format(paymentDate);
    }

    public String getShortDateDisplay() {
        if (this.paymentDate == null)
            return "";
        SIMPLE_DATE_FORMAT.applyLocalizedPattern("dd/MM/yy");
        return SIMPLE_DATE_FORMAT.format(paymentDate);
    }

    public double interestPercent() {
        if (isEmpty) return 0;
        return interestAmount / pricipalAmount;
    }

    public String getPrincipalAmountDisplay() {
        if (isEmpty) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(pricipalAmount);
    }

    public String getInterestAmountDisplay() {
        if (isEmpty) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(interestAmount);
    }

    public String getBalanceDisplay() {
        if (isEmpty) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(balance);
    }

    public String getLumpSumDisplay() {
        if ((isEmpty) || (lumpsum == 0)) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(lumpsum);
    }


    public Date getPaymentDate() {
        return paymentDate;
    }


    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }


    public double getInterestAmount() {
        return interestAmount;
    }


    public void setInterestAmount(double interestAmount) {
        this.interestAmount = interestAmount;
    }


    public void setPricipalAmount(double pricipalAmount) {
        this.pricipalAmount = pricipalAmount;
    }


    public double getBalance() {
        return balance;
    }


    public void setBalance(double balance) {
        this.balance = balance;
    }


    public double getLumpsum() {
        return lumpsum;
    }


    public void setLumpsum(double lumpsum) {
        this.lumpsum = lumpsum;
    }


    public boolean isEmpty() {
        return isEmpty;
    }


    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }


    @Override
    public String toString() {
        return pricipalAmount + " " + interestAmount + " " + balance + " ";
    }


}
