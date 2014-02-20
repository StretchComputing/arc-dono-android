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
import com.actionbarsherlock.view.MenuItem;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.utils.Constants;
import com.donomobile.utils.DonationTypeObject;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class DefaultLocation extends BaseActivity {

	private MerchantObject myMerchant;
	private TextView merchantNameText;
	private ImageView  merchantImage;
	private TextView titleText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default_location);
	
		titleText = (TextView) findViewById(R.id.textView2);
		titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
		
		
		merchantNameText = (TextView) findViewById(R.id.textView1);
		merchantNameText.setTypeface(ArcMobileApp.getLatoBoldTypeface());

		merchantImage = (ImageView) findViewById(R.id.imageView1);


		myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
		
		
		merchantNameText.setText(myMerchant.merchantName);
		
		 if (myMerchant.merchantId.equalsIgnoreCase("16")){
			 
	            merchantImage.setImageResource(R.drawable.sixteen);
	            
		 } else if (myMerchant.merchantId.equalsIgnoreCase("17")){
			 
	            merchantImage.setImageResource(R.drawable.seventeen);

		 } else if (myMerchant.merchantId.equalsIgnoreCase("15")){
			 
	            merchantImage.setImageResource(R.drawable.fifteen);

		 } else if (myMerchant.merchantId.equalsIgnoreCase("21")){
			 
	            merchantImage.setImageResource(R.drawable.twentyone);

		 } else if (myMerchant.merchantId.equalsIgnoreCase("20")){
			 
	            merchantImage.setImageResource(R.drawable.twenty);

		 }else{
			 
				String url = "http://arc.dagher.mobi/Images/App/Promo/"+myMerchant.merchantId+ ".png";
				ArcMobileApp.imageLoader.DisplayImage(url, merchantImage);
		 }
	            
	    
		 
	

		
		setActionBarTitle("Home");



	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.default_menu, menu);
		return true;
	}

	
	public void onMakeDonation(View view) {


		if (myMerchant.donationTypes.size() > 1){
			
			Intent multiple = new Intent(getApplicationContext(), ChurchDonationSelector.class);
			multiple.putExtra(Constants.VENUE, myMerchant);
			startActivity(multiple);
			
		}else{
			
			Intent single = new Intent(getApplicationContext(), ChurchDonationTypeSingle.class);
			single.putExtra(Constants.VENUE, myMerchant);
			startActivity(single);
			
		}
	}
	
	public void onViewHistory(View view) {

		Intent history = (new Intent(getApplicationContext(), PaymentHistory.class));
		startActivity(history);
	}


	public void onViewLocations(View view) {

		Intent goBackHome = new Intent(getApplicationContext(), Home.class);
        goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackHome);
	
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		
		
		if (itemId == 2131034349) {

			showOkDialog("Default Location", "You have chosen this as your default location.  This page will show when the app loads, or when you click 'Home' in the left menu. \n\n  If you would like to view other locations, select the 'View All Locations' button.\n", null);

			//return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	
}
