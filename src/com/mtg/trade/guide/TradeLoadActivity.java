package com.mtg.trade.guide;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class TradeLoadActivity extends Activity {
	private LinearLayout mTradeHistoryList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tradeload);
        
        mTradeHistoryList = (LinearLayout)findViewById(R.id.trade_history);
        String[] fileList = this.fileList();
        
        for (int i = 0; i < fileList.length; i++) {
        	String xml = "";
        	
        }
    }
}