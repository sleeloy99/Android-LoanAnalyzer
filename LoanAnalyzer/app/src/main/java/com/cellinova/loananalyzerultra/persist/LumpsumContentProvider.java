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
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LumpsumContentProvider extends ContentProvider implements ILumpsum {


    private static HashMap<String, String> sLumpsumsProjectionMap;

    private static final int LUMPSUMS = 100;
    private static final int LUMPSUM_ID = 102;
    private static final int LOANID_ID = 103;

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
            case LUMPSUMS:
                qb.setTables(LUMPSUMS_TABLE_NAME);
                qb.setProjectionMap(sLumpsumsProjectionMap);
                break;

            case LUMPSUM_ID:
                qb.setTables(LUMPSUMS_TABLE_NAME);
                qb.setProjectionMap(sLumpsumsProjectionMap);
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            case LOANID_ID:
                qb.setTables(LUMPSUMS_TABLE_NAME);
                qb.setProjectionMap(sLumpsumsProjectionMap);
                qb.appendWhere(LOANID + "=" + uri.getPathSegments().get(1));
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
            case LUMPSUMS:
                return CONTENT_TYPE;
            case LOANID_ID:
                return CONTENT_TYPE;
            case LUMPSUM_ID:
                return CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != LUMPSUMS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(LUMPSUMPERIOD) == false) {
            values.put(LUMPSUMPERIOD, 0);
        }
        if (values.containsKey(STARTDATE) == false) {
            values.put(STARTDATE, "");
        }
        if (values.containsKey(VALUE) == false) {
            values.put(VALUE, 0);
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(LUMPSUMS_TABLE_NAME, "NULL", values);
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
            case LUMPSUMS:
                count = db.delete(LUMPSUMS_TABLE_NAME, where, whereArgs);
                break;

            case LUMPSUM_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(LUMPSUMS_TABLE_NAME, _ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            case LOANID_ID:
                noteId = uri.getPathSegments().get(1);
                count = db.delete(LUMPSUMS_TABLE_NAME, LOANID + "=" + noteId
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
            case LUMPSUMS:
                count = db.update(LUMPSUMS_TABLE_NAME, values, where, whereArgs);
                break;
            case LOANID_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(LUMPSUMS_TABLE_NAME, values, LOANID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            case LUMPSUM_ID:
                noteId = uri.getPathSegments().get(1);
                count = db.update(LUMPSUMS_TABLE_NAME, values, _ID + "=" + noteId
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
        sUriMatcher.addURI(AUTHORITY, "lumpsums", LUMPSUMS);
        sUriMatcher.addURI(AUTHORITY, "lumpsums/#", LUMPSUM_ID);
        sUriMatcher.addURI(AUTHORITY, "lumploanid/#", LOANID_ID);

        sLumpsumsProjectionMap = new HashMap<String, String>();
        sLumpsumsProjectionMap.put(_ID, _ID);
        sLumpsumsProjectionMap.put(VALUE, VALUE);
        sLumpsumsProjectionMap.put(LOANID, LOANID);
        sLumpsumsProjectionMap.put(LUMPSUMPERIOD, LUMPSUMPERIOD);
        sLumpsumsProjectionMap.put(ENDDATE, ENDDATE);
        sLumpsumsProjectionMap.put(STARTDATE, STARTDATE);
    }
}
