/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.persist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cellinova.loananalyzerultra.model.MessageChannel;
import com.cellinova.loananalyzerultra.model.AmortizationObj;
import com.cellinova.loananalyzerultra.model.CompoundPeriodEnum;
import com.cellinova.loananalyzerultra.model.CompoundPeriodObj;
import com.cellinova.loananalyzerultra.model.DateObject;
import com.cellinova.loananalyzerultra.model.DollarObject;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.PaymentFreqEnum;
import com.cellinova.loananalyzerultra.model.PaymentFreqObj;
import com.cellinova.loananalyzerultra.model.PercentObject;

public class LoanHelper {

    public static List<LoanObject> loadLoans() {
        return null;
    }

    public static List<LoanObject> createMockObjects() {
        List<LoanObject> returnList = new ArrayList<LoanObject>();
        for (int rowNum = 0; rowNum < 4; rowNum++) {
            LoanObject loan = new LoanObject();
            loan.setName("Loan Name" + rowNum);
            loan.setInterestAmt(new DollarObject(rowNum, MessageChannel.INTERESTAMT_CHANNEL));
            loan.setLoanAmount(new DollarObject((rowNum + 1) * 100000, MessageChannel.LOANAMOUNT_CHANNEL));
            loan.setAmortization(new AmortizationObj(23 + rowNum, rowNum, MessageChannel.AMORTIZATION_CHANNEL));
            loan.setCompoundPeriod(new CompoundPeriodObj(CompoundPeriodEnum.getEnum(12), MessageChannel.COMPOUNDPERIOD_CHANNEL));
            loan.setPaymentFreq(new PaymentFreqObj(PaymentFreqEnum.getEnum(24), MessageChannel.FREQPERIOD_CHANNEL));
            loan.setInterestRate(new PercentObject(((double) rowNum + 1.0f) / 2.0f, MessageChannel.INTERESTRATE_CHANNEL));
            loan.setStartDate(new DateObject(Calendar.getInstance().getTime(), MessageChannel.STARTDATE_CHANNEL));
            loan.calculatePaymentAmt();
            returnList.add(loan);
        }

        return returnList;

    }

}
