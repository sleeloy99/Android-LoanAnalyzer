/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra.model;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class LoanAdapter<T> extends ArrayAdapter<T> {

    public LoanAdapter(Context context, int resource, int textViewResourceId,
                       List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public LoanAdapter(Context context, int resource, int textViewResourceId,
                       T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public LoanAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public LoanAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    public LoanAdapter(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, objects);
    }

    public LoanAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

}
