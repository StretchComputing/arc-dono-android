package com.donomobile.activities;

import java.util.ArrayList;

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

import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.domain.Cards;
import com.donomobile.utils.Constants;
import com.donomobile.utils.CurrencyFilter;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.utils.Security;
import com.donomobile.web.CreateRecurringDonationTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class RecurringDonationFinal extends BaseActivity {


	private TextView titleText;
	private MerchantObject myMerchant;
    private RecurringDonationObject myRecurringObject;
    
    private TextView stepThreeText;
    private TextView scheduleText;
    private TextView paymentText;
    private String decryptedCC;

    private TextView processingFeeText;
    private AlertDialog pinDialog;

    private EditText dollarAmount;
    
    private TextView scheduleSubText;
    private TextView paymentSubText;
    
    private Cards selectedCard;
    
    private double donationAmount;
    private Boolean justAddedCard;
    private Boolean isConfirmingPayment;
	private ProgressDialog loadingDialog;

    String myPIN = "";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_recurring_donation_final);
			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			myRecurringObject = (RecurringDonationObject) getIntent().getSerializableExtra(Constants.RECURRING_OBJECT);
			selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);
			justAddedCard = getIntent().getBooleanExtra(Constants.JUST_ADD_CARD, false);

			
			titleText = (TextView) findViewById(R.id.textView2);
			titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			stepThreeText = (TextView) findViewById(R.id.textView1);
			stepThreeText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			scheduleText = (TextView) findViewById(R.id.TextView01);
			scheduleText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			paymentText = (TextView) findViewById(R.id.TextView02);
			paymentText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			processingFeeText = (TextView) findViewById(R.id.TextView04);
			processingFeeText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			scheduleSubText = (TextView) findViewById(R.id.textView3);
			scheduleSubText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			scheduleSubText.setText(getScheduleText());
			
			paymentSubText = (TextView) findViewById(R.id.TextView03);
			paymentSubText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			paymentSubText.setText(selectedCard.getCardId());

			
			dollarAmount = (EditText)findViewById(R.id.editText1);
			dollarAmount.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			dollarAmount.setFilters(new InputFilter[] { new CurrencyFilter() });
			
			

			titleText.setText(myMerchant.merchantName);
			
			setActionBarTitle("Recurring Donation");
			
			if (myMerchant.chargeFee){
					processingFeeText.setText("*a $" + String.format("%.2f", myMerchant.convenienceFee) + " processing fee will be charged on all donations less than $" +
 					String.format("%.2f", myMerchant.convenienceFeeCap));
			}else{
				processingFeeText.setVisibility(View.INVISIBLE);
			}

		}catch(Exception e){
			
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//getTokensFromWeb();
		isConfirmingPayment = false;		
		
		
	
		
		
	}
	public String getScheduleText(){
		
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
			
			return "Every " + day;
			
		}else if (myRecurringObject.type.equalsIgnoreCase("monthly")){
			
			String suffix = "";
			
			if (myRecurringObject.value == 1 || myRecurringObject.value == 21){
				suffix = "st";
			}else if (myRecurringObject.value == 2 || myRecurringObject.value == 22){
				suffix = "2nd";
			}else if (myRecurringObject.value == 3 || myRecurringObject.value == 23){
				suffix = "rd";
			}else{
				suffix = "th";
			}
			
			return myRecurringObject.value + suffix + " of every month";
		
		}else{
			
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
			
			String occurrence = "";
			if (myRecurringObject.xOfMonth == 1){
				occurrence = "1st";
			}else if (myRecurringObject.xOfMonth == 2){
				occurrence = "2nd";
			}else if (myRecurringObject.xOfMonth == 3){
				occurrence = "3rd";
			}else if (myRecurringObject.xOfMonth == 4){
				occurrence = "4th";
			}
			
			return occurrence + " " + day + " of every month";
		}

	}
	
	
	public void submitClicked(View view){
		
		if (!isConfirmingPayment){
			isConfirmingPayment = true;
			
			if (dollarAmount != null && dollarAmount.getText() != null && dollarAmount.getText().toString() != null && dollarAmount.getText().toString().length() > 0){
				donationAmount = Double.parseDouble(dollarAmount.getText().toString());
				
				if (donationAmount > 0){

					myRecurringObject.amount = donationAmount;
					myRecurringObject.gratuity = 0.0;
					
					if (myMerchant.chargeFee){
						
						if (donationAmount < myMerchant.convenienceFeeCap){
							myRecurringObject.gratuity = myMerchant.convenienceFee;
						}
					}
					
					if (!justAddedCard){
						//Offer saving the card;
						showPinToPay();
					}else{
						
						decryptedCC = selectedCard.getNumber();
						createRecurringDonation();


						
					}
					
					
				}else{
					toastShort("You must enter an amount greater than 0!");
				}
			}else{
				toastShort("Please enter a donation amount.");

			}
		}
		
		
	}
	
	 
	protected void createRecurringDonation() {
		try {
			
			
			loadingDialog = new ProgressDialog(RecurringDonationFinal.this);
			loadingDialog.setTitle("Processing...");
			loadingDialog.setMessage("Please Wait...");
			loadingDialog.setCancelable(false);
			loadingDialog.show();
			
			String myToken = getToken();

			String myId = getId();

			//Get the CC number to the final web acll
			String account = decryptedCC.replace(" ", "");
			String month = selectedCard.getExpirationMonth();
			if (month.length() == 1) {
				month = "0" + month;
			}
			String year = "";
			if (selectedCard.getExpirationYear().length() == 2){
				 year = selectedCard.getExpirationYear();
			}else{
				 year = selectedCard.getExpirationYear().substring(2, 4);

			}
			String expiration = month + "-" + year;
			String pin = selectedCard.getCVV();
			
			
			CreateRecurringDonationTask getRecurringTask = new CreateRecurringDonationTask(getApplicationContext(), myToken, myId, myMerchant, myRecurringObject, account, expiration, pin) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						loadingDialog.hide();

						if (loadingDialog != null){
							loadingDialog.dismiss();
						}
						
						isConfirmingPayment = false;
						int errorCode = getErrorCode();

				
						
						if (getSuccess()){
						
							
							AppActions.add("RecurringDonationFinal - Create Recurring Succeeded");
							
							toastShort("Your recurring donation was created successfully!");
							
							Intent single = new Intent(getApplicationContext(), DefaultLocation.class);
							single.putExtra(Constants.VENUE, myMerchant);
							single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(single);
							
										        
						}else{
							AppActions.add("RecurringDonationFinal Location - Create Recurring Failed(or none) - Error Code:" + errorCode);

							toastShort("Error creating your recurring donation, please try again.");


						}
					} catch (Exception e) {
						
				

						(new CreateClientLogTask("RecurringDonationFinal.createRecurringDonationsForMerchant.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getRecurringTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("RecurringDonationFinal.createRecurringDonationsForMerchant", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void showPinToPay() {
		try {
			
			AppActions.add("Confirm Payment - Show PIN Dialog");

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.pin_dialog, null);
			final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
			
			
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("Please enter your PIN for this card.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			remainingBalance.setText("Save your payment info?");
			remainingBalance.setVisibility(View.GONE);
			
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(RecurringDonationFinal.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setCancelable(false);
			builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

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
									
									decryptedCC = decryptCreditCardNumber(selectedCard.getNumber());

									if (decryptedCC.length() > 0){
										
										AppActions.add("RecurringDonationFinal Payment - Make Payment Clicked - Entered Correct PIN");
										pinDialog.dismiss();

										createRecurringDonation();
									}else{
										
										AppActions.add("RecurringDonationFinal Payment - Make Payment Clicked - Entered Incorrect PIN");
										isConfirmingPayment = false;

										toastShort("Invalid PIN, please try again");
									}
									
									
								}
							} catch (Exception e) {
								(new CreateClientLogTask("RecurringDonationFinal.showPinDialog.onClickPositive", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
					
					
					Button c = pinDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
					c.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							try {
								pinDialog.dismiss();
								isConfirmingPayment = false;
								
							} catch (Exception e) {
								(new CreateClientLogTask("RecurringDonationFinal.showPinDialog.onClickNegative", "Exception Caught", "error", e)).execute();

							}
							
						
							
						}
					});
					
					
					
					
					
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("RecurringDonationFinal.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	private String decryptCreditCardNumber(String encryptedNumber){
		
		
		try{
			Security s = new Security();
	        //String decrypted = s.decrypt(myPinText.getText().toString(), encryptedNumber);
	        String decrypted = s.decryptBlowfish(encryptedNumber, myPIN);
			
	        if (decrypted == null){
	        	return "";
	        }
	        return decrypted;
	        
		}catch (Exception e){
			(new CreateClientLogTask("RecurringDonationFinal.decryptCreditCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
	}

		
}