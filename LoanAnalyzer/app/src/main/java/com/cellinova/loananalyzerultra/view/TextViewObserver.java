package com.cellinova.loananalyzerultra.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cellinova.loananalyzerultra.R;
import com.cellinova.loananalyzerultra.model.FormatObject;
import com.cellinova.loananalyzerultra.model.Message;
import com.cellinova.loananalyzerultra.model.MessageChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TextViewObserver extends TextView implements Observer {

	protected List<Integer> mChannelIds = new ArrayList<Integer>();

	public TextViewObserver(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.TextViewObserver,
				0, 0
		);


		try {
			int channelId = a.getInteger(R.styleable.TextViewObserver_channelid, 0);
			addChannel(channelId);
			MessageChannel.getInstance().addObserver(this);

		} finally {
			// release the TypedArray so that it can be reused.
			a.recycle();
		}

	}

	public TextViewObserver(Context context) {
		super(context);
	}
	public TextViewObserver(Context context, int channelid) {
		this(context);
		addChannel(channelid);
	}
	
	public void addChannel(int channelid){
		this.mChannelIds.add(channelid);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof Message){
			Message messageObj = (Message)data;
			if (this.mChannelIds.contains(messageObj.getChannelid()))
				if (messageObj.getMessage() instanceof FormatObject)
					setText(((FormatObject)messageObj.getMessage()).displayString());
				else if (messageObj.getMessage() instanceof String)
					setText((String)messageObj.getMessage());
		}
		else if (data instanceof Observable){
			((Observable)data).addObserver(this);
		}
		else if (observable instanceof FormatObject){
			setText(((FormatObject)observable).displayString());
		}

	}

	

}
