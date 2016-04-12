/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public class Message {
    protected int channelid;
    protected Object message;

    public Message(int channelid, Object message) {
        super();
        this.channelid = channelid;
        this.message = message;
    }

    public int getChannelid() {
        return channelid;
    }

    public Object getMessage() {
        return message;
    }
}
