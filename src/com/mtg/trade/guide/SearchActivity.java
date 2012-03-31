package com.mtg.trade.guide;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class SearchActivity extends Activity implements Returnable {
	private static String sUrl = "http://sales.starcitygames.com//search.php";
	Button mSearchButton;
	Button mMoreButton;
	
	CardSearchTextBox mCard;
	CardListLayout mResults;
	
	String mLastCardName;
	String mLastEditionName;
	String mLastProductId;
	
	String mLastQuery;
	int mQueryStart = 0;
	
	protected Toast mInternetError;
	protected Toast mGenericError;
	
	protected Toast mToastAddedToInventory;
	protected Toast mToastAddedToWishlist;
	protected Toast mToastNoSearchResult;
	
	private ProgressDialog mLoader;
	
	private boolean mCallbackMode;
	
	private boolean mDebuggable;
	private AdView mAdView;
	
	private Pattern mPriceRegex;

	private View.OnClickListener mSearchListener = new View.OnClickListener() {
		public void onClick(View v) {
			try {
				// Execute a whole new search queryl
				String cardQuery = mCard.getText().toString();
				if (cardQuery != null) {
					
					mLoader.show();
					mQueryStart = 0;
					searchGET(cardQuery, false);
					mLastQuery = cardQuery;
					mLoader.dismiss();
				}

			} catch (IllegalStateException e) {
				mGenericError.show();
				mLoader.dismiss();
			} catch (IOException e) {	
				mInternetError.show();
				mLoader.dismiss();
			}
		}
	};
	
	private View.OnClickListener mMoreListener = new View.OnClickListener() {
		public void onClick(View v) {
			// Execute a continuation of the previous query
			try {
				mLoader.show();
				searchGET(mLastQuery, true);
				mLoader.dismiss();
			} catch (IllegalStateException e) {
				mGenericError.show();
				mLoader.dismiss();
			} catch (IOException e) {
				mInternetError.show();
				mLoader.dismiss();
			}
		}
	};
	
	// Exit
	protected DialogInterface.OnClickListener mPositiveButtonListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			finish();
		}
	};
	
	// Cancel
	protected DialogInterface.OnClickListener mNegativeButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        mSearchButton = (Button)findViewById(R.id.startSearch);
        mSearchButton.setOnClickListener(mSearchListener);
        
        mMoreButton = (Button)findViewById(R.id.moreSearch);
        mMoreButton.setOnClickListener(mMoreListener);
        
        mCard = (CardSearchTextBox)findViewById(R.id.card);
        mCard.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					try {
						// Execute a whole new search queryl
						String cardQuery = mCard.getText().toString();
						if (cardQuery != null) {
							
							mLoader.show();
							mQueryStart = 0;
							searchGET(cardQuery, false);
							mLastQuery = cardQuery;
							mLoader.dismiss();
						}

					} catch (IllegalStateException e) {
						mGenericError.show();
						mLoader.dismiss();
					} catch (IOException e) {	
						mInternetError.show();
						mLoader.dismiss();
					}					
				}

				return false;
			}
        	
        });
        
        mResults = (CardListLayout)findViewById(R.id.results);

        mInternetError = Toast.makeText(this, "No network connection available. Cannot perform a search.", Toast.LENGTH_SHORT);
        mGenericError = Toast.makeText(this, "Something went wrong. Cannot perform a search.", Toast.LENGTH_SHORT);
        
        mToastAddedToInventory = Toast.makeText(this, "Selected cards added to inventory.", Toast.LENGTH_SHORT);
        mToastAddedToWishlist = Toast.makeText(this, "Selected cards added to wishlist.", Toast.LENGTH_SHORT);
        
        mToastNoSearchResult = Toast.makeText(this, "No search results returned. Please revise your search query.", Toast.LENGTH_SHORT);
        
        mLoader = new ProgressDialog(this);
        mLoader.setMessage("Searching...");
        
        mCallbackMode = true;
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
        	mCallbackMode = false;
        }
        else {
        	int requestCode = extras.getInt("requestCode", GlobalConstants.sFlagNormal);
        	if (requestCode == GlobalConstants.sFlagNormal) {
        		mCallbackMode = false;
        	}
        }
        
        // Ad related stuff
        mDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        LinearLayout adPlaceholder = (LinearLayout)findViewById(R.id.ad_placeholder);
	    mAdView = new AdView(this, AdSize.BANNER, this.getString(R.string.AD_ID));

	    mPriceRegex = Pattern.compile("\\$\\d+\\.\\d{2}");
	    
	    // Add the adView to it
	    adPlaceholder.addView(mAdView);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	showAds();
    }
    
    @Override
    public void finish() {
    	if (getParent() == null && mCallbackMode) {
    		setResult(Activity.RESULT_OK, onReturnCardInfo());
    	}
    	super.finish();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0) {
    		if (!mCallbackMode) {
    			
    		}
        }

        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if (mCallbackMode) {
            inflater.inflate(R.menu.searchlistoptions_callback, menu);
                		
    	}
    	else {
    		inflater.inflate(R.menu.searchlistoptions, menu);
    	}
    	
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.search_screen_add_to_inventory:
    			addCardListToInventory();
    			mToastAddedToInventory.show();
				return(true);
  
			case R.id.search_screen_add_to_wishlist:
				addCardListToWishlist();
				mToastAddedToWishlist.show();
				return(true);
				
			case R.id.search_screen_select_all:
				mResults.selectAll(true);
				return true;
				
			case R.id.search_screen_unselect_all:
				mResults.selectAll(false);
				return true;
    	}
  
    	return(super.onOptionsItemSelected(item));
    }
	
    /*
     * A method that searches for cards using a GET request and shows the results on the screen 
     * @param query: The search query that a customer would type in. Will be used as a value for the "substring" parameter in the GET request.
     * @param more: A flag that determines if the user is searching via the "Search" button or the "More" button if true or false respectively. The former case clears the search results while the latter doesn't.
     * */
    private void searchGET(String query, boolean more) throws IOException {
		query = query.trim().replaceAll(" +", "+"); // Replace all spaces with +'s
		query = sUrl + "?substring=" + query + "&start=" + mQueryStart;

		Document doc = Jsoup.connect(query).get();
		Vector<RawCardData> cards = searchDocument(doc);
		if (cards == null)
			return;
		if (cards.size() == 0) {
			mToastNoSearchResult.show();
			return;
		}
		
		// Don't touch the results if we are doing a continuation search
		if (!more) {
			// Don't get rid of cards that are checked already.
			Vector<RawCardData> checkedCards = getCheckedCards();
			mResults.removeAllViews();
			addCardListToResult(checkedCards);
		}
		addCardListToResult(cards);
		
		if (cards.size() == 50) {
			mQueryStart+=50;
			mMoreButton.setEnabled(true);
		}
		else
			mMoreButton.setEnabled(false);
    } 
    
    /*
     * A method that takes in a Jsoup Document object and returns a Vector of all the CardDataChecked objects in it 
     * @param query: A Jsoup Document object that represents the HTML page with search results
     * 
     * return: A Vector of all the cards in the HTML document in the form of CardDataChecked views
     * */
    private Vector<RawCardData> searchDocument(Document doc) throws IOException {

		Elements cards = doc.select("tr[class]");

		Vector<RawCardData> results = new Vector<RawCardData>();
		for (Element card : cards) {
			if (card.className().equals("deckdbbody") || card.className().equals("deckdbbody2")) {
				RawCardData cardData = extractCardInfo(card);
				if (cardData == null)
					continue;
				results.add(cardData);
			}
		}

		return results;
    }
    
    /*
     * A method that takes a Jsoup Element node representing the card and extracts the necessary data.
     * @param card: The Jsoup Element node that contains the card information
     * 
     * return: The View that represents the card
     * */
    public RawCardData extractCardInfo(Element card) {
    	Elements data = card.select("td");
    	int priceIdx = 8;
    	
		// This is for non-card products such as booster packs
		if (data.get(3).html().length() == 0)
			return null;
    	
		Elements cardConditionElements = null;
    	Elements cardNameElements = data.get(0).select("a[href]");
    	String cardName = "";
    	String cardEdition = "";
    	String cardCondition = "";
    	String productId = "";
    	
    	// Check if we are dealing with a new card.
    	if (cardNameElements.size() > 0) {
    		cardConditionElements = data.get(6).select("a[href]");
    		if (cardConditionElements.size() == 0)
    			return null;
    		
    		Element cardNameElement = cardNameElements.get(0);
    		cardName = Jsoup.parse(cardNameElement.html()).text();
    		String url = cardNameElement.attr("href");
    		productId = url.split("product=")[1];
    		
    		Element cardEditionElement = data.get(1).select("a[href]").get(0);
        	cardEdition = Jsoup.parse(cardEditionElement.html()).text();
        	
    	}
    	// This else statement deals with the same card as before but with different physical condition.
    	// These kind of cards have a different number of columns in the HTML row.
    	else {
    		cardConditionElements = data.get(5).select("a[href]");
    		if (cardConditionElements.size() == 0)
    			return null;
    		
    		cardName = mLastCardName;
    		cardEdition = mLastEditionName;
    		productId = mLastProductId;
    		priceIdx = 7;
    	}
    	
    	cardCondition = cardConditionElements.get(0).html();
    	mLastCardName = cardName;
    	mLastEditionName = cardEdition;
    	mLastProductId = productId;
    	
    	Element cardPriceElement = data.get(priceIdx);
    	String cardPrice = "";

    	Matcher priceMatches = mPriceRegex.matcher(cardPriceElement.html());
    	
    	// Pick the last match as that represents the sale value (if any)
    	while (priceMatches.find()) {
    		cardPrice = priceMatches.group();
    	}

    	RawCardData rawData = new RawCardData(cardName, cardEdition, Float.parseFloat(cardPrice.replaceFirst("\\$", "")), cardCondition, productId);
    	return rawData;
    }
    
    public Vector<RawCardData> getCheckedCards() {
    	int totalCards = mResults.getChildCount();
		Vector<RawCardData> checkedCards = new Vector<RawCardData>();
		for (int i = 0; i < totalCards; i++) {
			RawCardData card = ((CardDataView) mResults.getChildAt(i)).getCardData();
			if (card.getView().getChecked())
				checkedCards.add(card);
		}
		return checkedCards;
    }
    
    /*
     * Takes the necessary attributes of a particular card and makes its own CardData View out of it.
     * @param data: A string array that contains the card name, edition, price, condition, and whether it's checked or not in that order
     * */
    public void addCardListToResult(Vector<RawCardData> cards) {
    	for (int i = 0; i < cards.size(); i++) {
    		RawCardData card = (RawCardData)cards.get(i);
    		
    		// If a RawCardData has a View bound to it, then add it. Otherwise it will create a new View, which not only wastes memory but will add it as unchecked when it shouldn't be.
    		if (card.getView() == null)
    			mResults.addCardToView(card);
    		else
    			mResults.addView(card.getView());
    	}
    }
    
    public void addCardListToInventory() {
    	Vector<RawCardData> cards = getCheckedCards();
    	SharedPreferences settings = getSharedPreferences(getString(R.string.CARDLIST_INVENTORY), 0);
        SharedPreferences.Editor editor = settings.edit();
        
        int cardCount = settings.getAll().size()/CardDataView.sMandatoryFieldsCount;
    	Iterator<RawCardData> cardIterator = cards.iterator();
    	while (cardIterator.hasNext()) {
    		String prefix = "card" + cardCount++;
    		RawCardData card = (RawCardData) cardIterator.next();
    	    		
    		String name = card.getName();
    		String edition = card.getEdition();
    		float price = card.getPrice();
    		String condition = card.getCondition();
    		String productId = card.getProductId();
    		int quantity = 	1;
    		
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
    
    public void addCardListToWishlist() {
    	Vector<RawCardData> cards = getCheckedCards();
    	SharedPreferences settings = getSharedPreferences(getString(R.string.CARDLIST_WISHLIST), 0);
        SharedPreferences.Editor editor = settings.edit();
        
        int cardCount = settings.getAll().size()/CardDataView.sMandatoryFieldsCount;
    	Iterator<RawCardData> cardIterator = cards.iterator();
    	while (cardIterator.hasNext()) {
    		String prefix = "card" + cardCount++;
    		RawCardData card = (RawCardData) cardIterator.next();
    	    		
    		String name = card.getName();
    		String edition = card.getEdition();
    		float price = card.getPrice();
    		String condition = card.getCondition();
    		String productId = card.getProductId();
    		int quantity = 	1;
    		
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
    
    public void showAds() {
    	AdRequest request = new AdRequest();
		if (mDebuggable) {
		    request.addTestDevice(AdRequest.TEST_EMULATOR);
		    request.addTestDevice("A4D3A1B00E32ED50C9F5BF27A97ACBE3"); // Samsung Vibrant
		    request.addTestDevice("1442317211E5CA1270B73650E3549192"); // Galaxy Nexus
		}

		mAdView.loadAd(request);
    }

	@Override
	public Intent onReturnCardInfo() {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		int prevResults = mResults.getChildCount();
		
		for (int i = 0, j = 0; i < prevResults; i++) {
			CardDataView card = (CardDataView) mResults.getChildAt(i);
			if (card.getChecked()) {
				data.putExtra("card" + j++, card.getCardData().getDataArray());
			}
		}
		return data;
	}
}