package com.mtg.trade.guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TradeScreenFragment extends Fragment  {
	private CardListLayout mCards;
	private TextView mPriceSumTextView;
	private TradeActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
		
		LinearLayout view = (LinearLayout)inflater.inflate(R.layout.trade_list_tab, container, false);
		mCards = (CardListLayout) view.findViewById(R.id.cardList);
		mPriceSumTextView = (TextView) view.findViewById(R.id.totalCost);

        if (savedInstanceState != null) {
        	for (int i = 0; ; i++) {
        		String[] cardData = savedInstanceState.getStringArray("card"+i);
        		if (cardData == null)
        			break;
            	CardDataQuantity cardView = new CardDataQuantity(mActivity, null);
            	cardView.setAllData(cardData);
            	mCards.addCardToView(cardView);    	
        	}
        }
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (TradeActivity) activity;
	} 
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
    	int cards = mCards.getChildCount();
    	for (int i = 0; i < cards; i++) {
    		CardDataQuantity card = (CardDataQuantity) mCards.getChildAt(i);
    		String[] cardData = card.getAllData();
    		outState.putStringArray("card" + i, cardData);
    	}
	}
	
	public void loadList(String list) {
		if (mActivity == null) {
			mActivity = (TradeActivity) getActivity();
		}
			
    	SharedPreferences settings = mActivity.getSharedPreferences(list, 0);
        for (int i = 0; ; i++) {
        	
        	String prefix = "card" + i;
        	
    		String name = settings.getString(prefix + "_name", null);
    		if (name == null)
    			break;
    		String edition = settings.getString(prefix + "_edition", null);
    		float price = settings.getFloat(prefix + "_price", 0);
    		String condition = settings.getString(prefix + "_condition", null);
    		String productId = settings.getString(prefix + "_productId", null);
    		int quantity = settings.getInt(prefix + "_quantity", 1);
    		
    		CardDataQuantity c = new CardDataQuantity(mActivity, null);
    		c.setName(name);
    		c.setEdition(edition);
    		c.setPrice(price);
    		c.setCondition(condition);
    		c.setProductId(productId);
    		c.setQuantity(quantity);
    		
    		mCards.addCardToView(c);
    		c.setFragmentContainer(this);
    	}
        recalculate();
	}
    
    public void recalculate() {
    	mPriceSumTextView.setText("Total Price: $" + String.format("%.2f", mCards.getPriceSum()));
    	mActivity.judgeTrade();
    }
    
    public CardListLayout getCardList() {
    	return mCards;
    }
    
    public float getPriceSum() {
    	if (mCards == null)
    		return 0;
    	return mCards.getPriceSum();
    }
}