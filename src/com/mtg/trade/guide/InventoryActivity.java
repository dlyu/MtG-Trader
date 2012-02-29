package com.mtg.trade.guide;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

public class InventoryActivity extends CardListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory);
        
        mPreferencesListName = getString(R.string.CARDLIST_INVENTORY);
        mList = (CardListLayout)findViewById(R.id.inventory);
        
        mToastSaved = Toast.makeText(this, "Inventory saved", Toast.LENGTH_SHORT);
        mToastReverted = Toast.makeText(this, "Reverted back to old inventory", Toast.LENGTH_SHORT);
        
        mSavePrompt = new AlertDialog.Builder(this);
        mSavePrompt.setMessage("Save changes to your inventory?");
        mSavePrompt.setPositiveButton("Save", mPositiveButtonListener);
        mSavePrompt.setNegativeButton("Discard", mNegativeButtonListener);
        mSavePrompt.setNeutralButton("Return", mNeutralButtonListener);
        
        readFromStorageList(mPreferencesListName);
    }
}