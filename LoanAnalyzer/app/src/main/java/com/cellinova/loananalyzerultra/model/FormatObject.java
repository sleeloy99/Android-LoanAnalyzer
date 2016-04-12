/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

public interface FormatObject {
    public String displayString();

    public String displayDiffString(FormatObject original);

    public double getDiff(FormatObject original);
}
