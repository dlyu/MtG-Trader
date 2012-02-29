package com.mtg.trade.guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class CardListActivity extends Activity {
	protected CardListLayout mList;
	protected String mPreferencesListName;
	
	protected Toast mToastSaved;
	protected Toast mToastReverted;
	
	protected AlertDialog.Builder mSavePrompt; 

	// Save
	protected DialogInterface.OnClickListener mPositiveButtonListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			writeToStorageList(mPreferencesListName);
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
    			for (int i = 0; ; i++) {
    				// Display cards on wishlist
    				String[] cardData = data.getStringArrayExtra("card" + i);
    				if (cardData == null)
    					break;

    				CardDataQuantity c = new CardDataQuantity(this, null);
    				c.setAllData(cardData);
    				mList.addCardToView(c);
    			}
    		}
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardlistoptions, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.card_list_search:
				Intent i = new Intent(CardListActivity.this, SearchActivity.class);
				i.putExtra("requestCode", GlobalConstants.sFlagCallback);
				startActivityForResult(i, GlobalConstants.sFlagCallback);
	    		break;
    	
    		case R.id.card_list_save:
				writeToStorageList(mPreferencesListName);
				if (mToastSaved != null)
					mToastSaved.show();
				return(true);
  
			case R.id.card_list_revert:
				mList.removeAllViews();
				readFromStorageList(mPreferencesListName);
				if (mToastReverted != null)
					mToastReverted.show();
				return(true);
    	}
  
    	return(super.onOptionsItemSelected(item));
    }
    
    /*
     * Reads the last card list from the SharedPreferences named after the argument
     * @param pref: The name of the SharedPreferences from which the card list will be read
     * */
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
    		
    		CardDataQuantity c = new CardDataQuantity(this, null);
    		c.setName(name);
    		c.setEdition(edition);
    		c.setPrice(price);
    		c.setCondition(condition);
    		c.setProductId(productId);
    		c.setQuantity(quantity);
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
    		CardDataQuantity card = (CardDataQuantity) mList.getChildAt(i);
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
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0) {
    		SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
    		boolean autoSave = prefs.getBoolean("autoSave", true);
    		if (autoSave) {
    			writeToStorageList(mPreferencesListName);
    		}
    		else {
    			mSavePrompt.show();
                return false;	
    		}
    			
        }

        return super.onKeyDown(keyCode, event);
    }
	
    
    @Override
    public void finish() {
    	super.finish();
    }
}