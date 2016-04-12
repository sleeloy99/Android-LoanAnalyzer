/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.persist.ILoan;
import com.cellinova.loananalyzerultra.persist.ModelObject;
import com.cellinova.loananalyzerultra.view.SwipeDismissListViewTouchListener;
import com.cellinova.loananalyzerultra.view.ViewWrapper;

public class LoansActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mListView;
    private LoanListAdapter mAdapter;
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelObject.getInstance(this);
        setContentView(R.layout.loans);

        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddLoanActivity.class));
            }
        });

        getLoaderManager().initLoader(URL_LOADER, null, this);

        // Get ListView object from xml
        mListView = (ListView) findViewById(R.id.loans);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    try {
                                        Cursor item = (Cursor) mAdapter.getItem(position);
                                        long uid = item.getLong(0);
                                        ModelObject.getInstance().deleteLoan(uid);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                //  mAdapter.notifyDataSetChanged();
                            }
                        });
        mListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mListView.setOnScrollListener(touchListener.makeScrollListener());

        // Used to map notes entries from the database to views
        mAdapter = new LoanListAdapter(this, null, 0);


        // Assign adapter to ListView
        mListView.setAdapter(mAdapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LoanObject selectedLoan = ModelObject.getInstance().getLoanById(id);

                ModelObject.getInstance().assignSelected(selectedLoan);
                startActivity(new Intent(getApplicationContext(), LoanActivity.class));
            }

        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ModelObject.getInstance().createCursorLoader(this);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    public static class LoanListAdapter extends CursorAdapter implements Filterable {
        private LayoutInflater mInflater;

        public LoanListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View newView(Context ctxt, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.loancell, null);

            LoanObject loan = ModelObject.getInstance().addLoans(cursor);
            bindViewHelper(view, loan);
            return view;
        }

        @Override
        public void bindView(View view, Context ctxt, Cursor cursor) {
            LoanObject loan = ModelObject.getInstance().getLoan(cursor);
            if (loan == null) {
                loan = ModelObject.getInstance().addLoans(cursor);
            }
            bindViewHelper(view, loan);
        }

        private void bindViewHelper(View view, LoanObject loan) {
            TextView cellIcon = (TextView) view.findViewById(R.id.cellicon);
            cellIcon.setText(loan.getName().substring(0, 1).toUpperCase());
            TextView loanNameTxt = (TextView) view.findViewById(R.id.loanName);
            loanNameTxt.setText(loan.getName());
            view.setTag(loan.getUid());

            TextView loanAmtTxt = (TextView) view.findViewById(R.id.loanAmt);
            loanAmtTxt.setText(loan.getLoanAmount().displayString());

            TextView amortizationTxt = (TextView) view.findViewById(R.id.amortization);
            amortizationTxt.setText(loan.getAmortization().displayStringShort() + " @" + loan.getInterestRate().displayString());

            TextView paymentAmtTxt = (TextView) view.findViewById(R.id.paymentAmt);
            paymentAmtTxt.setText(loan.getPaymentAmount().displayString() + "/" + loan.getPaymentFreq().displayString());
        }
    }

}