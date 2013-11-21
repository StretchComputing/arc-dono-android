package com.donomobile.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.db.controllers.DBController;
import com.donomobile.domain.Cards;
import com.donomobile.domain.CreatePayment;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.DonationTypeObject;
import com.donomobile.utils.Keys;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.PaymentFlags;
import com.donomobile.utils.Security;
import com.donomobile.web.ErrorCodes;
import com.donomobile.web.MakePaymentTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class ConfirmPayment extends BaseActivity {

    private Cards selectedCard;
	private ListView typesListView;

    private TextView myTotalPayment;
    private TextView myPaymentUsed;
	private ProgressDialog loadingDialog;
	private boolean justAddedCard;
    private EditText myPinText;
    private String decryptedCC;
    private AlertDialog pinDialog;
	private String myPIN;
	private ArrayAdapter<DonationTypeObject> adapter;

	private double myPaymentId;
	private TextView paymentLabel;
	private TextView totalLabel;
	
	private Button confirmButton;
	private boolean isConfirmingPayment = false;
	
	private MerchantObject myMerchant;
	private TextView titleTextOne;
	private TextView titleTextTwo;
	private Boolean shouldChargeFee;
	private ArrayList<DonationTypeObject> selectedPaying;
   
	private Boolean isAnonymous;
	private Boolean isMakeDefault;
	
	private CheckBox defaultLocationCheckBox;
	private CheckBox anonymousCheckBox;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			AppActions.add("Confirm Payment - onCreate");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_confirm_payment);
			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			selectedCard =  (Cards) getIntent().getSerializableExtra(Constants.SELECTED_CARD);
			justAddedCard = getIntent().getBooleanExtra(Constants.JUST_ADD_CARD, false);
			
			myTotalPayment = (TextView) findViewById(R.id.my_total_payment);
			myTotalPayment.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			myPaymentUsed = (TextView) findViewById(R.id.my_payment_used);
			myPaymentUsed.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
	
			typesListView = (ListView) findViewById(R.id.listView1);
			
			myPinText = (EditText) findViewById(R.id.editText1);
			myPinText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			myPinText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6) });

			
			//titleText = (TextView) findViewById(R.id.text_enter_pin);
			//titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			paymentLabel = (TextView) findViewById(R.id.merchantNameText);
			paymentLabel.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			totalLabel = (TextView) findViewById(R.id.current_merchant);
			totalLabel.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			confirmButton = (Button) findViewById(R.id.button_email);
			confirmButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			
			titleTextOne = (TextView) findViewById(R.id.titleTextOne);
			titleTextOne.setTypeface(ArcMobileApp.getLatoLightTypeface());

			titleTextTwo = (TextView) findViewById(R.id.titleTextTwo);
			titleTextTwo.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			titleTextTwo.setText("for " + myMerchant.merchantName);
			
			selectedPaying = new ArrayList<DonationTypeObject>();
			
		
			//.setVisibility(View.INVISIBLE);

			if (justAddedCard){
			//	myPinText.setVisibility(View.INVISIBLE);
				
			}else{
				//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			}

			shouldChargeFee = false;
			if (myMerchant.chargeFee){
				
				if (myMerchant.donationAmount < myMerchant.convenienceFeeCap){
					shouldChargeFee = true;
					
					DonationTypeObject tmp = new DonationTypeObject();
					tmp.isProcessingFee = true;
					tmp.amountPaying = myMerchant.convenienceFee;
					tmp.percentPaying = 1.0;
					
					myMerchant.donationTypes.add(tmp);
				}
			}
			
			
			for (int i = 0; i  < myMerchant.donationTypes.size(); i++){
				
				DonationTypeObject donation = myMerchant.donationTypes.get(i);
				
				if (donation.percentPaying > 0){
					selectedPaying.add(donation);
				}

			}
			setLabels();
			
			
			populateListView();
			registerClickCallback();
			
			
			anonymousCheckBox = (CheckBox) findViewById(R.id.anonymousCheck);
			anonymousCheckBox.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			defaultLocationCheckBox = (CheckBox) findViewById(R.id.defaultCheck);
			defaultLocationCheckBox.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			
			
            ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());

			if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
				defaultLocationCheckBox.setChecked(false);
			}else{
				defaultLocationCheckBox.setChecked(true);

			}
			
			
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);

			if(customerToken == null || customerToken.length() == 0){
				//guest, hide the anonymouscheckbox
				anonymousCheckBox.setVisibility(View.INVISIBLE);
				
			}else{
				anonymousCheckBox.setVisibility(View.VISIBLE);

			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.onCreate", "Exception Caught", "error", e)).execute();

		}


	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog = new ProgressDialog(ConfirmPayment.this);
		loadingDialog.setTitle("Making Payment");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}
	
	
	private void setLabels(){
		
		try {
			
			double totalAmount = myMerchant.donationAmount;
			if (shouldChargeFee){
				totalAmount += myMerchant.convenienceFee;
			}
			myTotalPayment.setText(String.format("$%.2f", totalAmount));
			myPaymentUsed.setText(selectedCard.getCardId());
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.setLabels", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	public void onClickPin(View view) {

		toastShort("Your card pin is the numeric PIN you entered when saving this card.  If you do not remember your PIN, please add a new form of payment.");
		
	}
	public void onMakePaymentClicked(View view) {
		
		try {
			
			if (!isConfirmingPayment){
				
				
				if (defaultLocationCheckBox.isChecked()){
                    ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
                    myPrefs.putAndCommitString(Keys.DEFAULT_CHURCH_ID, myMerchant.merchantId);
				}
				
				
				
				isConfirmingPayment = true;
				
				String cardNumber = "";
				
				if (justAddedCard){
					AppActions.add("Confirm Payment - Make Payment Clicked - Just Added Card");

					decryptedCC = selectedCard.getNumber();
				}else{
					try{
						AppActions.add("Confirm Payment - Make Payment Clicked - Picked Stored Card");

						decryptedCC = decryptCreditCardNumber(selectedCard.getNumber());

					}catch(Exception e){
						
					}
				}
				
				
				if (decryptedCC.length() > 0){
					
					AppActions.add("Confirm Payment - Make Payment Clicked - Entered Correct PIN");

					makePayment();
				}else{
					
					AppActions.add("Confirm Payment - Make Payment Clicked - Entered Incorrect PIN");
					isConfirmingPayment = false;

					toastShort("Invalid PIN, please try again");
				}
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.onMakePaymentClicked", "Exception Caught", "error", e)).execute();

		}
		
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	private String decryptCreditCardNumber(String encryptedNumber){
		
		
		try{
			Security s = new Security();
	        //String decrypted = s.decrypt(myPinText.getText().toString(), encryptedNumber);
	        String decrypted = s.decryptBlowfish(encryptedNumber, myPinText.getText().toString());
			
	        if (decrypted == null){
	        	return "";
	        }
	        return decrypted;
	        
		}catch (Exception e){
			(new CreateClientLogTask("ConfirmPayment.decryptCreditCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
	}
	
	
	private void makePayment(){
		
		
		try {
			
			AppActions.add("Confirm Payment - Making Payment - My Donation:" + myMerchant.donationAmount);

			
			loadingDialog.show();
			String token = getToken();
			String customerId = getId();

			String account = decryptedCC.replace(" ", "");
			String month = selectedCard.getExpirationMonth();
			if (month.length() == 1) {
				month = "0" + month;
			}
			String year = selectedCard.getExpirationYear().substring(2, 4);
			String expiration = month + "-" + year;
			String pin = selectedCard.getCVV();
			String type = PaymentFlags.PaymentType.CREDIT.toString();
			
			String cardType = getCardTypeForNumber(account);
			
			
			
			
			String splitType = PaymentFlags.SplitType.DOLLAR.toString();
			

			//.setText("");
			
			double grandTotal = myMerchant.donationAmount;
			
			double gratuity = 0.0;
			if (shouldChargeFee){
				gratuity = myMerchant.convenienceFee;
			}
			CreatePayment newPayment = new CreatePayment(myMerchant.merchantId, customerId, myMerchant.invoiceId, grandTotal, grandTotal, gratuity, account, type, cardType, expiration, pin, null, splitType, null, null, myMerchant.donationTypes);

			MakePaymentTask task = new MakePaymentTask(token, newPayment, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						isConfirmingPayment = false;
						int errorCode = getErrorCode();


						loadingDialog.hide();
						if (getFinalSuccess()) {


							
							toastShort("Your payment has been processed successfully!");

							
							ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
							
							String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);

							if(customerToken == null && customerToken == null){
								//Offer Registration Screen
								
								AppActions.add("Confirm Payment - Payment Successful as Guest");

								loadingDialog.dismiss();
								
								
							}else{
								
								if (justAddedCard){
									//Offer saving the card;
									AppActions.add("Confirm Payment - Payment Successful Customer Just added Card");

									myPaymentId = getPaymentId();
									showPinDialog();
								}else{
									
									AppActions.add("Confirm Payment - Payment Successful Customer Picked Stored Card");
									loadingDialog.dismiss();

									

									
								}
								
								
							
							}
							
							
							
							
							
						} else {
							
							String errorMsg = "";
							
							Boolean editCardOption = false;
							Boolean duplicateTransaction = false;
							Boolean displayAlert = false;
							Boolean networkError = false;

							if (errorCode != 0){
								
								AppActions.add("Confirm Payment - Payment Failed - Error Code:" + errorCode);

								if(errorCode == ErrorCodes.CANNOT_GET_PAYMENT_AUTHORIZATION) {
					                //errorMsg = @"Credit card not approved.";
					                editCardOption = true;
					            } else if(errorCode == ErrorCodes.FAILED_TO_VALIDATE_CARD) {
					                // TODO need explanation from Jim to put proper error msg
					                //errorMsg = @"Failed to validate credit card";
					                editCardOption = true;
					            } else if (errorCode == ErrorCodes.FIELD_FORMAT_ERROR){
					               // errorMsg = @"Invalid Credit Card Field Format";
					                editCardOption = true;
					            }else if(errorCode == ErrorCodes.INVALID_ACCOUNT_NUMBER) {
					                // TODO need explanation from Jim to put proper error msg
					               // errorMsg = @"Invalid credit/debit card number";
					                editCardOption = true;
					            } else if(errorCode == ErrorCodes.MERCHANT_CANNOT_ACCEPT_PAYMENT_TYPE) {
					                // TODO put exact type of credit card not accepted in msg -- Visa, MasterCard, etc.
					                errorMsg = "Merchant does not accept credit/debit card";
					            } else if(errorCode == ErrorCodes.OVER_PAID) {
					                errorMsg = "Over payment. Please check invoice and try again.";
					            } else if(errorCode == ErrorCodes.INVALID_AMOUNT) {
					                errorMsg = "Invalid amount. Please re-enter payment and try again.";
					            } else if(errorCode == ErrorCodes.INVALID_EXPIRATION_DATE) {
					               // errorMsg = @"Invalid expiration date.";
					                editCardOption = true;
					            }  else if (errorCode == ErrorCodes.UNKOWN_ISIS_ERROR){
					               // editCardOption = YES;
					                errorMsg = "Dutch Error, Try Again.";
					            }else if (errorCode == ErrorCodes.PAYMENT_MAYBE_PROCESSED){
					                errorMsg = "This payment may have already processed.  To be sure, please wait 30 seconds and then try again.";
					                displayAlert = true;
					            }else if(errorCode == ErrorCodes.DUPLICATE_TRANSACTION){
					                duplicateTransaction = true;
					            }else if (errorCode == ErrorCodes.CHECK_IS_LOCKED){
					                errorMsg = "This check is currently locked.  Please try again in a few minutes.";
					                displayAlert = true;
					            }else if (errorCode == ErrorCodes.CARD_ALREADY_PROCESSED){
					                errorMsg = "This card has already been used for payment on this invoice.  A card may only be used once per invoice.  Please try again with a different card.";
					                displayAlert = true;
					            }else if (errorCode == ErrorCodes.NO_AUTHORIZATION_PROVIDED){
					                errorMsg = "Invalid Authorization, please try again.";
					                displayAlert = true;
					            }else if (errorCode == ErrorCodes.NETWORK_ERROR){
					                networkError = true;
					                errorMsg = "Dutch is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
					                
					            }else if (errorCode == ErrorCodes.NETWORK_ERROR_CONFIRM_PAYMENT){
					                networkError = true;
					                errorMsg = "Dutch experienced a problem with your internet connection while trying to confirm your payment.  Please check with your server to see if your payment was accepted.";
					                
					            }
					            else {
					                errorMsg = "Payment Failed, please try again.";
					            }
								
								
								if (displayAlert) {
						            
						            toastShort("Payment Warning: " + errorMsg);
						            
						        }else{
						            
						            if (errorMsg.length() > 0) {
						                if (networkError) {
								            toastShort("Internet Error: " + errorMsg);

						                }else{
								            toastShort("Payment Failed: " + errorMsg);

						                }
						            }            
						        }
								
								

							}else{
								AppActions.add("Confirm Payment - Payment Failed - Error Code:" + errorCode);

								toastShort("Payment Failed, please try again.");

							}

						}
					} catch (Exception e) {
						(new CreateClientLogTask("ConfirmPayment.makePayment.onPostExecute", "Exception Caught", "error", e)).execute();

					}
				}
			};
			
			task.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.makePayment", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
	

	
	private void showPinDialog() {
		try {
			
			AppActions.add("Confirm Payment - Show PIN Dialog");

			pinDialog = null;
			
			LayoutInflater factory = LayoutInflater.from(this);
			final View makePaymentView = factory.inflate(R.layout.pin_dialog, null);
			final EditText input = (EditText) makePaymentView.findViewById(R.id.paymentInput);
			
			
			
			TextView paymentTitle = (TextView) makePaymentView.findViewById(R.id.paymentTitle);
			paymentTitle.setText("You must create a PIN so we can securely encrypt your card information.");
			input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
			TextView remainingBalance = (TextView) makePaymentView.findViewById(R.id.paymentRemaining);
			remainingBalance.setText("Save your payment info?");
			//remainingBalance.setVisibility(View.GONE);
			
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			
			//Set colors
			if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){

				paymentTitle.setTextColor(getResources().getColor(R.color.white));
				remainingBalance.setTextColor(getResources().getColor(R.color.white));

			}
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPayment.this);
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setView(makePaymentView);
			//builder.setIcon(R.drawable.logo);
			builder.setCancelable(false);
			builder.setPositiveButton("Save Card", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {

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
									ConfirmPayment.this.refreshList();
								}
							} catch (Exception e) {
								(new CreateClientLogTask("ConfirmPayment.showPinDialog.onClickPositive", "Exception Caught", "error", e)).execute();

							}
						
							
						}
					});
					
					
					Button c = pinDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
					c.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							
							try {
								pinDialog.dismiss();
								
								//TODO - GO SOMEWHERE
							} catch (Exception e) {
								(new CreateClientLogTask("ConfirmPayment.showPinDialog.onClickNegative", "Exception Caught", "error", e)).execute();

							}
							
						
							
						}
					});
					
					
					
					
					
				}
			});
			pinDialog.show();
		} catch (NotFoundException e) {
			(new CreateClientLogTask("ConfirmPayment.showPinDialog", "Exception Caught", "error", e)).execute();

		}
		
	}
	
	
    public void refreshList(){
		
		try {
			//encrypt it
			selectedCard.setNumber(encryptCardNumber(selectedCard.getNumber()));
			//save it
			saveCard();
			
			loadingDialog.dismiss();
			pinDialog.dismiss();
			//refresh list
			
            //TODO - GO SOMEWHERE
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.refreshList", "Exception Caught", "error", e)).execute();

		}
		
	}
	
    
    public String encryptCardNumber(String cardNumber){	
    	
		try{
			Security s = new Security();
			
	        //String encrypted = s.encrypt(myPIN, cardNumber);
	        String encrypted = s.encryptBlowfish(cardNumber, myPIN);

	        
	        return encrypted;
		}catch (Exception e){
			
			(new CreateClientLogTask("ConfirmPayment.encryptCardNumber", "Exception Caught", "error", e)).execute();

			return "";
		}
                
	}
    
     protected void saveCard() {
		
		try {
			
			
			AppActions.add("Confirm Payment - New Card Added");

			DBController.saveCreditCard(getContentProvider(), selectedCard);
		} catch (Exception e) {
			(new CreateClientLogTask("ConfirmPayment.saveCard", "Exception Caught", "error", e)).execute();

		}
	}
     
     
     public String getCardTypeForNumber(String cardNumber){
    	    
    	    try {
    	        
    	        if (cardNumber.length() > 0) {
    	            
    	        	
    	            String firstOne = cardNumber.substring(0,1);
    	            String firstTwo = cardNumber.substring(0,2);
    	            String firstThree =  cardNumber.substring(0,3);
    	            String firstFour = cardNumber.substring(0,4);

    	            int numberLength = cardNumber.length();

    	            
    	            if (firstOne.equals("4") && ((numberLength == 15) || (numberLength == 16))) {
    	                return "V";
    	            }
    	            
    	            
    	            double cardDigits = Double.parseDouble(firstTwo);
    	            
    	            if ((cardDigits >= 51) && (cardDigits <= 55) && (numberLength == 16)) {
    	                return "M";
    	            }
    	            
    	            if ((firstTwo.equals("34") || firstTwo.equals("37")) && (numberLength == 15)) {
    	                return "A";
    	            }
    	            
    	            if ((firstTwo.equals("65") || firstFour.equals("6011")) && (numberLength == 16)) {
    	                return "D";
    	            }
    	            
    	            double threeDigits = Double.parseDouble(firstThree);  
    	            
    	            if ((numberLength == 14) && (firstTwo.equals("36") || firstTwo.equals("38") || ((threeDigits >= 300) && (threeDigits <= 305) ))) {
    	                return "N";
    	            }
    	            
    	            return "UNKOWN";
    	        }else{
    	            return "";
    	        }
    	    }
    	    catch (Exception e) {
    	        return "UNKNOWN";
    	    }
    	 
    	  
    	}
     
     
     
     private void populateListView() {
 		adapter = new MyListAdapter();
 		ListView list = (ListView) findViewById(R.id.listView1);
 		list.setAdapter(adapter);
 	}
     
     private class MyListAdapter extends ArrayAdapter<DonationTypeObject> {
 		public MyListAdapter() {
 			super(ConfirmPayment.this, R.layout.donation_selector_row, selectedPaying);
 		}

 		@Override
 		public View getView(int position, View convertView, ViewGroup parent) {
 			// Make sure we have a view to work with (may have been given null)
 			try {
 				
 				
 				View itemView = convertView;
 				if (itemView == null) {
 					itemView = getLayoutInflater().inflate(R.layout.confirm_payment_row, parent, false);
 				}
 				
 				// Find the car to work with.
 				DonationTypeObject currentItem = selectedPaying.get(position);
 				
 				
 				TextView name = (TextView) itemView.findViewById(R.id.textView1);
 				name.setTypeface(ArcMobileApp.getLatoLightTypeface());
 				
 				if (currentItem.isProcessingFee){
 					name.setText("Processing Fee (?)");
 				}else{
 	 				name.setText(currentItem.description);

 				}
 				
 				TextView amount = (TextView) itemView.findViewById(R.id.textView2);
 				amount.setTypeface(ArcMobileApp.getLatoLightTypeface());
 				amount.setText(String.format("%.2f", currentItem.amountPaying));
 			
 		
 				
 				return itemView;
 				
 				
 			} catch (Exception e) {
 			
 				(new CreateClientLogTask("ConfirmPayment.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
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

 						DonationTypeObject type = selectedPaying.get(position);

 						
 						if (type.isProcessingFee){
 							toastShort("Due to the cost of processing credit card transactions, a $" + String.format("%.2f", myMerchant.convenienceFee) + " fee will be charged on all donations less than $" +
 						    String.format("%.2f", myMerchant.convenienceFeeCap));
 						}else{
 						}
 						
 					

 						
 					
 					} catch (Exception e) {

 						(new CreateClientLogTask("ConfirmPayment.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

 					}
 					
 				
 				}
 			});
 			
 		
 		} catch (Exception e) {

 			(new CreateClientLogTask("ConfirmPayment.registerClickCallback", "Exception Caught", "error", e)).execute();

 		}
 	}

    
}
