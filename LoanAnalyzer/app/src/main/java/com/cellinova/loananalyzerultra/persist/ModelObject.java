/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.cellinova.loananalyzerultra.model.MessageChannel;
import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.AmortizationObj;
import com.cellinova.loananalyzerultra.model.CompoundPeriodEnum;
import com.cellinova.loananalyzerultra.model.CompoundPeriodObj;
import com.cellinova.loananalyzerultra.model.DateObject;
import com.cellinova.loananalyzerultra.model.DollarObject;
import com.cellinova.loananalyzerultra.model.DurationObject;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.LumpSum;
import com.cellinova.loananalyzerultra.model.LumpSumPeriodEnum;
import com.cellinova.loananalyzerultra.model.LumpSumPeriodObj;
import com.cellinova.loananalyzerultra.model.NotifyPaymentAmt;
import com.cellinova.loananalyzerultra.model.PaymentFreqEnum;
import com.cellinova.loananalyzerultra.model.PaymentFreqObj;
import com.cellinova.loananalyzerultra.model.PercentObject;

/**
 * Provides access to the MicroLoans database. Since this is not a Content Provider,
 * no other applications will have access to the database.
 */
public class ModelObject {
    public static final String TAG = "ModelObject";
    /**
     * Keep track of context so that we can load SQL from string resources
     */
    private final Context mContext;

    private static ModelObject _instance;
    private static ScheduleElement _emptyElement;
    private static Boolean supported;

    protected LoanObject selectedOriginalObject;
    protected LoanObject selectedObject;
    protected LumpSum selectedLumpSump;
    protected Map<Long, LoanObject> loans;
    protected Map<Long, LumpSum> lumpsums;


    public static ModelObject getInstance(Context context) {
        if (_instance == null) {
            _instance = new ModelObject(context);
            _emptyElement = new ScheduleElement();

        }
        return _instance;
    }

    public static ModelObject getInstance() {
        if (_instance == null)
            throw new RuntimeException("Model not initialized");
        return _instance;
    }


    public ScheduleElement getEmptyElement() {
        return _emptyElement;
    }

    /**
     * Constructor
     */
    public ModelObject(Context context) {
        this.mContext = context;
        this.loans = new HashMap<Long, LoanObject>();
        this.lumpsums = new HashMap<Long, LumpSum>();
    }

    public void updateDatabase(LoanObject loan, Uri mUri) {

        long idx = Long.parseLong(mUri.getLastPathSegment());

        ContentValues values = new ContentValues();
        values.put(ILoan._ID, idx);
        values.put(ILoan.NAME, loan.getName());
        values.put(ILoan.YEARS, loan.getAmortization().getYears());
        values.put(ILoan.MONTHS, loan.getAmortization().getMonths());
        values.put(ILoan.LOANAMT, loan.getLoanAmount().getValue());
        values.put(ILoan.INTEREST_RATE, loan.getInterestRate().getValue());
        values.put(ILoan.PAYMENT_FREQ, loan.getPaymentFreq().getFreqValue().getValue());
        values.put(ILoan.COMPOUND_PERIOD, loan.getCompoundPeriod().getCompoundValue().getValue());
        values.put(ILoan.STARTDATE, loan.getStartDate().getSQLDateStr());
        values.put(ILoan.PAYMENTAMT, loan.getPaymentAmount().getValue());
        mContext.getContentResolver().update(mUri, values, null, null);
    }

    public void deleteLoan(long id) {
        ModelObject.getInstance().removeLoanById(id);
        Uri noteUri = ContentUris.withAppendedId(ILoan.CONTENT_URI, id);
        mContext.getContentResolver().delete(noteUri, null, null);
        mContext.getContentResolver().notifyChange(ILoan.CONTENT_URI, null);
    }

    public LoanObject addLoans(Cursor values) {
        LoanObject loan = new LoanObject();
        loan.setUid(values.getLong(0));
        loan.setName(values.getString(1));
        loan.setLoanAmount(new DollarObject(values.getDouble(4), MessageChannel.LOANAMOUNT_CHANNEL));
        loan.setAmortization(new AmortizationObj(values.getInt(2), values.getInt(3), MessageChannel.AMORTIZATION_CHANNEL));
        loan.setCompoundPeriod(new CompoundPeriodObj(CompoundPeriodEnum.getEnum(values.getInt(7)), MessageChannel.COMPOUNDPERIOD_CHANNEL));
        loan.setPaymentFreq(new PaymentFreqObj(PaymentFreqEnum.getEnum(values.getInt(6)), MessageChannel.FREQPERIOD_CHANNEL));
        loan.setInterestRate(new PercentObject(values.getDouble(5), MessageChannel.INTERESTRATE_CHANNEL));
        loan.setStartDate(new DateObject(values.getString(8), MessageChannel.STARTDATE_CHANNEL));
        loan.setPaymentAmount(new NotifyPaymentAmt(values.getDouble(9), MessageChannel.PAYMENTAMOUNT_CHANNEL));
        loans.put(loan.getUid(), loan);

        return loan;
    }

    public LoanObject getLoan(Cursor cursor) {
        return loans.get(cursor.getLong(0));
    }

    public LoanObject getLoanById(long id) {
        return loans.get(id);
    }

    public LoanObject removeLoanById(long id) {
        return loans.remove(id);
    }

    public String toDBFormat(String dbstr) {
        return dbstr.replaceAll("'", "''");
    }

    public CursorLoader createCursorLoader(Activity activity) {
        CursorLoader cursorLoader = new CursorLoader(activity,
                ILoan.CONTENT_URI, ILoan.PROJECTION, null, null, null);
        return cursorLoader;
    }

    public CursorLoader createLumpSumCursorLoader(Activity activity) {
        CursorLoader cursorLoader = new CursorLoader(activity,
                ILumpsum.CONTENT_URI, ILumpsum.PROJECTION, null, null, null);
        return cursorLoader;
    }

    public void assignSelected(LumpSum lumpSum) {
        selectedLumpSump = lumpSum;
    }

    public LumpSum getSelectedLumpSump() {
        return selectedLumpSump;
    }


    public void assignSelected(LoanObject selectedObjectVal) {

        //check to see if the lumpsum list is loaded
        if (selectedObjectVal.getLumpSums() == null) {
//            Uri mUri = ContentUris.withAppendedId(ILumpsum.CONTENT_LOAN_URI, selectedObjectVal.getUid());
//
//			Cursor cursor = mContext.getContentResolver().query(mUri, PROJECTION, null, null, null);			
//			while (!cursor.isLast()){
//				selectedObjectVal.getLumpSums().add(addLumpsums(cursor));
//				cursor.moveToNext();
//			}
        }

        //may need to calculate the principal amount
        if (selectedObjectVal.getPrincipalAmt().getValue() == -1)
            selectedObjectVal.calculatePrincipalInterestAmt();

        selectedOriginalObject = selectedObjectVal;
        resetSelected();
    }

    public LoanObject getSelectedOriginalObject() {
        return selectedOriginalObject;
    }

    public LoanObject getSelectedObject() {
        return selectedObject;
    }

    public void resetSelected() {
        selectedObject = (LoanObject) selectedOriginalObject.clone();
    }

    public void assignNew() {
        LoanObject loan = new LoanObject();
        loan.setName(this.mContext.getResources().getString(R.string.newLoan));
        loan.getAmortization().setYears(20);
        loan.getAmortization().setMonths(0);
        loan.getLoanAmount().setValue(150000);
        loan.getInterestRate().setValue(4.25);
        loan.calculatePaymentAmt();
        loan.calculatePrincipalInterestAmt();
//		loan.setUid(loans.size());
        loan.setStartDate(new DateObject(Calendar.getInstance().getTime(), MessageChannel.STARTDATE_CHANNEL));
        this.selectedOriginalObject = loan;
        this.resetSelected();
    }

    public void saveSelected() {
        if (selectedObject != null) {
            long index = selectedObject.getUid();
            loans.put(index, selectedObject);
            Uri mUri = ContentUris.withAppendedId(ILoan.CONTENT_URI, selectedObject.getUid());
            updateDatabase(selectedObject, mUri);
            selectedOriginalObject = selectedObject;
            selectedObject = (LoanObject) selectedOriginalObject.clone();
        }
    }

    protected void synchLoanState() {
        loans.put(selectedObject.getUid(), selectedObject);
        selectedOriginalObject = selectedObject;
        selectedObject = (LoanObject) selectedOriginalObject.clone();
    }

    public void persistLoan() {
        //	selectedObject.setUid(loans.size() -1);

        Uri mUri = mContext.getContentResolver().insert(ILoan.CONTENT_URI, null);
        // If we were unable to create a new note, then just finish
        // this activity.  A RESULT_CANCELED will be sent back to the
        // original activity if they requested a result.
        if (mUri == null) {
            Log.e(TAG, "Failed to insert new note into ");
            return;
        }

        updateDatabase(selectedObject, mUri);
    }

    /**
     * ***********************
     * Lumpsum methods
     * ************************
     */

    public LumpSum createNewLumpSum() {
        return new LumpSum(new DurationObject(new DateObject(Calendar.getInstance().getTime(), MessageChannel.LUMPSUM_STARTDATE_CHANNEL), new DateObject(Calendar.getInstance().getTime(), MessageChannel.LUMPSUM_ENDDATE_CHANNEL)), new LumpSumPeriodObj(LumpSumPeriodEnum.LUMPNONE, MessageChannel.LUMPSUM_PERIOD_CHANNEL), new DollarObject(0, MessageChannel.LUMPSUM_VALUE_CHANNEL), Calendar.getInstance().getTime(), this.selectedObject.getUid());
    }

    public void persistLumpsum(LumpSum lumpsum) {

        Uri mUri = mContext.getContentResolver().insert(ILumpsum.CONTENT_URI, null);
        // If we were unable to create a new note, then just finish
        // this activity.  A RESULT_CANCELED will be sent back to the
        // original activity if they requested a result.
        if (mUri == null) {
            Log.e(TAG, "Failed to insert new note into ");
            return;
        }

        updateDatabase(lumpsum, mUri);
        replaceLumpSum(selectedObject, lumpsum);
        replaceLumpSum(selectedOriginalObject, lumpsum);

    }

    public void updateDatabase(LumpSum lumpsum, Uri mUri) {

        long idx = Long.parseLong(mUri.getLastPathSegment());

        ContentValues values = new ContentValues();
        values.put(ILumpsum._ID, idx);
        values.put(ILumpsum.VALUE, lumpsum.getValue().getValue());
        values.put(ILumpsum.LOANID, lumpsum.getLoanid());
        values.put(ILumpsum.LUMPSUMPERIOD, lumpsum.getLumpsumPeriod().getLumpSumValue().getValue());
        values.put(ILumpsum.ENDDATE, lumpsum.getDuration().getStartDate().getSQLDateStr());
        values.put(ILumpsum.STARTDATE, lumpsum.getDuration().getStartDate().getSQLDateStr());
        mContext.getContentResolver().update(mUri, values, null, null);
    }

    public void saveSelected(LumpSum lumpsum) {
        if (lumpsum != null) {
            long index = lumpsum.getUuid();
            Uri mUri = ContentUris.withAppendedId(ILumpsum.CONTENT_URI, index);
            updateDatabase(lumpsum, mUri);
            //need to update lumpsum list
            replaceLumpSum(selectedObject, lumpsum);
            replaceLumpSum(selectedOriginalObject, lumpsum);
            lumpsums.remove(index);
            lumpsums.put(index, lumpsum);
        }
    }

    protected void replaceLumpSum(LoanObject loanObject, LumpSum lumpsum) {
        List<LumpSum> lumpsumList = loanObject.getLumpSums();
        Iterator<LumpSum> iter = lumpsumList.iterator();
        LumpSum foundElem = null;
        while (iter.hasNext()) {
            LumpSum elem = iter.next();
            if (elem.getUuid() == lumpsum.getUuid()) {
                foundElem = elem;
                break;
            }
        }
        lumpsumList.remove(foundElem);
        lumpsumList.add(lumpsum);

    }

    public LumpSum addLumpsums(Cursor values) {
        LumpSum lumpsum = new LumpSum(new DurationObject(new DateObject(values.getString(3), MessageChannel.LUMPSUM_STARTDATE_CHANNEL), new DateObject(values.getString(4), MessageChannel.LUMPSUM_ENDDATE_CHANNEL)), new LumpSumPeriodObj(LumpSumPeriodEnum.getEnum(values.getInt(2)), MessageChannel.LUMPSUM_PERIOD_CHANNEL), new DollarObject(values.getDouble(5), MessageChannel.LUMPSUM_VALUE_CHANNEL), Calendar.getInstance().getTime(), values.getLong(1));
        lumpsum.setUuid(values.getLong(0));
        lumpsums.put(lumpsum.getUuid(), lumpsum);

        return lumpsum;
    }

    public void clearLumpsums() {
        lumpsums.clear();
    }

    public LumpSum getLumpsum(Cursor cursor) {
        return lumpsums.get(cursor.getLong(0));
    }

    public LumpSum getLumpsumById(long id) {
        return lumpsums.get(id);
    }

    public LumpSum removeLumpsumById(long id) {
        return lumpsums.remove(id);
    }


}

