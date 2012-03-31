package com.mtg.trade.guide;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;


public class CardSearchTextBox extends AutoCompleteTextView {
	private final int mMaxSearchResults = 6;
	
	private Context mContext;
	private AutoCompleteAdapter mResultsAdapter;
	private final String[] mIgnoreList = {"Booster Pack", "Booster Box", "Booster Case", "Complete Set", "Event Deck", "Fat Pack", "Intro Pack", "StarCityGames.com", "Player's Guide"};
	
	public CardSearchTextBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mResultsAdapter = new AutoCompleteAdapter(mContext, R.layout.list_item);
	    this.setAdapter(mResultsAdapter);
	}
	
	public String getQuery() {
		return this.getText().toString().replaceAll(" +", "+");
	}
	
	/*
	 * A class that extends the ArrayAdapter<String> so that it updates the contents itself without having to rely on external events.
	 * Credit for this code belongs to the wise man who posted the solution in this post:
	 * 		http://stackoverflow.com/questions/5023645/how-do-i-use-autocompletetextview-and-populate-it-with-data-from-a-web-api
	 * */
	private class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> mData;

	    public AutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	        mData = new ArrayList<String>();
	    }

	    @Override
	    public int getCount() {
	        return mData.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return mData.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter myFilter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	            	Log.d("Constraint value", constraint.toString());
	                FilterResults filterResults = new FilterResults();
	                if(constraint != null) {
	                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
	                	ArrayList<String> l = new ArrayList<String>();
                        
                        InputStream input = null;
                        Reader reader = null;
						try {
							input = new URL("http://sales.starcitygames.com/autocomplete/products_only.php?term=" + constraint.toString().replaceAll(" +", "+")).openStream();
							reader = new InputStreamReader(input, "UTF-8");
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							//e.printStackTrace();
						}
						
						StringBuilder jsonBuilder = new StringBuilder();
													
						String json = "";
						char[] buffer = new char[256];
						try {
							int count = 0;
							String readString;
							while ((count = reader.read(buffer)) != -1) {
								readString = String.valueOf(buffer).substring(0, count);
								jsonBuilder.append(readString);
							}
							reader.close();
						} catch (IOException e) {
						}
						
						Type type = new TypeToken<Map<Integer, JsonElement>>() {}.getType();
						try {
							// Because Starcitygames enclose their JSON in parentheses, I need to grab the substring excluding them.
							json = jsonBuilder.substring(1, jsonBuilder.length() - 1);
							Map<String, JsonElement> jsonResults = new Gson().fromJson(json, type);
							
							Iterator<JsonElement> values = jsonResults.values().iterator();
							boolean add;
							JsonElement curr;
							int count = 0;
							while (values.hasNext() && count < mMaxSearchResults) {
								curr = values.next();
								add = true;
								for (String ignore : mIgnoreList) {
									if (curr.toString().contains(ignore)) {
										add = false;
										break;
									}
								}
								if (add && curr.valid()) {
									l.add(curr.toString());
									count++;
								}
							}
						}
						catch (JsonSyntaxException e) { 
							// This is the case when no matching results are found. You would simply get a JSON that looks like this: ({"F":true})
						}
						catch (IndexOutOfBoundsException e) { 
							// This is the case when the user deletes all the characters in the text box, or at least that seems to be the cause
						}
						
						mData = l;
						
	                    // Now assign the values and count to the FilterResults object
	                    filterResults.values = mData;
	                    filterResults.count = mData.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence contraint, FilterResults results) {
	                if(results != null && results.count > 0) {
	                notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }
	        };
	        return myFilter;
	    }
	}
	
	private class JsonElement {
		private String name;
		private String text;
		private boolean max_suggestions;
		private int matches = 0;
		
		@Override
		public String toString() {
			return name;
		}
		
		public boolean valid() {
			return matches == 0;
		}
	}
}
