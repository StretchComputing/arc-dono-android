package com.donomobile.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donomobile.ArcMobileApp;
import com.donomobile.domain.Cards;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Keys;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.Security;
import com.donomobile.web.CreateUserTask;
import com.donomobile.web.ErrorCodes;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class UserCreateNew extends Activity {

	private EditText emailTextView;
	private EditText passwordTextView;
	private EditText nameTextView;

	private ProgressDialog loadingDialog;
	private AlertDialog successDialog;
	private AlertDialog pinDialog;
	private Cards enteredCard;
	private String myPIN;
	private boolean didCancelScan;
	private MerchantObject myMerchant;
    private Cards selectedCard;
	private boolean justAddedCard;
	
	private TextView titleText;
	private Button registerButton;
	private Boolean isPaymentFlow;
	
	private String firstName;
	private String lastName;
	private boolean isCreating = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("User Create New - OnCreate");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_user_create_new);
			

			emailTextView = (EditText) findViewById(R.id.new_email_text);
			passwordTextView = (EditText) findViewById(R.id.new_password_text);
			nameTextView = (EditText) findViewById(R.id.EditText01);

			emailTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			passwordTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());
			nameTextView.setTypeface(ArcMobileApp.getLatoLightTypeface());

			titleText = (TextView) findViewById(R.id.remainingText);
			registerButton = (Button)findViewById(R.id.resendButton);
			
			titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			registerButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			//setActionBarTitle("Register");

			isPaymentFlow = getIntent().getBooleanExtra(Constants.IS_PAYMENT_FLOW, false);
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);
			justAddedCard = getIntent().getBooleanExtra(Constants.JUST_ADD_CARD, false);
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
	///	MenuInflater inflater = getSupportMenuInflater();
	//.inflate(R.menu.action_bar_menu, menu);
	//	return true;
	//}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog = new ProgressDialog(UserCreateNew.this);
		loadingDialog.setTitle("Registering");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}
	public void onRegisterClicked(View view) {

		try {
			
			if (!isCreating){
				isCreating = true;
				if (emailTextView != null && emailTextView.length() > 0 && passwordTextView != null && passwordTextView.length() > 0 && nameTextView != null && nameTextView.length() > 0 ){
					//Send the login
					AppActions.add("User Create New - Register Clicked - Email:" + emailTextView.getText().toString());

					login();
				}else{
					toastShort("Please enter an email address, password, and your name.");
				}
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.onRegisterClicked", "Exception Caught", "error", e)).execute();

		}
	}

	private void login(){
		
		try {
			loadingDialog.show();
			

			firstName = "";
			lastName = "";
			
			String input = nameTextView.getText().toString();
			String[] elements = input.split(" ");
			firstName = elements[0];
			for (int i = 1; i < elements.length; i++){
				lastName = lastName + elements[i] + " ";
			}
			lastName = lastName.trim();
				
			CreateUserTask createUserTask = new CreateUserTask(emailTextView.getText().toString(), passwordTextView.getText().toString(), firstName, lastName, false, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						UserCreateNew.this.loadingDialog.hide();
						
						int errorCode = getErrorCode();
						isCreating = false;
						
						if (getFinalSuccess()){
							
							AppActions.add("Create Customer - Register Successful");

							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
							
							String guestId = myPrefs.getString(Keys.GUEST_ID);

							myPrefs.putAndCommitString(Keys.CUSTOMER_TOKEN, getDevToken());
							myPrefs.putAndCommitString(Keys.CUSTOMER_ID, guestId);
							myPrefs.putAndCommitString(Keys.CUSTOMER_EMAIL, UserCreateNew.this.emailTextView.getText().toString());
							
							myPrefs.putAndCommitString(Keys.CUSTOMER_FIRST_NAME, UserCreateNew.this.firstName);
							myPrefs.putAndCommitString(Keys.CUSTOMER_LAST_NAME, UserCreateNew.this.lastName);

							
							myPrefs.putAndCommitString(Keys.GUEST_TOKEN, "");
							myPrefs.putAndCommitString(Keys.GUEST_ID, "");
							
							
							

							//getCurrentServer();
							
							//showSuccessDialog();
							
							toastShort("Success! You have successfully registered!");
							goHome();

						}else{
							AppActions.add("User Create New - Register Failed - Error Code: " + errorCode);

							if (errorCode != 0){
								
								String errorMsg = "";
								
					            if(errorCode == ErrorCodes.USER_ALREADY_EXISTS) {
					                errorMsg = "This email address is already being used.  Please log in or try to register with a different email.";
					            }else if (errorCode == ErrorCodes.NETWORK_ERROR){
					                
					                errorMsg = "Dono is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
					                
					            }else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
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





	
	
	private void showSuccessDialog() {

		try {
			successDialog = null;

			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.payment_dialog, null);
			EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
			input.setVisibility(View.GONE);
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("You will now be prompted to enter a form of payment.");

			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);

			int currentapiVersion = android.os.Build.VERSION.SDK_INT;

			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));
			}

			remainingBalance.setText("Registration Successful!");
			AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
			builder.setCancelable(false);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			successDialog = builder.create();
			successDialog.setCancelable(false);
			successDialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {

					Button b = successDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							
							//clicked
							
							showCardIo();
							successDialog.dismiss();
						}
					});
				}
			});
			successDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("UserCreateNew.showSuccessDialog", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	public void showCardIo(){
		
		try {
			
			AppActions.add("User Create New - Show CardIO");

			Intent scanIntent = new Intent(this, CardIOActivity.class);
			// required for authentication with card.io
			scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
			startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.showCardIO", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	
	private void showPinDialog() {
		try {
			
			AppActions.add("User Create New - Show PIN Dialog");

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.pin_dialog, null);
			final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
			
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("Your PIN will be used to securely encrypt your card.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);
			
	

			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			//remainingBalance.setVisibility(View.GONE);
			remainingBalance.setText("Please create a PIN");
			
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setCancelable(false);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
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
								if (input.getText().toString().length() < 4){
									toastShort("PIN must be at least 4 digits");
								}else{
									myPIN = input.getText().toString();
									pinDialog.dismiss();
									UserCreateNew.this.refreshList();
								}
							} catch (Exception e) {
								(new CreateClientLogTask("UserCreateNew.showPinDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("UserCreateNew.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			
			AppActions.add("User Create New - CardIO Scan Completed");

			super.onActivityResult(requestCode, resultCode, data);

			String resultDisplayStr = "no response";

			if (requestCode == Constants.SCAN_REQUEST_CODE) {
				if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
					
					if(!scanResult.isExpiryValid()) {
						AppActions.add("User Create New - CardIO Scan Expiry Not Valid");

						resultDisplayStr = "Your credit card is not valid (expired)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
						AppActions.add("User Create New - CardIO Scan Credit Card Not Valid");

						resultDisplayStr = "Your credit card is not valid (type unknown)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					resultDisplayStr = "Card Number (formatted):\n"
							+ scanResult.getFormattedCardNumber() + "\n";

					resultDisplayStr += "Card Type: "
							+ scanResult.getCardType().name + "\n";

					
					
					// Do something with the raw number, e.g.:
					// myService.setCardNumber( scanResult.cardNumber );

					if (scanResult.isExpiryValid()) {
						resultDisplayStr += "Expiration Date: "
								+ scanResult.expiryMonth + "/"
								+ scanResult.expiryYear + "\n";
					}

					if (scanResult.cvv != null) {
						// Never log or display a CVV
						resultDisplayStr += "CVV has " + scanResult.cvv.length()
								+ " digits.\n";
					}

					if (scanResult.zip != null) {
						resultDisplayStr += "Zip: " + scanResult.zip + "\n";
					}
					
					saveTemp(scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, String.valueOf(scanResult.getCardType().ordinal()), scanResult.getCardType().name());
					showPinDialog();
					
				} else {
					
					didCancelScan = true;
					resultDisplayStr = "\nScan canceled.  You may instead enter payment from the 'Funds' section on the Menu, or as you are about to make a payment.\n";
					showInfoDialog(resultDisplayStr);
					
					
					
					
					return;
				}
			}

			//showSuccessMessage(resultDisplayStr);
			//toastLong(resultDisplayStr);
//		showInfoDialog(resultDisplayStr);
			// else handle other activity results
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.onActivityResult", "Exception Caught", "error", e)).execute();

		}
	}
	
	protected void saveTemp(String number, String month, String year, String zip, String cvv, String typeId, String typeLabel) {
		
		try {
			enteredCard = new Cards(number, month, year, zip, cvv, "****" + number.substring(number.length() - 4), typeLabel, null);
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.saveTemp", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	protected void saveCard() {
	
		try {
			
			AppActions.add("User Create New - New Card Added");

			//DBController.saveCreditCard(getContentProvider(), enteredCard);
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.saveCard", "Exception Caught", "error", e)).execute();

		}
		//showInfoDialog(DBController.getCardCount(getContentProvider()) + "Card added");
	}
	
	private void showInfoDialog(String display) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(UserCreateNew.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setMessage(display);
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// hideSuccessMessage();
					
					try {
						if (didCancelScan){
							didCancelScan = false;
							loadingDialog.dismiss();

							
							if (isPaymentFlow){
								Intent goBackProfile = new Intent(getApplicationContext(), ConfirmPayment.class);
								goBackProfile.putExtra(Constants.SELECTED_CARD, selectedCard);
								goBackProfile.putExtra(Constants.VENUE, myMerchant);				
								goBackProfile.putExtra(Constants.JUST_ADD_CARD, justAddedCard);
								goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(goBackProfile);
								
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(UserCreateNew.this.passwordTextView.getWindowToken(), 0);
								imm.hideSoftInputFromWindow(UserCreateNew.this.emailTextView.getWindowToken(), 0);
							}else{
								Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
								goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(goBackProfile);
							}
							
							
						}
					} catch (Exception e) {
						(new CreateClientLogTask("UserCreateNew.showInfoDialog.onClick", "Exception Caught", "error", e)).execute();

					}
					
					
					
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// hideSuccessMessage();
				}
			});
			builder.create().show();
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void refreshList(){
		
		try {
			//encrypt it
			enteredCard.setNumber(encryptCardNumber(enteredCard.getNumber()));
			//save it
			saveCard();
			
			loadingDialog.dismiss();

			
			toastShort("Thank you for registering!");
			
			if (isPaymentFlow){
				Intent goBackProfile = new Intent(getApplicationContext(), ConfirmPayment.class);
				goBackProfile.putExtra(Constants.SELECTED_CARD, selectedCard);
				goBackProfile.putExtra(Constants.VENUE, myMerchant);				
				goBackProfile.putExtra(Constants.JUST_ADD_CARD, justAddedCard);
				goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(goBackProfile);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(UserCreateNew.this.passwordTextView.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(UserCreateNew.this.emailTextView.getWindowToken(), 0);
				
			}else{
				Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
				goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(goBackProfile);
			}
			
			
			//refresh list
		} catch (Exception e) {
			(new CreateClientLogTask("UserCreateNew.refreshList", "Exception Caught", "error", e)).execute();

		}
	}
	
	public String encryptCardNumber(String cardNumber){	
		
		try{
			Security s = new Security();
			
	        //String encrypted = s.encrypt(myPIN, cardNumber);
	        String encrypted = s.encryptBlowfish(cardNumber, myPIN);

	        
	        return encrypted;
		}catch (Exception e){
			
			(new CreateClientLogTask("UserCreateNew.encryptCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
                
	}
	
	
	private void toast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
		Toast.makeText(getApplicationContext(), message, duration).show();

	}

	protected void toastShort(String message) {
		
		try {
			toast(message, Toast.LENGTH_LONG);			
		} catch (Exception e) {
			(new CreateClientLogTask("UserLogin.toastShort", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void goHome(){
		Intent goBackProfile = new Intent(getApplicationContext(), Home.class);
		goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goBackProfile);
	}
	

}
