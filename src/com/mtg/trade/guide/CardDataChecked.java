package com.mtg.trade.guide;

import com.mtg.trade.guide.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class CardDataChecked extends CardData {	
	private TextView mLabel1; // Displays the name and edition of the card
	private TextView mLabel2; // Displays the price and condition of the card
	private CheckBox mCheckBox;
	
	public CardDataChecked(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.carddatachecked, this);

		mLabel1 = (TextView) this.findViewById(R.id.cardLabel1);
		mLabel2 = (TextView) this.findViewById(R.id.cardLabel2);
		mCheckBox = (CheckBox) this.findViewById(R.id.checkbox);
		
		mLabel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(GlobalConstants.sProductUrl + mProductId));
				mContext.startActivity(i);
			}
			
		});
	}
	
	public boolean getChecked() {
		return mCheckBox.isChecked();
	}

	@Override
	public String[] getAllData() {
		// TODO Auto-generated method stub
		return new String[] {mName, mEdition, "" + mPrice, mCondition, mProductId, mCheckBox.isChecked() ? "true" : "false"};
	}
	
	public String[] getAllDataWithoutChecked() {
		return new String[] {mName, mEdition, "" + mPrice, mCondition, mProductId};
	}

	@Override
	public void setAllData(String[] data) {
		if (data.length != sMandatoryFieldsCount)
			return;

		mName = data[0];
		mEdition = data[1];
		mPrice = Float.parseFloat(data[2].replaceFirst("\\$", ""));
		mCondition = data[3];
		mProductId = data[4];
		
		refreshMetrics();
		mCheckBox.setChecked(data[5].equals("true"));
	}

	@Override
	public void refreshMetrics() {
		// TODO Auto-generated method stub
		mLabel1.setText(mName + "\n" + mEdition);
		mLabel2.setText("$" + String.format("%.2f", mPrice) + "\n" + mCondition);
	}
}
