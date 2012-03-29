package com.mtg.trade.guide;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class CardListActivity extends Activity implements Returnable {
	protected CardListLayout mList;
	protected String mPreferencesListName;
	protected String mTableName;
	
	protected Toast mToastSaved;
	protected Toast mToastReverted;
	
	protected AlertDialog.Builder mSavePrompt; 
	
	protected boolean mCallbackMode;

	// Save
	protected DialogInterface.OnClickListener mPositiveButtonListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			writeToStorageList(mPreferencesListName);
			//writeToDatabase();
			finish();
		}
	};
	
	// Discard
	protected DialogInterface.OnClickListener mNegativeButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	// Return
	protected DialogInterface.OnClickListener mNeutralButtonListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
							
		}
	};

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == GlobalConstants.sFlagCallback) {
    		if (resultCode == RESULT_OK) {
    			//mList.toggleChainedMode();
    			for (int i = 0; ; i++) {
    				// Display cards on wishlist
    				String[] cardData = data.getStringArrayExtra("card" + i);
    				if (cardData == null)
    					break;

    				RawCardData c = new RawCardData(cardData, null);
    				mList.addCardToView(c, false);
    			}
    			//mList.toggleChainedMode();
    		}
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mCallbackMode) {
        	inflater.inflate(R.menu.cardlistoptions_callback, menu);
        }
        else {
        	inflater.inflate(R.menu.cardlistoptions, menu);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		// Only available in non-callback mode
	    	case R.id.card_list_search:
				Intent i = new Intent(CardListActivity.this, SearchActivity.class);
				i.putExtra("requestCode", GlobalConstants.sFlagCallback);
				startActivityForResult(i, GlobalConstants.sFlagCallback);
	    		break;
	    	// Only available in non-callback mode
    		case R.id.card_list_save:
				writeToStorageList(mPreferencesListName);
    			//writeToDatabase();
				if (mToastSaved != null)
					mToastSaved.show();
				return(true);
			case R.id.card_list_revert:
				mList.removeAllViews();
				readFromStorageList(mPreferencesListName);
				if (mToastReverted != null)
					mToastReverted.show();
				return(true);
			// Only available in callback mode
			case R.id.card_list_cancel:
				finish();
				break;
			case R.id.card_list_select_all:
				mList.selectAll();
				break;
			case R.id.card_list_unselect_all:
				mList.selectAll(false);
				break;
    	}
  
    	return(super.onOptionsItemSelected(item));
    }
    
    /*
     * Reads the last card list from the SharedPreferences named after the argument
     * @param pref: The name of the SharedPreferences from which the card list will be read
     * */
    /**/
    protected void readFromStorageList(String pref) {
    	SharedPreferences settings = getSharedPreferences(pref, 0);
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
    		
    		RawCardData c = new RawCardData(name, edition, price, condition, productId, quantity, null);
    		mList.addCardToView(c, false);
    	}
    }
    
    /*
     * Saves the current card list into the SharedPreferences named after the argument
     * @param pref: The name of the SharedPreferences in which the card list will be stored
     * */
    protected void writeToStorageList(String pref) {
        SharedPreferences settings = getSharedPreferences(pref, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        
    	int cards = mList.getChildCount();
    	for (int i = 0, j = 0; i < cards; i++) {
    		RawCardData card = ((CardDataView) mList.getChildAt(i)).getCardData();
    		String name = card.getName();
    		String edition = card.getEdition();
    		float price = card.getPrice();
    		String condition = card.getCondition();
    		String productId = card.getProductId();
    		int quantity = card.getQuantity();
    		if (quantity == 0)
    			continue;
    		String prefix = "card" + j++;
    		
    		editor.putString(prefix + "_name", name);
    		editor.putString(prefix + "_edition", edition);
    		editor.putFloat(prefix + "_price", price);
    		editor.putString(prefix + "_condition", condition);
    		editor.putString(prefix + "_productId", productId);
    		editor.putInt(prefix + "_quantity", quantity);
    	}
    	
        // Commit the edits!
        editor.commit();
    }
    
    /*
    protected void writeToDatabase() {
    	CardDatabaseHelper myDbHelper = new CardDatabaseHelper(this);
    	 
        try {
        	myDbHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try {
	 		myDbHelper.openDataBase();
	 	} catch(SQLException sqle) {
	 		throw sqle;
	 	}
	 	
	 	// Get rid of the old data
	 	SharedPreferences settings = getSharedPreferences(mPreferencesListName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
	 	
    	
    	Vector<RawCardData> cards = mList.getAllCheckedCards();
    	int cardCount = cards.size();
    	
    	for (int i = 0; i < cardCount; i++) {
    		myDbHelper.insertIntoListTable(mTableName, cards.get(i));
    	}
        
	 	myDbHelper.close();
    }*/
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0) {
    		/*
    		if (mList.undo()) {
    			return false;
    		}*/
    		
    		if (mCallbackMode) {
    	    	if (getParent() == null) {
    	    		setResult(Activity.RESULT_OK, onReturnCardInfo());
    	    	}
    		}
    		else {
        		SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
        		boolean autoSave = prefs.getBoolean("autoSave", true);
        		if (autoSave) {
        			writeToStorageList(mPreferencesListName);
        			//writeToDatabase();
        		}
        		else {
        			mSavePrompt.show();
                    return false;	
        		}
    		}
        }
    	
    	else if ((keyCode == KeyEvent.KEYCODE_SEARCH) && event.getRepeatCount() == 0 && !mCallbackMode) {
			Intent i = new Intent(CardListActivity.this, SearchActivity.class);
			i.putExtra("requestCode", GlobalConstants.sFlagCallback);
			startActivityForResult(i, GlobalConstants.sFlagCallback);
        }

        return super.onKeyDown(keyCode, event);
    }
	
    
    @Override
    public void finish() {
    	super.finish();
    }

	@Override
	public Intent onReturnCardInfo() {
		// This should only be called when selective loading of wishlist/inventory is enabled, so only return the cards that are check-marked
		
		Intent data = new Intent();
		
		data.putExtra("listName", mPreferencesListName);
		Vector<RawCardData> returnCards = mList.getAllCheckedCards();
		if (returnCards != null) {
			for (int i = 0; i < returnCards.size(); i++) {
				RawCardData card = ((RawCardData)returnCards.get(i));
				data.putExtra("card" + i, card.getDataArray());
			}
		}
		return data;
	}
	/*
	public void readFromDatabase() {
		CardDatabaseHelper myDbHelper = new CardDatabaseHelper(this);
 
        try {
        	myDbHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try {
	 		myDbHelper.openDataBase();
	 	} catch(SQLException sqle) {
	 		throw sqle;
	 	}
	 	
	 	Vector<RawCardData> cards = myDbHelper.readAllFromListTable(mTableName);
	 	if (cards == null) { }
	 	else if (cards.size() == 0) {
	 		// To maintain backwards compatibility with the old data storage mechanism
	 		readFromStorageList(mPreferencesListName);
	 	}
	 	else {
	 		// This will use the database way of storing data
	 		Iterator<RawCardData> iterator = cards.iterator();
	    	while (iterator.hasNext()) {
	    		mList.addCardToView(iterator.next(), false);
	    	}
	 	}
	 	//mList.setLoadComplete();
	 	myDbHelper.close();
	}*/
}