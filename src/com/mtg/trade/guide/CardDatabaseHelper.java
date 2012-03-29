package com.mtg.trade.guide;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class CardDatabaseHelper extends SQLiteOpenHelper {
	static final String DATABASE_NAME = "mtg.trade.guide.db";
	static final int DATABASE_VERSION = 1;
	
	// For the card tables
	private static final String COLUMN_PRODUCT_HASH = "_id"; // The unique identifier is the combination of the card's product ID and condition
	private static final String COLUMN_CARD_NAME = "Name";
	private static final String COLUMN_EDITION = "Edition";
	private static final String COLUMN_PRICE = "Price";
	private static final String COLUMN_QUANTITY = "Quantity";
	
	// For the trade tables
	private static final String COLUMN_TRADE_ID = "_id"; // Also applies to the giving and receiving card tables
	private static final String COLUMN_TRADE_TIMESTAMP = "Created";
	private static final String COLUMN_TRADE_LAST_MODIFIED = "LastModified";
	private static final String COLUMN_TRADE_DESCRIPTION = "Description";
	
	static final String TABLE_INVENTORY = "dbInventory";
	static final String TABLE_WISHLIST = "dbWishlist";
	static final String TABLE_TRADE_GIVING = "dbTradeGiving";
	static final String TABLE_TRADE_RECEIVING = "dbTradeReceiving";
	static final String TABLE_TRADE_TABLE = "dbTradeList";
	
	private static String DB_PATH = "/data/data/com.mtg.trade.guide/databases/";
    private static String DB_NAME = "mtg_trade_helper_db";
	
	private Context mContext;
	private SQLiteDatabase mDatabase;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public CardDatabaseHelper(Context context) {
    	super(context, DB_NAME, null, 1);
        mContext = context;
    }	
 
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();
 
    	if (dbExist) {
    		//do nothing - database already exist
    	} else {
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
    	try {
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	} catch(SQLiteException e){
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = mContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    public Vector<RawCardData> readAllFromListTable(String tableName) {
    	if (mDatabase == null)
    		return null;
    	Vector<RawCardData> cards = new Vector<RawCardData>();
    	
    	// Fetch everything from the specified table name sorted by card name
    	Cursor curr = mDatabase.query(tableName, null, null, null, null, null, COLUMN_CARD_NAME);
    	
    	RawCardData card;
    	String name;
    	String edition;
    	float price;
    	String condition;
    	String productId;
    	int quantity;
    	
    	curr.moveToFirst();
    	
    	while (curr.isAfterLast() == false) {
    		//Log.d("Cursor info", String.format("Number of columns: %d, Column names: %s", curr.getColumnCount(), curr.getExtras().toString()));
    		String[] data = GlobalConstants.getDataFromProductHash(curr.getString(0));
            name = curr.getString(1);
            edition = curr.getString(2);
            price = curr.getFloat(3);
            quantity = curr.getInt(4);
            productId = data[0];
            condition = data[1];
             
            card = new RawCardData(name, edition, price, condition, productId, quantity);
            cards.add(card);
       	    curr.moveToNext();
        }
        curr.close();
        return cards;
    }
 
    public void insertIntoListTable(String tableName, RawCardData card) {
		String name = card.getName();
		String edition = card.getEdition();
		float price = card.getPrice();
		int quantity = card.getQuantity();
    	int productHash = GlobalConstants.getProductHashFromView(card);
		
    	boolean delete = (quantity == 0);
    	
		ContentValues row = new ContentValues();
		if (!delete) {
			row.put(COLUMN_PRODUCT_HASH, productHash);
			row.put(COLUMN_CARD_NAME, name);
			row.put(COLUMN_EDITION, edition);
			row.put(COLUMN_PRICE, price);
			row.put(COLUMN_QUANTITY, quantity);
			
			try {
	    		mDatabase.insertOrThrow(tableName, null, row);
	    	}
	    	catch (SQLException e) {
	    		mDatabase.execSQL(String.format("UPDATE %s SET %s=%d WHERE %s=%d", tableName, COLUMN_QUANTITY, quantity, COLUMN_PRODUCT_HASH, productHash));
	    		//mDatabase.update(tableName, row, String.format("%s=%s", COLUMN_PRODUCT_HASH, productHash), null);
	    	}
		}

		else {
			mDatabase.delete(tableName, String.format("%s=%s", COLUMN_PRODUCT_HASH, productHash), null);
		}
    }
    
    @Override
	public synchronized void close() {
		if(mDatabase != null)
			mDatabase.close();
		super.close(); 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {

	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views. 
	
	/*
	public CardDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the database for all the cards in the inventory
		String dbQueryInventory = String.format("CREATE TABLE %s " +
												"(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s NUMERIC, %s INTEGER);", 
												TABLE_INVENTORY, 
												COLUMN_PRODUCT_HASH, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_QUANTITY);
		db.execSQL(dbQueryInventory);
		
		// Create the database for all the cards in the wishlist
		String dbQueryWishlist = String.format("CREATE TABLE %s " +
											   "(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s NUMERIC, %s INTEGER);", 
											   TABLE_WISHLIST, 
											   COLUMN_PRODUCT_HASH, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_QUANTITY);
		db.execSQL(dbQueryWishlist);
		
		// Create the database for all the cards that are being given for all the trades
		String dbQueryTradeGiving = String.format("CREATE TABLE %s " +
												  "(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT, %s NUMERIC, %s INTEGER);", 
												  TABLE_TRADE_GIVING, 
												  COLUMN_PRODUCT_HASH, COLUMN_TRADE_ID, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_QUANTITY);
		db.execSQL(dbQueryTradeGiving);
		
		// Create the database for all the cards that are being received for all the trades
		String dbQueryTradeReceiving = String.format("CREATE TABLE %s " +
													 "(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT, %s NUMERIC, %s INTEGER);", 
													 TABLE_TRADE_RECEIVING, 
													 COLUMN_PRODUCT_HASH, COLUMN_TRADE_ID, COLUMN_CARD_NAME, COLUMN_EDITION, COLUMN_PRICE, COLUMN_QUANTITY);
		db.execSQL(dbQueryTradeReceiving);
		
		// Create the database for all the cards that are being received for all the trades
		String dbQueryTradeTable = String.format("CREATE TABLE %s " +
												 "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);", 
												 TABLE_TRADE_TABLE, 
												 COLUMN_TRADE_ID, COLUMN_TRADE_TIMESTAMP, COLUMN_TRADE_LAST_MODIFIED, COLUMN_TRADE_DESCRIPTION);
		db.execSQL(dbQueryTradeTable);
		
		SharedPreferences inventory = mContext.getSharedPreferences(mContext.getString(R.string.CARDLIST_INVENTORY), 0);
		SharedPreferences wishlist = mContext.getSharedPreferences(mContext.getString(R.string.CARDLIST_WISHLIST), 0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}*/
}
