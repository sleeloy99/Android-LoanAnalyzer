/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public enum LumpSumPeriodEnum {
    LUMPMONTHLY(12), LUMPANNUAL(1), LUMPNONE(0);
    private final int value;

    LumpSumPeriodEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LumpSumPeriodEnum getEnum(int value) {
        switch (value) {
            case 12:
                return LUMPMONTHLY;
            case 1:
                return LUMPANNUAL;
            default:
                return LUMPNONE;

        }
    }

}
