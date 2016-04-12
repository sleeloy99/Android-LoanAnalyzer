/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public class DurationObject implements FormatObject {
    protected DateObject startDate;
    protected DateObject endDate;


    public DurationObject(DateObject startDate, DateObject endDate) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String displayDiffString(FormatObject original) {
        return null;
    }

    @Override
    public String displayString() {
        return startDate.displayString() + "-" + endDate.displayString();
    }

    @Override
    public double getDiff(FormatObject original) {
        return 0;
    }

    public DateObject getStartDate() {
        return startDate;
    }

    public void setStartDate(DateObject startDate) {
        this.startDate = startDate;
    }

    public DateObject getEndDate() {
        return endDate;
    }

    public void setEndDate(DateObject endDate) {
        this.endDate = endDate;
    }


}
