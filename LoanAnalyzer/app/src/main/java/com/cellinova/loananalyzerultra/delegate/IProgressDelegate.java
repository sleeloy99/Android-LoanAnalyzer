/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.delegate;

public interface IProgressDelegate {
    void onProgressChanged(double value);

    void onProgressDone();

}
