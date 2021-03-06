package com.mtg.trade.guide;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WishlistActivity extends CardListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist);
        
        mPreferencesListName = getString(R.string.CARDLIST_WISHLIST);
        mTableName = getString(R.string.TABLE_WISHLIST);
        mList = (CardListLayout)findViewById(R.id.wishlist);
        
        mToastSaved = Toast.makeText(this, "Wishlist saved", Toast.LENGTH_SHORT);
        mToastReverted = Toast.makeText(this, "Reverted back to old wishlist", Toast.LENGTH_SHORT);
        
        mSavePrompt = new AlertDialog.Builder(this);
        mSavePrompt.setMessage("Save changes to your wishlist?");
        mSavePrompt.setPositiveButton("Save", mPositiveButtonListener);
        mSavePrompt.setNegativeButton("Discard", mNegativeButtonListener);
        mSavePrompt.setNeutralButton("Return", mNeutralButtonListener);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mCallbackMode = true;
        if (extras == null) {
        	mCallbackMode = false;
        }
        else {
        	int requestCode = extras.getInt("requestCode", GlobalConstants.sFlagNormal);
        	// Does not return data on activity finish
        	if (requestCode == GlobalConstants.sFlagNormal) {
        		mCallbackMode = false;
        	}
        	// Returns data on activity finish
        	else {
        		mList.setCardViewConfigurable(true, true);
        	}
        }
        
        readFromStorageList(this.mPreferencesListName);
        //readFromDatabase();
    }
}