package com.mtg.trade.guide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TradeListView extends LinearLayout {
	private Context mContext;
	private TextView mTradeLastModifyView;
	private TextView mTradeStatusView;
	private TextView mTradeDescView;
	
	public TradeListView(Context context, AttributeSet attrs, String filename, String status, String desc) {
		super(context, attrs);
		mContext = context;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.trade_file_view, this);

		mTradeLastModifyView = (TextView)findViewById(R.id.trade_file_last_modify);
		mTradeStatusView = (TextView)findViewById(R.id.trade_file_status);
		mTradeDescView = (TextView)findViewById(R.id.trade_file_desc);
		
		mTradeLastModifyView.setText(filename);
		mTradeStatusView.setText(status);
		mTradeDescView.setText(desc);
	}
}
