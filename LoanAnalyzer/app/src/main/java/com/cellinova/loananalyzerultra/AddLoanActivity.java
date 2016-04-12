/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra;

import android.os.Bundle;
import android.view.View;

import com.cellinova.loananalyzerultra.persist.ModelObject;

public class AddLoanActivity extends LoanActivity {
    public AddLoanActivity() {
        super();
        isAdd = true;
        ModelObject.getInstance().assignNew();

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }
}
