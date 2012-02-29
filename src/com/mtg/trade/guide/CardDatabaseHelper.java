/*
package com.mtg.trade.guide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
// UNUSED CLASS UNLESS IF I DECIDE TO USE A CONTENT PROVIDER
public class CardDatabaseHelper extends SQLiteOpenHelper {
	static final String DATABASE_NAME = "mtg.trade.guide.db";
	static final int DATABASE_VERSION = 1;
	
	static final String COLUMN_PRODUCT_ID = "ProductID";
	static final String COLUMN_CARD_NAME = "Name";
	static final String COLUMN_EDITION = "Edition";
	static final String COLUMN_PRICE = "Price";
	static final String COLUMN_CONDITION = "Condition";
	static final String COLUMN_QUANTITY = "Quantity";
	
	static final String TABLE_INVENTORY = "dbInventory";
	static final String TABLE_WISHLIST = "dbWishlist";
	
	public CardDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String dbQueryInventory = String.format("CREATE TABLE %s " +
				"								(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s NUMERIC, %s TEXT, %s INTEGER)", 
												TABLE_INVENTORY, 
												COLUMN_PRODUCT_ID, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_CONDITION, COLUMN_QUANTITY);
		db.execSQL(dbQueryInventory);
		
		String dbQueryWishlist = String.format("CREATE TABLE %s " +
				"								(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s NUMERIC, %s TEXT, %s INTEGER)", 
												TABLE_WISHLIST, 
												COLUMN_PRODUCT_ID, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_CONDITION, COLUMN_QUANTITY);
		db.execSQL(dbQueryWishlist);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
*/