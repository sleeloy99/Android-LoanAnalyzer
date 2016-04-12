/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.Observable;
import java.util.Observer;

public class MessageChannel extends Observable {

    protected static final int START_CHANNEL = 0;
    public static final int AMORTIZATION_CHANNEL = START_CHANNEL;
    public static final int LOANAMOUNT_CHANNEL = START_CHANNEL + 1;
    public static final int PAYMENTAMOUNT_CHANNEL = START_CHANNEL + 2;
    public static final int INTERESTRATE_CHANNEL = START_CHANNEL + 3;
    public static final int PRINCIPALAMT_CHANNEL = START_CHANNEL + 4;
    public static final int INTERESTAMT_CHANNEL = START_CHANNEL + 5;
    public static final int RATEPERPERIOD_CHANNEL = START_CHANNEL + 6;
    public static final int FREQPERIOD_CHANNEL = START_CHANNEL + 7;
    public static final int COMPOUNDPERIOD_CHANNEL = START_CHANNEL + 8;
    public static final int NAME_CHANNEL = START_CHANNEL + 9;
    public static final int STARTDATE_CHANNEL = START_CHANNEL + 10;

    public static final int LUMPSUM_VALUE_CHANNEL = START_CHANNEL + 11;
    public static final int LUMPSUM_STARTDATE_CHANNEL = START_CHANNEL + 12;
    public static final int LUMPSUM_ENDDATE_CHANNEL = START_CHANNEL + 13;
    public static final int LUMPSUM_PERIOD_CHANNEL = START_CHANNEL + 14;

    private static MessageChannel _instance;

    private MessageChannel() {
    }

    public static MessageChannel getInstance() {
        if (_instance == null)
            _instance = new MessageChannel();
        return _instance;
    }

    public void publish(Object message, int channelid) {
        setChanged();
        notifyObservers(new Message(channelid, message));
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        this.deleteObserver(observer);
        super.addObserver(observer);
    }
}
