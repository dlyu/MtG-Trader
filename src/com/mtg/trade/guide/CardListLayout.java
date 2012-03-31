package com.mtg.trade.guide;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.CheckBox;

public class CardListLayout extends LinearLayout implements CardAttributeChangedListener {
	protected HashMap<Integer, RawCardData> mListMap = new HashMap<Integer, RawCardData>();
	private float mPriceSum;

	private boolean mCheckable;
	private boolean mQuantifiable;
	
	private Context mContext;
	//private CardListActionReturnable mSavedAction, mLastAction;
	private int mCurrentActionIdx = 0;
	
	private final int IGNORE_LISTENER = 1;
	//private boolean mCardsLoaded = false;
	
	//private boolean mChainedActionMode = false;
	
	public CardListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CardListLayout);
		setCardViewConfigurable(a.getBoolean(R.styleable.CardListLayout_cardsCheckable, true), a.getBoolean(R.styleable.CardListLayout_cardsQuantifiable, true));
		a.recycle();
	}
	
	public void setCardViewConfigurable(boolean checkable, boolean quantifiable) {
		mCheckable = checkable;
		mQuantifiable = quantifiable;
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		mListMap.clear();
	}
	/*
	@Override
	public void removeAllViews() {
		super.removeAllViews();
		//mListMap.clear();
		
		Iterator<RawCardData> allCards = mListMap.values().iterator();

		toggleChainedMode();
    	while (allCards.hasNext()) {
    		RawCardData data = allCards.next();
    		CardDataView view = data.getView();
    		if (view.getActive())
    			removeCardFromView(data);
    	}
    	toggleChainedMode();
	}*/
	
	@Override
	public void onQuantityChange(CardAttributeChangedEvent q) {
		/*
		if (q.getMiscellaneous() != this.IGNORE_LISTENER) {
			int displacement = q.getNewQuantity() - q.getOldQuantity();
			CardListAction qtyChange = new CardListAction(GlobalConstants.getProductHashFromView(q.getSource()), CardListAction.ACTION_QUANTITY_CHANGE, displacement);
			queueToActionStack(qtyChange);			
		}*/
	}
	/*
	public boolean undo() {
		CardListActionReturnable action = dequeueFromActionStack();
		if (action == null)
			return false;
		executeAction(action, true);
		return true;
	}
	
	private void queueToActionStack(CardListActionReturnable action) {
		if (!mCardsLoaded)
			return;
		
		// The head of the linked list is null, so just append it
		if (mLastAction == null) {
			mLastAction = action;
			mCurrentActionIdx++;
		}
			
		else {
			if (mChainedActionMode) {
				action.appendChain(mLastAction.getCardListActions());
			}
			else {
				mLastAction.setNext(action);
				action.setPrev(mLastAction);
				mLastAction = action;
				mCurrentActionIdx++;
			}
		}
	}*/
	
	/*
	 * Dequeues from the action stack and returns the action that was undone.
	 * @returns: The CardListAction object that represents the action that was undone, which will be used to perform the actual undo action
	 * */
	/*
	private CardListActionReturnable dequeueFromActionStack() {
		CardListActionReturnable ret = null;
		if (mCurrentActionIdx > 0) {
			ret = mLastAction;
			
			mLastAction = mLastAction.getPrev();
			if (mLastAction != null)
				mLastAction.setNext(null);
			mCurrentActionIdx--;
		}
		return ret;
	}
	
	private void executeAction(CardListActionReturnable action) {
		executeAction(action, false);
	}
	
	private void executeAction(CardListActionReturnable action, boolean inverse) {
		CardListAction cardAction = action.getCardListActions();
		executeCardListAction(cardAction, inverse);
	}
	
	private void executeCardListAction(CardListAction action, boolean inverse) {
		int productHash = action.getProductHash();
		RawCardData data = mListMap.get(productHash);
		
		switch(action.getAction()) {
		case CardListAction.ACTION_ADD:
			data.getView().setActive(!inverse);
			break;
		case CardListAction.ACTION_DELETE:
			data.getView().setActive(inverse);
			break;
		case CardListAction.ACTION_QUANTITY_CHANGE:
			data.addQuantity(action.getQuantity() * (inverse ? -1 : 1), this.IGNORE_LISTENER);
			break;
		case CardListAction.ACTION_CHECKED:
			break;
		}
		
		CardListAction next = action.getChainNext();
		if (next != null) {
			executeCardListAction(next, inverse);
		}
	}*/

    /*
     * This method takes a card object and determines whether to add it to the results View or increment the quantity of its duplicate that's already on the results View.
     * @params card: The CardDataQuantity object to add
     * */
	public void addCardToView(RawCardData card) {
		this.addCardToView(card, true);
	}
	
    /*
     * This method takes a card object and determines whether to add it to the results View. If a duplicate is found, then the second parameter determines what to do with it.
     * @params card: The CardDataQuantity object to add
     * @params allowDuplicates: There may be more than one of the same product requested to be added (due to the simplistic way I've implemented adding from search to wishlist/inventory).
     * 							If true, then the card that is in the list will have its quantity incremented by one. Otherwise the new card will be ignored.
     * */
	public void addCardToView(RawCardData card, boolean allowDuplicates) {
    	int productHash = GlobalConstants.getProductHashFromView(card);
    	
    	// If the unique hash already exists, then increment the duplicate
    	if (mListMap.containsKey(productHash)) {
    		if (allowDuplicates) {
    			card = mListMap.get(productHash);
        		//card.increment();	
    		}
    	}
    	// Otherwise the new card is not in the results View; add it
    	else {
    		mListMap.put(productHash, card);
    		CardDataView view = new CardDataView(mContext, null, card, mCheckable, mQuantifiable);
    		this.addView(view);
    		card.setCardAttributeChangeListener(this);  		
    	}
	}
	
    /*
     * This method takes a card object and determines whether to add it to the results View. If a duplicate is found, then the second parameter determines what to do with it.
     * @params card: The CardDataQuantity object to add
     * @params allowDuplicates: There may be more than one of the same product requested to be added (due to the simplistic way I've implemented adding from search to wishlist/inventory).
     * 							If true, then the card that is in the list will have its quantity incremented by one. Otherwise the new card will be ignored.
     * 
	public void addCardToView(RawCardData card, boolean allowDuplicates) {
    	int productHash = GlobalConstants.getProductHashFromView(card);
    	
    	// If the unique hash already exists, then increment the duplicate
    	if (mListMap.containsKey(productHash)) {
    		RawCardData data = mListMap.get(productHash);
    		CardDataView view = data.getView();
    		
    		if (!view.getActive()) {
    			int oldQty = data.getQuantity();
    			data.setQuantity(1, this.IGNORE_LISTENER);
    			CardListAction setToOneAction = new CardListAction(productHash, CardListAction.ACTION_QUANTITY_CHANGE, 1 - oldQty);
    			CardListAction addAction = new CardListAction(productHash, CardListAction.ACTION_ADD);

    			// Adding a "disabled" card back to the list involves setting the quantity to 1 and using the add action. The two actions need to be chained.
    			queueToActionStack(setToOneAction);
    			
    			boolean temporaryChainMode = false;
    			
    			if (!mChainedActionMode) {
    				toggleChainedMode();
    				temporaryChainMode = true;
    			}
    			
    			queueToActionStack(addAction);
    			
    			if (mChainedActionMode && temporaryChainMode)
    				toggleChainedMode();
    		}
    	}
    	// Otherwise the new card is not in the results View; add it
    	else {
    		mListMap.put(productHash, card);
    		CardDataView view = new CardDataView(mContext, null, card, mCheckable, mQuantifiable);
    		this.addView(view);
    		card.setQuantityChangeListener(this);

    		CardListAction addAction = new CardListAction(productHash, CardListAction.ACTION_ADD);
			queueToActionStack(addAction);	
    	}
	}*/
	
	/*
	 * Removes a specific card view from the list and from the product hashmap.
	 * @param card: The View of the card to remove
	 * */
	public void removeCardFromView(RawCardData card) {
		CardDataView view = card.getView();
		int productHash = GlobalConstants.getProductHashFromView(card);
		if (view == null)
			return;
		int idx = this.indexOfChild(view);

		mListMap.remove(productHash);
		this.removeViewAt(idx);
		card.onRemove();
		
		//view.setActive(false); // "delete" the card by making it invisible
		//CardListAction removeAction = new CardListAction(GlobalConstants.getProductHashFromView(card), CardListAction.ACTION_DELETE);
		//queueToActionStack(removeAction);
	}

	public Vector<RawCardData> getAllCheckedCards() {
		Vector<RawCardData> checked = new Vector<RawCardData>();
		Iterator<RawCardData> allCards = mListMap.values().iterator();

    	while (allCards.hasNext()) {
    		RawCardData data = allCards.next();
    		CardDataView view = data.getView();
    		//if (!view.getActive())
    		//	continue;
    		
			if (view.getChecked() || !mCheckable) {
				checked.add(data);
			}
    	}
		
		return checked;
	}
	
	/*
	 * Sets and returns the sum of the prices for all the cards in this list.
	 * @return: the price sum of all the cards represented in this view. 
	 * */
	public float getPriceSum() {
		mPriceSum = 0;
    	Iterator<RawCardData> cards = mListMap.values().iterator();
    	while (cards.hasNext()) {
    		CardDataView view = cards.next().getView();
    		//if (view.getActive())
    			mPriceSum += view.getTotalPrice();
    	}
    	return mPriceSum;
	}
	
	public void selectAll() {
		selectAll(true);
	}
	
	public void selectAll(boolean state) {
		for (int i = 0; i < this.getChildCount(); i++) {
			CardDataView view = (CardDataView) this.getChildAt(i);
			view.setChecked(state);
		}
	}
	
	/*
	public void setLoadComplete() {
		mCardsLoaded = true;
	}
	*/
	
	/*
	 * Rearranges the cards presented in this view alphabetically. 
	 * */
	public void sortByName() {
		
	}

	/*
	 * Rearranges the cards presented in this view by card price. 
	 * */
	public void sortByPrice() {
		
	}
	
	/*
	 * Rearranges the cards presented in this view by card edition. 
	 * */
	public void sortByEdition() {
		
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	public void toggleChainedMode() {
		if (!mChainedActionMode) {
			CardListActionQueue queue = new CardListActionQueue();
			queueToActionStack(queue);
		}
		mChainedActionMode = !mChainedActionMode;
	}
	
	private interface CardListActionReturnable {
		// The head of the linked list representing the series of actions to be executed
		public abstract CardListAction getCardListActions();
		public abstract CardListActionReturnable getPrev();
		public abstract CardListActionReturnable getNext();
		public abstract void setPrev(CardListActionReturnable prev);
		public abstract void setNext(CardListActionReturnable next);
		public abstract void appendChain(CardListAction nextInChain);
	}
	
	private class CardListActionQueue implements CardListActionReturnable {
		private CardListAction mHead;
		private CardListAction mTail;
		
		private CardListActionReturnable mPrev;
		private CardListActionReturnable mNext;
		
		@Override
		public CardListAction getCardListActions() {
			Log.d("Getting action from cluster", "Derp");
			return mHead;
		}

		@Override
		public CardListActionReturnable getPrev() {
			return mPrev;
		}
		
		@Override
		public CardListActionReturnable getNext() {
			return mNext;
		}
		
		@Override
		public void setPrev(CardListActionReturnable prev) {
			mPrev = prev;
		}
		
		@Override
		public void setNext(CardListActionReturnable next) {
			mNext = next;
		}

		@Override
		public void appendChain(CardListAction nextInChain) {
			if (mTail == null) {
				mHead = nextInChain;
				mTail = nextInChain;
			}
				
			else {
				mTail.setNextInChain(nextInChain);
				mTail = nextInChain;
			}
		}
	} 
	
	private class CardListAction implements CardListActionReturnable {
		static final int ACTION_QUANTITY_CHANGE = 0;
		static final int ACTION_ADD = 1;
		static final int ACTION_DELETE = 2;
		static final int ACTION_CHECKED = 3;
		
		private int mProductHash;
		private int mAction;
		private int mQuantity;
		private CardListAction mChainNext; // For grouped events
		
		private CardListActionReturnable mPrev;
		private CardListActionReturnable mNext;
		
		public CardListAction(int product, int action) {
			this(product, action, -1);
		}
		
		public CardListAction(int product, int action, int quantity) {
			this.mProductHash = product;
			this.mAction = action;
			this.mQuantity = quantity;
			
			Log.d("Created CardListAction", String.format("Product Hash = %d, Action = %d, Quantity = %d", mProductHash, mAction, mQuantity));
		}
		
		public int getProductHash() {
			return mProductHash;
		}
		
		public int getAction() {
			return mAction;
		}
		
		public int getQuantity() {
			return mQuantity;
		}

		public CardListAction getChainNext() {
			return mChainNext;
		}
		
		public void setNextInChain(CardListAction action) {
			mChainNext = action;
		}
		
		@Override
		public void appendChain(CardListAction action) {
			
		}

		@Override
		public CardListAction getCardListActions() {
			Log.d("Getting action from individual", "Derp");
			return this;
		}

		@Override
		public CardListActionReturnable getPrev() {
			return mPrev;
		}
		
		@Override
		public CardListActionReturnable getNext() {
			return mNext;
		}
		
		@Override
		public void setPrev(CardListActionReturnable prev) {
			mPrev = prev;
		}
		
		@Override
		public void setNext(CardListActionReturnable next) {
			mNext = next;
		}
	}
	*/
}
