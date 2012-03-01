package com.mtg.trade.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class MainMenuActivity extends Activity {
    ImageView mTradeButton;
	ImageView mSearchButton;
	
	ImageView mInventoryButton;
	ImageView mWishlistButton;
	
	ImageView mOptionButton;
	
	private View.OnClickListener mSearchListener = new View.OnClickListener() {
		public void onClick(View v) {
			//Create an intent to call the game menu
			Intent i = new Intent(MainMenuActivity.this, SearchActivity.class);
			startActivity(i);				
		}
	};
	
	private View.OnClickListener mTradeListener = new View.OnClickListener() {
		public void onClick(View v) {
			//Create an intent to call the game menu
			Intent i = new Intent(MainMenuActivity.this, TradeActivity.class);
			startActivity(i);				
		}
	};
	
	private View.OnClickListener mInventoryListener = new View.OnClickListener() {
		public void onClick(View v) {
			//Create an intent to call the game menu
			Intent i = new Intent(MainMenuActivity.this, InventoryActivity.class);
			startActivity(i);				
		}
	};
	
	private View.OnClickListener mWishlistListener = new View.OnClickListener() {
		public void onClick(View v) {
			//Create an intent to call the game menu
			Intent i = new Intent(MainMenuActivity.this, WishlistActivity.class);
			startActivity(i);				
		}
	};
	
	private View.OnClickListener mOptionListener = new View.OnClickListener() {
		public void onClick(View v) {
			//Create an intent to call the game menu
			Intent i = new Intent(MainMenuActivity.this, OptionsActivity.class);
			startActivity(i);				
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Find a reference to the search button and set its onClickListener
        mSearchButton = (ImageView)findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(mSearchListener);
        
        // Find a reference to the trade button and set its onClickListener
        mTradeButton = (ImageView)findViewById(R.id.tradeButton);
        mTradeButton.setOnClickListener(mTradeListener);
        
        // Find a reference to the inventory button and set its onClickListener
        mInventoryButton = (ImageView)findViewById(R.id.inventoryButton);
        mInventoryButton.setOnClickListener(mInventoryListener);
        
        // Find a reference to the wishlist button and set its onClickListener
        mWishlistButton = (ImageView)findViewById(R.id.wishlistButton);
        mWishlistButton.setOnClickListener(mWishlistListener);
        
        // Find a reference to the option button and set its onClickListener
        mOptionButton = (ImageView)findViewById(R.id.optionButton);
        mOptionButton.setOnClickListener(mOptionListener);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	if ((keyCode == KeyEvent.KEYCODE_SEARCH) && event.getRepeatCount() == 0) {
			Intent i = new Intent(MainMenuActivity.this, SearchActivity.class);
			startActivity(i);	
        }

        return super.onKeyDown(keyCode, event);
    }
}