package com.mtg.trade.guide;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

/**
 * The <code>TabsViewPagerFragmentActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 * @author mwho
 */
public class TradeActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	private TabHost mTabHost; // Tab host that contains the tabs and the views corresponding to each tab
	private ViewPager mViewPager; // The layout that corresponds with each tab that the user can scroll across
	private PagerAdapter mPagerAdapter;
	private TextView mTradeJudge;
	
	private TradeScreenFragment mOffering;
	private TradeScreenFragment mOffered;
	
	private float mMaxPriceDifference;
	private boolean mSelectiveListLoad;

	/**
	 * A simple factory that returns dummy views to the Tabhost
	 * @author mwho
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }
	}
	
	public class PagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragments;
		/**
		 * @param fm
		 * @param fragments
		 */
		public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.mFragments = fragments;
		}
		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			return this.mFragments.get(position);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.mFragments.size();
		}
	}
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Inflate the layout
		setContentView(R.layout.tradescreen);
		
		mTradeJudge = (TextView)findViewById(R.id.trade_judgment);
		
		// Initialise the TabHost
		this.initialiseTabHost(savedInstanceState);
		// Intialise ViewPager
		this.intialiseViewPager();
		
		SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
		mMaxPriceDifference = Float.parseFloat(prefs.getString("preference_price_gap", "1.0"));
		mSelectiveListLoad = prefs.getBoolean("preference_selective_load", true);
		
		// Both sides of the trade will have zero value
		judgeTrade();
		if (savedInstanceState != null) {
			String tab = savedInstanceState.getString("tab");
			if (tab != null)
				mTabHost.setCurrentTabByTag(tab); //set the tab as per the saved state
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	/** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tradelistoptions, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
    	switch (item.getItemId()) {
    		case R.id.trade_screen_search:
    			i = new Intent(TradeActivity.this, SearchActivity.class);
    			i.putExtra("requestCode", GlobalConstants.sFlagCallback);
    			startActivityForResult(i, GlobalConstants.sFlagCallback);		
				return true;
			case R.id.trade_screen_load_inventory:
				if (mSelectiveListLoad) {
					i = new Intent(TradeActivity.this, InventoryActivity.class);
	    			i.putExtra("requestCode", GlobalConstants.sFlagCallback);
	    			startActivityForResult(i, GlobalConstants.sFlagCallback);
				}
				else {
					mOffering.loadList(getString(R.string.CARDLIST_INVENTORY));
				}
				return true;
			case R.id.trade_screen_load_wishlist:
				if (mSelectiveListLoad) {
					i = new Intent(TradeActivity.this, WishlistActivity.class);
	    			i.putExtra("requestCode", GlobalConstants.sFlagCallback);
	    			startActivityForResult(i, GlobalConstants.sFlagCallback);
				}
				else {
					mOffered.loadList(getString(R.string.CARDLIST_WISHLIST));
				}
				return true;
    	}
  
    	return(super.onOptionsItemSelected(item));
    }
    
    /**/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	if ((keyCode == KeyEvent.KEYCODE_SEARCH) && event.getRepeatCount() == 0) {
			Intent i = new Intent(TradeActivity.this, SearchActivity.class);
			i.putExtra("requestCode", GlobalConstants.sFlagCallback);
			startActivityForResult(i, GlobalConstants.sFlagCallback);	
        }

        return super.onKeyDown(keyCode, event);
    }
    
    /*
     * Called whenever this activity resumes from an Activity from which card data is retrieved.
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == GlobalConstants.sFlagCallback) {
    		if (resultCode == RESULT_OK) {
    			TradeScreenFragment target;
    			
    			String listName = data.getStringExtra("listName");
    			// If true, then this is being called after returning from the SearchActivity
    			if (listName == null) {
    				int pos = this.mTabHost.getCurrentTab();
        			if (pos == 0) 
        				target = mOffering;
        			else
        				target = mOffered;    				
    			}
    			// If true, then this is being called after returning from a subclass of CardListActivity
    			else {
    				if (listName.equals(getString(R.string.CARDLIST_INVENTORY))) {
    					target = mOffering;
    				}
    				else {
    					target = mOffered;
    				}
    			}

    			for (int i = 0; ; i++) {
    				// Display cards on wishlist
    				String[] cardData = data.getStringArrayExtra("card" + i);
    				if (cardData == null)
    					break;

    				CardDataQuantity c = new CardDataQuantity(this, null);
    				c.setAllData(cardData);
    				c.setFragmentContainer(target);
    				target.getCardList().addCardToView(c, false);
    			}
    			target.recalculate();
    		}
    	}
    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();
		mOffering = (TradeScreenFragment) Fragment.instantiate(this, TradeScreenFragment.class.getName());
		fragments.add(mOffering);
		mOffered = (TradeScreenFragment) Fragment.instantiate(this, TradeScreenFragment.class.getName());
		fragments.add(mOffered);
		this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);

		this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
    }

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TradeActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("tab_offering").setIndicator("Giving"));
        TradeActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("tab_offered").setIndicator("Receiving"));

        mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Add Tab content to the Tabhost
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(TradeActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		//TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
    }

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		this.mTabHost.setCurrentTab(position);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}
	
	public void judgeTrade() {
		float offeringPrice = mOffering.getPriceSum();
		float offeredPrice = mOffered.getPriceSum();
		float diff = offeringPrice - offeredPrice;
		
		mTradeJudge.setText(String.format("%s\n$%.2f", getString(R.string.PRICE_DIFF_TEXT), Math.abs(diff)));
		// FAIR TRADE, ONLY APPLIES WHEN BOTH PARTIES OFFER SOMETHING OR NOTHING
		if (Math.abs(diff) < mMaxPriceDifference) {
			// Giving stuff away for free is BAD
			if (offeredPrice == 0 && offeringPrice > 0)
				setNegativeJudge(diff);
			// Getting free stuff is GOOD
			else if (offeringPrice == 0 && offeredPrice > 0)
				setPositiveJudge(diff);
			else
				setNeutralJudge(diff);
		}
		else {
			// PROFIT
			if (offeringPrice < offeredPrice) {
				setPositiveJudge(diff);
			}
			// RIPOFF
			else {
				setNegativeJudge(diff);
			}
		}
	}
	
	private void setPositiveJudge(float diff) {
		mTradeJudge.setTextAppearance(this, R.style.TradeOutcomeFontPositive);
	}
	
	private void setNeutralJudge(float diff) {
		mTradeJudge.setTextAppearance(this, R.style.TradeOutcomeFontNeutral);
	}
	
	private void setNegativeJudge(float diff) {
		mTradeJudge.setTextAppearance(this, R.style.TradeOutcomeFontNegative);	
	}
	
	private void syncInventoryAndWishList() {
		
	}
}
