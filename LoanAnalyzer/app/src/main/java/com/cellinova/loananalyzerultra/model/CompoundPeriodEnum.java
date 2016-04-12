/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public enum CompoundPeriodEnum {
    CMONTHLY(12), CANNUAL(1), CSEMIANNUAL(2);
    private final int value;

    CompoundPeriodEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CompoundPeriodEnum getEnum(int value) {
        switch (value) {
            case 12:
                return CMONTHLY;
            case 1:
                return CANNUAL;
            default:
                return CSEMIANNUAL;

        }
    }
}
