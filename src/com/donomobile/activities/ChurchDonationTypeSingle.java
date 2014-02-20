package com.donomobile.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.db.controllers.DBController;
import com.donomobile.domain.Cards;
import com.donomobile.utils.Constants;
import com.donomobile.utils.CurrencyFilter;
import com.donomobile.utils.DonationTypeObject;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class ChurchDonationTypeSingle extends BaseActivity {

	private TextView locationName;
	private TextView howMuchString;
	private TextView dollarSign;
	private TextView quickDonate;
	private double donationAmount;
    private boolean justAddedCard;
    
    Button quickButtonOne;
    Button quickButtonTwo;
    Button quickButtonThree;
    Button quickButtonFour;


	private EditText dollarAmount;
	
	private MerchantObject myMerchant;
	private DonationTypeObject myDonationType;
	
	private ArrayList<Cards> cards;
    private Cards selectedCard;

    private Boolean isGoingConfirm;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_church_donation_type_single);
			
			locationName = (TextView)findViewById(R.id.location_name_text);
			locationName.setTypeface(ArcMobileApp.getLatoBoldTypeface());

			howMuchString = (TextView)findViewById(R.id.howmuchtop);
			howMuchString.setTypeface(ArcMobileApp.getLatoLightTypeface());



			dollarSign = (TextView)findViewById(R.id.TextView01);
			dollarSign.setTypeface(ArcMobileApp.getLatoLightTypeface());

			quickDonate = (TextView)findViewById(R.id.titleTextTwo);
			quickDonate.setTypeface(ArcMobileApp.getLatoBoldTypeface());


			dollarAmount = (EditText)findViewById(R.id.editText1);
			dollarAmount.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			dollarAmount.setFilters(new InputFilter[] { new CurrencyFilter() });

			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);

			locationName.setText("to " + myMerchant.merchantName);
			
			
			quickButtonOne = (Button) findViewById(R.id.quickOne);
			quickButtonOne.setText(String.format("$%.0f", myMerchant.quickDonateOne));
			
			quickButtonTwo = (Button) findViewById(R.id.quickTwo);
			quickButtonTwo.setText(String.format("$%.0f", myMerchant.quickDonateTwo));

			quickButtonThree = (Button) findViewById(R.id.quickThree);
			quickButtonThree.setText(String.format("$%.0f", myMerchant.quickDonateThree));

			quickButtonFour = (Button) findViewById(R.id.quickFour);
			quickButtonFour.setText(String.format("$%.0f", myMerchant.quickDonateFour));


			if (myMerchant.donationTypes.size() == 1){
				myDonationType = myMerchant.donationTypes.get(0);
			}else{
				
				for (int i = 0; i < myMerchant.donationTypes.size(); i++){
					DonationTypeObject tmp = myMerchant.donationTypes.get(i);
					if (tmp.isSelected){
						myDonationType = tmp;
						break;
					}
				}
			}
			
			setActionBarTitle("Amount");

			
		}catch(Exception e){
			(new CreateClientLogTask("ChurchDonationTypeSingle.onContinueButtonClicked", "Exception Caught", "error", e)).execute();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isGoingConfirm = false;
	}
	
	public void onContinueButtonClicked(View view) {

	
		try{
			if (dollarAmount != null && dollarAmount.getText() != null && dollarAmount.getText().toString() != null && dollarAmount.getText().toString().length() > 0){
				donationAmount = Double.parseDouble(dollarAmount.getText().toString());
				
				if (donationAmount > 0){

					goToPayment();
				}else{
					toastShort("You must enter an amount greater than 0!");
				}
			}else{
				toastShort("Please enter a donation amount.");

			}
		}
		catch (Exception e){
			
			(new CreateClientLogTask("ChurchDonationTypeSingle.onContinueButtonClicked", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	public void onQuickOne(View v) {

		donationAmount = myMerchant.quickDonateOne;
		goToPayment();
	}
	
	public void onQuickTwo(View v) {
		donationAmount = myMerchant.quickDonateTwo;
		goToPayment();
	}
	
	
	public void onQuickThree(View v) {

		donationAmount = myMerchant.quickDonateThree;
		goToPayment();
	}
	
	public void onQuickFour(View v) {
		donationAmount = myMerchant.quickDonateFour;
		goToPayment();
	}
	
	private void goToPayment(){
		
		dollarAmount.setText(String.format("%.2f", donationAmount));
		
		    cards = DBController.getCards(getContentProvider());

			if (cards.size() > 0){

				
				showAlertDialog();
				
				
			}else{
				showCardIo();

			}
	}
	
	
	public void showCardIo(){
		
		try {
			
			/*

			Intent scanIntent = new Intent(this, CardIOActivity.class);
			// required for authentication with card.io
			scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
			startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
			*/
			
			myMerchant.donationAmount = donationAmount;
			myDonationType.percentPaying = 1.0;
			myDonationType.amountPaying = donationAmount;

			Intent single = new Intent(getApplicationContext(), FundsEntry.class);
			single.putExtra(Constants.VENUE, myMerchant);				
			startActivity(single);
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.showCardIO", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			AppActions.add("Additional Tip - CardIO Scan Complete");

			String resultDisplayStr = "no response";

			if (requestCode == Constants.SCAN_REQUEST_CODE) {
				if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
					
					if(!scanResult.isExpiryValid()) {
						AppActions.add("Additional Tip - CardIO Scan Expiry Not Valid");

						resultDisplayStr = "Your credit card is not valid (expired)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
						AppActions.add("Additional Tip - CardIO Scan Credit Card Not Valid");

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
					
					
					Cards newCard = new Cards( scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), String.valueOf(scanResult.expiryYear), scanResult.zip, scanResult.cvv, "****" + scanResult.getFormattedCardNumber().substring(scanResult.getFormattedCardNumber().length() - 4), scanResult.getCardType().name(), null);
					selectedCard = newCard;
					justAddedCard = true;

					goConfirmPayment();
					
				} else {
					//resultDisplayStr = "\nScan was canceled.\n";
					//showInfoDialog(resultDisplayStr);
					return;
				}
			}

			//showSuccessMessage(resultDisplayStr);
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.onActivityResult", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	private void showInfoDialog(String display) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(ChurchDonationTypeSingle.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setMessage(display);
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//hideSuccessMessage();
						}
					});
			builder.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					//hideSuccessMessage();
				}
			});
			builder.create().show();
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void showAlertDialog(){
		
		  
		  try {
			List<String> listItems = new ArrayList<String>();

			  
			  listItems.add("+ New Card");

			  
			  for (int i = 0; i < cards.size(); i++){
				  Cards currentCard = cards.get(i);
				  
				  
				  if (currentCard.getCardName() != null && currentCard.getCardName().length() > 0){
			
						 listItems.add(currentCard.getCardName() +  " (" + currentCard.getCardLabel() + ") " +currentCard.getCardId());

					}else{
						  listItems.add(currentCard.getCardLabel() + "  " + currentCard.getCardId());

					}
			  }

			  final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

			    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setTitle("Select Payment:");
			    builder.setItems(items, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int item) {
			            // Do something with the selection
			        	
			        	if (item == 0){
			        		ChurchDonationTypeSingle.this.showCardIo();
			        	}else{
			        		ChurchDonationTypeSingle.this.selectedCard = ChurchDonationTypeSingle.this.cards.get(item-1);
				        	ChurchDonationTypeSingle.this.goConfirmPayment();
			        	}
			        	
			            	
			        }
			    });
			    AlertDialog alert = builder.create();
			    alert.show();
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.showAlertDialog", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	private void goConfirmPayment(){
		
		try {
			
			if (!isGoingConfirm){
				isGoingConfirm = true;
				myMerchant.donationAmount = donationAmount;
				myDonationType.percentPaying = 1.0;
				myDonationType.amountPaying = donationAmount;
				Intent confirmPayment = new Intent(getApplicationContext(), ConfirmPayment.class);
				confirmPayment.putExtra(Constants.SELECTED_CARD, selectedCard);
				confirmPayment.putExtra(Constants.VENUE, myMerchant);				
				confirmPayment.putExtra(Constants.JUST_ADD_CARD, justAddedCard);


				startActivity(confirmPayment);
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeSingle.goConfirmPayment", "Exception Caught", "error", e)).execute();

		}
	}

}
