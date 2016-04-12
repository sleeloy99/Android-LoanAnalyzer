/*
 * Copyright (c) 2015 Sheldon Lee-Loy to Present.
 * All rights reserved.
 */

package com.cellinova.loananalyzerultra;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.cellinova.loananalyzerultra.controller.LumpSumController;
import com.cellinova.loananalyzerultra.delegate.ISaveResetActivity;
import com.cellinova.loananalyzerultra.delegate.ISaveResetDelegate;
import com.cellinova.loananalyzerultra.delegate.IScheduleDataChangeListener;
import com.cellinova.loananalyzerultra.model.CompoundPeriodEnum;
import com.cellinova.loananalyzerultra.model.DateObject;
import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.LoanObject;
import com.cellinova.loananalyzerultra.model.LumpSum;
import com.cellinova.loananalyzerultra.model.LumpSumPeriodEnum;
import com.cellinova.loananalyzerultra.model.Message;
import com.cellinova.loananalyzerultra.model.MessageChannel;
import com.cellinova.loananalyzerultra.model.NumberFormatObject;
import com.cellinova.loananalyzerultra.model.PaymentFreqEnum;
import com.cellinova.loananalyzerultra.persist.ModelObject;
import com.cellinova.loananalyzerultra.persist.ScheduleElement;
import com.cellinova.loananalyzerultra.persist.ScheduleModel;
import com.cellinova.loananalyzerultra.view.DiffArrowViewObserver;
import com.cellinova.loananalyzerultra.view.DiffTextViewObserver;
import com.cellinova.loananalyzerultra.view.IGetOriginalLoan;
import com.cellinova.loananalyzerultra.view.NumberKeyboard;
import com.cellinova.loananalyzerultra.view.PercentageGauge;
import com.cellinova.loananalyzerultra.view.SimpleLineChart;
import com.cellinova.loananalyzerultra.view.SlidingTabLayout;
import com.cellinova.loananalyzerultra.view.SwipeDismissListViewTouchListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class LoanActivity extends FragmentActivity implements ISaveResetActivity, Observer {

    protected boolean isAdd = false;
    protected boolean isLumpsumAdd = false;
    protected AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    protected ViewPager mViewPager;
    protected SlidingTabLayout mSlidingTabLayout;
    protected List<ISaveResetDelegate> saveResetDelegates = new ArrayList<ISaveResetDelegate>();
    protected PercentageGauge mPrincipalGauge;
    protected PercentageGauge mInterestGauge;
    protected MenuItem mRestoreMenuItem;
    protected MenuItem mSaveMenuItem;
    protected EditText mTitleBar;
    protected boolean mTitleClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScheduleModel.getInstance().initializeArray(true);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        setContentView(R.layout.activity_custom_keyboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setActionBar(toolbar);

        mTitleBar =(EditText) findViewById(R.id.titleBar);
        mTitleBar.setText(ModelObject.getInstance().getSelectedObject().getName());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = mTitleBar.isEnabled();
                mTitleBar.setEnabled(!enabled);
                if (!enabled) {
                    InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.showSoftInput(mTitleBar, InputMethodManager.SHOW_IMPLICIT);

                }
            }
        });
        mTitleBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ModelObject.getInstance().getSelectedObject().setName(s.toString());
            }
        });

        DiffArrowViewObserver interestArrow = (DiffArrowViewObserver)findViewById(R.id.interestArrow);
        interestArrow.setTypeface(font);
        DiffArrowViewObserver principalArrow = (DiffArrowViewObserver)findViewById(R.id.principalArrow);
        principalArrow.setTypeface(font);
        interestArrow.setOriginal(new IGetOriginalLoan() {
            public FormatObject getFormatObject() {
                return ModelObject.getInstance().getSelectedOriginalObject().getInterestAmt();
            }
        });
        principalArrow.setOriginal(new IGetOriginalLoan() {
            public FormatObject getFormatObject() {
                return ModelObject.getInstance().getSelectedOriginalObject().getPrincipalAmt();
            }
        });

        mPrincipalGauge = (PercentageGauge)findViewById(R.id.principalGauge);
        mInterestGauge = (PercentageGauge)findViewById(R.id.interestGauge);
        float percentageI = 100;

        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
        float percentageP = (float) (loanObject.getPrincipalAmt().getValue() / (loanObject.getInterestAmt().getValue()+loanObject.getPrincipalAmt().getValue()))*100;

        mInterestGauge.setInterval(20);
        mPrincipalGauge.setInterval(20);
        mPrincipalGauge.setPercentage(percentageP);
        mInterestGauge.setPercentage(percentageI-percentageP);

        setControls();

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);
        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }
        });

        MessageChannel.getInstance().addObserver(this);


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        DiffTextViewObserver principalDiff = (DiffTextViewObserver) findViewById(R.id.principalDiff);
        principalDiff.setOriginal(new IGetOriginalLoan() {
            public FormatObject getFormatObject() {
                return ModelObject.getInstance().getSelectedOriginalObject().getPrincipalAmt();
            }
        });
        DiffTextViewObserver interestDiff = (DiffTextViewObserver) findViewById(R.id.interestDiff);
        interestDiff.setOriginal(new IGetOriginalLoan(){
            public FormatObject getFormatObject(){
                return ModelObject.getInstance().getSelectedOriginalObject().getInterestAmt();
            }
        });

    }

    private void hideKeyboard() {
        mTitleBar.setEnabled(false);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(mTitleBar.getWindowToken(), 0);

    }

    public void onTitleClick(View v) {
        EditText editText = (EditText)v;
        if (mTitleClick) {
            hideKeyboard();
        }
        else {
            editText.setEnabled(true);
            InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
         //           toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        mTitleClick = !mTitleClick;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Message){
            Message messageObj = (Message)data;
            if (MessageChannel.PRINCIPALAMT_CHANNEL == messageObj.getChannelid() || MessageChannel.INTERESTAMT_CHANNEL == messageObj.getChannelid()) {
                LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
                double principal = 0;
                double interest = 0;
                float percentageP = 100;
                float percentageI = 100;
                if (MessageChannel.PRINCIPALAMT_CHANNEL == messageObj.getChannelid()) {
                    principal =  ((NumberFormatObject)messageObj.getMessage()).getValue();
                    percentageP = (float) (principal / (loanObject.getInterestAmt().getValue()+loanObject.getPrincipalAmt().getValue()))*100;
                    percentageI -= percentageP;
                }
                else {
                    interest =  ((NumberFormatObject)messageObj.getMessage()).getValue();
                    percentageI = (float) (interest / (loanObject.getInterestAmt().getValue()+loanObject.getPrincipalAmt().getValue()))*100;
                    percentageP -= percentageI;
                }

                mPrincipalGauge.setInterval(1);
                mPrincipalGauge.setPercentage(percentageP);
                mInterestGauge.setInterval(1);
                mInterestGauge.setPercentage(percentageI);
                mPrincipalGauge.resetState();
                mPrincipalGauge.invalidate();
                mInterestGauge.resetState();
                mInterestGauge.invalidate();
                mRestoreMenuItem.setVisible(true);
                mSaveMenuItem.setVisible(true);
            }
            else if (MessageChannel.NAME_CHANNEL == messageObj.getChannelid()) {
                mRestoreMenuItem.setVisible(true);
                mSaveMenuItem.setVisible(true);
            }
        }

    }

    public void registerSaveResetDelegate(ISaveResetDelegate delegate) {
        saveResetDelegates.add(delegate);
    }

    private void setControls() {
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
        TextView interestLbl = (TextView)findViewById(R.id.interestLbl);
        interestLbl.setText(loanObject.getInterestAmt().displayString());
        TextView principalLbl = (TextView)findViewById(R.id.principalLbl);
        principalLbl.setText(loanObject.getPrincipalAmt().displayString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_keyboard, menu);
        mRestoreMenuItem = menu.getItem(0);
        mSaveMenuItem = menu.getItem(1);
        mRestoreMenuItem.setVisible(false);
        mSaveMenuItem.setVisible(isAdd);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_done) {
            saveLoan();
            hideKeyboard();
            for (ISaveResetDelegate saveResetDelegate: saveResetDelegates) {
                saveResetDelegate.saveLoan();
            }
            mRestoreMenuItem.setVisible(false);
            mSaveMenuItem.setVisible(false);
            return true;
        }
        else if (id == R.id.action_restore) {
            resetLoan();
            hideKeyboard();
            for (ISaveResetDelegate saveResetDelegate: saveResetDelegates) {
                saveResetDelegate.resetLoan();
            }
            mRestoreMenuItem.setVisible(false);
            mSaveMenuItem.setVisible(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void saveLoan() {
        if (this.isAdd) {
            ModelObject.getInstance().persistLoan();
            this.isAdd = false;
        } else {
            ModelObject.getInstance().saveSelected();
        }
        ScheduleModel.getInstance().initializeArray(true);

        LoanObject loan = ModelObject.getInstance().getSelectedObject();
        MessageChannel.getInstance().publish(loan.getAmortization(),
                MessageChannel.AMORTIZATION_CHANNEL);
        MessageChannel.getInstance().publish(loan.getStartDate(),
                MessageChannel.STARTDATE_CHANNEL);
        MessageChannel.getInstance().publish(loan.getName(),
                MessageChannel.NAME_CHANNEL);
        MessageChannel.getInstance().publish(loan.getLoanAmount(),
                MessageChannel.LOANAMOUNT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getPaymentAmount(),
                MessageChannel.PAYMENTAMOUNT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getInterestRate(),
                MessageChannel.INTERESTRATE_CHANNEL);
        MessageChannel.getInstance().publish(loan.getInterestAmt(),
                MessageChannel.INTERESTAMT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getPrincipalAmt(),
                MessageChannel.PRINCIPALAMT_CHANNEL);

    }

    protected void resetLoan() {
        ModelObject.getInstance().resetSelected();
        ScheduleModel.getInstance().initializeArray(true);

        // need to reset start data and name
        LoanObject loan = ModelObject.getInstance().getSelectedObject();

        MessageChannel.getInstance().publish(loan.getAmortization(),
                MessageChannel.AMORTIZATION_CHANNEL);
        MessageChannel.getInstance().publish(loan.getStartDate(),
                MessageChannel.STARTDATE_CHANNEL);
        MessageChannel.getInstance().publish(loan.getName(),
                MessageChannel.NAME_CHANNEL);
        MessageChannel.getInstance().publish(loan.getLoanAmount(),
                MessageChannel.LOANAMOUNT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getPaymentAmount(),
                MessageChannel.PAYMENTAMOUNT_CHANNEL);

        MessageChannel.getInstance().publish(loan.getPaymentFreq(),
                MessageChannel.FREQPERIOD_CHANNEL);
        MessageChannel.getInstance().publish(loan.getCompoundPeriod(),
                MessageChannel.COMPOUNDPERIOD_CHANNEL);


        MessageChannel.getInstance().publish(loan.getPaymentAmount(),
                MessageChannel.PAYMENTAMOUNT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getInterestRate(),
                MessageChannel.INTERESTRATE_CHANNEL);
        MessageChannel.getInstance().publish(loan.getInterestAmt(),
                MessageChannel.INTERESTAMT_CHANNEL);
        MessageChannel.getInstance().publish(loan.getPrincipalAmt(),
                MessageChannel.PRINCIPALAMT_CHANNEL);
//        setControls();
//        saveBtn.setVisibility(View.INVISIBLE);
//        resetBtn.setVisibility(View.INVISIBLE);
//        scheduleBtn.setVisibility(View.VISIBLE);

    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        private ISaveResetActivity mSaveResetActivity;
        public AppSectionsPagerAdapter(FragmentManager fm, ISaveResetActivity activity) {
            super(fm);
            mSaveResetActivity = activity;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    DetailSectionFragment fragment = new DetailSectionFragment();
                    mSaveResetActivity.registerSaveResetDelegate(fragment);
                    return fragment;
                case 1:
                    return new LumpSumSectionFragment();
                case 2:
                    return new ScheduleSectionFragment();
                default:
                    return new ChartSectionFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Lump Sum";
                case 2:
                    return "Schedule";
                default:
                    return "Charts";
            }

        }
    }

    public static class ScheduleSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LoanObject sel = ModelObject.getInstance().getSelectedObject();
            LoanObject orig = ModelObject.getInstance().getSelectedOriginalObject();
            if ((sel.getPrincipalAmt().getDiff(orig.getPrincipalAmt()) != 0) ||
                    (sel.getInterestAmt().getDiff(orig.getInterestAmt()) != 0)) {
                ScheduleModel.getInstance().initializeArray(true);
            }

            View rootView = inflater.inflate(R.layout.schedule_fragment, container, false);
            final StickyListHeadersListView listview = (StickyListHeadersListView) rootView.findViewById(R.id.schedule);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    ScheduleModel.getInstance().toggleMode();
 //                   listview.invalidate();
                }
            });

            final ScheduleAdapter adapter = new ScheduleAdapter(getActivity());

            listview.setAdapter(adapter);

            ScheduleModel.getInstance().setScheduleDataChangeListener(new IScheduleDataChangeListener() {
                @Override
                public void dataChanged() {
                    adapter.notifyDataSetInvalidated();
                }
            });

            return rootView;
        }
    }

    public static class ScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;

        public ScheduleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ScheduleModel.getInstance().getCount();
        }

        @Override
        public Object getItem(int position) {
            return ScheduleModel.getInstance().getElement(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.scheduleitem, parent, false);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.month = (TextView) convertView.findViewById(R.id.month);
                holder.principal = (TextView) convertView.findViewById(R.id.principal);
                holder.interest = (TextView) convertView.findViewById(R.id.interest);
                holder.balance = (TextView) convertView.findViewById(R.id.total);
                holder.lumpsum = (TextView) convertView.findViewById(R.id.lumpsum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScheduleElement elem = ScheduleModel.getInstance().getElement(position);

            if (elem.getPaymentDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(elem.getPaymentDate());
                holder.date.setText(Integer.toString(cal.get(Calendar.DATE)));
                holder.month.setText(elem.getMonthDisplay());
                holder.principal.setText(elem.getPrincipalAmountDisplay());
                holder.interest.setText(elem.getInterestAmountDisplay());
                holder.balance.setText(elem.getBalanceDisplay());
                if (elem.getLumpsum() > 0) {
                    holder.lumpsum.setText("Lump Sum: " + elem.getLumpSumDisplay());
                    holder.lumpsum.setVisibility(View.VISIBLE);
                }
                else {
                    holder.lumpsum.setText("");
                    holder.lumpsum.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.date.setText("");
                holder.month.setText("");
                holder.principal.setText("");
                holder.interest.setText("");
                holder.balance.setText("");
                holder.lumpsum.setText("");
                holder.lumpsum.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.header, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            ScheduleElement elem = ScheduleModel.getInstance().getElement(position);
            if (elem.getPaymentDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(elem.getPaymentDate());
                holder.title.setText(Integer.toString(cal.get(Calendar.YEAR)));
            }
            else {
                holder.title.setText("");
            }
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            ScheduleElement elem = ScheduleModel.getInstance().getElement(position);
            if (elem.getPaymentDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(elem.getPaymentDate());
                return cal.get(Calendar.YEAR);
            }
            else {
                return 0;
            }
        }

        class HeaderViewHolder {
            TextView title;
        }

        class ViewHolder {
            TextView date;
            TextView month;
            TextView principal;
            TextView interest;
            TextView balance;
            TextView lumpsum;
        }
    }

    public static class LumpSumSectionFragment extends Fragment {
        private ViewGroup activePanel;
        private ViewGroup lumpSumPanel;
        private ViewGroup lumSumPeriodPanel;
        private LumpSumListAdapter mAdapter;
        private LumpSumController mLumpSumController;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.lumpsum_root, container, false);

            FrameLayout baseLayout = (FrameLayout)rootView.findViewById(R.id.scene_lumpsumroot);

            View startViews =  (ViewGroup)rootView.findViewById(R.id.lumpsum_layout);

            final ViewGroup endViews = (ViewGroup)inflater
                    .inflate(R.layout.lumpsumdetail, baseLayout, false);
            mLumpSumController = new LumpSumController(endViews);

            lumpSumPanel = (ViewGroup)endViews.findViewById(R.id.lumpSumPanel);
            lumpSumPanel.setVisibility(View.INVISIBLE);
            lumSumPeriodPanel = (ViewGroup)endViews.findViewById(R.id.lumSumPeriodPanel);
            lumSumPeriodPanel.setVisibility(View.INVISIBLE);

            final Scene endScene = new Scene(baseLayout, endViews);
            final Scene startScene = new Scene(baseLayout, startViews);
            final AutoTransition transition = new AutoTransition();
            transition.setDuration(100);
            transition.setInterpolator(new AccelerateDecelerateInterpolator());

            ImageButton addButton = (ImageButton) rootView.findViewById(R.id.add_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
                    LumpSum lumpSum = ModelObject.getInstance().createNewLumpSum();
                    lumpSum.getDuration().getStartDate().getDateValue().setTime(loanObject.getStartDate().getDateValue().getTime());
                    Calendar c = Calendar.getInstance();
                    c.setTime(lumpSum.getDuration().getStartDate().getDateValue());
                    c.add(Calendar.MONTH, loanObject.getAmortization().getMonths());
                    c.add(Calendar.YEAR, loanObject.getAmortization().getYears());

                    lumpSum.getDuration().getEndDate().setDateValue(c.getTime());
                    loanObject.getLumpSums().add(lumpSum);
                    ModelObject.getInstance().assignSelected(lumpSum);
                    mLumpSumController.onCreate(lumpSum);
                    TransitionManager.go(endScene, transition);
                }
            });

            final ImageButton backButton = (ImageButton) endViews.findViewById(R.id.back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.notifyDataSetInvalidated();
                    TransitionManager.go(startScene, transition);
                }
            });

            // Get ListView object from xml
            ListView listView = (ListView) rootView.findViewById(R.id.lumpsumlist);

            List<View> views = new ArrayList<View>();
            final ClickedView clickedView = new ClickedView();
            views.add(endViews.findViewById(R.id.lumpSumGrp));
            views.add(endViews.findViewById(R.id.lumSumPeriodGrp));

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

            for (View view : views) {
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (clickedView.view != null) {
                            clickedView.view.setBackgroundColor(0xFFFFFFFF);
                        }
                        if (clickedView.view != v) {
                            v.setBackgroundColor(0xFF33B5E5);
                            clickedView.view = v;
                        }
                        else {
                            clickedView.view = null;
                        }
                        slideUpDown(v, backButton);
                    }
                });
            }


            final View startDate = endViews.findViewById(R.id.startDateGrp);

            startDate.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(0xFF33B5E5);
                    } else {
                        v.setBackgroundColor(0xFFFFFFFF);
                    }

                    return false;
                }
            });

            final TextView lumpSumStartDate = (TextView)endViews.findViewById(R.id.lumpSumStartDate);
            startDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                    clickedView.view = v;
                    slideUpDown(v, backButton);

                    DatePickerFragment newFragment = new DatePickerFragment();
                    LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
                    newFragment.setDateObject(lumpSum.getDuration().getStartDate());
                    newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day, 0, 0);

                            if (c.getTime().getTime() <= lumpSum.getDuration().getEndDate().getDateValue().getTime()) {
                                lumpSum.getDuration().getStartDate().setDateValue(c.getTime());
                                lumpSumStartDate.setText(lumpSum.getDuration().getStartDate().displayString());
                            }
                            else {
                                CharSequence text = "Start Date must be before the End Date";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                            }


                        }
                    });
                    newFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });
            final View endDate = endViews.findViewById(R.id.endDateGrp);
            final TextView lumpSumEndDate = (TextView)endViews.findViewById(R.id.lumpSumEndDate);

            endDate.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(0xFF33B5E5);
                    } else {
                        v.setBackgroundColor(0xFFFFFFFF);
                    }

                    return false;
                }
            });
            endDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                    clickedView.view = v;
                    slideUpDown(v, backButton);

                    DatePickerFragment newFragment = new DatePickerFragment();
                    LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
                    newFragment.setDateObject(lumpSum.getDuration().getEndDate());
                    newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day, 0, 0);

                            if (c.getTime().getTime() >= lumpSum.getDuration().getStartDate().getDateValue().getTime()) {
                                lumpSum.getDuration().getEndDate().setDateValue(c.getTime());
                                lumpSumEndDate.setText(lumpSum.getDuration().getEndDate().displayString());
                            }
                            else {
                                CharSequence text = "End Date must be after the Start Date";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                            }

                        }
                    });
                    newFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            mAdapter = new LumpSumListAdapter(getActivity());

            // Assign adapter to ListView
            listView.setAdapter(mAdapter);
            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    LumpSum lumpSum = ModelObject.getInstance().getSelectedObject().getLumpSums().get(position);
                    ModelObject.getInstance().assignSelected(lumpSum);
                    mLumpSumController.onCreate(lumpSum);
                    TransitionManager.go(endScene, transition);
                }

            });

            SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(
                            listView,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        try {
                                            LumpSum item = (LumpSum)mAdapter.getItem(position);
                                            ModelObject.getInstance().getSelectedObject().getLumpSums().remove(item);
                                            ModelObject.getInstance().getSelectedObject().calculatePrincipalInterestAmt();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    //  mAdapter.notifyDataSetChanged();
                                }
                            });
            listView.setOnTouchListener(touchListener);
            // Setting this scroll listener is required to ensure that during ListView scrolling,
            // we don't look for swipes.
            listView.setOnScrollListener(touchListener.makeScrollListener());

            return rootView;
        }

        public void slideUpDown(final View view, final View backButton) {
            ViewGroup showPanel = null;
            switch (view.getId()) {
                case R.id.lumpSumGrp:
                    showPanel = lumpSumPanel;
                    break;
                case R.id.lumSumPeriodGrp:
                    showPanel = lumSumPeriodPanel;
                    break;
                default:
            }

            hideActivePanel(backButton);

            if ((showPanel != null) && (showPanel != activePanel)) {
                // Show the panel
                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.bottom_up);

                showPanel.startAnimation(bottomUp);
                showPanel.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                activePanel = showPanel;
            }
            else {
                activePanel = null;
            }

        }

        private void hideActivePanel(final View backButton) {
            if (activePanel != null) {
                // Hide the Panel
                Animation bottomDown = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.bottom_down);

                activePanel.startAnimation(bottomDown);
                activePanel.setVisibility(View.INVISIBLE);
                if (backButton != null) {
                    backButton.setVisibility(View.VISIBLE);
                }
            }
        }

        private void createPanelTransition(View rootView, Typeface font, LayoutInflater inflater,
                                           final Transition transition, int switchId, int rootPanelId,
                                           int startSceneId, int endSceneId) {
            FrameLayout baseLayout = (FrameLayout)rootView.findViewById(rootPanelId);

            ViewGroup startViews = (ViewGroup)inflater
                    .inflate(startSceneId, baseLayout, false);

            ViewGroup endViews = (ViewGroup)inflater
                    .inflate(endSceneId, baseLayout, false);
            final Scene scene1 = new Scene(baseLayout, endViews);
            final Scene scene2 = new Scene(baseLayout, startViews);
            final Switch switchBtn = (Switch)rootView.findViewById(switchId);
            switchBtn.setSwitchTypeface(font);
            //attach a listener to check for changes in state
            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {
                        TransitionManager.go(scene1, transition);
                    } else {
                        TransitionManager.go(scene2, transition);
                    }

                }
            });

        }

        private class ClickedView {
            public View view = null;
        }
    }

    public static class LumpSumListAdapter extends ArrayAdapter<LumpSum> implements Filterable {

        public LumpSumListAdapter(Context context) {
            super(context, -1, new LumpSum[0]);
        }

        @Override
        public int getCount() {
            return ModelObject.getInstance().getSelectedObject().getLumpSums().size();
        }

        @Override
        public LumpSum getItem(int position) {
            return ModelObject.getInstance().getSelectedObject().getLumpSums().get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.lumpsumcell, null);

            LumpSum lumpSum = getItem(position);
            bindViewHelper(view, lumpSum);
            return view;
        }

//        public LumpSumListAdapter(Context context, Cursor c, int flags)  {
//            super(context, c, flags);
//            mInflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        }
//
//        @Override
//        public View newView(Context ctxt, Cursor cursor, ViewGroup parent) {
//            View view = mInflater.inflate(R.layout.lumpsumcell, null);
//
//            LumpSum lumpSum = ModelObject.getInstance().addLumpsums(cursor);
//            bindViewHelper(view, lumpSum);
//            return view;
//        }
//
//        @Override
//        public void bindView(View view, Context ctxt, Cursor cursor) {
//            LumpSum lumpSum = ModelObject.getInstance().getLumpsum(cursor);
//            if (lumpSum == null) {
//                lumpSum = ModelObject.getInstance().addLumpsums(cursor);
//            }
//            bindViewHelper(view, lumpSum);
//        }

        private void bindViewHelper(View view, LumpSum lumpSum) {
            TextView lumpSumAmt = (TextView) view.findViewById(R.id.lumpSumAmt);
            lumpSumAmt.setText(lumpSum.getValue().displayString());

            TextView startendDate = (TextView) view.findViewById(R.id.startendDate);
            startendDate.setText(lumpSum.getDuration().displayString());

            TextView period = (TextView) view.findViewById(R.id.period);
            period.setText(lumpSum.getLumpsumPeriod().displayString());
        }
    }

    public static class ChartSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LoanObject sel = ModelObject.getInstance().getSelectedObject();
            LoanObject orig = ModelObject.getInstance().getSelectedOriginalObject();
            if ((sel.getPrincipalAmt().getDiff(orig.getPrincipalAmt()) != 0) ||
                    (sel.getInterestAmt().getDiff(orig.getInterestAmt()) != 0)) {
                ScheduleModel.getInstance().initializeArray(true);
            }

            View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
            final double maxVal = ModelObject.getInstance().getSelectedObject().getLoanAmount().getValue();

            int count = ScheduleModel.getInstance().getCount();
            int interval = count/5;
            float[] balanceYValuesYValues = new float[6];
            float[] principalYValues = new float[6];
            String[] xValues = new String[6];
            for (int x = 0; x < balanceYValuesYValues.length; x++) {
                int idx = interval*x;
                if (idx >= count) {
                    idx = count - 1;
                }
                ScheduleElement scheduleElement = ScheduleModel.getInstance().getElement(idx);
                principalYValues[x] = (float)scheduleElement.getPrincipalAmt();
                balanceYValuesYValues[x] = (float)(maxVal - principalYValues[x]);
                xValues[x] = scheduleElement.getMonthYearDisplay();
            }

            SimpleLineChart principalChart = (SimpleLineChart)rootView.findViewById(R.id.PrincipalLineChart);
            principalChart.setMaxY((float)maxVal);
            principalChart.setYValues(principalYValues);
            principalChart.setXValues(xValues);

            SimpleLineChart balanceChart = (SimpleLineChart)rootView.findViewById(R.id.BalanceLineChart);
            balanceChart.setMaxY((float) maxVal);
            balanceChart.setYValues(balanceYValuesYValues);
            balanceChart.setXValues(xValues);

            return rootView;
        }
    }

    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener mListener;
        private DateObject mDateObject;

        public DatePickerFragment() {
            super();
        }

        public void setDateObject(DateObject dateObject) {
            mDateObject = dateObject;
        }

        public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            mListener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            if (mDateObject != null) {
                c.setTime(mDateObject.getDateValue());
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), mListener, year, month, day);
        }

    }

    public static class DetailSectionFragment extends Fragment implements ISaveResetDelegate {
        private ViewGroup activePanel;
        private ViewGroup loanPanel;
        private ViewGroup interestPanel;
        private ViewGroup amortizationPanel;
        private ViewGroup paymentPanel;
        private NumberKeyboard paymentKeyboard;
        private NumberKeyboard loanAmtKeyboard;
        private NumberKeyboard interestKeyboard;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

            TextView loanArrow = (TextView)rootView.findViewById(R.id.loanAmtArrow);
            loanArrow.setTypeface(font);
            TextView amortizationArrow = (TextView)rootView.findViewById(R.id.amortizationArrow);
            amortizationArrow.setTypeface(font);
            TextView interestArrow = (TextView)rootView.findViewById(R.id.interestArrow);
            interestArrow.setTypeface(font);
            TextView paymentAmtArrow = (TextView)rootView.findViewById(R.id.paymentAmtArrow);
            paymentAmtArrow.setTypeface(font);

            setControls(rootView);

            final ClickedView clickedView = new ClickedView();

            loanPanel = (ViewGroup)rootView.findViewById(R.id.loanAmtPanel);
            loanAmtKeyboard = (NumberKeyboard)loanPanel.findViewById(R.id.loanAmtKeyboard);
            loanAmtKeyboard.setTitleViewOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideActivePanel();
                    activePanel = null;
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                }
            });

            loanPanel.setVisibility(View.INVISIBLE);
            interestPanel = (ViewGroup)rootView.findViewById(R.id.interestPanel);
            interestKeyboard = (NumberKeyboard)interestPanel.findViewById(R.id.interestKeyboard);
            interestKeyboard.setTitleViewOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideActivePanel();
                    activePanel = null;
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                }
            });
            interestPanel.setVisibility(View.INVISIBLE);
            amortizationPanel = (ViewGroup)rootView.findViewById(R.id.amortizationPanel);
            TextView amortizationPanelTitle = (TextView)rootView.findViewById(R.id.amortizationPanelTitle);
            amortizationPanelTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideActivePanel();
                    activePanel = null;
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                }
            });

            amortizationPanel.setVisibility(View.INVISIBLE);
            paymentPanel = (ViewGroup)rootView.findViewById(R.id.paymentPanel);
            paymentKeyboard = (NumberKeyboard)paymentPanel.findViewById(R.id.paymentKeyboard);
            paymentKeyboard.setTitleViewOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideActivePanel();
                    activePanel = null;
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                }
            });
            paymentPanel.setVisibility(View.INVISIBLE);
            final AutoTransition transition = new AutoTransition();
            transition.setDuration(100);
            transition.setInterpolator(new AccelerateDecelerateInterpolator());

            activePanel = null;
            List<View> views = new ArrayList<View>();
            views.add(rootView.findViewById(R.id.interestRateGrp));
            views.add(rootView.findViewById(R.id.loanAmtGrp));
            views.add(rootView.findViewById(R.id.paymentAmtGrp));
            views.add(rootView.findViewById(R.id.amortizationGrp));
            final TextView startDate = (TextView) rootView.findViewById(R.id.startDate);
            final View startDateGroup = rootView.findViewById(R.id.startDateGrp);
            startDateGroup.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(0xFF33B5E5);
                    } else {
                        v.setBackgroundColor(0xFFFFFFFF);
                    }

                    return false;
                }
            });

            startDateGroup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (clickedView.view != null) {
                        clickedView.view.setBackgroundColor(0xFFFFFFFF);
                    }
                    clickedView.view = v;
                    slideUpDown(v);

                    DatePickerFragment newFragment = new DatePickerFragment();

                    newFragment.setDateObject(ModelObject.getInstance().getSelectedObject().getStartDate());
                    newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day, 0, 0);

                            loanObject.getStartDate().setDateValue(c.getTime());
                            startDate.setText(loanObject.getStartDate().displayString());

                        }
                    });
                    newFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            });

            for (View view : views) {
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (clickedView.view != null) {
                            clickedView.view.setBackgroundColor(0xFFFFFFFF);
                        }
                        if (clickedView.view != v) {
                            v.setBackgroundColor(0xFF33B5E5);
                            clickedView.view = v;
                        }
                        else {
                            clickedView.view = null;
                        }
                        slideUpDown(v);
                    }
                });
            }
            return rootView;
        }

        public void updateSlider(int viewId) {
            if (viewId == -1) {
                loanAmtKeyboard.updateSlider();
                interestKeyboard.updateSlider();
                paymentKeyboard.updateSlider();
                SeekBar yearSeekBar = (SeekBar) amortizationPanel.findViewById(R.id.yearSeekBar);
                SeekBar monthSeekBar = (SeekBar) amortizationPanel.findViewById(R.id.monthSeekBar);
                LoanObject loan = ModelObject.getInstance().getSelectedObject();
                yearSeekBar.setProgress(loan.getAmortization().getYears());
                monthSeekBar.setProgress(loan.getAmortization().getMonths());

            }
            else {
                switch (viewId) {
                    case R.id.loanAmtGrp:
                        loanAmtKeyboard.updateSlider();
                        break;
                    case R.id.interestRateGrp:
                        interestKeyboard.updateSlider();
                        break;
                    case R.id.amortizationGrp:
                        SeekBar yearSeekBar = (SeekBar) amortizationPanel.findViewById(R.id.yearSeekBar);
                        SeekBar monthSeekBar = (SeekBar) amortizationPanel.findViewById(R.id.monthSeekBar);
                        LoanObject loan = ModelObject.getInstance().getSelectedObject();
                        yearSeekBar.setProgress(loan.getAmortization().getYears());
                        monthSeekBar.setProgress(loan.getAmortization().getMonths());

                        break;
                    case R.id.paymentAmtGrp:
                        paymentKeyboard.updateSlider();
                        break;
                    default:
                }
            }
        }
        public void slideUpDown(final View view) {
            ViewGroup showPanel = null;
            switch (view.getId()) {
                case R.id.loanAmtGrp:
                    showPanel = loanPanel;
                    break;
                case R.id.interestRateGrp:
                    showPanel = interestPanel;
                    break;
                case R.id.amortizationGrp:
                    showPanel = amortizationPanel;
                    break;
                case R.id.paymentAmtGrp:
                    showPanel = paymentPanel;
                    break;
                default:
            }
            updateSlider(view.getId());
            hideActivePanel();

            if ((showPanel != null) && (showPanel != activePanel)) {
                // Show the panel
                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.bottom_up);

                showPanel.startAnimation(bottomUp);
                showPanel.setVisibility(View.VISIBLE);
                activePanel = showPanel;
            }
            else {
                activePanel = null;
            }

        }

        private void hideActivePanel() {
            if (activePanel != null) {
                // Hide the Panel
                Animation bottomDown = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.bottom_down);

                activePanel.startAnimation(bottomDown);
                activePanel.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void saveLoan() {
            updateSlider(-1);
            hideActivePanel();
            activePanel = null;
        }

        @Override
        public void resetLoan() {
            updateSlider(-1);
            hideActivePanel();
            activePanel = null;
        }

        private class ClickedView {
            public View view = null;
        }

        private void setControls(View rootView) {
            LoanObject loanObject = ModelObject.getInstance().getSelectedObject();
            TextView loanAmt = (TextView) rootView.findViewById(R.id.loanAmt);
            loanAmt.setText(loanObject.getLoanAmount().displayString());
            TextView interestRate = (TextView) rootView.findViewById(R.id.interestRate);
            interestRate.setText(loanObject.getInterestRate().displayString());
            TextView interestPeriod = (TextView) rootView.findViewById(R.id.interestPeriod);
            interestPeriod.setText(loanObject.getCompoundPeriod().displayString());
            TextView amortization = (TextView) rootView.findViewById(R.id.amortization);
            amortization.setText(loanObject.getAmortization().displayString());
            TextView paymentAmt = (TextView) rootView.findViewById(R.id.paymentAmt);
            paymentAmt.setText(loanObject.getPaymentAmount().displayString());
            TextView paymentPeriod = (TextView) rootView.findViewById(R.id.paymentPeriod);
            paymentPeriod.setText(loanObject.getPaymentFreq().displayString());
            TextView startDate = (TextView) rootView.findViewById(R.id.startDate);
            startDate.setText(loanObject.getStartDate().displayString());

            SeekBar yearSeekBar = (SeekBar) rootView.findViewById(R.id.yearSeekBar);
            LoanObject loan = ModelObject.getInstance().getSelectedObject();
            yearSeekBar.setMax(50);
            yearSeekBar.setProgress(loan.getAmortization().getYears());
            yearSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekbar, int value, boolean arg2) {
                    LoanObject loan = ModelObject.getInstance().getSelectedObject();
                    loan.getAmortization().setYears(value);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    LoanObject loan = ModelObject.getInstance().getSelectedObject();
                    loan.calculatePaymentAmt();
                    loan.calculatePrincipalInterestAmt();
                }

            });
            SeekBar monthSeekBar = (SeekBar) rootView.findViewById(R.id.monthSeekBar);
            monthSeekBar.setMax(12);
            monthSeekBar.setProgress(loan.getAmortization().getMonths());
            monthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekbar, int value, boolean arg2) {
                    LoanObject loan = ModelObject.getInstance().getSelectedObject();
                    loan.getAmortization().setMonths(value);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    LoanObject loan = ModelObject.getInstance().getSelectedObject();
                    loan.calculatePaymentAmt();
                    loan.calculatePrincipalInterestAmt();
                }

            });


            DiffTextViewObserver loanAmtDiff = (DiffTextViewObserver) rootView.findViewById(R.id.loanAmtDiff);
            loanAmtDiff.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getLoanAmount();
                }
            });
            loanAmtDiff.updateText(loanObject.getLoanAmount());
            DiffArrowViewObserver loanAmtArrow = (DiffArrowViewObserver) rootView.findViewById(R.id.loanAmtArrow);
            loanAmtArrow.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getLoanAmount();
                }
            });

            DiffTextViewObserver paymentAmtDiff = (DiffTextViewObserver) rootView.findViewById(R.id.paymentAmtDiff);
            paymentAmtDiff.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getPaymentAmount();
                }
            });
            paymentAmtDiff.updateText(loanObject.getPaymentAmount());
            DiffArrowViewObserver paymentAmtArrow = (DiffArrowViewObserver) rootView.findViewById(R.id.paymentAmtArrow);
            paymentAmtArrow.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getPaymentAmount();
                }
            });
            DiffTextViewObserver amortizationDiff = (DiffTextViewObserver) rootView.findViewById(R.id.amortizationDiff);
            amortizationDiff.setOriginal(new IGetOriginalLoan(){
                public FormatObject getFormatObject(){
                    return ModelObject.getInstance().getSelectedOriginalObject().getAmortization();
                }
            });
            amortizationDiff.updateText(loanObject.getAmortization());
            DiffArrowViewObserver amortizationArrow = (DiffArrowViewObserver) rootView.findViewById(R.id.amortizationArrow);
            amortizationArrow.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getAmortization();
                }
            });

            DiffTextViewObserver interestDiff = (DiffTextViewObserver) rootView.findViewById(R.id.interestDiff);
            interestDiff.setOriginal(new IGetOriginalLoan(){
                public FormatObject getFormatObject(){
                    return ModelObject.getInstance().getSelectedOriginalObject().getInterestRate();
                }
            });
            interestDiff.updateText(loanObject.getInterestRate());
            DiffArrowViewObserver interestArrow = (DiffArrowViewObserver) rootView.findViewById(R.id.interestArrow);
            interestArrow.setOriginal(new IGetOriginalLoan() {
                public FormatObject getFormatObject() {
                    return ModelObject.getInstance().getSelectedOriginalObject().getInterestRate();
                }
            });

        }
    }

    public void onLumpSumPeriodClicked(View view) {
        LumpSum lumpSum = ModelObject.getInstance().getSelectedLumpSump();
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.lumpSumAnnual:
                if (checked)
                    lumpSum.getLumpsumPeriod().setLumpSumValue(LumpSumPeriodEnum.LUMPANNUAL);
                break;
            case R.id.lumpSumMonthly:
                if (checked)
                    lumpSum.getLumpsumPeriod().setLumpSumValue(LumpSumPeriodEnum.LUMPMONTHLY);
                break;
            default:
                if (checked)
                    lumpSum.getLumpsumPeriod().setLumpSumValue(LumpSumPeriodEnum.LUMPNONE);
                break;
        }
        loanObject.calculatePrincipalInterestAmt();
    }

    public void onRadioButtonClicked(View view) {
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.monthly:
                if (checked)
                    loanObject.getPaymentFreq().setFreqValue(PaymentFreqEnum.MONTHLY);
                break;
            case R.id.bimonthly:
                if (checked)
                    loanObject.getPaymentFreq().setFreqValue(PaymentFreqEnum.BIMONTHLY);
                break;
            case R.id.weekly:
                if (checked)
                    loanObject.getPaymentFreq().setFreqValue(PaymentFreqEnum.WEEKLY);
                break;
            case R.id.biweekly:
                if (checked)
                    loanObject.getPaymentFreq().setFreqValue(PaymentFreqEnum.BIWEEKLY);
                break;
        }
        loanObject.calculatePrincipalInterestAmt();
    }

    public void onInterestRatePeriodClicked(View view) {
        LoanObject loanObject = ModelObject.getInstance().getSelectedObject();

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.monthly:
                if (checked)
                    loanObject.getCompoundPeriod().setCompoundValue(CompoundPeriodEnum.CMONTHLY);
                break;
            case R.id.annually:
                if (checked)
                    loanObject.getCompoundPeriod().setCompoundValue(CompoundPeriodEnum.CANNUAL);
                break;
            case R.id.semiannual:
                if (checked)
                    loanObject.getCompoundPeriod().setCompoundValue(CompoundPeriodEnum.CSEMIANNUAL);
                break;
        }
        loanObject.calculatePaymentAmt();
        loanObject.calculatePrincipalInterestAmt();
    }
}
