package com.donomobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Keys;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class Support extends BaseActivity {

	
	private TextView questionTextView;
	
	private TextView emailTextView;
	private TextView phoneTextView;
	
	private CheckBox donationOptionsCheckBox;
	private CheckBox defaultChurchCheckBox;
	
	private Button emailButton;
	private Button phoneButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			

			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_support);
		
			questionTextView = (TextView) findViewById(R.id.current_merchant);
			questionTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			emailTextView = (TextView) findViewById(R.id.merchantNameText);
			emailTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			phoneTextView = (TextView) findViewById(R.id.dateText);
			phoneTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			emailButton = (Button) findViewById(R.id.button_email);
			emailButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			phoneButton = (Button) findViewById(R.id.button_call);
			phoneButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			//donationOptionsCheckBox = (CheckBox) findViewById(R.id.defaultCheck);
			//donationOptionsCheckBox.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			defaultChurchCheckBox = (CheckBox) findViewById(R.id.anonymousCheck);
			defaultChurchCheckBox.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			//If there is a guest token or customer token, go to HOME
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			String customerEmail = myPrefs.getString(Keys.CUSTOMER_EMAIL);

			if (customerToken != null && customerToken.length() > 0){
				AppActions.add("Support - OnCreate - Viewed As Customer: " + customerEmail);

			}else{
				AppActions.add("Support - OnCreate - Viewed As Guest ");

			}
			
			
			//check boxes
			
			String defaultMerchantId = myPrefs.getString(Keys.DEFAULT_CHURCH_ID);

			/*
			if (skipDonationsOptions != null && skipDonationsOptions.length() > 0){
				donationOptionsCheckBox.setChecked(false);
			}else{
				donationOptionsCheckBox.setChecked(true);
			}
			
			donationOptionsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		        @Override
		        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		            //do stuff
		        	
		        	if (isChecked){
		        	
		        		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
		    			myPrefs.putAndCommitString(Keys.SKIP_DONATIONS_OPTIONS, "");
		    			
		        	}else{
		        		
		        		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
		    			myPrefs.putAndCommitString(Keys.SKIP_DONATIONS_OPTIONS, "yes");
		    			
		    			
		        	}

		        }
		    });
			
			*/
			
			if (defaultMerchantId != null && defaultMerchantId.length() > 0){
				defaultChurchCheckBox.setChecked(true);
			}else{
				defaultChurchCheckBox.setChecked(false);
			}
	
			defaultChurchCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		        @Override
		        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		            //do stuff

		        	if (isChecked){
		        		
		    			toastShort("To add a default church, select the 'Home' menu option, then select the 'Make this my default donation location' option when you choose a location.");

		    			
		        	}else{
		        		
		        		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
		    			myPrefs.putAndCommitString(Keys.DEFAULT_CHURCH_ID, "");
		    			
		    			toastShort("Your default church has been removed successfully!");
		        	}
		        }
		    });
			
			setActionBarTitle("Settings");

			
		} catch (Exception e) {
			(new CreateClientLogTask("Support.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	//	inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	public void emailNow(View view) {

		
		try{
			
			AppActions.add("Support - Send Email");

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "support@arcmobileapp.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Feedback");
			startActivity(emailIntent); 
		}catch (Exception e){

			toastShort("Error opening email client, please try again");
			(new CreateClientLogTask("Support.emailNow", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void callNow(View view) {

		try {
			
			AppActions.add("Support - Phone Call");

			Intent intent = new Intent(Intent.ACTION_CALL);

			intent.setData(Uri.parse("tel:7083209272"));
			startActivity(intent);
		} catch (Exception e) {
			
			toastShort("Error opening phone client, please try again");

			(new CreateClientLogTask("Support.callNow", "Exception Caught", "error", e)).execute();

		}
		
	}
}
