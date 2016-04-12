/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.HashMap;
import java.util.Map;

public enum LoanType {
    LUMPSUM(0), LOANAMT(1), PAYMENTAMT(2), INTERESTRATE(3), INTEREST(4), PRINCIPAL(5);
    private final int value;


    private static Map<Integer, LoanType> map = new HashMap<Integer, LoanType>();

    static {
        for (LoanType legEnum : LoanType.values()) {
            map.put(legEnum.value, legEnum);
        }
    }

    private LoanType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LoanType valueOf(int v) {
        return map.get(v);
    }
}
