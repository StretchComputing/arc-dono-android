package com.donomobile.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class ChurchDonationTypeMultiple extends BaseActivity {

	private MerchantObject myMerchant;

	private HorizontalScrollView myScrollView;
	private LinearLayout myLinearLayout;
	private int currentIndex = 0;
	private int screenwidth = 0;
	private int totalSelected = 0;
	private ArrayList<View> views;
	private ArrayList<Integer> viewIndexes;

	private double donationAmount;
    private boolean justAddedCard;
	private ArrayList<Cards> cards;
    private Cards selectedCard;
    ImageButton rightImageButton;
    ImageButton leftImageButton;
    private TextView textView1;
    private TextView muchDonateTop;

    private Boolean isGoingConfirm = false;
    
    
    public double myQuickOne;
    public double myQuickTwo;
    public double myQuickThree;
    public double myQuickFour;

    
	Handler handler = new Handler();

	Runnable runnableTwo = new Runnable() {
        public void run() {
        	timerDone();
        }
	};

	public void timerDone(){

		myScrollView.scrollTo(0, 0);
		
		RelativeLayout myLayout = (RelativeLayout) views.get(0);
		EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
		nameText.requestFocus();

	}

	@Override
	protected void onResume() {
		super.onResume();
		isGoingConfirm = false;
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_church_donation_type_multiple);
		
		myScrollView = (HorizontalScrollView) findViewById(R.id.my_scroller);
		myLinearLayout = (LinearLayout) findViewById (R.id.scroll_layout);
		myScrollView.setHorizontalScrollBarEnabled(false);
		myScrollView.setOnTouchListener( new OnTouchListener(){ 
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        return true; 
		    }
		});
		
		textView1 = (TextView)findViewById(R.id.titleTextOne);
		textView1.setTypeface(ArcMobileApp.getLatoBoldTypeface());

		muchDonateTop = (TextView)findViewById(R.id.muchDonateTop);
		muchDonateTop.setTypeface(ArcMobileApp.getLatoLightTypeface());

		views = new ArrayList<View>();
		viewIndexes = new ArrayList<Integer>();
		
		rightImageButton = (ImageButton) findViewById(R.id.imageButton1);
		leftImageButton = (ImageButton) findViewById(R.id.imageButton2);

		myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);

		
		 if (myMerchant.quickDonateFour <= 100) {
	            myQuickOne = 5.0;
	            myQuickTwo = 10.0;
	            myQuickThree = 15.0;
	            myQuickFour = 25.0;
	            
	        }else if (myMerchant.quickDonateFour <= 200){
	        	myQuickOne = 10.0;
	        	myQuickTwo = 25.0;
	        	myQuickThree = 50.0;
	        	myQuickFour = 75.0;
	        }else{
	        	myQuickOne = 25.0;
	        	myQuickTwo = 50.0;
	        	myQuickThree = 75.0;
	        	myQuickFour = 100.0;
	        }
		 
		 
		textView1.setText(myMerchant.merchantName);
		
		createDonationLayout();
		
        handler.postDelayed(runnableTwo, 500);

        
        leftImageButton.setVisibility(View.INVISIBLE);

        
        
       
        
        
        
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	public void createDonationLayout(){
		
		
		for (int i = 0; i < myMerchant.donationTypes.size(); i++){
			
			DonationTypeObject donation = myMerchant.donationTypes.get(i);
			if (donation.isSelected){
				totalSelected++;
				 View donationItem = createDonation(i);
				 views.add(donationItem);
				 viewIndexes.add(i);
				 myLinearLayout.addView(donationItem);
			}else{
				
			}
	
		   

		}


	}
	
	public RelativeLayout createDonation(int index) {
		
		try {
		
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			RelativeLayout rLayout = (RelativeLayout) inflater.inflate(R.layout.donation_item, null);
			 

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenwidth = size.x;
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenwidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rLayout.setLayoutParams(params);			
			
			DonationTypeObject donationType = myMerchant.donationTypes.get(index);
			 
		
			TextView nameText = (TextView) rLayout.findViewById(R.id.topLineText);
			nameText.setText(donationType.description);
			
			EditText input = (EditText) rLayout.findViewById(R.id.editText1);
			input.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			input.setFilters(new InputFilter[] { new CurrencyFilter() });
			
			TextView dollarSign = (TextView) rLayout.findViewById(R.id.titleTextTwo);
			dollarSign.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			Button quickOne = (Button) rLayout.findViewById(R.id.quickOne);
			quickOne.setText(String.format("$%.0f", myQuickOne));
			quickOne.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			            //Do stuff here
			    	ChurchDonationTypeMultiple.this.onQuickOne();
			    }
			});
			
			Button quickTwo = (Button) rLayout.findViewById(R.id.quickTwo);
			quickTwo.setText(String.format("$%.0f", myQuickTwo));
			quickTwo.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			            //Do stuff here
			    	ChurchDonationTypeMultiple.this.onQuickTwo();
			    }
			});
			
			Button quickThree = (Button) rLayout.findViewById(R.id.quickThree);
			quickThree.setText(String.format("$%.0f", myQuickThree));
			quickThree.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			            //Do stuff here
			    	ChurchDonationTypeMultiple.this.onQuickThree();
			    }
			});
			
					
			Button quickFour = (Button) rLayout.findViewById(R.id.quickFour);
			quickFour.setText(String.format("$%.0f", myQuickFour));
			quickFour.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			            //Do stuff here
			    	ChurchDonationTypeMultiple.this.onQuickFour();
			    }
			});

			return rLayout;
		} catch (Exception e) {
			(new CreateClientLogTask("ChurchDonationTypeMultiple.createCarouselItem", "Exception Caught", "error", e)).execute();
			return null;

		}
	}

	
	public void onRightClicked(View v) {
		
		
		if (currentIndex < totalSelected -1){
			currentIndex++;
			myScrollView.smoothScrollTo(screenwidth * currentIndex, 0);
			
			RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
			EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
			nameText.requestFocus();
			leftImageButton.setVisibility(View.VISIBLE);

		}
		
		if (currentIndex == (totalSelected - 1)){
	        rightImageButton.setVisibility(View.INVISIBLE);
		}else{
	        rightImageButton.setVisibility(View.VISIBLE);

		}
		
		

	}
	
	public void onLeftClicked(View v) {


		if (currentIndex > 0){
			currentIndex--;
			myScrollView.smoothScrollTo(screenwidth * currentIndex, 0);
			RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
			EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
			nameText.requestFocus();
	        rightImageButton.setVisibility(View.VISIBLE);

		}
		

		if (currentIndex == 0){
	        leftImageButton.setVisibility(View.INVISIBLE);
		}else{
			leftImageButton.setVisibility(View.VISIBLE);

		}
	}
	
	
	public void onQuickOne() {

		RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
		EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
		nameText.setText(String.format("%.2f", myQuickOne));
		onRightClicked(null);
	}
	
	public void onQuickTwo() {

		RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
		EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
		nameText.setText(String.format("%.2f", myQuickTwo));
		onRightClicked(null);
	}
	
	public void onQuickThree() {
		RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
		EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
		nameText.setText(String.format("%.2f", myQuickThree));
		onRightClicked(null);

	}
	
	public void onQuickFour() {

		RelativeLayout myLayout = (RelativeLayout) views.get(currentIndex);
		EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
		nameText.setText(String.format("%.2f", myQuickFour));
		onRightClicked(null);
	}

	public void onContinueButtonClicked(View v) {
		
		donationAmount = 0.0;
		
		for (int i = 0; i < views.size(); i++){
			
			RelativeLayout myLayout = (RelativeLayout) views.get(i);
			EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
			
			if (nameText != null && nameText.getText() != null && nameText.getText().toString() != null && nameText.getText().toString().length() > 0){
				donationAmount += Double.parseDouble(nameText.getText().toString());

			}
		}
		
		for (int i = 0; i < viewIndexes.size(); i++){
			
			Integer myInt = viewIndexes.get(i);
			DonationTypeObject donation = myMerchant.donationTypes.get(myInt);
			
			double thisDonationAmount = 0.0;
			
			RelativeLayout myLayout = (RelativeLayout) views.get(i);
			EditText nameText = (EditText) myLayout.findViewById(R.id.editText1);
			
			if (nameText != null && nameText.getText() != null && nameText.getText().toString() != null && nameText.getText().toString().length() > 0){
				thisDonationAmount = Double.parseDouble(nameText.getText().toString());

			}
			
			Logger.d("This One: " + thisDonationAmount);
			Logger.d("Total: " + donationAmount);
			Logger.d("Percent Paying: " + thisDonationAmount/donationAmount);
			donation.percentPaying = thisDonationAmount/donationAmount;
			donation.amountPaying = thisDonationAmount;
		}
	
		
		goToPayment();
	
	}
	
	
	private void goToPayment(){
		
	    cards = DBController.getCards(getContentProvider());

		if (cards.size() > 0){

			
			showAlertDialog();
			
			
		}else{
			showCardIo();

		}
}


public void showCardIo(){
	
	try {
		

		Intent scanIntent = new Intent(this, CardIOActivity.class);
		// required for authentication with card.io
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, Constants.MY_CARDIO_APP_TOKEN);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); 
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); 
		startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
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
				resultDisplayStr = "\nScan was canceled.\n";
				showInfoDialog(resultDisplayStr);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(ChurchDonationTypeMultiple.this);
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

		  
		  for (int i = 0; i < cards.size(); i++){
			  Cards currentCard = cards.get(i);
			  
			  listItems.add("+ New Card");
			  
			  if (currentCard.getCardName() != null && currentCard.getCardName().length() > 0){
		
					 listItems.add(currentCard.getCardName() +  " (" + currentCard.getCardLabel() + ") " +currentCard.getCardId());

				}else{
					  listItems.add(currentCard.getCardLabel() + currentCard.getCardId());

				}
		  }

		  final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Select Payment:");
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            // Do something with the selection
		        	
		        	if (item == 0){
		        		ChurchDonationTypeMultiple.this.showCardIo();
		        	}else{
		        		ChurchDonationTypeMultiple.this.selectedCard = ChurchDonationTypeMultiple.this.cards.get(item-1);
		        		ChurchDonationTypeMultiple.this.goConfirmPayment();
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
