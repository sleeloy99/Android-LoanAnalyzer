/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cellinova.loananalyzerultra.delegate.IScheduleDataChangeListener;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.PaymentFreqEnum;

public class ScheduleModel {
    protected List<ScheduleElement> schedule;
    protected Date initDate;
    protected PaymentFreqEnum paymentFreq;
    protected int startYear;
    protected int numOfPayments;
    protected boolean mMode = false;

    protected IScheduleDataChangeListener mScheduleDataChangeListener;

    protected static ScheduleModel _instance;

    public static ScheduleModel getInstance() {
        if (_instance == null)
            _instance = new ScheduleModel();
        return _instance;
    }


    public List<ScheduleElement> getSchedule() {
        return schedule;
    }


    public ScheduleElement getElement(int index) {
        return schedule.get(index);
    }

    public ScheduleElement getElementFromArray(List<ScheduleElement> array, int objectAtIndex) {
        if (objectAtIndex < array.size())
            return array.get(objectAtIndex);
        else
            return ModelObject.getInstance().getEmptyElement();
    }

    public int getCount() {
        return schedule.size();
    }


    public int getNumOfPayments() {
        return numOfPayments;
    }

    public void toggleMode() {
        mMode = !mMode;
        initializeArray(mMode);
    }

    public void setScheduleDataChangeListener(IScheduleDataChangeListener scheduleDataChangeListener) {
        mScheduleDataChangeListener = scheduleDataChangeListener;
    }

    public void initializeArray(boolean mode) {
        mMode = mode;

        schedule = new ArrayList<ScheduleElement>();

        LoanObject selectedLoan = ModelObject.getInstance().selectedObject;
        numOfPayments = selectedLoan.populateSchedule(schedule, mode);

        paymentFreq = selectedLoan.getPaymentFreq().getFreqValue();

        this.initDate = (Date) selectedLoan.getStartDate().getDateValue().clone();

        startYear = initDate.getYear() + 1900;
        if (mScheduleDataChangeListener != null) {
            mScheduleDataChangeListener.dataChanged();
        }


    }

}
