package com.donomobile.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.db.controllers.DBController;
import com.donomobile.domain.Cards;
import com.donomobile.utils.Constants;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class RecurringDonationInitial extends BaseActivity {


	private TextView titleText;
	private MerchantObject myMerchant;
	private Spinner weeklySpinner;
	private Spinner xOfMonthSpinner;

	private TextView stepOneText;
	private TextView stepTwoText;
	
	private TextView oneHelpText;
	private TextView twoHelpText;
	private TextView threeHelpText;
	
	private TextView subHelpText;

	private Button weeklyButton;
	private Button monthlyButton;
	private Button xOfMonthButton;

	
	private ImageView weeklyCheckImage;
	private ImageView monthlyCheckImage;
	private ImageView xOfMonthCheckImage;
	
	private ArrayList<Cards> cards;
    private Cards selectedCard;
    
    private RecurringDonationObject myRecurringObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_recurring_donation_initial);
			
			weeklyCheckImage = (ImageView)findViewById(R.id.imageView1);
			monthlyCheckImage = (ImageView)findViewById(R.id.ImageView01);
			xOfMonthCheckImage = (ImageView)findViewById(R.id.ImageView02);

			titleText = (TextView) findViewById(R.id.textView2);
			titleText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			subHelpText = (TextView) findViewById(R.id.sub_text);
			subHelpText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			
			stepOneText = (TextView) findViewById(R.id.textView1);
			stepOneText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			stepTwoText = (TextView) findViewById(R.id.TextView03);
			stepTwoText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			oneHelpText = (TextView) findViewById(R.id.textView3);
			oneHelpText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			twoHelpText = (TextView) findViewById(R.id.TextView01);
			twoHelpText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			threeHelpText = (TextView) findViewById(R.id.TextView02);
			threeHelpText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			weeklyButton = (Button) findViewById(R.id.button1);
			weeklyButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			monthlyButton = (Button) findViewById(R.id.Button01);
			monthlyButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			xOfMonthButton = (Button) findViewById(R.id.Button02);
			xOfMonthButton.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			myMerchant =  (MerchantObject) getIntent().getSerializableExtra(Constants.VENUE);
			titleText.setText(myMerchant.merchantName);
			
			setActionBarTitle("Recurring Donation");
			
			
			xOfMonthSpinner = (Spinner) findViewById(R.id.planets_spinner);

			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.first_four_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			xOfMonthSpinner.setAdapter(adapter);
			
			weeklySpinner = (Spinner) findViewById(R.id.main_spinner);

			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
			        R.array.weekdays_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			weeklySpinner.setAdapter(adapter1);
			
			
			xOfMonthSpinner.setVisibility(View.INVISIBLE);
			
			subHelpText.setText("(ex: Every Monday)");
			
			weeklyCheckImage.setVisibility(View.VISIBLE);
			monthlyCheckImage.setVisibility(View.INVISIBLE);
			xOfMonthCheckImage.setVisibility(View.INVISIBLE);

		}catch(Exception e){
			
		}
		
	}
	
	
	public void weeklyClicked(View view) {
		
		subHelpText.setText("(ex: Every Monday)");

		
		xOfMonthSpinner.setVisibility(View.INVISIBLE);

		weeklyCheckImage.setVisibility(View.VISIBLE);
		monthlyCheckImage.setVisibility(View.INVISIBLE);
		xOfMonthCheckImage.setVisibility(View.INVISIBLE);
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.weekdays_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		weeklySpinner.setAdapter(adapter1);
		
		
	}
	
	public void monthlyClicked(View view) {
		
		subHelpText.setText("(ex: 1st of every month)");

		
		xOfMonthSpinner.setVisibility(View.INVISIBLE);

		weeklyCheckImage.setVisibility(View.INVISIBLE);
		monthlyCheckImage.setVisibility(View.VISIBLE);
		xOfMonthCheckImage.setVisibility(View.INVISIBLE);
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.first_twenty_eight_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		weeklySpinner.setAdapter(adapter1);
		
	}
	
	public void xOfMonthClicked(View view) {

		subHelpText.setText("(ex: 1st Sunday of every month)");

		
		xOfMonthSpinner.setVisibility(View.VISIBLE);

		
		weeklyCheckImage.setVisibility(View.INVISIBLE);
		monthlyCheckImage.setVisibility(View.INVISIBLE);
		xOfMonthCheckImage.setVisibility(View.VISIBLE);
		
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.weekdays_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		weeklySpinner.setAdapter(adapter1);
		
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
			        		RecurringDonationInitial.this.goToFundsEntry();
			        	}else{
			        		RecurringDonationInitial.this.selectedCard = RecurringDonationInitial.this.cards.get(item-1);
			        		RecurringDonationInitial.this.goRecurringFinal();
			        	}
			        	
			            	
			        }
			    });
			    AlertDialog alert = builder.create();
			    alert.show();
		} catch (Exception e) {
			(new CreateClientLogTask("RecurringDonationInitial.showAlertDialog", "Exception Caught", "error", e)).execute();

		}

	}
	
	
	public void onContinueClicked(View view){
		
		myRecurringObject = new RecurringDonationObject();
		
		if (weeklyCheckImage.getVisibility() == View.VISIBLE){
			//weekly
			myRecurringObject.type = "WEEKLY";
			
			String selected = weeklySpinner.getSelectedItem().toString();
			
			int theValue = 0;
			if (selected.equalsIgnoreCase("monday")){
				theValue = 1;
			}else if (selected.equalsIgnoreCase("tuesday")){
				theValue = 2;
			}else if (selected.equalsIgnoreCase("wednesday")){
				theValue = 3;
			}else if (selected.equalsIgnoreCase("thursday")){
				theValue = 4;
			}else if (selected.equalsIgnoreCase("friday")){
				theValue = 5;
			}else if (selected.equalsIgnoreCase("saturday")){
				theValue = 6;
			}else if (selected.equalsIgnoreCase("sunday")){
				theValue = 7;
			}
			
			myRecurringObject.value = theValue;
			
		}else if (monthlyCheckImage.getVisibility() == View.VISIBLE){
			//monthly
			myRecurringObject.type = "MONTHLY";

			String selected = weeklySpinner.getSelectedItem().toString();
			
			selected = selected.replace("st", "");
			selected = selected.replace("nd", "");
			selected = selected.replace("rd", "");
			selected = selected.replace("th", "");
			
			myRecurringObject.value = Integer.parseInt(selected);

		}else{
			//xofmonth
			myRecurringObject.type = "xOfMonth";
			

			String selected = weeklySpinner.getSelectedItem().toString();
			
			int theValue = 0;
			if (selected.equalsIgnoreCase("monday")){
				theValue = 1;
			}else if (selected.equalsIgnoreCase("tuesday")){
				theValue = 2;
			}else if (selected.equalsIgnoreCase("wednesday")){
				theValue = 3;
			}else if (selected.equalsIgnoreCase("thursday")){
				theValue = 4;
			}else if (selected.equalsIgnoreCase("friday")){
				theValue = 5;
			}else if (selected.equalsIgnoreCase("saturday")){
				theValue = 6;
			}else if (selected.equalsIgnoreCase("sunday")){
				theValue = 7;
			}
			
			myRecurringObject.value = theValue;
			
			String xSelected = xOfMonthSpinner.getSelectedItem().toString();
			
			int xValue = 0;
			if (xSelected.equalsIgnoreCase("1st")){
				xValue = 1;
			}else if (xSelected.equalsIgnoreCase("2nd")){
				xValue = 2;

			}else if (xSelected.equalsIgnoreCase("3rd")){
				xValue = 3;

			}else if (xSelected.equalsIgnoreCase("4th")){
				xValue = 4;

			}

			myRecurringObject.xOfMonth = xValue;

			
		}
		
		 cards = DBController.getCards(getContentProvider());

			if (cards.size() > 0){

				
				showAlertDialog();
				
				
			}else{
				goToFundsEntry();

			}
		
	}
	
	
	
	public void goToFundsEntry(){
		
		Intent single = new Intent(getApplicationContext(), FundsEntry.class);
		single.putExtra(Constants.VENUE, myMerchant);	
		single.putExtra(Constants.RECURRING_OBJECT, myRecurringObject);				
		startActivity(single);
		
	}
	
	public void goRecurringFinal(){
		
		Intent single = new Intent(getApplicationContext(), RecurringDonationFinal.class);
		single.putExtra(Constants.VENUE, myMerchant);	
		single.putExtra(Constants.RECURRING_OBJECT, myRecurringObject);	
		single.putExtra(Constants.SELECTED_CARD, selectedCard);
		startActivity(single);
		
		
	}
	
	
}
		