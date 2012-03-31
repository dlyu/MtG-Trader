package com.mtg.trade.guide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardDataView extends LinearLayout implements CardAttributeChangedListener {
	public static final int sMandatoryFieldsCount = 6;
	
	private boolean mQuantifiable;
	private boolean mCheckable;
	
	private CardDataView mCardData;
	private TextView mLabel1; // Displays the card name and edition
	private TextView mLabel2; // Displays the card price and condition
	private EditText mQuantityText;
	private RelativeLayout mRemoveButton;
	private CheckBox mCheckBox;
	
	private TradeScreenFragment mContainer;
	
	/*
	protected String mName;
	protected String mEdition;
	protected float mPrice;
	protected String mCondition;
	protected String mProductId;
	*/
	protected Context mContext;
	protected RawCardData mData;
	
	//protected boolean mActive = true;
	
	public CardDataView(Context context, AttributeSet attrs, RawCardData data) {
		this(context, attrs, data, true, true);
	}
	
	public CardDataView(Context context, AttributeSet attrs, RawCardData data, boolean checkable, boolean quantifiable) {
		super(context, attrs);
		mContext = context;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.carddataview, this);

		mCardData = this;
		mLabel1 = (TextView) this.findViewById(R.id.cardLabel1);
		mLabel2 = (TextView) this.findViewById(R.id.cardLabel2);
		mQuantityText = (EditText) this.findViewById(R.id.quantity);
		mRemoveButton = (RelativeLayout) this.findViewById(R.id.removeButton);
		mCheckBox = (CheckBox) this.findViewById(R.id.checkbox);
		
		mCheckable = checkable;
		mQuantifiable = quantifiable;
		
		this.bindCardData(data);
		mQuantityText.setText("" + data.getQuantity());
		refreshMetrics();
		
		// Make the checkbox go away if it's not in use.
		if (!mCheckable) {
			mCheckBox.setVisibility(View.GONE);
		}
		
		// Might as well set the quantity text listeners only if it's going to be used.
		if (mQuantifiable) {
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
		}
		// Get rid of the quantity text and remove button if only the checkbox is used.
		else {
			mQuantityText.setVisibility(View.GONE);
			mRemoveButton.setVisibility(View.GONE);
		}
		
		// If both the quantity text and checkbox are visible, it would be stupid to have the remove button, so make it invisible.
		if (mQuantifiable && mCheckable) {
			mRemoveButton.setVisibility(View.GONE);
		}
		else {
			mRemoveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					((CardListLayout)mCardData.getParent()).removeCardFromView(mCardData.getCardData());
					if (mContainer != null)
						mContainer.recalculate();
				}
			});
		}
		
		mLabel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(GlobalConstants.sProductUrl + mData.getProductId()));
				mContext.startActivity(i);
			}
		});
	}
	
	private void bindCardData(RawCardData data) {
		mData = data;
		mData.bindView(this);
		mData.setCardAttributeChangeListener(this);
	}
	
	public void setFragmentContainer(Fragment f) {
		mContainer = (TradeScreenFragment)f;
	}
	
	public void setChecked(boolean state) {
		if (mCheckable)
			mCheckBox.setChecked(state);
	}
	/*
	public void setActive(boolean state) {
		mActive = state;
		this.setVisibility(mActive ? View.VISIBLE : View.GONE);
	}*/
	
	public RawCardData getCardData() {
		return mData;
	}
	
	public double getTotalPrice() {
		return mData.getPrice() * mData.getQuantity();
	}
	
	public boolean getChecked() {
		return mCheckable ? mCheckBox.isChecked() : false;
	}
	/*
	public boolean getActive() {
		return mActive;
	}*/

	public void updateQuantity() {
		if (!mQuantifiable)
			return;
		
		try {
			String quantityStr = mQuantityText.getText().toString();
			if (quantityStr == null)
				quantityStr = "0";
			else if (quantityStr.length() == 0)
				quantityStr = "0";
			//mQuantity = Integer.parseInt(quantityStr);
			mData.setQuantity(Integer.parseInt(quantityStr));
		}
		catch (Exception e) {
			
		}
	}

	public void refreshMetrics() {
		// TODO Auto-generated method stub
		mLabel1.setText(mData.getName() + "\n" + mData.getEdition());
		mLabel2.setText("$" + String.format("%.2f", getTotalPrice()) + "\n" + mData.getCondition());
		
		if (mContainer != null)
			mContainer.recalculate();
	}

	@Override
	public void onQuantityChange(CardAttributeChangedEvent q) {
		// TODO Auto-generated method stub
		refreshMetrics();
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub
		
	}
}
