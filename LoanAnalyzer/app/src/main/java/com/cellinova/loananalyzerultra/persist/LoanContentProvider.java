/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LoanContentProvider extends ContentProvider implements ILoan {


    private static HashMap<String, String> sLoansProjectionMap;

    private static final int LOANS = 1;
    private static final int LOAN_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case LOANS:
                qb.setTables(LOANS_TABLE_NAME);
                qb.setProjectionMap(sLoansProjectionMap);
                break;

            case LOAN_ID:
                qb.setTables(LOANS_TABLE_NAME);
                qb.setProjectionMap(sLoansProjectionMap);
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        orderBy = sortOrder;

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LOANS:
                return CONTENT_TYPE;

            case LOAN_ID:
                return CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != LOANS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(NAME) == false) {
            Resources r = Resources.getSystem();
            values.put(NAME, "");
        }

        if (values.containsKey(YEARS) == false) {
            values.put(YEARS, 25);
        }
        if (values.containsKey(MONTHS) == false) {
            values.put(MONTHS, 0);
        }
        if (values.containsKey(INTEREST_RATE) == false) {
            values.put(INTEREST_RATE, 4.25);
        }
        if (values.containsKey(PAYMENT_FREQ) == false) {
            values.put(PAYMENT_FREQ, 0);
        }
        if (values.containsKey(COMPOUND_PERIOD) == false) {
            values.put(COMPOUND_PERIOD, 0);
        }
        if (values.containsKey(STARTDATE) == false) {
            values.put(STARTDATE, "");
        }
        if (values.containsKey(PAYMENTAMT) == false) {
            values.put(PAYMENTAMT, 0);
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(LOANS_TABLE_NAME, "NULL", values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LOANS:
                count = db.delete(LOANS_TABLE_NAME, where, whereArgs);
                break;

            case LOAN_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(LOANS_TABLE_NAME, _ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LOANS:
                count = db.update(LOANS_TABLE_NAME, values, where, whereArgs);
                break;

            case LOAN_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(LOANS_TABLE_NAME, values, _ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "loans", LOANS);
        sUriMatcher.addURI(AUTHORITY, "loans/#", LOAN_ID);

        sLoansProjectionMap = new HashMap<String, String>();
        sLoansProjectionMap.put(_ID, _ID);
        sLoansProjectionMap.put(NAME, NAME);
        sLoansProjectionMap.put(YEARS, YEARS);
        sLoansProjectionMap.put(MONTHS, MONTHS);
        sLoansProjectionMap.put(LOANAMT, LOANAMT);
        sLoansProjectionMap.put(INTEREST_RATE, INTEREST_RATE);
        sLoansProjectionMap.put(PAYMENT_FREQ, PAYMENT_FREQ);
        sLoansProjectionMap.put(COMPOUND_PERIOD, COMPOUND_PERIOD);
        sLoansProjectionMap.put(STARTDATE, STARTDATE);
        sLoansProjectionMap.put(PAYMENTAMT, PAYMENTAMT);
    }
}
