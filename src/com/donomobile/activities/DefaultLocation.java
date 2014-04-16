package com.donomobile.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.web.DeleteRecurringDonationTask;
import com.donomobile.web.GetRecurringDonationsTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class DefaultLocation extends BaseActivity {

	private MerchantObject myMerchant;
	private TextView merchantNameText;
	private ImageView  merchantImage;
	private TextView titleText;
	
	private Button button01;
	private Button button02;
	private Button button03;
	private Boolean didJustPay;
	private static long back_pressed;
	
	private TextView recurringSubText;
	private ArrayList<RecurringDonationObject> recurringList;
	private RecurringDonationObject myRecurringObject = null;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_default_location);
		
			titleText = (TextView) findViewById(R.id.textView2);
			titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			button01 = (Button) findViewById(R.id.button1);
			button01.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			button02 = (Button) findViewById(R.id.button2);
			button02.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			button03 = (Button) findViewById(R.id.button3);
			button03.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			recurringSubText = (TextView) findViewById(R.id.textView3);
			recurringSubText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			
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
			
			
			didJustPay = getIntent().getBooleanExtra(Constants.DID_PAY, false);

		}catch(Exception e){
			(new CreateClientLogTask("DefaultLocation.onCreate", "Exception Caught", "error", e)).execute();

		}



	}

	@Override
	protected void onResume() {
		super.onResume();
		//getTokensFromWeb();
		button03.setText("Loading...");
		recurringSubText.setText("");
		
		getRecurringDonationsForMerchant();
		

	}
	@Override
	public void onBackPressed() {
		
		if (!didJustPay){
			finish();
		}else{
			Intent goBackHome = new Intent(getApplicationContext(), Home.class);
            goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goBackHome);
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.default_menu, menu);
		return true;
	}

	
	public void onMakeDonation(View view) {

		try{
		
			if (myMerchant.donationTypes.size() > 1){
				
				Intent multiple = new Intent(getApplicationContext(), ChurchDonationSelector.class);
				multiple.putExtra(Constants.VENUE, myMerchant);
				startActivity(multiple);
				
			}else{
				
				Intent single = new Intent(getApplicationContext(), ChurchDonationTypeSingle.class);
				single.putExtra(Constants.VENUE, myMerchant);
				startActivity(single);
				
			}
		}catch(Exception e){
			(new CreateClientLogTask("DefaultLocation.onMakeDonation", "Exception Caught", "error", e)).execute();

		}

		
	}
	
	public void onViewHistory(View view) {

		
		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

		String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);



		if (customerToken != null && customerToken.length() > 0){
			
			if (button03.getText().toString().equalsIgnoreCase("My Recurring Donation")){
				
				showInfoDialog();
			}else if  (button03.getText().toString().equalsIgnoreCase("Recurring Donations")){
				
				
				Intent single = new Intent(getApplicationContext(), RecurringDonationInitial.class);
				single.putExtra(Constants.VENUE, myMerchant);
				startActivity(single);
			}
			
		}else{
			
			toastShort("You must be logged in to create recurring donations.  Please log in or sign up by selecting 'My Profile' in the left menu.");
			
		}
		
		
		

	}


	public void onViewLocations(View view) {

		try{
			Intent goBackHome = new Intent(getApplicationContext(), Home.class);
	        goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(goBackHome);
		}catch(Exception e){
			(new CreateClientLogTask("DefaultLocation.onViewLocations", "Exception Caught", "error", e)).execute();

		}
		
	
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		try{
			int itemId = item.getItemId();
			
			
			if (itemId == 2131034349) {

				showOkDialog("Default Location", "You have chosen this as your default location.  This page will show when the app loads, or when you click 'Home' in the left menu. \n\n  If you would like to view other locations, select the 'View All Locations' button.\n", null);

				//return true;
			}
			
			
			return super.onOptionsItemSelected(item);
		}catch(Exception e){
			(new CreateClientLogTask("DefaultLocation.onOptionsItemSelected", "Exception Caught", "error", e)).execute();

			return false;
		}
	
	}
	
	protected void getRecurringDonationsForMerchant() {
		try {
			
			String myToken = getToken();

			String myId = getId();

			GetRecurringDonationsTask getRecurringTask = new GetRecurringDonationsTask(getApplicationContext(), myToken, myId) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						recurringList = new ArrayList<RecurringDonationObject>();
						
						recurringList = getDonationList();
						
						
						if (recurringList != null && recurringList.size() > 0){
						
							
							AppActions.add("DefaultLocation - Get Recurring Succeeded");
							
							boolean found = false;
							for (int i = 0; i < recurringList.size(); i++){
								
								RecurringDonationObject myObject = recurringList.get(i);
								
								if (myObject.merchantId == Integer.parseInt(myMerchant.merchantId)){
									myRecurringObject = recurringList.get(i);
									DefaultLocation.this.button03.setText("My Recurring Donation");
									DefaultLocation.this.recurringSubText.setText(DefaultLocation.this.getTextFromRecurring());
									found = true;
									break;

								}
							}
							
							if (!found){
								DefaultLocation.this.button03.setText("Recurring Donations");
								DefaultLocation.this.recurringSubText.setText("Click to schedule weekly or monthly donations");
							}

										        
						}else{
							AppActions.add("Default Location - Get Recurring Failed(or none) - Error Code:" + errorCode);

							DefaultLocation.this.button03.setText("Recurring Donations");
							DefaultLocation.this.recurringSubText.setText("Click to schedule weekly or monthly donations");


						}
					} catch (Exception e) {
						
				
						DefaultLocation.this.button03.setText("Recurring Donations");


						(new CreateClientLogTask("DefaultLocation.getRecurringDonationsForMerchant.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getRecurringTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("DefaultLocation.getRecurringDonationsForMerchant", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public String getTextFromRecurring(){
		
		try{
			
			double total = myRecurringObject.amount + myRecurringObject.gratuity;
			String dollarAmount = String.format("$%.2f", total);

			if (myRecurringObject.type.equalsIgnoreCase("weekly")){
				
				String day = "";
				if (myRecurringObject.value == 1){
					day = "Monday";
				}else if (myRecurringObject.value == 2){
					day = "Tuesday";

				}else if (myRecurringObject.value == 3){
					day = "Wednesday";

				}else if (myRecurringObject.value == 4){
					day = "Thursday";

				}else if (myRecurringObject.value == 5){
					day = "Friday";

				}else if (myRecurringObject.value == 6){
					day = "Saturday";

				}else if (myRecurringObject.value == 7){
					day = "Sunday";

				}
				String returnString = dollarAmount + ", every " + day;
				return returnString;
				
			}else if (myRecurringObject.type.equalsIgnoreCase("monthly")){
				
				String suffix = "th";
				if (myRecurringObject.value == 1 || myRecurringObject.value == 21){
					suffix = "st";
				}else if (myRecurringObject.value == 2 || myRecurringObject.value == 22){
					suffix = "nd";

				}else if (myRecurringObject.value == 3 || myRecurringObject.value == 23){
					suffix = "rd";

				}
				
				String returnString = dollarAmount + ", the " + myRecurringObject.value + suffix + " of every month";
				
				return returnString;
				
			}else if (myRecurringObject.type.equalsIgnoreCase("xofmonth")){
				
				String day = "";
				if (myRecurringObject.value == 1){
					day = "Monday";
				}else if (myRecurringObject.value == 2){
					day = "Tuesday";

				}else if (myRecurringObject.value == 3){
					day = "Wednesday";

				}else if (myRecurringObject.value == 4){
					day = "Thursday";

				}else if (myRecurringObject.value == 5){
					day = "Friday";

				}else if (myRecurringObject.value == 6){
					day = "Saturday";

				}else if (myRecurringObject.value == 7){
					day = "Sunday";

				}
				
				
				String ofMonth = "";
				if (myRecurringObject.xOfMonth == 1){
					ofMonth = "1st";
				}else if (myRecurringObject.xOfMonth == 2){
					ofMonth = "2nd";

				}else if (myRecurringObject.xOfMonth == 3){
					ofMonth = "3rd";

				}else if (myRecurringObject.xOfMonth == 4){
					ofMonth = "4th";

				}
				
				
				String returnString = dollarAmount + ", the " + ofMonth + " " + day + " of every month";

				return returnString;
			}else{
				return "";
			}
			
		}catch(Exception e){
			Logger.d("EXCEPTION + E: " + e);

			return "";
		}

	}
	
	private void showInfoDialog() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(DefaultLocation.this);
			builder.setTitle("Cancel Recurring Donation?");
			builder.setMessage("Would you like to remove your recurring donation?  Your card will no longer be charged, effective immediately");
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("Remove",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//hideSuccessMessage();
							deleteRecurringDonationsForMerchant();
						}
					});
			
			builder.setNegativeButton("No Thanks",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//hideSuccessMessage();
						}
					});
			
			builder.create().show();
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	protected void deleteRecurringDonationsForMerchant() {
		try {
			
			loadingDialog = new ProgressDialog(DefaultLocation.this);
			loadingDialog.setTitle("Removing Donation...");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
			loadingDialog.show();
			
			String myToken = getToken();


			DeleteRecurringDonationTask getRecurringTask = new DeleteRecurringDonationTask(getApplicationContext(), myToken, myRecurringObject.scheduleId) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						
						loadingDialog.hide();

						if (loadingDialog != null){
							loadingDialog.dismiss();
						}

						
						
						int errorCode = getErrorCode();

						if (getSuccess()){
							
							toastShort("Recurring donation successfully removed!");
							DefaultLocation.this.button03.setText("Recurring Donations");
							DefaultLocation.this.recurringSubText.setText("Click to schedule weekly or monthly donations");
							
							
						}else{
							
							toastShort("Error deleting recurring donation.  If the problem persists, please contact customer support.");

						}
						
					} catch (Exception e) {
						
				


						(new CreateClientLogTask("DefaultLocation.deleteRecurringDonationsForMerchant.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getRecurringTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("DefaultLocation.deleteRecurringDonationsForMerchant", "Exception Caught", "error", e)).execute();

		}
	}

}
