/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public enum PaymentFreqEnum {
    MONTHLY(12), BIMONTHLY(24), WEEKLY(52), BIWEEKLY(26);
    private final int value;

    PaymentFreqEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PaymentFreqEnum getEnum(int value) {
        switch (value) {
            case 12:
                return MONTHLY;
            case 24:
                return BIMONTHLY;
            case 52:
                return WEEKLY;
            default:
                return BIWEEKLY;

        }
    }
}
