/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "LoanProvider";

    private static final String DATABASE_NAME = "loan.db";
    private static final int DATABASE_VERSION = 2;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ILoan.LOANS_TABLE_NAME + " ("
                + ILoan._ID + " INTEGER PRIMARY KEY,"
                + ILoan.NAME + " TEXT,"
                + ILoan.YEARS + " INTEGER,"
                + ILoan.MONTHS + " INTEGER,"
                + ILoan.LOANAMT + " REAL,"
                + ILoan.INTEREST_RATE + " REAL,"
                + ILoan.PAYMENT_FREQ + " INTEGER,"
                + ILoan.COMPOUND_PERIOD + " INTEGER,"
                + ILoan.STARTDATE + " TEXT,"
                + ILoan.PAYMENTAMT + " REAL"
                + ");");
        db.execSQL("CREATE TABLE " + ILumpsum.LUMPSUMS_TABLE_NAME + " ("
                + ILumpsum._ID + " INTEGER PRIMARY KEY,"
                + ILumpsum.VALUE + " REAL,"
                + ILumpsum.LOANID + " INTEGER,"
                + ILumpsum.LUMPSUMPERIOD + " INTEGER,"
                + ILumpsum.ENDDATE + " TEXT,"
                + ILumpsum.STARTDATE + " TEXT"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ILoan.LOANS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ILumpsum.LUMPSUMS_TABLE_NAME);
        onCreate(db);
    }
}
