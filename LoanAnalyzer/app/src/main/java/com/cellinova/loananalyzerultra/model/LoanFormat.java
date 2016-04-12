/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.HashMap;
import java.util.Map;

public enum LoanFormat {
    PERCENTAGE(0), DOUBLE(1);
    private final int value;

    private static Map<Integer, LoanFormat> map = new HashMap<Integer, LoanFormat>();

    static {
        for (LoanFormat loanEnum : LoanFormat.values()) {
            map.put(loanEnum.value, loanEnum);
        }
    }

    private LoanFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LoanFormat valueOf(int v) {
        return map.get(v);
    }
}
