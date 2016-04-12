/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import android.net.Uri;

public interface ILoan {
    public static final String LOANS_TABLE_NAME = "fields";

    public static final String AUTHORITY = "com.cellinova.provider.LoanAnalyzer";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/loans");

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cellinova.loan";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cellinova.loan";

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String YEARS = "years";
    public static final String MONTHS = "months";
    public static final String LOANAMT = "loanamt";
    public static final String INTEREST_RATE = "interest_rate";
    public static final String PAYMENT_FREQ = "payment_freq";
    public static final String COMPOUND_PERIOD = "compound_period";
    public static final String STARTDATE = "startdate";
    public static final String PAYMENTAMT = "paymentamt";

    public static final String[] PROJECTION = new String[]{
            _ID, // 0
            NAME, // 1
            YEARS, // 2
            MONTHS, // 3
            LOANAMT, // 4
            INTEREST_RATE, // 5
            PAYMENT_FREQ, // 6
            COMPOUND_PERIOD, // 7
            STARTDATE, // 8
            PAYMENTAMT // 9
    };

}
