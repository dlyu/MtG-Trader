package com.mtg.trade.guide;

import java.util.Vector;

public class RawCardData {
	private String mName;
	private String mEdition;
	private float mPrice;
	private String mCondition;
	private String mProductId;
	private int mQuantity;
	private CardDataView mView;
	
	Vector<CardAttributeChangedListener> mQtyChangeListeners;
	
	public RawCardData(String name, String edition, float price, String condition, String productId) {
		this(name, edition, price, condition, productId, 1, null);
	}
	
	public RawCardData(String name, String edition, float price, String condition, String productId, int quantity) {
		this(name, edition, price, condition, productId, quantity, null);
	}

	public RawCardData(String[] data) {
		this(data[0], data[1], Float.parseFloat(data[2]), data[3], data[4], Integer.parseInt(data[5]), null);
	}
	
	public RawCardData(String[] data, CardDataView view) {
		this(data[0], data[1], Float.parseFloat(data[2]), data[3], data[4], Integer.parseInt(data[5]), view);
	}
	
	public RawCardData(String name, String edition, float price, String condition, String productId, int quantity, CardDataView view) {
		mQtyChangeListeners = new Vector<CardAttributeChangedListener>();
		
		setName(name);
		setEdition(edition);
		setPrice(price);
		setCondition(condition);
		setProductId(productId);
		setQuantity(quantity);
		bindView(view);
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
	
	public int getQuantity() {
		//return Integer.parseInt(mQuantity.getText().toString());
		return mQuantity;
	}
	
	public CardDataView getView() {
		return mView;
	}

	public String[] getDataArray() {
		return new String[] {mName, mEdition, "" + mPrice, mCondition, mProductId, "" + mQuantity};
	}
	
	public void bindView(CardDataView view) {
		if (view != null) {
			mView = view;
			mView.refreshMetrics();
		}
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setEdition(String edition) {
		mEdition = edition;
	}
	
	public void setPrice(float price) {
		mPrice = price;
	}
	
	public void setCondition(String condition) {
		mCondition = condition;
	}
	
	public void setProductId(String productId) {
		mProductId = productId;
	}
	
	public void setQuantity(int quantity) {
		setQuantity(quantity, -1);
	}
	
	public void setQuantity(int quantity, int misc) {
		CardAttributeChangedEvent q = new CardAttributeChangedEvent(this);
		q.setOldQuantity(mQuantity);
		q.setNewQuantity(quantity);
		q.setMiscellaneous(misc);
		mQuantity = quantity;
		
		for (int i = 0; i < mQtyChangeListeners.size(); i++) {
			mQtyChangeListeners.get(i).onQuantityChange(q);
		}
	}
	
	public void setCardAttributeChangeListener(CardAttributeChangedListener q) {
		mQtyChangeListeners.add(q);
	}
	
	public void addQuantity(int disp, int misc) {
		setQuantity(mQuantity + disp, misc);
	}
	
	public void increment() {
		mQuantity++;
	}
	
	public void decrement() {
		if (mQuantity > 0) {
			mQuantity--;			
		}
	}
	
	public void onRemove() {
		for (int i = 0; i < mQtyChangeListeners.size(); i++) {
			mQtyChangeListeners.get(i).onRemove();
		}
	}
}
