/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.Calendar;
import java.util.Date;

import com.cellinova.loananalyzerultra.model.MessageChannel;

public class LumpSum implements Cloneable {
    protected DurationObject duration;
    protected LumpSumPeriodObj lumpsumPeriod;
    protected DollarObject value;
    protected Date currentDate;
    protected long loanid;
    protected long uuid;
    protected boolean paid;
    protected int dateIncrement;

    public LumpSum() {

    }

    public LumpSum(DurationObject duration, LumpSumPeriodObj lumpsumPeriod,
                   DollarObject value, Date currentDate, long loanid) {
        super();
        this.duration = duration;
        this.lumpsumPeriod = lumpsumPeriod;
        this.value = value;
        this.loanid = loanid;
        uuid = -1;
        this.currentDate = currentDate;
    }

    public Object clone() {
        LumpSum cloned = new LumpSum();
        cloned.uuid = uuid;
        cloned.value = new DollarObject(value.getValue(), MessageChannel.LUMPSUM_VALUE_CHANNEL);

        cloned.duration = new DurationObject(new DateObject(duration.startDate.getDateValue(), MessageChannel.LUMPSUM_STARTDATE_CHANNEL), new DateObject(duration.endDate.getDateValue(), MessageChannel.LUMPSUM_ENDDATE_CHANNEL));
        cloned.lumpsumPeriod = new LumpSumPeriodObj(lumpsumPeriod.getLumpSumValue(), MessageChannel.LUMPSUM_PERIOD_CHANNEL);
        cloned.loanid = loanid;
        cloned.currentDate = (Date) currentDate.clone();
        return cloned;
    }

    public void resetCurrentDate() {
        Date startDateCopy = (Date) this.duration.getStartDate().getDateValue().clone();
        this.currentDate = startDateCopy;
        //reduce memory count by 1 since setter will retain
        if (lumpsumPeriod.getLumpSumValue() == LumpSumPeriodEnum.LUMPMONTHLY)
            dateIncrement = Calendar.MONTH;
        else if (lumpsumPeriod.getLumpSumValue() == LumpSumPeriodEnum.LUMPANNUAL)
            dateIncrement = Calendar.YEAR;
    }

    public double getLumpSumFor(Date paymentDate, Calendar gregorian) {

        double returnVal = 0;
        if (this.currentDate == null)
            return -1;
        if (this.duration.endDate.getDateValue().getTime() < this.currentDate.getTime())
            return -1;
        boolean comparision = paymentDate.getTime() >= this.currentDate.getTime();
        Date beforeTime = gregorian.getTime();
        while (comparision) {
            gregorian.add(dateIncrement, 1);
            this.currentDate = gregorian.getTime();

            returnVal += this.value.getValue();
            comparision = paymentDate.getTime() >= this.currentDate.getTime();

            //only apply lump sum once if it doesn't repeat
            if (lumpsumPeriod.getLumpSumValue() == LumpSumPeriodEnum.LUMPNONE) {
                this.currentDate = null;
                break;
            } else if (this.duration.endDate.getDateValue().getTime() < this.currentDate.getTime()) {
                break;
            }
        }
        gregorian.setTime(beforeTime);
        return returnVal;
    }

    public DurationObject getDuration() {
        return duration;
    }

    public void setDuration(DurationObject duration) {
        this.duration = duration;
    }

    public LumpSumPeriodObj getLumpsumPeriod() {
        return lumpsumPeriod;
    }

    public void setLumpsumPeriod(LumpSumPeriodObj lumpsumPeriod) {
        this.lumpsumPeriod = lumpsumPeriod;
    }

    public DollarObject getValue() {
        return value;
    }

    public void setValue(DollarObject value) {
        this.value = value;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public long getLoanid() {
        return loanid;
    }

    public void setLoanid(long loanid) {
        this.loanid = loanid;
    }

}
