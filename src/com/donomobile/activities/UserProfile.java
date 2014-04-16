package com.donomobile.activities;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.domain.Cards;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.GetTokenTask;
import com.donomobile.web.UpdateCustomerTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class UserProfile extends BaseActivity {

	private RelativeLayout loggedInView;
	private TextView emailTextView;
	private Button editServerButton;
	private Button signOutButton;
	private Button paymentHistoryButton;

	private boolean isLeaving = false;
	private boolean isSignout = false;
	private boolean isPaymentFlow = false;
	private MerchantObject myMerchant;
    private Cards selectedCard;
	private boolean justAddedCard;
	
	private EditText firstNameText;
	private EditText lastNameText;

	private TextView explainText;
	
	private String initialFirstName;
	private String initialLastName;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			AppActions.add("UserProfile - OnCreate");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_user_profile);
			setActionBarTitle("Profile");

			loggedInView = (RelativeLayout) findViewById(R.id.logged_in_view);
			emailTextView = (TextView) findViewById(R.id.email_text);
			emailTextView.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			explainText = (TextView) findViewById(R.id.textView1);
			explainText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			

			firstNameText = (EditText) findViewById(R.id.editText1);
			firstNameText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			firstNameText.setHint("First Name");
			
			lastNameText = (EditText) findViewById(R.id.EditText01);
			lastNameText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			lastNameText.setHint("Last Name");

			
			editServerButton = (Button) findViewById(R.id.edit_server_button);
			signOutButton = (Button) findViewById(R.id.resendButton);
			paymentHistoryButton = (Button)findViewById(R.id.quickFour);
			
			editServerButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			signOutButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
		//	createButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			
			isPaymentFlow = getIntent().getBooleanExtra(Constants.IS_PAYMENT_FLOW, false);

			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);
			justAddedCard = getIntent().getBooleanExtra(Constants.JUST_ADD_CARD, false);
			
			
			//set name
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

			String customerFirstName = myPrefs.getString(Keys.CUSTOMER_FIRST_NAME);
			String customerLastName = myPrefs.getString(Keys.CUSTOMER_LAST_NAME);
			
			initialFirstName = "";
			initialLastName = "";
			if (customerFirstName != null && customerFirstName.length() > 0){
				firstNameText.setText(customerFirstName);
				initialFirstName = customerFirstName;
			}
			
			if (customerLastName != null && customerLastName.length() > 0){
				lastNameText.setText(customerLastName);
				initialLastName = customerLastName;
			}
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onCreate", "Exception Caught", "error", e)).execute();

		}

		
		

	}

	@Override
	public void onResume(){
		try {
			super.onResume();
			
			isLeaving = false;
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			String customerEmail = myPrefs.getString(Keys.CUSTOMER_EMAIL);



			//Show "logged in" or "logged out" view
			if (customerToken != null && customerToken.length() > 0){
				
				AppActions.add("UserProfile - OnResume - As Customer");

				loggedInView.setVisibility(View.VISIBLE);
				
				emailTextView.setText(customerEmail);
				
				
			}else{
				
				AppActions.add("UserProfile - OnResume - As Guest");

				loggedInView.setVisibility(View.INVISIBLE);
			}
			
			//Show/hide edit server button
			if (customerToken != null && customerToken.length() > 0 && myPrefs.getBoolean(Keys.IS_ADMIN)){
				editServerButton.setVisibility(View.VISIBLE);
			}else{
				editServerButton.setVisibility(View.INVISIBLE);
			}
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onResume", "Exception Caught", "error", e)).execute();

		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	
	public void onLogInClicked(View view) {

		try {
			
			AppActions.add("UserProfile - Login Clicked");

			
			
			if (!isLeaving){
				isLeaving = true;
				Intent social = (new Intent(getApplicationContext(), UserLogin.class));
				social.putExtra(Keys.IS_PAYMENT_FLOW, isPaymentFlow);
				social.putExtra(Constants.SELECTED_CARD, selectedCard);
				social.putExtra(Constants.VENUE, myMerchant);				
				social.putExtra(Constants.JUST_ADD_CARD, justAddedCard);
		 		
				startActivity(social);
			}
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onLoginClicked", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	public void onCreateNewClicked(View view) {

		try {
			
			AppActions.add("UserProfile - Create Clicked");

		
			
			if (!isLeaving){
				isLeaving = true;
				Intent social = (new Intent(getApplicationContext(), UserCreateNew.class));
				social.putExtra(Keys.IS_PAYMENT_FLOW, isPaymentFlow);
				social.putExtra(Constants.SELECTED_CARD, selectedCard);
				social.putExtra(Constants.VENUE, myMerchant);				
		 		social.putExtra(Constants.JUST_ADD_CARD, justAddedCard);
		 		
				startActivity(social);
			}
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onCreateNewClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void onLogoutClicked(View view) {

		try {
			
			if (!isSignout){
				isSignout = true;
				AppActions.add("UserProfile - Logout Clicked");

				ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

				myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, "");
				myPrefs.putAndCommitString(Keys.CUSTOMER_ID, "");
				myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, "");
				myPrefs.putAndCommitString(Keys.DUTCH_URL, "");

				myPrefs.putAndCommitBoolean(Keys.IS_ADMIN, false);

				//getGuestToken();
				
				//Intent social = (new Intent(getApplicationContext(), UserCreate.class));
				
				toastShort("You have successfully logged out.");
				Intent single = new Intent(getApplicationContext(), InitActivity.class);
				single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(single);

			}
		
			
			//Intent social = (new Intent(getApplicationContext(), UserCreate.class));
			//startActivity(social);
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onLogoutClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void onViewPaymentHistoryClicked(View view) {

		try {
	
			Intent history = (new Intent(getApplicationContext(), PaymentHistory.class));
			startActivity(history);
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.onViewPaymentHistoryClicked", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void onEditServerClicked(View view) {
		
		AppActions.add("UserProfile - Edit Server Clicked");

		if (!isLeaving){
			isLeaving = true;
			Intent social = (new Intent(getApplicationContext(), EditServer.class));
			startActivity(social);
		}
		
	}

	
private void getGuestToken(){
		
		try {
			String uuid = UUID.randomUUID().toString();
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			myPrefs.putAndCommitString(Keys.MY_UUID, uuid);
			
			GetTokenTask getTokenTask = new GetTokenTask(uuid, uuid, true, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						isSignout = false;
						int errorCode = getErrorCode();

						
						
						if(getSuccess()) {
							
							AppActions.add("User Profile - Get Token Succeeded");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

							if(getDevToken()!=null) {

								myPrefs.putAndCommitString(Keys.GUEST_TOKEN, getDevToken());
								myPrefs.putAndCommitString(Keys.GUEST_ID, getDevCustomerId());
							}		
							
							Intent goHome = new Intent(getApplicationContext(), Home.class);
							goHome.putExtra(Constants.LOGGED_OUT, true);
							goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(goHome);
						
						}else{
							
							AppActions.add("User Profile - Get Token Failed - Error Code:" + errorCode);

							if (errorCode != 0){
								toastShort("Unable to complete logout, please try again.");

							}
						}
					} catch (Exception e) {
						(new CreateClientLogTask("InitActivity.getGuestToken.onPostExecute", "Exception Caught", "error", e)).execute();

					}
				}
			};
			getTokenTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("UserProfile.getGuestToken", "Exception Caught", "error", e)).execute();

		}
		
		
	}


	@Override
	public void onStop(){
		
		ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

		String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
		
		if (customerToken != null & customerToken.length() > 0){
			
			if (!firstNameText.getText().toString().equals(initialFirstName) || !lastNameText.getText().toString().equals(initialLastName)){
				updateName();
			}
		}
		
		
		super.onStop();
	}


	private void updateName(){
	
	try {

		UpdateCustomerTask createUserTask = new UpdateCustomerTask(firstNameText.getText().toString(), lastNameText.getText().toString(), getApplicationContext()) {
			@Override
			protected void onPostExecute(Void result) {
				try {
					super.onPostExecute(result);
					
					int errorCode = getErrorCode();
					
					if (getFinalSuccess()){
						
						ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

						Logger.d("UPDATE NAME SUCCEEDED!");
						myPrefs.putAndCommitString(Keys.CUSTOMER_FIRST_NAME, UserProfile.this.firstNameText.getText().toString());
						myPrefs.putAndCommitString(Keys.CUSTOMER_LAST_NAME, UserProfile.this.lastNameText.getText().toString());
						
						
					}else{
				

					}
				} catch (Exception e) {
					(new CreateClientLogTask("UserProfile.updateName.onPostExecute", "Exception Caught", "error", e)).execute();
				}
				
				
			}
		};
		createUserTask.execute();
	} catch (Exception e) {
		(new CreateClientLogTask("UserProfile.updateName", "Exception Caught", "error", e)).execute();
	}
	
	
	}



}
