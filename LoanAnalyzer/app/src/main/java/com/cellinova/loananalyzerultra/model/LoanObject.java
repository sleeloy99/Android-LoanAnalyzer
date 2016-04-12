/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.cellinova.loananalyzerultra.persist.ScheduleElement;

public class LoanObject implements Cloneable, Percentage {
    protected DollarObject loanAmount;
    protected PercentObject interestRate;
    protected AmortizationObj amortization;
    protected PaymentFreqObj paymentFreq;
    protected NotifyPaymentAmt paymentAmount;
    protected PercentObject ratePerPeriod;
    protected DollarObject principalAmt;
    protected DollarObject interestAmt;
    protected CompoundPeriodObj compoundPeriod;
    protected List<LumpSum> lumpSums;
    protected List<LumpSum> lumpSumsTmp;
    protected DateObject startDate;
    protected String name;
    protected long uid;

    public LoanObject() {
        setLoanAmount(new DollarObject(0, MessageChannel.LOANAMOUNT_CHANNEL));
        setRatePerPeriod(new PercentObject(0, MessageChannel.RATEPERPERIOD_CHANNEL));
        setInterestRate(new PercentObject(4, MessageChannel.INTERESTRATE_CHANNEL));
        setPaymentAmount(new NotifyPaymentAmt(-1, MessageChannel.PAYMENTAMOUNT_CHANNEL));
        setAmortization(new AmortizationObj(25, 0, MessageChannel.AMORTIZATION_CHANNEL));
        this.setPrincipalAmt(new DollarObject(-1, MessageChannel.PRINCIPALAMT_CHANNEL));
        this.setInterestAmt(new DollarObject(0, MessageChannel.INTERESTAMT_CHANNEL));
        this.setPaymentFreq(new PaymentFreqObj(PaymentFreqEnum.MONTHLY, MessageChannel.FREQPERIOD_CHANNEL));
        this.setCompoundPeriod(new CompoundPeriodObj(CompoundPeriodEnum.CMONTHLY, MessageChannel.COMPOUNDPERIOD_CHANNEL));
        lumpSums = new ArrayList<LumpSum>();
    }

    public Object clone() {
        LoanObject cloned = new LoanObject();
        cloned.uid = uid;
        cloned.loanAmount = new DollarObject(loanAmount.getValue(), MessageChannel.LOANAMOUNT_CHANNEL);
        cloned.interestRate = new PercentObject(interestRate.getValue(), MessageChannel.INTERESTRATE_CHANNEL);
        cloned.amortization = new AmortizationObj(amortization.getYears(), amortization.getMonths(), MessageChannel.AMORTIZATION_CHANNEL);
        cloned.paymentFreq = new PaymentFreqObj(paymentFreq.getFreqValue(), MessageChannel.FREQPERIOD_CHANNEL);
        cloned.paymentAmount = new NotifyPaymentAmt(paymentAmount.getValue(), MessageChannel.PAYMENTAMOUNT_CHANNEL);
        cloned.ratePerPeriod = new PercentObject(ratePerPeriod.getValue(), MessageChannel.RATEPERPERIOD_CHANNEL);
        cloned.principalAmt = new DollarObject(principalAmt.getValue(), MessageChannel.PRINCIPALAMT_CHANNEL);
        cloned.interestAmt = new DollarObject(interestAmt.getValue(), MessageChannel.INTERESTAMT_CHANNEL);
        cloned.compoundPeriod = new CompoundPeriodObj(compoundPeriod.getCompoundValue(), MessageChannel.COMPOUNDPERIOD_CHANNEL);
        cloned.startDate = new DateObject(startDate.getDateValue(), MessageChannel.STARTDATE_CHANNEL);
        cloned.lumpSums = lumpSums;
        cloned.name = name;
        return cloned;
    }

    public DollarObject getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(DollarObject loanAmount) {
        this.loanAmount = loanAmount;
    }

    public PercentObject getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(PercentObject interestRate) {
        this.interestRate = interestRate;
    }

    public AmortizationObj getAmortization() {
        return amortization;
    }

    public void setAmortization(AmortizationObj amortization) {
        this.amortization = amortization;
    }

    public PaymentFreqObj getPaymentFreq() {
        return paymentFreq;
    }

    public void setPaymentFreq(PaymentFreqObj paymentFreq) {
        this.paymentFreq = paymentFreq;
    }

    public NotifyPaymentAmt getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(NotifyPaymentAmt paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public PercentObject getRatePerPeriod() {
        return ratePerPeriod;
    }

    public void setRatePerPeriod(PercentObject ratePerPeriod) {
        this.ratePerPeriod = ratePerPeriod;
    }

    public DollarObject getPrincipalAmt() {
        return principalAmt;
    }

    public void setPrincipalAmt(DollarObject principalAmt) {
        this.principalAmt = principalAmt;
    }

    public DollarObject getInterestAmt() {
        return interestAmt;
    }

    @Override
    public double getPercent() {
        return interestAmt.getValue() / (interestAmt.getValue() + principalAmt.getValue());
    }

    public void setInterestAmt(DollarObject interestAmt) {
        this.interestAmt = interestAmt;
    }

    public CompoundPeriodObj getCompoundPeriod() {
        return compoundPeriod;
    }

    public void setCompoundPeriod(CompoundPeriodObj compoundPeriod) {
        this.compoundPeriod = compoundPeriod;
    }

    public List<LumpSum> getLumpSums() {
        return lumpSums;
    }

    public void setLumpSums(List<LumpSum> lumpSums) {
        this.lumpSums = lumpSums;
    }

    public List<LumpSum> getLumpSumsTmp() {
        return lumpSumsTmp;
    }

    public void setLumpSumsTmp(List<LumpSum> lumpSumsTmp) {
        this.lumpSumsTmp = lumpSumsTmp;
    }

    public DateObject getStartDate() {
        return startDate;
    }

    public void setStartDate(DateObject startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ((this.name != null) && (!this.name.equals(name))) {
            this.name = name;
            MessageChannel.getInstance().publish(name, MessageChannel.NAME_CHANNEL);
        } else
            this.name = name;

    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public PercentObject calculateRatePerPeriod() {

        double result, rate;
        double numOfPaymentsPerYear, numOfCompoundPerYear;

        numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
        numOfCompoundPerYear = compoundPeriod.getCompoundValue().getValue();
        rate = interestRate.getValue() / 100;

        result = Math.pow((1.0f + rate / numOfCompoundPerYear), numOfCompoundPerYear / numOfPaymentsPerYear) - 1.0f;
        ratePerPeriod.setValue(result * 100);

        return ratePerPeriod;

    }

    public DollarObject calculatePaymentAmt() {
        double rate;
        calculateRatePerPeriod();
        float numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
        ;
        float amortizationValue = amortization.getYears() * numOfPaymentsPerYear + amortization.getMonths() * numOfPaymentsPerYear / 12.0f;
        rate = ratePerPeriod.getValue() / 100.0f;
        if (rate != 0) {
            double result = loanAmount.getValue() * rate / (1 - Math.pow((1 + rate), -1 * amortizationValue));


            paymentAmount.setValue(result);
        }
        return paymentAmount;
    }


    public int populateSchedule(List<ScheduleElement> schedule, boolean mode) {

        int numOfPayments = 0;

        this.calculateRatePerPeriod();
        int numOfPaymentsPerYear, amortizationValue;

        double paymentAmt = paymentAmount.getValue();
        double ratePerPeriodVal = ratePerPeriod.getValue() / 100;
        double principalVal = 0, interestVal = 0;
        double balance = loanAmount.getValue();

        numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
        amortizationValue = amortization.getYears() * numOfPaymentsPerYear + amortization.getMonths() * numOfPaymentsPerYear / 12;
        int numOfPaymentsPerYearCount = 0;
        //set up payment frequence increment
        int startYear = startDate.getDateValue().getYear() + 1900;

        Date paymentDate = startDate.getDateValue();
        Date nextDate = startDate.getDateValue();
        Date biPaymentDate = null;
        Date nextBiDate = startDate.getDateValue();
        Calendar gregorian = Calendar.getInstance();
        gregorian.setTime(startDate.getDateValue());


        if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue()) {
            biPaymentDate = startDate.getDateValue();
            gregorian.add(Calendar.DATE, 14);
            biPaymentDate = gregorian.getTime();
            gregorian.add(Calendar.DATE, 14);
            nextBiDate = gregorian.getTime();
        }
        this.resetLumpSums();

        for (int x = 0; x < amortizationValue; x++) {
            /////////////////////////////////////////////////
            //check to see if we're in another year
            /////////////////////////////////////////////////
            paymentDate = gregorian.getTime();

            if (startYear != (paymentDate.getYear() + 1900)) {
                startYear++;
                numOfPaymentsPerYearCount = 0;
            }


            //////////////////////////
            //Calculate Payment
            //////////////////////////
            //start calculations at start date
            double lumpsum = this.getLumpSums(nextDate, gregorian);

            balance = balance - lumpsum;
            principalVal = principalVal + lumpsum;
            double interestAmtTmp = balance * ratePerPeriodVal;
            double principalAmtTmp = paymentAmt - interestAmtTmp;

            interestVal = interestVal + interestAmtTmp;
            principalVal = principalVal + principalAmtTmp;
            balance = balance - principalAmtTmp;

            numOfPaymentsPerYearCount++;
            numOfPayments++;

            ScheduleElement elem;
            if (mode)
                elem = new ScheduleElement(interestVal, principalVal, balance, lumpsum);
            else
                elem = new ScheduleElement(interestAmtTmp, principalAmtTmp, balance, lumpsum);

            elem.setPaymentDate(paymentDate);
            schedule.add(elem);

            if (balance <= 0) break;
            if (principalVal > loanAmount.getValue()) break;

            if (biPaymentDate != null) {
                //need to update bi payment date
                /////////////////////////////////////////////////
                //check to see if we're in another year
                /////////////////////////////////////////////////


                if (startYear != (biPaymentDate.getYear() + 1900)) {
                    startYear++;
                    numOfPaymentsPerYearCount = 0;
                }

                //////////////////////////
                //Calculate Payment
                //////////////////////////
                //start calculations at start date

                lumpsum = this.getLumpSums(nextDate, gregorian);

                balance = balance - lumpsum;
                principalVal = principalVal + lumpsum;
                interestAmtTmp = balance * ratePerPeriodVal;
                principalAmtTmp = paymentAmt - interestAmtTmp;

                interestVal = interestVal + interestAmtTmp;
                principalVal = principalVal + principalAmtTmp;
                balance = balance - principalAmtTmp;

                numOfPaymentsPerYearCount++;
                numOfPayments++;
                if (mode)
                    elem = new ScheduleElement(interestVal, principalVal, balance, lumpsum);
                else
                    elem = new ScheduleElement(interestAmtTmp, principalAmtTmp, balance, lumpsum);

                elem.setPaymentDate(paymentDate);
                schedule.add(elem);

                if (numOfPaymentsPerYear == PaymentFreqEnum.MONTHLY.getValue())
                    gregorian.add(Calendar.MONTH, 1);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue())
                    gregorian.add(Calendar.MONTH, 2);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.WEEKLY.getValue())
                    gregorian.add(Calendar.DATE, 7);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.BIWEEKLY.getValue())
                    gregorian.add(Calendar.DATE, 14);
                biPaymentDate = gregorian.getTime();

                if (balance <= 0) break;
                if (principalVal > loanAmount.getValue()) break;

                //increment count by 1 since we've calculated a second payment

                x++;
            }


            if (numOfPaymentsPerYear == PaymentFreqEnum.MONTHLY.getValue())
                gregorian.add(Calendar.MONTH, 1);
            else if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue())
                gregorian.add(Calendar.MONTH, 2);
            else if (numOfPaymentsPerYear == PaymentFreqEnum.WEEKLY.getValue())
                gregorian.add(Calendar.DATE, 7);
            else if (numOfPaymentsPerYear == PaymentFreqEnum.BIWEEKLY.getValue())
                gregorian.add(Calendar.DATE, 14);
            nextDate = gregorian.getTime();

        }
        return numOfPayments;

    }

    public void populateYears(List<String> years, int numOfYears) {
        int intialYear = this.startDate.getDateValue().getYear() + 1900;

        for (int x = 0; x < numOfYears; x++) {
            years.add(String.format("%d", intialYear + x));
        }
    }

    protected void calculatePrincipalInterestAmtNoLumpsums() {
        int numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
        int amortizationValue = amortization.getYears() * numOfPaymentsPerYear + amortization.getMonths() * numOfPaymentsPerYear / 12;


        double loanAmountTmp = loanAmount.getValue();
        double paymentAmt = paymentAmount.getValue();
        double balance = paymentAmt * amortizationValue;
        double interestAmtTmp = balance - loanAmountTmp;

        principalAmt.setValue(loanAmountTmp);
        interestAmt.setValue(interestAmtTmp);
    }

    public void calculatePrincipalInterestAmt() {

        if ((lumpSums == null) || (lumpSums.size() == 0)) {
            calculatePrincipalInterestAmtNoLumpsums();
        } else {

            int numOfPayments = 0;

            this.calculateRatePerPeriod();
            int numOfPaymentsPerYear, amortizationValue;

            double paymentAmt = paymentAmount.getValue();
            double ratePerPeriodVal = ratePerPeriod.getValue() / 100;
            double principalVal = 0, interestVal = 0;
            double balance = loanAmount.getValue();

            numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
            amortizationValue = amortization.getYears() * numOfPaymentsPerYear + amortization.getMonths() * numOfPaymentsPerYear / 12;
            //set up payment frequence increment

            Date paymentDate = startDate.getDateValue();
            Date nextDate = startDate.getDateValue();
            Date biPaymentDate = null;
            Date nextBiDate = startDate.getDateValue();
            Calendar gregorian = Calendar.getInstance();
            gregorian.setTime(startDate.getDateValue());


            if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue()) {
                biPaymentDate = startDate.getDateValue();
                gregorian.add(Calendar.DATE, 14);
                biPaymentDate = gregorian.getTime();
                gregorian.add(Calendar.DATE, 14);
                nextBiDate = gregorian.getTime();
            }
            this.resetLumpSums();

            for (int x = 0; x < amortizationValue; x++) {

                //////////////////////////
                //Calculate Payment
                //////////////////////////
                //start calculations at start date
                double lumpsum = this.getLumpSums(nextDate, gregorian);

                balance = balance - lumpsum;
                principalVal = principalVal + lumpsum;
                double interestAmtTmp = balance * ratePerPeriodVal;
                double principalAmtTmp = paymentAmt - interestAmtTmp;

                interestVal = interestVal + interestAmtTmp;
                principalVal = principalVal + principalAmtTmp;
                balance = balance - principalAmtTmp;

                if (balance <= 0) break;
                if (principalVal > loanAmount.getValue()) break;

                if (biPaymentDate != null) {

                    //////////////////////////
                    //Calculate Payment
                    //////////////////////////
                    //start calculations at start date

                    lumpsum = this.getLumpSums(nextDate, gregorian);

                    balance = balance - lumpsum;
                    principalVal = principalVal + lumpsum;
                    interestAmtTmp = balance * ratePerPeriodVal;
                    principalAmtTmp = paymentAmt - interestAmtTmp;

                    interestVal = interestVal + interestAmtTmp;
                    principalVal = principalVal + principalAmtTmp;
                    balance = balance - principalAmtTmp;

                    if (numOfPaymentsPerYear == PaymentFreqEnum.MONTHLY.getValue())
                        gregorian.add(Calendar.MONTH, 1);
                    else if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue())
                        gregorian.add(Calendar.MONTH, 2);
                    else if (numOfPaymentsPerYear == PaymentFreqEnum.WEEKLY.getValue())
                        gregorian.add(Calendar.DATE, 7);
                    else if (numOfPaymentsPerYear == PaymentFreqEnum.BIWEEKLY.getValue())
                        gregorian.add(Calendar.DATE, 14);
                    biPaymentDate = gregorian.getTime();

                    if (balance <= 0) break;
                    if (principalVal > loanAmount.getValue()) break;

                    //increment count by 1 since we've calculated a second payment

                    x++;
                }


                if (numOfPaymentsPerYear == PaymentFreqEnum.MONTHLY.getValue())
                    gregorian.add(Calendar.MONTH, 1);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue())
                    gregorian.add(Calendar.MONTH, 2);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.WEEKLY.getValue())
                    gregorian.add(Calendar.DATE, 7);
                else if (numOfPaymentsPerYear == PaymentFreqEnum.BIWEEKLY.getValue())
                    gregorian.add(Calendar.DATE, 14);
                nextDate = gregorian.getTime();

            }
            principalAmt.setValue(principalVal);
            interestAmt.setValue(interestVal);

        }

    }

    public void calculateAmortization() {
        if (ratePerPeriod.getValue() == 0)
            calculateRatePerPeriod();
        double rate = ratePerPeriod.getValue() / 100;
        int year;
        double amoritization = -1.0 * Math.log(1 - loanAmount.getValue() * rate / paymentAmount.getValue()) / Math.log(1 + rate);
        int numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();
        int monthDivider = 1;

        numOfPaymentsPerYear = paymentFreq.getFreqValue().getValue();

        if (numOfPaymentsPerYear == PaymentFreqEnum.BIMONTHLY.getValue())
            monthDivider = 2;
        else if (numOfPaymentsPerYear == PaymentFreqEnum.WEEKLY.getValue())
            monthDivider = 4;
        else if (numOfPaymentsPerYear == PaymentFreqEnum.BIWEEKLY.getValue())
            monthDivider = 2;

        year = (int) Math.floor(amoritization / numOfPaymentsPerYear);
        int month = (int) (Math.floor(((amoritization % numOfPaymentsPerYear)) / monthDivider)) + 1;
        amortization.setYears(year);
        amortization.setMonths(month);

    }

    public void resetLumpSums() {
        if (lumpSumsTmp != null) {
            lumpSumsTmp.clear();
            lumpSumsTmp = null;
        }
        lumpSumsTmp = new ArrayList<LumpSum>();
        Iterator<LumpSum> iter = lumpSums.iterator();
        while (iter.hasNext()) {
            LumpSum lumpsum = iter.next();
            lumpsum.resetCurrentDate();
            lumpSumsTmp.add(lumpsum);

        }
    }

    public double getLumpSums(Date paymentDate, Calendar gregorian) {
        double result = 0;
        List<LumpSum> removeObjects = null;
        Iterator<LumpSum> iter = lumpSumsTmp.iterator();
        while (iter.hasNext()) {
            LumpSum lumpsum = iter.next();
            double tmpResult = lumpsum.getLumpSumFor(paymentDate, gregorian);
            if (tmpResult != -1)
                result += tmpResult;
            else {
                if (removeObjects == null)
                    removeObjects = new ArrayList<LumpSum>();
                removeObjects.add(lumpsum);
            }
        }
        if (removeObjects != null) {
            //remove objects from lumpSumsTmp
            for (int x = 0; x < removeObjects.size(); x++) {
                lumpSumsTmp.remove(removeObjects.get(x));
            }
        }
        return result;

    }
}
