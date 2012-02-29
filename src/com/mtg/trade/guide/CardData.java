package com.mtg.trade.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class CardData extends LinearLayout {
	public static final int sMandatoryFieldsCount = 6;
	
	protected String mName;
	protected String mEdition;
	protected float mPrice;
	protected String mCondition;
	protected String mProductId;
	protected Context mContext;
	
	public CardData(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getEdition() {
		return mEdition;
	}
	
	public float getPrice() {
		return mPrice;
	}
	
	public String getCondition() {
		return mCondition;
	}
	
	public String getProductId() {
		return mProductId;
	}
	
	public void setName(String name) {
		mName = name;
		refreshMetrics();
	}
	
	public void setEdition(String edition) {
		mEdition = edition;
		refreshMetrics();
	}
	
	public void setPrice(float price) {
		mPrice = price;
		refreshMetrics();
	}
	
	public void setCondition(String condition) {
		mCondition = condition;
		refreshMetrics();
	}
	
	public void setProductId(String productId) {
		mProductId = productId;
		refreshMetrics();
	}
	
	public abstract String[] getAllData();
	public abstract void setAllData(String[] data);
	public abstract void refreshMetrics();
}
