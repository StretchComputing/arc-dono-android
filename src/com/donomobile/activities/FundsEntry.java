package com.donomobile.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
import com.donomobile.db.controllers.DBController;
import com.donomobile.domain.Cards;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.utils.Security;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;
public class FundsEntry extends BaseActivity {

	private TextView homeTitle;
	private Boolean isAddingCard = false;
	private String myPIN;
	private AlertDialog pinDialog;
	private Cards enteredCard;
	private Cards cardToName;
	private MerchantObject myMerchant;

	private EditText cardNumberText;
	private EditText expirationText;
	private EditText ccvText;
	private Boolean shouldRun = true;
	private Boolean isPaymentFlow = false;
    private RecurringDonationObject myRecurringObject = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_funds_entry);
			
			
			homeTitle = (TextView) findViewById(R.id.here_title);
			homeTitle.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			cardNumberText = (EditText)findViewById(R.id.editText1);
			cardNumberText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			expirationText = (EditText)findViewById(R.id.editText2);
			expirationText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			expirationText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });

			ccvText = (EditText)findViewById(R.id.editText3);
			ccvText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			ccvText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });


			setActionBarTitle("Payment");

			setFieldFormatters();
			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			myRecurringObject =  (RecurringDonationObject) getIntent().getSerializableExtra(Constants.RECURRING_OBJECT);

			if (myMerchant != null){
				isPaymentFlow = true;
			}
		}catch(Exception e){
			(new CreateClientLogTask("FundsEntry.onCreate", "Exception Caught", "error", e)).execute();

		}
	

	}

	private void setFieldFormatters(){
		
		
		
		
		
		cardNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	
            	Boolean didMakeChange = false;
      

            	
             	try{

                 	 if (count > 0 && shouldRun) {
                          
                  		
                          String cardNumber = cardNumberText.getText().toString();
                          String initCardNumber = cardNumberText.getText().toString();
                          Boolean isAmex = false;

                          if (cardNumber.length() > 1) {
                              String firstTwo = cardNumber.substring(0,2);

                              if (firstTwo.equals("34") || firstTwo.equals("37")) {

                                  isAmex = true;
                              }
                          }
                          
                          if (isAmex) {
                              
                              

                                  if (cardNumber.length() == 4) {
                               
                                 	 cardNumber = (cardNumber + " ");

                                      
                                  }else if (cardNumber.length() == 11){
                                 	 cardNumber = (cardNumber + " ");
                                  }else if (cardNumber.length() == 17){
                                      expirationText.requestFocus();
                                  }else if (cardNumber.length() == 5) {
                                 	 
                                 	 cardNumber = (cardNumber.substring(0, 4) + " " + cardNumber.substring(4));

                                  }else if (cardNumber.length() == 12){
                                 	 
                                 	 cardNumber = (cardNumber.substring(0, 11) + " " + cardNumber.substring(11));

                                      
                                  }
                              
                              
                              
                              
                          }else{
                                                            	
                              	
                                  if (cardNumber.length() == 4) {
                                 	 cardNumber = (cardNumber + " ");
                                  }else if (cardNumber.length() == 9){
                                 	 cardNumber = (cardNumber + " ");
                                  }else if (cardNumber.length() == 14){
                                 	 cardNumber = (cardNumber + " ");
                                  }else if (cardNumber.length() == 19){
                                      expirationText.requestFocus();
                                  }else if (cardNumber.length() == 5) {
                                 	 cardNumber = (cardNumber.substring(0, 4) + " " + cardNumber.substring(4));
                                  }else if (cardNumber.length() == 10){
                                 	 cardNumber = (cardNumber.substring(0, 9) + " " + cardNumber.substring(9));
                                      
                                  }else if (cardNumber.length() == 15){
                                 	 cardNumber = (cardNumber.substring(0, 14) + " " + cardNumber.substring(14));
                                  }
                              
                              
                          }
                          
                        
                          
                          if (!initCardNumber.equals(cardNumber)){

                        	  shouldRun = false;
                        	  cardNumberText.setText(cardNumber);
                              cardNumberText.setSelection(cardNumberText.getText().length());
                          }
                         

                      }else{
                    	  
                    	  if (!shouldRun){
                        	  shouldRun = true;
                          }
                      }
                      
                      
                 	 
                 	 
             	}catch(Exception e){
        			(new CreateClientLogTask("FundsEntry.CardNumberText.onTextChanged", "Exception Caught", "error", e)).execute();
             	}
           	 
           	 
           	 
            	
         
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
		
		
		
		expirationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            	try{
            		if (count == 0 && start == 2){
                		expirationText.setText(expirationText.getText().toString().substring(0, 1));
                		expirationText.setSelection(expirationText.getText().length());
                	}else if (s.toString().length() == 2){
                		expirationText.setText(expirationText.getText().toString() + "/");
                		expirationText.setSelection(expirationText.getText().length());
                	}else if (s.toString().length() == 1){
                	
                		double myDouble = Double.parseDouble(s.toString());
                		
                		if (myDouble > 1){
                			expirationText.setText("0" + s.toString());
                    		expirationText.setSelection(expirationText.getText().length());
                		}
                		
                		
                	}else if (s.toString().length() == 5){
                		ccvText.requestFocus();
                	}
            	}catch(Exception e){
        			(new CreateClientLogTask("FundsEntry.ExpirationText.onTextChanged", "Exception Caught", "error", e)).execute();

            	}
            

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
		
		
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
		isAddingCard = false;
	}

	
	public void scanCard(View v) {

		if (!isAddingCard){
			isAddingCard = true;
			AppActions.add("FundsEntry - Add Card Clicked");

			//hideSuccessMessage();
			Intent scanIntent = new Intent(this, CardIOActivity.class);
			// required for authentication with card.io
			scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
			//scanIntent.putExtra(CardIOActivity.EXTRA_NO_CAMERA, true);

			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
			scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
			startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
		}
		
		
	}
	
	public void addCard(View v) {

		try{
			
			String cardNumber = cardNumberText.getText().toString().replace(" ", "");
			
			Logger.d("CardNumber: " + cardNumber);
			
			if (cardNumber.length() == 0){
				toastShort("Please enter a card number.");
			}else if (expirationText.getText().toString().length() == 0 || ccvText.getText().toString().length() == 0){
				toastShort("Please enter all card information.");
			}else if (!luhnCheck(cardNumber)){
				toastShort("Please enter a valid credit card number.");

			}else{
				
				String number = cardNumber;
				String cvv = ccvText.getText().toString();
				
				String month = expirationText.getText().toString().substring(0,2);
				String year = expirationText.getText().toString().substring(3);

				Logger.d("Month: " + month);
				Logger.d("Year: " + year);
				
				String typeLabel = getCardTypeFromNumber(number);
				
				
				enteredCard = new Cards(cardNumberText.getText().toString(), month, year, "", cvv, "****" + number.substring(number.length() - 4), typeLabel, null);
				
				if (isPaymentFlow){
					refreshList();
				}else{
					showPinDialog();

				}
			}
		}catch(Exception e){
			toastShort("Please enter all card information.");

		}
		
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			
			AppActions.add("Funds - CardIO Scan Complete");

			
			super.onActivityResult(requestCode, resultCode, data);

			String resultDisplayStr = "no response";

			if (requestCode == Constants.SCAN_REQUEST_CODE) {
				if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
					
					if(!scanResult.isExpiryValid()) {
						AppActions.add("Funds - CardIO Scan Expiry Not Valid");

						resultDisplayStr = "Your credit card is not valid (expired)";
						showInfoDialog(resultDisplayStr);
						return;
					}
					
					if(scanResult.getCardType() == CardType.INSUFFICIENT_DIGITS || scanResult.getCardType() == CardType.UNKNOWN || scanResult.getCardType() == CardType.JCB) {
						
						AppActions.add("Funds - CardIO Scan Credit Card Not Valid");

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
					
					String year = String.valueOf(scanResult.expiryYear);
					if (year.length() == 4){
						year = year.substring(2, 4);
					}

					saveTemp(scanResult.getFormattedCardNumber(), String.valueOf(scanResult.expiryMonth), year, scanResult.zip, scanResult.cvv, String.valueOf(scanResult.getCardType().ordinal()), scanResult.getCardType().name());
					if (isPaymentFlow){
						refreshList();
					}else{
						showPinDialog();

					}
					
				} else {
					//resultDisplayStr = "\nScan was canceled.\n";
					//showInfoDialog(resultDisplayStr);
					return;
				}
			}

			//showSuccessMessage(resultDisplayStr);
			//toastLong(resultDisplayStr);
//		showInfoDialog(resultDisplayStr);
			// else handle other activity results
		} catch (Exception e) {
			(new CreateClientLogTask("FundsEntry.onActivityResults", "Exception Caught", "error", e)).execute();

		}
	}
	
	protected void saveTemp(String number, String month, String year, String zip, String cvv, String typeId, String typeLabel) {
		
		try {
			enteredCard = new Cards(number, month, year, zip, cvv, "****" + number.substring(number.length() - 4), typeLabel, null);
		} catch (Exception e) {
			(new CreateClientLogTask("FundsEntry.saveTemp", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	protected void saveCard() {
				
		try {
			
			AppActions.add("Funds - New Card Added");

			DBController.saveCreditCard(getContentProvider(), enteredCard);
		} catch (Exception e) {
			(new CreateClientLogTask("FundsEntry.saveCard", "Exception Caught", "error", e)).execute();

		}
	}
	
	public String encryptCardNumber(String cardNumber){	
		
		try{
			Security s = new Security();
			
	        //String encrypted = s.encrypt(myPIN, cardNumber);
	        String encrypted = s.encryptBlowfish(cardNumber, myPIN);	        
	        return encrypted;
		}catch (Exception e){
			
			(new CreateClientLogTask("FundsEntry.encryptCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
                
	}
	
	
	private void showPinDialog() {
		try {
			
			AppActions.add("Funds -Show PIN Dialog");

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
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(FundsEntry.this);
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
									FundsEntry.this.refreshList();
								}
							} catch (Exception e) {
								(new CreateClientLogTask("FundsEntry.showPinDialog.onClick", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("FundsEntry.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
	
	private void showInfoDialog(String display) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(FundsEntry.this);
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
			(new CreateClientLogTask("FundsEntry.showInfoDialog", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void refreshList(){
		
		try {
			
			if (myRecurringObject != null){
			
			
				Intent single = new Intent(getApplicationContext(), RecurringDonationFinal.class);
				single.putExtra(Constants.VENUE, myMerchant);	
				single.putExtra(Constants.RECURRING_OBJECT, myRecurringObject);	
				single.putExtra(Constants.SELECTED_CARD, enteredCard);
				single.putExtra(Constants.JUST_ADD_CARD, true);
				startActivity(single);
				
				
			}else if (isPaymentFlow){
							
				
				Intent confirmPayment = new Intent(getApplicationContext(), ConfirmPayment.class);
				confirmPayment.putExtra(Constants.SELECTED_CARD, enteredCard);
				confirmPayment.putExtra(Constants.VENUE, myMerchant);				
				confirmPayment.putExtra(Constants.JUST_ADD_CARD, true);
				startActivity(confirmPayment);

			}else{
				
				//encrypt it
				enteredCard.setNumber(encryptCardNumber(enteredCard.getNumber()));
				
				
				//save it
				saveCard();
				
				//Now go back 
				
				finish();
			}
			
			
		} catch (Exception e) {
			(new CreateClientLogTask("FundsEntry.refreshList", "Exception Caught", "error", e)).execute();

		}
	}
	
	public String getCardTypeFromNumber(String cardNumber){
		
		
		try{
			String firstOne = cardNumber.substring(0,1);
			String firstTwo = cardNumber.substring(0,2);
			String firstFour = cardNumber.substring(0,4);

		
			
			double firstTwoDouble = Double.parseDouble(firstTwo);
			double firstThreeDouble = Double.parseDouble(cardNumber.substring(0,3));

			if (firstTwo.equalsIgnoreCase("34") || firstTwo.equalsIgnoreCase("37")){
				return "AMEX";
			}else if (firstOne.equalsIgnoreCase("4")){
				return "VISA";
			}else if (firstTwoDouble >= 51 && firstTwoDouble <= 55){
				return "MASTERCARD";
			}else if (firstTwo.equalsIgnoreCase("65") || firstFour.equalsIgnoreCase("6011")){
				return "DISCOVER";
			}else if ((cardNumber.length() == 14) && (firstTwo.equalsIgnoreCase("36") || firstTwo.equalsIgnoreCase("38") || (firstThreeDouble >= 300 && firstThreeDouble <= 305))){
				return "DINERS";
			}
			
		}catch(Exception e){
			(new CreateClientLogTask("FundsEntry.getCardTypeFromNumber", "Exception Caught", "error", e)).execute();
			return "UNKNOWN";
		}
                
        
		return "";
	}

	

	
	private Boolean luhnCheck(String ccNumber){
			    
		try{
			Boolean isOdd = true;
			int oddSum = 0;
			int evenSum = 0;
		    
			for (int i = ccNumber.length() - 1; i >= 0; i--) {
		        
				
				int digit = Integer.parseInt("" + ccNumber.charAt(i));
		        
				if (isOdd)
					oddSum += digit;
				else
					evenSum += digit/5 + (2*digit) % 10;
		        
				isOdd = !isOdd;
			}
			
		    
			return ((oddSum + evenSum) % 10 == 0);
		}catch(Exception e){
			(new CreateClientLogTask("FundsEntry.luhnCheck", "Exception Caught", "error", e)).execute();

			return false;
		}
		
		
	}

}






