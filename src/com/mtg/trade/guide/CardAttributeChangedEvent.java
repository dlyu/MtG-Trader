package com.mtg.trade.guide;

import java.util.EventObject;

public class CardAttributeChangedEvent extends EventObject {
	private int mOldQty;
	private int mNewQty;
	private int mMisc;
	private RawCardData mData;
	
	public CardAttributeChangedEvent(Object source) {
		super(source);
		mData = (RawCardData)source;
	}

	public void setOldQuantity(int q) {
		mOldQty = q;
	}
	
	public void setNewQuantity(int q) {
		mNewQty = q;
	}
	
	public void setMiscellaneous(int misc) {
		mMisc = misc;
	}
	
	public int getOldQuantity() {
		return mOldQty;
	}
	
	public int getNewQuantity() {
		return mNewQty;
	}
	
	public RawCardData getSource() {
		return mData;
	}
	
	public int getMiscellaneous() {
		return mMisc;
	}
}