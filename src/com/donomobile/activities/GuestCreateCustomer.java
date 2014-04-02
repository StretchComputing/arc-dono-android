package com.donomobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.domain.Check;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Keys;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.ErrorCodes;
import com.donomobile.web.UpdateCustomerTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class GuestCreateCustomer extends BaseActivity {

	private Check theBill;
	private TextView emailTextView;
	private TextView passwordTextView;
	private ProgressDialog loadingDialog;
	private Button button2;
	private Button button1;

	private TextView titleText;
	private TextView subText;
	private boolean isCreating = false;
	private AlertDialog pinDialog;

	private MerchantObject myMerchant;
	
	@Override
	public void onBackPressed() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			
			AppActions.add("Guest Create Customer - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_guest_create_customer);
			
			theBill =  (Check) getIntent().getSerializableExtra(Constants.INVOICE);
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);

			emailTextView = (TextView) findViewById (R.id.guest_email_textv);
			emailTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			passwordTextView = (TextView) findViewById (R.id.guest_password_textv);
			passwordTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			
			button1 = (Button) findViewById (R.id.resendButton);
			button1.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			button2 = (Button) findViewById (R.id.quickOne);
			button2.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			titleText = (TextView) findViewById(R.id.home_title);
			titleText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			subText = (TextView) findViewById(R.id.help_item_text);
			subText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			setActionBarTitle("New Account");

			
		}catch(Exception e){
			(new CreateClientLogTask("GuestCreateCustomer.onCreate", "Exception Caught", "error", e)).execute();

		}
	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	public void onCreateClicked(View view) {

		try {
			
			if (!isCreating){
				isCreating = true;
				if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 ){
					//Send the login
					AppActions.add("Guest Create Customer - Create Clicked - Email: " + emailTextView.getText().toString());

					register();
				}else{
					isCreating = false;
					toastShort("Please enter an email address and password.");
				}
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.onCreateClicked", "Exception Caught", "error", e)).execute();
		}
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isCreating = false;
		loadingDialog = new ProgressDialog(GuestCreateCustomer.this);
		loadingDialog.setTitle("Creating Account");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}
	
	
	public void onNoThanksClicked(View view) {

		
		try {
			
			if (!isCreating){
				isCreating = true;
				AppActions.add("Guest Create Customer - No Thanks Clicked");

		 		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
		 		
		 		if(myPrefs.getString(Keys.DID_SHOW_ARE_YOU_SURE_GUEST) != null && myPrefs.getString(Keys.DID_SHOW_ARE_YOU_SURE_GUEST).length() > 0 ){
		 			
		 			if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
						
						//if you have a default church ID, go there
						Intent single = new Intent(getApplicationContext(), DefaultLocation.class);
						single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						single.putExtra(Constants.VENUE, myMerchant);
						single.putExtra(Constants.DID_PAY, true);

						startActivity(single);
			             
			             
					}else{
						 Intent goBackHome = new Intent(getApplicationContext(), Home.class);
			             goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			             startActivity(goBackHome);

					}
		 			
		 			
		 		}else{
		 			isCreating = false;
		 			myPrefs.putAndCommitString(Keys.DID_SHOW_ARE_YOU_SURE_GUEST, "yes");
		 			showPinDialog();
		 		}

				
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.onNoThanksClicked", "Exception Caught", "error", e)).execute();
		}
		
		
	}

	private void register(){
		
		try {
			loadingDialog.show();
			UpdateCustomerTask createUserTask = new UpdateCustomerTask(emailTextView.getText().toString(), passwordTextView.getText().toString(), getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						GuestCreateCustomer.this.loadingDialog.hide();
						
						int errorCode = getErrorCode();
						isCreating = false;
						
						if (getFinalSuccess()){
							
							AppActions.add("Guest Create Customer - Register Successful");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
							
							String guestId = myPrefs.getString(Keys.GUEST_ID);

							myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getNewCustomerToken());
							myPrefs.putAndCommitString(Keys.CUSTOMER_ID, guestId);
							myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, GuestCreateCustomer.this.emailTextView.getText().toString());
							
							
							myPrefs.putAndCommitString(Keys.GUEST_TOKEN, getNewCustomerToken());
							myPrefs.putAndCommitString(Keys.GUEST_ID, guestId);
							
							
							

							toastShort("Account created successfully!");
							
							getCurrentServer();

							loadingDialog.dismiss();


							if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
								
								//if you have a default church ID, go there
								Intent single = new Intent(getApplicationContext(), DefaultLocation.class);
								single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								single.putExtra(Constants.VENUE, myMerchant);
								single.putExtra(Constants.DID_PAY, true);
								startActivity(single);
					             
					             
							}else{
								 Intent goBackHome = new Intent(getApplicationContext(), Home.class);
					             goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					             startActivity(goBackHome);

							}
							
							
						}else{
							AppActions.add("Guest Create Customer - Register Failed - Error Code:" + errorCode);
							isCreating = false;
							if (errorCode != 0){
								
								String errorMsg = "";
								
					            if(errorCode == 103) {
					                errorMsg = "The email address you entered is already being used.  If you already have an account, please sign in by going to Profile in the left menu";
					            }else if (errorCode == ErrorCodes.NETWORK_ERROR){
					                
					                errorMsg = "Arc is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
					                
					            }else {
					                errorMsg = "We encountered an error during the registration process, please try again.";
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("We encountered an error during the registration process, please try again.");

							}


						}
					} catch (Exception e) {
						(new CreateClientLogTask("GuestCreateCustomer.register.onPostExecute", "Exception Caught", "error", e)).execute();
					}
					
					
				}
			};
			createUserTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("GuestCreateCustomer.register", "Exception Caught", "error", e)).execute();
		}
		
		
	}
	
	
	
	
	private void showPinDialog() {
		try {
			

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.pin_dialog, null);
			EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);			
			input.setVisibility(View.GONE);
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("Are you sure you want to remain anonymous?  For tax purposes we recommend  you sign up so you can receive email receipts.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			//remainingBalance.setVisibility(View.GONE);
			remainingBalance.setText("Remain Anonymous?");
			
	
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(GuestCreateCustomer.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setCancelable(false);
			builder.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			
			builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			
			
			/*
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			*/
			pinDialog = builder.create();
			
			pinDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

			pinDialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {

					Button b = pinDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							try {
								
								pinDialog.dismiss();
							} catch (Exception e) {
								(new CreateClientLogTask("GuestCreateCustomer.showPinDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
					
					
					Button n = pinDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
					n.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							try {
								
								pinDialog.dismiss();

								ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
						 		
								if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
									
									//if you have a default church ID, go there
									Intent single = new Intent(getApplicationContext(), DefaultLocation.class);
									single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									single.putExtra(Constants.VENUE, myMerchant);
									single.putExtra(Constants.DID_PAY, true);

									startActivity(single);
						             
						             
								}else{
									 Intent goBackHome = new Intent(getApplicationContext(), Home.class);
						             goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						             startActivity(goBackHome);

								}
						 			
						 			
						 			
							} catch (Exception e) {
								(new CreateClientLogTask("GuestCreateCustomer.showPinDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
					
					
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("GuestCreateCustomer.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
}
