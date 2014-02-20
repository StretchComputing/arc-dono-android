package com.donomobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.utils.Constants;
import com.donomobile.utils.DonationTypeObject;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;


public class ChurchDonationSelector extends BaseActivity {

	
	private ListView typesListView;
	private TextView titleTextView;
	private TextView explainTextView;
	private MerchantObject myMerchant;
	private ArrayAdapter<DonationTypeObject> adapter;
    private boolean isGoingConfirm = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_church_donation_selector);
		
		typesListView = (ListView) findViewById(R.id.types_list_view);
		
		titleTextView = (TextView) findViewById(R.id.location_name_text);
		titleTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());

		explainTextView = (TextView) findViewById(R.id.howmuchtop);
		explainTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
		
		
		myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
		
		
		
		titleTextView.setText(myMerchant.merchantName);
	

		populateListView();
		registerClickCallback();
		
		setActionBarTitle("Donation Type(s)");


	}

	private void populateListView() {
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.types_list_view);
		list.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	private class MyListAdapter extends ArrayAdapter<DonationTypeObject> {
		public MyListAdapter() {
			super(ChurchDonationSelector.this, R.layout.donation_selector_row, myMerchant.donationTypes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			try {
				
				
				View itemView = convertView;
				if (itemView == null) {
					itemView = getLayoutInflater().inflate(R.layout.donation_selector_row, parent, false);
				}
				
				// Find the car to work with.
				DonationTypeObject currentItem = myMerchant.donationTypes.get(position);
				
				
				// Amount
				TextView name = (TextView) itemView.findViewById(R.id.donation_description);
				name.setTypeface(ArcMobileApp.getLatoBoldTypeface());
				name.setText(currentItem.description);
				
				ImageView check = (ImageView) itemView.findViewById(R.id.check_image);
				
				if (currentItem.isSelected){
					check.setVisibility(View.VISIBLE);
				}else{
					check.setVisibility(View.INVISIBLE);

				}
			
		
				
				return itemView;
				
				
			} catch (Exception e) {
			
				(new CreateClientLogTask("ChurchDonationSelector.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
				return convertView;

			}
		}				
	}
	
	
	private void registerClickCallback() {
		
		
		try {
			
			typesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {

						DonationTypeObject type = myMerchant.donationTypes.get(position);

						
						if (type.isSelected){
							type.isSelected = false;
						}else{
							type.isSelected = true;
						}
						
						adapter.notifyDataSetChanged();

						
					
					} catch (Exception e) {

						(new CreateClientLogTask("ChurchDonationSelector.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

					}
					
				
				}
			});
			
		
		} catch (Exception e) {

			(new CreateClientLogTask("ChurchDonationSelector.registerClickCallback", "Exception Caught", "error", e)).execute();

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isGoingConfirm = false;
	}
	
	public void onContinueClicked(View v) {

		
		if (!isGoingConfirm){
			isGoingConfirm = true;
			
			int count = 0;
			for (int i = 0; i < myMerchant.donationTypes.size(); i++){
				
				DonationTypeObject type = myMerchant.donationTypes.get(i);
				
				if (type.isSelected){
					count++;
				}
			}
			
			
			if (count == 0){
				
				toastShort("You must select at least one donation area.");
				isGoingConfirm = false;
			}else if (count == 1){
				
				Intent single = new Intent(getApplicationContext(), ChurchDonationTypeSingle.class);
				single.putExtra(Constants.VENUE, myMerchant);
				startActivity(single);
				
			}else{
				// > 1
				
				Intent single = new Intent(getApplicationContext(), ChurchDonationTypeMultiple.class);
				single.putExtra(Constants.VENUE, myMerchant);
				startActivity(single);
				
			}
		}
		
	}

}
