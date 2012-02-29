package com.mtg.trade.guide;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CardListLayout extends LinearLayout {
	protected HashMap<Integer, CardDataQuantity> mListMap = new HashMap<Integer, CardDataQuantity>();
	private float mPriceSum;
	
	public CardListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		mListMap.clear();
	}

    /*
     * This method takes a card object and determines whether to add it to the results View or increment the quantity of its duplicate that's already on the results View.
     * @params card: The CardDataQuantity object to add
     * */
	public void addCardToView(CardDataQuantity card) {
		this.addCardToView(card, true);
	}
	
    /*
     * This method takes a card object and determines whether to add it to the results View. If a duplicate is found, then the second parameter determines what to do with it.
     * @params card: The CardDataQuantity object to add
     * @params allowDuplicates: There may be more than one of the same product requested to be added (due to the simplistic way I've implemented adding from search to wishlist/inventory).
     * 							If true, then the card that is in the list will have its quantity incremented by one. Otherwise the new card will be ignored.
     * */
	public void addCardToView(CardDataQuantity card, boolean allowDuplicates) {
    	int productHash = GlobalConstants.getProductHashFromView(card);
    	
    	// If the unique hash already exists, then increment the duplicate
    	if (mListMap.containsKey(productHash)) {
    		if (allowDuplicates) {
    			card = mListMap.get(productHash);
        		card.increment();	
    		}
    	}
    	// Otherwise the new card is not in the results View; add it
    	else {
    		mListMap.put(productHash, card);
    		this.addView(card);    		
    	}
	}
	
	public void removeCardFromView(CardDataQuantity card) {
		int idx = this.indexOfChild(card);
		int productHash = GlobalConstants.getProductHashFromView(card);
		mListMap.remove(productHash);
		this.removeViewAt(idx);
	}
	
	public float getPriceSum() {
		mPriceSum = 0;
    	Iterator<CardDataQuantity> cards = mListMap.values().iterator();
    	while (cards.hasNext()) {
    		mPriceSum += ((CardDataQuantity)cards.next()).getTotalPrice();
    	}
    	return mPriceSum;
	}
}
