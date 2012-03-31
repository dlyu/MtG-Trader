package com.mtg.trade.guide;

import com.mtg.trade.guide.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CardDataQuantity extends CardData {
	private CardDataQuantity mCardData;
	private TextView mLabel1; // Displays the card name and edition
	private TextView mLabel2; // Displays the card price and condition
	private EditText mQuantityText;
	private ImageView mRemoveButton;
	private double mTotalPrice;
	private int mQuantity;
	
	private TradeScreenFragment mContainer;
	
	public CardDataQuantity(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.carddataqty, this);

		mCardData = this;
		mLabel1 = (TextView) this.findViewById(R.id.cardLabel1);
		mLabel2 = (TextView) this.findViewById(R.id.cardLabel2);
		mQuantityText = (EditText) this.findViewById(R.id.quantity);
		mRemoveButton = (ImageView) this.findViewById(R.id.removeButton);
		
		mQuantityText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				updateQuantity();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mLabel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(GlobalConstants.sProductUrl + mProductId));
				mContext.startActivity(i);
			}
		});
		
		mRemoveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((CardListLayout)mCardData.getParent()).removeCardFromView(mCardData);
				if (mContainer != null)
					mContainer.recalculate();
			}
		});
	}

	@Override
	public String[] getAllData() {
		return new String[] {mName, mEdition, "" + mPrice, mCondition, mProductId, "" + mQuantity};
	}

	@Override
	public void setAllData(String[] data) {
		if (data.length < 5)
			return;

		mName = data[0];
		mEdition = data[1];
		mPrice = Float.parseFloat(data[2].replaceFirst("\\$", ""));
		mCondition = data[3];
		mProductId = data[4];
		
		String qty = "";
		try {
			qty = data[5];
		}
		catch (IndexOutOfBoundsException e) {
			qty = "1";
		}
		
		if (qty == null)
			qty = "0";
		else if (qty.length() == 0)
			qty = "0";
		mQuantity = Integer.parseInt(qty);
		mQuantityText.setText("" + mQuantity);
		
		mTotalPrice = mPrice * mQuantity;
		
		refreshMetrics();
	}
	
	public void setFragmentContainer(Fragment f) {
		mContainer = (TradeScreenFragment)f;
	}
	
	
	public int getQuantity() {
		//return Integer.parseInt(mQuantity.getText().toString());
		return mQuantity;
	}
	
	public void setQuantity(int quantity) {
		mQuantity = quantity;
		mQuantityText.setText("" + quantity);
	}
	
	public double getTotalPrice() {
		return mTotalPrice;
	}

	private void updateQuantity() {
		try {
			String quantityStr = mQuantityText.getText().toString();
			if (quantityStr == null)
				quantityStr = "0";
			else if (quantityStr.length() == 0)
				quantityStr = "0";
			mQuantity = Integer.parseInt(quantityStr);
			mTotalPrice = mPrice * mQuantity;
			
			refreshMetrics();			
		}
		catch (Exception e) {
			
		}
	}

	public void increment() {
		mQuantityText.setText("" + (++mQuantity));
	}
	
	public void decrement() {
		if (mQuantity > 0)
			mQuantityText.setText("" + (--mQuantity));
	}

	@Override
	public void refreshMetrics() {
		// TODO Auto-generated method stub
		mLabel1.setText(mName + "\n" + mEdition);
		mLabel2.setText("$" + String.format("%.2f", mTotalPrice) + "\n" + mCondition);
		
		if (mContainer != null)
			mContainer.recalculate();
	}
}