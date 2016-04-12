/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import android.net.Uri;

public interface ILumpsum {
    public static final String LUMPSUMS_TABLE_NAME = "lumpsums";

    public static final String AUTHORITY = "com.cellinova.provider.LoanAnalyzerLumpsum";


    public static final Uri CONTENT_URI
            = Uri.parse("content://com.cellinova.provider.LoanAnalyzerLumpsum/lumpsums");
    public static final Uri CONTENT_LOAN_URI
            = Uri.parse("content://com.cellinova.provider.LoanAnalyzerLumpsum/lumploanid");

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cellinova.lumpsum";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cellinova.lumpsum";

    public static final String _ID = "_id";
    public static final String LUMPSUMPERIOD = "lumpsumperiod";
    public static final String VALUE = "value";
    public static final String ENDDATE = "enddate";
    public static final String STARTDATE = "startdate";
    public static final String LOANID = "loanid";

    public static final String[] PROJECTION = new String[]{
            ILumpsum._ID, // 0
            ILumpsum.LOANID, // 1
            ILumpsum.LUMPSUMPERIOD, // 2
            ILumpsum.ENDDATE, // 3
            ILumpsum.STARTDATE, // 4
            ILumpsum.VALUE // 5
    };


}
